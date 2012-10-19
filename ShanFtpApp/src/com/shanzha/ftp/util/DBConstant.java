package com.shanzha.ftp.util;

import android.net.Uri;
import android.provider.ContactsContract;

/**
 * 数据库操作常量
 * 
 * @author ShanZha
 * @date 2012-10-8 16:37
 * 
 */
public final class DBConstant {
	
	/******************** “记录”（Begin）*************************/
	/**
	 * “记录”的表名
	 */
	public static final String TABLE_RECORD = "record";
	/**
	 * “记录”的id
	 */
	public static final String RECORD_ID = "_id";
	/**
	 * 下载字段--uri
	 */
	public static final String RECORD_URI = "uri";
	/**
	 * “记录”的本地路径
	 */
	public static final String RECORD_LOCAL_DIR = "localDir";
	/**
	 * “记录”的远程路径
	 */
	public static final String RECORD_REMOTE_DIR = "remoteDir";
	/**
	 * “记录”的文件名
	 */
	public static final String RECORD_FILENAME = "fileName";
	/**
	 * “记录”的时间
	 */
	public static final String RECORD_TIME = "downloadTime";
	/**
	 * 记录的类型
	 * 1：已上传类型
	 * 2：已下载类型
	 */
	public static final String RECORD_TYPE = "type";
	
	/******************** “记录”（End）*************************/
	 
	/********************短信（Begin）*************************/
	/**
	 * 所有短信的uri
	 */
	public static final String SMS_URI_ALL = "content://sms/";
	/**
	 * 收件箱短信的uri
	 */
	public static final String SMS_URI_INBOX = "content://sms/inbox";
	/**
	 * 发件箱短信的uri
	 */
	public static final String SMS_URI_SEND = "content://sms/send";
	/**
	 * 草稿箱短信的uri
	 */
	public static final String SMS_URI_DRAFT = "content://sms/draft";
	/**
	 * column id
	 */
	public static final String SMS_ID = "_id";
	/**
	 * column thread_id
	 */
	public static final String SMS_THREAD_ID = "thread_id";
	/**
	 * column address(phone number)
	 */
	public static final String SMS_ADDRESS = "address";
	/**
	 * column body
	 */
	public static final String SMS_BODY = "body";
	/**
	 * column date
	 */
	public static final String SMS_DATE = "date";
	/**
	 * column type
	 */
	public static final String SMS_TYPE = "type";
	
	/********************短信（End）*************************/
	
	/********************联系人(Begin)*********************************/
	/**
	 * 所有联系人的信息Uri，主要是获取data表里的contact_id，再根据contact_id去Data里 获取某人所有信息
	 * content://com.android.contacts/raw_contacts/
	 */
	public static final Uri CONTACT_RAW_URI = ContactsContract.RawContacts.CONTENT_URI;
	/**
	 * content://com.android.contacts/contacts/
	 */
	public static final Uri CONTACT_URI = ContactsContract.Contacts.CONTENT_URI;
	/**
	 * 某个联系人真正的数据所在Uri content://com.android.contacts/data
	 */
	public static final Uri CONTACT_DATA_URI = ContactsContract.Data.CONTENT_URI;
	/**
	 * Data表里的raw_contact_id
	 */
	public static final String CONTACT_RAW_ID = ContactsContract.Data.RAW_CONTACT_ID;
	/**
	 * Data表里的contact_id
	 */
	public static final String CONTACT_ID = ContactsContract.Data.CONTACT_ID;
	/**
	 * Data表里的_id
	 */
	public static final String CONTACT_DATA_ID = ContactsContract.Data._ID;
	
	/***************************联系人（End）**************************/
}
