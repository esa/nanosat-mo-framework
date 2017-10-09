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

import java.util.logging.Level;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;

import esa.mo.mal.encoder.binary.BinaryElementInputStream;
import esa.mo.mal.transport.tcpip.TCPIPMessageHeader;
import static esa.mo.mal.transport.tcpip.TCPIPTransport.RLOGGER;
import org.ccsds.moims.mo.mal.structures.Blob;

/**
 * Manage the decoding of an incoming TCPIP Message. Separate decoders are used
 * for the message header and body. The header uses a custom implementation
 * according to MAL TCPIP Transport Binding specifications, and the body is
 * split binary decoded.
 *
 * @author Rian van Gijlswijk
 *
 */
public class TCPIPFixedBinaryElementInputStream extends BinaryElementInputStream {

    public TCPIPFixedBinaryElementInputStream(final java.io.InputStream is) {
        super(new TCPIPFixedBinaryDecoder(is));
        RLOGGER.log(Level.FINEST, "TCPIPHeaderElementInputStream constructor 1");
    }

    protected TCPIPFixedBinaryElementInputStream(final byte[] src, final int offset) {
        super(new TCPIPFixedBinaryDecoder(src, offset));
        RLOGGER.log(Level.FINEST, "TCPIPHeaderElementInputStream constructor 2");
    }

    /**
     * Read an element
     */
    @Override
    public Object readElement(final Object element, final MALEncodingContext ctx)
            throws IllegalArgumentException, MALException {
        RLOGGER.log(Level.FINEST, "TCPIPHeaderElementInputStream.readElement()");

        if (element == ctx.getHeader()) {
            // header is decoded using custom tcpip decoder
            return decodeHeader(element);
        } else {
            // body is not decoded
            return null;
        }
    }

    /**
     * Decode the header
     *
     * @param element The header to decode
     * @return The decoded header
     * @throws MALException
     */
    private Object decodeHeader(final Object element) throws MALException {
        if (!(element instanceof TCPIPMessageHeader)) {
            throw new MALException("Wrong header element supplied. Must be instance of TCPIPMessageHeader");
        }

        TCPIPMessageHeader header = (TCPIPMessageHeader) element;

        short versionAndSDU = dec.decodeUOctet().getValue();
        header.versionNumber = (versionAndSDU >> 0x5);
        short sduType = (short) (versionAndSDU & 0x1f);
        header.setServiceArea(new UShort(dec.decodeShort()));
        header.setService(new UShort(dec.decodeShort()));
        header.setOperation(new UShort(dec.decodeShort()));
        header.setAreaVersion(dec.decodeUOctet());

        short parts = dec.decodeUOctet().getValue();
        header.setIsErrorMessage((((parts & 0x80) >> 7) == 0x1));
        header.setQoSlevel(QoSLevel.fromOrdinal(((parts & 0x70) >> 4)));
        header.setSession(SessionType.fromOrdinal(parts & 0xF));
        Long transactionId = ((TCPIPFixedBinaryDecoder) dec).decodeMALLong();
        header.setTransactionId(transactionId);
//		RLOGGER.log(Level.FINEST, "QOS DECODING: qos=" + header.getQoSlevel().getOrdinal() + " parts=" + parts);

        short flags = dec.decodeUOctet().getValue(); // flags
        boolean sourceIdFlag = (((flags & 0x80) >> 7) == 0x1);
        boolean destinationIdFlag = (((flags & 0x40) >> 6) == 0x1);
        boolean priorityFlag = (((flags & 0x20) >> 5) == 0x1);
        boolean timestampFlag = (((flags & 0x10) >> 4) == 0x1);
        boolean networkZoneFlag = (((flags & 0x8) >> 3) == 0x1);
        boolean sessionNameFlag = (((flags & 0x4) >> 2) == 0x1);
        boolean domainFlag = (((flags & 0x2) >> 1) == 0x1);
        boolean authenticationIdFlag = ((flags & 0x1) == 0x1);

        header.setEncodingId(dec.decodeUOctet().getValue());
        int bodyLength = (int) dec.decodeInteger();
        header.setBodyLength(bodyLength);

        if (sourceIdFlag) {
            String sourceId = dec.decodeString();
            if (isURI(sourceId)) {
                header.setURIFrom(new URI(sourceId));
            } else {
                String from = header.getURIFrom() + sourceId;
                header.setURIFrom(new URI(from));
            }
        }
        if (destinationIdFlag) {
            String destinationId = dec.decodeString();
            if (isURI(destinationId)) {
                header.setURITo(new URI(destinationId));
            } else {
                String to = header.getURITo() + destinationId;
                header.setURITo(new URI(to));
            }
        }
        if (priorityFlag) {
            header.setPriority(dec.decodeUInteger());
        }
        if (timestampFlag) {
            header.setTimestamp(dec.decodeTime());
        }
        if (networkZoneFlag) {
            header.setNetworkZone(dec.decodeIdentifier());
        }
        if (sessionNameFlag) {
            header.setSessionName(dec.decodeIdentifier());
        }
        if (domainFlag) {
            IdentifierList list = (IdentifierList) new IdentifierList().decode(dec);
            header.setDomain(list);
        }
        if (authenticationIdFlag) {
            header.setAuthenticationId(dec.decodeBlob());
        } else {
            header.setAuthenticationId(new Blob());
        }

        header.setInteractionType(sduType);
        header.setInteractionStage(sduType);

        header.decodedHeaderBytes = ((TCPIPFixedBinaryDecoder) dec).getBufferOffset();

        // debug information
        /*
		RLOGGER.log(Level.FINEST, "Decoded header:");
		RLOGGER.log(Level.FINEST, "---------------------------------------");
		RLOGGER.log(Level.FINEST, element.toString());
		RLOGGER.log(Level.FINEST, "Decoded header bytes:");
		RLOGGER.log(Level.FINEST, header.decodedHeaderBytes + "");
		RLOGGER.log(Level.FINEST, "---------------------------------------");		
         */
        return header;
    }

    /**
     * Is @param a URI?
     *
     * @param uri
     * @return
     */
    private boolean isURI(String uri) {
        return uri.startsWith("maltcp://");
    }
}
