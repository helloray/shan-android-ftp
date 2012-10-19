package com.shanzha.ftp.util;

/**
 * 全局的常量类
 * @author ShanZha
 * @date 2012-9-27 17:55
 *
 */
public abstract class Constant {
	
	/**
	 * 文件分隔符（本地或服务器）
	 */
	public static final String SEPERATOR = "/";
	/**
	 * Ftp服务器根目录
	 */
	public static final String FTP_ROOT = "/";
	/**
	 * 退出程序的dialog id
	 */
	public static final int DIALOG_LOGOUT = 0;
	/**
	 * 是否上传的dialog id
	 */
	public static final int DIALOG_UPLOAD = DIALOG_LOGOUT+1;
	/**
	 * 通知Adapter数据改变
	 */
	public static final int MSG_NOTIFY_DATA_CHANGED = 0;
	/**
	 * Ftp服务器开始连接
	 */
	public static final int MSG_CONNECT_START = MSG_NOTIFY_DATA_CHANGED+1;
	/**
	 * Ftp服务器连接成功
	 */
	public static final int MSG_CONNECT_SUCCESS = MSG_CONNECT_START+1;
	/**
	 * Ftp服务器连接失败
	 */
	public static final int MSG_CONNECT_FAIL = MSG_CONNECT_SUCCESS + 1;
	/**
	 * 获取文件--开始
	 */
	public static final int MSG_GETFILES_START = MSG_CONNECT_FAIL+1;
	/**
	 * 获取文件--出错
	 */
	public static final int MSG_GETFILES_ERROR = MSG_GETFILES_START+1;
	/**
	 * 获取文件--已完成
	 */
	public static final int MSG_GETFILES_COMPLETE = MSG_GETFILES_ERROR+1;
	/**
	 * 下载--单个文件--开始
	 */
	public static final int MSG_DOWNLOAD_START = MSG_GETFILES_COMPLETE+1;
	/**
	 * 下载--单个文件--已下载多少
	 */
	public static final int MSG_DOWNLOAD_TRANSFERRED = MSG_DOWNLOAD_START+1;
	/**
	 * 下载--单个文件--出错
	 */
	public static final int MSG_DOWNLOAD_ERROR = MSG_DOWNLOAD_TRANSFERRED+1;
	/**
	 * 下载--单个文件--已完成
	 */
	public static final int MSG_DOWNLOAD_COMPLETE = MSG_DOWNLOAD_ERROR+1;
	/**
	 * 下载--多个文件--开始
	 */
	public static final int MSG_DOWNLOAD_MULTI_START = MSG_DOWNLOAD_COMPLETE+1;
	/**
	 * 下载--多个文件--已下载多少
	 */
	public static final int MSG_DOWNLOAD_MULTI_TRANSFERED = MSG_DOWNLOAD_MULTI_START+1;
	/**
	 * 下载--多个文件--出错
	 */
	public static final int MSG_DOWNLOAD_MULTI_ERROR = MSG_DOWNLOAD_MULTI_TRANSFERED+1;
	/**
	 * 下载--多个文件--其中一个文件完成
	 */
	public static final int MSG_DOWNLOAD_MULTI_ONEFILE_COMPLETE = MSG_DOWNLOAD_MULTI_ERROR+1;
	/**
	 * 下载--多个文件--已完成
	 */
	public static final int MSG_DOWNLOAD_MULTI_COMPLETE = MSG_DOWNLOAD_MULTI_ONEFILE_COMPLETE+1;
	/**
	 * 上传--单个文件--开始
	 */
	public static final int MSG_UPLOAD_START = MSG_DOWNLOAD_MULTI_COMPLETE+1;
	/**
	 * 上传--单个文件--已上传多少
	 */
	public static final int MSG_UPLOAD_TRANSFERED = MSG_UPLOAD_START+1;
	/**
	 * 上传--单个文件--出错
	 */
	public static final int MSG_UPLOAD_ERROR = MSG_UPLOAD_TRANSFERED+1;
	/**
	 * 上传--单个文件--已完成
	 */
	public static final int MSG_UPLOAD_COMPLETE = MSG_UPLOAD_ERROR+1;
	/**
	 * 上传--多个文件--开始
	 */
	public static final int MSG_UPLOAD_MULTI_START = MSG_UPLOAD_COMPLETE+1;
	/**
	 * 上传--多个文件--已上传多少
	 */
	public static final int MSG_UPLOAD_MULTI_TRANSFERED = MSG_UPLOAD_MULTI_START+1;
	/**
	 * 上传--多个文件--出错
	 */
	public static final int MSG_UPLOAD_MULTI_ERROR = MSG_UPLOAD_MULTI_TRANSFERED+1;
	/**
	 * 上传--多个文件--其中一个已完成
	 */
	public static final int MSG_UPLOAD_MULTI_ONEFILE_COMPLETE = MSG_UPLOAD_MULTI_ERROR+1;
	/**
	 * 上传--多个文件--已完成
	 */
	public static final int MSG_UPLOAD_MULTI_COMPLETE = MSG_UPLOAD_MULTI_ONEFILE_COMPLETE+1;
	
}
