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
package esa.mo.ground.echo;

import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.EventCOMObject;
import esa.mo.com.impl.util.EventReceivedListener;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mc.alert.AlertHelper;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetails;
import org.ccsds.moims.mo.mc.alert.structures.AlertEventDetails;
import org.ccsds.moims.mo.mc.alert.consumer.Alert;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;

/**
 *
 * @author Philip Brabbin
 */
public class AlertTest2 {

    static final String URL_DIRECTORY_SERVICE = "maltcp://x:1024-nanosat-mo-supervisor-Directory";
    public final GroundMOAdapterImpl groundAdapter;
    private final Identifier subscriptionId;
    private final Random random = new Random();

    public AlertTest2() throws Throwable {
        ProviderSummaryList summaryList = GroundMOAdapterImpl.retrieveProvidersFromDirectory(new URI(URL_DIRECTORY_SERVICE));

        if (!summaryList.isEmpty()) {
            groundAdapter = new GroundMOAdapterImpl(summaryList.get(0));
        } else {
            //Something went wrong...
            groundAdapter = null;
        }

        //----------------------------------------------------------------
        Alert alertConsumer = groundAdapter.getMCServices().getAlertService().getAlertStub();
        EventConsumerServiceImpl eventConsumer = groundAdapter.getCOMServices().getEventService();

        // get a list of identifiers for all Alert Definitions in the Archive
        IdentifierList alertDefNames = new IdentifierList();
        alertDefNames.add(new Identifier("*"));

        ObjectInstancePairList moAlertDefIds = alertConsumer.listDefinition(alertDefNames);

        for (ObjectInstancePair pair : moAlertDefIds) {
            Long moAlertDefId = pair.getObjIdentityInstanceId();

            // enable generation of this alert in the Alert Provider
            InstanceBooleanPairList enableInstance = new InstanceBooleanPairList();
            enableInstance.add(new InstanceBooleanPair(moAlertDefId, true));

            Boolean isGroupIds = false;
            alertConsumer.enableGeneration(isGroupIds, enableInstance);

            System.out.println("Generation enabled for Alert Def Id: " + moAlertDefId);
        }

        //subscribe to events
        //Get all object numbers from the Activity Tracking Service Events
        final Long secondEntityKey = 0xFFFFFFFFFF000000L & HelperCOM.generateSubKey(AlertHelper.ALERTDEFINITION_OBJECT_TYPE);
        this.subscriptionId = new Identifier("AlertEvent" + random.nextInt());  // Add some randomness in the subscriptionId to avoid collisions
        Subscription eventSub = ConnectionConsumer.subscriptionKeys(this.subscriptionId, new Identifier("*"), secondEntityKey, 0L, 0L);

        eventConsumer.addEventReceivedListener(eventSub, new EventConsumerAdapter());
    }

    public class EventConsumerAdapter extends EventReceivedListener {

        @Override
        public void onDataReceived(EventCOMObject eventCOMObject) {
            Date date = new Date(eventCOMObject.getTimestamp().getValue());
            Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            IdentifierList domain = eventCOMObject.getDomain();
            String domainName = HelperMisc.domain2domainId(domain);

            ObjectType objTypeEvent = eventCOMObject.getObjType();
            String eventName = HelperCOM.objType2COMObject(objTypeEvent).getObjectName().toString();

            Element body = eventCOMObject.getBody();

            if (body instanceof AlertEventDetails) {
                AlertEventDetails receivedAlert = (AlertEventDetails) body;

                Long alertDefObjId = eventCOMObject.getRelated();

                AlertDefinitionDetails details = null;  // TBD - look this up in list of 
                //Severity severity = details.getSeverity();
                //String messageToDisplay = details.getName().getValue() + " ";
                final StringBuilder messageToDisplay = new StringBuilder("<TEST-MSG>" + " ");

                AttributeValueList attValues = receivedAlert.getArgumentValues();

                if (attValues != null) {
                    if (attValues.size() == 1) {
                        messageToDisplay.append(attValues.get(0).getValue().toString());
                    }
                    if (attValues.size() > 1) {
                        for (int i = 0; i < attValues.size(); i++) {
                            AttributeValue attValue = attValues.get(i);
                            messageToDisplay.append("[").append(i).append("] ").append(attValue.getValue().toString()).append("\n");
                        }
                    }
                }

                System.out.println(format.format(date));
                //System.out.println(severity.toString());
                System.out.println(messageToDisplay);

                System.out.println();

            } else {
                // Something's wrong...
            }
        }
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Throwable {
        AlertTest2 demo = new AlertTest2();
    }
}
