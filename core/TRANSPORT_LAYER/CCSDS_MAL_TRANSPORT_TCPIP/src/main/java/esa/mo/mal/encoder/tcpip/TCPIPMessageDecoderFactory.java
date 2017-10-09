/* ----------------------------------------------------------------------------
 * Copyright (C) 2013      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO Split Binary encoder
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
package esa.mo.mal.encoder.tcpip;

import org.ccsds.moims.mo.mal.MALException;
import esa.mo.mal.transport.gen.GENMessage;
import esa.mo.mal.transport.gen.GENReceptionHandler;
import esa.mo.mal.transport.gen.GENTransport;
import esa.mo.mal.transport.gen.receivers.GENIncomingMessageDecoder;
import esa.mo.mal.transport.gen.receivers.GENIncomingMessageDecoderFactory;
import esa.mo.mal.transport.gen.receivers.GENIncomingMessageHolder;
import esa.mo.mal.transport.tcpip.TCPIPPacketInfoHolder;
import esa.mo.mal.transport.tcpip.TCPIPTransport;

/**
 *
 * @author Rian van Gijlswijk
 * @param <O>
 *
 */
public class TCPIPMessageDecoderFactory<O> implements GENIncomingMessageDecoderFactory<TCPIPPacketInfoHolder, O> {

    @Override
    public GENIncomingMessageDecoder createDecoder(GENTransport transport,
            GENReceptionHandler receptionHandler, TCPIPPacketInfoHolder packetInfo) {
        return new TCPIPMessageDecoder((TCPIPTransport) transport, packetInfo);
    }

    public static final class TCPIPMessageDecoder implements GENIncomingMessageDecoder {

        private final TCPIPTransport transport;
        private TCPIPPacketInfoHolder packetInfo;

        public TCPIPMessageDecoder(TCPIPTransport transport, TCPIPPacketInfoHolder packetInfo) {
            this.transport = transport;
            this.packetInfo = packetInfo;
        }

        @Override
        public GENIncomingMessageHolder decodeAndCreateMessage()
                throws MALException {
            GENTransport.PacketToString smsg = transport.new PacketToString(null);
            GENMessage msg = transport.createMessage(packetInfo);
            packetInfo.setPacketData(null);

            if (msg != null) {
                return new GENIncomingMessageHolder(msg.getHeader().getTransactionId(), msg, smsg);
            }

            return null;
        }
    }

}
