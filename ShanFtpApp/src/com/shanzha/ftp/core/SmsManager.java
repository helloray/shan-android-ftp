package com.shanzha.ftp.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.shanzha.ftp.dao.SmsDBService;
import com.shanzha.ftp.inter.ILoadSmsContactListener;
import com.shanzha.ftp.model.Sms;
import com.shanzha.ftp.util.Constant;
import com.shanzha.ftp.util.FileUtil;

import android.content.Context;

/**
 * ���Ź����ߣ���Ҫ�Ǹ����ڴ��ж�Ϣ�Ĺ����Լ�һЩ��ز���
 * 
 * @author ShanZha
 * @date 2012-10-11 15:09
 * @see SmsDBService
 */
public class SmsManager {

	private static final String TAG = "SmsManager";
	/**
	 * ����ʵ��
	 */
	private static SmsManager instance;
	/**
	 * ������Ӧ�û���
	 */
	private Context mContext;
	/**
	 * ���ж��ż���(���ڴ���ȫ��ʹ��)
	 */
	private List<Sms> mSmsList = new ArrayList<Sms>();
	/**
	 * �̳߳أ����Ĭ��3�������ظ�ʹ��
	 */
	private ExecutorService mExecutors = Executors.newFixedThreadPool(3);
	/**
	 * �������ݿ���������ࣨ����һ�ж��ŵ����ݿ������
	 */
	private SmsDBService mSmsDB;
	/**
	 * Json��ʽ�����ࣨ����һ�й���json��ʽ��ת����
	 */
	private JsonManager mJsonMgr;
	/**
	 * �����ж���д��sdcard֮����ļ�·��
	 */
	private String localFilePath;

	private SmsManager(Context context) {
		this.mContext = context.getApplicationContext();
		mSmsDB = SmsDBService.getInstance(context);
		mJsonMgr = JsonManager.getInstance();
	}

	/**
	 * �ṩ���ⲿһ��ʵ��
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static SmsManager getInstance(Context context) {
		if (null == instance) {
			instance = new SmsManager(context);
		}
		return instance;
	}

	/**
	 * �ڴ��е����ж��ż���
	 * 
	 * @return
	 */
	public List<Sms> getSmsList() {
		return mSmsList;
	}

	/**
	 * ����isRefresh�ж��Ƿ����¶�ȡ���ض���
	 * 
	 * @param isRefresh
	 * @return
	 */
	// public void readAllSmss(IWriteCallback writeCallback) {
	//
	// SmsService.getInstance(mContext).getAllSms(this, writeCallback);
	// }
	/**
	 * ���û����ص�sdcard�Ķ���Ϣ�ļ�д��ϵͳ��
	 */
	// public void writeAllSmss(String path)
	// {
	// List<Sms> tempSmss = getSmssFromSdcard(path);
	// SmsService.getInstance(mContext).writeSmsListToDB(tempSmss);
	// }

	public String getLocalFilePath() {
		return localFilePath;
	}

	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}

	/**
	 * �ѱ��ض�Ϣ��Json��ʽд��sdcard�� �������� 1���Ȱ��ֻ������з��������Ķ��Ŷ�ȡ���� 2���ٰ���Щ����תΪjson��ʽ�ַ���
	 * 3�������.txt�ļ���ʽ���浽sdcard
	 * 
	 * @param iUpload
	 */
	public void writeSmsListToSdcard(final ILoadSmsContactListener iUpload) {
		mExecutors.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (null != iUpload) {
					iUpload.onLoadPreparation(ILoadSmsContactListener.TYPE_SMS);
				}
				try {
					// ����1
					List<Sms> smsList = mSmsDB.queryAllSms();
					if (null != mSmsList && null != smsList) {
						mSmsList.addAll(smsList);
					}
					// ����2
					String result = mJsonMgr.buildJsonStrFromList(smsList,
							mContext);
					String dir = FileUtil.getSdcardPath() + Constant.SEPERATOR
							+ FileUtil.PATH_UPLOAD;
					// ����3
					String path = FileUtil.writeToFile(dir,
							FileUtil.SMS_FILENAME, result);
					if (null != iUpload) {
						iUpload.onLoadCompleted(path,
								ILoadSmsContactListener.TYPE_SMS);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (null != iUpload) {
						iUpload.onLoadError(e.getMessage(),
								ILoadSmsContactListener.TYPE_SMS);
					}
					e.printStackTrace();
				}
			}
		});
	}
	// /**
	// * ��json��ʽ���ַ�����sdcard�ļ����ȡ��������תΪ��Ϣ�б�
	// * @param path
	// * @return
	// */
	// private List<Sms> getSmssFromSdcard(String path)
	// {
	// String result = StringUtil.transferFileToStr(path);
	// List<Sms> smss = JsonUtil.parseSmsToList(result);
	// return smss;
	// }

}
