package com.shanzha.ftp.inter;

/**
 * FTP��������Ľӿ�
 * @author ShanZha
 * @date 2012-9-28 11:39
 * @see IFTPDataListener
 * @see IFTPDataMultiListener
 * @see IFTPGetFileNameListListener
 * @see IFTPGetFilesListener
 *
 */
public interface IFTPBaseListener {

	/**
	 * Ftp��������--��ʼ
	 * @param type ����ͬ��������
	 */
	void onRequestFtpDataStart(int type);
	/**
	 * Ftp��������--����
	 * @param errorMsg
	 * @param type ����ͬ��������
	 */
	void onRequestFtpDataError(String errorMsg,int type);
	
	/**
	 * �ļ��Լ��ļ�����Ϣ 
	 */
	public static final int TYPE_GETFILES = 0;
	/**
	 * �ļ��Լ��ļ������� 
	 */
	public static final int TYPE_GETFILENAMES = TYPE_GETFILES+1;
	/**
	 * ����--�����ļ� 
	 */
	public static final int TYPE_DOWNLOAD = TYPE_GETFILENAMES+1;
	/**
	 * ����--����ļ� 
	 */
	public static final int TYPE_DOWNLOAD_MULTI = TYPE_DOWNLOAD+1;
	/**
	 * �ϴ�--�����ļ� 
	 */
	public static final int TYPE_UPLOAD = TYPE_DOWNLOAD_MULTI+1;
	/**
	 * �ϴ�--����ļ�
	 */
	public static final int TYPE_UPLOAD_MULTI = TYPE_UPLOAD+1;
}
