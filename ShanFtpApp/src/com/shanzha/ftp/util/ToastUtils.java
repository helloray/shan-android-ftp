package com.shanzha.ftp.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast提示工具类
 * @author ShanZha
 * @date 2012-9-29 13:40
 */
public abstract class ToastUtils {

	/**
	 * 展示一个土司提示信息给用户
	 * @param context
	 * @param msg
	 * @param isLongTime
	 */
	public static void show(Context context,String msg,boolean isLongTime)
	{
		if(isLongTime)
		{
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}else
		{
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
}
