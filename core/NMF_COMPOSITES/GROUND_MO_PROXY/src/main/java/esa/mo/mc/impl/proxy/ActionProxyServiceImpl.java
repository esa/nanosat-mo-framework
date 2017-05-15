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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.mc.impl.provider.ActionManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.action.ActionHelper;
import org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton;
import org.ccsds.moims.mo.mc.action.structures.ActionCreationRequestList;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;

/**
 *
 */
public class ActionProxyServiceImpl extends ActionInheritanceSkeleton {

    private final static String IS_INTERMEDIATE_RELAY_PROPERTY = "esa.mo.mc.impl.provider.ActionProviderServiceImpl.isIntermediateRelay";
    private MALProvider actionServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    private ActionManager manager;
    private final ConnectionProvider connection = new ConnectionProvider();

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @param actions
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices, 
            ActionInvocationListener actions) throws MALException {
        if (!initialiased) {

            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
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
                ActionHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }

        }

        // Shut down old service transport
        if (null != actionServiceProvider) {
            connection.close();
        }

        actionServiceProvider = connection.startService(ActionHelper.ACTION_SERVICE_NAME.toString(), ActionHelper.ACTION_SERVICE, this);

        running = true;
        manager = new ActionManager(comServices, actions);

        initialiased = true;
        Logger.getLogger(ActionProxyServiceImpl.class.getName()).info("Action service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != actionServiceProvider) {
                actionServiceProvider.close();
            }

            connection.close();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(ActionProxyServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }
    
    public ConnectionProvider getConnectionProvider(){
        return this.connection;
    }


    @Override
    public void submitAction(Long actionInstId, ActionInstanceDetails actionDetails, MALInteraction interaction) throws MALInteractionException, MALException {

        
        
    }

    @Override
    public Boolean preCheckAction(ActionInstanceDetails actionDetails, MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList invIndexList = new UIntegerList();

        // 3.2.10.3.2
        /*
        if (!manager.exists(actionDetails.getDefInstId())) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }
        s*/

        // 3.2.10.2.c
        boolean accepted = manager.checkActionInstanceDetails(actionDetails, invIndexList);

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.2.9.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }
        
        return accepted;

    }    

    @Override
    public ObjectInstancePairList listDefinition(IdentifierList il, MALInteraction mali) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObjectInstancePairList addAction(ActionCreationRequestList acrl, MALInteraction mali) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LongList updateDefinition(LongList ll, ActionDefinitionDetailsList addl, MALInteraction mali) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeAction(LongList ll, MALInteraction mali) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
