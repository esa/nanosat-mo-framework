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
package esa.mo.mal.encoder.tcpip;

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import esa.mo.mal.encoder.gen.GENElementOutputStream;
import esa.mo.mal.encoder.gen.GENEncoder;
import esa.mo.mal.transport.tcpip.TCPIPMessageHeader;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.UOctet;

/**
 * Encode a TCPIP Message
 *
 * @author Rian van Gijlswijk
 *
 */
public class TCPIPFixedBinaryElementOutputStream extends GENElementOutputStream {

    /**
     * Logger
     */
    public static final java.util.logging.Logger RLOGGER = Logger.getLogger("org.ccsds.moims.mo.mal.encoding.tcpip");

    public TCPIPFixedBinaryElementOutputStream(OutputStream os) {
        super(os);
    }

    /**
     * Create a split binary encoder for the body
     */
    @Override
    protected GENEncoder createEncoder(OutputStream os) {
        RLOGGER.log(Level.FINEST, "TCPIPHeaderElementOutputStream.createEncoder()");
        return new TCPIPFixedBinaryEncoder(os);
    }

    /**
     * Encode an element. Only encode the header, the body is encoded
     * automatically by the MAL framework using the body encoder provided by
     * this class.
     */
    @Override
    public void writeElement(final Object element, final MALEncodingContext ctx)
            throws MALException {
        if (enc == null) {
            enc = createEncoder(this.dos);
        }

        if (element == ctx.getHeader()) {
            // header is encoded using tcpip custom encoder
            encodeHeader(element);
        }

        // body is not encoded
    }

    /**
     * Encode the header
     *
     * @param element
     * @throws MALException
     */
    private void encodeHeader(final Object element) throws MALException {
        if (!(element instanceof TCPIPMessageHeader)) {
            throw new MALException("Wrong header element supplied. Must be instance of TCPIPMessageHeader");
        }

        TCPIPMessageHeader header = (TCPIPMessageHeader) element;

        // version number & sdu type
        byte versionAndSDU = (byte) (header.versionNumber << 5 | header.getSDUType());

        UOctet test = new UOctet(versionAndSDU);
        enc.encodeUOctet(test);
        enc.encodeShort((short) header.getServiceArea().getValue());
        enc.encodeShort((short) header.getService().getValue());
        enc.encodeShort((short) header.getOperation().getValue());
        enc.encodeUOctet(header.getAreaVersion());

        short parts = (short) (((header.getIsErrorMessage() ? 0x1 : 0x0) << 7)
                | (header.getQoSlevel().getOrdinal() << 4)
                | header.getSession().getOrdinal());

        // RLOGGER.log(Level.FINEST, "QOS ENCODING: qos=" + header.getQoSlevel().getOrdinal() + " parts=" + parts);
        enc.encodeUOctet(new UOctet(parts));
        ((TCPIPFixedBinaryEncoder) enc).encodeMALLong(header.getTransactionId());

        // set flags
        enc.encodeUOctet(getFlags(header));
        // set encoding id
        enc.encodeUOctet(new UOctet(header.getEncodingId()));

        // preset body length. Allocate four bytes.
        enc.encodeInteger(0);

        // encode rest of header
        if (!header.getServiceFrom().isEmpty()) {
            enc.encodeString(header.getURIFrom().toString());
        }
        if (!header.getServiceTo().isEmpty()) {
            enc.encodeString(header.getURITo().toString());
        }
        if (header.getPriority() != null) {
            enc.encodeUInteger(header.getPriority());
        }
        if (header.getTimestamp() != null) {
            enc.encodeTime(header.getTimestamp());
        }
        if (header.getNetworkZone() != null) {
            enc.encodeIdentifier(header.getNetworkZone());
        }
        if (header.getSessionName() != null) {
            enc.encodeIdentifier(header.getSessionName());
        }
        if (header.getDomain() != null && header.getDomain().size() > 0) {
            header.getDomain().encode(enc);
        }
        if (header.getAuthenticationId() != null && header.getAuthenticationId().getLength() > 0) {
            enc.encodeBlob(header.getAuthenticationId());
        }
    }

    /**
     * Set a byte which flags the optional fields that are set in the header.
     *
     * @param header
     * @return
     */
    private UOctet getFlags(TCPIPMessageHeader header) {

        short result = 0;
        if (!header.getServiceFrom().isEmpty()) {
            result |= (0x1 << 7);
        }
        if (!header.getServiceTo().isEmpty()) {
            result |= (0x1 << 6);
        }
        if (header.getPriority() != null) {
            result |= (0x1 << 5);
        }
        if (header.getTimestamp() != null) {
            result |= (0x1 << 4);
        }
        if (header.getNetworkZone() != null) {
            result |= (0x1 << 3);
        }
        if (header.getSessionName() != null) {
            result |= (0x1 << 2);
        }
        if (header.getDomain() != null && header.getDomain().size() > 0) {
            result |= (0x1 << 1);
        }
        if (header.getAuthenticationId() != null && header.getAuthenticationId().getLength() > 0) {
            result |= 0x1;
        }

        return new UOctet(result);
    }

}
