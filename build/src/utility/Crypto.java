package utility;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class Crypto {
	
	
	/**
	 * Encrypt with AES
	 * @param input Plaintext
	 * @param key AES Key
	 * @return Ciphertext
	 */
	public static String encrypt(String input, Key key) {
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, key); 
			byte[] output = c.doFinal(input.getBytes());
			return Base64.getEncoder().encodeToString(output);
		}catch(Exception e) {
			e.printStackTrace(); 
			return e.getMessage(); 
		}
	}
	
	/**
	 * Decrypt with AES
	 * @param input Ciphertext
	 * @param key AES Key
	 * @return Plaintext
	 */
	public static String decrypt(String input, Key key) {
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, key); 
			byte[] b64in = Base64.getDecoder().decode(input);
			byte[] output = c.doFinal(b64in);
			return new String(output);  
		}catch(Exception e) {
			e.printStackTrace(); 
			return e.getMessage(); 
		}
	}
	
	/**
	 * Generate an AES key from a 16 byte input
	 * @param key
	 * @return
	 */
	public static Key generateKey() {
		Random rand = new Random(); 
		byte[] ki = new byte[16]; 
		rand.nextBytes(ki); 
		return new SecretKeySpec(ki, "AES");
	}
	
	
	/**
	 * Helpful method to take a key and output it as base64. 
	 * @param k Key Object
	 * @return Base64 encoded key string
	 */
	public static String exportKeyAsBase64(Key k) {
		return Base64.getEncoder().encodeToString(k.getEncoded());
	}
	
	/**
	 * Helpful method to take a base64 encoded string and output a Key Object
	 * @param Base64 encoded key string
	 * @return Key Object
	 */
	public static Key importKeyFromBase64(String in) {
		return new SecretKeySpec(Base64.getDecoder().decode(in), "AES"); 
	}

}
