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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.ccsds.moims.mo.mal.transport.MALTransport;

public class ProtocolBridgeSPP extends ProtocolBridge {

    private MALTransport transportA;
    private MALTransport transportB;
    private MALEndpoint epB;
    private final static String PROTOCOL_SPP = "malspp";
    private final VirtualSPPURIsManager virtualSPPURI = new VirtualSPPURIsManager();

    public void init(final String protocol, final Map properties) throws MALException, Exception {
        transportA = createTransport(PROTOCOL_SPP, properties);
        transportB = createTransport(protocol, properties);

        epB = createEndpoint(protocol, transportB);

        System.out.println("Linking transports...");
        epB.setMessageListener(new BridgeMessageHandlerSPP(null, epB));

        System.out.println("Starting message delivery...");
        epB.startMessageDelivery();
    }

    public URI getRoutingProtocol() {
        return epB.getURI();
    }

    protected class BridgeMessageHandlerSPP implements MALMessageListener {

        private final MALEndpoint epSPP;
        private final MALEndpoint epOther;

        public BridgeMessageHandlerSPP(MALEndpoint epSPP, MALEndpoint epOther) {
            this.epSPP = epSPP;
            this.epOther = epOther;
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
                    String uriTrans = virtualSPPURI.getURI(srcMessage.getHeader().getURITo().getValue());

                    // copy source message into destination message format
                    dMsg = cloneForwardMessageFromSPP(epOther, srcMessage, new URI(uriTrans));
                    System.out.println("Injecting message...");
                    epOther.sendMessage(dMsg);
                } else {
                    String virtualURIs = virtualSPPURI.getVirtualSPPURI(uriFrom);

                    // copy source message into destination message format
                    MALEndpoint ep = transportA.getEndpoint(virtualURIs);

                    if (ep == null) {
                        ep = transportA.createEndpoint(virtualURIs, System.getProperties());
                        ep.setMessageListener(new BridgeMessageHandlerSPP(ep, epOther));
                        ep.startMessageDelivery();
                    }

                    dMsg = cloneForwardMessageToSPP(ep, srcMessage, virtualURIs);
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
                    dMsgList[i] = cloneForwardMessage(epSPP, srcMessageList[i]);
                }

                epSPP.sendMessages(dMsgList);
            } catch (MALException ex) {
                // ToDo need to bounce this back to source
            }
        }

        private boolean isSPP(String value) {
            return value.startsWith(PROTOCOL_SPP);
        }

    }

    protected static MALMessage cloneForwardMessageToSPP(final MALEndpoint destination,
            final MALMessage srcMessage, final String virtualURI) throws MALException {
        MALMessageHeader sourceHdr = srcMessage.getHeader();
        MALMessageBody body = srcMessage.getBody();
        MALEncodedBody encBody = body.getEncodedBody();
        int size = body.getElementCount();
        System.out.println("Body size: " + size);
        System.out.println("Local URI: " + destination.getURI());

        Object[] objList = new Object[size];

        for (int i = 0; i < body.getElementCount(); i++) {
            objList[i] = body.getBodyElement(i, null);
        }

        System.out.println("cloneForwardMessage from : " + sourceHdr.getURIFrom() + "                to  :    " + sourceHdr.getURITo());
        String endpointUriPart = sourceHdr.getURITo().getValue();
        final int iSecond = endpointUriPart.indexOf("@");
        endpointUriPart = endpointUriPart.substring(iSecond + 1, endpointUriPart.length());
        URI to = new URI(endpointUriPart);
        URI from = new URI(virtualURI);

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

        for (int i = 0; i < body.getElementCount(); i++) {
            objList[i] = body.getBodyElement(i, null);
        }

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
