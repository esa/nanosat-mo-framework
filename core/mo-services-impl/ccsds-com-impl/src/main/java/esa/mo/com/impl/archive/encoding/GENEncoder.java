/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO Generic Encoder Framework
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
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALListEncoder;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * Extends the MALEncoder and MALListEncoder interfaces for use in the generic encoding framework.
 */
public abstract class GENEncoder implements MALListEncoder {
    protected static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;
    protected static final String ENCODING_EXCEPTION_STR = "Bad encoding";
    protected final StreamHolder outputStream;

    /**
     * Constructor for derived classes that have their own stream holder implementation that should be used.
     *
     * @param os Output stream to write to.
     */
    protected GENEncoder(final StreamHolder os) {
        this.outputStream = os;
    }

    @Override
    public MALListEncoder createListEncoder(final java.util.List value) throws MALException {
        try {
            outputStream.addUnsignedInt(value.size());

            return this;
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeDouble(final Double value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addDouble(value);
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableDouble(final Double value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeDouble(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeInteger(final Integer value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addSignedInt(value);
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableInteger(final Integer value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeInteger(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeLong(final Long value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addSignedLong(value);
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableLong(final Long value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeLong(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeOctet(final Byte value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addByte(value);
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableOctet(final Byte value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeOctet(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeShort(final Short value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addSignedShort(value);
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableShort(final Short value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeShort(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeUInteger(final UInteger value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addUnsignedLong32(value.getValue());
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableUInteger(final UInteger value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeUInteger(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeULong(final ULong value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addBigInteger(value.getValue());
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableULong(final ULong value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeULong(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeUOctet(final UOctet value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addUnsignedShort8(value.getValue());
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableUOctet(final UOctet value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeUOctet(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeUShort(final UShort value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addUnsignedInt16(value.getValue());
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableUShort(final UShort value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeUShort(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeURI(final URI value) throws MALException {
        try {
            checkForNull(value);
            checkForNull(value.getValue());
            outputStream.addString(value.getValue());
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableURI(final URI value) throws MALException {
        try {
            if ((null != value) && (null != value.getValue())) {
                outputStream.addNotNull();
                encodeURI(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeIdentifier(final Identifier value) throws MALException {
        try {
            checkForNull(value);
            checkForNull(value.getValue());
            outputStream.addString(value.getValue());
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableIdentifier(final Identifier value) throws MALException {
        try {
            if ((null != value) && (null != value.getValue())) {
                outputStream.addNotNull();
                encodeIdentifier(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeString(final String value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addString(value);
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableString(final String value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeString(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeBoolean(final Boolean value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addBool(value);
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableBoolean(final Boolean value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeBoolean(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeTime(final Time value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addUnsignedLong(value.getValue());
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableTime(final Time value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeTime(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeFineTime(final FineTime value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addUnsignedLong(value.getValue());
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableFineTime(final FineTime value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeFineTime(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeBlob(final Blob value) throws MALException {
        try {
            checkForNull(value);
            if (value.isURLBased()) {
                checkForNull(value.getURL());
            } else {
                checkForNull(value.getValue());
            }
            outputStream.addBytes(value.getValue());
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableBlob(final Blob value) throws MALException {
        try {
            if ((null != value) && ((value.isURLBased() && (null != value.getURL())) || (!value.isURLBased() && (null !=
                value.getValue())))) {
                outputStream.addNotNull();
                encodeBlob(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeDuration(final Duration value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addDouble(value.getValue());
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableDuration(final Duration value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeDuration(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeFloat(final Float value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addFloat(value);
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableFloat(final Float value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeFloat(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeAttribute(final Attribute value) throws MALException {
        try {
            checkForNull(value);
            outputStream.addByte(internalEncodeAttributeType(value.getTypeShortForm().byteValue()));
            value.encode(this);
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeNullableAttribute(final Attribute value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                encodeAttribute(value);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void encodeElement(final Element value) throws MALException {
        checkForNull(value);
        value.encode(this);
    }

    @Override
    public void encodeNullableElement(final Element value) throws MALException {
        try {
            if (null != value) {
                outputStream.addNotNull();
                value.encode(this);
            } else {
                outputStream.addIsNull();
            }
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    @Override
    public void close() {
        try {
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger("org.ccsds.moims.mo.mal.encoding.gen").log(Level.WARNING,
                "Exception thrown on Encoder.close", ex);
        }
    }

    /**
     * Allows the encoding of a byte array, usually for already encoded values
     *
     * @param value The type to encode
     * @throws MALException if there is an error
     */
    public void directEncodeBytes(final byte[] value) throws MALException {
        try {
            outputStream.directAdd(value);
        } catch (IOException ex) {
            throw new MALException(ENCODING_EXCEPTION_STR, ex);
        }
    }

    /**
     * Allows the encoding for the type of an abstract element to be over-ridded
     *
     * @param value The type to encode
     * @param withNull If true encode a isNull field
     * @throws MALException if there is an error
     */
    public void encodeAbstractElementType(final Long value, boolean withNull) throws MALException {
        if (withNull) {
            encodeNullableLong(value);
        } else {
            encodeLong(value);
        }
    }

    /**
     * Converts the MAL representation of an Attribute type short form to the representation used by the encoding.
     *
     * @param value The Attribute type short form.
     * @return The byte value used by the encoding
     * @throws MALException On error.
     */
    public byte internalEncodeAttributeType(byte value) throws MALException {
        return value;
    }

    /**
     * Throws a MALException when supplied with a NULL value
     *
     * @param value The value to check
     * @throws MALException if value is NULL
     */
    protected void checkForNull(Object value) throws MALException {
        if (null == value) {
            throw new MALException("Null value supplied in a non-nullable field");
        }
    }

    /**
     * Internal class for writing to the output stream. Overridden by sub-classes to alter the low level encoding.
     */
    protected abstract static class StreamHolder {
        protected final OutputStream outputStream;

        /**
         * Constructor.
         *
         * @param outputStream the stream to encode in to.
         */
        public StreamHolder(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        /**
         * Adds a String to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addString(final String value) throws IOException;

        /**
         * Adds a float to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addFloat(final float value) throws IOException;

        /**
         * Adds a double to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addDouble(final double value) throws IOException;

        /**
         * Adds a BigInteger to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addBigInteger(final BigInteger value) throws IOException;

        /**
         * Adds a signed long to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addSignedLong(final long value) throws IOException;

        /**
         * Adds a signed int to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addSignedInt(final int value) throws IOException;

        /**
         * Adds a signed short to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addSignedShort(final short value) throws IOException;

        /**
         * Adds a zigzag encoded unsigned long to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addUnsignedLong(long value) throws IOException;

        /**
         * Adds an unsigned 32bit integer held as a long to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addUnsignedLong32(long value) throws IOException;

        /**
         * Adds a zigzag encoded unsigned int to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addUnsignedInt(int value) throws IOException;

        /**
         * Adds an unsigned 32bit integer held as a long to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addUnsignedInt16(int value) throws IOException;

        /**
         * Adds a zigzag encoded unsigned short to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addUnsignedShort(int value) throws IOException;

        /**
         * Adds an unsigned 32bit integer held as a long to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addUnsignedShort8(short value) throws IOException;

        /**
         * Adds a byte array to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addBytes(final byte[] value) throws IOException;

        /**
         * Adds a byte to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addByte(final byte value) throws IOException;

        /**
         * Adds a Boolean to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addBool(boolean value) throws IOException;

        /**
         * Adds a not Null flag value to the output stream.
         *
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addNotNull() throws IOException;

        /**
         * Adds an is Null flag value to the output stream.
         *
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public abstract void addIsNull() throws IOException;

        /**
         * Low level byte array write to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public void directAdd(final byte[] value) throws IOException {
            outputStream.write(value);
        }

        /**
         * Low level byte array write to the output stream.
         *
         * @param value the value to encode.
         * @param os offset into array.
         * @param ln length to add.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public void directAdd(final byte[] value, int os, int ln) throws IOException {
            outputStream.write(value, os, ln);
        }

        /**
         * Low level byte write to the output stream.
         *
         * @param value the value to encode.
         * @throws IOException is there is a problem adding the value to the stream.
         */
        public void directAdd(final byte value) throws IOException {
            outputStream.write(value);
        }

        /**
         * Closes and flushes the output stream.
         *
         * @throws IOException if there is an error.s
         */
        public void close() throws IOException {
            outputStream.flush();
        }
    }
}
