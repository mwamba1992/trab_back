package tz.go.mof.trab.utils;

import java.io.File;
import java.security.MessageDigest;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.uuid.Generators;



/**
 * @author termis-development team
 * @date June 2, 2020
 * @version 1.0.0
 */
public final class CustomGeneratedData {

	private static final Logger logger = LoggerFactory.getLogger(CustomGeneratedData.class);

	public static String GenerateUniqueID() {
		UUID uuid = Generators.timeBasedGenerator().generate();
		String uuidStr = uuid.toString().replace("-", "");
		return uuidStr;
	}

	public static long getTimeStamp() {

		return new Date().getTime();
	}

	public static LocalDate stringToLocalDate(String formDate) {
		LocalDate convertedDate = LocalDate.parse(formDate);
		return convertedDate;
	}

	public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static LocalDateTime convertToLocalDateTimeViaMilisecond(Date dateToConvert) {
		return Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static LocalDateTime getLocalDate(long valueToConvert) {
		Date currentDate = new Date(valueToConvert);
		return currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static LocalDate stringToDate(String dateToConvert) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return LocalDate.parse(dateToConvert, formatter);
	}

	public static LocalDate stringToLocalDateFormat(String dateToConvert) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return LocalDate.parse(dateToConvert, formatter);
	}

	public static LocalDate stringToDateFormat(String dateToConvert) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[dd-MM-yyyy][yyyy-MM-dd]");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		LocalDate receivedDate = LocalDate.parse(dateToConvert, formatter);
		String returnDateStr = receivedDate.format(formatter2);

		return LocalDate.parse(returnDateStr, formatter2);
	}

	public static List<?> convertObjectToList(Object obj) {
		List<?> list = new ArrayList<>();
		if (obj.getClass().isArray()) {
			list = Arrays.asList((Object[]) obj);
		} else if (obj instanceof Collection) {
			list = new ArrayList<>((Collection<?>) obj);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static boolean isObjectHashMap(Object source) {

		try {
			HashMap<String, Object> result = (HashMap<String, Object>) source;
			return true;
		} catch (ClassCastException e) {
		}

		return false;

	}

	public static boolean isObjectString(Object source) {

		try {
			//String result = (String) source;
			return true;
		} catch (ClassCastException e) {
		}

		return false;

	}

	public static String harshMethod(String string) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(string.getBytes());

		byte[] byteData = md.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xFF) + 256, 16).substring(1));
		}
		return sb.toString();
	}
	
	public static String convertFileToBase64(File file) {
		String contentToSend = null;
		try {
			if (file.exists()) {
			byte[] fileContent = FileUtils.readFileToByteArray(file);
			 contentToSend = Base64.getEncoder().encodeToString(fileContent);
			}else {
			 contentToSend = Constant.FILE_NOT_EXIST.toString();
			}
		}catch (Exception e) {
			logger.error("Error ocured while accessing the file",e);
		}
		return contentToSend;
	}

}
