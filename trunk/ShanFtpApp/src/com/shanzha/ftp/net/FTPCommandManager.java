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
 * ����Ftp�ĸ����������
 * 
 * @author ShanZha
 * @date 2012-9-26 10:46
 */
public class FTPCommandManager extends FTPSocket {

	private static final String TAG = "FTPCommandManager";
	/**
	 * ����Э��Ĭ�ϵı��뷽ʽ
	 */
//	protected static final String DEFAULT_CONTROL_ENCODING = "ISO-8859-1";
	protected static final String DEFAULT_CONTROL_ENCODING = "UTF-8";
	/**
	 * ���뷽ʽ
	 */
	private String mEncoding;
	/**
	 * �����루�����Ƿ����������ʾ��һ�е���Ӧ�룩
	 */
//	protected int mResponseCode;
	/**
	 * ����һ������֮�����Ӧ�ַ���
	 */
	private StringBuilder mCurrentResponse = new StringBuilder();
//	protected List<String> mResponseList = new ArrayList<String>();
	/**
	 * ���в�������Ӧ�б�ÿ��Ϊһ��Ԫ��
	 */
	// protected List<String> mResponseList = new ArrayList<String>();
	/**
	 * ��ȡ��Ӧ��һ���ַ�������
	 */
	private StringBuilder mOutputBuffer = new StringBuilder();
	/**
	 * ����Э����ַ�������
	 */
	private BufferedReader mControlReader;
	/**
	 * ����Э����ַ������
	 */
	private BufferedWriter mControlWriter;

	/**
	 * �������
	 */
	protected FTPCommandManager() {
		super();

		mEncoding = DEFAULT_CONTROL_ENCODING;
	}

	/**
	 * �������֮��Ļ�����ʼ��
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
	 * ����һ��USER����
	 * 
	 * @param username
	 * @throws IOException
	 */
	protected void user(String username) throws IOException {
		sendCommand(FTPCommand.USER, username);
	}

	/**
	 * ����һ��PASS����
	 * 
	 * @param password
	 * @throws IOException
	 */
	protected void pass(String password) throws IOException {
		sendCommand(FTPCommand.PASS, password);
	}

	/**
	 * ����һ��ACCT����
	 * 
	 * @param account
	 * @throws IOException
	 */
	protected void acct(String account) throws IOException {
		sendCommand(FTPCommand.ACCT, account);
	}

	/**
	 * ����һ��ABOR����
	 * 
	 * @throws IOException
	 */
	protected void abor() throws IOException {
		sendCommand(FTPCommand.ABOR, null);
	}

	/**
	 * ����һ��CWD����
	 * 
	 * @param directory
	 * @throws IOException
	 */
	protected void cwd(String directory) throws IOException {
		sendCommand(FTPCommand.CWD, directory);
	}

	/**
	 * ����һ��CDUP����
	 * 
	 * @throws IOException
	 */
	protected void cdup() throws IOException {
		sendCommand(FTPCommand.CDUP, null);
	}

	/**
	 * ����һ��QUIT����
	 * 
	 * @throws IOException
	 */
	protected void quit() throws IOException {
		sendCommand(FTPCommand.QUIT, null);
	}

	/**
	 * ����һ��REIN����
	 * 
	 * @throws IOException
	 */
	protected void rein() throws IOException {
		sendCommand(FTPCommand.REIN, null);
	}

	/**
	 * ����һ��SMNT����
	 * 
	 * @param dir
	 * @throws IOException
	 */
	protected void smnt(String dir) throws IOException {
		sendCommand(FTPCommand.SMNT, dir);
	}

	/**
	 * ����һ��PASV����
	 * 
	 * @throws IOException
	 */
	protected void pasv() throws IOException {
		sendCommand(FTPCommand.PASV, null);
	}

	/**
	 * ����һ��RETR��������أ�
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void retr(String pathname) throws IOException {
		sendCommand(FTPCommand.RETR, pathname);
	}

	/**
	 * ����һ��STOR����
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void stor(String pathname) throws IOException {
		sendCommand(FTPCommand.STOR, pathname);
	}

	/**
	 * ����һ��STOU����
	 * 
	 * @throws IOException
	 */
	protected void stou() throws IOException {
		sendCommand(FTPCommand.STOU, null);
	}

	/**
	 * ����һ��STOU����
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void stou(String pathname) throws IOException {
		sendCommand(FTPCommand.STOU, pathname);
	}

	/**
	 * ����һ��APPE������ϴ�
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void appe(String pathname) throws IOException {
		 sendCommand(FTPCommand.APPE, pathname);
	}

	/**
	 * ����һ��ALLO����
	 * 
	 * @param bytes
	 * @throws IOException
	 */
	protected void allo(int bytes) throws IOException {
		 sendCommand(FTPCommand.ALLO,Integer.toString(bytes));
	}

	/**
	 * ����һ��REST����
	 * 
	 * @param marker
	 * @throws IOException
	 */
	protected void rest(String marker) throws IOException {
		 sendCommand(FTPCommand.REST, marker);
	}

	/**
	 * ����һ��DELE����
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void dele(String pathname) throws IOException {
		 sendCommand(FTPCommand.DELE, pathname);
	}

	/**
	 * ����һ��RMD����
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void rmd(String pathname) throws IOException {
		 sendCommand(FTPCommand.RMD, pathname);
	}

	/**
	 * ����һ��MKD����
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void mkd(String pathname) throws IOException {
		sendCommand(FTPCommand.MKD, pathname);
	}

	/**
	 * ����һ��PWD����
	 * 
	 * @throws IOException
	 */
	protected void pwd() throws IOException {
		 sendCommand(FTPCommand.PWD, null);
	}

	/**
	 * ����һ��LIST����
	 * 
	 * @return ��Ӧ��
	 * @throws IOException
	 */
	protected void list() throws IOException {
		sendCommand(FTPCommand.LIST, null);
	}

	/**
	 * ����һ��LIST����
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void list(String pathname) throws IOException {
		sendCommand(FTPCommand.LIST, pathname);
	}

	/**
	 * ����һ��NLST����
	 * 
	 * @throws IOException
	 */
	protected void nlst() throws IOException {
		sendCommand(FTPCommand.NLST, null);
	}

	/**
	 * ����һ��NLST����
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void nlst(String pathname) throws IOException {
		 sendCommand(FTPCommand.NLST, pathname);
	}

	/**
	 * ����һ��SITE����
	 * 
	 * @param parameters
	 * @throws IOException
	 */
	protected void site(String parameters) throws IOException {
		 sendCommand(FTPCommand.SITE, parameters);
	}

	/**
	 * ����һ��SYST����
	 * 
	 * @throws IOException
	 */
	protected void syst() throws IOException {
		 sendCommand(FTPCommand.SYST, null);
	}

	/**
	 * ����һ��STAT
	 * 
	 * @throws IOException
	 */
	protected void stat() throws IOException {
		 sendCommand(FTPCommand.STAT, null);
	}

	/**
	 * ����һ��STAT����
	 * 
	 * @param pathname
	 * @throws IOException
	 */
	protected void stat(String pathname) throws IOException {
		 sendCommand(FTPCommand.STAT, pathname);
	}

	/**
	 * ����һ��HELP����
	 * 
	 * @throws IOException
	 */
	protected void help() throws IOException {
		 sendCommand(FTPCommand.HELP, null);
	}

	/**
	 * ����һ��HELP����
	 * 
	 * @param command
	 * @throws IOException
	 */
	protected void help(String command) throws IOException {
		 sendCommand(FTPCommand.HELP, command);
	}

	/**
	 * ����һ��NOOP����
	 * 
	 * @throws IOException
	 */
	protected void noop() throws IOException {
		 sendCommand(FTPCommand.NOOP, null);
	}
	/**
	 * ����һ������
	 * 
	 * @param command
	 * @param arg
	 *            �������
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
			throw new IOException("����δ��");
		}
		Log.i(TAG, "sendCommand()>> " + mOutputBuffer.toString());
		try {
			mControlWriter.write(mOutputBuffer.toString());
			mControlWriter.flush();
		} catch (SocketException e) {
			if (!isConnected()) {
				throw new IOException("�����Ѿ��Ͽ�");
			} else {
				throw e;
			}
		}

//		setResponse();
//		String line = null;
//		line = mControlReader.readLine();
//		Log.i(TAG,"response first line = "+line);
//		if (null == line) {
//			throw new IOException("û����Ӧֵ");
//		}
//
//		if (line.length() > 3) {
//			String code = line.substring(0, 3);
//			mResponseCode = Integer.valueOf(code);
//		} else {
//			throw new IOException("������Ӧ���ض�");
//		}
//		mResponseList.add(line);
		 
	}

//	/**
//	 * ������Ӧ����Ӧ���Լ�һЩ��Ӧ��Ϣ
//	 * 
//	 * @throws IOException
//	 */
//	private void setResponse() throws IOException {
//		// ��ȡ��Ӧ
//		String line = mControlReader.readLine();
//		mCurrentResponse.setLength(0);
//		mCurrentResponse.append(line);
//		Log.i(TAG, "setResponse() first line = " + line);
//		if (null == line) {
//			throw new IOException("û����Ӧֵ");
//		}
//
//		if (line.length() > 3) {
//			String code = line.substring(0, 3);
//			mResponseCode = Integer.valueOf(code);
//		} else {
//			throw new IOException("������Ӧ���ض�");
//		}
//		
//		Log.i(TAG," mResponseCode = "+mResponseCode);
//		if (mResponseCode == 220// 220�� �����û�����׼��������ֻ��ÿ�ε�½��ʱ����У�
//				||mResponseCode==150)//150:�ļ�״̬������׼�����������ӣ������Ի�ȡ����
//		{
//			//��ȡ��һ�м���������Ӧ
//			String secondLine = mControlReader.readLine();
//			Log.i(TAG,"setResponse() second line��"+secondLine);
//			mCurrentResponse.setLength(0);
//			mCurrentResponse.append(secondLine);
//
//			if (secondLine.length() > 3) {
//				String code = secondLine.substring(0, 3);
//				mResponseCode = Integer.valueOf(code);
//			} else {
//				throw new IOException("�ڶ��з�����Ӧ���ض�");
//			}
//		} 
//		Log.i(TAG,"setResponse() over  = "+mResponseCode);
//		// mResponseList.clear();
//		// mResponseList.add(line);
//		// //��ȡ�ڶ��У���Щ���������裬���½����Ҫ�û�������ɹ�֮����Ҫ����
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
//		// throw new IOException("������Ӧ���ض�");
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
	 * ��ȡ��ǰ�е���Ӧ
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
	 * ���ط��ͱ�������֮�����Ӧ��
	 * 
	 * @return
	 */
	protected int getResponseCode() throws IOException
	{
		String res = mCurrentResponse.toString();
		if(null==res)
		{
			throw new IOException("û����Ӧ�쳣");
		}
		if(res.length()>3)
		{
			String resCode = res.substring(0,3);
			return Integer.valueOf(resCode);
		}else
		{
			throw new IOException("��Ӧ����쳣");
		}
	}

	public void setControlEncoding(String encoding) {
		this.mEncoding = encoding;
	}

	public String getControlEncoding() {
		return this.mEncoding;
	}
}
