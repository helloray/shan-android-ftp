package com.shanzha.ftp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharePreferenceπ§æﬂ¿‡
 * @author ShanZha
 * @date 2012-9-25 15:21
 *
 */
public abstract class SharePreferenceUtil {

	public static void setFtpIp(Context context,String ftpIP){
		SharedPreferences shared = context.getSharedPreferences("shanFtp", Context.MODE_PRIVATE);
		Editor edit = shared.edit();
		edit.putString("ftpip", ftpIP);
		edit.commit();
	}
	
	public static String getFtpIp(Context context){
		SharedPreferences shared = context.getSharedPreferences("shanFtp", Context.MODE_PRIVATE);
		return shared.getString("ftpip","");
	}
	
	public static void setUserName(Context context,String username){
		SharedPreferences shared = context.getSharedPreferences("shanFtp", Context.MODE_PRIVATE);
		Editor edit = shared.edit();
		edit.putString("username", username);
		edit.commit();
	}
	
	public static String getUserName(Context context){
		SharedPreferences shared = context.getSharedPreferences("shanFtp", Context.MODE_PRIVATE);
		return shared.getString("username","");
	}
	
	public static void setPassword(Context context,String password){
		SharedPreferences shared = context.getSharedPreferences("shanFtp", Context.MODE_PRIVATE);
		Editor edit = shared.edit();
		edit.putString("password", password);
		edit.commit();
	}
	
	public static String getPassword(Context context){
		SharedPreferences shared = context.getSharedPreferences("shanFtp", Context.MODE_PRIVATE);
		return shared.getString("password","");
	}
	
	public static void setFolderName(Context context,String foldername){
		SharedPreferences shared = context.getSharedPreferences("shanFtp", Context.MODE_PRIVATE);
		Editor edit = shared.edit();
		edit.putString("foldername", foldername);
		edit.commit();
	}
	
	public static String getFolderName(Context context){
		SharedPreferences shared = context.getSharedPreferences("shanFtp", Context.MODE_PRIVATE);
		return shared.getString("foldername","");
	}
}
