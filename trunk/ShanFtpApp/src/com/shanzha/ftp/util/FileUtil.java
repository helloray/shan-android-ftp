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
 * �ļ�������
 * 
 * @author ShanZha
 * @date 2012-10-9 10:04
 * 
 */
public abstract class FileUtil {

	private static final String TAG = "FileUtil";
	/** ��Ŀ�洢��Ŀ¼ **/
	public static final String PATH_PROJECT = "/shanFtp";
	/** ���ش洢·�� **/
	public static final String PATH_DOWNLOAD = PATH_PROJECT + "/download";
	/** ���ش洢·��--ͼƬ **/
	public static final String PATH_PIC = PATH_DOWNLOAD + "/pic";
	/** ���ش洢·��--��Ƶ **/
	public static final String PATH_AUDIO = PATH_DOWNLOAD + "/audio";
	/** ���ش洢·��--��Ƶ **/
	public static final String PATH_VIDEO = PATH_DOWNLOAD + "/video";
	/** ���ش洢·��--���� **/
	public static final String PATH_OTHER = PATH_DOWNLOAD + "/other";
	/** �ϴ��洢·������Ҫ�����ڴ洢��ϵ���Լ���Ϣ�ļ��� **/
	public static final String PATH_UPLOAD = PATH_PROJECT + "/upload";
	/** ������ŵı����ļ��� **/
	public static final String SMS_FILENAME = "sms.txt";
	/** ������ϵ�˵ı����ļ��� **/
	public static final String CONTACT_FILENAME = "contact.txt";

	/**
	 * sdcard�Ƿ���ڲ�����
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
	 * ��ȡsdcard·��
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
	 * ��ȡsdcard�ĸ�·��
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
	 * �����ļ�����ȡ���ش洢·��
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
	 * ����һ���ļ����ж��Ƿ���ͼƬ�ļ�
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
	 * �����ļ����жϴ��ļ��ڱ����Ƿ��Ѿ����ڣ�ָ��Ŀ¼�£�
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
					Log.i(TAG, "���ش�Ŀ¼���Ѿ����� " + tempname);
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
	 * Ĭ��дһ���ļ���sdcard��
	 * 
	 * @param fileName
	 */
	public static String writeToFile(String dir, String fileName, String content)
			throws FileNotFoundException, IOException {
		if(!isSdcardExist())
		{
			throw new IOException("sdcard ������");
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
			throw new FileNotFoundException("�ļ�û���ҵ� " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException("IO�쳣 " + e.getMessage());
		} finally {
			try {
				if (null != bos) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new IOException("IO�쳣 " + e.getMessage());
			}
		}
		return file.getPath();
		// return file.getParent();
	}
	
	/**
	 * ת���ļ���С
	 * 
	 * @param fileS
	 * @return
	 */
	public static String formatFileSize(long fileS) {// ת���ļ���С
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
	 * �����ļ���׺����ö�Ӧ��MIME���͡�
	 * 
	 * @param file
	 */
	public static String getMIMEType(String filename) {
		String type = "*/*";
		// String fName=file.getName();
		// ��ȡ��׺��ǰ�ķָ���"."��fName�е�λ�á�
		int dotIndex = filename.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* ��ȡ�ļ��ĺ�׺�� */
		String end = filename.substring(dotIndex, filename.length())
				.toLowerCase();
		if (end == "")
			return type;
		// ��MIME���ļ����͵�ƥ������ҵ���Ӧ��MIME���͡�
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	public static final String[][] MIME_MapTable = {
			// {��׺���� MIME����}
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
