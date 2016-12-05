package esa.mo.mal.encoder.tcpip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALListEncoder;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;

import esa.mo.mal.encoder.binary.fixed.FixedBinaryEncoder;
import static esa.mo.mal.transport.tcpip.TCPIPTransport.RLOGGER;


/**
 * TCPIP Message header encoder
 * 
 * @author Rian van Gijlswijk <r.vangijlswijk@telespazio-vega.de>
 *
 */
public class TCPIPFixedBinaryEncoder extends FixedBinaryEncoder {

	public TCPIPFixedBinaryEncoder(final OutputStream os) {
		super(new TCPIPStreamHolder(os));
	}

	@Override
	public MALListEncoder createListEncoder(List list)
			throws IllegalArgumentException, MALException {

		// encode number of elements
		encodeUInteger(new UInteger(list.size()));

		return this;
	}

	/**
	 * A MAL string is encoded as follows: - String Length: UInteger -
	 * Character: UTF-8, variable size, multiple of octet The field 'string
	 * length' shall be assigned with the number of octets required to encode
	 * the character of the string
	 * 
	 * @param val
	 *            The string to encode
	 * @throws MALException
	 *             if the string to encode is too large
	 */
	@Override
	public void encodeString(String val) throws MALException {

		long MAX_STRING_LENGTH = 2 * (long) Integer.MAX_VALUE + 1;
		byte[] output = val.getBytes(UTF8_CHARSET);

		if (output.length > MAX_STRING_LENGTH) {
			throw new MALException(
					"The string length is greater than 2^32 -1 bytes! Please provide a shorter string.");
		}

		encodeUInteger(new UInteger(output.length));
		try {
			outputStream.directAdd(output);
		} catch (IOException ex) {
			throw new MALException(ENCODING_EXCEPTION_STR, ex);
		}
	}

	/**
	 * Encode a long
	 * 
	 * @param val
	 * @throws MALException
	 */
	public void encodeMALLong(Long val) throws MALException {

		try {
			outputStream.addSignedLong(val);
		} catch (IOException ex) {
			throw new MALException(ENCODING_EXCEPTION_STR, ex);
		}
	}

	/**
	 * Encode an unsigned integer using split-binary encoding for a 4-byte
	 * variable sized int
	 */
	@Override
	public void encodeUInteger(final UInteger value) throws MALException {

		try {
			((TCPIPStreamHolder)outputStream).addUnsignedVarint4((int) value.getValue());
		} catch (IOException ex) {
			throw new MALException(ENCODING_EXCEPTION_STR, ex);
		}
	}

	/**
	 * Encode a nullable identifier
	 */
	@Override
	public void encodeNullableIdentifier(final Identifier value)
			throws MALException {

		if (value != null) {
			// encode presence flag
			encodeBoolean(true);
			// encode element as String
			encodeIdentifier(value);
		} else {
			// encode presence flag
			encodeBoolean(false);
		}
	}

	/**
	 * Encode an identifier
	 */
	@Override
	public void encodeIdentifier(final Identifier value) throws MALException {

		encodeString(value.getValue());
	}

	/**
	 * Encode a blob
	 */
	@Override
	public void encodeBlob(final Blob value) throws MALException {

		byte[] byteValue = value.getValue();

		encodeUInteger(new UInteger(byteValue.length));

		if (value.getLength() > 0) {
			try {
				outputStream.directAdd(byteValue);
			} catch (IOException ex) {
				throw new MALException(ENCODING_EXCEPTION_STR, ex);
			}
		}
	}

	public static class TCPIPStreamHolder extends FixedStreamHolder {

		public TCPIPStreamHolder(OutputStream outputStream) {
			super(outputStream);
		}

		/**
		 * Encode a varint using a split binary encoding algorithm
		 *
		 * @param value
		 * @throws IOException
		 */
		public void addUnsignedVarint4(int value) throws IOException {

			while ((value & 0xFFFFFF80) != 0L) {
				outputStream.write((value & 0x7F) | 0x80);
				value >>>= 7;
			}
			outputStream.write(value & 0x7F);
		}
	}

}
