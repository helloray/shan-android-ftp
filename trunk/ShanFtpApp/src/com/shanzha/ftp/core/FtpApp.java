package com.shanzha.ftp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.shanzha.ftp.activity.FileUploadActivity;
import com.shanzha.ftp.dao.FTPDBService;
import com.shanzha.ftp.inter.ILoadPicListener;
import com.shanzha.ftp.inter.ILoadSmsContactListener;
import com.shanzha.ftp.model.Record;
import com.shanzha.ftp.util.FileUtil;

/**
 * 核心应用
 * 
 * @author ShanZha
 * @date 2012-9-25 11:25
 * 
 */
public class FtpApp extends Application {

	private static final String TAG = "FtpApp";
	/** 默认ftp主机 **/
	public static final String DEFAULT_HOST = "10.100.203.156";
	/** 默认ftp端口 **/
	public static final int DEFAULT_PORT = 21;
	/** 默认ftp用户 **/
	public static final String DEFAULT_USERNAME = "shanzha";
	/** 默认ftp密码 **/
	public static final String DEFAULT_PSWD = "LTT900324";
	/** 自身实例 **/
	private static FtpApp instance;
	/** （全局变量）设备名称 **/
	public static String DEVICE_NAME = Build.MODEL;
	/**
	 * 已经下载--内存集合 key---远程路径(包括文件名) value---文件名
	 */
	private Map<String, String> mMapDownloaded = new HashMap<String, String>();
	/**
	 * 已经上传--内存集合 key---本地路径(包括文件名) value---文件名
	 */
	private Map<String, String> mMapUploaded = new HashMap<String, String>();
	/**
	 * 加载本地图片完毕之后的图片路径集合
	 */
	private List<String> mLocalPicPathList = new ArrayList<String>();
	
	public FtpApp() {

	}

	/**
	 * 提供外界唯一实例
	 * 
	 * @return
	 */
	public synchronized static FtpApp getInstance() {
		if (null == instance) {
			instance = new FtpApp();
		}
		return instance;
	}

	/**
	 * 提供全局（已下载）
	 * 
	 * @return
	 */
	public Map<String, String> getmMapDownloaded() {
		return mMapDownloaded;
	}

	/**
	 * 提供全局（已上传）
	 * 
	 * @return
	 */
	public Map<String, String> getmMapUploaded() {
		return mMapUploaded;
	}

	public List<String> getmLocalPicPathList() {
		return mLocalPicPathList;
	}

	/**
	 * 查询已下载/已上传
	 * @param context
	 */
	public void queryFTPDB(final Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				FTPDBService dbService = FTPDBService.getInstance(context);
				List<Record> recordList = dbService.queryRecordList();
				
				for(Record record:recordList)
				{
					int type = record.getType();
					if(type==Record.TYPE_DOWNLOAD_RECORD)
					{
						mMapDownloaded.put(record.getUri(), record.getFilename());
					}else if(type==Record.TYPE_UPLOAD_RECORD)
					{
						mMapUploaded.put(record.getUri(), record.getFilename());
					}
				}
			}
		}).start();
	}
	
	/**
	 * 初始化短信文件，即把系统里短信读取出来，并已文件格式写到sdcard里
	 * @param context
	 * @param iUpload
	 */
	public void initSmsFile(Context context,ILoadSmsContactListener iUpload)
	{
		SmsManager.getInstance(context).writeSmsListToSdcard(iUpload);
	}
	
	/**
	 * 初始化联系人文件
	 * @param context
	 * @param listener
	 */
	public void initContactFile(Context context,ILoadSmsContactListener listener)
	{
		ContactManager.getInstance(context).writeContactListToSdcard(listener);
	}
	
	/**
	 * 初始化sdcard里的所有图片
	 * @param rootPath
	 * @param listener
	 */
	public void initPicFile(String rootPath,ILoadPicListener listener)
	{
		MediaScanner scanner = new MediaScanner(rootPath);
		scanner.setListener(listener);
		scanner.startScanning();
	}
}
