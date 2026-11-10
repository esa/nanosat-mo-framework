/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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

import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.accesscontrol.NMFAccessControl;
import esa.mo.nmf.composites.GroundMOAdapterImpl;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapperSupplier;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.Provider;
import org.ccsds.moims.mo.com.structures.ProviderList;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherServiceInfo;

/**
 * A Model Context Protocol server that exposes the AppsLauncher service of an
 * NMF Supervisor as MCP tools. The tools are generated from the service
 * metadata by the {@link OperationToolMapper}, so they automatically reflect
 * the operations, fields and descriptions defined in the MO service XML.
 *
 * <p>
 * It connects as a ground consumer (the same path as the CLI Tool) to the
 * Directory service URI passed as the single argument, then serves over stdio.
 */
public final class McpAdapterServer {

    private static final Logger LOGGER = Logger.getLogger(McpAdapterServer.class.getName());

    private McpAdapterServer() {
    }

    public static void main(String[] args) throws Exception {
        String credentialFile = null;
        List<String> positionals = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if ("--credential-file".equals(args[i]) && i + 1 < args.length) {
                credentialFile = args[++i];
            } else {
                positionals.add(args[i]);
            }
        }

        if (positionals.isEmpty()) {
            System.err.println("Usage: McpAdapterServer <directory-service-uri> [provider-name] "
                    + "[--credential-file <file>]");
            System.exit(1);
        }
        String directoryURI = positionals.get(0);
        String providerName = (positionals.size() > 1) ? positionals.get(1) : null;

        presentGroundCredential(directoryURI, credentialFile);

        MALConsumer consumer = connect(directoryURI, providerName);
        List<McpTool> tools = OperationToolMapper.toolsFor(consumer,
                AppsLauncherServiceInfo.OPERATIONS);
        LOGGER.log(Level.INFO, "Generated {0} MCP tools from AppsLauncher", tools.size());

        serve(tools);
    }

    /**
     * Connects to the Supervisor through the Directory service and returns the
     * MAL consumer backing the AppsLauncher stub.
     */
    /**
     * Presents the credential of ground on the requests that this server sends
     * to the Supervisor, so that its calls are accepted when the Supervisor
     * enforces access control. It must run before the first connection is
     * opened. The credential comes from the file named by {@code --credential-file},
     * or, when none is given, from the file named by the ground credential
     * system property, as in the playground. Without either, no credential is
     * presented, which is enough only for a Supervisor that does not enforce
     * access control.
     *
     * @param directoryURI the URI of the Supervisor's Directory
     * @param credentialFile the credential file, or null
     * @throws IOException if the credential file cannot be read
     */
    private static void presentGroundCredential(String directoryURI, String credentialFile)
            throws IOException {
        NMFAccessControl.install();
        if (credentialFile != null) {
            NMFAccessControl.INSTANCE.presentGroundCredential(
                    NMFAccessControl.readCredential(credentialFile), directoryURI);
        } else {
            NMFAccessControl.presentGroundCredential(directoryURI);
        }
    }

    private static MALConsumer connect(String directoryURI, String providerName) throws Exception {
        HelperMisc.loadPropertiesFile();
        ProviderList providers = NMFConsumer.retrieveProvidersFromDirectory(new URI(directoryURI));
        if (providers.isEmpty()) {
            throw new IllegalStateException("No providers found at " + directoryURI);
        }

        Provider provider = null;
        if (providerName != null) {
            for (Provider p : providers) {
                if (p.getProviderName().getValue().equals(providerName)) {
                    provider = p;
                    break;
                }
            }
            if (provider == null) {
                throw new IllegalStateException("Provider '" + providerName + "' not found");
            }
        } else {
            provider = providers.get(0);
        }

        GroundMOAdapterImpl adapter = new GroundMOAdapterImpl(provider);
        if (adapter.getSMServices().getAppsLauncherService() == null) {
            throw new IllegalStateException("Provider does not expose the AppsLauncher service");
        }
        return adapter.getSMServices().getAppsLauncherService().getAppsLauncherStub().getConsumer();
    }

    /**
     * Binds the generated tools to the MCP SDK and serves over stdio until the
     * process is terminated.
     */
    private static void serve(List<McpTool> tools) throws InterruptedException {
        McpJsonMapper jsonMapper = new JacksonMcpJsonMapperSupplier().get();
        StdioServerTransportProvider transport = new StdioServerTransportProvider(jsonMapper);

        List<McpServerFeatures.SyncToolSpecification> specs = new ArrayList<>();
        for (McpTool tool : tools) {
            specs.add(toSpecification(jsonMapper, tool));
        }

        McpServer.sync(transport)
                .serverInfo("nmf-mcp-adapter", "0.1.0")
                .tools(specs)
                .build();

        // The stdio transport serves on its own threads; block the main thread.
        new CountDownLatch(1).await();
    }

    private static McpServerFeatures.SyncToolSpecification toSpecification(
            McpJsonMapper jsonMapper, McpTool tool) {
        String description = (tool.getDescription() != null) ? tool.getDescription() : "";
        McpSchema.Tool schemaTool = new McpSchema.Tool.Builder()
                .name(tool.getName())
                .description(description)
                .inputSchema(jsonMapper, tool.getInputSchema())
                .build();

        return new McpServerFeatures.SyncToolSpecification.Builder()
                .tool(schemaTool)
                .callHandler((exchange, request) -> {
                    McpTool.Result result = tool.getHandler().apply(request.arguments());
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent(result.text())), result.isError(), null, null);
                })
                .build();
    }
}
