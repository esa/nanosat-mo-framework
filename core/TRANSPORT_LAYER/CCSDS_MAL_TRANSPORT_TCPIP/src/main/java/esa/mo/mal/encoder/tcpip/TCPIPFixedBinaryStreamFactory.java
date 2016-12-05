package esa.mo.mal.encoder.tcpip;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.encoding.MALElementInputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;
import esa.mo.mal.encoder.binary.BinaryStreamFactory;
import static esa.mo.mal.transport.tcpip.TCPIPTransport.RLOGGER; 
/**
 * A factory implementation for the generation of input and output stream classes,
 * which manage decoding and encoding, respectively.
 * 
 * @author Rian van Gijlswijk <r.vangijlswijk@telespazio-vega.de>
 *
 */
public class TCPIPFixedBinaryStreamFactory extends BinaryStreamFactory {

	@Override
	protected void init(String protocol, Map properties)
			throws IllegalArgumentException, MALException {
	}

	@Override
	public MALElementInputStream createInputStream(InputStream is)
			throws IllegalArgumentException, MALException {
		return new TCPIPFixedBinaryElementInputStream(is);
	}

	@Override
	public MALElementInputStream createInputStream(byte[] bytes, int offset) {
		return new TCPIPFixedBinaryElementInputStream(bytes, offset);
	}

	@Override
	public MALElementOutputStream createOutputStream(OutputStream os)
			throws IllegalArgumentException, MALException {
		return new TCPIPFixedBinaryElementOutputStream(os);
	}

}
