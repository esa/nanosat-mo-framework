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
import org.ccsds.moims.mo.com.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mc.parameter.ParameterServiceInfo;
import org.ccsds.moims.mo.mc.structures.ParameterValue;
import org.ccsds.moims.mo.mc.structures.ParameterValueList;
import org.ccsds.moims.mo.mc.structures.ValidityState;

/**
 *
 * @author Cesar Coelho
 */
public class StoreParameters {

    public static void storeParameterValues(int numberOfObjs, NMFInterface connector) {
        try {
            ParameterValue pValue = new ParameterValue(ValidityState.VALID,
                    (Attribute) HelperAttributes.javaType2Attribute(123.4567), null);

            ParameterValueList values = new ParameterValueList();

            for (int i = 0; i < numberOfObjs; i++) {
                values.add(pValue);
            }
            ConnectionProvider connection = connector.getMCServices().getActionService().getConnectionProvider();
            ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(
                    null, null, connection.getConnectionDetails());
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
                for (int i = 0; i < values.size(); i++) {
                    ArchiveDetailsList xxx = new ArchiveDetailsList();
                    HeterogeneousList yyy = new HeterogeneousList();
                    xxx.add(archDetails.get(0));
                    yyy.add(values.get(i));

                    connector.getCOMServices().getArchiveService().store(true,
                            ParameterServiceInfo.PARAMETERVALUEINSTANCE_OBJECT_TYPE,
                            connection.getConnectionDetails().getDomain(),
                            xxx, yyy, null);

                }
            } catch (MALException | MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            long estimatedTime = System.nanoTime() - startTime;
            Logger.getLogger(BenchmarkApp.class.getName()).log(Level.INFO,
                    "Total time: " + numberOfObjs + " objects in {0} nanoseconds",
                    estimatedTime);
            float objectPerSec = numberOfObjs / ((float) estimatedTime / (float) 1000000000);
            float averageTimePerObj = 1 / objectPerSec;
            Logger.getLogger(BenchmarkApp.class.getName()).log(Level.INFO,
                    "Objects per second: " + objectPerSec + " (average: " + averageTimePerObj + " sec)");
        } catch (NMFException ex) {
            Logger.getLogger(BenchmarkApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
