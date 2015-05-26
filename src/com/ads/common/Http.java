package com.ads.common;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.Cache;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Http {

	// 服务器请求地址
	private final static String baseUrl = "http://192.168.191.1:8080/ads/";
	// private final static String baseUrl = "http://192.168.23.1:8080/ads/";
	// private final static String baseUrl = "http://127.0.0.1:8080/ads/";
	// private final static String baseUrl = "http://advertisement.aliapp.com/";
	// 服务器请求地址

	private static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象
	private static KJHttp kjHttp=new KJHttp();
	static {
		//client.setTimeout(10000); // 设置链接超时，如果不设置，默认为10s
	}

	public static void post(Context context, String urlString,
			AsyncHttpResponseHandler res) // 用一个完整url获取一个string对象
	{
		urlString = baseUrl + urlString;
		client.post(urlString, res);
	}

	public static void post(Context context, String urlString,
			RequestParams params, AsyncHttpResponseHandler res) // url里面带参数
	{
		urlString = baseUrl + urlString;
		client.post(urlString, params, res);
	}
	
	
	/**
	 * 带缓存的请求
	 * @return 
	 */

	public static void kjget(String urlString,HttpCallBack callback){
		urlString = baseUrl + urlString;
		kjHttp.get(urlString, callback);
	}
	
	public static AsyncHttpClient getClient() {
		return client;
	}
	
	/**
	 * 
	 * @param para
	 *            格式如："name,id",1,2
	 * @return
	 */
	public static RequestParams addParams(String names, Object... para) {
		RequestParams requestParams = new RequestParams();
		String strs[] = names.split(",");
		for (int i = 0; i < strs.length; i++) {
			requestParams.put(strs[i], para[i]);
		}
		return requestParams;
	}
}
