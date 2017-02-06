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
package esa.mo.mc.impl.proxy;

import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.mc.impl.consumer.ParameterConsumerServiceImpl;
import esa.mo.nmf.groundmoadapter.GroundMOAdapter;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.body.GetValueResponse;
import org.ccsds.moims.mo.mc.parameter.provider.ParameterInheritanceSkeleton;
import org.ccsds.moims.mo.mc.parameter.structures.*;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueList;

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
    public synchronized void initProxy(HashMap<String, GroundMOAdapter> adaptersList) throws MALException {
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

            try {
                ConfigurationHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }

            try {
                ParameterHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }

        }

        // One should initialize the Consumer first...
        // Maybe we can use the Ground MO Adapter and pass it during initialization... ;)
        this.consumer = adaptersList.get("lalalalla").getMCServices().getParameterService();
        
/*
        publisher = createMonitorValuePublisher(configuration.getDomain(),
                configuration.getNetwork(),
                SessionType.LIVE,
                new Identifier("LIVE"),
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
    public GetValueResponse getValue(final LongList lLongList, final MALInteraction interaction) throws MALException, MALInteractionException {

        // In this case, the object this.consumer represents the connection 
        // between the consumer part of the proxy to the provider on Space
        
        //  Check the interaction object to know to whom the message should be forwarded to...
        URI uriTo = interaction.getMessageHeader().getURITo();

        // Remove the first part of the uriTo
        URI uriOfSpaceProvider = this.removePrefix(uriTo);

        // Select the right provider from a List of providers based on the 
        // uriOfSpaceProvider. If it does not exist, then initialize the 
        // connection to it.
        
        
        boolean weCareAboutConnectionAvailableToGS = true; // Will be a future property that can be set
        boolean weHaveQueueing = true;                     // Will be a future property that can be set
        boolean isConnectionAvailable = true;              // Proxy needs to periodically check the connection for this var
        boolean isSatelliteInSight = true;                 // This information must be provided OR it can be calculated if the orbit is known
        int TIME_BETWEEN_COMMANDS = 50;                    // The time between each telecommand in milliseconds

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

        } catch (InterruptedException ex) {
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
                GetValueResponse response = this.consumer.getParameterStub().getValue(lLongList);

                // returns the answer to the connected consumer
                return response;

            }
        }

    }

    @Override
    public void enableGeneration(final Boolean isGroupIds, final InstanceBooleanPairList enableInstances,
            final MALInteraction interaction) throws MALException, MALInteractionException {

    }

    @Override
    public void setValue(final LongList paramDefInstId, final ParameterValueList newValues,
            final MALInteraction interaction) throws MALException, MALInteractionException {

    }

    @Override
    public LongList listDefinition(final IdentifierList lIdentifier, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement: 3.3.8.2.1
        LongList outLongLst = new LongList();

        // Errors
        // The operation does not return any errors.
        return outLongLst;
    }

    @Override
    public LongList addDefinition(final ParameterDefinitionDetailsList lParameterDefinitionList,
            final MALInteraction interaction) throws MALException, MALInteractionException {
        LongList outLongLst = new LongList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();
        ParameterDefinitionDetails tempParameterDefinition;

        return outLongLst; // requirement: 3.3.9.2.4
    }

    @Override
    public void updateDefinition(LongList paramDefInstIds, ParameterDefinitionDetailsList paramDefDetails,
            MALInteraction interaction) throws MALInteractionException, MALException { // requirement: 3.3.10.2.1

    }

    @Override
    public void removeDefinition(final LongList lLongList, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement: 3.3.11.2.1

    }

    private URI removePrefix(URI uriTo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
