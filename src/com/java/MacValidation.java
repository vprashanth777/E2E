package com.java;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.Key;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;

public class MacValidation {

	final private String MacAddress ="50LZZDWMJzZdk53XHbZ3imDMsF/iiSgjU9ZrOqMgw/Y=::6hoVWvH5Hcz+TrTG5Vi9+Iygm2JLesBpkriJqdBF8eM=::raDHQ+cdWtUvp1rllgyuJSyxv1Jj1+npgA+KlCTMb4w=::jWX3ei+UHSls79u/ylOmJyyxv1Jj1+npgA+KlCTMb4w=::So7HpgDnrdtPLeZJYj5T3mDMsF/iiSgjU9ZrOqMgw/Y=::xOXU/UEFeOB23Y13oH0Lm0EBBNagsy6eB2dVISN0B+U=::";

	private HashMap<String, String> macDetails = new HashMap();

	public HashMap<String, String> validate() throws Exception {

		InetAddress hostAddress;
		try {

			hostAddress = InetAddress.getLocalHost();

			NetworkInterface network = NetworkInterface.getByInetAddress(hostAddress);

			byte[] mac = network.getHardwareAddress();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}

			for (int i = 0; i < MacAddress.split("::").length; i++) {
				if (decrypt(MacAddress.split("::")[i]).equalsIgnoreCase(sb.toString())) {

					macDetails.put("ActualdMacAddress", sb.toString());
					macDetails.put("status", "true");
					return macDetails;

				}
				if (!(i < MacAddress.split("::").length - 1)) {
					macDetails.put("ActualdMacAddress", sb.toString());
					macDetails.put("status", "false");
					RunTest.exit_status = 5;
					return macDetails;
				}
			}

		} catch (UnknownHostException e) {

			e.printStackTrace();

		} catch (SocketException e) {

			e.printStackTrace();

		}
		return macDetails;
	}

	private static String decrypt(String encryptedText) throws Exception {
		// generate key
		Key key = generateKey();
		Cipher chiper = Cipher.getInstance(algorithm);
		chiper.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedText);
		byte[] decValue = chiper.doFinal(decordedValue);
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}

	private static Key generateKey() throws Exception {
		Key key = new SecretKeySpec(keyValue, algorithm);
		return key;
	}

	private static String algorithm = "AES";
	private static byte[] keyValue = new byte[] { 'A', 'S', 'e', 'c', 'u', 'r', 'e', 'S', 'e', 'c', 'r', 'e', 't', 'K',
			'e', 'y' };
}