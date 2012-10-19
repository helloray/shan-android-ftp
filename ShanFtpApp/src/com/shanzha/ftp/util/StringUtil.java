package com.shanzha.ftp.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import android.text.TextUtils;
import android.util.MonthDisplayHelper;

/**
 * 字符串工具类
 * @author XingRongJing
 * @date 2012-8-29 15:41
 *
 */
public abstract class StringUtil {

	private static Hashtable<String, String> monthTable = new Hashtable<String, String>();
	/**
	 * 移除国家码
	 * @param number
	 * @return
	 */
	public static String removeCode(String number)
	{
		String num = "";
		if(!TextUtils.isEmpty((number)))
		{
			return "";
		}
		if(number.startsWith("+86"))
		{
			num = number.substring(3);
		}else if(number.startsWith("86"))
		{
			num =  number.substring(2);
		}else if(number.startsWith("+"))
		{
			num = number.substring(1);
		}else
		{
			num = number;
		}
		return num;
	}
	/**
	 * 移除字符串（orig）中包含所有的指定字符（character）
	 * @param orig
	 * @param character
	 * @return
	 */
	public static String removeCharacter(String orig,String character)
	{
		String res = "";
		if(!TextUtils.isEmpty(orig))
		{
			if(orig.contains(character))
			{
				res = orig.replaceAll(character, "");
			}else
			{
				res = orig;
			}
		} 
		return res;
	}
	/**
	 * 根据一个long类型时间，转为yyyy--MM--dd hh:mm格式字符串
	 * @param time
	 * @return
	 */
	public static String formatDate(long time,String format)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = new Date(time);
		return sdf.format(date);
	}
	 
	/**
	 * 根据完整文件路径获取文件名和文件目录
	 * @param path
	 * @return List<String> 第一项是filename,第二项是dir
	 */
	public static List<String> getFilenameAndDirByPath(String path)
	{
		if(TextUtils.isEmpty(path))
		{
			return null;
		}
		List<String> tempList = new ArrayList<String>();
		String filename = path.substring(path.lastIndexOf("/")+1, path.length());
		String dir = path.substring(0,path.lastIndexOf("/"));
		tempList.add(filename);
		tempList.add(dir);
		return tempList;
	}
	
	/**
	 * 根据英文的月份数获取数字月份
	 * @param en
	 * @return
	 */
	public static String getMonthByEn(String en)
	{
		String str = "";
		if(!TextUtils.isEmpty(en))
		{
			String lowEn = en.toLowerCase();
			if(monthTable.containsKey(lowEn))
			{
				str = monthTable.get(lowEn);
			}else
			{
				str = en;
			}
		}
		return str;
	}
	static
	{
		monthTable.put("jan", "1");
		monthTable.put("feb", "2");
		monthTable.put("mar", "3");
		monthTable.put("apr", "4");
		monthTable.put("may", "5");
		monthTable.put("june", "6");
		monthTable.put("july", "7");
		monthTable.put("aug", "8");
//		monthTable.put("sept", "9");
		monthTable.put("sep", "9");
		monthTable.put("oct", "10");
		monthTable.put("nov", "11");
		monthTable.put("dec", "12");
	}
}
