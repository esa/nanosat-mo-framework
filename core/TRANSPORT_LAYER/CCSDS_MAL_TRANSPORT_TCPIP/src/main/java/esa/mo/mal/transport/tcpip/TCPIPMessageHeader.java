/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
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

import esa.mo.mal.transport.gen.GENMessageHeader;
import esa.mo.mal.transport.gen.GENTransport;
import org.ccsds.moims.mo.mal.MALDecoder;
import org.ccsds.moims.mo.mal.MALEncoder;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInvokeOperation;
import org.ccsds.moims.mo.mal.MALProgressOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALRequestOperation;
import org.ccsds.moims.mo.mal.MALSubmitOperation;
import org.ccsds.moims.mo.mal.structures.*;

/**
 * An implementation of the message header interface for TCP/IP.
 */
public class TCPIPMessageHeader extends GENMessageHeader
{
  protected String fromUriBase;
  protected String toUriBase;

  protected short versionNumber; // actually 3-bits binary value.
  protected UOctet encodingId;

  protected Boolean sourceIdFlag;
  protected String sourceId;
  protected Boolean destinationIdFlag;
  protected String destinationId;

  // TODO where to pass and how to set ??
  protected Boolean priorityFlag = true;
  protected Boolean timestampFlag = true;
  protected Boolean networkZoneFlag = true;
  protected Boolean sessionNameFlag = true;
  protected Boolean domainFlag = true;
  protected Boolean authenticationIdFlag = true;

  /**
   * Constructor.
   */
  public TCPIPMessageHeader()
  {
  }

  /**
   * Constructor.
   *
   * @param fromUriBase Base address of the message source
   * @param toUriBase Base address of the message destination
   */
  public TCPIPMessageHeader(final String fromUriBase, final String toUriBase)
  {
    this.fromUriBase = fromUriBase;
    this.toUriBase = toUriBase;
  }

  /**
   * Constructor.
   *
   * @param transport The containing transport
   * @param uriFrom URI of the message source
   * @param authenticationId Authentication identifier of the message
   * @param uriTo URI of the message destination
   * @param timestamp Timestamp of the message
   * @param qosLevel QoS level of the message
   * @param priority Priority of the message
   * @param domain Domain of the service provider
   * @param networkZone Network zone of the service provider
   * @param session Session of the service provider
   * @param sessionName Session name of the service provider
   * @param interactionType Interaction type of the operation
   * @param interactionStage Interaction stage of the interaction
   * @param transactionId Transaction identifier of the interaction, may be null.
   * @param serviceArea Area number of the service
   * @param service Service number
   * @param operation Operation number
   * @param serviceVersion Service version number
   * @param isErrorMessage Flag indicating if the message conveys an error
   */
  public TCPIPMessageHeader(GENTransport transport,
          final URI uriFrom,
          final Blob authenticationId,
          final URI uriTo,
          final Time timestamp,
          final QoSLevel qosLevel,
          final UInteger priority,
          final IdentifierList domain,
          final Identifier networkZone,
          final SessionType session,
          final Identifier sessionName,
          final InteractionType interactionType,
          final UOctet interactionStage,
          final Long transactionId,
          final UShort serviceArea,
          final UShort service,
          final UShort operation,
          final UOctet serviceVersion,
          final Boolean isErrorMessage)
  {
    super(uriFrom, authenticationId, uriTo, timestamp, qosLevel, priority, domain, networkZone, session, sessionName, interactionType, interactionStage, transactionId, serviceArea, service, operation, serviceVersion, isErrorMessage);

    this.versionNumber = 1; // according to 3.4.2 in recommended standard.
    this.encodingId = new UOctet((short) 0); // not further defined as of 3.4.3 in recommended standard.

    sourceId = transport.getRoutingPart(uriFrom.getValue());
    sourceIdFlag = sourceId != null;
    destinationId = transport.getRoutingPart(uriTo.getValue());
    destinationIdFlag = destinationId != null;
  }

  @Override
  public Element createElement()
  {
    return new TCPIPMessageHeader();
  }

  @Override
  public void encode(final MALEncoder encoder) throws MALException
  {
    short sduType = getSDUType(interactionType, interactionStage); // actually 5-bits binary value.
    encoder.encodeUOctet(new UOctet((short) ((versionNumber << 5) | sduType)));
    encoder.encodeUShort(serviceArea);
    encoder.encodeUShort(service);
    encoder.encodeUShort(operation);
    encoder.encodeUOctet(areaVersion);
    short isErrorMessageShort = (short) (isErrorMessage ? 1 : 0);
    encoder.encodeUOctet(new UOctet((short) ((isErrorMessageShort << 7) | (QoSlevel.getOrdinal() << 4) | session.getOrdinal())));
    encoder.encodeLong(transactionId);
    short sourceIdFlagShort = (short) (sourceIdFlag ? 1 : 0);
    short destinationIdFlagShort = (short) (destinationIdFlag ? 1 : 0);
    short priorityFlagShort = (short) (priorityFlag ? 1 : 0);
    short timestampFlagShort = (short) (timestampFlag ? 1 : 0);
    short networkZoneFlagShort = (short) (networkZoneFlag ? 1 : 0);
    short sessionNameFlagShort = (short) (sessionNameFlag ? 1 : 0);
    short domainFlagShort = (short) (domainFlag ? 1 : 0);
    short authenticationIdFlagShort = (short) (authenticationIdFlag ? 1 : 0);
    encoder.encodeUOctet(new UOctet((short) ((sourceIdFlagShort << 7) | (destinationIdFlagShort << 6) | (priorityFlagShort << 5) | (timestampFlagShort << 4) | (networkZoneFlagShort << 3) | (sessionNameFlagShort << 2) | (domainFlagShort << 1) | (authenticationIdFlagShort))));
    encoder.encodeUOctet(encodingId);
    if (sourceIdFlag)
    {
      encoder.encodeString(sourceId);
    }
    if (destinationIdFlag)
    {
      encoder.encodeString(destinationId);
    }
    if (priorityFlag)
    {
      encoder.encodeUInteger(priority);
    }
    if (timestampFlag)
    {
      encoder.encodeTime(timestamp);
    }
    if (networkZoneFlag)
    {
      encoder.encodeIdentifier(networkZone);
    }
    if (sessionNameFlag)
    {
      encoder.encodeIdentifier(sessionName);
    }
    if (domainFlag)
    {
      encoder.encodeElement(domain);
    }
    if (authenticationIdFlag)
    {
      encoder.encodeBlob(authenticationId);
    }
  }

  @Override
  public Element decode(final MALDecoder decoder) throws MALException
  {
    short versionTypeAndSduType = decoder.decodeUOctet().getValue();
    versionNumber = (short) ((0xE0 & versionTypeAndSduType) >>> 5);
    short sduType = (short) (0x1F & versionTypeAndSduType);
    interactionType = getInteractionType(sduType);
    interactionStage = getInteractionStage(sduType);
    serviceArea = decoder.decodeUShort();
    service = decoder.decodeUShort();
    operation = decoder.decodeUShort();
    areaVersion = decoder.decodeUOctet();
    short isErrorMessageAndQoSLevelAndSession = decoder.decodeUOctet().getValue();
    isErrorMessage = ((isErrorMessageAndQoSLevelAndSession >>> 7) & 1) != 0;
    QoSlevel = QoSLevel.fromOrdinal((0x70 & isErrorMessageAndQoSLevelAndSession) >>> 4);
    session = SessionType.fromOrdinal(0x0F & isErrorMessageAndQoSLevelAndSession);
    transactionId = decoder.decodeLong();
    short flags = decoder.decodeUOctet().getValue();
    sourceIdFlag = ((flags >>> 7) & 1) != 0;
    destinationIdFlag = (((0x40 & flags) >>> 6) & 1) != 0;
    priorityFlag = (((0x20 & flags) >>> 5) & 1) != 0;
    timestampFlag = (((0x10 & flags) >>> 4) & 1) != 0;
    networkZoneFlag = (((0x08 & flags) >>> 3) & 1) != 0;
    sessionNameFlag = (((0x04 & flags) >>> 2) & 1) != 0;
    domainFlag = (((0x02 & flags) >>> 1) & 1) != 0;
    authenticationIdFlag = (flags & 1) != 0;
    encodingId = decoder.decodeUOctet();
    if (sourceIdFlag)
    {
      sourceId = decoder.decodeString();
    }
    if (destinationIdFlag)
    {
      destinationId = decoder.decodeString();
    }
    if (priorityFlag)
    {
      priority = decoder.decodeUInteger();
    }
    if (timestampFlag)
    {
      timestamp = decoder.decodeTime();
    }
    if (networkZoneFlag)
    {
      networkZone = decoder.decodeIdentifier();
    }
    if (sessionNameFlag)
    {
      sessionName = decoder.decodeIdentifier();
    }
    if (domainFlag)
    {
      domain = (IdentifierList) decoder.decodeElement(new IdentifierList());
    }
    if (authenticationIdFlag)
    {
      authenticationId = decoder.decodeBlob();
    }

    URIFrom = recomposeURI(fromUriBase, sourceId);
    URITo = recomposeURI(toUriBase, destinationId);

    return this;
  }

  /**
   * Reconstructs the full URI from a given IP address, port number and optional source or destination id
   *
   * @param uri the IP address of the URI to recompose
   * @param srcDestId the source or destination id if present or null otherwise
   * @return the reconstructed URI
   */
  protected static URI recomposeURI(String uri, String srcDestId)
  {
    if (srcDestId != null)
    {
      uri += srcDestId;
    }
    return new URI(uri);
  }

  /**
   * Mapping MAL header fields 'Interaction Type' and 'Interaction Stage' to MAL TCP/IP PDU header field 'SDU Type'
   * according to 3.3.12 in recommended standard.
   *
   * @param interactionType Interaction type of the operation
   * @param interactionStage Interaction stage of the interaction
   * @return SDU Type decimal code
   */
  protected static short getSDUType(InteractionType interactionType, UOctet interactionStage)
  {
    final short stage = (InteractionType._SEND_INDEX == interactionType.getOrdinal()) ? 0 : interactionStage.getValue();

    switch (interactionType.getOrdinal())
    {
      case InteractionType._SEND_INDEX:
        return 0;
      case InteractionType._SUBMIT_INDEX:
        if (MALSubmitOperation._SUBMIT_STAGE == stage)
        {
          return 1;
        }
        return 2;
      case InteractionType._REQUEST_INDEX:
        if (MALRequestOperation._REQUEST_STAGE == stage)
        {
          return 3;
        }
        return 4;
      case InteractionType._INVOKE_INDEX:
        if (MALInvokeOperation._INVOKE_STAGE == stage)
        {
          return 5;
        }
        else if (MALInvokeOperation._INVOKE_ACK_STAGE == stage)
        {
          return 6;
        }
        return 7;
      case InteractionType._PROGRESS_INDEX:
      {
        if (MALProgressOperation._PROGRESS_STAGE == stage)
        {
          return 8;
        }
        if (MALProgressOperation._PROGRESS_ACK_STAGE == stage)
        {
          return 9;
        }
        else if (MALProgressOperation._PROGRESS_UPDATE_STAGE == stage)
        {
          return 10;
        }
        return 11;
      }
      case InteractionType._PUBSUB_INDEX:
      {
        switch (stage)
        {
          case MALPubSubOperation._REGISTER_STAGE:
            return 12;
          case MALPubSubOperation._DEREGISTER_STAGE:
            return 13;
          case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
            return 14;
          case MALPubSubOperation._PUBLISH_DEREGISTER_STAGE:
            return 15;
          case MALPubSubOperation._REGISTER_ACK_STAGE:
            return 16;
          case MALPubSubOperation._DEREGISTER_ACK_STAGE:
            return 17;
          case MALPubSubOperation._PUBLISH_REGISTER_ACK_STAGE:
            return 18;
          case MALPubSubOperation._PUBLISH_DEREGISTER_ACK_STAGE:
            return 19;
          case MALPubSubOperation._PUBLISH_STAGE:
            return 20;
          case MALPubSubOperation._NOTIFY_STAGE:
            return 21;
        }
      }
    }

    return 0;
  }

  /**
   * Resolving MAL TCP/IP PDU header field 'SDU Type' to MAL header field 'Interaction Type' according to 3.3.12 in
   * recommended standard.
   *
   * @param sduType SDU Type decimal code
   * @return Interaction type of the operation
   */
  protected static InteractionType getInteractionType(short sduType)
  {
    switch (sduType)
    {
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
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
        return InteractionType.PUBSUB;
    }

    return null;
  }

  /**
   * Resolving MAL TCP/IP PDU header field 'SDU Type' to MAL header field 'Interaction Stage' according to 3.3.12 in
   * recommended standard.
   *
   * @param sduType SDU Type decimal code
   * @return Interaction stage of the interaction
   */
  protected static UOctet getInteractionStage(short sduType)
  {
    switch (sduType)
    {
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
        return MALPubSubOperation.DEREGISTER_STAGE;
      case 14:
        return MALPubSubOperation.PUBLISH_REGISTER_STAGE;
      case 15:
        return MALPubSubOperation.PUBLISH_DEREGISTER_STAGE;
      case 16:
        return MALPubSubOperation.REGISTER_ACK_STAGE;
      case 17:
        return MALPubSubOperation.DEREGISTER_ACK_STAGE;
      case 18:
        return MALPubSubOperation.PUBLISH_REGISTER_ACK_STAGE;
      case 19:
        return MALPubSubOperation.PUBLISH_DEREGISTER_ACK_STAGE;
      case 20:
        return MALPubSubOperation.PUBLISH_STAGE;
      case 21:
        return MALPubSubOperation.NOTIFY_STAGE;
    }

    return null;
  }

  @Override
  public String toString()
  {
    short sduType = getSDUType(interactionType, interactionStage);

    final StringBuilder str = new StringBuilder("TCPIPMessageHeader{");
    str.append("versionNumber=");
    str.append(versionNumber);
    str.append(", sduType=");
    str.append(sduType);
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
    str.append(", sourceIdFlag=");
    str.append(sourceIdFlag);
    str.append(", destinationIdFlag=");
    str.append(destinationIdFlag);
    str.append(", priorityFlag=");
    str.append(priorityFlag);
    str.append(", timestampFlag=");
    str.append(timestampFlag);
    str.append(", networkZoneFlag=");
    str.append(networkZoneFlag);
    str.append(", sessionNameFlag=");
    str.append(sessionNameFlag);
    str.append(", domainFlag=");
    str.append(domainFlag);
    str.append(", authenticationIdFlag=");
    str.append(authenticationIdFlag);
    str.append(", encodingId=");
    str.append(encodingId);
    str.append(", sourceId=");
    str.append(sourceId);
    str.append(", destinationId=");
    str.append(destinationId);
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
    str.append('}');

    return str.toString();
  }
}
