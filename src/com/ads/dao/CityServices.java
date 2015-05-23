package com.ads.dao;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;

import com.ads.common.DB;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class CityServices {

	private DB db = null;

	public CityServices(Context context) {
		db = new DB(context);
	}

	public List<String> getProvinces() {
		JSONArray array = db.queryToJsonArray("select * from province");
		List<String> citys = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			JSONObject object = array.getJSONObject(i);
			citys.add(object.getString("pname"));
		}
		return citys;
	}
	public List<String> getCitys(String ProvinceName) {
		String pid = getProvinceID(ProvinceName);
		JSONArray array = db.queryToJsonArray("select * from city where provinceID=?",
				pid);
		List<String> citys = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			JSONObject object = array.getJSONObject(i);
			citys.add(object.getString("city"));
		}
		return citys;
	}
	
	public List<String> getArea(String ProvinceName,String cityName) {
		String provinceID = getProvinceID(ProvinceName);
		JSONObject city=db.queryToJsonObject("select * from city where provinceID=? and city=?", provinceID,cityName);
		String cid=city.getString("cityID");
		JSONArray array = db.queryToJsonArray("select * from area where cityID=?",
				cid);
		List<String> citys = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			JSONObject object = array.getJSONObject(i);
			citys.add(object.getString("area"));
		}
		return citys;
	}

	/**
	 * 通过省份获取学校集合
	 * @param province
	 * @return
	 */
	public String[] getSchool(String pname) {
		String pid = getProvinceID(pname);
		JSONArray array=db.queryToJsonArray("select * from college where provinceID=?", pid);
		int size=array.size();
		String[] str=new String[size];
		for(int i=0;i<array.size();i++){
			String name=array.getJSONObject(i).getString("name");
			str[i]=name;
		}
		return str;
	}

	/**
	 * 通过省份名称获取省份ID
	 * @param pname
	 * @return
	 */
	private String getProvinceID(String pname) {
		JSONObject province=db.queryToJsonObject("select * from province where pname=?", pname);
		String pid=province.getString("provinceID");
		return pid;
	}
	
}
