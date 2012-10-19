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
 * 短信管理者，主要是负责内存中短息的管理，以及一些相关操作
 * 
 * @author ShanZha
 * @date 2012-10-11 15:09
 * @see SmsDBService
 */
public class SmsManager {

	private static final String TAG = "SmsManager";
	/**
	 * 自身实例
	 */
	private static SmsManager instance;
	/**
	 * 上下文应用环境
	 */
	private Context mContext;
	/**
	 * 所有短信集合(放内存中全局使用)
	 */
	private List<Sms> mSmsList = new ArrayList<Sms>();
	/**
	 * 线程池，最大默认3个，可重复使用
	 */
	private ExecutorService mExecutors = Executors.newFixedThreadPool(3);
	/**
	 * 短信数据库操作服务类（处理一切短信的数据库操作）
	 */
	private SmsDBService mSmsDB;
	/**
	 * Json格式管理类（处理一切关于json格式的转换）
	 */
	private JsonManager mJsonMgr;
	/**
	 * 把所有短信写到sdcard之后的文件路径
	 */
	private String localFilePath;

	private SmsManager(Context context) {
		this.mContext = context.getApplicationContext();
		mSmsDB = SmsDBService.getInstance(context);
		mJsonMgr = JsonManager.getInstance();
	}

	/**
	 * 提供给外部一个实例
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
	 * 内存中的所有短信集合
	 * 
	 * @return
	 */
	public List<Sms> getSmsList() {
		return mSmsList;
	}

	/**
	 * 根据isRefresh判断是否重新读取本地短信
	 * 
	 * @param isRefresh
	 * @return
	 */
	// public void readAllSmss(IWriteCallback writeCallback) {
	//
	// SmsService.getInstance(mContext).getAllSms(this, writeCallback);
	// }
	/**
	 * 把用户下载到sdcard的短信息文件写到系统里
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
	 * 把本地短息以Json格式写到sdcard里 分三步： 1、先把手机里所有符合条件的短信读取处理 2、再把这些短信转为json格式字符串
	 * 3、最后以.txt文件格式保存到sdcard
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
					// 步骤1
					List<Sms> smsList = mSmsDB.queryAllSms();
					if (null != mSmsList && null != smsList) {
						mSmsList.addAll(smsList);
					}
					// 步骤2
					String result = mJsonMgr.buildJsonStrFromList(smsList,
							mContext);
					String dir = FileUtil.getSdcardPath() + Constant.SEPERATOR
							+ FileUtil.PATH_UPLOAD;
					// 步骤3
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
	// * 把json格式的字符串从sdcard文件里读取出来，并转为短息列表
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
