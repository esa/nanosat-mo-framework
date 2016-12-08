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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.consumer.AggregationAdapter;
import org.ccsds.moims.mo.mc.aggregation.consumer.AggregationStub;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValueList;

/**
 *
 * @author Cesar Coelho
 */
public class AggregationConsumerServiceImpl_old {

    private ConnectionConsumer connection = new ConnectionConsumer();
    private final AggregationConsumerAdapter adapterAggregation = new AggregationConsumerAdapter();
    private AggregationStub aggregationService = null;
    private MALConsumer tmConsumer;
    private DefaultTableModel aggregationTableData;
    private List<DefaultTableModel> parameterSetsTableDataAll = new ArrayList<DefaultTableModel>();
    private SingleConnectionDetails connectionDetails;
    private ParameterConsumerServiceImpl_old parameterConsumer;

    public DefaultTableModel getAggregationTableData() {
        return aggregationTableData;
    }

    public List<DefaultTableModel> getParameterSetsTableDataAll() {
        return parameterSetsTableDataAll;
    }

    public AggregationConsumerAdapter getAggregationAdapter() {
        return this.adapterAggregation;
    }

    public ParameterConsumerServiceImpl_old getParameterConsumer() {
        return parameterConsumer;
    }

    public AggregationStub getAggregationService() {
        return this.aggregationService;
    }

    public SingleConnectionDetails getConnectionDetails(){
        return connectionDetails;
    }

    public AggregationConsumerServiceImpl_old(SingleConnectionDetails connectionDetails, ParameterConsumerServiceImpl_old parameterConsumer) throws MALException, MalformedURLException, MALInteractionException {

        this.connectionDetails = connectionDetails;
        this.parameterConsumer = parameterConsumer;

        String[] aggregationTableCol = new String[]{
            "Obj Inst Id", "name", "description", "category",
            "generationEnabled", "updateInterval", "filterEnabled", "filteredTimeout"};

        aggregationTableData = new javax.swing.table.DefaultTableModel(
                new Object[][]{}, aggregationTableCol) {
                    Class[] types = new Class[]{
                        java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class,
                        java.lang.Boolean.class, java.lang.Float.class, java.lang.Boolean.class, java.lang.Float.class
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
                if (aggregationService != null) {
                    aggregationService.monitorValueDeregister(subLst);
                }

                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        tmConsumer = connection.startService(
                this.connectionDetails.getProviderURI(),
                this.connectionDetails.getBrokerURI(),
                this.connectionDetails.getDomain(),
                AggregationHelper.AGGREGATION_SERVICE);

        this.aggregationService = new AggregationStub(tmConsumer);

        // Subscribe to Parameter Values
        Subscription subscription = ConnectionConsumer.subscriptionWildcard();

        aggregationService.monitorValueRegister(subscription, this.adapterAggregation);

    }

    // Get ObjId from Name (parameter table)
    private Long getObjIdFromName(String name) {
        for (int i = 0; i < parameterConsumer.getParameterTableData().getRowCount(); i++) {
            String parameter = parameterConsumer.getParameterTableData().getValueAt(i, 1).toString();
            if (parameter.equals(name)) {
                return new Long(parameterConsumer.getParameterTableData().getValueAt(i, 0).toString());
            }
        }
        return null; // Not found (it shouldn't occur...)
    }

    // Get ObjId from Name
    public Long getParameterSetsObjIdFromAggNameAndIndex(String name, int index) {
        for (int i = 0; i < aggregationTableData.getRowCount(); i++) {
            String aggregation = aggregationTableData.getValueAt(i, 1).toString();
            if (aggregation.equals(name) && index <= parameterSetsTableDataAll.get(i).getRowCount()) {
                return getObjIdFromName(parameterSetsTableDataAll.get(i).getValueAt(index, 0).toString());
            }
        }
        return null; // Not found (it shouldn't occur...)
    }

    public org.ccsds.moims.mo.mc.aggregation.body.GetValueResponse getValue(LongList longlist) {
        try {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "getValue started");
            org.ccsds.moims.mo.mc.aggregation.body.GetValueResponse value = aggregationService.getValue(longlist);
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "getValue executed");
            this.save2File();
            return value;
        } catch (MALException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void enableGeneration(Boolean enable, InstanceBooleanPairList boolPairList) {
        try {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "enableGeneration started");
            aggregationService.enableGeneration(enable, boolPairList);
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "enableGeneration executed");
            this.save2File();

        } catch (MALException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public LongList listDefinition(IdentifierList ids) {
        try {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "listDefinition started");
            LongList objIds = aggregationService.listDefinition(ids);
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "listDefinition executed");
            this.save2File();
            return objIds;
        } catch (MALException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public LongList addDefinition(AggregationDefinitionDetailsList defs) {
        try {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "addDefinition started");
            LongList ids = aggregationService.addDefinition(defs);
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "addDefinition executed");
            this.save2File();

            return ids;

        } catch (MALException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void updateDefinition(LongList ids, AggregationDefinitionDetailsList defs) {
        try {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "updateDefinition started");
            aggregationService.updateDefinition(ids, defs);
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "updateDefinition executed");
            this.save2File();
        } catch (MALException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeDefinition(LongList objIds) {
        try {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "removeDefinition started");
            aggregationService.removeDefinition(objIds);
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "removeDefinition executed");

            if (objIds.size() == 0) {
                return;
            }
            if (objIds.get(0) == null) {
                return;  // Now it is safe for the if below
            }
            this.save2File();
        } catch (MALException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void save2File() {
        // BUG
        // Code missing
    }

    public void enableFilter(Boolean bool, InstanceBooleanPairList boolPairList) {

        try {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "enableFilter started");
            aggregationService.enableFilter(bool, boolPairList);
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "enableFilter executed");

            this.save2File();
        } catch (MALException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public class AggregationConsumerAdapter extends AggregationAdapter {

        @Override
        public void monitorValueNotifyReceived(final MALMessageHeader msgHeader,
                final Identifier lIdentifier, final UpdateHeaderList lUpdateHeaderList,
                final ObjectIdList lObjectIdList, final AggregationValueList lAggregationValueList,
                final Map qosp) {
            /*
             Logger.getLogger(AggregationConsumerServiceImpl_old.class.getName()).log(Level.INFO, "Received update aggregations list of size : {0}", lObjectIdList.size());
             final long iDiff = ConfigurationProvider.getTimestampMillis().getValue() - msgHeader.getTimestamp().getValue();

             final UpdateHeader updateHeader = lUpdateHeaderList.get(0);
             final String Aggname = updateHeader.getKey().getFirstSubKey().getValue();
             final int objId = updateHeader.getKey().getSecondSubKey().intValue();

             try {
             if (msgBoxOn.isSelected() && lUpdateHeaderList.size() != 0 && lAggregationValueList.size() != 0) {
             String str = "";
             final AggregationValue aggregationValue = lAggregationValueList.get(0);
             str += "AggregationValue generationMode: " + aggregationValue.getGenerationMode().toString() + " (filtered: " + aggregationValue.getFiltered().toString() + ")" + "\n";

             str += "Aggregation objId " + objId + " (name: " + Aggname + "):" + "\n";

             for (int i = 0; i < aggregationValue.getParameterSetValues().size(); i++) {  // Cycle through parameterSetValues
             str += "- AggregationParameterSet values index: " + i + "\n";
             str += "deltaTime: " + aggregationValue.getParameterSetValues().get(i).getDeltaTime();
             str += " and intervalTime: " + aggregationValue.getParameterSetValues().get(i).getIntervalTime() + "\n";
             AggregationSetValue parameterSetsValue = aggregationValue.getParameterSetValues().get(i);

             for (int j = 0; j < parameterSetsValue.getValues().size(); j++) { // Cycle through the values
             if (parameterSetsValue.getValues().get(j) == null) {
             continue;
             }
             str += "values index: " + j + "\n";
             str += "validityState: " + parameterSetsValue.getValues().get(j).getInvalidSubState().toString() + "\n";
             if (parameterSetsValue.getValues().get(j).getRawValue() != null) {
             str += "rawValue: " + parameterSetsValue.getValues().get(j).getRawValue().toString() + "\n";
             }
             if (parameterSetsValue.getValues().get(j).getConvertedValue() != null) {
             str += "convertedValue: " + parameterSetsValue.getValues().get(j).getConvertedValue().toString() + "\n";
             }
             str += "\n";
             }
             }

             JOptionPane.showMessageDialog(null, str, "Returned Values from the Provider", JOptionPane.PLAIN_MESSAGE);
             }

             if (Aggname.equals("Map")) {
             for (int i = 0; i < lAggregationValueList.get(0).getParameterSetValues().size(); i++) {
             if (getParameterSetsObjIdFromAggNameAndIndex(Aggname, i) == null || lAggregationValueList.get(0).getParameterSetValues().get(i).getValues().get(0) == null) // Not found... :/
             {
             continue;
             }

             // Is it the latitude parameter?
             if (getParameterSetsObjIdFromAggNameAndIndex(Aggname, i).equals(getObjIdFromName("GPS.Latitude"))) {
             mapLatitude = Double.parseDouble(lAggregationValueList.get(0).getParameterSetValues().get(i).getValues().get(0).getRawValue().toString());
             }

             // Is it the longitude parameter?
             if (getParameterSetsObjIdFromAggNameAndIndex(Aggname, i).equals(getObjIdFromName("GPS.Longitude"))) {
             mapLongitude = Double.parseDouble(lAggregationValueList.get(0).getParameterSetValues().get(i).getValues().get(0).getRawValue().toString());
             }

             if (mapLatitude != 0 && mapLongitude != 0) {  // So, it's still the same message?
             pictureLabel.setIcon(map.addCoordinate(mapLatitude, mapLongitude));
             break;
             }
             }
             mapLatitude = 0;
             mapLongitude = 0;
             }

             } catch (NumberFormatException ex) {
             LOGGER.log(Level.WARNING, "Error decoding update with name: {0}", lUpdateHeaderList.get(0).getKey().getFirstSubKey().getValue());
             }
             }
             */
        }
    }

}
