package utilities;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class EncryptionUtilities {


	
	public static String get_SHA_512_SecurePassword(String notEncryptedPassword) {
	    Random random = new Random();
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		//String saltAsString = new String(salt);
		String saltAsString = "a1._fis9";

		String generatedPassword = null;
	    try {
	         MessageDigest md = MessageDigest.getInstance("SHA-512");
	         md.update(saltAsString.getBytes(StandardCharsets.UTF_8));
	         byte[] bytes = md.digest(notEncryptedPassword.getBytes(StandardCharsets.UTF_8));
	         StringBuilder sb = new StringBuilder();
	         for(int i=0; i< bytes.length ;i++){
	            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	         }
	         generatedPassword = sb.toString();
	        } 
	       catch (NoSuchAlgorithmException e){
	        e.printStackTrace();
	       }
	    return generatedPassword;

	}
}
