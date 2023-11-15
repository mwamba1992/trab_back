package tz.go.mof.trab.utils;

/**
*
* @author CTPALAPALA &  Salum Shomvi
* @
* 
*/
import java.security.Signature;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import org.springframework.stereotype.Component;


/**
 *
 * @author  Joel Msungu Gaitan
 */
@Component
public class GePGGlobalSignature {

	private PrivateKey getPrivateKey(String keyPass, String keyAlias, String keyFilePath) throws Exception {

		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream is = new FileInputStream(keyFilePath);

		keyStore.load(is, keyPass.toCharArray());
		PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, keyPass.toCharArray());
		return privateKey;
	}

	public String CreateSignature(String content, String privateKeyPass, String privateKeyAlias,
			String privateKeyFilePath) throws Exception {

		byte[] data = content.getBytes();
		Signature sig = Signature.getInstance("SHA1withRSA");
		sig.initSign(getPrivateKey(privateKeyPass, privateKeyAlias, privateKeyFilePath));
		sig.update(data);
		byte[] signatureBytes = sig.sign();

		//String resultSig = new BASE64Encoder().encode(signatureBytes);
		// System.out.println(resultSig);
		
		//String resultSig = Base64.getEncoder().encodeToString(signatureBytes);
		String resultSig = org.apache.commons.codec.binary.Base64.encodeBase64String(signatureBytes);
		// System.out.println(resultSig);
		return resultSig;
	}

	private PublicKey getPublicKey(String keyPass, String keyAlias, String keyFilePath) throws Exception {

		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream is = new FileInputStream(keyFilePath);
		keyStore.load(is, keyPass.toCharArray());
		Certificate cert = keyStore.getCertificate(keyAlias);
		PublicKey publicKey = cert.getPublicKey();
		return publicKey;

	}

	public boolean verifySignature(String signature, String content, String publicKeyPass, String publicKeyAlias,
			String publicKeyFilePath) throws Exception {

		boolean t = false;
		try {
			byte db[] = org.apache.commons.codec.binary.Base64.decodeBase64(signature.getBytes());
			Signature sig = Signature.getInstance("SHA1withRSA");
			byte[] data = content.getBytes();
			sig.initVerify(getPublicKey(publicKeyPass, publicKeyAlias, publicKeyFilePath));
			sig.update(data);
			t = sig.verify(db);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return t;
	}

}
