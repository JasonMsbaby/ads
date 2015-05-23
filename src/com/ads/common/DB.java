package com.ads.common;


import java.util.Iterator;




import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作工具类
 * 
 * @author Jason
 * 
 */
public class DB {

	Context context;
	DatabaseHelper dbhelper;
	public SQLiteDatabase sqlitedatabase;

	public DB(Context context) {
		super();
		this.context = context;
	}

	// 打开数据库连接
	public void opendb(Context context) {
		dbhelper = new DatabaseHelper(context);
		sqlitedatabase = dbhelper.getWritableDatabase();

	}

	// 关闭数据库连接
	public void closedb(Context context) {
		if (sqlitedatabase.isOpen()) {
			sqlitedatabase.close();
		}
	}

	// 插入表数据
	public long insert(String table_name, ContentValues values) {
		opendb(context);
		long res = sqlitedatabase.insert(table_name, null, values);
		closedb(context);
		return res;
	}

	// 更新数据
	public int updatatable(String table_name, ContentValues values,
			String where, String... paras) {
		opendb(context);
		return sqlitedatabase.update(table_name, values, where, paras);
	}

	// 删除表数据
	public int delete(String table_name) {
		opendb(context);
		int res = 0;
		try {
			JSONArray jsonArray = queryToJsonArray("select * from "
					+ table_name);
			if (jsonArray.size() > 0) {
				opendb(context);
				res = sqlitedatabase.delete(table_name, null, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closedb(context);
		}
		return res;
	}

	// 删除表数据
	public void delete(String table_name, String where, String... para) {
		
		try {
			opendb(context);
			JSONArray array2 = queryToJsonArray("select * from " + table_name);
			opendb(context);
			JSONArray array = queryToJsonArray("select * from " + table_name
					+ " where " + where, para);
			if (array.size() > 0) {
				opendb(context);
				sqlitedatabase.delete(table_name, where, para);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closedb(context);
		}
	}

	// 查找数据
	public JSONArray queryToJsonArray(String sql, String... paras) {
		JSONArray Items = new JSONArray();
		try {
			opendb(context);
			Cursor c = sqlitedatabase.rawQuery(sql, paras);
			if (c != null) {
				String[] colums = c.getColumnNames();
				while (c.moveToNext()) {
					JSONObject item = new JSONObject();
					for (String str : colums) {
						int index=c.getColumnIndex(str);
						item.put(str, c.getString(index));
					}
					Items.add(item);
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closedb(context);
		}
		return Items;
	}

	public JSONObject queryToJsonObject(String sql, String... paras) {
		JSONObject item = new JSONObject();
		try {
			opendb(context);
			Cursor c = sqlitedatabase.rawQuery(sql, paras);
			if (c.moveToNext()) {
				String[] colums = c.getColumnNames();
				for (String str : colums) {
					int index=c.getColumnIndex(str);
					String val=c.getString(index);
					item.put(str, val);
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closedb(context);
		}
		return item;
	}

	/**
	 * 将JsonArray格式插入到表中
	 * 
	 * @param jsonArray
	 * @return
	 */
	public void insertJsonArray(JSONArray jsonArray, String tableName) {
		JSONObject jsonObject;
		for (int i = 0; i < jsonArray.size(); i++) {
			jsonObject = jsonArray.getJSONObject(i);
			ContentValues cv = new ContentValues();
			Iterator<String> iterator = jsonObject.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				cv.put(key, jsonObject.getString(key));
			}
			long l = insert(tableName, cv);
		}
	}
}
