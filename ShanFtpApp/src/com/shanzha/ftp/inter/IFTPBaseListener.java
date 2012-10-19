package com.shanzha.ftp.inter;

/**
 * FTP请求基本的接口
 * @author ShanZha
 * @date 2012-9-28 11:39
 * @see IFTPDataListener
 * @see IFTPDataMultiListener
 * @see IFTPGetFileNameListListener
 * @see IFTPGetFilesListener
 *
 */
public interface IFTPBaseListener {

	/**
	 * Ftp数据请求--开始
	 * @param type 区别不同操作请求
	 */
	void onRequestFtpDataStart(int type);
	/**
	 * Ftp数据请求--出错
	 * @param errorMsg
	 * @param type 区别不同操作请求
	 */
	void onRequestFtpDataError(String errorMsg,int type);
	
	/**
	 * 文件以及文件夹信息 
	 */
	public static final int TYPE_GETFILES = 0;
	/**
	 * 文件以及文件夹名字 
	 */
	public static final int TYPE_GETFILENAMES = TYPE_GETFILES+1;
	/**
	 * 下载--单个文件 
	 */
	public static final int TYPE_DOWNLOAD = TYPE_GETFILENAMES+1;
	/**
	 * 下载--多个文件 
	 */
	public static final int TYPE_DOWNLOAD_MULTI = TYPE_DOWNLOAD+1;
	/**
	 * 上传--单个文件 
	 */
	public static final int TYPE_UPLOAD = TYPE_DOWNLOAD_MULTI+1;
	/**
	 * 上传--多个文件
	 */
	public static final int TYPE_UPLOAD_MULTI = TYPE_UPLOAD+1;
}
