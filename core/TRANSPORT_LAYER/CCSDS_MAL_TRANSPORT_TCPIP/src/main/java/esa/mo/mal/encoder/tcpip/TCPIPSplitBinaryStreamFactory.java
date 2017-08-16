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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import org.ccsds.moims.mo.mal.encoding.MALElementInputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;

import esa.mo.mal.encoder.binary.split.SplitBinaryStreamFactory;
import static esa.mo.mal.transport.tcpip.TCPIPTransport.RLOGGER; 


/**
 * A factory implementation for the generation of input and output stream classes,
 * which manage decoding and encoding, respectively.
 * 
 * @author Rian van Gijlswijk
 *
 */
public class TCPIPSplitBinaryStreamFactory extends SplitBinaryStreamFactory {

	@Override
	public MALElementInputStream createInputStream(InputStream is){
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryStreamFactory.createInputStream(InputStream)");
		return new TCPIPSplitBinaryElementInputStream(is);
	}

	@Override
	public MALElementInputStream createInputStream(byte[] bytes, int offset) {
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryStreamFactory.createInputStream(byte[], int)");
		return new TCPIPSplitBinaryElementInputStream(bytes, offset);
	}

	@Override
	public MALElementOutputStream createOutputStream(OutputStream os){
		RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryStreamFactory.createOutputStream(OutputStream)");
		return new TCPIPSplitBinaryElementOutputStream(os);
	}

}
