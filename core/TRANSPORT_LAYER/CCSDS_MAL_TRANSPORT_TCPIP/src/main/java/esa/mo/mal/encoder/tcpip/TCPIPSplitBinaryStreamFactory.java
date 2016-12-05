package esa.mo.mal.encoder.tcpip;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.encoding.MALElementInputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;

import esa.mo.mal.encoder.binary.split.SplitBinaryStreamFactory;
import static esa.mo.mal.transport.tcpip.TCPIPTransport.RLOGGER; 


/**
 * A factory implementation for the generation of input and output stream classes,
 * which manage decoding and encoding, respectively.
 * 
 * @author Rian van Gijlswijk <r.vangijlswijk@telespazio-vega.de>
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
