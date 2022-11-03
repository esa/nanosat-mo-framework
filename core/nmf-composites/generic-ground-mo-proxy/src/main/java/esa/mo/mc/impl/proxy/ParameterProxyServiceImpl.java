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
package esa.mo.mc.impl.proxy;

import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.mc.impl.consumer.ParameterConsumerServiceImpl;
import esa.mo.nmf.NMFConsumer;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.common.CommonHelper;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.provider.ParameterInheritanceSkeleton;
import org.ccsds.moims.mo.mc.parameter.structures.*;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;

/**
 *
 */
public class ParameterProxyServiceImpl extends ParameterInheritanceSkeleton {

    private boolean proxyInitialiased = false;

    private ParameterConsumerServiceImpl consumer;
    private final Semaphore queueSemaphore = new Semaphore(1, true);
    
    private MALProvider parameterServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    private final ConnectionProvider connection = new ConnectionProvider();

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param adaptersList
     */
    public synchronized void initProxy(final HashMap<String, NMFConsumer> adaptersList) throws MALException {
        if (!proxyInitialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION) == null) {
                MCHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION) == null) {
                CommonHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION)
                        .getServiceByName(ConfigurationHelper.CONFIGURATION_SERVICE_NAME) == null)
            {
                ConfigurationHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION)
                        .getServiceByName(ParameterHelper.PARAMETER_SERVICE_NAME) == null)
            {
                ParameterHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

        }

        // One should initialize the Consumer first...
        // Maybe we can use the Ground MO Adapter and pass it during initialization... ;)
        this.consumer = adaptersList.get("lalalalla").getMCServices().getParameterService();
        
/*
        publisher = createMonitorValuePublisher(configuration.getDomain(),
                configuration.getNetwork(),
                SessionType.LIVE,
                ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));
*/
        // shut down old service transport
        if (null != parameterServiceProvider) {
            connection.close();
        }

        parameterServiceProvider = connection.startService(ParameterHelper.PARAMETER_SERVICE_NAME.toString(), ParameterHelper.PARAMETER_SERVICE, this);

        running = true;

        proxyInitialiased = true;
        Logger.getLogger(ParameterProxyServiceImpl.class.getName()).info("Parameter service READY");

    }

    @Override
    public ParameterValueDetailsList getValue(final LongList ll, final MALInteraction interaction) throws MALInteractionException, MALException {
        // In this case, the object this.consumer represents the connection 
        // between the consumer part of the proxy to the provider on Space
        
        //  Check the interaction object to know to whom the message should be forwarded to...
        final URI uriTo = interaction.getMessageHeader().getURITo();

        // Remove the first part of the uriTo
        final URI uriOfSpaceProvider = this.removePrefix(uriTo);

        // Select the right provider from a List of providers based on the 
        // uriOfSpaceProvider. If it does not exist, then initialize the 
        // connection to it.
        
        
        final boolean weCareAboutConnectionAvailableToGS = true; // Will be a future property that can be set
        final boolean weHaveQueueing = true;                     // Will be a future property that can be set
        final boolean isConnectionAvailable = true;              // Proxy needs to periodically check the connection for this var
        final boolean isSatelliteInSight = true;                 // This information must be provided OR it can be calculated if the orbit is known
        final int TIME_BETWEEN_COMMANDS = 50;                    // The time between each telecommand in milliseconds

//------------------Check Connection------------------        
        if (weCareAboutConnectionAvailableToGS) {
            // Is the connection to the GS available?
            if (!isConnectionAvailable) {
                // Publish RECEPTION Event failure back to the consumer

                throw new MALException("The connection to the Ground Station is not available...");
            }
        }
//----------------------------------------------------

        // Publish RECEPTION Event success back to the consumer
        
        
        try {
            if (weHaveQueueing) {
                queueSemaphore.acquire(); // Put it waiting in the queue list
            }

            // Make each telecommand wait TIME_BETWEEN_COMMANDS milliseconds before the previous
            Thread.sleep(TIME_BETWEEN_COMMANDS);

        } catch (final InterruptedException ex) {
            Logger.getLogger(ParameterProxyServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while (true) {
            if (isSatelliteInSight) {
                if (weHaveQueueing) {
                    // Let the next command in the queue continue its execution
                    queueSemaphore.release();
                }

                // Publish FORWARD Event back to the consumer (runs inside a thread)
                
                // Makes a call to the provider on Space
//                GetValueResponse response = this.consumer.getParameterStub().getValue(lLongList);

                // returns the answer to the connected consumer
//                return response;
                return null;
          }
        }

    }

    @Override
    public LongList enableGeneration(final Boolean isGroupIds, final InstanceBooleanPairList enableInstances,
            final MALInteraction interaction) throws MALException, MALInteractionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private URI removePrefix(final URI uriTo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setValue(final ParameterRawValueList prvl, final MALInteraction mali) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObjectInstancePairList addParameter(final ParameterCreationRequestList pcrl, final MALInteraction mali) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeParameter(final LongList ll, final MALInteraction mali) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObjectInstancePairList listDefinition(final IdentifierList il, final MALInteraction mali) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LongList updateDefinition(final LongList ll, final ParameterDefinitionDetailsList pddl, final MALInteraction mali) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
