package com.shanzha.ftp.inter;

import java.util.List;

/**
 * ��ȡ�ļ������ļ������ֽӿ�
 * @author ShanZha
 * @date 2012-9-28 15:09
 * @see IFTPGetFilesListener
 */
public interface IFTPGetFileNameListener extends IFTPBaseListener{

	/**
	 * ����ftp�������ļ����Լ��ļ��������
	 * @param filenames
	 */
	void onRequestFtpFilenameListCompleted(List<String> filenames);
}
