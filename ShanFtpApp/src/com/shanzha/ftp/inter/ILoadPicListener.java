package com.shanzha.ftp.inter;

import java.util.List;

/**
 * 加载本地图片监听
 * 
 * @author ShanZha
 * @date 2012-10-16 13:58
 * 
 */
public interface ILoadPicListener extends ILoadBaseListener {

	/**
	 * 加载本地所有图片--完成
	 * 
	 * @param picPathList
	 */
	void onLoadPicCompleted(List<String> picPathList);
}
