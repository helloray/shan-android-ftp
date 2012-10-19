package com.shanzha.ftp.observer;

import java.io.IOException;
import java.util.Stack;

import com.shanzha.ftp.core.FTPManager;
import com.shanzha.ftp.core.FtpApp;
import com.shanzha.ftp.dao.FTPDBService;
import com.shanzha.ftp.inter.IFTPBaseListener;
import com.shanzha.ftp.inter.IFTPDataListener;
import com.shanzha.ftp.model.Record;
import com.shanzha.ftp.util.Constant;
import com.shanzha.ftp.util.FileUtil;
import com.shanzha.ftp.util.StringUtil;

import android.content.Context;
import android.os.FileObserver;
import android.util.Log;


/**
 * 监听本地指定路径的监听者
 * 注意：文档里面说明不对，这个监听只能监听一个文件夹下，不能迭代，除非自己实现
 * 
 * @author ShanZha
 * @date 2012-10-16 16:11
 * 
 */
public class FtpFileObserver extends FileObserver implements IFTPDataListener{

	private static final String TAG = "FtpFileObserver";
	private Context mContext;
	private String monitorPath;
	private Record mCurrRecord;
	private Stack<Record> mCache = new Stack<Record>();
	private boolean isUploading = false;
	public FtpFileObserver(String path, Context context) {
		super(path);
		this.mContext = context;
		this.monitorPath = path;
		Log.i(TAG, "path: " + path);
	}

	public FtpFileObserver(String path) {
		super(path);
		// TODO Auto-generated constructor stub
		this.monitorPath = path;
	}

	@Override
	public void onEvent(int event, String path) {//path:在此是相对的，其实就是文件名
		// TODO Auto-generated method stub
		switch (event) {
		case FileObserver.CREATE:
			Log.i(TAG, "Create: " + path);
			//监听相机照相默认存放图片的文件夹（/mnt/sdcard/DCIM/Camera/），添加了一张图片，则直接上传
			if(FileUtil.isPic(path))
			{
				String filepath = this.monitorPath+Constant.SEPERATOR+path;
//				mCurrRecord = new Record();
				Record record = new Record();
				record.setUri(filepath);
				record.setType(Record.TYPE_UPLOAD_RECORD);
				record.setFilename(path);
				record.setLocalDir(this.monitorPath);
				String remoteDir = Constant.FTP_ROOT+FtpApp.DEVICE_NAME;
				record.setRemoteDir(remoteDir);
				record.setTime(StringUtil.formatDate(System.currentTimeMillis(), 
						"yyyy-MM--dd hh:mm"));
				mCache.push(record);
				
			}
			break;
//		case FileObserver.ACCESS:
//			Log.i(TAG, "access: " + path);
//			break;
		case FileObserver.DELETE:
			Log.i(TAG, "delete: " + path);
			break;
		}
	}
	private void upload()
	{
		if(!isUploading)
		{
			Record record = mCache.pop();
			if(null==record)
			{
				return;
			}
			mCurrRecord = record;
			isUploading = true;
			try {
				FTPManager.getInstance().upload(mCurrRecord, this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}

	@Override
	public void onRequestFtpDataStart(int type) {
		// TODO Auto-generated method stub
		 
		Log.i(TAG,"实时备份照片--开始");
		 
	}

	@Override
	public void onRequestFtpDataError(String errorMsg, int type) {
		// TODO Auto-generated method stub
		Log.i(TAG,"实时备份照片--出错"+errorMsg);
		isUploading = false;
		upload();
	}

	@Override
	public void onRequestFtpDataTransfered(int transeredLength, int type) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRequestFtpDataCompleted(int type) {
		// TODO Auto-generated method stub
		Log.i(TAG,"实时备份照片--完成");
		//放入待上传内存集合(已经备份过，不用再放入内存) 
//		FtpApp.getInstance().getmLocalPicPathList().add(mCurrRecord.getUri());
		//记录已经上传（内存）
		FtpApp.getInstance().getmMapUploaded().put(mCurrRecord.getUri(),mCurrRecord.getFilename());
		//记录已经上传（持久化）
		FTPDBService.getInstance(mContext).saveRecord(mCurrRecord);
		mCurrRecord = null;
		isUploading = false;
		upload();
	}

}
