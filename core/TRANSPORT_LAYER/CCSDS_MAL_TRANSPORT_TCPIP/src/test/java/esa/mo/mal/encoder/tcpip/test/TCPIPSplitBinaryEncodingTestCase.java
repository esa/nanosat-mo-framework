package esa.mo.mal.encoder.tcpip.test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.junit.Before;
import org.junit.Test;

import esa.mo.mal.encoder.tcpip.TCPIPSplitBinaryEncoder;


public class TCPIPSplitBinaryEncodingTestCase {
	
	private TCPIPSplitBinaryEncoder enc = null;
	private ByteArrayOutputStream baos = null;

	@Before
	public void setUp() {
		
		baos = new ByteArrayOutputStream();
		enc = new TCPIPSplitBinaryEncoder(baos);		
	}
	
	@Test
	public void encodeULong0() {

		ULong long1 = new ULong(new BigInteger("1"));		
		byte[] output = null;
		
		try {
			
			// encode "0"
			enc.encodeULong(long1);
			enc.close();
			output = baos.toByteArray();
			assertEquals(2, output.length);
			assertEquals(1, output[1]);
			
		} catch (MALException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void encodeULongMax() {

		ULong longMax = new ULong(new BigInteger("FFFFFFFFFFFFFFFF", 16));		
		byte[] output = null;
		
		try {
			
			// encode unsigned long max
			enc.encodeULong(longMax);
			enc.close();
			output = baos.toByteArray();
			assertEquals(11, output.length);
			
			// output value is 0xFF FF FF FF FF FF FF FF FF 01
			assertEquals(0xFF, output[1] & 0xff);
			assertEquals(0xFF, output[2] & 0xff);
			assertEquals(0xFF, output[3] & 0xff);
			assertEquals(1, output[10]);
			
		} catch (MALException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void encodeSignedLongMax() {

		long value = Long.MAX_VALUE;		
		byte[] output = null;
		
		try {
			
			// encode unsigned long max
			enc.encodeLong(value);
			enc.close();
			output = baos.toByteArray();
			assertEquals(11, output.length);
			
			// output value is 0xFE FF FF FF FF FF FF FF FF 01 
			assertEquals(0xFE, output[1] & 0xff);
			assertEquals(0xFF, output[2] & 0xff);
			assertEquals(0xFF, output[3] & 0xff);
			assertEquals(1, output[10]);
			
		} catch (MALException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void encodeSignedLongMin() {

		long value = Long.MIN_VALUE;		
		byte[] output = null;
		
		try {
			
			// encode unsigned long min
			enc.encodeLong(value);
			enc.close();
			output = baos.toByteArray();
			assertEquals(11, output.length);
			
			// output value is 0xFF FF FF FF FF FF FF FF FF 01
			assertEquals(0xFF, output[1] & 0xff);
			assertEquals(0xFF, output[2] & 0xff);
			assertEquals(0xFF, output[3] & 0xff);
			assertEquals(1, output[10]);
			
		} catch (MALException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Encode signed int with value 0
	 * Encoded output should be 0x00
	 */
	@Test
	public void encodeSignedInt0() {

		int value = 0;
		byte[] output = null;
		
		try {
			
			enc.encodeInteger(value);
			enc.close();
			output = baos.toByteArray();
			assertEquals(2, output.length);
			
			// output value is 0
			assertEquals(0, output[1] & 0xff);
			
		} catch (MALException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Encode signed int with value -1
	 * Output should be: 0x01
	 */
	@Test
	public void encodeSignedIntMinus1() {

		int value = -1;
		byte[] output = null;
		
		try {
			
			enc.encodeInteger(value);
			enc.close();
			output = baos.toByteArray();
			assertEquals(2, output.length);
			
			// output value is 0
			assertEquals(0x01, output[1] & 0xff);
			
		} catch (MALException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Encode signed int with value 1
	 * Output should be: 0x02
	 */
	@Test
	public void encodeSignedInt1() {

		int value = 1;
		byte[] output = null;
		
		try {
			
			enc.encodeInteger(value);
			enc.close();
			output = baos.toByteArray();
			assertEquals(2, output.length);
			
			// output value is 0
			assertEquals(0x02, output[1] & 0xff);
			
		} catch (MALException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Encode signed int with value Integer.MAX_INT
	 * Output should be: 0xFE FF FF FF 0F 
	 */
	@Test
	public void encodeSignedIntMax() {

		int value = Integer.MAX_VALUE;
		byte[] output = null;
		
		try {
			
			// encode unsigned long max
			enc.encodeInteger(value);
			enc.close();
			output = baos.toByteArray();
			pbyte(output);
			assertEquals(6, output.length);
			
			// output value is 0
			assertEquals(0xFE, output[1] & 0xff);
			assertEquals(0xFF, output[2] & 0xff);
			assertEquals(0xFF, output[3] & 0xff);
			assertEquals(0xFF, output[4] & 0xff);
			assertEquals(0x0F, output[5] & 0xff);
			
		} catch (MALException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void ppl(String arg) {
		System.out.println(arg);
	}
	
	private void pp(String arg) {
		System.out.print(arg);
	}
	
	private void pbyte(byte[] arr) {
        for (int i = 0; i<arr.length; i++) {
            System.out.print(String.format("%02X ", arr[i]));
    	}
        System.out.println();
	}

}
