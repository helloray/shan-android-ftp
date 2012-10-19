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
 * FTP数据操作请求处理类 (数据连接只能每次都新建一个，原本想模仿控制连接，但因为数据连接的端口和ip是发送完PASV或者PORT模式
 * 命令之后返回来的，FTP服务器只监听这个端口，所以每次数据请求必须新建一个Socket数据连接，这是FTP协议
 * 的规则，控制连接则可以只有一个Socket连接，因为FTP服务器一直在监听这个端口(21))
 * 数据获取主要分四步：
 * 1、发送PASV（常用）或者PORT请求模式，根据返回值，获数据获取ip和端口
 * 2、发送具体的数据请求，获取返回值，如果是150开头，则数据端口打开，可进行数据交互
 * 3、数据交互
 * 4、获取数据交互是否完毕（这步骤不能省略，否则控制协议的响应对应不上了）
 * 
 * @author ShanZha
 * @date 2012-9-26 14:24
 */
public class FTPClient extends FTPCommandManager {

	private static final String TAG = "FTPClient";
	/**
	 * 是否打印日志
	 */
	private boolean isLog = true;
	// /**
	// * 数据协议超时
	// */
	// private int mDataTimeOut;
	// /**
	// * 数据传输的Socket
	// */
	// private Socket mDataSocket;
	// /**
	// * 数据输入流
	// */
	// private InputStream mDataInput;
	// /**
	// * 数据输出流
	// */
	// private OutputStream mDataOutput;
	/**
	 * 缓冲流的缓冲大小
	 */
	private static final int BUFFER_SIZE = 4 * 1024;
	/****** 多文件上传/下载记录变量(BEGIN) **********/
	private IFTPDataMultiListener mMultiListener;
	private int mTotalCount = -1;
	private int mTransferedLength = -1;
	private int mTranferedCount = 0;
	private boolean isStart = false;
	private List<Record> mMultlRecordList = null;
	private int mType;// 标记上传多个/还是下载多个

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
	 * 登陆(分两个步骤：1 发送USER命令，如果返回3XX则表示此步骤成功 2：步骤1成功之后，再发送
	 * PASS命令，如果返回2XX则表示整个登陆成功)
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
		// 连接没有成功则直接登录失败 ( 220 Serv-U FTP Server v12.1 ready...)
		if (!FTPResponse.isPositiveCompletion(code)) {
			return false;
		}
		user(username);
		// 用户名没有成功则直接返回失败
		// if (!FTPResponse.isPositiveCompletion(code)) {
		// return false;
		// }
		response = readLineResponse();
		code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "login<< username code = " + code);
		}
		// 如果用户名成功了，还需验证密码则往下走，否则直接失败（331 User name okay, need password.）
		if (!FTPResponse.isPositiveIntermediate(code)) {
			return false;
		}
		pass(pswd);
		response = readLineResponse();
		code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "login<< pswd code = " + code);
		}
		// 需要密码，则根据密码命令成功与否即可
		return FTPResponse.isPositiveCompletion(code);
	}

	/**
	 * 注销登陆
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
	 * 改变操作目录
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
	 * 回到父目录
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
	 * 创建一个目录
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
	 * 放弃当前数据请求
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
//			throw new IOException("终止当前数据请求失败 " + response);
//		}
//		response = readLineResponse();
//		code = getResponseCode();
//		if (isLog) {
//			Log.i(TAG, "abortCurrDataConnect second line << " + response);
//		}
//		if (!FTPResponse.isPositiveCompletion(code)) {
//			throw new IOException("终止当前数据请求失败 " + response);
//		}
		//如果是451 Operation aborted due to ABOR command，则继续读下一行，否则不读下一行
		if(FTPResponse.isNegativeTransient(code))
		{
			response = readLineResponse();
			code = getResponseCode();
			if (isLog) {
				Log.i(TAG, "abortCurrDataConnect second line << " + response);
			}
		}
		//如果不是226 Abort successfu，则终止失败
		if(!FTPResponse.isPositiveCompletion(code))
		{
			throw new IOException("终止当前数据请求失败 " + response);
		}
	}

	/**
	 * 获取指定目录下的所有文件信息
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
			Log.i(TAG, "listFiles 第一步<< " + response);
		}
		// 假如初步响应不成功即响应码不是100-199之间(只有初始打开数据连接时成功时才是1开头)
		// ( 150 Opening ASCII mode data connection for /bin/ls)
		if (!FTPResponse.isPositivePreliminary(code)) {
			throw new IOException(" 获取指定目录下的文件出错 " + response);
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
			Log.i(TAG, "listFiles 第二步<< " + response);
		}
		// 传输完成（200-299之间是成功）(226 Transfer complete. 577 bytes transferred.
		// 37.57 KB/sec.)
		if (!FTPResponse.isPositiveCompletion(code)) {
			throw new IOException(" 获取指定目录下的文件出错 " + response);
		}
		return files;
	}

	/**
	 * 获取指定目录下的所有文件名称
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
			Log.i(TAG, "listNames 第一步<< " + response);
		}
		// 假如初步响应不成功即响应码不是100-199之间(只有初始打开数据连接时成功时才是1开头)
		// ( 150 Opening ASCII mode data connection for /bin/ls)
		if (!FTPResponse.isPositivePreliminary(code)) {
			throw new IOException(" 获取指定目录下的文件出错 " + response);
		}
		response = readLineResponse();
		code = getResponseCode();
		if (isLog) {
			Log.i(TAG, "listNames 第二步<< " + response);
		}
		// 传输完成（200-299之间是成功）(226 Transfer complete. 577 bytes transferred.
		// 37.57 KB/sec.)
		if (!FTPResponse.isPositiveCompletion(code)) {
			throw new IOException(" 获取指定目录下的文件出错 " + response);
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
	 * 上传单个文件到服务器
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
			Log.i(TAG, "uploadFile 第一步<< " + response);
		}
		// 假如初步响应不成功即响应码不是100-199之间(只有初始打开数据连接时成功时才是1开头)
		// ( 150 Opening ASCII mode data connection for /bin/ls)
		if (!FTPResponse.isPositivePreliminary(code)) {
			throw new IOException(" 上传文件出错 " + response);
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
			Log.i(TAG, "uploadFile 第二步<< " + response);
		}
		// 传输完成（200-299之间是成功）(226 Transfer complete. 577 bytes transferred.
		// 37.57 KB/sec.)
		if (!FTPResponse.isPositiveCompletion(code)) {
			throw new IOException(" 上传文件出错 " + response);
		}
	}

	/**
	 * 上传单个文件到服务器
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
	 * 上传多个文件到服务器
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
	 * 下载单个文件
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
			Log.i(TAG, "downloadFile 第一步<< " + response);
		}
		// 假如初步响应不成功即响应码不是100-199之间(只有初始打开数据连接时成功时才是1开头)
		// ( 150 Opening ASCII mode data connection for /bin/ls)
		if (!FTPResponse.isPositivePreliminary(code)) {
			String error = " 下载文件出错 " + response;
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
			Log.i(TAG, "downloadFile 第二步<< " + response);
		}
		// 传输完成（200-299之间是成功）(226 Transfer complete. 577 bytes transferred.
		// 37.57 KB/sec.)
		if (!FTPResponse.isPositiveCompletion(code)) {
			String errorMsg = " 下载文件出错 " + response;
			mTransferedLength = 0;
			if (null != listener) {
				listener.onRequestFtpDataError(errorMsg,
						IFTPBaseListener.TYPE_DOWNLOAD);
			}
			throw new IOException(errorMsg);
		}
	}

	/**
	 * 下载单个文件
	 * 
	 * @param filename
	 * @param storePath
	 *            本地存储路径，不包括文件名
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
	 * 下载多个文件，存储路径根据文件后缀名自行分配
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
	 * 发送一个PASV命令（即设置模式为被动模式）之后获取ip和port ，再根据得到的ip和port组成一个新的Socket
	 * 
	 * @throws IOException
	 */
	protected Socket getDataSocket() throws IOException {

		// if(null!=mDataSocket)
		// {
		// if(isLog)
		// {
		// Log.i(TAG,"数据连接不为空，但数据连接是否连接"+mDataSocket.isConnected());
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
			throw new IOException(" 不能设置被动模式 " + response);
		}

		// String response = getResponse();
		// Log.i(TAG, getResponse());
		// if (getResponse() <= 0) {
		// throw new IOException("设置被动模式之后，没有获取到响应值");
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
	 * 多文件上传/下载重置
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
	 * 解析ftp服务器返回来的目录列表结果 注意：linux下都是固定的格式，去除空格之后总共9项，区别是否是文件夹，看第一项（即权限那项）
	 * 的开始那个字符是不是d，是d则是目录，是-则是个文件。 第5项是文件大小（文件夹为0），第6项是月份，第7项是日期，第8项是时间，第9项是名字
	 * 
	 * @param line
	 *            形如 -rw-rw-rw- 1 user group 3269 Aug 31 16:53
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
					// 去除分割出来的字符串是空格之后，仅剩下9项
					if (!TextUtils.isEmpty(s)) {
						tempList.add(s);
					}
				}
				int size = tempList.size();
				if (size > 0) {
					String right = tempList.get(0);
					file = new FTPFile();
					// 目录
					if (right.startsWith("d")) {
						file.setType(FTPFile.TYPE_DIRECTORY);
					} else if (right.startsWith("-")) {
						// 内容，即文件
						file.setType(FTPFile.TYPE_FILE);
					}
				}
				if (size > 4) {
					// 文件大小
					String length = tempList.get(4);
					if (null != file && file.isFile()) {
						file.setSize((Long.valueOf(length)));
					}
				}
				StringBuilder sb = new StringBuilder();
				if (size > 5) {
					// 返回来的本身没有年份，所以自己主动加上
					String year = StringUtil.formatDate(
							System.currentTimeMillis(), "yyyy");
					sb.append(year);
					sb.append("-");
					// 月份
					String month = tempList.get(5);
					// 返回来是英文的，转为数字
					String numMonth = StringUtil.getMonthByEn(month);
					sb.append(numMonth);
					sb.append("-");
				}
				if (size > 6) {
					// 日期
					String day = tempList.get(6);
					sb.append(day);
					sb.append(" ");
				}
				if (size > 7) {
					// 时间
					String time = tempList.get(7);
					sb.append(time);
				}
				if (null != file) {
					file.setTime(sb.toString());
				}
				if (size > 8) {
					// 名字
					// String name = tempList.get(8);
					// file.setName(name);
					StringBuilder name = new StringBuilder();
					// 文件名中有空格，则会多出来几项，此时，需要自己 重新组装名字
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
	 * 多文件上传/下载的监听实现
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
