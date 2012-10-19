package com.shanzha.ftp.model;

/**
 * 短信模型
 * @author ShanZha
 * @date 2012-10-11 14:55
 */
public class Sms {

	/**
	 * 消息id
	 */
	private String id;
	/**
	 * 会话id
	 */
	private String thread_id;
	/**
	 * 电话号码
	 */
	private String phoneNumber;
	/**
	 * 时间
	 */
	private long date;
	/**
	 * 内容
	 */
	private String body;
	/**
	 * 类型：1 接收  2：发送 3:草稿
	 */
	private String type;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getThread_id() {
		return thread_id;
	}
	public void setThread_id(String thread_id) {
		this.thread_id = thread_id;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	 
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString()
	{
		return "SMS:[id="+id+" thread_id = "+thread_id+" phoneNumber = "+phoneNumber+" body = "+body
				+" date = "+date+" type = "+type+"]";
	}
}
