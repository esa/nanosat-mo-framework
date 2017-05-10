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
package esa.mo.nmf.groundmoproxy;

import esa.mo.com.impl.archive.encoding.BinaryDecoder;
import esa.mo.mal.transport.gen.GENTransport;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.ccsds.moims.mo.mal.transport.MALTransport;
import org.ccsds.moims.mo.mal.transport.MALTransportFactory;

public class ProtocolBridgeSPP extends ProtocolBridge {

    private MALTransport transportA;
    private MALTransport transportB;
    private MALEndpoint epA;
    private MALEndpoint epB;
    private final static String PROTOCOL_SPP = "malspp";
    private final int rangeStart = 10;
    private final int rangeEnd = 60;
    private final HashMap<String, Long> virtualAPIDsMap = new HashMap<String, Long>();
    private final HashMap<Long, String> reverseMap = new HashMap<Long, String>();
    private final AtomicLong uniqueAPID = new AtomicLong(rangeStart);
    private final Object MUTEX = new Object();

    public void init(final String protocol, final Map properties) throws MALException, Exception {
        transportA = createTransport(PROTOCOL_SPP, properties);
        transportB = createTransport(protocol, properties);

//        transportA = TransportSingleton.instance(protocolA, null);
//        transportB = TransportSingleton.instance(protocolB, null);
        epA = createEndpoint(PROTOCOL_SPP, transportA);
        epB = createEndpoint(protocol, transportB);

        System.out.println("Linking transports...");
        epA.setMessageListener(new BridgeMessageHandlerSPP(epB));
        epB.setMessageListener(new BridgeMessageHandlerSPP(epA));

        System.out.println("Starting message delivery...");
        epA.startMessageDelivery();
        epB.startMessageDelivery();
    }

    public URI getRoutingProtocol() {
        return epB.getURI();
    }

    protected class BridgeMessageHandlerSPP implements MALMessageListener {

        private final MALEndpoint destination;

        public BridgeMessageHandlerSPP(MALEndpoint destination) {
            this.destination = destination;
        }

        @Override
        public void onInternalError(MALEndpoint callingEndpoint, Throwable err) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onTransmitError(MALEndpoint callingEndpoint, MALMessageHeader srcMessageHeader, MALStandardError err, Map qosMap) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onMessage(MALEndpoint callingEndpoint, MALMessage srcMessage) {
            try {
                String uriFrom = srcMessage.getHeader().getURIFrom().getValue();
                System.out.println("\nReceived message from: " + uriFrom);

                boolean isFromSPP = this.isSPP(uriFrom);

                MALMessage dMsg = null;

                if (isFromSPP) {
                    Long virtualAPID = getVirtualAPID(srcMessage.getHeader().getURITo().getValue());
                    String reverse = null;

                    synchronized (MUTEX) {
                        reverse = reverseMap.get(virtualAPID);

                        if (reverse == null) {
                            System.out.println("The reverse APID for APID: " + virtualAPID + " could not be found!");
                        }
                    }

                    // copy source message into destination message format
                    dMsg = cloneForwardMessageFromSPP(destination, srcMessage, new URI(reverse));
                    System.out.println("Injecting message...");
                    destination.sendMessage(dMsg);
                } else {
                    Long virtualAPID = null;

                    synchronized (MUTEX) {
                        virtualAPID = virtualAPIDsMap.get(uriFrom);

                        if (virtualAPID == null) {
                            virtualAPID = uniqueAPID.getAndIncrement();
                            virtualAPIDsMap.put(uriFrom, virtualAPID);
                            reverseMap.put(virtualAPID, uriFrom);
                            MALEndpoint ep = transportA.createEndpoint(uriFrom, null);
                        }

                        System.out.println("The virtualAPID is: " + virtualAPID);
                    }

                    // copy source message into destination message format
                    MALEndpoint ep = transportA.createEndpoint(uriFrom, null);
//                            MALEndpoint ep = transportA.createEndpoint(uriFrom, null);
                    ep.startMessageDelivery();
                    dMsg = cloneForwardMessageToSPP(ep, srcMessage, virtualAPID); 
                    System.out.println("Injecting message...");
                    ep.sendMessage(dMsg);
                }

            } catch (MALException ex) {
                Logger.getLogger(ProtocolBridgeSPP.class.getName()).log(Level.SEVERE, null, ex);
                // ToDo need to bounce this back to source... maybe
            } catch (MALTransmitErrorException ex) {
                Logger.getLogger(ProtocolBridgeSPP.class.getName()).log(Level.SEVERE, null, ex);
                // ToDo need to bounce this back to source... maybe
            }
        }

        @Override
        public void onMessages(MALEndpoint callingEndpoint, MALMessage[] srcMessageList) {
            try {
                MALMessage[] dMsgList = new MALMessage[srcMessageList.length];
                for (int i = 0; i < srcMessageList.length; i++) {
                    dMsgList[i] = cloneForwardMessage(destination, srcMessageList[i]);
                }

                destination.sendMessages(dMsgList);
            } catch (MALException ex) {
                // ToDo need to bounce this back to source
            }
        }

        private boolean isSPP(String value) {
            return value.startsWith("malspp");
        }

        private Long getVirtualAPID(String value) {
            final String[] aaa = value.split("/");
            return Long.parseLong(aaa[2]);
        }
    }

    protected static MALMessage cloneForwardMessageToSPP(final MALEndpoint destination,
            final MALMessage srcMessage, final Long virtualAPID) throws MALException {
        MALMessageHeader sourceHdr = srcMessage.getHeader();
        MALMessageBody body = srcMessage.getBody();
        MALEncodedBody encBody = body.getEncodedBody();
        int size = body.getElementCount();
        System.out.println("Body size: " + size);
        System.out.println("Local URI: " + destination.getURI());
        
        Object[] objList = new Object[size];
        
        for(int i = 0; i < body.getElementCount(); i++){
            objList[i] = body.getBodyElement(i, null);
        }
        
        System.out.println("cloneForwardMessage from : " + sourceHdr.getURIFrom() + "                to  :    " + sourceHdr.getURITo());
        String endpointUriPart = sourceHdr.getURITo().getValue();
        final int iSecond = endpointUriPart.indexOf("@");
        endpointUriPart = endpointUriPart.substring(iSecond + 1, endpointUriPart.length());
        URI to = new URI(endpointUriPart);
//        URI from = new URI(destination.getURI().getValue() + "@" + sourceHdr.getURIFrom().getValue());
//        URI from = new URI("malspp:247/" + virtualAPID + "/0");
        URI from = new URI("malspp:247/2/" + virtualAPID);

        System.out.println("cloneForwardMessage      : " + from + "                to  :    " + to);

        MALMessage destMessage = destination.createMessage(
                sourceHdr.getAuthenticationId(),
                to,
                sourceHdr.getTimestamp(),
                sourceHdr.getQoSlevel(),
                sourceHdr.getPriority(),
                sourceHdr.getDomain(),
                sourceHdr.getNetworkZone(),
                sourceHdr.getSession(),
                sourceHdr.getSessionName(),
                sourceHdr.getInteractionType(),
                sourceHdr.getInteractionStage(),
                sourceHdr.getTransactionId(),
                sourceHdr.getServiceArea(),
                sourceHdr.getService(),
                sourceHdr.getOperation(),
                sourceHdr.getAreaVersion(),
                sourceHdr.getIsErrorMessage(),
                srcMessage.getQoSProperties(),
                objList);

        destMessage.getHeader().setURIFrom(from);

        return destMessage;
    }

    protected static MALMessage cloneForwardMessageFromSPP(final MALEndpoint destination,
            final MALMessage srcMessage, final URI reverse) throws MALException {
        MALMessageHeader sourceHdr = srcMessage.getHeader();
        MALMessageBody body = srcMessage.getBody();
        MALEncodedBody encBody = body.getEncodedBody();
        int size = body.getElementCount();
        System.out.println("Body size: " + size);
        System.out.println("Local URI: " + destination.getURI());
        
        Object[] objList = new Object[size];
        
        for(int i = 0; i < body.getElementCount(); i++){
            objList[i] = body.getBodyElement(i, null);
        }
        
        
        // We need to decode the body and encode it back again....
//        MALEncodedBody oldEncodedBody = body.getEncodedBody();
//        Blob oldBody = oldEncodedBody.getEncodedBody();
        
        // Convert old body to new body...
//        SPPElementStreamFactory fact = new SPPElementStreamFactory();
//        BinaryDecoder aaa = new BinaryDecoder(oldBody.getValue());
//        Element aaaaa = aaa.decodeElement(oldBody);

//        Blob newBody = oldBody;
//        MALEncodedBody newEncodedBody = new MALEncodedBody(newBody);
        

        System.out.println("cloneForwardMessage from : " + sourceHdr.getURIFrom() + "                to  :    " + sourceHdr.getURITo());
        String endpointUriPart = sourceHdr.getURITo().getValue();
        final int iSecond = endpointUriPart.indexOf("@");
        endpointUriPart = endpointUriPart.substring(iSecond + 1, endpointUriPart.length());
        URI to = reverse;
        URI from = new URI(destination.getURI().getValue() + "@" + sourceHdr.getURIFrom().getValue());

        System.out.println("cloneForwardMessage      : " + from + "                to  :    " + to);

        MALMessage destMessage = destination.createMessage(
                sourceHdr.getAuthenticationId(),
                to,
                sourceHdr.getTimestamp(),
                sourceHdr.getQoSlevel(),
                sourceHdr.getPriority(),
                sourceHdr.getDomain(),
                sourceHdr.getNetworkZone(),
                sourceHdr.getSession(),
                sourceHdr.getSessionName(),
                sourceHdr.getInteractionType(),
                sourceHdr.getInteractionStage(),
                sourceHdr.getTransactionId(),
                sourceHdr.getServiceArea(),
                sourceHdr.getService(),
                sourceHdr.getOperation(),
                sourceHdr.getAreaVersion(),
                sourceHdr.getIsErrorMessage(),
                srcMessage.getQoSProperties(),
                objList);

        destMessage.getHeader().setURIFrom(from);

        return destMessage;
    }

}
