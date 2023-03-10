package tz.go.mof.trab.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;


/**
 *
 * @author Mayala Mwendesha
 */
@Controller
public class MigsPaymentAdapter {




	private static final Logger mgisReport = Logger.getLogger("opg2.mgis.payment.notification");
	private static final Logger erroLog = Logger.getLogger("opg2.error.notification");
	private static final Logger epayLog = Logger.getLogger("opg2.epay.general.notification");
	static final char[] HEX_TABLE = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	// ----------------------------------------------------------------------------
	String hashKeys = new String();
	String hashValues = new String();
	Properties pp = new Properties();

	private Model model;

	/**
	 * This method is for sorting the fields and creating a SHA256 secure hash.
	 *
	 * @param fields
	 *            is a map of all the incoming hey-value pairs from the VPC
	 * @param buf
	 *            is the hash being returned for comparison to the incoming hash
	 */
	public void appendQueryFields(StringBuffer buf, Map<String, String> fields) {

		// create a list
		List<String> fieldNames = new ArrayList(fields.keySet());
		Iterator<String> itr = fieldNames.iterator();

		// move through the list and create a series of URL key/value pairs
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = fields.get(fieldName).toString();

			if ((fieldValue != null) && (fieldValue.length() >= 0)) {
				// append the URL parameters
				// URLEncoder.encode(fieldName, secret);
				buf.append(URLEncoder.encode(fieldName));
				buf.append('=');
				buf.append(URLEncoder.encode(fieldValue));
			}

			// add a '&' to the end if we have more fields coming.
			if (itr.hasNext()) {
				buf.append('&');
			}
		}

	}

	public String SHAhashAllFields(Map<String, String> fields, String SECURE_SECRET) {

		hashKeys = "";
		hashValues = "";

		// create a list and sort it
		List fieldNames = new ArrayList(fields.keySet());
		Collections.sort(fieldNames);
		// create a buffer for the SHA256 input
		StringBuilder buf = new StringBuilder();

		// iterate through the list and add the remaining field values
		Iterator itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = fields.get(fieldName).toString();
			hashKeys += fieldName + ", ";
			if ((fieldValue != null) && (fieldValue.length() >= 0)) {
				buf.append(fieldName).append("=").append(fieldValue);
				if (itr.hasNext()) {
					buf.append('&');
				}
			}
			
		  
		}
	
	System.out.println("value after sorting: " + buf.toString());
	
		byte[] mac = null;
		try {
			byte[] b = fromHexString(SECURE_SECRET, 0, SECURE_SECRET.length());
			SecretKey key = new SecretKeySpec(b, "HmacSHA256");
			Mac m = Mac.getInstance("HmacSHA256");
			m.init(key);
			m.update(buf.toString().getBytes("ISO-8859-1"));
			mac = m.doFinal();
		} catch (UnsupportedEncodingException | IllegalStateException | InvalidKeyException
				| NoSuchAlgorithmException e) {

		}

		String hashValue = hex(mac);
		

		return hashValue;

	} // end hashAllFields()

	public static byte[] fromHexString(String s, int offset, int length) {
		if ((length % 2) != 0) {
			return null;
		}
		byte[] byteArray = new byte[length / 2];
		int j = 0;
		int end = offset + length;
		for (int i = offset; i < end; i += 2) {
			int high_nibble = Character.digit(s.charAt(i), 16);
			int low_nibble = Character.digit(s.charAt(i + 1), 16);
			if (high_nibble == -1 || low_nibble == -1) {
				// illegal format
				return null;
			}
			byteArray[j++] = (byte) (((high_nibble << 4) & 0xf0) | (low_nibble & 0x0f));
		}
		return byteArray;
	}
	// ----------------------------------------------------------------------------

	/**
	 * Returns Hex output of byte array
	 */
	static String hex(byte[] input) {
		// create a StringBuffer 2x the size of the hash array
		StringBuilder sb = new StringBuilder(input.length * 2);

		// retrieve the byte array data, convert it to hex
		// and add it to the StringBuffer
		for (int i = 0; i < input.length; i++) {
			sb.append(HEX_TABLE[(input[i] >> 4) & 0xf]);
			sb.append(HEX_TABLE[input[i] & 0xf]);
		}
		return sb.toString();
	}
}
