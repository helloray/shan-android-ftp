package com.shanzha.ftp.observer;

import com.shanzha.ftp.util.FileUtil;
import com.shanzha.ftp.util.ToastUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 启动各种监听的服务（如监听本地文件夹，联系人，短信等）
 * 
 * @author ShanZha
 * @date 2012-10-16 17:47
 * 
 */
public class ObserverService extends Service {

	private static final String TAG = "ObserverService";
	private FtpFileObserver mFileObverser;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "...onCreate()...");

		mFileObverser = new FtpFileObserver(FileUtil.getSdcardPath()
				+ "/DCIM/Camera", ObserverService.this);
		mFileObverser.startWatching();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (null != mFileObverser) {
			mFileObverser.stopWatching();
		}
		Log.i(TAG, "...onDestroy()...");
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		Log.i(TAG, "...onLowMemery()...");
	}

}
