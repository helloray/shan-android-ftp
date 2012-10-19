package com.shanzha.ftp.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

/**
 * Dialog������
 * @author ShanZha
 * @date 2012-9-25 14:29
 *
 */
public abstract class DialogUtil {

	/**
	 * ����һ��һ���dialog������������ť��ȷ����ȡ����
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param listener
	 * @return
	 */
	public static AlertDialog getCommonDialg(Context context, String title,
			String msg, OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton("ȷ��", listener);
		builder.setNegativeButton("ȡ��", listener);
		return builder.create();
	}
	/**
	 * ����һ����ֱ��ProgressDialog
	 * @param context
	 * @param title
	 * @param msg
	 * @return
	 */
	public static ProgressDialog getWaittingProDialog(Context context,String title
			,String msg)
	{
		ProgressDialog pd = new ProgressDialog(context);
		pd.setTitle(title);
		pd.setMessage(msg);
		return pd;
	}
	/**
	 * ����һ��ˮƽ��ProgressDialog
	 * @param context
	 * @param title
	 * @param msg
	 * @return
	 */
	public static ProgressDialog getHorizontalWaittingProDialog(Context context,String title
			,String msg)
	{
		ProgressDialog pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setTitle(title);
		pd.setMessage(msg);
		return pd;
	}
	/**
	 * չʾһ��ProgressDialog
	 * @param pd
	 */
	public static void showWaittingDialog(ProgressDialog pd)
	{
		if(null!=pd&&!pd.isShowing())
		{
			pd.show();
		}
	}
	/**
	 * ��ʧһ��ProgressDialog
	 * @param pd
	 */
	public static void dismissWaittingDialog(ProgressDialog pd)
	{
		if(null!=pd&&pd.isShowing())
		{
			pd.dismiss();
		}
	}
}
