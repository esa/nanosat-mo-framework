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

import java.math.BigInteger;
import java.util.logging.Level;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;

import static esa.mo.mal.transport.tcpip.TCPIPTransport.RLOGGER;


/**
 * Implements the MALDecoder interface for a split binary encoding.
 */
public class TCPIPSplitBinaryDecoder extends esa.mo.mal.encoder.binary.BinaryDecoder {
	/**
	 * Constructor.
	 *
	 * @param src
	 *            Byte array to read from.
	 */
	public TCPIPSplitBinaryDecoder(final byte[] src) {
		super(new TCPIPSplitBufferHolder(null, src, 0, src.length));
	}

	/**
	 * Constructor.
	 *
	 * @param is
	 *            Input stream to read from.
	 */
	public TCPIPSplitBinaryDecoder(final java.io.InputStream is) {
		super(new TCPIPSplitBufferHolder(is, null, 0, 0));
	}

	/**
	 * Constructor.
	 *
	 * @param src
	 *            Byte array to read from.
	 * @param offset
	 *            index in array to start reading from.
	 */
	public TCPIPSplitBinaryDecoder(final byte[] src, final int offset) {
		super(new TCPIPSplitBufferHolder(null, src, offset, src.length));
	}

	/**
	 * Constructor.
	 *
	 * @param src
	 *            Source buffer holder to use.
	 */
	protected TCPIPSplitBinaryDecoder(final BufferHolder src) {
		super(src);
	}

	@Override
	public org.ccsds.moims.mo.mal.MALListDecoder createListDecoder(
			final java.util.List list) throws MALException {
		return new TCPIPSplitBinaryListDecoder(list, sourceBuffer);
	}

	@Override
	public Boolean decodeBoolean() throws MALException {
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryEncoder.decodeBoolean is called");
		return sourceBuffer.getBool();
	}

	@Override
	public Boolean decodeNullableBoolean() throws MALException {
		
		boolean isNotNull = decodeBoolean();
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryEncoder.decodeNullableBoolean: " + isNotNull);

		Boolean rv = null;

		// decode one element, or add null if presence flag indicates no element
		if (isNotNull) {
			rv = decodeBoolean();
		}
		
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryEncoder.decodeNullableBoolean: " + rv);

		return rv;
	}

	@Override
	public String decodeString() throws MALException {
		
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryEncoder.decodeString is called");

		return sourceBuffer.getString();
	}
	
	@Override
	public String decodeNullableString() throws MALException {
		
		// decode presence flag
		boolean isNotNull = decodeBoolean();
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryEncoder.decodeNullableString: " + isNotNull);
			
		String rv = null;
		
		// decode one element, or add null if presence flag indicates no element
		if (isNotNull) {
			rv = decodeString();
		}
		
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryEncoder.decodeNullableString: " + rv);
	
		return rv;
	}
	
	@Override
	public Blob decodeNullableBlob() throws MALException {
		
		// decode presence flag
		boolean isNotNull = decodeBoolean();
			
		// decode one element, or add null if presence flag indicates no element
		if (isNotNull) {
			return decodeBlob();
		}
	
		return null;
	}
	
	@Override
	public Identifier decodeNullableIdentifier() throws MALException {
		
		// decode presence flag
		boolean isNotNull = decodeBoolean();
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryEncoder.decodeNullableIdentifier: " + isNotNull);
		
		Identifier rv = null;
			
		// decode one element, or add null if presence flag indicates no element
		if (isNotNull) {
			rv = decodeIdentifier();
		}
		
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryEncoder.decodeNullableIdentifier: " + rv);
		
		return rv;
	}
	
	@Override
	public URI decodeNullableURI() throws MALException {

		// decode presence flag
		boolean isNotNull = decodeBoolean();
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryEncoder.decodeNullableURI: " + isNotNull);
			
		// decode one element, or add null if presence flag indicates no element
		if (isNotNull) {
			return decodeURI();
		}
	
		return null;
	}

	@Override
	public ULong decodeNullableULong() throws MALException {

		// decode presence flag
		boolean isNotNull = decodeBoolean();
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryEncoder.decodeNullableULong: " + isNotNull);
		
		ULong rv = null;
			
		// decode one element, or add null if presence flag indicates no element
		if (isNotNull) {
			rv = decodeULong();
		}
		
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryDecoder.decodeNullableULong: " + rv);
	
		return rv;
	}
	
	@Override
	public Time decodeTime() throws MALException {
		return new Time(((TCPIPSplitBufferHolder)sourceBuffer).getFixedUnsignedLong());
	}

	@Override
	public FineTime decodeFineTime() throws MALException {
		return new FineTime(((TCPIPSplitBufferHolder)sourceBuffer).getFixedUnsignedLong());
	}
	
	@Override
	public Byte decodeOctet() throws MALException {
		return sourceBuffer.get8();
	}
	
	@Override
	public UOctet decodeUOctet() throws MALException {
		int val = ((TCPIPSplitBufferHolder)sourceBuffer).getUnsignedByte();
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryDecoder.decodeUOctet: " + String.format("%02X ", val) + " " + val);
		return new UOctet((short) val);
	}

	/**
	 * Extends BufferHolder to handle split binary encoding.
	 */
	protected static class TCPIPSplitBufferHolder extends BinaryBufferHolder {
		private boolean bitStoreLoaded = false;
		private BitGet bitStore = null;

		private static final BigInteger B_0X7F = new BigInteger("127");
		private static final BigInteger B_0X80 = new BigInteger("128");

		/**
		 * Constructor.
		 *
		 * @param is
		 *            Input stream to read from.
		 * @param buf
		 *            Source buffer to use.
		 * @param offset
		 *            Buffer offset to read from next.
		 * @param length
		 *            Length of readable data held in the array, which may be
		 *            larger.
		 */
		public TCPIPSplitBufferHolder(final java.io.InputStream is,
				final byte[] buf, final int offset, final int length) {
			super(is, buf, offset, length);

			super.buf.setForceRealloc(true);
		}

                
		public void checkBuffer(final int requiredLength) throws MALException {
			// ensure that the bit buffer has been loaded first
			if (!bitStoreLoaded) {
				loadBitStore();
			}

			super.buf.checkBuffer(requiredLength);
		}

		protected void bufferRealloced(int oldSize) {
			if (0 < oldSize) {
        			super.buf.setForceRealloc(false);
			}
		}
		
		@Override
		public String getString() throws MALException {
			
			// ensure that the bit buffer has been loaded first
			if (!bitStoreLoaded) {
				loadBitStore();
			}
			
			final int len = getUnsignedInt();

			if (len >= 0) {
				checkBuffer(len);

				final String s = new String(buf.getBuf(), buf.getOffset(), len, UTF8_CHARSET);
				buf.shiftOffsetAndReturnPrevious((int)len);
				return s;
			}
			return null;
		}
		
		public int getUnsignedByte() throws MALException {
			
			RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryDecoder.getUnsignedByte()");
			
			if (!bitStoreLoaded) {
				loadBitStore();
			}

			checkBuffer(1);
			
			RLOGGER.log(Level.FINEST, "Buffer: " + String.format("%02X ", buf.getBuf()[buf.getOffset()]));
			
			int retVal = buf.getBuf()[buf.shiftOffsetAndReturnPrevious(1)];
			if (retVal == -1) {
				retVal = 255;
			}

			return retVal;
//			 return buf[offset++] & 0xFF;
		}

		@Override
		public byte[] getBytes() throws MALException {

			// ensure that the bit buffer has been loaded first
			if (!bitStoreLoaded) {
				loadBitStore();
			}
			
			return directGetBytes(getUnsignedInt());
		}

		@Override
		public boolean getBool() throws MALException {
			// ensure that the bit buffer has been loaded first
			if (!bitStoreLoaded) {
				loadBitStore();
			}
			
			return bitStore.pop();
		}
		
	    @Override
	    public BigInteger getBigInteger() throws MALException
	    {
	    	if (!bitStoreLoaded) {
				loadBitStore();
			}
	    	
	    	RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryDecoder.getBigInteger");
	    	
			int i = 0;
			int j = 0;
			int b;
			BigInteger resultValue = BigInteger.ZERO;
			while (((b = get8()) & 0x80) != 0) {
				resultValue = resultValue.or((new BigInteger(b + "").and(B_0X7F)).shiftLeft(i));
				i+=7;
				j++;
			}
			resultValue = resultValue.or(new BigInteger(b + "").shiftLeft(i));
			
			RLOGGER.log(Level.FINEST, "In decimal: " + resultValue.toString(16));
			
			return resultValue;
	    }
		
		/**
		 * Ensures that the bit buffer has been loaded
		 *
		 * @throws MALException
		 *             on error.
		 */
		protected void loadBitStore() throws MALException {
			// ensure that the bit buffer has been loaded first
			bitStoreLoaded = true;
			int size = super.getUnsignedInt();

			if (size >= 0) {
				super.buf.checkBuffer(size);

				bitStore = new BitGet(buf.getBuf(), buf.getOffset(), size);
				buf.shiftOffsetAndReturnPrevious((int)size);
			} else {
				bitStore = new BitGet(null, 0, 0);
			}
		}
		
		/**
		 * Decode an unsigned int using a split-binary approach
		 */
		@Override
		public int getUnsignedInt() throws MALException {
			
			if (!bitStoreLoaded) {
				loadBitStore();
			}
			
			int value = 0;
			int i = 0;
			int b;
			while (((b = get8()) & 0x80) != 0) {
				value |= (b & 0x7F) << i;
				i += 7;
			}
			return value | (b << i);
		}
		
		/**
		 * Decode an unsigned long using a split-binary approach
		 */
		@Override
		public long getUnsignedLong() throws MALException {
			
			if (!bitStoreLoaded) {
				loadBitStore();
			}
			
			long value = 0;
			int i = 0;
			long b;
			while (((b = get8()) & 0x80) != 0) {
				value |= (b & 0x7F) << i;
				i += 7;
			}
			return value | (b << i);
		}
		
		public long getFixedUnsignedLong() throws MALException {
			
			checkBuffer(8);
			
			final int i = super.buf.shiftOffsetAndReturnPrevious(8);
			return java.nio.ByteBuffer.wrap(super.buf.getBuf(), i, 8).getLong();
		}
	}

	/**
	 * Simple helper class for dealing with bit array. Smaller and faster than
	 * Java BitSet.
	 */
	protected static class BitGet {
		private final byte[] bitBytes;
		private final int bitBytesOffset;
		private final int bitBytesInUse;
		private int byteIndex = 0;
		private int bitIndex = 0;

		/**
		 * Constructor.
		 *
		 * @param bytes
		 *            Encoded bit set bytes. Supplied array is accessed
		 *            directly, it is not copied.
		 * @param offset
		 *            Offset, in bytes, into supplied byte array for start of
		 *            bit set.
		 * @param length
		 *            Length, in bytes, of supplied bit set.
		 */
		public BitGet(byte[] bytes, final int offset, final int length) {
			this.bitBytes = bytes;
			this.bitBytesOffset = offset;
			this.bitBytesInUse = length;
		}

		/**
		 * Returns true if the next bit is set to '1', false is set to '0'.
		 *
		 * @return True is set to '1', false otherwise.
		 */
		public boolean pop() {
			boolean rv = (byteIndex < bitBytesInUse)
					&& ((bitBytes[byteIndex + bitBytesOffset] & (1 << bitIndex)) != 0);
			RLOGGER.log(Level.FINEST, "BitGet [byteIndex=" + byteIndex + ", bitIndex=" + bitIndex + ", rv=" + rv + "]");

			if (7 == bitIndex) {
				bitIndex = 0;
				++byteIndex;
			} else {
				++bitIndex;
			}			

			return rv;
		}
	}
}
