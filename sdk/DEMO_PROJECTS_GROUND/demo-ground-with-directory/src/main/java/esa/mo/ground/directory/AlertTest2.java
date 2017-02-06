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
package esa.mo.ground.directory;

import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.EventCOMObject;
import esa.mo.com.impl.util.EventReceivedListener;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.groundmoadapter.GroundMOAdapter;
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
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mc.alert.AlertHelper;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetails;
import org.ccsds.moims.mo.mc.alert.structures.AlertEventDetails;
import org.ccsds.moims.mo.mc.alert.consumer.Alert;
import org.ccsds.moims.mo.mc.structures.ArgumentValue;
import org.ccsds.moims.mo.mc.structures.ArgumentValueList;


/*


date:		2016-04-08 11:08:50.286
sourceURI:	
eKey2:		MC - Alert
eventName:	AlertEvent
domainName:	esa.MEX.MEX_MO_Provider
eKey3:		
eKey4:		
detailsRelated:	1
null
1


date:		2016-04-08 11:08:50.426
sourceURI:	
eKey2:		COM - Archive
eventName:	ObjectStored
domainName:	esa.MEX.MEX_MO_Provider
eKey3:	6300
eKey4:		MC - Alert: AlertEvent
detailsRelated:	null
1
0


*/

/**
 *
 * @author Philip Brabbin
 */
public class AlertTest2 {

    //static final String URL_DIRECTORY_SERVICE = "rmi://131.176.53.64:1024/1024-MEX_MO_Provider-Directory";
//    static final String URL_DIRECTORY_SERVICE = "malhttp://131.176.53.64:61616/MEX_MO_Provider-Directory";
    static final String URL_DIRECTORY_SERVICE = "tcpip://mitydev2:1024-NanoSat_MO_Supervisor-Directory";

    public final GroundMOAdapter groundAdapter;

    private final Identifier subscriptionId;
    private final Random random = new Random();

    public AlertTest2() throws Throwable {

        ProviderSummaryList summaryList = GroundMOAdapter.retrieveProvidersFromDirectory(new URI(URL_DIRECTORY_SERVICE));

        if (!summaryList.isEmpty()) {
            groundAdapter = new GroundMOAdapter(summaryList.get(0));
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

        LongList moAlertDefIds = alertConsumer.listDefinition(alertDefNames);

        for (Long moAlertDefId : moAlertDefIds) {

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
        Subscription eventSub = ConnectionConsumer.subscriptionKeys(this.subscriptionId, new Identifier("*"), secondEntityKey, new Long(0), new Long(0));

        eventConsumer.addEventReceivedListener(eventSub, new EventConsumerAdapter());
    }

    public class EventConsumerAdapter extends EventReceivedListener {

        @Override
        public void onDataReceived (EventCOMObject eventCOMObject) {

            Date date = new Date(eventCOMObject.getTimestamp().getValue());
            Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            IdentifierList domain = eventCOMObject.getDomain();
            String domainName = HelperMisc.domain2domainId(domain);

            ObjectType objTypeEvent = eventCOMObject.getObjType();
            String eventName = HelperCOM.objType2COMObject(objTypeEvent).getObjectName().toString();
            
            Element body = eventCOMObject.getBody();

            if (body instanceof AlertEventDetails){
                AlertEventDetails receivedAlert = (AlertEventDetails) body;

                Long alertDefObjId = eventCOMObject.getRelated();

                AlertDefinitionDetails details = null;  // TBD - look this up in list of 
                //Severity severity = details.getSeverity();
                //String messageToDisplay = details.getName().getValue() + " ";
                String messageToDisplay = "<TEST-MSG>" + " ";

                ArgumentValueList argValues = receivedAlert.getArgumentValues();
                
                if(argValues != null){
                    if (argValues.size() == 1){
                        messageToDisplay += argValues.get(0).getValue().toString();
                    }
                    if (argValues.size() > 1){
                        for(int i = 0 ; i < argValues.size(); i++){
                            ArgumentValue argValue = argValues.get(i);
                            messageToDisplay += "[" + i + "] " + argValue.getValue().toString() + "\n";
                        }
                    }
                }

                System.out.println(format.format(date));
                //System.out.println(severity.toString());
                System.out.println(messageToDisplay);

                System.out.println();

            }else{
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
