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

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.mc.impl.util.MCServicesConsumer;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.accesscontrol.NMFAccessControl;
import esa.mo.nmf.composites.GroundMOAdapterImpl;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.sm.impl.util.SMServicesConsumer;
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
import org.ccsds.moims.mo.com.archive.ArchiveServiceInfo;
import org.ccsds.moims.mo.com.archivesync.ArchiveSyncServiceInfo;
import org.ccsds.moims.mo.com.directory.DirectoryServiceInfo;
import org.ccsds.moims.mo.com.structures.Provider;
import org.ccsds.moims.mo.com.structures.ProviderList;
import org.ccsds.moims.mo.mal.ServiceInfo;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mc.action.ActionServiceInfo;
import org.ccsds.moims.mo.mc.aggregation.AggregationServiceInfo;
import org.ccsds.moims.mo.mc.alert.AlertServiceInfo;
import org.ccsds.moims.mo.mc.parameter.ParameterServiceInfo;
import org.ccsds.moims.mo.platform.artificialintelligence.ArtificialIntelligenceServiceInfo;
import org.ccsds.moims.mo.platform.autonomousadcs.AutonomousADCSServiceInfo;
import org.ccsds.moims.mo.platform.camera.CameraServiceInfo;
import org.ccsds.moims.mo.platform.fpga.FPGAServiceInfo;
import org.ccsds.moims.mo.platform.gps.GPSServiceInfo;
import org.ccsds.moims.mo.platform.opticaldatareceiver.OpticalDataReceiverServiceInfo;
import org.ccsds.moims.mo.platform.powercontrol.PowerControlServiceInfo;
import org.ccsds.moims.mo.platform.softwaredefinedradio.SoftwareDefinedRadioServiceInfo;
import org.ccsds.moims.mo.platform.softwareimages.SoftwareImagesServiceInfo;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherServiceInfo;
import org.ccsds.moims.mo.sm.commandexecutor.CommandExecutorServiceInfo;
import org.ccsds.moims.mo.sm.heartbeat.HeartbeatServiceInfo;
import org.ccsds.moims.mo.sm.packagemanagement.PackageManagementServiceInfo;

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

        GroundMOAdapterImpl adapter = connect(directoryURI, providerName);
        List<McpTool> tools = allTools(adapter);
        if (tools.isEmpty()) {
            throw new IllegalStateException("The provider exposes no operation that can be a tool");
        }
        LOGGER.log(Level.INFO, "Generated {0} MCP tools", tools.size());

        serve(tools);
    }

    /**
     * Builds the tools for every service that the provider exposes: each COM,
     * MC, SM and Platform service it offers contributes its operations as
     * tools. A service that the provider does not offer is skipped.
     *
     * @param adapter the adapter connected to the provider
     * @return the tools of every service offered
     */
    private static List<McpTool> allTools(GroundMOAdapterImpl adapter) {
        List<McpTool> tools = new ArrayList<>();
        COMServicesConsumer com = adapter.getCOMServices();
        MCServicesConsumer mc = adapter.getMCServices();
        SMServicesConsumer sm = adapter.getSMServices();
        PlatformServicesConsumer platform = adapter.getPlatformServices();

        if (com != null) {
            addService(tools, new ArchiveServiceInfo(),
                    () -> com.getArchiveService().getArchiveStub().getConsumer());
            addService(tools, new ArchiveSyncServiceInfo(),
                    () -> com.getArchiveSyncService().getArchiveSyncStub().getConsumer());
            addService(tools, new DirectoryServiceInfo(),
                    () -> com.getDirectoryService().getDirectoryStub().getConsumer());
        }
        if (mc != null) {
            addService(tools, new ParameterServiceInfo(),
                    () -> mc.getParameterService().getParameterStub().getConsumer());
            addService(tools, new ActionServiceInfo(),
                    () -> mc.getActionService().getActionStub().getConsumer());
            addService(tools, new AlertServiceInfo(),
                    () -> mc.getAlertService().getAlertStub().getConsumer());
            addService(tools, new AggregationServiceInfo(),
                    () -> mc.getAggregationService().getAggregationStub().getConsumer());
        }
        if (sm != null) {
            addService(tools, new AppsLauncherServiceInfo(),
                    () -> sm.getAppsLauncherService().getAppsLauncherStub().getConsumer());
            addService(tools, new PackageManagementServiceInfo(),
                    () -> sm.getPackageManagementService().getPackageManagementStub().getConsumer());
            addService(tools, new HeartbeatServiceInfo(),
                    () -> sm.getHeartbeatService().getHeartbeatStub().getConsumer());
            addService(tools, new CommandExecutorServiceInfo(),
                    () -> sm.getCommandExecutorService().getCommandExecutorStub().getConsumer());
        }
        if (platform != null) {
            addService(tools, new CameraServiceInfo(),
                    () -> platform.getCameraService().getConsumer());
            addService(tools, new GPSServiceInfo(),
                    () -> platform.getGPSService().getConsumer());
            addService(tools, new AutonomousADCSServiceInfo(),
                    () -> platform.getAutonomousADCSService().getConsumer());
            addService(tools, new OpticalDataReceiverServiceInfo(),
                    () -> platform.getOpticalDataReceiverService().getConsumer());
            addService(tools, new SoftwareDefinedRadioServiceInfo(),
                    () -> platform.getSoftwareDefinedRadioService().getConsumer());
            addService(tools, new PowerControlServiceInfo(),
                    () -> platform.getPowerControlService().getConsumer());
            addService(tools, new ArtificialIntelligenceServiceInfo(),
                    () -> platform.getAIService().getConsumer());
            addService(tools, new FPGAServiceInfo(),
                    () -> platform.getFPGAService().getConsumer());
            addService(tools, new SoftwareImagesServiceInfo(),
                    () -> platform.getSoftwareImagesService().getConsumer());
        }
        return tools;
    }

    /**
     * Adds the tools of one service, when the provider offers it. The consumer
     * is read through a supplier, because the getter of a service that is not
     * offered returns null or throws; such a service is logged and skipped.
     */
    private static void addService(List<McpTool> tools, ServiceInfo service, ConsumerSupplier consumer) {
        String name = service.getName().getValue();
        try {
            List<McpTool> serviceTools = OperationToolMapper.toolsFor(service, consumer.get());
            tools.addAll(serviceTools);
            LOGGER.log(Level.INFO, "Service {0}: {1} tools", new Object[]{name, serviceTools.size()});
        } catch (Exception ex) {
            LOGGER.log(Level.FINE, "Service " + name + " is not available and is skipped", ex);
        }
    }

    /** Reads the consumer of a service, which may be absent. */
    @FunctionalInterface
    private interface ConsumerSupplier {

        MALConsumer get() throws Exception;
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

    private static GroundMOAdapterImpl connect(String directoryURI, String providerName) throws Exception {
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

        return new GroundMOAdapterImpl(provider);
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
