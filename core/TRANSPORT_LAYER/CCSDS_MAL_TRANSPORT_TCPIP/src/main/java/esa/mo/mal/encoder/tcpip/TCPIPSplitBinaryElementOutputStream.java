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

import esa.mo.mal.encoder.binary.BinaryElementOutputStream;

public class TCPIPSplitBinaryElementOutputStream extends BinaryElementOutputStream {

	public TCPIPSplitBinaryElementOutputStream(final java.io.OutputStream os) {
		super(os);
	}

	@Override
	protected esa.mo.mal.encoder.gen.GENEncoder createEncoder(java.io.OutputStream os) {
		return new TCPIPSplitBinaryEncoder(os);
	}

}
