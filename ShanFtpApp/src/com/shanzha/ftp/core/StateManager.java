package com.shanzha.ftp.core;

/**
 * 初始化资源时的状态管理
 * （因为我想一开始进入主页面就读取短信、联系人、图片，并把前两者写成文件形式，但用户点击上传
 *  可能会存在同步问题，所以加个状态管理）
 * @author ShanZha
 * @date 2012-10-11 16:32
 *
 */
public class StateManager {

	private static final String TAG = "StateManager";
	/** 自身实例 **/
	private static StateManager instance;
	/** 短信初始化状态 **/
	private int smsState;
	/** 联系人初始化状态 **/
	private int contactState;
	/** 本地所有图片初始化状态 **/
	private int picState;
	
	private StateManager(){};
	/**
	 * 提供外部唯一实例
	 * @return
	 */
	public synchronized static StateManager getInstance()
	{
		if(null==instance)
		{
			instance = new StateManager();
		}
		return instance;
	}
	public int getSmsState() {
		return smsState;
	}
	public void setSmsState(int smsState) {
		this.smsState = smsState;
	}
	public int getContactState() {
		return contactState;
	}
	public void setContactState(int contactState) {
		this.contactState = contactState;
	}
	public int getPicState() {
		return picState;
	}
	public void setPicState(int picState) {
		this.picState = picState;
	}
	/********************资源初始化状态（Begin）*******************************/
	public static final int STATE_PREPARE = 0;
	public static final int STATE_ERROR = STATE_PREPARE+1;
	public static final int STATE_COMPLETE = STATE_ERROR+1;
	/********************资源初始化状态（End）*******************************/
}
