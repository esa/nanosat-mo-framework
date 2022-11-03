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
import java.util.Properties;
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

/**
 * The Protocol Bridge class for SPP.
 */
public class ProtocolBridgeSPP extends ProtocolBridge {

    private static final Logger LOGGER = Logger.getLogger(ProtocolBridgeSPP.class.getName());
    public static final String PROPERTY_APID_RANGE_START = "esa.mo.nmf.groundmoproxy.protocolbrige.spp.apid.start";
    public static final String PROPERTY_APID_RANGE_END = "esa.mo.nmf.groundmoproxy.protocolbrige.spp.apid.end";
    private static final String PROTOCOL_SPP = "malspp";
    private MALTransport transportA;
    private MALTransport transportB;
    private MALEndpoint epB;
    private VirtualSPPURIsManager virtualSPPURI;

    public void init(final String protocol, final Map properties) throws Exception {
        transportA = createTransport(PROTOCOL_SPP, properties);
        transportB = createTransport(protocol, properties);

        if (System.getProperty(PROPERTY_APID_RANGE_START) == null || System.getProperty(PROPERTY_APID_RANGE_END) == null) {
            throw new MALException("The APID ranges need to be set using the properties: "
                    + PROPERTY_APID_RANGE_START + " and " + PROPERTY_APID_RANGE_END);
        }

        // To do: Get the ranges from the properties file
        final int apidStart = Integer.parseInt(System.getProperty(PROPERTY_APID_RANGE_START));
        final int apidEnd = Integer.parseInt(System.getProperty(PROPERTY_APID_RANGE_END));

        virtualSPPURI = new VirtualSPPURIsManager(apidStart, apidEnd);

        epB = createEndpoint(protocol, transportB);

        LOGGER.log(Level.INFO, "Linking transports...");
        epB.setMessageListener(new BridgeMessageHandlerSPP(null, epB));

        LOGGER.log(Level.INFO, "Starting message delivery...");
        epB.startMessageDelivery();
    }

    public URI getRoutingProtocol() {
        return epB.getURI();
    }

    protected class BridgeMessageHandlerSPP implements MALMessageListener {

        private final MALEndpoint epSPP;
        private final MALEndpoint epOther;

        public BridgeMessageHandlerSPP(final MALEndpoint epSPP, final MALEndpoint epOther) {
            this.epSPP = epSPP;
            this.epOther = epOther;
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
                final String uriFrom = srcMessage.getHeader().getURIFrom().getValue();
                LOGGER.log(Level.FINER, "Received message from: {0}", uriFrom);

                final MALMessage dMsg;

                if (this.isSPP(uriFrom)) {
                    final String uriTrans = virtualSPPURI.getURI(srcMessage.getHeader().getURITo().getValue());

                    // copy source message into destination message format
                    dMsg = cloneForwardMessageFromSPP(epOther, srcMessage, new URI(uriTrans));
                    LOGGER.log(Level.FINE, "Injecting message... Ground<-Space");
                    epOther.sendMessage(dMsg);
                } else {
                    final String virtualURIs = virtualSPPURI.getVirtualSPPURI(uriFrom);

                    // copy source message into destination message format
                    MALEndpoint ep = transportA.getEndpoint(virtualURIs);

                    if (ep == null) {
                        final Properties props = new Properties(System.getProperties());
                        final int apid = VirtualSPPURIsManager.getAPIDFromVirtualSPPURI(virtualURIs);
                        props.put("org.ccsds.moims.mo.malspp.apid", apid);
                        ep = transportA.createEndpoint(virtualURIs, props);
                        ep.setMessageListener(new BridgeMessageHandlerSPP(ep, epOther));
                        ep.startMessageDelivery();
                    }

                    dMsg = cloneForwardMessageToSPP(ep, srcMessage, virtualURIs);
                    LOGGER.log(Level.FINE, "Injecting message... Ground->Space");
                    ep.sendMessage(dMsg);
                }
            } catch (final MALException ex) {
                LOGGER.log(Level.SEVERE,
                        "MALException", ex);
                // To do: needs to bounce this back to source?
            } catch (final MALTransmitErrorException ex) {
                LOGGER.log(Level.SEVERE,
                        "MALTransmitErrorException: Maybe the consumer was disconnected?", ex);
                // To do: needs to bounce this back to source?
            }
        }

        @Override
        public void onMessages(final MALEndpoint callingEndpoint, final MALMessage[] srcMessageList) {
            try {
                final MALMessage[] dMsgList = new MALMessage[srcMessageList.length];
                for (int i = 0; i < srcMessageList.length; i++) {
                    dMsgList[i] = cloneForwardMessage(epSPP, srcMessageList[i]);
                }

                epSPP.sendMessages(dMsgList);
            } catch (final MALException ex) {
                // ToDo need to bounce this back to source
            }
        }

        private boolean isSPP(final String value) {
            return value.startsWith(PROTOCOL_SPP);
        }

    }

    protected static MALMessage cloneForwardMessageToSPP(final MALEndpoint destination,
            final MALMessage srcMessage, final String virtualURI) throws MALException {
        final MALMessageHeader sourceHdr = srcMessage.getHeader();
        final MALMessageBody body = srcMessage.getBody();
        final int size = body.getElementCount();
        LOGGER.log(Level.FINER, "Body size: {0}", size);
        LOGGER.log(Level.FINER, "Local URI: {0}", destination.getURI());

        final Object[] objList = new Object[size];

        for (int i = 0; i < body.getElementCount(); i++) {
            objList[i] = body.getBodyElement(i, null);
        }

        LOGGER.log(Level.FINER, "cloneForwardMessage from: {0} to: {1}", new Object[]{sourceHdr.getURIFrom(),
          sourceHdr.getURITo()});
        String endpointUriPart = sourceHdr.getURITo().getValue();
        final int iSecond = endpointUriPart.indexOf("@");
        endpointUriPart = endpointUriPart.substring(iSecond + 1);
        final URI to = new URI(endpointUriPart);
        final URI from = new URI(virtualURI);

        LOGGER.log(Level.FINER, "cloneForwardMessage from: {0} to: {1}", new Object[]{from,
          to});

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
                objList);

        destMessage.getHeader().setURIFrom(from);

        return destMessage;
    }

    protected static MALMessage cloneForwardMessageFromSPP(final MALEndpoint destination,
            final MALMessage srcMessage, final URI reverse) throws MALException {
        final MALMessageHeader sourceHdr = srcMessage.getHeader();
        final MALMessageBody body = srcMessage.getBody();
        final int size = body.getElementCount();
        LOGGER.log(Level.FINER, "Body size: {0}", size);
        LOGGER.log(Level.FINER, "Local URI: {0}", destination.getURI());

        final Object[] objList = new Object[size];

        for (int i = 0; i < body.getElementCount(); i++) {
            objList[i] = body.getBodyElement(i, null);
        }

        LOGGER.log(Level.FINER, "cloneForwardMessage from : {0} to: {1}", new Object[]{sourceHdr.getURIFrom(),
          sourceHdr.getURITo()});
        final URI from = new URI(destination.getURI().getValue() + "@" + sourceHdr.getURIFrom().getValue());

        LOGGER.log(Level.FINER, "cloneForwardMessage from: {0} to: {1}", new Object[]{from, reverse});

        final MALMessage destMessage = destination.createMessage(
                sourceHdr.getAuthenticationId(),
                reverse,
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
