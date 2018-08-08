package com.ruizton.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruizton.main.model.Fvirtualcointype;


public class Utils {
	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	public static String wget(String u) throws Exception {
		URL url = new URL(u);
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		StringBuffer content = new StringBuffer();
		String tmp = null;

		while ((tmp = br.readLine()) != null) {
			content.append(tmp);
		}
		br.close();
		return content.toString();
	}

	// 获得随机字符串
	public static String randomString(int count) {
		String str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
		int size = str.length();
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		while (count > 0) {
			sb.append(String.valueOf(str.charAt(random.nextInt(size))));
			count--;
		}
		return sb.toString();
	}

	public static String randomInteger(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(new Random().nextInt(10));
		}
		return sb.toString();
	}

	public static String getRandomImageName() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmsss");
		String path = simpleDateFormat.format(new Date());
		path += "_" + randomString(5);
		return path;
	}

	public static boolean saveFile(String dir, String fileName, InputStream inputStream) {
		boolean flag = false;
		File directory = new File(dir);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		if (!directory.isDirectory()) {
			log.debug("Not a directory!");
			return flag;
		}
		if (!directory.exists()) {
			directory.mkdirs();
		}

		if (inputStream == null) {
			log.debug("InputStream null.");
			return flag;
		}

		File realFile = new File(directory, fileName);
		FileOutputStream fileOutputStream = null;
		int tmp = 0;
		try {
			fileOutputStream = new FileOutputStream(realFile);
			while ((tmp = inputStream.read()) != -1) {
				fileOutputStream.write(tmp);
			}

			if (fileOutputStream != null) {
				fileOutputStream.close();
			}

			if (inputStream != null) {
				inputStream.close();
			}

			flag = true;

		} catch (Exception e) {
			log.debug("Read InputStream fail.");
		} finally {
			fileOutputStream = null;
			inputStream = null;
		}

		return flag;
	}
	//
	// public static String MD5(String content) throws Exception {
	// MessageDigest md5 = MessageDigest.getInstance("MD5");
	// sun.misc.BASE64Encoder baseEncoder = new sun.misc.BASE64Encoder();
	// String retString = baseEncoder.encode(md5.digest(content.getBytes()));
	// return retString;
	// }

	public static String MD5(String content) throws Exception {
		// content = "%#97"+content+"242";
		// MessageDigest md5 = MessageDigest.getInstance("MD5");
		// sun.misc.BASE64Encoder baseEncoder = new sun.misc.BASE64Encoder();
		// String retString =
		// baseEncoder.encode(md5.digest(content.getBytes()));
		return getMD5_32(content);
	}

	public static String getMD5_32(String str) throws Exception {
		str = "%#97" + str + "242";
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		Base64.Encoder baseEncoder = Base64.getEncoder();
		String retString = baseEncoder.encodeToString(md5.digest(str.getBytes()));
		return retString;
	}

	public static String getCookie(Cookie[] cookies, String key) throws Exception {
		String value = null;
		if (cookies != null && key != null) {
			for (Cookie cookie : cookies) {
				if (key.equals(cookie.getName())) {
					value = cookie.getValue();
				}
			}
		}

		return value;
	}

	public static Timestamp getTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	public static int getNumPerPage() {
		return 40;
	}

	public static synchronized String UUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	// return seconds
	public static long timeMinus(Timestamp t1, Timestamp t2) {
		return (t1.getTime() - t2.getTime()) / 1000;
	}

	// 获得今天0点
	public static long getTimesmorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	public static String getCurTimeString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	public static String number2String(double f) {
		DecimalFormat df = new DecimalFormat();
		String style = "0.0000";// 定义要显示的数字的格式
		df.applyPattern(style);
		return df.format(f);
	}

	public static void main(String args[]) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(Long.valueOf(1473490800000L));
		System.out.println(sdf.format(date));
		// System.out.println(new Date(Long.valueOf("1410250997956")));
		// System.out.println(getTimesmorning());
		// System.out.println(new
		// Timestamp(1401183780000L));2f9924d920dd168bd8f8eaa16a7d7989
		System.out.println(getMD5_32("admin123"));
	}

	public static double getDouble(String val, int scale) {
		if (val == null)
			return 0d;
		double value = Double.parseDouble(val);
		String a = "";
		for (int i = 0; i < scale; i++) {
			a = a + "#";
		}
		DecimalFormat s = new DecimalFormat("###." + a);
		return Double.valueOf(s.format(value));
	}

	public static double getDouble(double value, int scale) {
		if (value == 0)
			return 0d;
		String a = "";
		for (int i = 0; i < scale; i++) {
			a = a + "#";
		}
		DecimalFormat s = new DecimalFormat("###." + a);
		return Double.valueOf(s.format(value));
	}
/**
 * 默认保留4位小数
 * @param value
 * @param pattern 格式 例如 ##0.####
 * @return
 */
	public static String getDouble(double value,String pattern) {
		if( StringUtils.isEmpty(pattern))
		{
			pattern="##0.####";
		}
		DecimalFormat decimalFormat = new DecimalFormat(pattern);// 格式化设置
		return decimalFormat.format(value);
	}

	/**
	 * 保留scale位小数 不四舍五入
	 * 
	 * @param value
	 * @param scale
	 * @return
	 */
	public static double getSubDouble(double value, int scale) {
		if (value == 0)
			return 0d;
		String a = Double.toString(value);
		int index = a.indexOf('.');
		if (index != -1 & a.length() >= index + 1 + scale) {
			a = a.substring(0, index + 1 + scale);
		}
		return Double.valueOf(a);
	}

	public static String dateFormat(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(timestamp);
	}

	public static boolean isNumeric(String str) {
		if (str == null || str.trim().length() == 0)
			return false;
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "01234567890123456789012345678901234567890123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 功能：设置地区编码
	 * 
	 * @return Hashtable 对象
	 */
	public static Hashtable getAreaCode() {
		Hashtable hashtable = new Hashtable();
		hashtable.put("11", "北京");
		hashtable.put("12", "天津");
		hashtable.put("13", "河北");
		hashtable.put("14", "山西");
		hashtable.put("15", "内蒙古");
		hashtable.put("21", "辽宁");
		hashtable.put("22", "吉林");
		hashtable.put("23", "黑龙江");
		hashtable.put("31", "上海");
		hashtable.put("32", "江苏");
		hashtable.put("33", "浙江");
		hashtable.put("34", "安徽");
		hashtable.put("35", "福建");
		hashtable.put("36", "江西");
		hashtable.put("37", "山东");
		hashtable.put("41", "河南");
		hashtable.put("42", "湖北");
		hashtable.put("43", "湖南");
		hashtable.put("44", "广东");
		hashtable.put("45", "广西");
		hashtable.put("46", "海南");
		hashtable.put("50", "重庆");
		hashtable.put("51", "四川");
		hashtable.put("52", "贵州");
		hashtable.put("53", "云南");
		hashtable.put("54", "西藏");
		hashtable.put("61", "陕西");
		hashtable.put("62", "甘肃");
		hashtable.put("63", "青海");
		hashtable.put("64", "宁夏");
		hashtable.put("65", "新疆");
		hashtable.put("71", "台湾");
		hashtable.put("81", "香港");
		hashtable.put("82", "澳门");
		hashtable.put("91", "国外");
		return hashtable;
	}

	public static String getAfterDay(int day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -day);
		Date monday = c.getTime();
		String preMonday = sdf.format(monday);
		return preMonday;
	}

	/**
	 * 功能：判断字符串是否为日期格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDate(String strDate) {
		Pattern pattern = Pattern.compile(
				"^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher m = pattern.matcher(strDate);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 得到本周周一
	 * 
	 * @return yyyy-MM-dd
	 */
	public static String getMondayOfThisWeek(int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, days);
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 1);
		return sdf.format(c.getTime());
	}

	/**
	 * 得到本周周日
	 * 
	 * @return yyyy-MM-dd
	 */
	public static String getSundayOfThisWeek(int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, days);
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 7);
		return sdf.format(c.getTime());
	}

	public static boolean openTrade(Fvirtualcointype type) {
		Timestamp now = Utils.getTimestamp();
		// 精确到小时
		// int nows = Integer.parseInt(new SimpleDateFormat("HH").format(now));
		// 精确到分
		double nows = Double.parseDouble(new SimpleDateFormat("HH.mm").format(now));
		boolean flag = true;
		String value = type.getFtradetime();
		double min = Double.parseDouble(value.trim().split("-")[0]);
		double max = Double.parseDouble(value.trim().split("-")[1]);
		// 24-0代表24小时，0-24代表不开放交易
		if (min == 0 && max == 24) {
			return false;
		}
		if (min == 24 && max == 0) {
			return true;
		}

		if (min <= max) {
			if (nows >= min && nows <= max) {
				flag = false;
			}
		}

		if (max < min) {
			if (!(nows > max && nows < min)) {
				flag = false;
			}
		}

		return flag;
	}

	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
