package com.shanzha.ftp.util;

import android.net.Uri;
import android.provider.ContactsContract;

/**
 * ���ݿ��������
 * 
 * @author ShanZha
 * @date 2012-10-8 16:37
 * 
 */
public final class DBConstant {
	
	/******************** ����¼����Begin��*************************/
	/**
	 * ����¼���ı���
	 */
	public static final String TABLE_RECORD = "record";
	/**
	 * ����¼����id
	 */
	public static final String RECORD_ID = "_id";
	/**
	 * �����ֶ�--uri
	 */
	public static final String RECORD_URI = "uri";
	/**
	 * ����¼���ı���·��
	 */
	public static final String RECORD_LOCAL_DIR = "localDir";
	/**
	 * ����¼����Զ��·��
	 */
	public static final String RECORD_REMOTE_DIR = "remoteDir";
	/**
	 * ����¼�����ļ���
	 */
	public static final String RECORD_FILENAME = "fileName";
	/**
	 * ����¼����ʱ��
	 */
	public static final String RECORD_TIME = "downloadTime";
	/**
	 * ��¼������
	 * 1�����ϴ�����
	 * 2������������
	 */
	public static final String RECORD_TYPE = "type";
	
	/******************** ����¼����End��*************************/
	 
	/********************���ţ�Begin��*************************/
	/**
	 * ���ж��ŵ�uri
	 */
	public static final String SMS_URI_ALL = "content://sms/";
	/**
	 * �ռ�����ŵ�uri
	 */
	public static final String SMS_URI_INBOX = "content://sms/inbox";
	/**
	 * ��������ŵ�uri
	 */
	public static final String SMS_URI_SEND = "content://sms/send";
	/**
	 * �ݸ�����ŵ�uri
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
	
	/********************���ţ�End��*************************/
	
	/********************��ϵ��(Begin)*********************************/
	/**
	 * ������ϵ�˵���ϢUri����Ҫ�ǻ�ȡdata�����contact_id���ٸ���contact_idȥData�� ��ȡĳ��������Ϣ
	 * content://com.android.contacts/raw_contacts/
	 */
	public static final Uri CONTACT_RAW_URI = ContactsContract.RawContacts.CONTENT_URI;
	/**
	 * content://com.android.contacts/contacts/
	 */
	public static final Uri CONTACT_URI = ContactsContract.Contacts.CONTENT_URI;
	/**
	 * ĳ����ϵ����������������Uri content://com.android.contacts/data
	 */
	public static final Uri CONTACT_DATA_URI = ContactsContract.Data.CONTENT_URI;
	/**
	 * Data�����raw_contact_id
	 */
	public static final String CONTACT_RAW_ID = ContactsContract.Data.RAW_CONTACT_ID;
	/**
	 * Data�����contact_id
	 */
	public static final String CONTACT_ID = ContactsContract.Data.CONTACT_ID;
	/**
	 * Data�����_id
	 */
	public static final String CONTACT_DATA_ID = ContactsContract.Data._ID;
	
	/***************************��ϵ�ˣ�End��**************************/
}
