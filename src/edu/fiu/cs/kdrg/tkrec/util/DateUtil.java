package edu.fiu.cs.kdrg.tkrec.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Liang Tang
 * @date Dec 27, 2013 4:48:24 PM
 */
public class DateUtil {

	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public static String getDate() {
		return sdf.format(new Date());
	}

	
	public static long getMilliseconds(String dateString) {
		try {
			return sdf.parse(dateString).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0l;
	}

}
