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

import java.util.List;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALListDecoder;

/**
 * Implements the MALListDecoder interface for a binary encoding.
 */
public class BinaryListDecoder extends BinaryDecoder implements MALListDecoder {
    private final int size;
    private final List list;

    /**
     * Constructor.
     *
     * @param list List to decode into.
     * @param srcBuffer Buffer to manage.
     * @throws MALException If cannot decode list size.
     */
    public BinaryListDecoder(final List list, final BufferHolder srcBuffer) throws MALException {
        super(srcBuffer);

        this.list = list;
        size = srcBuffer.getUnsignedInt();
    }

    @Override
    public boolean hasNext() {
        return list.size() < size;
    }

    @Override
    public int size() {
        return size;
    }
}
