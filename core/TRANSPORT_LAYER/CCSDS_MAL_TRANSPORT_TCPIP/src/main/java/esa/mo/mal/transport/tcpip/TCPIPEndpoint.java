package esa.mo.mal.transport.tcpip;

import java.util.Map;
import java.util.logging.Level;

import esa.mo.mal.transport.gen.GENEndpoint;
import esa.mo.mal.transport.gen.GENMessageHeader;
import esa.mo.mal.transport.gen.GENTransport;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import static esa.mo.mal.transport.tcpip.TCPIPTransport.RLOGGER;

/**
 * TCPIP Transport binding MAL endpoint implementation. Creates messages and
 * generates the correct message header.
 * 
 * @author Rian van Gijlswijk <r.vangijlswijk@telespazio-vega.de>
 *
 */
public class TCPIPEndpoint extends GENEndpoint {

	public TCPIPEndpoint(GENTransport transport, String localName,
			String routingName, String uri, boolean wrapBodyParts) {
		super(transport, localName, routingName, uri, wrapBodyParts);
	}

	@Override
	public MALMessage createMessage(final Blob authenticationId,
			final URI uriTo, final Time timestamp, final QoSLevel qosLevel,
			final UInteger priority, final IdentifierList domain,
			final Identifier networkZone, final SessionType session,
			final Identifier sessionName,
			final InteractionType interactionType,
			final UOctet interactionStage, final Long transactionId,
			final UShort serviceArea, final UShort service,
			final UShort operation, final UOctet serviceVersion,
			final Boolean isErrorMessage, final Map qosProperties,
			final Object... body) throws MALException {
		RLOGGER.log(Level.FINEST, "TCPIPEndpoint.createMessage() 1 uriFrom: " + getURI() + " uriTo: " + uriTo);

		GENMessageHeader hdr = createMessageHeader(getURI(), authenticationId,
				uriTo, timestamp, qosLevel, priority, domain, networkZone,
				session, sessionName, interactionType,
				interactionStage, transactionId, serviceArea, 
				service, operation, serviceVersion,
				isErrorMessage, qosProperties);
		try {
			return new TCPIPMessage(false, hdr, qosProperties, null, transport.getStreamFactory(), body);
		} catch (MALInteractionException e) {
			throw new MALException("Error creating message", e);
		}

	}
	
	@Override
	public MALMessage createMessage(final Blob authenticationId,
			final URI uriTo, final Time timestamp, final QoSLevel qosLevel,
			final UInteger priority, final IdentifierList domain,
			final Identifier networkZone, final SessionType session,
			final Identifier sessionName,
			final InteractionType interactionType,
			final UOctet interactionStage, final Long transactionId,
			final UShort serviceArea, final UShort service,
			final UShort operation, final UOctet serviceVersion,
			final Boolean isErrorMessage, final Map qosProperties,
			final MALEncodedBody body) throws MALException {
		RLOGGER.log(Level.FINEST, "TCPIPEndpoint.createMessage() 2 uriFrom: " + getURI() + " uriTo: " + uriTo);
				return null;

	}
	
	@Override
	public MALMessage createMessage(final Blob authenticationId,
	          final URI uriTo,
	          final Time timestamp,
	          final QoSLevel qosLevel,
	          final UInteger priority,
	          final IdentifierList domain,
	          final Identifier networkZone,
	          final SessionType session,
	          final Identifier sessionName,
	          final Long transactionId,
	          final Boolean isErrorMessage,
	          final MALOperation op,
	          final UOctet interactionStage,
	          final Map qosProperties,
	          final MALEncodedBody body) throws MALException
	  {
		RLOGGER.log(Level.FINEST, "TCPIPEndpoint.createMessage() 3 uriFrom: " + getURI() + " uriTo: " + uriTo);
		return null;
		
	  }
	
	@Override
	public MALMessage createMessage(final Blob authenticationId,
			final URI uriTo, final Time timestamp, final QoSLevel qosLevel,
			final UInteger priority, final IdentifierList domain,
			final Identifier networkZone, final SessionType session,
			final Identifier sessionName, final Long transactionId,
			final Boolean isErrorMessage, final MALOperation op,
			final UOctet interactionStage, final Map qosProperties,
			final Object... body) throws MALException {
		
		GENMessageHeader hdr = createMessageHeader(getURI(), authenticationId,
				uriTo, timestamp, qosLevel, priority, domain, networkZone,
				session, sessionName, op.getInteractionType(),
				interactionStage, transactionId, op.getService().getArea().getNumber(), 
				op.getService().getNumber(), op.getNumber(), op.getService().getArea().getVersion(),
				isErrorMessage, qosProperties);
		RLOGGER.log(Level.FINEST, "TCPIPEndpoint.createMessage() 4 uriFrom: " + getURI() + " uriTo: " + uriTo);
		try {
			return new TCPIPMessage(false, hdr, qosProperties, op, transport.getStreamFactory(), body);
		} catch (MALInteractionException e) {
			throw new MALException("Error creating message", e);
		}
	}
	  
	/**
	 * Create a message header with all header properties set. The serviceFrom and serviceTo
	 * parameters will be explicitly set in the header, so that they include only the routing
	 * part of their respective urls. The routing part is the part of an URL that comes after
	 * the service delimiter: maltcp://host:port/routingpart
	 */
	public GENMessageHeader createMessageHeader(final URI uriFrom,
			Blob authenticationId, final URI uriTo, final Time timestamp,
			final QoSLevel qosLevel, final UInteger priority,
			final IdentifierList domain, final Identifier networkZone,
			final SessionType session, final Identifier sessionName,
			final InteractionType interactionType,
			final UOctet interactionStage, final Long transactionId,
			final UShort serviceArea, final UShort service,
			final UShort operation, final UOctet serviceVersion,
			final Boolean isErrorMessage, final Map qosProperties) {
		
		String serviceFrom = transport.getRoutingPart(uriFrom.toString());
		String serviceTo = transport.getRoutingPart(uriTo.toString());
		
		return new TCPIPMessageHeader(uriFrom, serviceFrom, authenticationId,
				uriTo, serviceTo, timestamp, qosLevel, priority, domain,
				networkZone, session, sessionName, interactionType,
				interactionStage, transactionId, serviceArea, service,
				operation, serviceVersion, isErrorMessage);

	}

}
