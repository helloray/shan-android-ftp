package com.shanzha.ftp.inter;

/**
 * Ftp�ϴ�/�������ݼ���--�����ļ���ȫ�ӿڣ�����ļ��Ĳ���ȫ�ӿ�
 * @author ShanZha
 * @date 2012-9-26 17:03
 * @see IFTPDataMultiListener
 */
public interface IFTPDataListener extends IFTPBaseListener{

	/**
	 * �ϴ�/����--����/����ļ�--�Ѿ��������
	 * @param transeredLength
	 * @param type ����ͬ�Ĳ�������
	 */
	void onRequestFtpDataTransfered(int transeredLength,int type);
	/**
	 * �ϴ�/����--����/����ļ�--�����
	 * @param type ����ͬ�Ĳ�������
	 */
	void onRequestFtpDataCompleted(int type);
}
