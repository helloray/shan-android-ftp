package com.shanzha.ftp.inter;

/**
 * ׼���ϴ���Դ�Ľӿ�
 * 
 * @author ShanZha
 * @date 2012-10-11 15:34
 * 
 */
public interface ILoadSmsContactListener extends ILoadBaseListener {

	/**
	 * ���ض���/��ϵ��--���
	 * 
	 * @param path
	 * @param type
	 */
	void onLoadCompleted(String path, int type);
}
