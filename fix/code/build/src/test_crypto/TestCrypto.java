package test_crypto;

import static org.junit.jupiter.api.Assertions.*;

import java.security.Key;
import java.util.Random;

import org.junit.jupiter.api.Test;

import utility.Crypto;

class TestCrypto {

	@Test
	void testEncryptDecrypt() {
		Key k = Crypto.generateKey(); 
		System.out.println("Key value: " + Crypto.exportKeyAsBase64(k));
		String test = "hi my name is john"; 
		assertEquals(test,Crypto.decrypt(Crypto.encrypt(test, k), k)); 
	}
	
	@Test
	void testEncodeDecodeKey() {
		Key k = Crypto.generateKey(); 
		String s = Crypto.exportKeyAsBase64(k); 
		Key other = Crypto.importKeyFromBase64(s); 
		assertEquals(new String(k.getEncoded()),new String(other.getEncoded()));
	}

}
