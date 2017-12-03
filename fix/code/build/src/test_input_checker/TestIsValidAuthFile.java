package test_input_checker;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import utility.InputChecker;

class TestIsValidAuthFile {

	@Test
	void testInvalidAuthFilePeriod() {
		String auth = "."; 
		assertEquals(false,InputChecker.isValidAuthFile(auth)); 
	}
	
	@Test
	void testInvalidAuthFileDoublePeriod() {
		String auth = ".."; 
		assertEquals(false,InputChecker.isValidAuthFile(auth)); 
	}
	
	@Test
	void testInvalidAuthFileEmpty() {
		String auth = ""; 
		assertEquals(false,InputChecker.isValidAuthFile(auth)); 
	}
	@Test
	void testInvalidAuthFileTooLarge() {
		StringBuilder s = new StringBuilder(); 
		for(int i = 0; i < 256; i++) {
			s.append("x"); 
		}
		assertEquals(false,InputChecker.isValidAuthFile(s.toString())); 
	}
	@Test
	void testValidAuthFileSize255() {
		StringBuilder s = new StringBuilder(); 
		for(int i = 0; i < 255; i++) {
			s.append("x"); 
		}
		assertEquals(true,InputChecker.isValidAuthFile(s.toString())); 
	}
	@Test
	void testValidAuthFileSize1() {
		String auth = "a"; 
		assertEquals(true,InputChecker.isValidAuthFile(auth)); 
	}
	@Test
	void testInvalidAuthFileQuote() {
		String auth = "\""; 
		assertEquals(false,InputChecker.isValidAuthFile(auth)); 
	}
	@Test
	void testValidAuthFileAllRegexItems() {
		String auth = "_-.1234567890abcdefghijklmnopqrstuvwxyz"; 
		assertEquals(true,InputChecker.isValidAuthFile(auth)); 
	}

	@Test
	void testInvalidAuthFileUppercase() {
		String auth = "ABCDEFJHIJKLMNOPQRSTUVWXYZ"; 
		assertEquals(false,InputChecker.isValidAuthFile(auth)); 
	}
}
