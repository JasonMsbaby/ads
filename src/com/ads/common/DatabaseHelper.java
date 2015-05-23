package com.ads.common;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库操作帮助基类
 * 
 * @author Jason
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1; // 数据库版本号

	private static final String DATABASE_NAME = "ads.db"; // 数据库名称

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*String city="CREATE TABLE city (cId INTEGER PRIMARY KEY,pId INTEGER ,cName TEXT ,cType INTEGER)";
		db.execSQL(city);*/

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
