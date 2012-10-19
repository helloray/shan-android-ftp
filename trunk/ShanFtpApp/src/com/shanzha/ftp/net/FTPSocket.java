package com.shanzha.ftp.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import android.util.Log;

/**
 * Ftp的Socket管理类
 * 
 * @author ShanZha
 * @date 2012-9-26 10:19
 */
public abstract class FTPSocket {
	
	private static final String TAG = "FTPSocket";
	/**
	 * 换行
	 */
	public static final String NETASCII_EOL = "\r\n";
	/**
	 * Ftp默认的控制端口
	 */
	private static final int CONTROL_PORT = 21;
	/**
	 * Ftp默认的数据传输端口
	 */
	private static final int DATA_PORT = 20;
	/**
	 * 发送命令的输出流
	 */
	protected OutputStream mOutput;
	/**
	 * 接受相应的输入流
	 */
	protected InputStream mInput;
	/**
	 * 连接超时
	 */
	protected int mTimeOut;
	/**
	 * 用于连接ftp
	 */
	protected Socket mSocket;

	public FTPSocket() {
		mSocket = null;
		mOutput = null;
		mInput = null;
		mTimeOut = 60*1000;
	}

	/**
	 * Socket连接
	 * 
	 * @param host
	 * @param port
	 * @throws IOException
	 */
	public void connect(String host, int port) throws IOException {
		Log.i(TAG,"Connect Ip: "+host+" port: "+port);
		mSocket = new Socket();
		mSocket.connect(new InetSocketAddress(host, port));
		connectAction();
	}

	/**
	 * Socket连接默认控制端口
	 * 
	 * @param host
	 * @throws IOException
	 */
	public void connect(String host) throws IOException {
		connect(host, CONTROL_PORT);
	}
	
	/**
	 * 连接完成之后的初始化
	 * @throws IOException
	 */
	protected void connectAction() throws IOException
	{
		mSocket.setSoTimeout(mTimeOut);
		mOutput = mSocket.getOutputStream();
		mInput = mSocket.getInputStream();
	}

	/**
	 * 关闭连接并释放资源
	 * 
	 * @throws IOException
	 */
	protected void disconnect() throws IOException {
		if (null != mSocket) {
			mSocket.close();
			mSocket = null;
		}
		if (null != mOutput) {
			mOutput.close();
			mOutput = null;
		}
		if (null != mInput) {
			mInput.close();
			mInput = null;
		}
	}

	/**
	 * 是否已连接
	 * 
	 * @return
	 */
	public boolean isConnected() {
		if (null == mSocket) {
			return false;
		}
		return mSocket.isConnected();
	}

	public int getmTimeOut() {
		return mTimeOut;
	}

	public void setmTimeOut(int mTimeOut) {
		this.mTimeOut = mTimeOut;
	}

}
