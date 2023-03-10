package tz.go.mof.trab.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tz.go.mof.trab.models.SystemUser;


public class Common {
	
	
	public static String encodePassword(String plainPassword) {
		
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(4);
		
		String encodedPassword=encoder.encode(plainPassword);
		
		return encodedPassword;
	}
	
	
	public static String generateRandomPassword(int len)
	{
		
		final String chars = "HgstNO#BCDYJ!KLMmRS*TUV@WXuv09d#EFGuv09$dEZ?a16no&efA78*bchj#k23wxyz45$pqr$PQ";

		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder();

	
		for (int i = 0; i < len; i++) {
			int randomIndex = random.nextInt(chars.length());
			sb.append(chars.charAt(randomIndex));
		}

		return sb.toString();
	}

	
	public String newAccountMessage(String names,String username,String password) {
		
		String message="Dear "+names+","+System.lineSeparator();
		message+="Your account was successfully created, to access your account use the following credentials :"+System.lineSeparator();
		message+=" Username : "+username+System.lineSeparator();
		message+=" Password : "+password;
		
		return message.trim();    
	}
	    
	public String changePassword(String names,String username,String password) {
		
		String message="Dear "+names+","+System.lineSeparator();
		message+="Your account was successfully updated, to access your account use the following credentials :"+System.lineSeparator();
		message+=" Username : "+username+System.lineSeparator();
		message+=" Password : "+password;
		
		return message.toString().trim();    
	}


}
