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
package esa.mo.nmf.nanosatmosupervisor;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.Quota;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.nmf.NMFProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.misc.Const;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.CloseAppListener;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.NMFException;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.reconfigurable.provider.PersistProviderConfiguration;
import esa.mo.sm.impl.provider.AppsLauncherProviderServiceImpl;
import esa.mo.sm.impl.provider.CommandExecutorProviderServiceImpl;
import esa.mo.sm.impl.util.PMBackend;
import esa.mo.sm.impl.provider.PackageManagementProviderServiceImpl;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;

/**
 * The implementation of the NanoSat MO Supervisor that can be extended by particular
 * implementations.
 *
 * @author Cesar Coelho
 */
public abstract class NanoSatMOSupervisor extends NMFProvider {

  private final PackageManagementProviderServiceImpl packageManagementService
      = new PackageManagementProviderServiceImpl();
  private final AppsLauncherProviderServiceImpl appsLauncherService
      = new AppsLauncherProviderServiceImpl();
  private final CommandExecutorProviderServiceImpl commandExecutorService
      = new CommandExecutorProviderServiceImpl();

  /**
   * Initializes the NanoSat MO Supervisor. The MonitorAndControlAdapter adapter class can be
   * extended for remote monitoring and control with the CCSDS Monitor and Control services. One can
   * also extend the SimpleMonitorAndControlAdapter class which contains a simpler interface.
   *
   * @param mcAdapter                The adapter to connect the actions and parameters to the
   *                                 corresponding methods and variables of a specific entity.
   * @param platformServices         The Platform services consumer stubs
   * @param packageManagementBackend The Package Management services backend.
   */
  public void init(MonitorAndControlNMFAdapter mcAdapter,
      PlatformServicesConsumer platformServices,
      PMBackend packageManagementBackend) {
    super.startTime = System.currentTimeMillis();
    HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties
    ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
    HelperMisc.setInputProcessorsProperty();

    // Enforce the App Name property to be Const.NANOSAT_MO_SUPERVISOR_NAME
    System.setProperty(HelperMisc.PROP_MO_APP_NAME, Const.NANOSAT_MO_SUPERVISOR_NAME);

    // Provider name to be used on the Directory service...
    this.providerName = System.getProperty(HelperMisc.PROP_MO_APP_NAME);

    this.platformServices = platformServices;

    try {
      Quota stdQuota = new Quota();
      this.comServices.init();
      this.heartbeatService.init();
      this.directoryService.init(comServices);
      this.appsLauncherService.init(comServices, directoryService);
      this.commandExecutorService.init(comServices);
      this.packageManagementService.init(comServices, packageManagementBackend);
      this.comServices.initArchiveSync();
      super.reconfigurableServices.add(this.appsLauncherService);
      this.appsLauncherService.setStdPerApp(stdQuota);
      this.comServices.getArchiveSyncService().setQuota(stdQuota);
      this.startMCServices(mcAdapter);
      this.initPlatformServices(comServices);
    } catch (MALException ex) {
      Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.SEVERE,
          "The services could not be initialized. "
          + "Perhaps there's something wrong with the Transport Layer.", ex);
      return;
    }

    // Are the dynamic changes enabled?
    if ("true".equals(System.getProperty(Const.DYNAMIC_CHANGES_PROPERTY))) {
      Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.INFO,
          "Loading previous configurations...");

      // Activate the previous configuration
      final ObjectId confId = new ObjectId(ConfigurationHelper.PROVIDERCONFIGURATION_OBJECT_TYPE,
          new ObjectKey(ConfigurationProviderSingleton.getDomain(),
              DEFAULT_PROVIDER_CONFIGURATION_OBJID));

      super.providerConfiguration = new PersistProviderConfiguration(this, confId,
          comServices.getArchiveService());

      try {
        super.providerConfiguration.loadPreviousConfigurations();
      } catch (IOException ex) {
        Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    if (mcAdapter != null) {
      MCRegistration registration
          = new MCRegistration(comServices, mcServices.getParameterService(),
              mcServices.getAggregationService(), mcServices.getAlertService(),
              mcServices.getActionService());
      mcAdapter.initialRegistrations(registration);
    }

    // Populate the Directory service with the entries from the URIs File
    Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.INFO,
        "Populating Directory service...");
    this.directoryService.loadURIs(Const.NANOSAT_MO_SUPERVISOR_NAME);

    final String primaryURI
        = this.directoryService.getConnection().getPrimaryConnectionDetails().getProviderURI().toString();

    final SingleConnectionDetails det
        = this.directoryService.getConnection().getSecondaryConnectionDetails();
    final String secondaryURI = (det != null) ? det.getProviderURI().toString() : null;
    this.writeCentralDirectoryServiceURI(primaryURI, secondaryURI);
    Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.INFO,
        "NanoSat MO Supervisor initialized in "
        + (((float) (System.currentTimeMillis() - super.startTime)) / 1000)
        + " seconds!");
    Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.INFO,
        "URI: {0}\n", primaryURI);

    // We just loaded everything, it is a good time to
    // hint the garbage collector and clean up some memory
    // NanoSatMOFrameworkProvider.hintGC();
  }

  @Override
  public void setCloseAppListener(final CloseAppListener closeAppAdapter) {
    this.closeAppAdapter = closeAppAdapter;
  }

  /**
   * It closes the App gracefully.
   *
   * @param source The source of the triggering. Can be null
   */
  @Override
  public final void closeGracefully(final ObjectId source) {
    try {
      long timestamp = System.currentTimeMillis();

      // Acknowledge the reception of the request to close (Closing...)
      Long eventId = this.getCOMServices().getEventService().generateAndStoreEvent(
          AppsLauncherHelper.STOPPING_OBJECT_TYPE,
          ConfigurationProviderSingleton.getDomain(),
          null,
          null,
          source,
          null);

      final URI uri
          = this.getCOMServices().getEventService().getConnectionProvider().getConnectionDetails().getProviderURI();

      try {
        this.getCOMServices().getEventService().publishEvent(uri, eventId,
            AppsLauncherHelper.STOPPING_OBJECT_TYPE, null, source, null);
      } catch (IOException ex) {
        Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.SEVERE, null, ex);
      }

      // Close the app...
      // Make a call on the app layer to close nicely...
      if (this.closeAppAdapter != null) {
        Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.INFO,
            "Triggering the closeAppAdapter of the app business logic...");
        this.closeAppAdapter.onClose(); // Time to sleep, boy!
      }

      Long eventId2 = this.getCOMServices().getEventService().generateAndStoreEvent(
          AppsLauncherHelper.STOPPED_OBJECT_TYPE,
          ConfigurationProviderSingleton.getDomain(),
          null,
          null,
          source,
          null);

      try {
        this.getCOMServices().getEventService().publishEvent(uri, eventId2,
            AppsLauncherHelper.STOPPED_OBJECT_TYPE, null, source, null);
      } catch (IOException ex) {
        Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.SEVERE, null, ex);
      }

      // Should close them safely as well...
//        provider.getMCServices().closeServices();
//        provider.getCOMServices().closeServices();
      this.getCOMServices().closeAll();

      // Exit the Java application
      Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.INFO,
          "Success! The currently running Java Virtual Machine will now terminate. "
          + "(NanoSat MO Supervisor closed in: " + (System.currentTimeMillis() - timestamp) + " ms)\n");

    } catch (NMFException ex) {
      Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.SEVERE, null, ex);
    }

    System.exit(0);
  }

  public abstract void initPlatformServices(COMServicesProvider comServices);

}
