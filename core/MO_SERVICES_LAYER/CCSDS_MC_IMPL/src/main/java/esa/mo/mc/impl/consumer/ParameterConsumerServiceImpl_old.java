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
package esa.mo.mc.impl.consumer;

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterStub;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;

/**
 *
 * @author Cesar Coelho
 */
public class ParameterConsumerServiceImpl_old {

    private ParameterStub parameterService = null;
    private MALConsumer tmConsumer;
    private ConnectionConsumer connection = new ConnectionConsumer();
    private DefaultTableModel parameterTableData;
    private SingleConnectionDetails connectionDetails;

    public DefaultTableModel getParameterTableData() {
        return parameterTableData;
    }

    public ParameterStub getParameterService() {
        return this.parameterService;
    }

    public SingleConnectionDetails getConnectionDetails(){
        return connectionDetails;
    }

    public ParameterConsumerServiceImpl_old(SingleConnectionDetails connectionDetails) throws MALException, MalformedURLException, MALInteractionException {

        this.connectionDetails = connectionDetails;

        String[] parameterTableCol = new String[]{
            "Obj Inst Id", "name", "description", "rawType", "rawUnit", "generationEnabled", "updateInterval"};

        parameterTableData = new javax.swing.table.DefaultTableModel(
                new Object[][]{}, parameterTableCol) {
                    Class[] types = new Class[]{
                        java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class,
                        java.lang.String.class, java.lang.Boolean.class, java.lang.Float.class
                    };

                    @Override               //all cells false
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }

                    @Override
                    public Class getColumnClass(int columnIndex) {
                        return types[columnIndex];
                    }
                };


        // Close old connection
        if (tmConsumer != null) {
            try {
                final Identifier subscriptionId = new Identifier("SUB");
                final IdentifierList subLst = new IdentifierList();
                subLst.add(subscriptionId);
                if (parameterService != null) {
                    parameterService.monitorValueDeregister(subLst);
                }

                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        tmConsumer = connection.startService(
                this.connectionDetails.getProviderURI(),
                this.connectionDetails.getBrokerURI(),
                this.connectionDetails.getDomain(),
                ParameterHelper.PARAMETER_SERVICE);

        this.parameterService = new ParameterStub(tmConsumer);


    }

    public org.ccsds.moims.mo.mc.parameter.body.GetValueResponse getValue(LongList longlist) {
        try {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.INFO, "getValue started");
            org.ccsds.moims.mo.mc.parameter.body.GetValueResponse value = parameterService.getValue(longlist);
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.INFO, "getValue executed");
            this.save2File();
            return value;
        } catch (MALException ex) {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    public LongList listDefinition(IdentifierList ids) {
        try {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.INFO, "listDefinition started");
            LongList objIds = parameterService.listDefinition(ids);
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.INFO, "listDefinition executed");
            this.save2File();
            return objIds;
        } catch (MALException ex) {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public LongList addDefinition(ParameterDefinitionDetailsList defs) {
        try {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.INFO, "addDefinition started");
            LongList ids = parameterService.addDefinition(defs);
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.INFO, "addDefinition executed");
            this.save2File();

            return ids;

        } catch (MALException ex) {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void updateDefinition(LongList ids, ParameterDefinitionDetailsList defs) {
        try {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.INFO, "updateDefinition started");
            parameterService.updateDefinition(ids, defs);
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.INFO, "updateDefinition executed");
            this.save2File();
        } catch (MALException ex) {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeDefinition(LongList objIds) {
        try {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.INFO, "removeDefinition started");
            parameterService.removeDefinition(objIds);
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.INFO, "removeDefinition executed");

            if (objIds.size() == 0) {
                return;
            }
            if (objIds.get(0) == null) {
                return;  // Now it is safe for the if below
            }

            this.save2File();
        } catch (MALException ex) {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ParameterConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void save2File() {
        // BUG
        // Code missing
    }
    

}
