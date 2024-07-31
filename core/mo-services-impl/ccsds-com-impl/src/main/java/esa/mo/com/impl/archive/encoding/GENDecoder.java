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

import java.math.BigInteger;
import org.ccsds.moims.mo.mal.MALDecoder;
import org.ccsds.moims.mo.mal.MALException;
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
import org.ccsds.moims.mo.mal.structures.Union;

/**
 * Extends the MALDecoder interface for use in the generic encoding framework.
 */
public abstract class GENDecoder implements MALDecoder {
    protected final BufferHolder sourceBuffer;

    protected GENDecoder(BufferHolder sourceBuffer) {
        this.sourceBuffer = sourceBuffer;
    }

    @Override
    public Blob decodeBlob() throws MALException {
        return new Blob(sourceBuffer.getBytes());
    }

    @Override
    public Boolean decodeBoolean() throws MALException {
        return sourceBuffer.getBool();
    }

    @Override
    public Identifier decodeIdentifier() throws MALException {
        return new Identifier(sourceBuffer.getString());
    }

    @Override
    public URI decodeURI() throws MALException {
        return new URI(sourceBuffer.getString());
    }

    @Override
    public String decodeString() throws MALException {
        return sourceBuffer.getString();
    }

    @Override
    public Integer decodeInteger() throws MALException {
        return sourceBuffer.getSignedInt();
    }

    @Override
    public Time decodeTime() throws MALException {
        return new Time(sourceBuffer.getUnsignedLong());
    }

    @Override
    public FineTime decodeFineTime() throws MALException {
        return new FineTime(sourceBuffer.getUnsignedLong());
    }

    @Override
    public Duration decodeDuration() throws MALException {
        return new Duration(sourceBuffer.getDouble());
    }

    @Override
    public Long decodeLong() throws MALException {
        return sourceBuffer.getSignedLong();
    }

    @Override
    public Byte decodeOctet() throws MALException {
        return sourceBuffer.get8();
    }

    @Override
    public Short decodeShort() throws MALException {
        return sourceBuffer.getSignedShort();
    }

    @Override
    public ULong decodeULong() throws MALException {
        return new ULong(sourceBuffer.getBigInteger());
    }

    @Override
    public UInteger decodeUInteger() throws MALException {
        return new UInteger(sourceBuffer.getUnsignedLong32());
    }

    @Override
    public UOctet decodeUOctet() throws MALException {
        return new UOctet(sourceBuffer.getUnsignedShort8());
    }

    @Override
    public UShort decodeUShort() throws MALException {
        return new UShort(sourceBuffer.getUnsignedInt16());
    }

    @Override
    public Float decodeFloat() throws MALException {
        return sourceBuffer.getFloat();
    }

    @Override
    public Double decodeDouble() throws MALException {
        return sourceBuffer.getDouble();
    }

    @Override
    public String decodeNullableString() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return sourceBuffer.getString();
        }

        return null;
    }

    @Override
    public Identifier decodeNullableIdentifier() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeIdentifier();
        }

        return null;
    }

    @Override
    public URI decodeNullableURI() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeURI();
        }

        return null;
    }

    @Override
    public Blob decodeNullableBlob() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeBlob();
        }

        return null;
    }

    @Override
    public Boolean decodeNullableBoolean() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeBoolean();
        }

        return null;
    }

    @Override
    public Time decodeNullableTime() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeTime();
        }

        return null;
    }

    @Override
    public FineTime decodeNullableFineTime() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeFineTime();
        }

        return null;
    }

    @Override
    public Duration decodeNullableDuration() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeDuration();
        }

        return null;
    }

    @Override
    public Float decodeNullableFloat() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeFloat();
        }

        return null;
    }

    @Override
    public Double decodeNullableDouble() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeDouble();
        }

        return null;
    }

    @Override
    public Long decodeNullableLong() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeLong();
        }

        return null;
    }

    @Override
    public Integer decodeNullableInteger() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeInteger();
        }

        return null;
    }

    @Override
    public Short decodeNullableShort() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeShort();
        }

        return null;
    }

    @Override
    public Byte decodeNullableOctet() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeOctet();
        }

        return null;
    }

    @Override
    public ULong decodeNullableULong() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeULong();
        }

        return null;
    }

    @Override
    public UInteger decodeNullableUInteger() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeUInteger();
        }

        return null;
    }

    @Override
    public UShort decodeNullableUShort() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeUShort();
        }

        return null;
    }

    @Override
    public UOctet decodeNullableUOctet() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeUOctet();
        }

        return null;
    }

    @Override
    public Attribute decodeAttribute() throws MALException {
        return internalDecodeAttribute(internalDecodeAttributeType(sourceBuffer.get8()));
    }

    @Override
    public Attribute decodeNullableAttribute() throws MALException {
        if (sourceBuffer.isNotNull()) {
            return decodeAttribute();
        }

        return null;
    }

    protected Attribute internalDecodeAttribute(final int typeval) throws MALException {
        switch (typeval) {
            case Attribute._BLOB_TYPE_SHORT_FORM:
                return decodeBlob();
            case Attribute._BOOLEAN_TYPE_SHORT_FORM:
                return new Union(decodeBoolean());
            case Attribute._DURATION_TYPE_SHORT_FORM:
                return decodeDuration();
            case Attribute._FLOAT_TYPE_SHORT_FORM:
                return new Union(decodeFloat());
            case Attribute._DOUBLE_TYPE_SHORT_FORM:
                return new Union(decodeDouble());
            case Attribute._IDENTIFIER_TYPE_SHORT_FORM:
                return decodeIdentifier();
            case Attribute._OCTET_TYPE_SHORT_FORM:
                return new Union(decodeOctet());
            case Attribute._UOCTET_TYPE_SHORT_FORM:
                return decodeUOctet();
            case Attribute._SHORT_TYPE_SHORT_FORM:
                return new Union(decodeShort());
            case Attribute._USHORT_TYPE_SHORT_FORM:
                return decodeUShort();
            case Attribute._INTEGER_TYPE_SHORT_FORM:
                return new Union(decodeInteger());
            case Attribute._UINTEGER_TYPE_SHORT_FORM:
                return decodeUInteger();
            case Attribute._LONG_TYPE_SHORT_FORM:
                return new Union(decodeLong());
            case Attribute._ULONG_TYPE_SHORT_FORM:
                return decodeULong();
            case Attribute._STRING_TYPE_SHORT_FORM:
                return new Union(decodeString());
            case Attribute._TIME_TYPE_SHORT_FORM:
                return decodeTime();
            case Attribute._FINETIME_TYPE_SHORT_FORM:
                return decodeFineTime();
            case Attribute._URI_TYPE_SHORT_FORM:
                return decodeURI();
            default:
                throw new MALException("Unknown attribute type received: " + typeval);
        }
    }

    @Override
    public Element decodeElement(final Element element) throws IllegalArgumentException, MALException {
        return element.decode(this);
    }

    @Override
    public Element decodeNullableElement(final Element element) throws MALException {
        if (sourceBuffer.isNotNull()) {
            return element.decode(this);
        }

        return null;
    }

    /**
     * Allows the decoding for the type of an abstract element to be over-ridded
     *
     * @param withNull If true encode a isNull field
     * @return The type to decode
     * @throws MALException if there is an error
     */
    public Long decodeAbstractElementType(boolean withNull) throws MALException {
        if (withNull) {
            return decodeNullableLong();
        }

        return decodeLong();
    }

    public int internalDecodeAttributeType(byte value) throws MALException {
        return value;
    }

    /**
     * Returns the remaining data of the input stream that has not been used for decoding for wrapping in a MALEncodedBody class.
     *
     * @return the unused body data.
     * @throws MALException if there is an error.
     */
    public abstract byte[] getRemainingEncodedData() throws MALException;

    /**
     * Internal class that is used to hold the byte buffer. Derived classes should extend this (and replace it in the constructors) if
     * they encode the fields differently from this encoding.
     */
    public abstract static class BufferHolder {
        /**
         * Gets a string from the incoming stream.
         *
         * @return the extracted string.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract String getString() throws MALException;

        /**
         * Gets a float from the incoming stream.
         *
         * @return the extracted float.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract float getFloat() throws MALException;

        /**
         * Gets a double from the incoming stream.
         *
         * @return the extracted double.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract double getDouble() throws MALException;

        /**
         * Gets a BigInteger from the incoming stream.
         *
         * @return the extracted BigInteger.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract BigInteger getBigInteger() throws MALException;

        /**
         * Gets a single signed long from the incoming stream.
         *
         * @return the extracted value.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract long getSignedLong() throws MALException;

        /**
         * Gets a single signed integer from the incoming stream.
         *
         * @return the extracted value.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract int getSignedInt() throws MALException;

        /**
         * Gets a single signed short from the incoming stream.
         *
         * @return the extracted value.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract short getSignedShort() throws MALException;

        /**
         * Gets a single unsigned long from the incoming stream.
         *
         * @return the extracted value.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract long getUnsignedLong() throws MALException;

        /**
         * Gets a single 32 bit unsigned integer as a long from the incoming stream.
         *
         * @return the extracted value.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract long getUnsignedLong32() throws MALException;

        /**
         * Gets a single unsigned integer from the incoming stream.
         *
         * @return the extracted value.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract int getUnsignedInt() throws MALException;

        /**
         * Gets a single 16 bit unsigned integer as a int from the incoming stream.
         *
         * @return the extracted value.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract int getUnsignedInt16() throws MALException;

        /**
         * Gets a single unsigned short from the incoming stream.
         *
         * @return the extracted value.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract int getUnsignedShort() throws MALException;

        /**
         * Gets a single 8 bit unsigned integer as a short from the incoming stream.
         *
         * @return the extracted value.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract short getUnsignedShort8() throws MALException;

        /**
         * Gets a single byte from the incoming stream.
         *
         * @return the extracted byte.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract byte get8() throws MALException;

        /**
         * Gets a byte array from the incoming stream.
         *
         * @return the extracted byte.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract byte[] getBytes() throws MALException;

        /**
         * Gets a single Boolean value from the incoming stream.
         *
         * @return the extracted value.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract boolean getBool() throws MALException;

        /**
         * Returns true is the next value is not NULL.
         *
         * @return the extracted value.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract boolean isNotNull() throws MALException;

        /**
         * Gets a byte array from the incoming stream.
         *
         * @param length The number of bytes to retrieve
         * @return the extracted byte.
         * @throws MALException If there is a problem with the decoding.
         */
        public abstract byte[] directGetBytes(int length) throws MALException;
    }
}
