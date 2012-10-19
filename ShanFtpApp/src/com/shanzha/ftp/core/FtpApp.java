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
 * ����Ӧ��
 * 
 * @author ShanZha
 * @date 2012-9-25 11:25
 * 
 */
public class FtpApp extends Application {

	private static final String TAG = "FtpApp";
	/** Ĭ��ftp���� **/
	public static final String DEFAULT_HOST = "10.100.203.156";
	/** Ĭ��ftp�˿� **/
	public static final int DEFAULT_PORT = 21;
	/** Ĭ��ftp�û� **/
	public static final String DEFAULT_USERNAME = "shanzha";
	/** Ĭ��ftp���� **/
	public static final String DEFAULT_PSWD = "LTT900324";
	/** ����ʵ�� **/
	private static FtpApp instance;
	/** ��ȫ�ֱ������豸���� **/
	public static String DEVICE_NAME = Build.MODEL;
	/**
	 * �Ѿ�����--�ڴ漯�� key---Զ��·��(�����ļ���) value---�ļ���
	 */
	private Map<String, String> mMapDownloaded = new HashMap<String, String>();
	/**
	 * �Ѿ��ϴ�--�ڴ漯�� key---����·��(�����ļ���) value---�ļ���
	 */
	private Map<String, String> mMapUploaded = new HashMap<String, String>();
	/**
	 * ���ر���ͼƬ���֮���ͼƬ·������
	 */
	private List<String> mLocalPicPathList = new ArrayList<String>();
	
	public FtpApp() {

	}

	/**
	 * �ṩ���Ψһʵ��
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
	 * �ṩȫ�֣������أ�
	 * 
	 * @return
	 */
	public Map<String, String> getmMapDownloaded() {
		return mMapDownloaded;
	}

	/**
	 * �ṩȫ�֣����ϴ���
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
	 * ��ѯ������/���ϴ�
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
	 * ��ʼ�������ļ�������ϵͳ����Ŷ�ȡ�����������ļ���ʽд��sdcard��
	 * @param context
	 * @param iUpload
	 */
	public void initSmsFile(Context context,ILoadSmsContactListener iUpload)
	{
		SmsManager.getInstance(context).writeSmsListToSdcard(iUpload);
	}
	
	/**
	 * ��ʼ����ϵ���ļ�
	 * @param context
	 * @param listener
	 */
	public void initContactFile(Context context,ILoadSmsContactListener listener)
	{
		ContactManager.getInstance(context).writeContactListToSdcard(listener);
	}
	
	/**
	 * ��ʼ��sdcard�������ͼƬ
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
