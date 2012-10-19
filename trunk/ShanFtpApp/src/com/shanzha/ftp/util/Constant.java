package com.shanzha.ftp.util;

/**
 * ȫ�ֵĳ�����
 * @author ShanZha
 * @date 2012-9-27 17:55
 *
 */
public abstract class Constant {
	
	/**
	 * �ļ��ָ��������ػ��������
	 */
	public static final String SEPERATOR = "/";
	/**
	 * Ftp��������Ŀ¼
	 */
	public static final String FTP_ROOT = "/";
	/**
	 * �˳������dialog id
	 */
	public static final int DIALOG_LOGOUT = 0;
	/**
	 * �Ƿ��ϴ���dialog id
	 */
	public static final int DIALOG_UPLOAD = DIALOG_LOGOUT+1;
	/**
	 * ֪ͨAdapter���ݸı�
	 */
	public static final int MSG_NOTIFY_DATA_CHANGED = 0;
	/**
	 * Ftp��������ʼ����
	 */
	public static final int MSG_CONNECT_START = MSG_NOTIFY_DATA_CHANGED+1;
	/**
	 * Ftp���������ӳɹ�
	 */
	public static final int MSG_CONNECT_SUCCESS = MSG_CONNECT_START+1;
	/**
	 * Ftp����������ʧ��
	 */
	public static final int MSG_CONNECT_FAIL = MSG_CONNECT_SUCCESS + 1;
	/**
	 * ��ȡ�ļ�--��ʼ
	 */
	public static final int MSG_GETFILES_START = MSG_CONNECT_FAIL+1;
	/**
	 * ��ȡ�ļ�--����
	 */
	public static final int MSG_GETFILES_ERROR = MSG_GETFILES_START+1;
	/**
	 * ��ȡ�ļ�--�����
	 */
	public static final int MSG_GETFILES_COMPLETE = MSG_GETFILES_ERROR+1;
	/**
	 * ����--�����ļ�--��ʼ
	 */
	public static final int MSG_DOWNLOAD_START = MSG_GETFILES_COMPLETE+1;
	/**
	 * ����--�����ļ�--�����ض���
	 */
	public static final int MSG_DOWNLOAD_TRANSFERRED = MSG_DOWNLOAD_START+1;
	/**
	 * ����--�����ļ�--����
	 */
	public static final int MSG_DOWNLOAD_ERROR = MSG_DOWNLOAD_TRANSFERRED+1;
	/**
	 * ����--�����ļ�--�����
	 */
	public static final int MSG_DOWNLOAD_COMPLETE = MSG_DOWNLOAD_ERROR+1;
	/**
	 * ����--����ļ�--��ʼ
	 */
	public static final int MSG_DOWNLOAD_MULTI_START = MSG_DOWNLOAD_COMPLETE+1;
	/**
	 * ����--����ļ�--�����ض���
	 */
	public static final int MSG_DOWNLOAD_MULTI_TRANSFERED = MSG_DOWNLOAD_MULTI_START+1;
	/**
	 * ����--����ļ�--����
	 */
	public static final int MSG_DOWNLOAD_MULTI_ERROR = MSG_DOWNLOAD_MULTI_TRANSFERED+1;
	/**
	 * ����--����ļ�--����һ���ļ����
	 */
	public static final int MSG_DOWNLOAD_MULTI_ONEFILE_COMPLETE = MSG_DOWNLOAD_MULTI_ERROR+1;
	/**
	 * ����--����ļ�--�����
	 */
	public static final int MSG_DOWNLOAD_MULTI_COMPLETE = MSG_DOWNLOAD_MULTI_ONEFILE_COMPLETE+1;
	/**
	 * �ϴ�--�����ļ�--��ʼ
	 */
	public static final int MSG_UPLOAD_START = MSG_DOWNLOAD_MULTI_COMPLETE+1;
	/**
	 * �ϴ�--�����ļ�--���ϴ�����
	 */
	public static final int MSG_UPLOAD_TRANSFERED = MSG_UPLOAD_START+1;
	/**
	 * �ϴ�--�����ļ�--����
	 */
	public static final int MSG_UPLOAD_ERROR = MSG_UPLOAD_TRANSFERED+1;
	/**
	 * �ϴ�--�����ļ�--�����
	 */
	public static final int MSG_UPLOAD_COMPLETE = MSG_UPLOAD_ERROR+1;
	/**
	 * �ϴ�--����ļ�--��ʼ
	 */
	public static final int MSG_UPLOAD_MULTI_START = MSG_UPLOAD_COMPLETE+1;
	/**
	 * �ϴ�--����ļ�--���ϴ�����
	 */
	public static final int MSG_UPLOAD_MULTI_TRANSFERED = MSG_UPLOAD_MULTI_START+1;
	/**
	 * �ϴ�--����ļ�--����
	 */
	public static final int MSG_UPLOAD_MULTI_ERROR = MSG_UPLOAD_MULTI_TRANSFERED+1;
	/**
	 * �ϴ�--����ļ�--����һ�������
	 */
	public static final int MSG_UPLOAD_MULTI_ONEFILE_COMPLETE = MSG_UPLOAD_MULTI_ERROR+1;
	/**
	 * �ϴ�--����ļ�--�����
	 */
	public static final int MSG_UPLOAD_MULTI_COMPLETE = MSG_UPLOAD_MULTI_ONEFILE_COMPLETE+1;
	
}
