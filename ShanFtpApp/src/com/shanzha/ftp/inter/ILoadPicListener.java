package com.shanzha.ftp.inter;

import java.util.List;

/**
 * ���ر���ͼƬ����
 * 
 * @author ShanZha
 * @date 2012-10-16 13:58
 * 
 */
public interface ILoadPicListener extends ILoadBaseListener {

	/**
	 * ���ر�������ͼƬ--���
	 * 
	 * @param picPathList
	 */
	void onLoadPicCompleted(List<String> picPathList);
}
