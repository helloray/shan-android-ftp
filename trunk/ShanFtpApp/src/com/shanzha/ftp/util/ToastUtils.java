package com.shanzha.ftp.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast��ʾ������
 * @author ShanZha
 * @date 2012-9-29 13:40
 */
public abstract class ToastUtils {

	/**
	 * չʾһ����˾��ʾ��Ϣ���û�
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
