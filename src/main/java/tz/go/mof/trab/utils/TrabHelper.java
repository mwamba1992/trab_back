package tz.go.mof.trab.utils;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author GOTHOMIS-development team
 * @date Nov 11, 2019
 * @version 1.0.0
 */

public class TrabHelper {
	
	private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

	private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
	
	public static String findFinancialYear() {		
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int prevYear = Calendar.getInstance().get(Calendar.YEAR)-1;
		String currentYearInString = String.valueOf(currentYear);
		String prevYearInString = String.valueOf(prevYear);
		String financialYr=prevYearInString+"-"+currentYearInString;
		
		return financialYr;
	}

	public static boolean emailValidator(String email) {

		if (email == null) {
			return false;
		}

		Matcher matcher = EMAIL_PATTERN.matcher(email);
		return matcher.matches();
	}

	public static void print(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		String className = "";
		if (obj == null) {
			className = "You've passed null object";
		} else {
			className = obj.getClass().getSimpleName();
		}
		System.out.println("--------------------------" + className
				+ "----------------------------------------------------------");
		try {
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
		} catch (JsonProcessingException e) {
			System.out.println("########################ERROR########################");
			e.printStackTrace();
		}
		System.out.println(
				"-----------------------------------------------------------------------------------------------------");
	}

	public static void print(Object obj, String headerMsg) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			System.out.println("");
			System.out.println("");
			String className;
			if (obj == null) {
				className = "You've passed null object";
			} else {
				className = obj.getClass().getSimpleName();
			}

			String msg = "--------------------------" + className + " (" + headerMsg
					+ ")----------------------------------------------------------";

			System.out.println(msg);

			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));

		} catch (Exception e) {
			System.out.println("########################ERROR########################");
			e.printStackTrace();
		}

		System.out.println(
				"------------------------------  End of " + headerMsg + " ------------------------------------");
		System.out.println("");
		System.out.println("");
	}

	public static String resourceMessage(Object id) {
		return " resource-id=" + id.toString();
	}

	public static void printParams(Object... objects) {

		TrabHelper.print(objects, "List of parameters");

	}
	
	 /**
     * Copies properties from one object to another
     * @param source
     * @destination
     * @return
     */
    public static void copyNonNullProperties(Object source, Object destination){
		BeanUtils.copyProperties(source, destination,
				getNullPropertyNames(source));
    }
    /**
     * Returns an array of null properties of an object
     * @param source
     * @return
     */
    private static String[] getNullPropertyNames (Object source) {
	    final BeanWrapper src = new BeanWrapperImpl(source);
	    java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
 
	    Set emptyNames = new HashSet();
	    for(java.beans.PropertyDescriptor pd : pds) {
		//check if value of this property is null then add it to the collection
	        Object srcValue = src.getPropertyValue(pd.getName());
	        if (srcValue == null) emptyNames.add(pd.getName());
	    }
	    String[] result = new String[emptyNames.size()];
	    return (String[]) emptyNames.toArray(result);
	}
    
    public static boolean isNumeric(final String str) {

        // null or empty
        if (str == null || str.length() == 0) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;

    }

}
