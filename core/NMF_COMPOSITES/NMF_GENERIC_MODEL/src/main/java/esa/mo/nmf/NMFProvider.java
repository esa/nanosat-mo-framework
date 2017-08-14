/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
import esa.mo.mc.impl.provider.ParameterInstance;
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
import java.nio.charset.Charset;
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

/**
 * A Provider of MO services composed by COM, M&C, and Platform services.
 * Selects the transport layer based on the selected values of the properties
 * file and initializes all services automatically. Provides configuration
 * persistence, therefore the last state of the configuration of the MO services
 * will be kept upon restart. Additionally, it implements an abstraction layer
 * over the M&C services to facilitate the monitoring of the application logic
 * using the NanoSat MO Framework.
 *
 * @author Cesar Coelho
 */
public abstract class NMFProvider implements ReconfigurableProvider, NMFInterface {

    public final static String DYNAMIC_CHANGES_PROPERTY = "esa.mo.nanosatmoframework.provider.dynamicchanges";
    private final static String MC_SERVICES_NOT_INITIALIZED = "The M&C services were not initialized!";
    public final static String FILENAME_CENTRAL_DIRECTORY_SERVICE = "centralDirectoryService.uri";
    public final static String NANOSAT_MO_SUPERVISOR_NAME = "NanoSat_MO_Supervisor";
    public final static Long DEFAULT_PROVIDER_CONFIGURATION_OBJID = (long) 1;  // The objId of the configuration to be used by the provider
    protected final COMServicesProvider comServices = new COMServicesProvider();
    protected final HeartbeatProviderServiceImpl heartbeatService = new HeartbeatProviderServiceImpl();
    protected final DirectoryProviderServiceImpl directoryService = new DirectoryProviderServiceImpl();
    public MCServicesProviderNMF mcServices;
    public PlatformServicesConsumer platformServices;
    public CloseAppListener closeAppAdapter = null;
    public ConfigurationChangeListener providerConfigurationAdapter = null;
    public String providerName;
    protected long startTime;

    public PersistProviderConfiguration providerConfiguration;
    public final ArrayList<ReconfigurableService> reconfigurableServices = new ArrayList<ReconfigurableService>();

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
    public PlatformServicesConsumer getPlatformServices() throws NMFException {
        if (this.platformServices == null) {
            throw new NMFException("The Platform services are not available.");
        }

        return platformServices;
    }

    @Override
    public void reportActionExecutionProgress(final boolean success, final int errorNumber,
            final int progressStage, final int totalNumberOfProgressStages, final long actionInstId) throws NMFException {
        if (this.getMCServices() == null) {
            throw new NMFException(MC_SERVICES_NOT_INITIALIZED);
        }

        try {
            this.getMCServices().getActionService().reportExecutionProgress(success,
                    new UInteger(errorNumber), progressStage, totalNumberOfProgressStages, actionInstId);
        } catch (IOException ex) {
            throw new NMFException("The action execution progress could not be reported!", ex);
        }
    }

    @Override
    public Long publishAlertEvent(final String alertDefinitionName, final AttributeValueList attributeValues) throws NMFException {
        if (this.getMCServices() == null) {
            throw new NMFException(MC_SERVICES_NOT_INITIALIZED);
        }

        return this.getMCServices().getAlertService().publishAlertEvent(null,
                new Identifier(alertDefinitionName), attributeValues, null, null);
    }

    @Override
    public Boolean pushParameterValue(final String name, final Serializable content) throws NMFException {
        return this.pushParameterValue(name, content, true);
    }

    @Override
    public Boolean pushParameterValue(final String name, final Serializable content, final boolean storeIt) throws NMFException {
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
        ArrayList<ParameterInstance> parameters = new ArrayList<ParameterInstance>(1); // We just add 1 element
        parameters.add(instance);

        return this.getMCServices().getParameterService().pushMultipleParameterValues(parameters, storeIt);
    }

    public abstract void initPlatformServices(COMServicesProvider comServices);

    public final void startMCServices(MonitorAndControlNMFAdapter mcAdapter) throws MALException {
        if (mcAdapter != null) {
            mcServices = new MCServicesProviderNMF();
            mcServices.init(comServices, mcAdapter);
            this.reconfigurableServices.add(mcServices.getActionService());
            this.reconfigurableServices.add(mcServices.getAggregationService());
            this.reconfigurableServices.add(mcServices.getAlertService());
            this.reconfigurableServices.add(mcServices.getParameterService());
        }
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
    public void setCloseAppListener(CloseAppListener closeAppAdapter) {
        this.closeAppAdapter = closeAppAdapter;
    }

    public CloseAppListener getCloseAppListener() {
        return this.closeAppAdapter;
    }

    /**
     * Hints the GC to do Garbage Collection and also hints it to go through the
     * finalization method of the pending finalization objects.
     */
    public static void hintGC() {
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
        String path = ".."
                + File.separator
                + NMFProvider.NANOSAT_MO_SUPERVISOR_NAME
                + File.separator
                + FILENAME_CENTRAL_DIRECTORY_SERVICE;

        File file = new File(path); // Select the file that we want to read from

        try {
            // Get the text out of that file...
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);

            try {
                String line = br.readLine(); // Reads the first line!
                br.close();
                return new URI(line);
            } catch (IOException ex) {
                Logger.getLogger(NMFProvider.class.getName()).log(Level.SEVERE, "An error happened!", ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NMFProvider.class.getName()).log(Level.WARNING,
                    "The File {0} could not be found!", file.getPath());
            return null;
        }

        return null;
    }

    public final void writeCentralDirectoryServiceURI(final String centralDirectoryURI, final String secondaryURI) {
        BufferedWriter wrt = null;
        try { // Reset the file
            wrt = new BufferedWriter(new FileWriter(FILENAME_CENTRAL_DIRECTORY_SERVICE, false));
            if (secondaryURI != null) {
                wrt.write(secondaryURI);
                wrt.write("\n");
            }

            wrt.write(centralDirectoryURI);
        } catch (IOException ex) {
            Logger.getLogger(NMFProvider.class.getName()).log(Level.WARNING,
                    "Unable to reset URI information from properties file {0}", ex);
        } finally {
            if (wrt != null) {
                try {
                    wrt.close();
                } catch (IOException ex) {
                }
            }
        }
    }

}
