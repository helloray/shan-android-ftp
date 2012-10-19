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
 * FTP相关的数据库操作服务类
 * @author ShanZha
 * @date 2012-10-08 16:24
 *
 */
public class FTPDBService {

	/**
	 * 自身实例
	 */
	private static FTPDBService instance;
	/**
	 * 数据库助手
	 */
	private DBHelper mDBHelper;
	/**
	 * 数据库读写操作管家
	 */
	private SQLiteDatabase mDBWriter;
	/**
	 * 数据库仅读操作管家
	 */
	private SQLiteDatabase mDBReader;
	
	private FTPDBService(Context context)
	{
		mDBHelper = new DBHelper(context);
		mDBWriter = mDBHelper.getWritableDatabase();
		mDBReader = mDBHelper.getReadableDatabase();
	}
	/**
	 * 提供外界实例
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
	 * 持久化一个“记录”对象(已上传和已下载)
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
	 * 从数据库中查询所有已经上传过和下载过的record对象
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
