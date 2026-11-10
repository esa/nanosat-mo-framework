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
package esa.mo.nmf.testbed.e2e.tests;

import esa.mo.nmf.mcp.McpAdapterServer;
import esa.mo.nmf.mcp.McpTool;
import esa.mo.nmf.testbed.e2e.SupervisorHarness;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Drives the MCP adapter against a live Supervisor: it reads the provider from
 * the Directory, turns every service the provider offers into tools, and a
 * read-only tool call returns the provider's answer. The calls are read-only,
 * so the test leaves the shared filesystem as it found it.
 */
public class McpAdapterE2ETest {

    private static final SupervisorHarness harness = new SupervisorHarness();
    private static List<McpTool> tools;

    @BeforeClass
    public static void setUpClass() throws Exception {
        harness.setUp();
        // The harness presents the ground credential, so these calls are accepted
        tools = McpAdapterServer.toolsForProvider(harness.getDirectoryURI(), null);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        harness.tearDown();
    }

    @Test
    public void everyServiceOfTheProviderBecomesTools() {
        Set<String> names = tools.stream().map(McpTool::getName).collect(Collectors.toSet());

        // The Supervisor provides a COM, an MC and an SM stack; a tool of each
        // confirms that the services are discovered from the Directory, not a list
        Assert.assertTrue("A COM service is exposed", names.stream()
                .anyMatch(n -> n.startsWith("Archive_") || n.startsWith("Directory_")));
        Assert.assertTrue("An MC service is exposed", names.contains("Parameter_listDefinition"));
        Assert.assertTrue("An SM service is exposed", names.contains("AppsLauncher_listApp"));
        Assert.assertTrue("The Heartbeat service is exposed", names.contains("Heartbeat_getTime"));

        Assert.assertTrue("Several services are turned into tools: " + names.size(), names.size() >= 10);
    }

    @Test
    public void aReadOnlyToolReturnsTheProvidersAnswer() {
        McpTool getTime = tool("Heartbeat_getTime");
        McpTool.Result result = getTime.getHandler().apply(new HashMap<>());

        Assert.assertFalse("The call to the provider succeeds: " + result.text(), result.isError());
        Assert.assertTrue(result.text(), result.text().contains("timestamp"));
    }

    @Test
    public void listAppReturnsTheInstalledApps() {
        McpTool listApp = tool("AppsLauncher_listApp");
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("appNames", List.of("*"));

        McpTool.Result result = listApp.getHandler().apply(arguments);

        Assert.assertFalse("listApp succeeds: " + result.text(), result.isError());
        Assert.assertTrue(result.text(), result.text().contains("appIds"));
    }

    private static McpTool tool(String name) {
        return tools.stream().filter(t -> t.getName().equals(name)).findFirst()
                .orElseThrow(() -> new AssertionError("No tool named " + name + " was generated"));
    }
}
