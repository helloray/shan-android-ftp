package com.shanzha.ftp.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.text.TextUtils;
import android.util.Log;

import com.shanzha.ftp.inter.IFTPBaseListener;
import com.shanzha.ftp.inter.IFTPDataListener;
import com.shanzha.ftp.inter.IFTPDataMultiListener;
import com.shanzha.ftp.model.FTPFile;
import com.shanzha.ftp.model.Record;
import com.shanzha.ftp.util.FTPResponse;
import com.shanzha.ftp.util.FileUtil;
import com.shanzha.ftp.util.StringUtil;

/**
 * FTP���ݲ����������� (��������ֻ��ÿ�ζ��½�һ����ԭ����ģ�¿������ӣ�����Ϊ�������ӵĶ˿ں�ip�Ƿ�����PASV����PORTģʽ
 * ����֮�󷵻����ģ�FTP������ֻ��������˿ڣ�����ÿ��������������½�һ��Socket�������ӣ�����FTPЭ��
 * �Ĺ��򣬿������������ֻ��һ��Socket���ӣ���ΪFTP������һֱ�ڼ�������˿�(21))
 * ���ݻ�ȡ��Ҫ���Ĳ���
 * 1������PASV�����ã�����PORT����ģʽ�����ݷ���ֵ�������ݻ�ȡip�Ͷ˿�
 * 2�����;�����������󣬻�ȡ����ֵ�������150��ͷ�������ݶ˿ڴ򿪣��ɽ������ݽ���
 * 3�����ݽ���
 * 4����ȡ���ݽ����Ƿ���ϣ��ⲽ�費��ʡ�ԣ��������Э�����Ӧ��Ӧ�����ˣ�
 * 
 * @author ShanZha
 * @date 2012-9-26 14:24
 */
public class FTPClient extends FTPCommandManager {

	private static final String TAG = "FTPClient";
	/**
	 * �Ƿ��ӡ��־
	 */
	private boolean isLog = true;
	// /**
	// * ����Э�鳬ʱ
	// */
	// private int mDataTimeOut;
	// /**
	// * ���ݴ����Socket
	// */
	// private Socket mDataSocket;
	// /**
	// * ����������
	// */
	// private InputStream mDataInput;
	// /**
	// * ���������
	// */
	// private OutputStream mDataOutput;
	/**
	 * �������Ļ����С
	 */
	private static final int BUFFER_SIZE = 4 * 1024;
	/****** ���ļ��ϴ�/���ؼ�¼����(BEGIN) **********/
	private IFTPDataMultiListener mMultiListener;
	private int mTotalCount = -1;
	private int mTransferedLength = -1;
	private int mTranferedCount = 0;
	private boolean isStart = false;
	private List<Record> mMultlRecordList = null;
	private int mType;// ����ϴ����/�������ض��

	/******* (END) *******/

	public FTPClient() {
		super();
	}

	@Override
	protected void connectAction() throws IOException {
		// TODO Auto-generated method stub
		super.connectAction();
	}

	/**
	 * ��½(���������裺1 ����USER����������3XX���ʾ�˲���ɹ� 2������1�ɹ�֮���ٷ���
	 * PASS����������2XX���ʾ������½�ɹ�)
	 * 
	 * @param username
	 * @param pswd
	 * @return
	 * @throws IOException
	 */
	public boolean login(String username, String pswd) throws IOException {
		if (isLog) {
			Log.i(TAG, "login>> username = " + username + " pswd = " + pswd);
		}
		// int code = user(username);
		String response = readLineResponse();
		int code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "connect<< " + response);
		}
		// ����û�гɹ���ֱ�ӵ�¼ʧ�� ( 220 Serv-U FTP Server v12.1 ready...)
		if (!FTPResponse.isPositiveCompletion(code)) {
			return false;
		}
		user(username);
		// �û���û�гɹ���ֱ�ӷ���ʧ��
		// if (!FTPResponse.isPositiveCompletion(code)) {
		// return false;
		// }
		response = readLineResponse();
		code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "login<< username code = " + code);
		}
		// ����û����ɹ��ˣ�������֤�����������ߣ�����ֱ��ʧ�ܣ�331 User name okay, need password.��
		if (!FTPResponse.isPositiveIntermediate(code)) {
			return false;
		}
		pass(pswd);
		response = readLineResponse();
		code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "login<< pswd code = " + code);
		}
		// ��Ҫ���룬�������������ɹ���񼴿�
		return FTPResponse.isPositiveCompletion(code);
	}

	/**
	 * ע����½
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean logout() throws IOException {
		quit();
		String response = readLineResponse();
		int code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "logout<< " + response);
		}
		return FTPResponse.isPositiveCompletion(code);
	}

	@Override
	public void disconnect() throws IOException {
		// TODO Auto-generated method stub
		super.disconnect();
		// if (null != mDataSocket) {
		// mDataSocket.close();
		// mDataSocket = null;
		// }
		// if(null!=mDataInput)
		// {
		// mDataInput.close();
		// mDataInput = null;
		// }
		// if(null!=mDataOutput)
		// {
		// mDataOutput.close();
		// mDataOutput = null;
		// }
	}

	/**
	 * �ı����Ŀ¼
	 * 
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public boolean changeDirectory(String dir) throws IOException {
		cwd(dir);
		String response = readLineResponse();
		int code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "changeDirectory<< " + response);
		}
		return FTPResponse.isPositiveCompletion(code);
	}

	/**
	 * �ص���Ŀ¼
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean changeToParentDirectory() throws IOException {
		cdup();
		String response = readLineResponse();
		int code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "changeToParentDirectory<< " + response);
		}
		return FTPResponse.isPositiveCompletion(code);
	}

	/**
	 * ����һ��Ŀ¼
	 * 
	 * @param dirName
	 * @return
	 * @throws IOException
	 */
	public boolean createDirectory(String dirName) throws IOException {
		mkd(dirName);
		String response = readLineResponse();
		int code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "createDirectory<< " + response);
		}
		return FTPResponse.isPositiveCompletion(code);
	}

	/**
	 * ������ǰ��������
	 * 
	 * @throws IOException
	 */
	public void abortCurrDataConnect() throws IOException {
//		flushResponse();
		abor();
		String response = readLineResponse();
		int code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "abortCurrDataConnect first line << " + response);
		}
//		if (!FTPResponse.isNegativeTransient(code)) {
//			throw new IOException("��ֹ��ǰ��������ʧ�� " + response);
//		}
//		response = readLineResponse();
//		code = getResponseCode();
//		if (isLog) {
//			Log.i(TAG, "abortCurrDataConnect second line << " + response);
//		}
//		if (!FTPResponse.isPositiveCompletion(code)) {
//			throw new IOException("��ֹ��ǰ��������ʧ�� " + response);
//		}
		//�����451 Operation aborted due to ABOR command�����������һ�У����򲻶���һ��
		if(FTPResponse.isNegativeTransient(code))
		{
			response = readLineResponse();
			code = getResponseCode();
			if (isLog) {
				Log.i(TAG, "abortCurrDataConnect second line << " + response);
			}
		}
		//�������226 Abort successfu������ֹʧ��
		if(!FTPResponse.isPositiveCompletion(code))
		{
			throw new IOException("��ֹ��ǰ��������ʧ�� " + response);
		}
	}

	/**
	 * ��ȡָ��Ŀ¼�µ������ļ���Ϣ
	 * 
	 * @param dirName
	 * @return
	 * @throws IOException
	 */
	public List<FTPFile> listFiles(String dirName) throws IOException {

		Socket dataSocket = getDataSocket();

		list(dirName);
		String response = readLineResponse();
		int code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "listFiles ��һ��<< " + response);
		}
		// ���������Ӧ���ɹ�����Ӧ�벻��100-199֮��(ֻ�г�ʼ����������ʱ�ɹ�ʱ����1��ͷ)
		// ( 150 Opening ASCII mode data connection for /bin/ls)
		if (!FTPResponse.isPositivePreliminary(code)) {
			throw new IOException(" ��ȡָ��Ŀ¼�µ��ļ����� " + response);
		}

		List<FTPFile> files = new ArrayList<FTPFile>();
		BufferedReader mBufferReader = null;
		try {
			mBufferReader = new BufferedReader(new InputStreamReader(
					dataSocket.getInputStream()), BUFFER_SIZE);
			String line = "";
			while ((line = mBufferReader.readLine()) != null) {
				Log.i(TAG, line);
				FTPFile file = parseDirs(line);
				if (!(".".equals(file.getName()) || "..".equals(file.getName()))) {
					file.setRemotePath(dirName);
					files.add(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} finally {
			try {
				if (null != mBufferReader) {
					mBufferReader.close();
					mBufferReader = null;
				}
				if (null != dataSocket) {
					dataSocket.close();
					dataSocket = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		response = readLineResponse();
		code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "listFiles �ڶ���<< " + response);
		}
		// ������ɣ�200-299֮���ǳɹ���(226 Transfer complete. 577 bytes transferred.
		// 37.57 KB/sec.)
		if (!FTPResponse.isPositiveCompletion(code)) {
			throw new IOException(" ��ȡָ��Ŀ¼�µ��ļ����� " + response);
		}
		return files;
	}

	/**
	 * ��ȡָ��Ŀ¼�µ������ļ�����
	 * 
	 * @param dirName
	 * @return
	 * @throws IOException
	 */
	public List<String> listNames(String dirName) throws IOException {

		Socket dataSocket = getDataSocket();
		nlst(dirName);
		String response = readLineResponse();
		int code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "listNames ��һ��<< " + response);
		}
		// ���������Ӧ���ɹ�����Ӧ�벻��100-199֮��(ֻ�г�ʼ����������ʱ�ɹ�ʱ����1��ͷ)
		// ( 150 Opening ASCII mode data connection for /bin/ls)
		if (!FTPResponse.isPositivePreliminary(code)) {
			throw new IOException(" ��ȡָ��Ŀ¼�µ��ļ����� " + response);
		}
		response = readLineResponse();
		code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "listNames �ڶ���<< " + response);
		}
		// ������ɣ�200-299֮���ǳɹ���(226 Transfer complete. 577 bytes transferred.
		// 37.57 KB/sec.)
		if (!FTPResponse.isPositiveCompletion(code)) {
			throw new IOException(" ��ȡָ��Ŀ¼�µ��ļ����� " + response);
		}
		List<String> names = new ArrayList<String>();
		BufferedReader mBufferReader = null;
		try {
			mBufferReader = new BufferedReader(new InputStreamReader(
					dataSocket.getInputStream()), BUFFER_SIZE);
			String line = "";
			while ((line = mBufferReader.readLine()) != null) {
				names.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != mBufferReader) {
					mBufferReader.close();
					mBufferReader = null;
				}
				if (null != dataSocket) {
					dataSocket.close();
					dataSocket = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return names;
	}

	/**
	 * �ϴ������ļ���������
	 * 
	 * @param filename
	 * @param is
	 * @param listener
	 * @throws IOException
	 */
	public void uploadFile(String filename, InputStream is,
			IFTPDataListener listener) throws IOException {

		Socket dataSocket = getDataSocket();
		appe(filename);
		String response = readLineResponse();
		int code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "uploadFile ��һ��<< " + response);
		}
		// ���������Ӧ���ɹ�����Ӧ�벻��100-199֮��(ֻ�г�ʼ����������ʱ�ɹ�ʱ����1��ͷ)
		// ( 150 Opening ASCII mode data connection for /bin/ls)
		if (!FTPResponse.isPositivePreliminary(code)) {
			throw new IOException(" �ϴ��ļ����� " + response);
		}

		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			if (null != listener) {
				listener.onRequestFtpDataStart(IFTPBaseListener.TYPE_UPLOAD);
			}
			bos = new BufferedOutputStream(dataSocket.getOutputStream(),
					BUFFER_SIZE);
			bis = new BufferedInputStream(is, BUFFER_SIZE);
			byte[] buffer = new byte[4 * 1024];
			int len = -1;
			while ((len = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
				if (null != listener) {
					listener.onRequestFtpDataTransfered(len,
							IFTPBaseListener.TYPE_UPLOAD);
				}
			}
			bos.flush();
			if (null != listener) {
				listener.onRequestFtpDataCompleted(IFTPBaseListener.TYPE_UPLOAD);
			}
		} catch (IOException e) {
			if (null != listener) {
				listener.onRequestFtpDataError(e.getMessage(),
						IFTPBaseListener.TYPE_UPLOAD);
			}
			e.printStackTrace();
		} finally {
			try {
				if (null != bis) {
					bis.close();
					bis = null;
				}
				if (null != bos) {
					bos.close();
					bos = null;
				}
				if (null != dataSocket) {
					dataSocket.close();
					dataSocket = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		response = readLineResponse();
		code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "uploadFile �ڶ���<< " + response);
		}
		// ������ɣ�200-299֮���ǳɹ���(226 Transfer complete. 577 bytes transferred.
		// 37.57 KB/sec.)
		if (!FTPResponse.isPositiveCompletion(code)) {
			throw new IOException(" �ϴ��ļ����� " + response);
		}
	}

	/**
	 * �ϴ������ļ���������
	 * 
	 * @param localPath
	 * @param filename
	 * @param listener
	 * @throws IOException
	 */
	public void uploadFile(String filename, String localPath,
			IFTPDataListener listener) throws IOException {
		File file = new File(localPath);
		FileInputStream fis = new FileInputStream(file);
		uploadFile(filename, fis, listener);
	}

	/**
	 * �ϴ�����ļ���������
	 * 
	 * @param filepaths
	 * @param listener
	 * @throws IOException
	 */
	public void uploadMultiFiles(List<Record> uploadList,
			final IFTPDataMultiListener listener) throws IOException {
		this.mMultiListener = listener;
		this.mMultlRecordList = uploadList;
		this.mType = IFTPDataListener.TYPE_UPLOAD_MULTI;
		int size = uploadList.size();
		this.mTotalCount = size;
		for (int i = 0; i < size; i++) {
			Record upload = uploadList.get(i);
			String filepath = upload.getUri();
			File file = new File(filepath);
			String filename = file.getName();
			FileInputStream fis = new FileInputStream(file);
			uploadFile(filename, fis, mDataMultiListener);
		}
	}

	/**
	 * ���ص����ļ�
	 * 
	 * @param filename
	 * @param os
	 * @param listener
	 * @throws IOException
	 */
	public void downloadFile(String filename, OutputStream os,
			IFTPDataListener listener) throws IOException {

		Socket dataSocket = getDataSocket();
		retr(filename);
		String response = readLineResponse();
		int code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "downloadFile ��һ��<< " + response);
		}
		// ���������Ӧ���ɹ�����Ӧ�벻��100-199֮��(ֻ�г�ʼ����������ʱ�ɹ�ʱ����1��ͷ)
		// ( 150 Opening ASCII mode data connection for /bin/ls)
		if (!FTPResponse.isPositivePreliminary(code)) {
			String error = " �����ļ����� " + response;
			if (null != listener) {
				listener.onRequestFtpDataError(error,
						IFTPBaseListener.TYPE_DOWNLOAD);
			}
			throw new IOException(error);
		}

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			if (null != listener) {
				listener.onRequestFtpDataStart(IFTPBaseListener.TYPE_DOWNLOAD);
			}
			bis = new BufferedInputStream(dataSocket.getInputStream(),
					BUFFER_SIZE);
			bos = new BufferedOutputStream(os, BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_SIZE];
			int len = -1;
			while ((len = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
//				mTransferedLength+=len;
				if (null != listener) {
					listener.onRequestFtpDataTransfered(len,
							IFTPBaseListener.TYPE_DOWNLOAD);
				}
//				Log.i(TAG, "downloadFile len = " + len);
			}
			bos.flush();
			if (null != listener) {
				listener.onRequestFtpDataCompleted(IFTPBaseListener.TYPE_DOWNLOAD);
			}
		} catch (IOException e) {
			if (null != listener) {
				listener.onRequestFtpDataError(e.getMessage(),
						IFTPBaseListener.TYPE_DOWNLOAD);
			}
			e.printStackTrace();
		} finally {
			try {
				if (null != bos) {
					bos.close();
					bos = null;
				}
				if (null != bis) {
					bis.close();
					bis = null;
				}
				if (null != dataSocket) {
					dataSocket.close();
					dataSocket = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		response = readLineResponse();
		code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "downloadFile �ڶ���<< " + response);
		}
		// ������ɣ�200-299֮���ǳɹ���(226 Transfer complete. 577 bytes transferred.
		// 37.57 KB/sec.)
		if (!FTPResponse.isPositiveCompletion(code)) {
			String errorMsg = " �����ļ����� " + response;
			mTransferedLength = 0;
			if (null != listener) {
				listener.onRequestFtpDataError(errorMsg,
						IFTPBaseListener.TYPE_DOWNLOAD);
			}
			throw new IOException(errorMsg);
		}
	}

	/**
	 * ���ص����ļ�
	 * 
	 * @param filename
	 * @param storePath
	 *            ���ش洢·�����������ļ���
	 * @param listener
	 * @throws IOException
	 */
	public void downloadFile(String filename, String storePath,
			IFTPDataListener listener) throws IOException {
		File dir = new File(storePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(storePath + "/" + filename);
		FileOutputStream fos = new FileOutputStream(file);
		downloadFile(filename, fos, listener);
	}

	/**
	 * ���ض���ļ����洢·�������ļ���׺�����з���
	 * 
	 * @param filenames
	 * @param listener
	 * @throws IOException
	 */
	public void downloadMulti(List<Record> downloadList,
			IFTPDataMultiListener listener) throws IOException {
		this.mMultiListener = listener;
		this.mMultlRecordList = downloadList;
		this.mType = IFTPBaseListener.TYPE_DOWNLOAD_MULTI;
		int size = downloadList.size();
		mTotalCount = size;
		for (int i = 0; i < size; i++) {
			Record download = downloadList.get(i);
			String filename = download.getFilename();
			String storePath = FileUtil.getStoragePathByFilename(filename);
			downloadFile(filename, storePath, mDataMultiListener);
		}
	}

	/**
	 * ����һ��PASV���������ģʽΪ����ģʽ��֮���ȡip��port ���ٸ��ݵõ���ip��port���һ���µ�Socket
	 * 
	 * @throws IOException
	 */
	protected Socket getDataSocket() throws IOException {

		// if(null!=mDataSocket)
		// {
		// if(isLog)
		// {
		// Log.i(TAG,"�������Ӳ�Ϊ�գ������������Ƿ�����"+mDataSocket.isConnected());
		// }
		// return mDataSocket;
		// }

		pasv();
		String response = readLineResponse();
		int code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "getDataSocket<< " + response);
		}
		if (!FTPResponse.isPositiveCompletion(code)) {
			throw new IOException(" �������ñ���ģʽ " + response);
		}

		// String response = getResponse();
		// Log.i(TAG, getResponse());
		// if (getResponse() <= 0) {
		// throw new IOException("���ñ���ģʽ֮��û�л�ȡ����Ӧֵ");
		// }
		// String firstLine = mResponseList.get(0);
		String ip = null;
		int port = -1;
		int opening = response.indexOf('(');
		int closing = response.indexOf(')', opening + 1);
		if (closing > 0) {
			String dataLink = response.substring(opening + 1, closing);
			if (isLog) {
				Log.i(TAG, "dataLink " + dataLink);
			}
			StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
			try {
				ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
						+ tokenizer.nextToken() + "." + tokenizer.nextToken();
				port = Integer.parseInt(tokenizer.nextToken()) * 256
						+ Integer.parseInt(tokenizer.nextToken());
			} catch (Exception e) {
				throw new IOException(
						"SimpleFTP received bad data link information: "
								+ response);
			}
		}
		if (isLog) {
			Log.i(TAG, "ip:" + ip + " port:" + port);
		}
		Socket dataSocket = new Socket(ip, port);
		// mDataSocket.setSoTimeout(mDataTimeOut);
		// mDataInput = mDataSocket.getInputStream();
		// mDataOutput = mDataSocket.getOutputStream();
		// mDataSocket.setKeepAlive(false);
		return dataSocket;
	}

	// public int getmDataTimeOut() {
	// return mDataTimeOut;
	// }
	//
	// public void setmDataTimeOut(int mDataTimeOut) {
	// this.mDataTimeOut = mDataTimeOut;
	// }

	/**
	 * ���ļ��ϴ�/��������
	 */
	private void reset() {
		mTotalCount = -1;
		mTransferedLength = -1;
		mTranferedCount = -1;
		isStart = false;
		mType = -1;
		mMultlRecordList = null;
	}

	/**
	 * ����ftp��������������Ŀ¼�б��� ע�⣺linux�¶��ǹ̶��ĸ�ʽ��ȥ���ո�֮���ܹ�9������Ƿ����ļ��У�����һ���Ȩ�����
	 * �Ŀ�ʼ�Ǹ��ַ��ǲ���d����d����Ŀ¼����-���Ǹ��ļ��� ��5�����ļ���С���ļ���Ϊ0������6�����·ݣ���7�������ڣ���8����ʱ�䣬��9��������
	 * 
	 * @param line
	 *            ���� -rw-rw-rw- 1 user group 3269 Aug 31 16:53
	 *            2fb8f933-403c-4b5b-9f1d-4c5587081df4_filebrowser_file.PNG
	 * @return
	 */
	private FTPFile parseDirs(String line) {
		FTPFile file = null;
		if (!TextUtils.isEmpty(line)) {
			String[] list = line.split("\\s");
			if (null != list) {
				List<String> tempList = new ArrayList<String>();
				int len = list.length;
				for (int i = 0; i < len; i++) {
					String s = list[i];
					// ȥ���ָ�������ַ����ǿո�֮�󣬽�ʣ��9��
					if (!TextUtils.isEmpty(s)) {
						tempList.add(s);
					}
				}
				int size = tempList.size();
				if (size > 0) {
					String right = tempList.get(0);
					file = new FTPFile();
					// Ŀ¼
					if (right.startsWith("d")) {
						file.setType(FTPFile.TYPE_DIRECTORY);
					} else if (right.startsWith("-")) {
						// ���ݣ����ļ�
						file.setType(FTPFile.TYPE_FILE);
					}
				}
				if (size > 4) {
					// �ļ���С
					String length = tempList.get(4);
					if (null != file && file.isFile()) {
						file.setSize((Long.valueOf(length)));
					}
				}
				StringBuilder sb = new StringBuilder();
				if (size > 5) {
					// �������ı���û����ݣ������Լ���������
					String year = StringUtil.formatDate(
							System.currentTimeMillis(), "yyyy");
					sb.append(year);
					sb.append("-");
					// �·�
					String month = tempList.get(5);
					// ��������Ӣ�ĵģ�תΪ����
					String numMonth = StringUtil.getMonthByEn(month);
					sb.append(numMonth);
					sb.append("-");
				}
				if (size > 6) {
					// ����
					String day = tempList.get(6);
					sb.append(day);
					sb.append(" ");
				}
				if (size > 7) {
					// ʱ��
					String time = tempList.get(7);
					sb.append(time);
				}
				if (null != file) {
					file.setTime(sb.toString());
				}
				if (size > 8) {
					// ����
					// String name = tempList.get(8);
					// file.setName(name);
					StringBuilder name = new StringBuilder();
					// �ļ������пո�������������ʱ����Ҫ�Լ� ������װ����
					for (int i = 8; i < size; i++) {
						String temp = tempList.get(i);
						name.append(temp);
						if (i != size - 1) {
							name.append(" ");
						}
					}
					file.setName(name.toString());
				}
			}
		}
		return file;
	}

	/**
	 * ���ļ��ϴ�/���صļ���ʵ��
	 */
	private IFTPDataListener mDataMultiListener = new IFTPDataListener() {

		@Override
		public void onRequestFtpDataStart(int type) {
			// TODO Auto-generated method stub
			if (!isStart) {
				if (null != mMultiListener) {
					mMultiListener.onRequestFtpDataStart(FTPClient.this.mType);
				}
				isStart = true;
			}
		}

		@Override
		public void onRequestFtpDataError(String errorMsg, int type) {
			// TODO Auto-generated method stub
			if (null != mMultiListener) {
				mMultiListener.onRequestFtpDataError(errorMsg,
						FTPClient.this.mType);
			}
			reset();
		}

		@Override
		public void onRequestFtpDataTransfered(int transeredLength, int type) {
			// TODO Auto-generated method stub
			mTransferedLength += transeredLength;
			if (null != mMultiListener) {
				mMultiListener.onRequestFtpDataTransfered(mTransferedLength,
						FTPClient.this.mType);
			}
		}

		@Override
		public void onRequestFtpDataCompleted(int type) {
			// TODO Auto-generated method stub
			if (null != mMultiListener) {
				mMultiListener.onRequestFtpDataOneFileCompleted(
						mMultlRecordList.get(mTranferedCount),
						FTPClient.this.mType);
			}
			Log.i(TAG,"mTranferedCount="+mTranferedCount
					+" mTotalCount ="+mTotalCount+" mTransferedLength = "+mTransferedLength);
			if (mTranferedCount == mTotalCount - 1) {
				if (null != mMultiListener) {
					mMultiListener
							.onRequestFtpDataCompleted(FTPClient.this.mType);
				}
				reset();
			} else {
				++mTranferedCount;
			}
		}
	};
}
