package com.shanzha.ftp.inter;

import java.util.List;

/**
 * 获取文件或者文件夹名字接口
 * @author ShanZha
 * @date 2012-9-28 15:09
 * @see IFTPGetFilesListener
 */
public interface IFTPGetFileNameListener extends IFTPBaseListener{

	/**
	 * 请求ftp服务器文件夹以及文件名字完成
	 * @param filenames
	 */
	void onRequestFtpFilenameListCompleted(List<String> filenames);
}
