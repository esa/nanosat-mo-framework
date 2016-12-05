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
