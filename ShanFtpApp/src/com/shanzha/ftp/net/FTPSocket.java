package com.shanzha.ftp.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import android.util.Log;

/**
 * Ftp��Socket������
 * 
 * @author ShanZha
 * @date 2012-9-26 10:19
 */
public abstract class FTPSocket {
	
	private static final String TAG = "FTPSocket";
	/**
	 * ����
	 */
	public static final String NETASCII_EOL = "\r\n";
	/**
	 * FtpĬ�ϵĿ��ƶ˿�
	 */
	private static final int CONTROL_PORT = 21;
	/**
	 * FtpĬ�ϵ����ݴ���˿�
	 */
	private static final int DATA_PORT = 20;
	/**
	 * ��������������
	 */
	protected OutputStream mOutput;
	/**
	 * ������Ӧ��������
	 */
	protected InputStream mInput;
	/**
	 * ���ӳ�ʱ
	 */
	protected int mTimeOut;
	/**
	 * ��������ftp
	 */
	protected Socket mSocket;

	public FTPSocket() {
		mSocket = null;
		mOutput = null;
		mInput = null;
		mTimeOut = 60*1000;
	}

	/**
	 * Socket����
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
	 * Socket����Ĭ�Ͽ��ƶ˿�
	 * 
	 * @param host
	 * @throws IOException
	 */
	public void connect(String host) throws IOException {
		connect(host, CONTROL_PORT);
	}
	
	/**
	 * �������֮��ĳ�ʼ��
	 * @throws IOException
	 */
	protected void connectAction() throws IOException
	{
		mSocket.setSoTimeout(mTimeOut);
		mOutput = mSocket.getOutputStream();
		mInput = mSocket.getInputStream();
	}

	/**
	 * �ر����Ӳ��ͷ���Դ
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
	 * �Ƿ�������
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
