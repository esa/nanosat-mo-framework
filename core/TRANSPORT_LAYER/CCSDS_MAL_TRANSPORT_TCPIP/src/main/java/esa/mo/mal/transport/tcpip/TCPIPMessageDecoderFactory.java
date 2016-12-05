package esa.mo.mal.transport.tcpip;

import java.util.logging.Level;

import org.ccsds.moims.mo.mal.MALException;

import esa.mo.mal.transport.gen.GENMessage;
import esa.mo.mal.transport.gen.GENReceptionHandler;
import esa.mo.mal.transport.gen.GENTransport;
import esa.mo.mal.transport.gen.receivers.GENIncomingMessageDecoder;
import esa.mo.mal.transport.gen.receivers.GENIncomingMessageDecoderFactory;
import esa.mo.mal.transport.gen.receivers.GENIncomingMessageHolder;
import esa.mo.mal.transport.tcpip.TCPIPPacketInfoHolder;
import esa.mo.mal.transport.tcpip.TCPIPTransport;
import static esa.mo.mal.transport.tcpip.TCPIPTransport.RLOGGER; 

/**
 * 
 * @author Rian van Gijlswijk <r.vangijlswijk@telespazio-vega.de>
 * @param <O>
 *
 */
public class TCPIPMessageDecoderFactory<O> implements GENIncomingMessageDecoderFactory<TCPIPPacketInfoHolder, O>{

	@Override
	public GENIncomingMessageDecoder createDecoder(GENTransport transport,
			GENReceptionHandler receptionHandler, TCPIPPacketInfoHolder packetInfo) {			
		return new TCPIPMessageDecoder((TCPIPTransport) transport, packetInfo);
	}
	
	public static final class TCPIPMessageDecoder implements GENIncomingMessageDecoder {
		
		private final TCPIPTransport transport;
		private final TCPIPPacketInfoHolder packetInfo;
		
		public TCPIPMessageDecoder(TCPIPTransport transport, TCPIPPacketInfoHolder packetInfo) {
			this.transport = transport;
			this.packetInfo = packetInfo;
		}

		@Override
		public GENIncomingMessageHolder decodeAndCreateMessage()
				throws MALException {
			
			RLOGGER.log(Level.FINEST, "TCPIPMessageDecoder.decodeAndCreateMessage()");

			GENTransport.PacketToString smsg = transport.new PacketToString(null);
			GENMessage msg = transport.createMessage(packetInfo);
			
			if (msg != null) {
				return new GENIncomingMessageHolder(msg.getHeader().getTransactionId(), msg, smsg);
			}
			
			return null;
		}
		
	}

}
