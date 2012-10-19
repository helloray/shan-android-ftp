package com.shanzha.ftp.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shanzha.ftp.model.Record;
import com.shanzha.ftp.util.DBConstant;

/**
 * FTP��ص����ݿ����������
 * @author ShanZha
 * @date 2012-10-08 16:24
 *
 */
public class FTPDBService {

	/**
	 * ����ʵ��
	 */
	private static FTPDBService instance;
	/**
	 * ���ݿ�����
	 */
	private DBHelper mDBHelper;
	/**
	 * ���ݿ��д�����ܼ�
	 */
	private SQLiteDatabase mDBWriter;
	/**
	 * ���ݿ���������ܼ�
	 */
	private SQLiteDatabase mDBReader;
	
	private FTPDBService(Context context)
	{
		mDBHelper = new DBHelper(context);
		mDBWriter = mDBHelper.getWritableDatabase();
		mDBReader = mDBHelper.getReadableDatabase();
	}
	/**
	 * �ṩ���ʵ��
	 * @return
	 */
	public synchronized static FTPDBService getInstance(Context context)
	{
		if(null==instance)
		{
			instance = new FTPDBService(context.getApplicationContext());
		}
		return instance;
	}
	/**
	 * �־û�һ������¼������(���ϴ���������)
	 * @param record
	 * @return
	 */
	public long saveRecord(Record record)
	{
		ContentValues values = new ContentValues();
		values.put(DBConstant.RECORD_URI, record.getUri());
		values.put(DBConstant.RECORD_LOCAL_DIR, record.getLocalDir());
		values.put(DBConstant.RECORD_REMOTE_DIR, record.getRemoteDir());
		values.put(DBConstant.RECORD_FILENAME, record.getFilename());
		values.put(DBConstant.RECORD_TIME, record.getTime());
		values.put(DBConstant.RECORD_TYPE, record.getType());
		return mDBWriter.insert(DBConstant.TABLE_RECORD, null, values);
	}
	/**
	 * �����ݿ��в�ѯ�����Ѿ��ϴ��������ع���record����
	 * @return
	 */
	public List<Record> queryRecordList()
	{
		Cursor cursor = mDBReader.query(DBConstant.TABLE_RECORD, null, null, null, null, null, null);
		List<Record> tempList = new ArrayList<Record>();
		while(cursor.moveToNext())
		{
			int id = cursor.getInt(cursor.getColumnIndex(DBConstant.RECORD_ID));
			String uri = cursor.getString(cursor.getColumnIndex(DBConstant.RECORD_URI));
			int type = cursor.getInt(cursor.getColumnIndex(DBConstant.RECORD_TYPE));
			String localDir = cursor.getString(cursor.getColumnIndex(DBConstant.RECORD_LOCAL_DIR));
			String remoteDir = cursor.getString(cursor.getColumnIndex(DBConstant.RECORD_REMOTE_DIR));
			String fileName = cursor.getString(cursor.getColumnIndex(DBConstant.RECORD_FILENAME));
			String time = cursor.getString(cursor.getColumnIndex(DBConstant.RECORD_TIME));
			Record record = new Record();
			record.setId(id);
			record.setUri(uri);
			record.setType(type);
			record.setLocalDir(localDir);
			record.setRemoteDir(remoteDir);
			record.setFilename(fileName);
			record.setTime(time);
			tempList.add(record);
			
		}
		if(null!=cursor)
		{
			cursor.close();
		}
		return tempList;
	}
	 
	 
}
