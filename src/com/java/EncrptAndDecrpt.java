package com.java;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class EncrptAndDecrpt {

	public static void main(String[] args) throws Exception {

		/*
		 * 50-7B-9D-FB-34-F4 44-85-00-B0-C2-AF 46-85-00-B0-C2-B0
		 * 46-85-00-B0-C2-AF 00-FF-10-B0-C3-0C/
		 */
		

		String password[] = { "3C-97-0E-CC-C3-19","FC-4D-D4-D8-68-D7","A4-1F-72-71-9C-C4","00-50-56-B8-76-E4","00-0C-29-35-93-79","FC-4D-D4-F2-5E-23" };

		Checker AESencrp = new Checker();
		String key = "";
		for (int i = 0; i < password.length; ++i) {

			String passwordEnc = AESencrp.encrypt(password[i]);
			String passwordDec = AESencrp.decrypt(passwordEnc);

			// System.out.println("Plain Text : " + password);
			System.out.println("Encrypted Text : " + passwordEnc);
			System.out.println("Decrypted Text : " + passwordDec);

			key += passwordEnc + "::";

		}
		System.out.print("Encrypted String to be kept in MacValidation.java : " + key);

	}
}

class Checker {

	 private static final String ALGO = "AES";
	    private static final byte[] keyValue = new byte[] { 'A', 'S', 'e', 'c', 'u', 'r', 'e', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };


	public static String encrypt(String Data) throws Exception {
	        Key key = generateKey();
	        Cipher c = Cipher.getInstance(ALGO);
	        c.init(Cipher.ENCRYPT_MODE, key);
	        byte[] encVal = c.doFinal(Data.getBytes());
	        String encryptedValue = new BASE64Encoder().encode(encVal);
	        return encryptedValue;
	    }

	    public static String decrypt(String encryptedData) throws Exception {
	        Key key = generateKey();
	        Cipher c = Cipher.getInstance(ALGO);
	        c.init(Cipher.DECRYPT_MODE, key);
	        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
	        byte[] decValue = c.doFinal(decordedValue);
	        String decryptedValue = new String(decValue);
	        return decryptedValue;
	    }
	    private static Key generateKey() throws Exception {
	        Key key = new SecretKeySpec(keyValue, ALGO);
	        return key;
	}

}
