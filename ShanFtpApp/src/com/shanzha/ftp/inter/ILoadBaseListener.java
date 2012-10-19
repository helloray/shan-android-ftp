package com.shanzha.ftp.inter;

/**
 * 初始本地加载资源的回调基本监听
 * @author ShanZha
 * @date 2012-10-16 15:51
 *
 */
public interface ILoadBaseListener {

	/**
	 * 加载--准备中
	 * @param type 短信/联系人/图片
	 */
	void onLoadPreparation(int type);
	/**
	 * 加载--出错
	 * @param errorMsg
	 * @param type 短信/联系人/图片
	 */
	void onLoadError(String errorMsg,int type);
	
	public static final int TYPE_SMS = 0;
	public static final int TYPE_CONTACT = TYPE_SMS+1;
	public static final int TYPE_PIC = TYPE_CONTACT+1;
}
