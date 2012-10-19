package com.shanzha.ftp.inter;

/**
 * 每个Activity的初始方法(三个方法顺序执行)
 * @author ShanZha
 * @date 2012-9-25
 */
public interface IBase {

	void initUI();
	void initListener();
	void initRes();
}
