/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO Binary encoder
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
package esa.mo.com.impl.archive.encoding;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALListDecoder;
import org.ccsds.moims.mo.mal.structures.*;

/**
 * Implements the MALDecoder interface for a binary encoding.
 */
public class BinaryDecoder extends GENDecoder {
    protected static final java.util.logging.Logger LOGGER = Logger.getLogger(BinaryDecoder.class.getName());
    protected static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;
    protected static final int BLOCK_SIZE = 65536;

    /**
     * Constructor.
     *
     * @param src Byte array to read from.
     */
    public BinaryDecoder(final byte[] src) {
        super(new BinaryBufferHolder(null, src, 0, src.length));
    }

    /**
     * Constructor.
     *
     * @param is Input stream to read from.
     */
    public BinaryDecoder(final java.io.InputStream is) {
        super(new BinaryBufferHolder(is, null, 0, 0));
    }

    /**
     * Constructor.
     *
     * @param src Byte array to read from.
     * @param offset index in array to start reading from.
     */
    public BinaryDecoder(final byte[] src, final int offset) {
        super(new BinaryBufferHolder(null, src, offset, src.length));
    }

    /**
     * Constructor.
     *
     * @param src Source buffer holder to use.
     */
    protected BinaryDecoder(final BufferHolder src) {
        super(src);
    }

    @Override
    public MALListDecoder createListDecoder(final List list) throws MALException {
        return new BinaryListDecoder(list, sourceBuffer);
    }

    @Override
    public Boolean decodeNullableBoolean() throws MALException {
        final byte b = sourceBuffer.get8();

        if (2 == b) {
            return null;
        }

        return 1 == b ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public String decodeNullableString() throws MALException {
        return sourceBuffer.getString();
    }

    @Override
    public Identifier decodeNullableIdentifier() throws MALException {
        final String s = sourceBuffer.getString();
        if (null != s) {
            return new Identifier(s);
        }

        return null;
    }

    @Override
    public URI decodeNullableURI() throws MALException {
        final String s = sourceBuffer.getString();
        if (null != s) {
            return new URI(s);
        }

        return null;
    }

    @Override
    public Blob decodeNullableBlob() throws MALException {
        final int len = sourceBuffer.getSignedInt();
        if (len >= 0) {
            return new Blob(sourceBuffer.directGetBytes(len));
        }

        return null;
    }

    @Override
    public ULong decodeNullableULong() throws MALException {
        final int len = sourceBuffer.getSignedInt();
        if (len >= 0) {
            return new ULong(new BigInteger(sourceBuffer.directGetBytes(len)));
        }

        return null;
    }

    @Override
    public byte[] getRemainingEncodedData() throws MALException {
        BinaryBufferHolder dSourceBuffer = (BinaryBufferHolder) sourceBuffer;
        return Arrays.copyOfRange(dSourceBuffer.buf.buf, dSourceBuffer.buf.offset, dSourceBuffer.buf.contentLength);
    }

    /**
     * Internal class that is used to hold the byte buffer. Derived classes should extend this (and replace it in the
     * constructors) if they encode the fields differently from this encoding.
     */
    protected static class BinaryBufferHolder extends BufferHolder {
        protected final InputReader buf;

        /**
         * Constructor.
         *
         * @param is Input stream to read from.
         * @param buf Source buffer to use.
         * @param offset Buffer offset to read from next.
         * @param length Length of readable data held in the array, which may be larger.
         */
        public BinaryBufferHolder(final java.io.InputStream is, final byte[] buf, final int offset, final int length) {
            super();
            this.buf = new InputReader(is, buf, offset, length);
        }

        /**
         * Constructor.
         *
         * @param buf Source buffer to use.
         */
        protected BinaryBufferHolder(final InputReader buf) {
            super();
            this.buf = buf;
        }

        public InputReader getBuf() {
            return buf;
        }

        @Override
        public String getString() throws MALException {
            final int len = getSignedInt();

            if (len >= 0) {
                buf.checkBuffer(len);

                final String s = new String(buf.buf, buf.offset, len, UTF8_CHARSET);
                buf.offset += len;
                return s;
            }
            return null;
        }

        @Override
        public long getSignedLong() throws MALException {
            final long raw = getUnsignedLong();
            final long temp = (((raw << 63) >> 63) ^ raw) >> 1;
            return temp ^ (raw & (1L << 63));
        }

        @Override
        public int getSignedInt() throws MALException {
            final int raw = getUnsignedInt();
            final int temp = (((raw << 31) >> 31) ^ raw) >> 1;
            return temp ^ (raw & (1 << 31));
        }

        @Override
        public short getSignedShort() throws MALException {
            return (short) getSignedInt();
        }

        @Override
        public long getUnsignedLong() throws MALException {
            long value = 0L;
            int i = 0;
            long b;
            while (((b = get8()) & 0x80L) != 0) {
                value |= (b & 0x7F) << i;
                i += 7;
            }
            return value | (b << i);
        }

        @Override
        public long getUnsignedLong32() throws MALException {
            return getUnsignedLong();
        }

        @Override
        public int getUnsignedInt() throws MALException {
            int value = 0;
            int i = 0;
            int b;
            while (((b = get8()) & 0x80) != 0) {
                value |= (b & 0x7F) << i;
                i += 7;
            }
            return value | (b << i);
        }

        @Override
        public int getUnsignedInt16() throws MALException {
            return getUnsignedInt();
        }

        @Override
        public int getUnsignedShort() throws MALException {
            return getUnsignedInt();
        }

        @Override
        public short getUnsignedShort8() throws MALException {
            return (short) getUnsignedShort();
        }

        @Override
        public byte get8() throws MALException {
            return buf.get8();
        }

        @Override
        public byte[] getBytes() throws MALException {
            return directGetBytes(getSignedInt());
        }

        @Override
        public boolean getBool() throws MALException {
            return !(0 == get8());
        }

        @Override
        public boolean isNotNull() throws MALException {
            return getBool();
        }

        @Override
        public float getFloat() throws MALException {
            return Float.intBitsToFloat(getSignedInt());
        }

        @Override
        public double getDouble() throws MALException {
            return Double.longBitsToDouble(getSignedLong());
        }

        @Override
        public BigInteger getBigInteger() throws MALException {
            return new BigInteger(directGetBytes(getSignedInt()));
        }

        @Override
        public byte[] directGetBytes(final int size) throws MALException {
            return buf.directGetBytes(size);
        }
    }

    protected static class InputReader {
        protected final java.io.InputStream inputStream;
        protected byte[] buf;
        protected int offset;
        protected int contentLength;
        protected boolean forceRealloc = false;

        /**
         * Constructor.
         *
         * @param is Input stream to read from.
         * @param buf Source buffer to use.
         * @param offset Buffer offset to read from next.
         * @param length Length of readable data held in the array, which may be larger.
         */
        public InputReader(final java.io.InputStream is, final byte[] buf, final int offset, final int length) {
            super();
            this.inputStream = is;
            this.buf = buf;
            this.offset = offset;
            this.contentLength = length;
        }

        public void setForceRealloc(boolean forceRealloc) {
            this.forceRealloc = forceRealloc;
        }

        public byte get8() throws MALException {
            checkBuffer(1);

            return buf[offset++];
        }

        public byte[] directGetBytes(final int size) throws MALException {
            if (size >= 0) {
                checkBuffer(size);

                final byte[] v = Arrays.copyOfRange(buf, offset, offset + size);
                offset += size;
                return v;
            }

            throw new IllegalArgumentException("Size must not be negative");
        }

        /**
         * Ensures that we have loaded enough buffer from the input stream (if we are stream based) for the next read.
         *
         * @param requiredLength number of bytes required.
         * @throws MALException if there is an error reading from the stream
         */
        public void checkBuffer(final int requiredLength) throws MALException {
            if (null != inputStream) {
                int existingContentRemaining = 0;
                int existingBufferLength = 0;

                // have we got any loaded data currently
                if (null != this.buf) {
                    existingContentRemaining = this.contentLength - this.offset;
                    existingBufferLength = this.buf.length;
                }

                // check to see if currently loaded data covers the required data size
                if (existingContentRemaining < requiredLength) {
                    LOGGER.log(Level.FINER, "Not enought bytes available. Expecting {0}", requiredLength);

                    // ok, check to see if we have enough space left in the current buffer for what we need to load
                    if ((existingBufferLength - this.offset) < requiredLength) {
                        byte[] destBuf = this.buf;

                        // its not big enough, we need to check if we need a bigger buffer or in case we know the existing 
                        // buffer is still required.
                        if (forceRealloc || (existingBufferLength < requiredLength)) {
                            // we do, so allocate one
                            bufferRealloced(existingBufferLength);
                            existingBufferLength = (requiredLength > BLOCK_SIZE) ? requiredLength : BLOCK_SIZE;
                            destBuf = new byte[existingBufferLength];
                        }

                        // this either shifts the existing contents to the start of the old buffer, or copies it into the new buffer
                        // NOTE: this is faster than System.arraycopy, as that performs argument type checks
                        if (existingContentRemaining >= 0)
                            System.arraycopy(this.buf, this.offset + 0, destBuf, 0, existingContentRemaining);

                        // the start of the data in the buffer has moved to zero now
                        this.buf = destBuf;
                        this.offset = 0;
                        this.contentLength = existingContentRemaining;
                    }

                    try {
                        // read into the empty space of the buffer
                        LOGGER.log(Level.FINER, "Reading from input stream: {0}", (existingBufferLength -
                            this.contentLength));
                        final int read = inputStream.read(this.buf, this.contentLength, existingBufferLength -
                            this.contentLength);
                        LOGGER.log(Level.FINER, "Read from input stream: {0}", read);
                        if (read < 0) {
                            throw new MALException("Unable to read required amount from source stream: end of file.");
                        }
                        this.contentLength += read;
                    } catch (IOException ex) {
                        throw new MALException("Unable to read required amount from source stream", ex);
                    }
                }
            }
        }

        /**
         * Returns the internal byte buffer.
         *
         * @return the byte buffer
         */
        public byte[] getBuf() {
            return buf;
        }

        public int getOffset() {
            return offset;
        }

        /**
         * Adds a delta to the internal offset and returns the previous offset
         *
         * @param delta the delta to apply
         * @return the previous offset
         */
        public int shiftOffsetAndReturnPrevious(int delta) {
            int i = offset;
            offset += delta;
            return i;
        }

        /**
         * Notification method that can be used by derived classes to notify them that the internal buffer has been
         * reallocated.
         *
         * @param oldSize the old buffer size
         */
        public void bufferRealloced(int oldSize) {
            // no implementation for standard decoder
        }
    }
}
