package com.shanzha.ftp.inter;

/**
 * 准备上传资源的接口
 * 
 * @author ShanZha
 * @date 2012-10-11 15:34
 * 
 */
public interface ILoadSmsContactListener extends ILoadBaseListener {

	/**
	 * 加载短信/联系人--完成
	 * 
	 * @param path
	 * @param type
	 */
	void onLoadCompleted(String path, int type);
}
