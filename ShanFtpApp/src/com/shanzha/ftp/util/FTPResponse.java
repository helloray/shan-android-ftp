package com.shanzha.ftp.util;


/**
 * ��Ӧ������
 * @author ShanZha
 * @date 2012-9-26 15:24
 */
public abstract class FTPResponse {

	/**
	 * ������Ӧ�Ƿ�ɹ���100-199֮�䣩
	 * 
	 * @param reply
	 * @return
	 */
	public static boolean isPositivePreliminary(int reply) {
		return (reply >= 100 && reply < 200);
	}

	/**
	 * Ftp������Ӧ�ɹ���200-299֮�䣩
	 * 
	 * @param code
	 * @return
	 */
	public static boolean isPositiveCompletion(int code) {
		return (code >= 200 && code < 300);
	}

	/**
	 * Ftp������������ֵ�ĳһ�������Ƿ�ɹ�(300-399֮��)
	 * 
	 * @param reply
	 * @return
	 */
	public static boolean isPositiveIntermediate(int reply) {
		return (reply >= 300 && reply < 400);
	}

	/**
	 * �ǲ���˲�����Ӧʧ�� ��400-499֮�䣩
	 * 
	 * @param reply
	 * @return
	 */
	public static boolean isNegativeTransient(int reply) {
		return (reply >= 400 && reply < 500);
	}

	/**
	 * �Ƿ�������ʧ�ܣ�500-599֮�䣩
	 * 
	 * @param reply
	 * @return
	 */
	public static boolean isNegativePermanent(int reply) {
		return (reply >= 500 && reply < 600);
	}
}
