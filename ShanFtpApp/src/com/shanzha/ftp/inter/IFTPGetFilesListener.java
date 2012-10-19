package com.shanzha.ftp.inter;

import java.util.List;

import com.shanzha.ftp.model.FTPFile;
/**
 * ��ȡ�ļ���Ϣ�Ľӿ�
 * @author ShanZha
 * @date 2012-9-28 10:46
 * @see IFTPGetFileNameListListener
 */
public interface IFTPGetFilesListener extends IFTPBaseListener{

	/**
	 * ����Ftp�������ļ����Լ��ļ���Ϣ���
	 * @param files
	 */
	void onRequestFtpFileListCompleted(List<FTPFile> files);
}
