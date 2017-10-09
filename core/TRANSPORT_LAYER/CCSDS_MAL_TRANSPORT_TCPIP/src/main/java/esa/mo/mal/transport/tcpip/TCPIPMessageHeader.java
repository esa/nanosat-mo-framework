/* ----------------------------------------------------------------------------
 * Copyright (C) 2014      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO TCP/IP Transport Framework
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
package esa.mo.mal.transport.tcpip;

import java.util.logging.Level;

import org.ccsds.moims.mo.mal.MALDecoder;
import org.ccsds.moims.mo.mal.MALEncoder;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInvokeOperation;
import org.ccsds.moims.mo.mal.MALProgressOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALRequestOperation;
import org.ccsds.moims.mo.mal.MALSubmitOperation;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;

import esa.mo.mal.transport.gen.GENMessageHeader;

import static esa.mo.mal.transport.tcpip.TCPIPTransport.RLOGGER;

public class TCPIPMessageHeader extends GENMessageHeader {

    private static final long serialVersionUID = 1L;

    public int versionNumber = 1;

    private short encodingId = 0;

    private int bodyLength;

    private byte[] remainingEncodedData;

    private String serviceFrom;

    private String serviceTo;

    public int decodedHeaderBytes = 0;

    public TCPIPMessageHeader() {
    }

    public TCPIPMessageHeader(URI uriFrom, URI uriTo) {
        this.URIFrom = uriFrom;
        this.URITo = uriTo;
    }

    public TCPIPMessageHeader(URI uriFrom, String serviceFrom,
            Blob authenticationId, URI uriTo, String serviceTo, Time timestamp,
            QoSLevel qosLevel, UInteger priority, IdentifierList domain,
            Identifier networkZone, SessionType session,
            Identifier sessionName, InteractionType interactionType,
            UOctet interactionStage, Long transactionId, UShort serviceArea,
            UShort service, UShort operation, UOctet serviceVersion,
            Boolean isErrorMessage) {
        super(uriFrom, authenticationId, uriTo, timestamp, qosLevel, priority,
                domain, networkZone, session, sessionName, interactionType, interactionStage,
                transactionId, serviceArea, service, operation, serviceVersion, isErrorMessage);
        this.serviceFrom = serviceFrom;
        this.serviceTo = serviceTo;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public String getServiceFrom() {
        return serviceFrom;
    }

    public void setServiceFrom(String serviceFrom) {
        this.serviceFrom = serviceFrom;
    }

    public String getServiceTo() {
        return serviceTo;
    }

    public void setServiceTo(String serviceTo) {
        this.serviceTo = serviceTo;
    }

    public byte[] getRemainingEncodedData() {
        return remainingEncodedData;
    }

    public void setRemainingEncodedData(byte[] remainingEncodedData) {
        this.remainingEncodedData = remainingEncodedData;
    }

    public short getEncodingId() {
        return encodingId;
    }

    public void setEncodingId(short encodingId) {
        this.encodingId = encodingId;
    }

    @Override
    public Element decode(final MALDecoder decoder) throws MALException {
        return this;
    }

    @Override
    public void encode(final MALEncoder encoder) throws MALException {
    }

    public short getSDUType() {
        final short stage = (InteractionType._SEND_INDEX == interactionType.getOrdinal()) ? 0 : interactionStage.getValue();

        switch (interactionType.getOrdinal()) {
            case InteractionType._SEND_INDEX:
                return 0;
            case InteractionType._SUBMIT_INDEX:
                if (MALSubmitOperation._SUBMIT_STAGE == stage) {
                    return 1;
                }
                return 2;
            case InteractionType._REQUEST_INDEX:
                if (MALRequestOperation._REQUEST_STAGE == stage) {
                    return 3;
                }
                return 4;
            case InteractionType._INVOKE_INDEX:
                if (MALInvokeOperation._INVOKE_STAGE == stage) {
                    return 5;
                } else if (MALInvokeOperation._INVOKE_ACK_STAGE == stage) {
                    return 6;
                }
                return 7;
            case InteractionType._PROGRESS_INDEX: {
                if (MALProgressOperation._PROGRESS_STAGE == stage) {
                    return 8;
                }
                if (MALProgressOperation._PROGRESS_ACK_STAGE == stage) {
                    return 9;
                } else if (MALProgressOperation._PROGRESS_UPDATE_STAGE == stage) {
                    return 10;
                }
                return 11;
            }
            case InteractionType._PUBSUB_INDEX: {
                switch (stage) {
                    case MALPubSubOperation._REGISTER_STAGE:
                        return 12;
                    case MALPubSubOperation._REGISTER_ACK_STAGE:
                        return 13;
                    case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
                        return 14;
                    case MALPubSubOperation._PUBLISH_REGISTER_ACK_STAGE:
                        return 15;
                    case MALPubSubOperation._PUBLISH_STAGE:
                        return 16;
                    case MALPubSubOperation._NOTIFY_STAGE:
                        return 17;
                    case MALPubSubOperation._DEREGISTER_STAGE:
                        return 18;
                    case MALPubSubOperation._DEREGISTER_ACK_STAGE:
                        return 19;
                    case MALPubSubOperation._PUBLISH_DEREGISTER_STAGE:
                        return 20;
                    case MALPubSubOperation._PUBLISH_DEREGISTER_ACK_STAGE:
                        return 21;
                }
            }
        }

        return 0;
    }

    protected InteractionType getInteractionType(short sduType) {

        switch (sduType) {
            case 0:
                return InteractionType.SEND;
            case 1:
            case 2:
                return InteractionType.SUBMIT;
            case 3:
            case 4:
                return InteractionType.REQUEST;
            case 5:
            case 6:
            case 7:
                return InteractionType.INVOKE;
            case 8:
            case 9:
            case 10:
            case 11:
                return InteractionType.PROGRESS;
        }

        return InteractionType.PUBSUB;
    }

    protected static UOctet getInteractionStage(short sduType) {
        switch (sduType) {
            case 0:
                return new UOctet((short) 0);
            case 1:
                return MALSubmitOperation.SUBMIT_STAGE;
            case 2:
                return MALSubmitOperation.SUBMIT_ACK_STAGE;
            case 3:
                return MALRequestOperation.REQUEST_STAGE;
            case 4:
                return MALRequestOperation.REQUEST_RESPONSE_STAGE;
            case 5:
                return MALInvokeOperation.INVOKE_STAGE;
            case 6:
                return MALInvokeOperation.INVOKE_ACK_STAGE;
            case 7:
                return MALInvokeOperation.INVOKE_RESPONSE_STAGE;
            case 8:
                return MALProgressOperation.PROGRESS_STAGE;
            case 9:
                return MALProgressOperation.PROGRESS_ACK_STAGE;
            case 10:
                return MALProgressOperation.PROGRESS_UPDATE_STAGE;
            case 11:
                return MALProgressOperation.PROGRESS_RESPONSE_STAGE;
            case 12:
                return MALPubSubOperation.REGISTER_STAGE;
            case 13:
                return MALPubSubOperation.REGISTER_ACK_STAGE;
            case 14:
                return MALPubSubOperation.PUBLISH_REGISTER_STAGE;
            case 15:
                return MALPubSubOperation.PUBLISH_REGISTER_ACK_STAGE;
            case 16:
                return MALPubSubOperation.PUBLISH_STAGE;
            case 17:
                return MALPubSubOperation.NOTIFY_STAGE;
            case 18:
                return MALPubSubOperation.DEREGISTER_STAGE;
            case 19:
                return MALPubSubOperation.DEREGISTER_ACK_STAGE;
            case 20:
                return MALPubSubOperation.PUBLISH_DEREGISTER_STAGE;
            case 21:
                return MALPubSubOperation.PUBLISH_DEREGISTER_ACK_STAGE;
        }

        RLOGGER.log(Level.WARNING, "SPPMessageHeader: Unknown sdu value "
                + "received during decoding of {0}", sduType);

        return null;
    }

    public void setInteractionType(short sduType) {
        interactionType = getInteractionType(sduType);
    }

    public void setInteractionStage(short sduType) {
        interactionStage = getInteractionStage(sduType);
    }

    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder("TCPIPMessageHeader{");

        str.append("BodyLength=");
        str.append(bodyLength);
        str.append(", VersionNumber=");
        str.append(versionNumber);
        str.append(", SDUType=");
        str.append(", serviceArea=");
        str.append(serviceArea);
        str.append(", service=");
        str.append(service);
        str.append(", operation=");
        str.append(operation);
        str.append(", areaVersion=");
        str.append(areaVersion);
        str.append(", isErrorMessage=");
        str.append(isErrorMessage);
        str.append(", QoSlevel=");
        str.append(QoSlevel);
        str.append(", session=");
        str.append(session);
        str.append(", transactionId=");
        str.append(transactionId);
        str.append(", priority=");
        str.append(priority);
        str.append(", timestamp=");
        str.append(timestamp);
        str.append(", networkZone=");
        str.append(networkZone);
        str.append(", sessionName=");
        str.append(sessionName);
        str.append(", domain=");
        str.append(domain);
        str.append(", authenticationId=");
        str.append(authenticationId);
        str.append(", URIFrom=");
        str.append(URIFrom);
        str.append(", URITo=");
        str.append(URITo);
        str.append(", interactionType=");
        str.append(interactionType);
        str.append(", interactionStage=");
        str.append(interactionStage);
        str.append('}');

        return str.toString();
    }

}
