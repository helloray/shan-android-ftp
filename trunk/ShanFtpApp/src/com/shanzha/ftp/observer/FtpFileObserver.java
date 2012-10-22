package com.shanzha.ftp.observer;

import java.io.IOException;
import java.util.Stack;

import android.content.Context;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.shanzha.ftp.core.FTPManager;
import com.shanzha.ftp.core.FtpApp;
import com.shanzha.ftp.dao.FTPDBService;
import com.shanzha.ftp.inter.IFTPDataListener;
import com.shanzha.ftp.model.Record;
import com.shanzha.ftp.util.Constant;
import com.shanzha.ftp.util.FileUtil;
import com.shanzha.ftp.util.StringUtil;


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
				upload();
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
	
	/**
	 * 一个一个上传
	 */
	private void upload()
	{
		Log.i(TAG,"upload() isUploading = "+isUploading+" prepare size = "+mCache.size());
		if(!isUploading&&mCache.size()>0)
		{
			Record record = mCache.pop();
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
		mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_START);
		 
	}

	@Override
	public void onRequestFtpDataError(String errorMsg, int type) {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_ERROR);
	}

	@Override
	public void onRequestFtpDataTransfered(int transeredLength, int type) {
		// TODO Auto-generated method stub
		//暂时不用关心上传多少(transeredLength：指每次上传的长度，总上传的长度自己控制)
//		Message msg = mHandler.obtainMessage();
//		msg.what = Constant.MSG_UPLOAD_TRANSFERED;
//		msg.obj = transeredLength;
//		msg.sendToTarget();
	}

	@Override
	public void onRequestFtpDataCompleted(int type) {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_COMPLETE);
	}
	
	/**
	 * 处理各种消息
	 */
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what)
			{
			case Constant.MSG_UPLOAD_START://上传--单个文件--开始
				Log.i(TAG,"实时备份照片--开始  "+mCurrRecord.getFilename());
				break;
			case Constant.MSG_UPLOAD_ERROR://上传--单个文件--出错 
				Log.i(TAG,"实时备份照片--出错 "+mCurrRecord.getFilename());
				isUploading = false;
				//重新压栈，重新上传
				mCache.push(mCurrRecord);
				upload();
				break;
			case Constant.MSG_UPLOAD_TRANSFERED://上传--单个文件--已经上传多少
				
				break;
			case Constant.MSG_UPLOAD_COMPLETE://上传--单个文件--结束
				Log.i(TAG,"实时备份照片--完成"+mCurrRecord.getFilename());
				//放入待上传内存集合(已经备份过，不用再放入内存) 
//				FtpApp.getInstance().getmLocalPicPathList().add(mCurrRecord.getUri());
				//记录已经上传（内存）
				FtpApp.getInstance().getmMapUploaded().put(mCurrRecord.getUri(),mCurrRecord.getFilename());
				//记录已经上传（持久化）
				FTPDBService.getInstance(mContext).saveRecord(mCurrRecord);
				mCurrRecord = null;
				isUploading = false;
				upload();
				break;
			}
		}
		
	};
}
