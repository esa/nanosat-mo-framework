package esa.mo.mal.encoder.tcpip.test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.junit.Before;
import org.junit.Test;

import esa.mo.mal.encoder.tcpip.TCPIPSplitBinaryDecoder;
import esa.mo.mal.encoder.tcpip.TCPIPSplitBinaryEncoder;

public class TCPIPSplitBinaryDecodingTestCase {
	
	
	private TCPIPSplitBinaryDecoder dec = null;
	
	private static final long UINT_MAX_VALUE = 4294967295L;

	@Before
	public void setUp() {
				
	}

	@Test
	public void decodeUOctet() {

		try {
			
			dec = new TCPIPSplitBinaryDecoder(new byte[] {0x00, 0x02});	
			UOctet result = dec.decodeUOctet();
			assertEquals(2, result.getValue());
			
			dec = new TCPIPSplitBinaryDecoder(new byte[] {0x00, 0x00});	
			result = dec.decodeUOctet();
			assertEquals(0, result.getValue());
			
			dec = new TCPIPSplitBinaryDecoder(new byte[] {0x00, (byte) 0xFF});	
			result = dec.decodeUOctet();
			assertEquals(255, result.getValue());
			
		} catch (MALException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Test 
	public void decodeUInteger() {
		
		try {
			
			// decode value 2
			dec = new TCPIPSplitBinaryDecoder(new byte[] {0x00, 0x02});	
			UInteger result = dec.decodeUInteger();
			assertEquals(2, result.getValue());
			
			// decode value 0
			dec = new TCPIPSplitBinaryDecoder(new byte[] {0x00, 0x00});	
			result = dec.decodeUInteger();
			assertEquals(0, result.getValue());
			
			// decode value 2097151
			dec = new TCPIPSplitBinaryDecoder(new byte[] {0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0x7F});	
			result = dec.decodeUInteger();
			assertEquals(2097151, result.getValue());

			// decode unsigned int max value (4.2B)
			dec = new TCPIPSplitBinaryDecoder(new byte[] {0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x0F});	
			result = dec.decodeUInteger();
			assertEquals(UINT_MAX_VALUE, result.getValue());
			
			// add extra garbage bit, decode unsigned int max value
			dec = new TCPIPSplitBinaryDecoder(new byte[] {0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x0F, (byte) 0x80});	
			result = dec.decodeUInteger();
			assertEquals(UINT_MAX_VALUE, result.getValue());
			
		} catch (MALException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
