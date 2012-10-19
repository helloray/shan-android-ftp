package com.shanzha.ftp.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.shanzha.ftp.model.Sms;
import com.shanzha.ftp.util.DBConstant;

/**
 * �������ݿ������
 * 
 * @author ShanZha
 * @date 2012-10-11 14:46
 */
public class SmsDBService {
	/**
	 * ����ʵ��
	 */
	private static SmsDBService instance;
	/**
	 * �����Ļ���
	 */
	private Context mContext;
	/**
	 * ��ȡ�������ݵ�ʵ��
	 */
	private ContentResolver mCr;

	public SmsDBService(Context context) {
		this.mContext = context.getApplicationContext();
		mCr = this.mContext.getContentResolver();
		instance = this;
	}

	/**
	 * �ṩ�ⲿһ��ʵ��
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static SmsDBService getInstance(Context context) {
		if (null == instance) {
			instance = new SmsDBService(context);
		}
		return instance;
	}

	/**
	 * �ӱ��ػ�ȡ���ж���
	 * 
	 * @return
	 */
	public List<Sms> queryAllSms() throws Exception {
		Cursor cursor = null;
		try {
			List<Sms> smss = new ArrayList<Sms>();
			Uri uri = Uri.parse(DBConstant.SMS_URI_ALL);
			cursor = mCr.query(uri, null, null, null, null);
			while (cursor.moveToNext()) {
				String id = cursor.getString(cursor
						.getColumnIndex(DBConstant.SMS_ID));
				String thread_id = cursor.getString(cursor
						.getColumnIndex(DBConstant.SMS_THREAD_ID));
				String phoneNumber = cursor.getString(cursor
						.getColumnIndex(DBConstant.SMS_ADDRESS));
				String body = cursor.getString(cursor
						.getColumnIndex(DBConstant.SMS_BODY));
				long date = cursor.getLong(cursor
						.getColumnIndex(DBConstant.SMS_DATE));
				String type = cursor.getString(cursor
						.getColumnIndex(DBConstant.SMS_TYPE));
				Sms sms = new Sms();
				sms.setId(id);
				sms.setThread_id(thread_id);
				sms.setBody(body);
				sms.setPhoneNumber(phoneNumber);
				sms.setDate(date);
				sms.setType(type);
				smss.add(sms);
			}
			return smss;
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IOException(" ��ȡ�����쳣 " + e.getMessage());
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}

	}

	/**
	 * ��һ��Smsд�����ݿ���
	 * 
	 * @param sms
	 * @return
	 */
	public ContentProviderResult[] writeSmsToDB(Sms sms) {
		ArrayList<ContentProviderOperation> opList = new ArrayList<ContentProviderOperation>();

		opList.add(ContentProviderOperation
				.newInsert(Uri.parse(DBConstant.SMS_URI_ALL))
				.withValue(DBConstant.SMS_ID, sms.getId())
				// .withValue(THREAD_ID, sms.getThread_id())
				.withValue(DBConstant.SMS_ADDRESS, sms.getPhoneNumber())
				.withValue(DBConstant.SMS_BODY, sms.getBody())
				.withValue(DBConstant.SMS_TYPE, sms.getType())
				.withValue(DBConstant.SMS_DATE, sms.getDate()).build());
		try {
			return mCr.applyBatch("sms", opList);
		} catch (Exception e) {
			System.out.println(" ������� exception " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
