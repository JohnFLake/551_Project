package test_bank_server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import bank_server.Main;

class TestIsValidPortNumber {

	@Test
	void testValidPortNumber1024() {
		String num = "1024"; 
		assertEquals(true, Main.isValidPortNumber(num)); 
	}

	@Test
	void testInvalidPortNumber1023() {
		String num = "1023"; 
		assertEquals(false, Main.isValidPortNumber(num)); 
	}
	@Test
	void testValidPortNumber65535() {
		String num = "65535"; 
		assertEquals(true, Main.isValidPortNumber(num)); 
	}
	@Test
	void testInvalidPortNumber65536() {
		String num = "65536"; 
		assertEquals(false, Main.isValidPortNumber(num)); 
	}
	@Test
	void testInvalidPortNumber052() {
		String num = "052"; 
		assertEquals(false, Main.isValidPortNumber(num)); 
	}
	@Test
	void testInvalidPortNumber0x2A() {
		String num = "0x2A"; 
		assertEquals(false, Main.isValidPortNumber(num)); 
	}
}
