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

public class TCPIPSplitBinaryElementInputStream extends esa.mo.mal.encoder.binary.BinaryElementInputStream {
	
	public TCPIPSplitBinaryElementInputStream(final java.io.InputStream is) {
		super(new TCPIPSplitBinaryDecoder(is));
	}

	public TCPIPSplitBinaryElementInputStream(final byte[] buf, final int offset) {
		super(new TCPIPSplitBinaryDecoder(buf, offset));
	}

}
