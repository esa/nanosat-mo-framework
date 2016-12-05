package esa.mo.mal.encoder.tcpip;

public class TCPIPSplitBinaryElementInputStream extends esa.mo.mal.encoder.binary.BinaryElementInputStream {
	
	public TCPIPSplitBinaryElementInputStream(final java.io.InputStream is) {
		super(new TCPIPSplitBinaryDecoder(is));
	}

	public TCPIPSplitBinaryElementInputStream(final byte[] buf, final int offset) {
		super(new TCPIPSplitBinaryDecoder(buf, offset));
	}

}
