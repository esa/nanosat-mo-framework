package esa.mo.mal.encoder.tcpip;

import java.util.List;
import java.util.logging.Level;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALListDecoder;

import static esa.mo.mal.transport.tcpip.TCPIPTransport.RLOGGER;


/**
 * Decode a list of elements
 * 
 * @author Rian van Gijlswijk <r.vangijlswijk@telespazio-vega.de>
 *
 */
public class TCPIPSplitBinaryListDecoder extends TCPIPSplitBinaryDecoder implements MALListDecoder {
	
	private final int size;
	private final List list;

	protected TCPIPSplitBinaryListDecoder(List list, final BufferHolder srcBuffer) throws MALException {
		super(srcBuffer);
		
		this.list = list;
		
		// decode number of elements in list
	    size = srcBuffer.getUnsignedInt();
	    RLOGGER.log(Level.FINEST, "TCPIPSplitBinaryListDecoder.size: " + size);
	}

	/**
	 * Returns false once the list is filled with a number of elements equalling the expected
	 * size. The expected size is set at the beginning of the output stream. As soon as this
	 * occurs, all elements are read from the outputstream.
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
