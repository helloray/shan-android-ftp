package com.shanzha.ftp.inter;

import com.shanzha.ftp.model.Record;

/**
 * �ϴ�/�������ݼ���--���ļ�
 * @author ShanZha
 * @date 2012-9-28 13:59
 *
 */
public interface IFTPDataMultiListener extends IFTPDataListener{
	 
	/**
	 * �ϴ�/����--����ļ�--����һ�����
	 * @param obj
	 * @param type
	 */
	void onRequestFtpDataOneFileCompleted(Record record,int type);
}
