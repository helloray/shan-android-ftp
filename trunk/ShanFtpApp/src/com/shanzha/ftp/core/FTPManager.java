package com.shanzha.ftp.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.shanzha.ftp.inter.IFTPBaseListener;
import com.shanzha.ftp.inter.IFTPDataListener;
import com.shanzha.ftp.inter.IFTPDataMultiListener;
import com.shanzha.ftp.inter.IFTPGetFileNameListener;
import com.shanzha.ftp.inter.IFTPGetFilesListener;
import com.shanzha.ftp.inter.IFTPRequeset;
import com.shanzha.ftp.model.FTPFile;
import com.shanzha.ftp.model.Record;
import com.shanzha.ftp.net.FTPClient;

/**
 * 各种FTP网络请求管理类
 * 
 * @author ShanZha
 * @date 2012-9-28 11:34
 */
public class FTPManager implements IFTPRequeset {

	private static final String TAG = "FTPManager";
	/** 自身实例 **/
	private static FTPManager instance;
	/** FTP 真正操作对象 全局的对象 **/
	private FTPClient mClient;
	/** 线程池，最大默认5个，可重复使用 **/
	private ExecutorService mExecutors = Executors.newFixedThreadPool(10);
	private int mTimeOut;

	/**
	 * 私有化构造器
	 * 
	 * @param context
	 */
	private FTPManager() {
	}

	/**
	 * 提供外部唯一实例
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static FTPManager getInstance() {
		if (null == instance) {
			instance = new FTPManager();
		}
		return instance;
	}

	public void setFTPClient(FTPClient client) {
		this.mClient = client;
	}

	public int getmTimeOut() {
		return mTimeOut;
	}

	public void setmTimeOut(int mTimeOut) {
		if (null != mClient) {
			mClient.setmTimeOut(mTimeOut);
		}
		this.mTimeOut = mTimeOut;
	}

	/**
	 * 各个FTP请求的初始判断
	 * 
	 * @param listener
	 * @throws IOException
	 */
	private void initValid(IFTPBaseListener listener, int type)
			throws IOException {
		String errorMsg = "";
		if (null == mClient) {
			errorMsg = "FTPClient 不能为空";
			if (null != listener) {
				listener.onRequestFtpDataError(errorMsg, type);
			}
			throw new IOException(errorMsg);
		}
		if (!mClient.isConnected()) {
			errorMsg = "已断开连接";
			if (null != listener) {
				listener.onRequestFtpDataError(errorMsg, type);
			}
			throw new IOException(errorMsg);
		}
	}

	@Override
	public void getFilesByDir(final String remoteDir,
			final IFTPGetFilesListener listener) throws IOException {
		// TODO Auto-generated method stub
		Log.i(TAG, "getFilesByDir()>> " + remoteDir);
		initValid(listener, IFTPBaseListener.TYPE_GETFILES);
		mExecutors.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (null != listener) {
					listener.onRequestFtpDataStart(IFTPBaseListener.TYPE_GETFILES);
				}
				List<FTPFile> results = null;
				try {
					results = mClient.listFiles(remoteDir);
					Log.i(TAG, "getFileByDir()<< " + results.size());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if (null != listener) {
						listener.onRequestFtpDataError(e.getMessage(),
								IFTPBaseListener.TYPE_GETFILES);
					}
					e.printStackTrace();
				}
				if (null != listener) {
					listener.onRequestFtpFileListCompleted(results);
				}
			}
		});
	}

	@Override
	public void getFilesNameByDir(final String remoteDir,
			final IFTPGetFileNameListener listener) throws IOException {
		// TODO Auto-generated method stub
		Log.i(TAG, "getFileNamesByDir()>> " + remoteDir);
		initValid(listener, IFTPBaseListener.TYPE_GETFILENAMES);
		mExecutors.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (null != listener) {
					listener.onRequestFtpDataStart(IFTPBaseListener.TYPE_GETFILENAMES);
				}
				List<String> filenames = null;
				try {
					filenames = mClient.listNames(remoteDir);
					Log.i(TAG, "getFileNamesByDir()>> " + filenames.size());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if (null != listener) {
						listener.onRequestFtpDataError(e.getMessage(),
								IFTPBaseListener.TYPE_GETFILENAMES);
					}
					e.printStackTrace();
				}
				if (null != listener) {
					listener.onRequestFtpFilenameListCompleted(filenames);
				}
			}
		});
	}

	@Override
	public void isExist(String remoteDir, String filename, int filetype)
			throws IOException {
		// TODO Auto-generated method stub
		initValid(null, -1);
	}

	@Override
	public void download(final String filename, final OutputStream os,
			final String serverDir, final IFTPDataListener listener)
			throws IOException {
		// TODO Auto-generated method stub
		Log.i(TAG, "download() >> " + filename);
		initValid(listener, IFTPBaseListener.TYPE_DOWNLOAD);
		mExecutors.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					mClient.changeDirectory(serverDir);
					mClient.downloadFile(filename, os, listener);
					Log.i(TAG, "download() << " + filename);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void download(final Record download, final IFTPDataListener listener)
			throws IOException {
		// TODO Auto-generated method stub
		Log.i(TAG, "download() >> " + download.getFilename());
		initValid(listener, IFTPBaseListener.TYPE_DOWNLOAD);
		mExecutors.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					mClient.changeDirectory(download.getRemoteDir());
					mClient.downloadFile(download.getFilename(),
							download.getLocalDir(), listener);
					Log.i(TAG, "download() << " + download.getFilename());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void downloadMulti(final List<Record> downloadList,
			final String serverDir, final IFTPDataMultiListener listener)
			throws IOException {
		// TODO Auto-generated method stub
		if (null == downloadList || downloadList.size() == 0) {
			Log.i(TAG, " 没有下载的文件");
			return;
		}
		Log.i(TAG, "downloadMulti() >> " + downloadList.size());
		initValid(listener, IFTPBaseListener.TYPE_DOWNLOAD_MULTI);
		mExecutors.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					mClient.changeDirectory(serverDir);
					mClient.downloadMulti(downloadList, listener);
					Log.i(TAG, "downloadMulti() << " + downloadList.size()
							+ " serverDir = " + serverDir);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if (null != listener) {
						listener.onRequestFtpDataError(e.getMessage(),
								IFTPBaseListener.TYPE_DOWNLOAD_MULTI);
					}
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void upload(final String filename, final InputStream is,
			final String serverDir, final IFTPDataListener listener)
			throws IOException {
		// TODO Auto-generated method stub
		Log.i(TAG, "upload() >> " + filename);
		initValid(listener, IFTPBaseListener.TYPE_UPLOAD);
		mExecutors.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					mClient.changeDirectory(serverDir);
					mClient.uploadFile(filename, is, listener);
					Log.i(TAG, "upload() << " + filename);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void upload(final Record record, final IFTPDataListener listener)
			throws IOException {
		// TODO Auto-generated method stub
		Log.i(TAG, "upload() >> " + record.getFilename());
		initValid(listener, IFTPBaseListener.TYPE_UPLOAD);
		mExecutors.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					mClient.changeDirectory(record.getRemoteDir());
					mClient.uploadFile(record.getFilename(), record.getUri(),
							listener);
					Log.i(TAG, "upload() << " + record.getFilename());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void uploadMulti(final List<Record> uploadList,
			final IFTPDataMultiListener listener) throws IOException {
		// TODO Auto-generated method stub
		if (null == uploadList || uploadList.size() == 0) {
			Log.i(TAG, "uploadMulti() 没有文件要上传");
			return;
		}
		Log.i(TAG, "upload() >> " + uploadList.size());
		initValid(listener, IFTPBaseListener.TYPE_UPLOAD_MULTI);
		mExecutors.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					// 暂时默认所要上传的文件都上传到相同目录下
					String serverDir = uploadList.get(0).getRemoteDir();
					mClient.changeDirectory(serverDir);
					mClient.uploadMultiFiles(uploadList, listener);
					Log.i(TAG, "upload() << " + uploadList.size());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void connect(String ip, int port) throws IOException {
		// TODO Auto-generated method stub
		if (null == mClient) {
			throw new IOException("FTPClient 不能为空");
		}
		mClient.connect(ip, port);
	}

	@Override
	public boolean login(String user, String pswd) throws IOException {
		// TODO Auto-generated method stub
		initValid(null, -1);
		return mClient.login(user, pswd);
	}

	@Override
	public boolean logout() throws IOException {
		// TODO Auto-generated method stub
		initValid(null, -1);
		return mClient.logout();
	}

	@Override
	public void disconnect() throws IOException {
		// TODO Auto-generated method stub
		initValid(null, -1);
		Log.i(TAG, ">>disconnect");
		mClient.disconnect();
	}

	@Override
	public boolean changeToParentDirectory() throws IOException {
		// TODO Auto-generated method stub
		initValid(null, -1);
		return mClient.changeToParentDirectory();
	}

	@Override
	public boolean changeDirectory(String dir) throws IOException {
		// TODO Auto-generated method stub
		initValid(null, -1);
		return mClient.changeDirectory(dir);
	}

	@Override
	public boolean createDirectory(String dirName) throws IOException {
		// TODO Auto-generated method stub
		initValid(null, -1);
		return mClient.createDirectory(dirName);
	}

	@Override
	public void abortCurrDataConnect() throws IOException {
		// TODO Auto-generated method stub
		initValid(null, -1);
		mClient.abortCurrDataConnect();
	}
}
