package com.shanzha.ftp.dao;

import com.shanzha.ftp.util.DBConstant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ���ݿ�����
 * 
 * @author ShanZha
 * @date 2012-10-08 16:05
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "shanFtp.db";
	private static final int DB_VERSION = 1;

	/**
	 * ��������¼����
	 */
	private static final String SQL_CREATE_TABLE_RECORD = "CREATE TABLE "
			+ DBConstant.TABLE_RECORD + "(" + DBConstant.RECORD_ID
			+ " integer primary key autoincrement,"// id
			+ DBConstant.RECORD_URI+" varchar(120),"//uri
			+ DBConstant.RECORD_TYPE+" integer ,"//type:�ϴ�/����
			+ DBConstant.RECORD_LOCAL_DIR + " varchar(100)," // ����·��
			+ DBConstant.RECORD_REMOTE_DIR + " varchar(100)," // Զ��·��
			+ DBConstant.RECORD_FILENAME + " varchar(100)," // �ļ���;
			+ DBConstant.RECORD_TIME+" varchar(100)"//ʱ��
			+ ")";
	 

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_CREATE_TABLE_RECORD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE " + DBConstant.TABLE_RECORD + "IF EXISTS");
	}

}
