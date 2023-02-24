/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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
package esa.mo.nmf;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.common.impl.provider.DirectoryProviderServiceImpl;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.misc.Const;
import esa.mo.mc.impl.provider.ParameterInstance;
import esa.mo.mp.impl.provider.MPServicesProvider;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.reconfigurable.provider.PersistProviderConfiguration;
import esa.mo.sm.impl.provider.HeartbeatProviderServiceImpl;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import esa.mo.reconfigurable.service.ReconfigurableService;
import esa.mo.reconfigurable.service.ConfigurationChangeListener;
import esa.mo.reconfigurable.provider.ReconfigurableProvider;
import java.util.Properties;

/**
 * The generic NMF Provider. Includes a Heartbeat service and a Directory
 * service. Selects the transport layer based on the values of the
 * transport.properties file. Provides a mechanism to set a listener for change
 * of configuration.
 *
 * @author Cesar Coelho
 */
public abstract class NMFProvider implements ReconfigurableProvider, NMFInterface {

    protected final static String MC_SERVICES_NOT_INITIALIZED = "The M&C services were not initialized!";
    protected final static Long DEFAULT_PROVIDER_CONFIGURATION_OBJID = (long) 1;  // The objId of the configuration to be used by the provider
    protected final COMServicesProvider comServices = new COMServicesProvider();
    protected final HeartbeatProviderServiceImpl heartbeatService = new HeartbeatProviderServiceImpl();
    protected final DirectoryProviderServiceImpl directoryService = new DirectoryProviderServiceImpl();
    protected MCServicesProviderNMF mcServices;
    protected MPServicesProvider mpServices;
    protected PlatformServicesConsumer platformServices;
    protected CloseAppListener closeAppAdapter = null;
    protected ConfigurationChangeListener providerConfigurationAdapter = null;
    protected String providerName;
    protected long startTime;

    protected PersistProviderConfiguration providerConfiguration;
    protected final ArrayList<ReconfigurableService> reconfigurableServices = new ArrayList<>();

    /**
     * Initializes the NMF provider using a monitoring and control adapter that
     * connects to the Monitor and Control services.
     *
     * @param mcAdapter The Monitor and Control Adapter.
     */
    public abstract void init(final MonitorAndControlNMFAdapter mcAdapter);

    @Override
    public COMServicesProvider getCOMServices() throws NMFException {
        if (this.comServices == null) {
            throw new NMFException("The COM services are not available.");
        }

        return comServices;
    }

    @Override
    public MCServicesProviderNMF getMCServices() throws NMFException {
        if (this.mcServices == null) {
            throw new NMFException("The Monitor and Control services are not available.");
        }

        return mcServices;
    }

    @Override
    public MPServicesProvider getMPServices() throws NMFException {
        if (this.mpServices == null) {
            throw new NMFException("The Mission Planning services are not available.");
        }

        return mpServices;
    }

    @Override
    public PlatformServicesConsumer getPlatformServices() throws NMFException {
        if (this.platformServices == null) {
            throw new NMFException("The Platform services are not available.");
        }

        return platformServices;
    }

    @Override
    public void reportActionExecutionProgress(final boolean success, final int errorNumber, final int progressStage,
        final int totalNumberOfProgressStages, final long actionInstId) throws NMFException {
        if (this.getMCServices() == null) {
            throw new NMFException(MC_SERVICES_NOT_INITIALIZED);
        }

        try {
            this.getMCServices().getActionService().reportExecutionProgress(success, new UInteger(errorNumber),
                progressStage, totalNumberOfProgressStages, actionInstId);
        } catch (IOException ex) {
            throw new NMFException("The action execution progress could not be reported!", ex);
        }
    }

    @Override
    public Long publishAlertEvent(final String alertDefinitionName, final AttributeValueList attributeValues)
        throws NMFException {
        if (this.getMCServices() == null) {
            throw new NMFException(MC_SERVICES_NOT_INITIALIZED);
        }

        return this.getMCServices().getAlertService().publishAlertEvent(null, new Identifier(alertDefinitionName),
            attributeValues, null, null);
    }

    @Override
    public Boolean pushParameterValue(final String name, final Serializable content) throws NMFException {
        return this.pushParameterValue(name, content, true);
    }

    @Override
    public Boolean pushParameterValue(final String name, final Serializable content, final boolean storeIt)
        throws NMFException {
        if (this.getMCServices() == null) {
            throw new NMFException(MC_SERVICES_NOT_INITIALIZED);
        }

        Object obj = HelperAttributes.javaType2Attribute(content); // Convert to MAL type if possible

        // If it is not a MAL type, then try to convert it into a Blob container
        if (!(obj instanceof Attribute)) {
            try {
                obj = HelperAttributes.serialObject2blobAttribute((Serializable) obj);
            } catch (IOException ex) {
                Logger.getLogger(NMFProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        ParameterInstance instance = new ParameterInstance(new Identifier(name), (Attribute) obj, null, null);
        ArrayList<ParameterInstance> parameters = new ArrayList<>(1); // We just add 1 element
        parameters.add(instance);

        return this.getMCServices().getParameterService().pushMultipleParameterValues(parameters, storeIt);
    }

    public Boolean pushMultipleParameterValues(final ArrayList<ParameterInstance> parameters, final boolean storeIt)
        throws NMFException {
        if (this.getMCServices() == null) {
            throw new NMFException(MC_SERVICES_NOT_INITIALIZED);
        }

        return this.getMCServices().getParameterService().pushMultipleParameterValues(parameters, storeIt);
    }

    public final void startMCServices(MonitorAndControlNMFAdapter mcAdapter) throws MALException {
        if (mcAdapter != null) {
            mcServices = new MCServicesProviderNMF();
            mcServices.init(comServices, mcAdapter);
            this.reconfigurableServices.add(mcServices.getActionService());
            this.reconfigurableServices.add(mcServices.getParameterService());
            this.reconfigurableServices.add(mcServices.getAggregationService());
            this.reconfigurableServices.add(mcServices.getAlertService());
        }
    }

    public final void startMPServices(MissionPlanningNMFAdapter mpAdapter) throws MALException {
        if (mpAdapter != null) {
            this.mpServices = new MPServicesProvider();
            this.mpServices.init(comServices);
        }
    }

    /**
     * Sets the transport dispatcher executor threads configurations.
     */
    public void configureTransportThreads() {
        System.setProperty("org.ccsds.moims.mo.mal.transport.gen.inputprocessors", "5");

        // Clean up idle threads after 2 seconds:  (does not seem to work)
        // System.setProperty("org.ccsds.moims.mo.mal.transport.gen.idleinputprocessors", "2");
        
        // let's have a minimum of 2 threads:  (does not seem to work)
        // System.setProperty("org.ccsds.moims.mo.mal.transport.gen.mininputprocessors", "2");
    }

    @Override
    public void setOnConfigurationChangeListener(ConfigurationChangeListener configurationAdapter) {
        this.providerConfigurationAdapter = configurationAdapter;
    }

    @Override
    public ArrayList<ReconfigurableService> getServices() {
        return reconfigurableServices;
    }

    @Override
    public Boolean reloadConfiguration(ConfigurationObjectDetails configurationObjectDetails) {
        throw new UnsupportedOperationException("The NMF does no support reconfiguration.");
    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        throw new UnsupportedOperationException("The NMF does no support reconfiguration.");
    }

    @Override
    public Identifier getProviderName() {
        return new Identifier(this.providerName);
    }

    @Override
    public void setCloseAppListener(final CloseAppListener closeAppAdapter) {
        this.closeAppAdapter = closeAppAdapter;
    }

    public CloseAppListener getCloseAppListener() {
        return this.closeAppAdapter;
    }

    /**
     * Hints the GC to do Garbage Collection and also hints it to go through the
     * finalization method of the pending finalization objects.
     */
    public void hintGC() {
        System.gc();
        System.runFinalization();
    }

    /**
     * Reads the first line of the file that carries the Central Directory
     * service URI. Returns null if the file was not found.
     *
     * @return The URI of the Central Directory service or null if not found.
     */
    public final URI readCentralDirectoryServiceURI() {
        if (System.getProperty(Const.CENTRAL_DIRECTORY_URI_PROPERTY) != null) {
            return new URI(System.getProperty(Const.CENTRAL_DIRECTORY_URI_PROPERTY));
        } else {
            String path = ".."
                    + File.separator + ".."
                    + File.separator
                    + Const.NANOSAT_MO_SUPERVISOR_NAME
                    + File.separator
                    + Const.FILENAME_CENTRAL_DIRECTORY_SERVICE;
            Logger.getLogger(NMFProvider.class.getName()).log(Level.INFO,
                    "Property {0} not set. Falling back to reading from {1}.", new Object[]{
                        Const.CENTRAL_DIRECTORY_URI_PROPERTY, path});

            File file = new File(path); // Select the file that we want to read from

            try {
                // Get the text out of that file...
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);

                try {
                    String line = br.readLine(); // Reads the first line!
                    br.close();
                    return new URI(line);
                } catch (IOException ex) {
                    Logger.getLogger(NMFProvider.class.getName()).log(Level.SEVERE, "An error happened!", ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(NMFProvider.class.getName()).log(Level.WARNING, "The File {0} could not be found!",
                    file.getPath());
                return null;
            }
            return null;
        }
    }

    public final void writeCentralDirectoryServiceURI(final String centralDirectoryURI, final String secondaryURI) {
        try (BufferedWriter wrt = new BufferedWriter(new FileWriter(Const.FILENAME_CENTRAL_DIRECTORY_SERVICE, false))) { // Reset the file
            if (secondaryURI != null) {
                wrt.write(secondaryURI);
                wrt.write("\n");
            }

            wrt.write(centralDirectoryURI);
        } catch (IOException ex) {
            Logger.getLogger(NMFProvider.class.getName()).log(Level.WARNING,
                "Unable to reset URI information from properties file {0}", ex);
        }
    }

    /**
     * Generates a starting Banner that can be used for NMF Providers.
     *
     * @return The banner.
     */
    protected String generateStartBanner() {
        Properties p = System.getProperties();
        final String SEPARATOR = "------------\n";

        StringBuilder banner = new StringBuilder(256);
        banner.append("\n");
        banner.append(SEPARATOR);
        banner.append("NanoSat MO Framework\n");

        // OS version
        banner.append("OS: ");
        banner.append(p.getProperty("os.name", "?"));
        banner.append(" (version: ");
        banner.append(p.getProperty("os.version", "?"));
        banner.append(")\n");

        // User
        banner.append("Running as User: ");
        banner.append(p.getProperty("user.name", "?"));
        banner.append("\n");

        // Java version
        banner.append("Java: ");
        banner.append(p.getProperty("java.runtime.name", "?"));
        banner.append(" (version: ");
        banner.append(p.getProperty("java.runtime.version", "?"));
        banner.append(")\n");

        banner.append(SEPARATOR);
        return banner.toString();
    }

}
