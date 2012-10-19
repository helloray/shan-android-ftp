package com.shanzha.ftp.inter;

/**
 * ��ʼ���ؼ�����Դ�Ļص���������
 * @author ShanZha
 * @date 2012-10-16 15:51
 *
 */
public interface ILoadBaseListener {

	/**
	 * ����--׼����
	 * @param type ����/��ϵ��/ͼƬ
	 */
	void onLoadPreparation(int type);
	/**
	 * ����--����
	 * @param errorMsg
	 * @param type ����/��ϵ��/ͼƬ
	 */
	void onLoadError(String errorMsg,int type);
	
	public static final int TYPE_SMS = 0;
	public static final int TYPE_CONTACT = TYPE_SMS+1;
	public static final int TYPE_PIC = TYPE_CONTACT+1;
}
