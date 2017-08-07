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
package esa.mo.nmf.apps;

import esa.mo.com.impl.util.HelperArchive;
import esa.mo.mc.impl.provider.ParameterManager;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFInterface;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSetList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;

/**
 *
 * @author Cesar Coelho
 */
public class MCAdapter extends MonitorAndControlNMFAdapter {

    private NMFInterface nanoSatMOFramework;
//    private static int NUMBER_OF_OBJS = 5000;
    private static int NUMBER_OF_OBJS = 10;
    private static final String ACTION_STORE_AGGS = "StoreAggregations";

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        // ------------------ Actions ------------------
        ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
        IdentifierList actionNames = new IdentifierList();

        ArgumentDefinitionDetailsList arguments1 = new ArgumentDefinitionDetailsList();
        {
            Byte rawType = Attribute._DOUBLE_TYPE_SHORT_FORM;
            String rawUnit = "KB";
            ConditionalConversionList conditionalConversions = null;
            Byte convertedType = null;
            String convertedUnit = null;

            arguments1.add(new ArgumentDefinitionDetails(rawType, rawUnit,
                    conditionalConversions, convertedType, convertedUnit));
        }

        actionDefs.add(new ActionDefinitionDetails(
                "Stores " + NUMBER_OF_OBJS + " aggregation definition objects in the COM Archive.",
                new UOctet((short) 0),
                new UShort(0),
                arguments1,
                null
        ));
        actionNames.add(new Identifier(ACTION_STORE_AGGS));

        LongList actionObjIds = registration.registerActions(actionNames, actionDefs);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, Byte rawType) {
        return null;
    }

    @Override
    public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {

        if (ACTION_STORE_AGGS.equals(name.getValue())) {
            try {
                AggregationDefinitionDetailsList defs = new AggregationDefinitionDetailsList();
                AggregationDefinitionDetails def = new AggregationDefinitionDetails();
                def.setDescription("dfvgdf");
                def.setCategory(new UOctet((short) 1));
                def.setSendUnchanged(false);
                def.setGenerationEnabled(true);
                def.setReportInterval(new Duration(45));
                def.setFilterEnabled(false);
                def.setFilteredTimeout(new Duration(54));
                def.setSendDefinitions(false);
                AggregationParameterSetList aaa = new AggregationParameterSetList();
                AggregationParameterSet aa = new AggregationParameterSet();
                aa.setDomain(null);
                LongList lissssst = new LongList();
                lissssst.add(new Long(65));
                aa.setParameters(lissssst);
                aa.setSampleInterval(new Duration(43));
                aa.setReportFilter(null);
                aaa.add(aa);
                def.setParameterSets(aaa);
                for (int i = 0; i < NUMBER_OF_OBJS; i++) {
                    defs.add(def);
                }
                ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(null, null,
                        nanoSatMOFramework.getMCServices().getActionService().getConnectionProvider().getConnectionDetails());
                for (int i = 0; i < NUMBER_OF_OBJS - 1; i++) {
                    archDetails.add(archDetails.get(0));
                }
                long startTime = System.nanoTime();
                /*
            try {
            LongList objIds = nanoSatMOFramework.getCOMServices().getArchiveService().store(
            true,
            AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE,
            nanoSatMOFramework.getMCServices().getActionService().getConnectionProvider().getConnectionDetails().getDomain(),
            archDetails,
            defs,
            null);
            
            } catch (MALException ex) {
            Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
            Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            for (int i = 0; i < NUMBER_OF_OBJS - 1; i++) {
            archDetails.get(i).setInstId(new Long(i));
            }
            
            try {
            nanoSatMOFramework.getCOMServices().getArchiveService().update(
            AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE,
            nanoSatMOFramework.getMCServices().getActionService().getConnectionProvider().getConnectionDetails().getDomain(),
            archDetails,
            defs,
            null);
            
            } catch (MALException ex) {
            Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
            Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            
                 */
                try {

                    for (int i = 0; i < defs.size(); i++) {
                        ArchiveDetailsList xxx = new ArchiveDetailsList();
                        AggregationDefinitionDetailsList yyy = new AggregationDefinitionDetailsList();
                        xxx.add(archDetails.get(0));
                        yyy.add(defs.get(i));

                        nanoSatMOFramework.getCOMServices().getArchiveService().store(
                                true,
                                AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE,
                                nanoSatMOFramework.getMCServices().getActionService().getConnectionProvider().getConnectionDetails().getDomain(),
                                xxx,
                                yyy,
                                null);

                    }

                } catch (MALException ex) {
                    Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MALInteractionException ex) {
                    Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                long estimatedTime = System.nanoTime() - startTime;
                Logger.getLogger(BenchmarkApp.class.getName()).log(Level.INFO, "Total time: " + NUMBER_OF_OBJS + " objects in {0} nanoseconds", estimatedTime);
                float objectPerSec = NUMBER_OF_OBJS / ((float) ((float) estimatedTime / (float) 1000000000));
                float averageTimePerObj = 1 / objectPerSec;
                Logger.getLogger(BenchmarkApp.class.getName()).log(Level.INFO, "Objects per second: " + objectPerSec + " (average: " + averageTimePerObj + " sec)");

//        nanoSatMOFramework.getCOMServices().getArchiveService().reset();
            } catch (NMFException ex) {
                Logger.getLogger(BenchmarkApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;  // Action service not integrated
    }

    public void setNMF(final NMFInterface nanoSatMOFramework) {
        this.nanoSatMOFramework = nanoSatMOFramework;
    }

}
