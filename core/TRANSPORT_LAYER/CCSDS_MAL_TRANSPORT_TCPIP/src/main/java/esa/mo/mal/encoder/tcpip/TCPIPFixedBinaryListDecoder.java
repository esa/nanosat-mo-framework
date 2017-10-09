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

import java.util.List;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALListDecoder;

/**
 * Decode a list of elements
 *
 * @author Rian van Gijlswijk
 *
 */
public class TCPIPFixedBinaryListDecoder extends TCPIPFixedBinaryDecoder implements MALListDecoder {

    private final int size;
    private final List list;

    protected TCPIPFixedBinaryListDecoder(List list, final BufferHolder srcBuffer) throws MALException {
        super(srcBuffer);
        this.list = list;

        // decode number of elements in list
        this.size = (int) decodeUInteger().getValue();
    }

    /**
     * Returns false once the list is filled with a number of elements equalling
     * the expected size. The expected size is set at the beginning of the
     * output stream. As soon as this occurs, all elements are read from the
     * outputstream.
     */
    @Override
    public boolean hasNext() {
        return list.size() < size;
    }

    @Override
    public int size() {
        return size;
    }

}
