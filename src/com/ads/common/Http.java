package com.ads.common;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Http {

	// 服务器请求地址
	private final static String baseUrl = "http://192.168.191.1:8080/ads/";
	// private final static String baseUrl = "http://192.168.23.1:8080/ads/";
	// private final static String baseUrl = "http://127.0.0.1:8080/ads/";
	//private final static String baseUrl = "http://advertisement.aliapp.com/";
	//服务器请求地址
	
    private static  AsyncHttpClient client =new AsyncHttpClient();    //实例话对象
    static
    {
        client.setTimeout(5000);   //设置链接超时，如果不设置，默认为10s
    }
    public static void get(String urlString,AsyncHttpResponseHandler res)    //用一个完整url获取一个string对象
    {
    	urlString=baseUrl+urlString;
        client.get(urlString, res);
    }
    public static void get(String urlString,RequestParams params,AsyncHttpResponseHandler res)   //url里面带参数
    {
    	urlString=baseUrl+urlString;
        client.get(urlString, params,res);
    }
    public static void get(String urlString,JsonHttpResponseHandler res)   //不带参数，获取json对象或者数组
    {
    	urlString=baseUrl+urlString;
        client.get(urlString, res);
    }
    public static void get(String urlString,RequestParams params,JsonHttpResponseHandler res)   //带参数，获取json对象或者数组
    {
    	urlString=baseUrl+urlString;
        client.get(urlString, params,res);
    }
    public static void get(String urlString, BinaryHttpResponseHandler bHandler)   //下载数据使用，会返回byte数据
    {
    	urlString=baseUrl+urlString;
        client.get(urlString, bHandler);
    }
    public static AsyncHttpClient getClient()
    {
        return client;
    }
    /**
     * 
     * @param para 格式如："name,id",1,2
     * @return
     */
    public static RequestParams addParams(String names,String ...para){
    	RequestParams requestParams=new RequestParams();
    	String strs[]=names.split(",");
    	for(int i=0;i<strs.length;i++){
    		requestParams.add(strs[i], para[i]);
    	}
    	return requestParams;
    }
}
