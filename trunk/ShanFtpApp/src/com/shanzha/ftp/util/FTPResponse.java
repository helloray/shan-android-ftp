package com.shanzha.ftp.util;


/**
 * 响应帮助类
 * @author ShanZha
 * @date 2012-9-26 15:24
 */
public abstract class FTPResponse {

	/**
	 * 初步响应是否成功（100-199之间）
	 * 
	 * @param reply
	 * @return
	 */
	public static boolean isPositivePreliminary(int reply) {
		return (reply >= 100 && reply < 200);
	}

	/**
	 * Ftp服务响应成功（200-299之间）
	 * 
	 * @param code
	 * @return
	 */
	public static boolean isPositiveCompletion(int code) {
		return (code >= 200 && code < 300);
	}

	/**
	 * Ftp服务器多个部分的某一个部分是否成功(300-399之间)
	 * 
	 * @param reply
	 * @return
	 */
	public static boolean isPositiveIntermediate(int reply) {
		return (reply >= 300 && reply < 400);
	}

	/**
	 * 是不是瞬间的响应失败 （400-499之间）
	 * 
	 * @param reply
	 * @return
	 */
	public static boolean isNegativeTransient(int reply) {
		return (reply >= 400 && reply < 500);
	}

	/**
	 * 是否是永久失败（500-599之间）
	 * 
	 * @param reply
	 * @return
	 */
	public static boolean isNegativePermanent(int reply) {
		return (reply >= 500 && reply < 600);
	}
}
