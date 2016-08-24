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
package esa.mo.demo.provider.opssatsimulation4;

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.provider.EventProviderServiceImpl;
import esa.mo.demo.provider.opssatsimulation2.MonitoringParametersImpl;
import esa.mo.mc.impl.provider.AggregationProviderServiceImpl;
import esa.mo.mc.impl.provider.ParameterManager;
import esa.mo.mc.impl.provider.ParameterProviderServiceImpl;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperMisc;
import opssat.simulator.InstrumentsSimulator;

/**
 * This class provides a simple cli for the control of the provider.
 * 
 */
public class DemoSimulationOPSSAT4
{

  private static final InstrumentsSimulator instrumentSimulator = new InstrumentsSimulator();
  private static ParameterManager parameterManager;
  
  private static final ArchiveProviderServiceImpl archiveService = new ArchiveProviderServiceImpl();
  private static final EventProviderServiceImpl eventService = new EventProviderServiceImpl();
  private static final ParameterProviderServiceImpl parameterService = new ParameterProviderServiceImpl();
  private static final AggregationProviderServiceImpl aggregationService = new AggregationProviderServiceImpl();

  /**
   * Main command line entry point.
   *
   * @param args the command line arguments
   * @throws java.lang.Exception If there is an error
   */
  public static void main(final String args[]) throws Exception
  {

    ConnectionProvider.resetURILinksFile();
    HelperMisc.loadPropertiesFile();

    archiveService.init(null);

    // No Archive for the Parameter service
    parameterManager = new ParameterManager(null, new MonitoringParametersImpl(instrumentSimulator) );
//    parameterManager = new ParameterManager(comServices, new MonitoringParametersImpl(instrumentSimulator));
    
    parameterService.init(parameterManager);
    
    COMServicesProvider comServices = new COMServicesProvider();
    comServices.setArchiveService(archiveService);
    
    aggregationService.init(comServices, parameterManager);

  }

}

