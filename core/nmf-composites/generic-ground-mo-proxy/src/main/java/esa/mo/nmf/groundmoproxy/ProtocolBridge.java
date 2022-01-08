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
package esa.mo.nmf.groundmoproxy;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.ccsds.moims.mo.mal.transport.MALTransport;
import org.ccsds.moims.mo.mal.transport.MALTransportFactory;

/**
 * The Protocol Bridge class.
 */
public class ProtocolBridge {

    private MALTransport transportA;
    private MALTransport transportB;
    private MALEndpoint epA;
    private MALEndpoint epB;

    public void init(final String protocolA, final String protocolB, final Map properties) throws Exception {
        transportA = createTransport(protocolA, properties);
        transportB = createTransport(protocolB, properties);
        epA = createEndpoint(protocolA, transportA);
        epB = createEndpoint(protocolB, transportB);

        System.out.println("Linking transports...");
        epA.setMessageListener(new BridgeMessageHandler(epB));
        epB.setMessageListener(new BridgeMessageHandler(epA));
        System.out.println("Starting message delivery...");
        epA.startMessageDelivery();
        epB.startMessageDelivery();
    }

    protected static MALTransport createTransport(final String protocol, final Map properties) throws Exception {
        System.out.println("Creating transport " + protocol);
        return MALTransportFactory.newFactory(protocol).createTransport(null, properties);
    }

    protected static MALEndpoint createEndpoint(final String protocol, final MALTransport trans) throws Exception {
        System.out.println("Creating endpoint for transport " + protocol);
        final MALEndpoint ep = trans.createEndpoint("ProtocolBridge", null);
        System.out.println("Transport " + protocol + " URI is " + ep.getURI().getValue());

        return ep;
    }

    public URI getRoutingProtocolA() {
        return epA.getURI();
    }

    public URI getRoutingProtocolB() {
        return epB.getURI();
    }

    protected static class BridgeMessageHandler implements MALMessageListener {

        private final MALEndpoint destination;

        public BridgeMessageHandler(final MALEndpoint destination) {
            this.destination = destination;
        }

        @Override
        public void onInternalError(final MALEndpoint callingEndpoint, final Throwable err) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onTransmitError(final MALEndpoint callingEndpoint, final MALMessageHeader srcMessageHeader, final MALStandardError err, final Map qosMap) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onMessage(final MALEndpoint callingEndpoint, final MALMessage srcMessage) {
            try {
                System.out.println("Received message from: " + srcMessage.getHeader().getURIFrom().getValue());

                // copy source message into destination message format
                final MALMessage dMsg = cloneForwardMessage(destination, srcMessage);
                System.out.println("Injecting message...");
                destination.sendMessage(dMsg);
            } catch (final MALException | MALTransmitErrorException ex) {
                Logger.getLogger(ProtocolBridge.class.getName()).log(Level.SEVERE, null, ex);
                // ToDo need to bounce this back to source... maybe
            }
        }

        @Override
        public void onMessages(final MALEndpoint callingEndpoint, final MALMessage[] srcMessageList) {
            try {
                final MALMessage[] dMsgList = new MALMessage[srcMessageList.length];
                for (int i = 0; i < srcMessageList.length; i++) {
                    dMsgList[i] = cloneForwardMessage(destination, srcMessageList[i]);
                }

                destination.sendMessages(dMsgList);
            } catch (final MALException ex) {
                // ToDo need to bounce this back to source
            }
        }
    }

    protected static MALMessage cloneForwardMessage(final MALEndpoint destination, final MALMessage srcMessage) throws MALException {
        final MALMessageHeader sourceHdr = srcMessage.getHeader();
        final MALMessageBody body = srcMessage.getBody();

        System.out.println("cloneForwardMessage from : " + sourceHdr.getURIFrom() + "                to  :    " + sourceHdr.getURITo());
        String endpointUriPart = sourceHdr.getURITo().getValue();
        final int iSecond = endpointUriPart.indexOf("@");
        endpointUriPart = endpointUriPart.substring(iSecond + 1);
        final URI to = new URI(endpointUriPart);
        final URI from = new URI(destination.getURI().getValue() + "@" + sourceHdr.getURIFrom().getValue());
        System.out.println("cloneForwardMessage      : " + from + "                to  :    " + to);

        final MALMessage destMessage = destination.createMessage(
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
                body.getEncodedBody());

        destMessage.getHeader().setURIFrom(from);

        return destMessage;
    }

}
