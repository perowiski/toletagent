package utility;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class HashUtil {
	public static String hash(String text) {
		return DigestUtils.sha256Hex(text);
	}
	
	public static String encodeBase64(String text) {
		return Base64.encodeBase64URLSafeString(text.getBytes());
	}
	
	public static String decodeBase64(String text) {
		return new String(Base64.decodeBase64(text.getBytes()));
	}
	
	public static void main(String[] args){
		try {
			String email = "seyi.ayeni@tolet.com.ng";
			String enc = encodeBase64(email);
			System.out.println(enc);
			
			String dec = decodeBase64(enc);
			System.out.println(dec);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}