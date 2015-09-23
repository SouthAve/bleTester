package com.xinzhongxin.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressLint("SimpleDateFormat")
public class DateUtil {
	
	/***
	 * 获得年月�? 时分�?
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrentDatatime() {
		SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}

	/***
	 * 获得�?
	 * 
	 * @param date
	 * @return
	 */
	public static int getYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);

	}

	/***
	 * 获得月份
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH) + 1;

	}
	
	/***
	 * 获得日期
	 * 
	 * @param date
	 * @return
	 */
	public static int getDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DATE);

	}

	public static String getMonth(Date date, Locale locale) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM", locale);
		return sdf.format(date);

	}

	/***
	 * 获得�?年中的第几周
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonthWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.WEEK_OF_MONTH);

	}

	/***
	 * 获得�?年中的第几周
	 * 
	 * @param date
	 * @return
	 */
	public static int getYearWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.WEEK_OF_YEAR);
	}

	public static String toDateYYYYMMDD(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}

	/***
	 * 年月yyyymm
	 * 
	 * @param date
	 * @return
	 */
	public static String toDateYYYYMM(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
		return df.format(date);
	}

	/***
	 * 加减日期
	 * 
	 * @param date
	 * @param addDay
	 * @return
	 */
	public static Date dateAddDay(Date date, int addDay) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, addDay);
		return c.getTime();
	}

	/***
	 * 加减�?
	 * 
	 * @param date
	 * @param addDay
	 * @return
	 */
	public static Date dateAddMonth(Date date, int addMonth) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, addMonth);
		return c.getTime();
	}
	
	/***
	 * 获得当年分钟
	 * 
	 * @param date
	 * @param addDay
	 * @return
	 */
	public static int getCurrentMinutes() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.HOUR_OF_DAY)*60+c.get(Calendar.MINUTE);
	}
}
