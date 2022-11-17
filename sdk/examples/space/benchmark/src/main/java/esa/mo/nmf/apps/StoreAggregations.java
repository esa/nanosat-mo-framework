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
package esa.mo.nmf.apps;

import esa.mo.com.impl.util.HelperArchive;
import esa.mo.mc.impl.provider.ParameterManager;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFInterface;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSetList;

/**
 *
 * @author Cesar Coelho
 */
public class StoreAggregations {

    public static void storeAggregations(int numberOfObjs, NMFInterface connector) {
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
            lissssst.add(65L);
            aa.setParameters(lissssst);
            aa.setSampleInterval(new Duration(43));
            aa.setReportFilter(null);
            aaa.add(aa);
            def.setParameterSets(aaa);
            for (int i = 0; i < numberOfObjs; i++) {
                defs.add(def);
            }
            ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(null, null, connector
                .getMCServices().getActionService().getConnectionProvider().getConnectionDetails());
            for (int i = 0; i < numberOfObjs - 1; i++) {
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

                    connector.getCOMServices().getArchiveService().store(true,
                        AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE, connector.getMCServices()
                            .getActionService().getConnectionProvider().getConnectionDetails().getDomain(), xxx, yyy,
                        null);

                }

            } catch (MALException | MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            long estimatedTime = System.nanoTime() - startTime;
            Logger.getLogger(BenchmarkApp.class.getName()).log(Level.INFO, "Total time: " + numberOfObjs +
                " objects in {0} nanoseconds", estimatedTime);
            float objectPerSec = numberOfObjs / ((float) estimatedTime / (float) 1000000000);
            float averageTimePerObj = 1 / objectPerSec;
            Logger.getLogger(BenchmarkApp.class.getName()).log(Level.INFO, "Objects per second: " + objectPerSec +
                " (average: " + averageTimePerObj + " sec)");
        } catch (NMFException ex) {
            Logger.getLogger(BenchmarkApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
