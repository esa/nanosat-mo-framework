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
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.Identifier;
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

    /**
     * Default constructor.
     */
    public ProtocolBridge() {
    }

    /**
     * Initializes the bridge by creating a transport and endpoint for each protocol and
     * cross-linking them so that messages received on one side are forwarded to the other.
     *
     * @param protocolA the first transport protocol (for example {@code maltcp})
     * @param protocolB the second transport protocol (for example {@code malspp})
     * @param properties the transport properties passed to both transports
     * @throws Exception if a transport or endpoint cannot be created or started
     */
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

    /**
     * Creates a MAL transport for the given protocol.
     *
     * @param protocol the transport protocol
     * @param properties the transport properties
     * @return the created transport
     * @throws Exception if the transport cannot be created
     */
    protected static MALTransport createTransport(final String protocol, final Map properties) throws Exception {
        System.out.println("Creating transport " + protocol);
        return MALTransportFactory.newFactory(protocol).createTransport(properties);
    }

    /**
     * Creates a MAL endpoint on the given transport.
     *
     * @param protocol the transport protocol, used for logging
     * @param trans the transport to create the endpoint on
     * @return the created endpoint
     * @throws Exception if the endpoint cannot be created
     */
    protected static MALEndpoint createEndpoint(String protocol, MALTransport trans) throws Exception {
        System.out.println("Creating endpoint for transport " + protocol);
        MALEndpoint ep = trans.createEndpoint("ProtocolBridge", null, null);
        System.out.println("Transport " + protocol + " URI is " + ep.getURI().getValue());

        return ep;
    }

    /**
     * Returns the routing URI of the first protocol's endpoint.
     *
     * @return the URI of the first endpoint
     */
    public URI getRoutingProtocolA() {
        return epA.getURI();
    }

    /**
     * Returns the routing URI of the second protocol's endpoint.
     *
     * @return the URI of the second endpoint
     */
    public URI getRoutingProtocolB() {
        return epB.getURI();
    }

    /**
     * Message listener that forwards every message it receives to a destination endpoint.
     */
    protected static class BridgeMessageHandler implements MALMessageListener {

        private final MALEndpoint destination;

        /**
         * Creates the handler.
         *
         * @param destination the endpoint the received messages are forwarded to
         */
        public BridgeMessageHandler(MALEndpoint destination) {
            this.destination = destination;
        }

        @Override
        public void onInternalError(MALEndpoint callingEndpoint, Throwable err) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onTransmitError(MALEndpoint callingEndpoint,
                MALMessageHeader srcMessageHeader, MOErrorException err, Map qosMap) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onMessage(MALEndpoint callingEndpoint, MALMessage srcMessage) {
            try {
                System.out.println("Received message from: " + srcMessage.getHeader().getFrom().getValue());

                // copy source message into destination message format
                MALMessage dMsg = cloneForwardMessage(destination, srcMessage);
                System.out.println("Injecting message...");
                destination.sendMessage(dMsg);
            } catch (MALException | MALTransmitErrorException ex) {
                Logger.getLogger(ProtocolBridge.class.getName()).log(Level.SEVERE, null, ex);
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
    }

    /**
     * Clones a source message into a new message addressed to the destination endpoint,
     * rewriting the {@code from} and {@code to} fields for forwarding.
     *
     * @param destination the endpoint the message is forwarded to
     * @param srcMessage the source message to clone
     * @return the cloned message ready to be sent to the destination
     * @throws MALException if the message cannot be cloned
     */
    protected static MALMessage cloneForwardMessage(MALEndpoint destination, MALMessage srcMessage)
        throws MALException {
        MALMessageHeader sourceHdr = srcMessage.getHeader();
        MALMessageBody body = srcMessage.getBody();

        System.out.println("cloneForwardMessage from : " + sourceHdr.getFrom()
                + "                to  :    " + sourceHdr.getTo());
        String endpointUriPart = sourceHdr.getTo().getValue();
        final int iSecond = endpointUriPart.indexOf("@");
        endpointUriPart = endpointUriPart.substring(iSecond + 1);
        URI to = new URI(endpointUriPart);
        Identifier from = new Identifier(destination.getURI().getValue() + "@" + sourceHdr.getFrom().getValue());
        System.out.println("cloneForwardMessage      : " + from + "                to  :    " + to);

        throw new MALException("The code below needs to be updated!");
        /*
        MALMessage destMessage = destination.createMessage(
                sourceHdr.getAuthenticationId(),
                to,
                sourceHdr.getTimestamp(),
                sourceHdr.getInteractionType(),
                sourceHdr.getInteractionStage(),
                sourceHdr.getTransactionId(),
                sourceHdr.getServiceArea(),
                sourceHdr.getService(),
                sourceHdr.getOperation(),
                sourceHdr.getServiceVersion(),
                sourceHdr.getIsErrorMessage(),
                sourceHdr.getSupplements(),
                srcMessage.getQoSProperties(),
                body.getEncodedBody());

        destMessage.getHeader().setFrom(from);

        return destMessage;
        */
    }

}
