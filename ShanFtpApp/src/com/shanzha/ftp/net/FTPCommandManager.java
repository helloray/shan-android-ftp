package com.shanzha.ftp.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.shanzha.ftp.util.FTPCommand;

import android.util.Log;

/**
 * 负责Ftp的各种命令管理
 * 
 * @author ShanZha
 * @date 2012-9-26 10:46
 */
public class FTPCommandManager extends FTPSocket {

	private static final String TAG = "FTPCommandManager";
	/**
	 * 控制协议默认的编码方式
	 */
//	protected static final String DEFAULT_CONTROL_ENCODING = "ISO-8859-1";
	protected static final String DEFAULT_CONTROL_ENCODING = "UTF-8";
	/**
	 * 编码方式
	 */
	private String mEncoding;
	/**
	 * 返回码（无论是否多个步骤均表示第一行的响应码）
	 */
//	protected int mResponseCode;
	/**
	 * 发送一个命令之后的响应字符串
	 */
	private StringBuilder mCurrentResponse = new StringBuilder();
//	protected List<String> mResponseList = new ArrayList<String>();
	/**
	 * 所有操作的响应列表，每行为一个元素
	 */
	// protected List<String> mResponseList = new ArrayList<String>();
	/**
	 * 读取响应的一个字符串缓存
	 */
	private StringBuilder mOutputBuffer = new StringBuilder();
	/**
	 * 控制协议的字符输入流
	 */
	private BufferedReader mControlReader;
	/**
	 * 控制协议的字符输出流
	 */
	private BufferedWriter mControlWriter;

	/**
	 * 构造参数
	 */
	protected FTPCommandManager() {
		super();

		mEncoding = DEFAULT_CONTROL_ENCODING;
	}

	/**
	 * 连接完成之后的基本初始化
	 */
	@Override
	protected void connectAction() throws IOException {
		super.connectAction();

		mControlReader = new BufferedReader(new InputStreamReader(
				mInput, mEncoding));

		mControlWriter = new BufferedWriter(new OutputStreamWriter(
				mOutput, mEncoding));
	}

	/**
	 * 发送一个USER命令
	 * 
	 * @param username
	 * @throws IOException
	 */
	protected void user(String username) throws IOException {
		sendCommand(FTPCommand.USER, username);
	}

	/**
	 * 发送一个PASS命令
	 * 
	 * @param password
	 * @throws IOException
	 */
	protected void pass(String password) throws IOException {
		sendCommand(FTPCommand.PASS, password);
	}

	/**
	 * 发送一个ACCT命令
	 * 
	 * @param account
	 * @throws IOException
	 */
	protected void acct(String account) throws IOException {
		sendCommand(FTPCommand.ACCT, account);
	}

	/**
	 * 发送一个ABOR命令
	 * 
	 * @throws IOException
	 */
	protected void abor() throws IOException {
		sendCommand(FTPCommand.ABOR, null);
	}

	/**
	 * 发送一个CWD命令
	 * 
	 * @param directory
	 * @throws IOException
	 */
	protected void cwd(String directory) throws IOException {
		sendCommand(FTPCommand.CWD, directory);
	}

	/**
	 * 发送一个CDUP命令
	 * 
	 * @throws IOException
	 */
	protected void cdup() throws IOException {
		sendCommand(FTPCommand.CDUP, null);
	}

	/**
	 * 发送一个QUIT命令
	 * 
	 * @throws IOException
	 */
	protected void quit() throws IOException {
		sendCommand(FTPCommand.QUIT, null);
	}

	/**
	 * 发送一个REIN命令
	 * 
	 * @throws IOException
	 */
	protected void rein() throws IOException {
		sendCommand(FTPCommand.REIN, null);
	}

	/**
	 * 发送一个SMNT命令
	 * 
	 * @param dir
	 * @throws IOException
	 */
	protected void smnt(String dir) throws IOException {
		sendCommand(FTPCommand.SMNT, dir);
	}

	/**
	 * 发送一个PASV命令
	 * 
	 * @throws IOException
	 */
	protected void pasv() throws IOException {
		sendCommand(FTPCommand.PASV, null);
	}

	/**
	 * 发送一个RETR命令（即下载）
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void retr(String pathname) throws IOException {
		sendCommand(FTPCommand.RETR, pathname);
	}

	/**
	 * 发送一个STOR命令
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void stor(String pathname) throws IOException {
		sendCommand(FTPCommand.STOR, pathname);
	}

	/**
	 * 发送一个STOU命令
	 * 
	 * @throws IOException
	 */
	protected void stou() throws IOException {
		sendCommand(FTPCommand.STOU, null);
	}

	/**
	 * 发送一个STOU命令
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void stou(String pathname) throws IOException {
		sendCommand(FTPCommand.STOU, pathname);
	}

	/**
	 * 发送一个APPE命令，即上传
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void appe(String pathname) throws IOException {
		 sendCommand(FTPCommand.APPE, pathname);
	}

	/**
	 * 发送一个ALLO命令
	 * 
	 * @param bytes
	 * @throws IOException
	 */
	protected void allo(int bytes) throws IOException {
		 sendCommand(FTPCommand.ALLO,Integer.toString(bytes));
	}

	/**
	 * 发送一个REST命令
	 * 
	 * @param marker
	 * @throws IOException
	 */
	protected void rest(String marker) throws IOException {
		 sendCommand(FTPCommand.REST, marker);
	}

	/**
	 * 发送一个DELE命令
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void dele(String pathname) throws IOException {
		 sendCommand(FTPCommand.DELE, pathname);
	}

	/**
	 * 发送一个RMD命令
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void rmd(String pathname) throws IOException {
		 sendCommand(FTPCommand.RMD, pathname);
	}

	/**
	 * 发送一个MKD命令
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void mkd(String pathname) throws IOException {
		sendCommand(FTPCommand.MKD, pathname);
	}

	/**
	 * 发送一个PWD命令
	 * 
	 * @throws IOException
	 */
	protected void pwd() throws IOException {
		 sendCommand(FTPCommand.PWD, null);
	}

	/**
	 * 发送一个LIST命令
	 * 
	 * @return 响应码
	 * @throws IOException
	 */
	protected void list() throws IOException {
		sendCommand(FTPCommand.LIST, null);
	}

	/**
	 * 发送一个LIST命令
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void list(String pathname) throws IOException {
		sendCommand(FTPCommand.LIST, pathname);
	}

	/**
	 * 发送一个NLST命令
	 * 
	 * @throws IOException
	 */
	protected void nlst() throws IOException {
		sendCommand(FTPCommand.NLST, null);
	}

	/**
	 * 发送一个NLST命令
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void nlst(String pathname) throws IOException {
		 sendCommand(FTPCommand.NLST, pathname);
	}

	/**
	 * 发送一个SITE命令
	 * 
	 * @param parameters
	 * @throws IOException
	 */
	protected void site(String parameters) throws IOException {
		 sendCommand(FTPCommand.SITE, parameters);
	}

	/**
	 * 发送一个SYST命令
	 * 
	 * @throws IOException
	 */
	protected void syst() throws IOException {
		 sendCommand(FTPCommand.SYST, null);
	}

	/**
	 * 发送一个STAT
	 * 
	 * @throws IOException
	 */
	protected void stat() throws IOException {
		 sendCommand(FTPCommand.STAT, null);
	}

	/**
	 * 发送一个STAT命令
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void stat(String pathname) throws IOException {
		 sendCommand(FTPCommand.STAT, pathname);
	}

	/**
	 * 发送一个HELP命令
	 * 
	 * @throws IOException
	 */
	protected void help() throws IOException {
		 sendCommand(FTPCommand.HELP, null);
	}

	/**
	 * 发送一个HELP命令
	 * 
	 * @param command
	 * @throws IOException
	 */
	protected void help(String command) throws IOException {
		 sendCommand(FTPCommand.HELP, command);
	}

	/**
	 * 发送一个NOOP命令
	 * 
	 * @throws IOException
	 */
	protected void noop() throws IOException {
		 sendCommand(FTPCommand.NOOP, null);
	}
	/**
	 * 发送一个命令
	 * 
	 * @param command
	 * @param arg
	 *            命令参数
	 * @return
	 * @throws IOException
	 */
	private void sendCommand(int cmd, String arg) throws IOException {
		String command = FTPCommand.getCommand(cmd);
		mOutputBuffer.setLength(0);
		mOutputBuffer.append(command);

		if (null != arg) {
			mOutputBuffer.append(" ");
			mOutputBuffer.append(arg);
		}
		mOutputBuffer.append(FTPSocket.NETASCII_EOL);
		if (null == mControlWriter) {
			throw new IOException("连接未打开");
		}
		Log.i(TAG, "sendCommand()>> " + mOutputBuffer.toString());
		try {
			mControlWriter.write(mOutputBuffer.toString());
			mControlWriter.flush();
		} catch (SocketException e) {
			if (!isConnected()) {
				throw new IOException("连接已经断开");
			} else {
				throw e;
			}
		}

//		setResponse();
//		String line = null;
//		line = mControlReader.readLine();
//		Log.i(TAG,"response first line = "+line);
//		if (null == line) {
//			throw new IOException("没有响应值");
//		}
//
//		if (line.length() > 3) {
//			String code = line.substring(0, 3);
//			mResponseCode = Integer.valueOf(code);
//		} else {
//			throw new IOException("返回响应被截断");
//		}
//		mResponseList.add(line);
		 
	}

//	/**
//	 * 设置响应，响应码以及一些响应信息
//	 * 
//	 * @throws IOException
//	 */
//	private void setResponse() throws IOException {
//		// 读取响应
//		String line = mControlReader.readLine();
//		mCurrentResponse.setLength(0);
//		mCurrentResponse.append(line);
//		Log.i(TAG, "setResponse() first line = " + line);
//		if (null == line) {
//			throw new IOException("没有响应值");
//		}
//
//		if (line.length() > 3) {
//			String code = line.substring(0, 3);
//			mResponseCode = Integer.valueOf(code);
//		} else {
//			throw new IOException("返回响应被截断");
//		}
//		
//		Log.i(TAG," mResponseCode = "+mResponseCode);
//		if (mResponseCode == 220// 220： 对新用户服务准备就绪（只有每次登陆的时候会有）
//				||mResponseCode==150)//150:文件状态正常，准备打开数据连接，即可以获取数据
//		{
//			//读取下一行即真正的响应
//			String secondLine = mControlReader.readLine();
//			Log.i(TAG,"setResponse() second line："+secondLine);
//			mCurrentResponse.setLength(0);
//			mCurrentResponse.append(secondLine);
//
//			if (secondLine.length() > 3) {
//				String code = secondLine.substring(0, 3);
//				mResponseCode = Integer.valueOf(code);
//			} else {
//				throw new IOException("第二行返回响应被截断");
//			}
//		} 
//		Log.i(TAG,"setResponse() over  = "+mResponseCode);
//		// mResponseList.clear();
//		// mResponseList.add(line);
//		// //读取第二行，有些有两个步骤，如登陆，先要用户名，其成功之后再要密码
//
//		// while(null!=(line = mControlReader.readLine())) {
//		// // mBufferResponse.append(line);
//		// Log.i(TAG," setResponse() second line = "+line);
//		// if(line.length()>3)
//		// {
//		// String temp = line.substring(0, 3);
//		// mResponseContinueCode = Integer.valueOf(temp);
//		// }else
//		// {
//		// throw new IOException("返回响应被截断");
//		// }
//		// mResponseList.add(line);
//		// }
//	}
	protected void flushResponse() throws IOException
	{
		Log.i(TAG,"flushResponse() before...");
//		String res = mControlReader.readLine();
//		Log.i(TAG,"flushResponse() res1 = "+res);
//		res = mControlReader.readLine();
//		Log.i(TAG,"flushResponse() res2 = "+res);
		String res = null;
		while((res = mControlReader.readLine())!=null)
		{
			Log.i(TAG,"flushResponse() res = "+res);
		}
		Log.i(TAG,"flushResponse() end...");
		 
	}
	/**
	 * 读取当前行的响应
	 * @return
	 * @throws IOException
	 */
	protected String readLineResponse() throws IOException
	{
		String response = mControlReader.readLine();
		mCurrentResponse.setLength(0);
		mCurrentResponse.append(response);
//		Log.i(TAG,"response<< "+response);
		return response;
	}
	
	/**
	 * 返回发送本次命令之后的响应码
	 * 
	 * @return
	 */
	protected int getResponseCode() throws IOException
	{
		String res = mCurrentResponse.toString();
		if(null==res)
		{
			throw new IOException("没有响应异常");
		}
		if(res.length()>3)
		{
			String resCode = res.substring(0,3);
			return Integer.valueOf(resCode);
		}else
		{
			throw new IOException("响应结果异常");
		}
	}

	public void setControlEncoding(String encoding) {
		this.mEncoding = encoding;
	}

	public String getControlEncoding() {
		return this.mEncoding;
	}
}
