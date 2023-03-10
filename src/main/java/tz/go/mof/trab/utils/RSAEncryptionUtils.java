package tz.go.mof.trab.utils;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class RSAEncryptionUtils {

	public String getBase64EncodedPassword (byte[] password){
		return Base64.encodeBase64String(password);
	}
	
	public KeyPair getKyesGenerated() throws NoSuchAlgorithmException {
		
		System.out.println("Generating an RSA key...");
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.genKeyPair();
		System.out.println("Done generating the key.");
		
		return keyPair;
	}

	public String getRSAPKCSpaddingPassowrd(String password) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		byte[] bytePassword = password.getBytes();
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, getKyesGenerated().getPublic());
		byte[] cipherText = cipher.doFinal(bytePassword);
		
		String encodedPassword = getBase64EncodedPassword(cipherText);
		return encodedPassword;
	}
	
	public byte[] encrypt(PrivateKey privateKey, String pin) throws 
	NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		
		Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);  
        
        byte[] cipherText = cipher.doFinal(pin.getBytes());
        return cipherText;
	}
}
