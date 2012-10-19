package com.shanzha.ftp.inter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.shanzha.ftp.model.Record;

/**
 * FTP����Ļ����ӿ�
 * 
 * @author ShanZha
 * @date 2012-9-28 11:36
 */
public interface IFTPRequeset {
	
	/**
	 * ���ӣ�����Э�����ӣ�
	 * @param ip
	 * @param port
	 */
	void connect(String ip,int port) throws IOException;
	/**
	 * ��½ftp������
	 * @param user
	 * @param pswd
	 * @return
	 * @throws IOException
	 */
	boolean login(String user,String pswd) throws IOException;
	/**
	 * ע����½
	 * @return
	 * @throws IOException
	 */
	boolean logout() throws IOException;
	/**
	 * �Ͽ����ӣ��Ͽ�����Э�����ӣ�
	 * @throws IOException
	 */
	void disconnect() throws IOException ;
	/**
	 * �ı乤��Ŀ¼����ǰ�ĸ�Ŀ¼
	 * @return
	 * @throws IOException
	 */
	boolean changeToParentDirectory() throws IOException;
	/**
	 * �ı乤��Ŀ¼��ָ��Ŀ¼
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	boolean changeDirectory(String dir) throws IOException;
	/**
	 * �����ļ���
	 * @param dirName
	 * @return
	 * @throws IOException
	 */
	boolean createDirectory(String dirName) throws IOException;
	/**
	 * ��ֹ��ǰ��������
	 */
	void abortCurrDataConnect() throws IOException;
	/**
	 * ��ȡָ��·���µ������ļ����ļ�����Ϣ
	 * 
	 * @param remoteDir
	 * @param listener
	 */
	void getFilesByDir(String remoteDir, IFTPGetFilesListener listener)
			throws IOException;

	/**
	 * ��ȡָ��·���µ������ļ��Լ��ļ��е�����
	 * 
	 * @param remoteDir
	 * @param listener
	 */
	void getFilesNameByDir(String remoteDir, IFTPGetFileNameListener listener)
			throws IOException;

	/**
	 * �ж���ָ��·�����Ƿ�����������ļ������ļ���
	 * 
	 * @param remoteDir
	 * @param filename
	 * @param filetype
	 *            �ļ��л����ļ�
	 */
	void isExist(String remoteDir, String filename, int filetype)
			throws IOException;

	/**
	 * ���ص����ļ�
	 * 
	 * @param filename
	 * @param os
	 * @param serverDir
	 * @param listener
	 */
	void download(String filename, OutputStream os,String serverDir, IFTPDataListener listener)
			throws IOException;

	/**
	 * ���ص����ļ�
	 * @param record
	 * @param listener
	 */
	void download(Record record,IFTPDataListener listener)
			throws IOException;

	/**
	 * ���ض���ļ����洢·�����ݺ�׺�����д洢��
	 * 
	 * @param downloadList
	 * @param serverDir
	 * @param listener
	 */
	void downloadMulti(List<Record> downloadList,  
			String serverDir,IFTPDataMultiListener listener) throws IOException;

	/**
	 * �ϴ������ļ�
	 * 
	 * @param filename
	 * @param is
	 * @param serverDir
	 * @param listener
	 */
	void upload(String filename, InputStream is,String serverDir,IFTPDataListener listener)
			throws IOException;

	/**
	 * �ϴ������ļ�
	 * 
	 * @param upload
	 * @param listener
	 */
	void upload(Record upload, IFTPDataListener listener)
			throws IOException;

	/**
	 * �ϴ�����ļ�
	 * 
	 * @param uploadList
	 * @param listener
	 */
	void uploadMulti(List<Record> uploadList,
			IFTPDataMultiListener listener) throws IOException;
	
}
