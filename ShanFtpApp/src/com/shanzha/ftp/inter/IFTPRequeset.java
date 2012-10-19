package com.shanzha.ftp.inter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.shanzha.ftp.model.Record;

/**
 * FTP请求的基本接口
 * 
 * @author ShanZha
 * @date 2012-9-28 11:36
 */
public interface IFTPRequeset {
	
	/**
	 * 连接（控制协议连接）
	 * @param ip
	 * @param port
	 */
	void connect(String ip,int port) throws IOException;
	/**
	 * 登陆ftp服务器
	 * @param user
	 * @param pswd
	 * @return
	 * @throws IOException
	 */
	boolean login(String user,String pswd) throws IOException;
	/**
	 * 注销登陆
	 * @return
	 * @throws IOException
	 */
	boolean logout() throws IOException;
	/**
	 * 断开连接（断开控制协议连接）
	 * @throws IOException
	 */
	void disconnect() throws IOException ;
	/**
	 * 改变工作目录到当前的父目录
	 * @return
	 * @throws IOException
	 */
	boolean changeToParentDirectory() throws IOException;
	/**
	 * 改变工作目录到指定目录
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	boolean changeDirectory(String dir) throws IOException;
	/**
	 * 创建文件夹
	 * @param dirName
	 * @return
	 * @throws IOException
	 */
	boolean createDirectory(String dirName) throws IOException;
	/**
	 * 终止当前数据连接
	 */
	void abortCurrDataConnect() throws IOException;
	/**
	 * 获取指定路径下的所有文件及文件夹信息
	 * 
	 * @param remoteDir
	 * @param listener
	 */
	void getFilesByDir(String remoteDir, IFTPGetFilesListener listener)
			throws IOException;

	/**
	 * 获取指定路径下的所有文件以及文件夹的名字
	 * 
	 * @param remoteDir
	 * @param listener
	 */
	void getFilesNameByDir(String remoteDir, IFTPGetFileNameListener listener)
			throws IOException;

	/**
	 * 判断在指定路径下是否存在这样的文件或者文件夹
	 * 
	 * @param remoteDir
	 * @param filename
	 * @param filetype
	 *            文件夹还是文件
	 */
	void isExist(String remoteDir, String filename, int filetype)
			throws IOException;

	/**
	 * 下载单个文件
	 * 
	 * @param filename
	 * @param os
	 * @param serverDir
	 * @param listener
	 */
	void download(String filename, OutputStream os,String serverDir, IFTPDataListener listener)
			throws IOException;

	/**
	 * 下载单个文件
	 * @param record
	 * @param listener
	 */
	void download(Record record,IFTPDataListener listener)
			throws IOException;

	/**
	 * 下载多个文件（存储路径根据后缀名自行存储）
	 * 
	 * @param downloadList
	 * @param serverDir
	 * @param listener
	 */
	void downloadMulti(List<Record> downloadList,  
			String serverDir,IFTPDataMultiListener listener) throws IOException;

	/**
	 * 上传单个文件
	 * 
	 * @param filename
	 * @param is
	 * @param serverDir
	 * @param listener
	 */
	void upload(String filename, InputStream is,String serverDir,IFTPDataListener listener)
			throws IOException;

	/**
	 * 上传单个文件
	 * 
	 * @param upload
	 * @param listener
	 */
	void upload(Record upload, IFTPDataListener listener)
			throws IOException;

	/**
	 * 上传多个文件
	 * 
	 * @param uploadList
	 * @param listener
	 */
	void uploadMulti(List<Record> uploadList,
			IFTPDataMultiListener listener) throws IOException;
	
}
