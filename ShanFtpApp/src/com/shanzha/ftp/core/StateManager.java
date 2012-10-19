package com.shanzha.ftp.core;

/**
 * ��ʼ����Դʱ��״̬����
 * ����Ϊ����һ��ʼ������ҳ��Ͷ�ȡ���š���ϵ�ˡ�ͼƬ������ǰ����д���ļ���ʽ�����û�����ϴ�
 *  ���ܻ����ͬ�����⣬���ԼӸ�״̬����
 * @author ShanZha
 * @date 2012-10-11 16:32
 *
 */
public class StateManager {

	private static final String TAG = "StateManager";
	/** ����ʵ�� **/
	private static StateManager instance;
	/** ���ų�ʼ��״̬ **/
	private int smsState;
	/** ��ϵ�˳�ʼ��״̬ **/
	private int contactState;
	/** ��������ͼƬ��ʼ��״̬ **/
	private int picState;
	
	private StateManager(){};
	/**
	 * �ṩ�ⲿΨһʵ��
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
	/********************��Դ��ʼ��״̬��Begin��*******************************/
	public static final int STATE_PREPARE = 0;
	public static final int STATE_ERROR = STATE_PREPARE+1;
	public static final int STATE_COMPLETE = STATE_ERROR+1;
	/********************��Դ��ʼ��״̬��End��*******************************/
}
