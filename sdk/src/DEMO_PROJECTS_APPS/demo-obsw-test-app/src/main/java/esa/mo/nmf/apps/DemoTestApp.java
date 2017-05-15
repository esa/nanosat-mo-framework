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
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.mc.impl.provider.ParameterManager;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.provider.NanoSatMOMonolithicSim;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationCategory;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSetList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import esa.mo.nmf.NMFInterface;

/**
 * This class provides a simple Hello World demo cli provider
 *
 */
public class DemoTestApp {

    private final NMFInterface nanoSatMOFramework = new NanoSatMOMonolithicSim(new MCAdapter());
    private String str = "Hello World!";
    private Attribute something;
    private Blob laPicture = new Blob();
    private static final int NUMBER_OF_OBJS = 100000;

    public DemoTestApp() {

        try {
            AggregationDefinitionDetailsList defs = new AggregationDefinitionDetailsList();
            AggregationDefinitionDetails def = new AggregationDefinitionDetails();
            def.setDescription("dfvgdf");
            def.setCategory(new UOctet((short) AggregationCategory.GENERAL.getOrdinal()));
            def.setGenerationEnabled(true);
            def.setReportInterval(new Duration(45));
            def.setFilterEnabled(false);
            def.setFilteredTimeout(new Duration(54));
            AggregationParameterSetList aaa = new AggregationParameterSetList();
            AggregationParameterSet aa = new AggregationParameterSet();
            aa.setDomain(null);
            LongList lissssst = new LongList();
            lissssst.add(new Long(65));
            aa.setParameters(lissssst);
            aa.setSampleInterval(new Duration(43));
            aaa.add(aa);
            def.setParameterSets(aaa);
            for (int i = 0; i < NUMBER_OF_OBJS; i++) {
                defs.add(def);
            }
            URI uri = nanoSatMOFramework.getMCServices().getActionService().getConnectionProvider().getConnectionDetails().getProviderURI();
            ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(null, null, uri);
            for (int i = 0; i < NUMBER_OF_OBJS - 1; i++) {
                archDetails.add(HelperArchive.generateArchiveDetailsList(null, null, uri).get(0));
            }
            /*
            archDetails.get(0).setInstId((long) 1);
            archDetails.get(1).setInstId((long) 2);
            */
            long startTime = System.nanoTime();
            nanoSatMOFramework.getCOMServices().getArchiveService().reset();
            try {
                LongList objIds = nanoSatMOFramework.getCOMServices().getArchiveService().store(
                        true,
                        AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        archDetails,
                        defs,
                        null);

                objIds = null;
                
            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            /*
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
            */
            
            long estimatedTime = System.nanoTime() - startTime;
            Logger.getLogger(DemoTestApp.class.getName()).log(Level.INFO, "time: " + estimatedTime + " nanoseconds");

        } catch (NMFException ex) {
            Logger.getLogger(DemoTestApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        DemoTestApp demo = new DemoTestApp();
    }

    public class MCAdapter extends MonitorAndControlNMFAdapter {

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {
        }

        @Override
        public Attribute onGetValue(Identifier identifier, Byte rawType) {

            /*            
             Picture aaa;
             try {
             aaa = nanoSatMOFramework.getPlatformServices().getCameraService().previewPicture(null);
             laPicture = aaa.getContent();
             } catch (MALInteractionException | MALException ex) {
             Logger.getLogger(DemoTestApp.class.getName()).log(Level.SEVERE, null, ex);
             }

             Attribute value = (Attribute) HelperAttributes.javaType2Attribute(laPicture);  // Convert to Attribute type
             nanoSatMOFramework.getMCServices().getParameterService().pushSingleParameterValueAttribute(new Identifier("Pushed_Value"), value);
        
             return value;
             */
            return (Attribute) HelperAttributes.javaType2Attribute(str);

        }

        @Override
        public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
            str = values.get(0).getRawValue().toString(); // Let's set the str variable
            return true;  // to confirm that the variable was set
        }

        @Override
        public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
            return null;  // Action service not integrated
        }
    }

}
