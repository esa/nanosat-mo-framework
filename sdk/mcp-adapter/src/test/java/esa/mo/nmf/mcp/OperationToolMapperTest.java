/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
package esa.mo.nmf.mcp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.ServiceInfo;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherServiceInfo;
import org.ccsds.moims.mo.sm.heartbeat.HeartbeatServiceInfo;
import org.ccsds.moims.mo.sm.packagemanagement.PackageManagementServiceInfo;
import org.ccsds.moims.mo.mc.action.ActionServiceInfo;
import org.ccsds.moims.mo.mc.parameter.ParameterServiceInfo;
import org.junit.Assert;
import org.junit.Test;

/**
 * The {@link OperationToolMapper} turns the operations of a service into MCP
 * tools, named by the service and the operation, for the interaction patterns
 * that map to a single call, and reports a failed call as an error.
 */
public class OperationToolMapperTest {

    @Test
    public void everyRequestSubmitSendInvokeOrProgressOperationBecomesATool() {
        Set<String> names = toolNames(new AppsLauncherServiceInfo());

        // runApp and killApp are SUBMIT, listApp is REQUEST, stopApp is PROGRESS
        Assert.assertTrue(names.contains("AppsLauncher_runApp"));
        Assert.assertTrue(names.contains("AppsLauncher_killApp"));
        Assert.assertTrue(names.contains("AppsLauncher_listApp"));
        Assert.assertTrue("A PROGRESS operation is mapped", names.contains("AppsLauncher_stopApp"));

        // install, uninstall and update are INVOKE
        Set<String> pm = toolNames(new PackageManagementServiceInfo());
        Assert.assertTrue("An INVOKE operation is mapped", pm.contains("PackageManagement_install"));
        Assert.assertTrue(pm.contains("PackageManagement_uninstall"));
        Assert.assertTrue(pm.contains("PackageManagement_update"));
    }

    @Test
    public void pubSubOperationsAreSkipped() {
        // monitorExecution and monitorEvents are PUB-SUB, which is not a tool
        Set<String> names = toolNames(new AppsLauncherServiceInfo());
        Assert.assertFalse(names.contains("AppsLauncher_monitorExecution"));
        Assert.assertFalse(names.contains("AppsLauncher_monitorEvents"));
    }

    @Test
    public void toolNamesArePrefixedByTheServiceSoTheyDoNotClash() {
        Set<String> parameter = toolNames(new ParameterServiceInfo());
        Set<String> action = toolNames(new ActionServiceInfo());

        Assert.assertTrue("Both services have a listDefinition operation",
                parameter.contains("Parameter_listDefinition") && action.contains("Action_listDefinition"));
        for (String name : parameter) {
            Assert.assertTrue(name + " must be prefixed by its service", name.startsWith("Parameter_"));
        }
        Assert.assertTrue("The tools of two services never share a name",
                parameter.stream().noneMatch(action::contains));
    }

    @Test
    public void theSchemaRequiresTheNonNullableFields() {
        JsonObject schema = schemaOf(new AppsLauncherServiceInfo(), "AppsLauncher_listApp");
        JsonArray required = schema.getAsJsonArray("required");
        Assert.assertNotNull("listApp has a required field", required);
        Assert.assertTrue("appNames is required", contains(required, "appNames"));
    }

    @Test
    public void aCallThatFailsIsReportedAsAnError() {
        MALConsumer consumer = consumerFailingOnRequest();
        McpTool getTime = tool(new HeartbeatServiceInfo(), consumer, "Heartbeat_getTime");

        McpTool.Result result = getTime.getHandler().apply(new HashMap<>());

        Assert.assertTrue("A failed call is an error", result.isError());
        Assert.assertTrue(result.text(), result.text().contains("Error invoking 'getTime'"));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    private static List<McpTool> tools(ServiceInfo service, MALConsumer consumer) {
        return OperationToolMapper.toolsFor(service, consumer);
    }

    private static Set<String> toolNames(ServiceInfo service) {
        // The consumer is not used to generate the tools, so none is needed
        return tools(service, null).stream().map(McpTool::getName).collect(Collectors.toSet());
    }

    private static McpTool tool(ServiceInfo service, MALConsumer consumer, String name) {
        return tools(service, consumer).stream()
                .filter(t -> t.getName().equals(name)).findFirst()
                .orElseThrow(() -> new AssertionError("No tool named " + name));
    }

    private static JsonObject schemaOf(ServiceInfo service, String toolName) {
        return JsonParser.parseString(tool(service, null, toolName).getInputSchema()).getAsJsonObject();
    }

    private static boolean contains(JsonArray array, String value) {
        for (int i = 0; i < array.size(); i++) {
            if (value.equals(array.get(i).getAsString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * A MAL consumer whose {@code request} fails, and that does nothing for the
     * other calls. It is enough to drive the error path of a tool call.
     */
    private static MALConsumer consumerFailingOnRequest() {
        return (MALConsumer) Proxy.newProxyInstance(
                MALConsumer.class.getClassLoader(),
                new Class<?>[]{MALConsumer.class},
                (proxy, method, args) -> {
                    if ("request".equals(method.getName())) {
                        throw new MALException("the provider is not reachable");
                    }
                    Class<?> returnType = method.getReturnType();
                    if (returnType == boolean.class) {
                        return false;
                    }
                    return returnType.isPrimitive() ? 0 : null;
                });
    }
}
