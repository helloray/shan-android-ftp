package com.shanzha.ftp.inter;

import com.shanzha.ftp.model.Record;

/**
 * 上传/下载数据监听--多文件
 * @author ShanZha
 * @date 2012-9-28 13:59
 *
 */
public interface IFTPDataMultiListener extends IFTPDataListener{
	 
	/**
	 * 上传/下载--多个文件--其中一个完成
	 * @param obj
	 * @param type
	 */
	void onRequestFtpDataOneFileCompleted(Record record,int type);
}
