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
package esa.mo.com.impl.archive.encoding;

import java.io.InputStream;
import org.ccsds.moims.mo.mal.MALException;

/**
 * Implements the MALDecoder interface for a split binary encoding.
 */
public class SplitBinaryDecoder extends esa.mo.com.impl.archive.encoding.BinaryDecoder
{
  /**
   * Constructor.
   *
   * @param src Byte array to read from.
   */
  public SplitBinaryDecoder(final byte[] src)
  {
    super(new SplitBufferHolder(null, src, 0, src.length));
  }

  /**
   * Constructor.
   *
   * @param is Input stream to read from.
   */
  public SplitBinaryDecoder(final java.io.InputStream is)
  {
    super(new SplitBufferHolder(is, null, 0, 0));
  }

  /**
   * Constructor.
   *
   * @param src Byte array to read from.
   * @param offset index in array to start reading from.
   */
  public SplitBinaryDecoder(final byte[] src, final int offset)
  {
    super(new SplitBufferHolder(null, src, offset, src.length));
  }

  /**
   * Constructor.
   *
   * @param src Source buffer holder to use.
   */
  protected SplitBinaryDecoder(final BufferHolder src)
  {
    super(src);
  }

  @Override
  public org.ccsds.moims.mo.mal.MALListDecoder createListDecoder(final java.util.List list) throws MALException
  {
    return new SplitBinaryListDecoder(list, sourceBuffer);
  }

  @Override
  public Boolean decodeBoolean() throws MALException
  {
    return sourceBuffer.getBool();
  }

  @Override
  public Boolean decodeNullableBoolean() throws MALException
  {
    if (sourceBuffer.getBool())
    {
      return decodeBoolean();
    }

    return null;
  }

  /**
   * Extends BufferHolder to handle split binary encoding.s
   */
  protected static class SplitBufferHolder extends BinaryBufferHolder
  {
    protected final SplitInputReader sbuf;
    
    /**
     * Constructor.
     *
     * @param is Input stream to read from.
     * @param buf Source buffer to use.
     * @param offset Buffer offset to read from next.
     * @param length Length of readable data held in the array, which may be larger.
     */
    public SplitBufferHolder(final java.io.InputStream is, final byte[] buf, final int offset, final int length)
    {
      super(new SplitInputReader(is, buf, offset, length));
      
      sbuf = (SplitInputReader)super.buf;
    }

    @Override
    public boolean getBool() throws MALException
    {
      // ensure that the bit buffer has been loaded first
      if (!sbuf.bitStoreLoaded)
      {
        sbuf.loadBitStore();
      }

      return sbuf.bitStore.pop();
    }
  }

  protected static class SplitInputReader extends InputReader
  {
    private boolean bitStoreLoaded = false;
    private BitGet bitStore = null;

    public SplitInputReader(InputStream is, byte[] buf, int offset, int length)
    {
      super(is, buf, offset, length);
      
      forceRealloc = true;
    }

    @Override
    public void checkBuffer(final int requiredLength) throws MALException
    {
      // ensure that the bit buffer has been loaded first
      if (!bitStoreLoaded)
      {
        loadBitStore();
      }

      super.checkBuffer(requiredLength);
    }

    @Override
    public void bufferRealloced(int oldSize)
    {
      if (0 < oldSize)
      {
        setForceRealloc(false);
      }
    }

    /**
     * Ensures that the bit buffer has been loaded
     *
     * @throws MALException on error.
     */
    protected void loadBitStore() throws MALException
    {
      // ensure that the bit buffer has been loaded first
      bitStoreLoaded = true;
      int size = getUnsignedInt();

      if (size >= 0)
      {
        super.checkBuffer(size);

        bitStore = new BitGet(buf, offset, size);
        offset += size;
      }
      else
      {
        bitStore = new BitGet(null, 0, 0);
      }
    }

    protected int getUnsignedInt() throws MALException
    {
      int value = 0;
      int i = 0;
      int b;
      while (((b = get8()) & 0x80) != 0)
      {
        value |= (b & 0x7F) << i;
        i += 7;
      }
      return value | (b << i);
    }
  }

  /**
   * Simple helper class for dealing with bit array. Smaller and faster than Java BitSet.
   */
  protected static class BitGet
  {
    private final byte[] bitBytes;
    private final int bitBytesOffset;
    private final int bitBytesInUse;
    private int byteIndex = 0;
    private int bitIndex = 0;

    /**
     * Constructor.
     *
     * @param bytes Encoded bit set bytes. Supplied array is accessed directly, it is not copied.
     * @param offset Offset, in bytes, into supplied byte array for start of bit set.
     * @param length Length, in bytes, of supplied bit set.
     */
    public BitGet(byte[] bytes, final int offset, final int length)
    {
      this.bitBytes = bytes;
      this.bitBytesOffset = offset;
      this.bitBytesInUse = length;
    }

    /**
     * Returns true if the next bit is set to '1', false is set to '0'.
     *
     * @return True is set to '1', false otherwise.
     */
    public boolean pop()
    {
      boolean rv = (byteIndex < bitBytesInUse) && ((bitBytes[byteIndex + bitBytesOffset] & (1 << bitIndex)) != 0);

      if (7 == bitIndex)
      {
        bitIndex = 0;
        ++byteIndex;
      }
      else
      {
        ++bitIndex;
      }

      return rv;
    }
  }
}
