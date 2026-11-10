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

import esa.mo.com.impl.util.HelperCommon;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.accesscontrol.NMFAccessControl;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapperSupplier;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.Provider;
import org.ccsds.moims.mo.com.structures.ServiceCapability;
import org.ccsds.moims.mo.com.structures.ProviderList;
import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.ServiceInfo;
import org.ccsds.moims.mo.mal.structures.ServiceId;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * A Model Context Protocol server that exposes an NMF provider as MCP tools.
 * Every service the provider advertises in the Directory becomes tools, with
 * no list of services to keep: the server reads the provider's entry, opens a
 * consumer for each service it offers, and generates the tools from the service
 * metadata through the {@link OperationToolMapper}, so they reflect the
 * operations, fields and descriptions defined in the MO service XML, and a
 * service added to a provider appears on its own.
 *
 * <p>
 * It connects as a ground consumer (the same path as the CLI Tool) to the
 * Directory service URI passed as the first argument, then serves over stdio.
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

        List<McpTool> tools = toolsForProvider(directoryURI, providerName);
        if (tools.isEmpty()) {
            throw new IllegalStateException("The provider exposes no operation that can be a tool");
        }
        LOGGER.log(Level.INFO, "Generated {0} MCP tools", tools.size());

        serve(tools);
    }

    /**
     * Builds the tools for every service that the provider advertises in the
     * Directory. The provider's entry lists each service it offers, with the
     * service key (area, service and version) and the address it is reached at.
     * For each, a consumer is opened and the service metadata is looked up, so
     * that any service the provider offers becomes tools, with no list of
     * services to keep: a service added to a provider appears on its own.
     *
     * The access control credential, if any, must be presented before this is
     * called, as the first connection is opened here.
     *
     * @param directoryURI the URI of the Supervisor's Directory
     * @param providerName the name of the provider, or null for the first one
     * @return the tools of every service the provider advertises
     * @throws Exception if the provider could not be read
     */
    public static List<McpTool> toolsForProvider(String directoryURI, String providerName)
            throws Exception {
        HelperMisc.loadPropertiesFile();
        // Registers the types of the MAL, COM, MC, SM and Platform areas, so
        // that a service of any of them can be looked up and its values decoded
        NMFConsumer.initHelpers();

        Provider provider = selectProvider(directoryURI, providerName);
        ConnectionConsumer connection = HelperCommon.providerToConnectionConsumer(provider);
        Map<String, SingleConnectionDetails> addresses = connection.getServicesDetails().getServices();

        List<McpTool> tools = new ArrayList<>();
        for (ServiceCapability capability : provider.getServiceCapabilities()) {
            ServiceId key = capability.getServiceId();
            try {
                ServiceInfo service = lookupService(key);
                if (service == null) {
                    LOGGER.log(Level.INFO, "The service with key {0} is not known and is skipped", key);
                    continue;
                }
                SingleConnectionDetails details = addresses.get(service.getName().toString());
                if (details == null) {
                    continue; // The provider advertises no address for the service
                }
                MALConsumer consumer = connection.startService(details, service, null, null);
                List<McpTool> serviceTools = OperationToolMapper.toolsFor(service, consumer);
                tools.addAll(serviceTools);
                LOGGER.log(Level.INFO, "Service {0}: {1} tools",
                        new Object[]{service.getName().getValue(), serviceTools.size()});
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "The service with key " + key + " could not be read and is skipped", ex);
            }
        }
        return tools;
    }

    /**
     * Looks up the metadata of the service with the given key (area, service
     * and version), from the areas that {@link NMFConsumer#initHelpers()}
     * registered, or NULL if that service is not known to this build.
     */
    private static ServiceInfo lookupService(ServiceId key) {
        try {
            MALArea area = MALContextFactory.lookupArea(key.getKeyArea(), key.getKeyAreaVersion());
            return (area == null) ? null : area.getServiceByNumber(key.getKeyService());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Selects the provider to connect to from the Directory: the one with the
     * given name, or the first one when no name is given.
     */
    private static Provider selectProvider(String directoryURI, String providerName) throws Exception {
        ProviderList providers = NMFConsumer.retrieveProvidersFromDirectory(new URI(directoryURI));
        if (providers.isEmpty()) {
            throw new IllegalStateException("No providers found at " + directoryURI);
        }
        if (providerName == null) {
            return providers.get(0);
        }
        for (Provider p : providers) {
            if (p.getProviderName().getValue().equals(providerName)) {
                return p;
            }
        }
        throw new IllegalStateException("Provider '" + providerName + "' not found");
    }

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
