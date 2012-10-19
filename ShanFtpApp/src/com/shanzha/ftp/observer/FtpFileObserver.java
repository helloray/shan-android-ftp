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
 * ��������ָ��·���ļ�����
 * ע�⣺�ĵ�����˵�����ԣ��������ֻ�ܼ���һ���ļ����£����ܵ����������Լ�ʵ��
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
	public void onEvent(int event, String path) {//path:�ڴ�����Եģ���ʵ�����ļ���
		// TODO Auto-generated method stub
		switch (event) {
		case FileObserver.CREATE:
			Log.i(TAG, "Create: " + path);
			//�����������Ĭ�ϴ��ͼƬ���ļ��У�/mnt/sdcard/DCIM/Camera/���������һ��ͼƬ����ֱ���ϴ�
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
		 
		Log.i(TAG,"ʵʱ������Ƭ--��ʼ");
		 
	}

	@Override
	public void onRequestFtpDataError(String errorMsg, int type) {
		// TODO Auto-generated method stub
		Log.i(TAG,"ʵʱ������Ƭ--����"+errorMsg);
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
		Log.i(TAG,"ʵʱ������Ƭ--���");
		//������ϴ��ڴ漯��(�Ѿ����ݹ��������ٷ����ڴ�) 
//		FtpApp.getInstance().getmLocalPicPathList().add(mCurrRecord.getUri());
		//��¼�Ѿ��ϴ����ڴ棩
		FtpApp.getInstance().getmMapUploaded().put(mCurrRecord.getUri(),mCurrRecord.getFilename());
		//��¼�Ѿ��ϴ����־û���
		FTPDBService.getInstance(mContext).saveRecord(mCurrRecord);
		mCurrRecord = null;
		isUploading = false;
		upload();
	}

}
