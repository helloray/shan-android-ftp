package com.shanzha.ftp.inter;

/**
 * Ftp上传/下载数据监听--单个文件完全接口，多个文件的不完全接口
 * @author ShanZha
 * @date 2012-9-26 17:03
 * @see IFTPDataMultiListener
 */
public interface IFTPDataListener extends IFTPBaseListener{

	/**
	 * 上传/下载--单个/多个文件--已经传输多少
	 * @param transeredLength
	 * @param type 区别不同的操作请求
	 */
	void onRequestFtpDataTransfered(int transeredLength,int type);
	/**
	 * 上传/下载--单个/多个文件--已完成
	 * @param type 区别不同的操作请求
	 */
	void onRequestFtpDataCompleted(int type);
}
