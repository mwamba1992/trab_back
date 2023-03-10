/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tz.go.mof.trab.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.springframework.stereotype.Component;

/**
 *
 * @author Mayala Mwendesha
 */


@Component
public class MigsUtil {

	// Define Constants
	// ****************
	/*
	 * Note: ---- In a proper production environment, only the retrieving of all the
	 * input parameters and the HTML output would be in this file. The following
	 * constants and all other methods would be contained in a separate helper class
	 * so that users could not gain access to these values.
	 */
	// Variables
	// This is secret for encoding the SHA256 hash
	// This secret will vary from merchant to merchant

	public static final String SECURE_SECRET = "";

	private static final Logger mgisReport = Logger.getLogger("opg2.mgis.payment.notification");
	private static final Logger epayLog = Logger.getLogger("opg2.epay.general.notification");

	// This is an array for creating hex chars
	static final char[] HEX_TABLE = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	// ----------------------------------------------------------------------------
	/**
	 * This method is for sorting the fields and creating a SHA256 secure hash.
	 *
	 * @param fields
	 *            is a map of all the incoming hey-value pairs from the VPC
	 * @return is the hash being returned for comparison to the incoming hash
	 */
	public static String SHAhashAllFields(Map fields) {

		// hashKeys = "";
		// hashValues = "";
		// create a list and sort it
		List fieldNames = new ArrayList(fields.keySet());
		Collections.sort(fieldNames);

		// create a buffer for the SHA256 input
		StringBuilder buf = new StringBuilder();

		// iterate through the list and add the remaining field values
		Iterator itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) fields.get(fieldName);
			// hashKeys += fieldName + ", ";
			// if ((fieldValue != null) && (fieldValue.length() > 0)) {
			buf.append(fieldName).append("=").append(fieldValue);
			if (itr.hasNext()) {
				buf.append('&');
			}
			// }
		}

		byte[] mac = null;
		try {
			byte[] b = fromHexString(SECURE_SECRET, 0, SECURE_SECRET.length());
			SecretKey key = new SecretKeySpec(b, "HmacSHA256");
			Mac m = Mac.getInstance("HmacSHA256");
			m.init(key);
			// String values = new String(buf.toString(), "UTF-8");
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

	/*
	 * This method takes a byte array and returns a string of its contents
	 *
	 * @param input - byte array containing the input data
	 * 
	 * @return String containing the output String
	 */
	public static String hex(byte[] input) {
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

	// ----------------------------------------------------------------------------

	/*
	 * This method takes a data String and returns a predefined value if empty If
	 * data Sting is null, returns string "No Value Returned", else returns input
	 *
	 * @param in String containing the data String
	 * 
	 * @return String containing the output String
	 */
	public static String null2unknown(String in) {
		if (in == null || in.length() == 0) {
			return "No Value Returned";
		} else {
			return in;
		}
	} // null2unknown()

	// ----------------------------------------------------------------------------

	/*
	 * This function uses the returned status code retrieved from the Digital
	 * Response and returns an appropriate description for the code
	 *
	 * @param vResponseCode String containing the vpc_TxnResponseCode
	 * 
	 * @return description String containing the appropriate description
	 */
	public static String getResponseDescription(String vResponseCode) {

		String result = "";

		// check if a single digit response code
		if (vResponseCode.length() == 1) {

			// Java cannot switch on a string so turn everything to a char
			char input = vResponseCode.charAt(0);

			switch (input) {
			case '0':
				result = "Transaction Successful";
				break;
			case '1':
				result = "Transaction Declined";
				break;
			case '2':
				result = "Bank Declined Transaction";
				break;
			case '3':
				result = "No Reply from Bank";
				break;
			case '4':
				result = "Expired Card";
				break;
			case '5':
				result = "Insufficient Funds";
				break;
			case '6':
				result = "Error Communicating with Bank";
				break;
			case '7':
				result = "Merchant does not support currency";
				break;
			case '8':
				result = "Transaction Type Not Supported";
				break;
			case '9':
				result = "Bank declined transaction (Do not contact Bank)";
				break;
			case 'A':
				result = "Transaction Aborted";
				break;
			case 'B':
				result = "Transaction Declined - Contact the Bank";
				break;
			case 'C':
				result = "Transaction Cancelled";
				break;
			case 'D':
				result = "Deferred transaction has been received and is awaiting processing";
				break;
			case 'E':
				result = "Transaction Declined - Refer to card issuer";
				break;
			case 'F':
				result = "3-D Secure Authentication failed";
				break;
			case 'I':
				result = "Card Security Code verification failed";
				break;
			case 'L':
				result = "Shopping Transaction Locked (Please try the transaction again later)";
				break;
			case 'M':
				result = "Transaction Submitted (No response from acquirer)";
				break;
			case 'N':
				result = "Cardholder is not enrolled in Authentication scheme";
				break;
			case 'P':
				result = "Transaction has been received by the Payment Adaptor and is being processed";
				break;
			case 'R':
				result = "Transaction was not processed - Reached limit of retry attempts allowed";
				break;
			case 'S':
				result = "Duplicate SessionID";
				break;
			case 'T':
				result = "Address Verification Failed";
				break;
			case 'U':
				result = "Card Security Code Failed";
				break;
			case 'V':
				result = "Address Verification and Card Security Code Failed";
				break;
			case '?':
				result = "Transaction status is unknown";
				break;
			default:
				result = "Unable to be determined";
				break;
			}

			return result;
		} else {
			return "No Value Returned";
		}
	} // getResponseDescription()

	// ----------------------------------------------------------------------------
	/**
	 * This function uses the QSI AVS Result Code retrieved from the Digital Receipt
	 * and returns an appropriate description for this code.
	 *
	 * @param vAVSResultCode
	 *            String containing the vpc_AVSResultCode
	 * @return description String containing the appropriate description
	 */
	private String displayAVSResponse(String vAVSResultCode) {

		String result = "";
		if (vAVSResultCode != null || vAVSResultCode.length() == 0) {

			if (vAVSResultCode.equalsIgnoreCase("Unsupported")
					|| vAVSResultCode.equalsIgnoreCase("No Value Returned")) {
				result = "AVS not supported or there was no AVS data provided";
			} else {
				// Java cannot switch on a string so turn everything to a char
				char input = vAVSResultCode.charAt(0);

				switch (input) {
				case 'X':
					result = "Exact match - address and 9 digit ZIP/postal code";
					break;
				case 'Y':
					result = "Exact match - address and 5 digit ZIP/postal code";
					break;
				case 'S':
					result = "Service not supported or address not verified (international transaction)";
					break;
				case 'G':
					result = "Issuer does not participate in AVS (international transaction)";
					break;
				case 'C':
					result = "Street Address and Postal Code not verified for International Transaction due to incompatible formats.";
					break;
				case 'I':
					result = "Visa Only. Address information not verified for international transaction.";
					break;
				case 'A':
					result = "Address match only";
					break;
				case 'W':
					result = "9 digit ZIP/postal code matched, Address not Matched";
					break;
				case 'Z':
					result = "5 digit ZIP/postal code matched, Address not Matched";
					break;
				case 'R':
					result = "Issuer system is unavailable";
					break;
				case 'U':
					result = "Address unavailable or not verified";
					break;
				case 'E':
					result = "Address and ZIP/postal code not provided";
					break;
				case 'B':
					result = "Street Address match for international transaction. Postal Code not verified due to incompatible formats.";
					break;
				case 'N':
					result = "Address and ZIP/postal code not matched";
					break;
				case '0':
					result = "AVS not requested";
					break;
				case 'D':
					result = "Street Address and postal code match for international transaction.";
					break;
				case 'M':
					result = "Street Address and postal code match for international transaction.";
					break;
				case 'P':
					result = "Postal Codes match for international transaction but street address not verified due to incompatible formats.";
					break;
				case 'K':
					result = "Card holder name only matches.";
					break;
				case 'F':
					result = "Street address and postal code match. Applies to U.K. only.";
					break;
				default:
					result = "Unable to be determined";
					break;
				}
			}
		} else {
			result = "null response";
		}
		return result;
	}

	// ----------------------------------------------------------------------------
	/**
	 * This function uses the QSI CSC Result Code retrieved from the Digital Receipt
	 * and returns an appropriate description for this code.
	 *
	 * @param vCSCResultCode
	 *            String containing the vpc_CSCResultCode
	 * @return description String containing the appropriate description
	 */
	private String displayCSCResponse(String vCSCResultCode) {

		String result = "";
		if (vCSCResultCode != null || vCSCResultCode.length() == 0) {

			if (vCSCResultCode.equalsIgnoreCase("Unsupported")
					|| vCSCResultCode.equalsIgnoreCase("No Value Returned")) {
				result = "CSC not supported or there was no CSC data provided";
			} else {
				// Java cannot switch on a string so turn everything to a char
				char input = vCSCResultCode.charAt(0);

				switch (input) {
				case 'M':
					result = "Exact code match";
					break;
				case 'S':
					result = "Merchant has indicated that CSC is not present on the card (MOTO situation)";
					break;
				case 'P':
					result = "Code not processed";
					break;
				case 'U':
					result = "Card issuer is not registered and/or certified";
					break;
				case 'N':
					result = "Code invalid or not matched";
					break;
				default:
					result = "Unable to be determined";
				}
			}

		} else {
			result = "null response";
		}
		return result;
	}

	// ----------------------------------------------------------------------------
	/**
	 * This method uses the 3DS verStatus retrieved from the Response and returns an
	 * appropriate description for this code.
	 *
	 * @param vpc_VerStatus
	 *            String containing the status code
	 * @return description String containing the appropriate description
	 */
	static String getStatusDescription(String vStatus) {

		String result = "";
		if (vStatus != null && !vStatus.equals("")) {

			if (vStatus.equalsIgnoreCase("Unsupported") || vStatus.equals("No Value Returned")) {
				result = "3DS not supported or there was no 3DS data provided";
			} else {

				// Java cannot switch on a string so turn everything to a character
				char input = vStatus.charAt(0);

				switch (input) {
				case 'Y':
					result = "The cardholder was successfully authenticated.";
					break;
				case 'E':
					result = "The cardholder is not enrolled.";
					break;
				case 'N':
					result = "The cardholder was not verified.";
					break;
				case 'U':
					result = "The cardholder's Issuer was unable to authenticate due to some system error at the Issuer.";
					break;
				case 'F':
					result = "There was an error in the format of the request from the merchant.";
					break;
				case 'A':
					result = "Authentication of your Merchant ID and Password to the ACS Directory Failed.";
					break;
				case 'D':
					result = "Error communicating with the Directory Server.";
					break;
				case 'C':
					result = "The card type is not supported for authentication.";
					break;
				case 'S':
					result = "The signature on the response received from the Issuer could not be validated.";
					break;
				case 'P':
					result = "Error parsing input from Issuer.";
					break;
				case 'I':
					result = "Internal Payment Server system error.";
					break;
				default:
					result = "Unable to be determined";
					break;
				}
			}
		} else {
			result = "null response";
		}
		return result;
	}

	/**
	 * @author Joel M Gaitan
	 *
	 */
	public static HashMap<String, String> extractUrEncoded(String Data) {

		String params[] = Data.split("&");
		HashMap<String, String> words = new HashMap<>();

		int i = 0;

		for (String word : params) {
			words.put(word.substring(0, word.indexOf("=")), word.substring(word.indexOf("=") + 1, word.length()));
			i++;
		}

		return words;

	}

	/**
	 * @author Joel M Gaitan
	 *
	 */
	public static String mgisStatusCheck(Map data) throws Exception {
		String vpcQry = "https://migs.mastercard.com.au/vpcdps";
		// URL u = new URL(vpcQueryDrURL);
		URL u = new URL(vpcQry);
		StringBuffer buff = new StringBuffer();

		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		MigsUtil mgs = new MigsUtil();

		appendQueryrequestParams(buff, data);

		mgisReport.info("***sending status check to mgis******" + "\n" + buff + "\n" + "\n");

		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(buff.length()));
		OutputStream os = conn.getOutputStream();

		os.write(buff.toString().getBytes());

		InputStream instr = conn.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(instr));
		String lin;

		while ((lin = br.readLine()) != null) {
			mgisReport.info("***result from status check to mgis******" + "\n" + lin + "\n" + "\n");
			return lin;

		}
		return null;

	}

	public static void appendQueryrequestParams(StringBuffer buf, Map requestParams) {
		// create a list
		List fieldNames = new ArrayList(requestParams.keySet());
		Iterator itr = fieldNames.iterator();

		// move through the list and create a series of URL key/value pairs
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = requestParams.get(fieldName).toString();

			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				// append the URL parameters
				// URLEncoder.encode(fieldName, secret);
				buf.append(URLEncoder.encode(fieldName));
				buf.append('=');
				buf.append(URLEncoder.encode(fieldValue));
			}

			// add a '&' to the end if we have more requestParams coming.
			if (itr.hasNext()) {
				buf.append('&');
			}
		}
	}

	public static Map<String, String> migsCheckRequest(String controlNumber, String ccy) {

		Map<String, String> requestParams1 = new HashMap<String, String>();
		requestParams1.put("vpc_Version", "1");

		requestParams1.put("vpc_Command", "queryDR");
		requestParams1.put("vpc_MerchTxnRef", controlNumber);
	

		epayLog.info("CURRENCY" + ccy + controlNumber);
		if (ccy.equalsIgnoreCase("USD")) {
			requestParams1.put("vpc_AccessCode", "BCE69E24");
			requestParams1.put("vpc_Merchant", "GEPG002");
			requestParams1.put("vpc_User", "serekali02");
			requestParams1.put("vpc_Password", "Mayala753");
		} else if (ccy.equalsIgnoreCase("TZS")) {
			requestParams1.put("vpc_Merchant", "GEPG001");
			requestParams1.put("vpc_User", "gepg01");
			requestParams1.put("vpc_AccessCode", "7C2A7E6A");
			requestParams1.put("vpc_Password", "Mayala@098");
		}

		return requestParams1;
	}

	public static Map<String, String> readPairNameValueString(String npvString) {
		Map<String, String> allValues = new HashMap<String, String>();

		String values[] = npvString.split("&");

		for (String value : values) {
			String items[] = value.split("=");
			allValues.put(items[0], items[1]);

		}

		return allValues;

	}


}
