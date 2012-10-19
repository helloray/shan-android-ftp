package com.shanzha.ftp.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/**
 * 文件工具类
 * 
 * @author ShanZha
 * @date 2012-10-9 10:04
 * 
 */
public abstract class FileUtil {

	private static final String TAG = "FileUtil";
	/** 项目存储根目录 **/
	public static final String PATH_PROJECT = "/shanFtp";
	/** 下载存储路径 **/
	public static final String PATH_DOWNLOAD = PATH_PROJECT + "/download";
	/** 下载存储路径--图片 **/
	public static final String PATH_PIC = PATH_DOWNLOAD + "/pic";
	/** 下载存储路径--音频 **/
	public static final String PATH_AUDIO = PATH_DOWNLOAD + "/audio";
	/** 下载存储路径--视频 **/
	public static final String PATH_VIDEO = PATH_DOWNLOAD + "/video";
	/** 下载存储路径--其他 **/
	public static final String PATH_OTHER = PATH_DOWNLOAD + "/other";
	/** 上传存储路径（主要是用于存储联系人以及信息文件） **/
	public static final String PATH_UPLOAD = PATH_PROJECT + "/upload";
	/** 保存短信的本地文件名 **/
	public static final String SMS_FILENAME = "sms.txt";
	/** 保存联系人的本地文件名 **/
	public static final String CONTACT_FILENAME = "contact.txt";

	/**
	 * sdcard是否存在并可用
	 * 
	 * @return
	 */
	public static boolean isSdcardExist() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取sdcard路径
	 * 
	 * @return
	 */
	public static String getSdcardPath() {
		if (isSdcardExist()) {
			return Environment.getExternalStorageDirectory().getPath();
		}
		return "";
	}

	/**
	 * 获取sdcard的父路劲
	 * @return
	 */
	public static String getSdcardParentPath()
	{
		if(isSdcardExist())
		{
			return Environment.getExternalStorageDirectory().getParent();
		}
		return "";
	}
	
	/**
	 * 根据文件名获取本地存储路径
	 * 
	 * @param filename
	 * @return
	 */
	public static String getStoragePathByFilename(String filename) {
		String type = getMIMEType(filename);
		if (type.indexOf("image/") != -1) {
			return getSdcardPath() + PATH_PIC;
		} else if (type.indexOf("audio/") != -1) {
			return getSdcardPath() + PATH_AUDIO;
		} else if (type.indexOf("video/") != -1) {
			return getSdcardPath() + PATH_VIDEO;
		} else {
			return getSdcardPath() + PATH_OTHER;
		}
	}

	/**
	 * 根据一个文件名判断是否是图片文件
	 * @param filename
	 * @return
	 */
	public static boolean isPic(String filename)
	{
		if(TextUtils.isEmpty(filename))
		{
			return false;
		}
		String type = getMIMEType(filename);
		if(type.indexOf("image/")!=-1)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 根据文件名判断此文件在本地是否已经存在（指定目录下）
	 * 
	 * @param filename
	 * @param dir
	 * @return
	 */
	public static boolean isExist(String filename, String dir) {
		boolean isExist = false;
		if (!TextUtils.isEmpty(filename)) {
			File file = new File(dir);
			Log.i(TAG, "dir=" + dir + " filename=" + filename);
			String[] filenames = file.list();
			if (null != filenames) {
				for (int i = 0; i < filenames.length; i++) {
					String tempname = filenames[i];
					Log.i(TAG, "本地此目录下已经存在 " + tempname);
					if (filename.equals(tempname)) {
						isExist = true;
						break;
					}
				}
			}
		}
		return isExist;
	}

	/**
	 * 默认写一个文件到sdcard上
	 * 
	 * @param fileName
	 */
	public static String writeToFile(String dir, String fileName, String content)
			throws FileNotFoundException, IOException {
		if(!isSdcardExist())
		{
			throw new IOException("sdcard 不可用");
		}
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File file = new File(dir + Constant.SEPERATOR + fileName);
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos, 10 * 1024);
			bos.write(content.getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new FileNotFoundException("文件没有找到 " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException("IO异常 " + e.getMessage());
		} finally {
			try {
				if (null != bos) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new IOException("IO异常 " + e.getMessage());
			}
		}
		return file.getPath();
		// return file.getParent();
	}
	
	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return
	 */
	public static String formatFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * 
	 * @param file
	 */
	public static String getMIMEType(String filename) {
		String type = "*/*";
		// String fName=file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = filename.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = filename.substring(dotIndex, filename.length())
				.toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	public static final String[][] MIME_MapTable = {
			// {后缀名， MIME类型}
			{ ".3gp", "video/3gpp" }, { ".3gpp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" }, { ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" }, { ".bmp", "image/bmp" },
			{ ".c", "text/plain" }, { ".class", "application/octet-stream" },
			{ ".conf", "text/plain" }, { ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".exe", "application/octet-stream" }, { ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" }, { ".gz", "application/x-gzip" },
			{ ".h", "text/plain" }, { ".htm", "text/html" },
			{ ".html", "text/html" }, { ".jar", "application/java-archive" },
			{ ".java", "text/plain" }, { ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" }, { ".js", "application/x-javascript" },
			{ ".log", "text/plain" }, { ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" }, { ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" }, { ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" }, { ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" }, { ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" }, { ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" }, { ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" }, { ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" }, { ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".prop", "text/plain" },
			{ ".rar", "application/x-rar-compressed" },
			{ ".rc", "text/plain" }, { ".rmvb", "audio/x-pn-realaudio" },
			{ ".rtf", "application/rtf" }, { ".sh", "text/plain" },
			{ ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/xml" },
			{ ".xml", "text/plain" }, { ".z", "application/x-compress" },
			{ ".zip", "application/zip" }, { "", "*/*" } };
}
