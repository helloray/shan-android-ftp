package com.shanzha.ftp.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

/**
 * Dialog工具类
 * @author ShanZha
 * @date 2012-9-25 14:29
 *
 */
public abstract class DialogUtil {

	/**
	 * 创建一个一般的dialog，包括两个按钮（确定和取消）
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
		builder.setPositiveButton("确定", listener);
		builder.setNegativeButton("取消", listener);
		return builder.create();
	}
	/**
	 * 生成一个垂直的ProgressDialog
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
	 * 生成一个水平的ProgressDialog
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
	 * 展示一个ProgressDialog
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
	 * 消失一个ProgressDialog
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
