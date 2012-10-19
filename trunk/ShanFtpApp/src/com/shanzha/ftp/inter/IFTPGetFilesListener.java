package com.shanzha.ftp.inter;

import java.util.List;

import com.shanzha.ftp.model.FTPFile;
/**
 * 获取文件信息的接口
 * @author ShanZha
 * @date 2012-9-28 10:46
 * @see IFTPGetFileNameListListener
 */
public interface IFTPGetFilesListener extends IFTPBaseListener{

	/**
	 * 请求Ftp服务器文件夹以及文件信息完成
	 * @param files
	 */
	void onRequestFtpFileListCompleted(List<FTPFile> files);
}
