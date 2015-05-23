package com.ads.dao;

import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.utils.SystemTool;

import android.content.Context;

import com.ads.common.DESUtil;
import com.ads.common.Http;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class PersonServices {
	
	private Context context;
	public PersonServices(Context context){
		this.context=context;
	}
	/**
	 * 根据字段信息获取用户的信息  不存在则返回0
	 * @param colums
	 * @return
	 */
	public String getCurrentUser(String colums) {
		String res = PreferenceHelper.readString(context, "ads", "currentUser",
				"0");
		if (!res.equals("0")) {
			res = DESUtil.decrypt(res);
			JSONObject user = JSON.parseObject(res);
			return user.getString(colums);
		} else {
			return "0";
		}

	}

	public String getCurrentUser() {
		String res = PreferenceHelper.readString(context, "ads", "currentUser",
				"0");
		if (!res.equals("0")) {
			res = DESUtil.decrypt(res);
		}
		return res;
	}

	public  void resetUser() {
		// 更新用户信息
		if (SystemTool.checkNet(context)) {
			Http.get(
					"login/loginCheck",
					Http.addParams("name,pwd",getCurrentUser("uName"),getCurrentUser("uPwd")),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String t) {
							if (!t.equals("0")) {
								PreferenceHelper.remove(context, "ads",
										"currentUser");
								PreferenceHelper.write(context, "ads",
										"currentUser", t);

							}else{
								ViewInject.toast("更新用户信息失败");
							}

						}
						@Override
						public void onFailure(int errorNo, Throwable error,String strMsg) {
							ViewInject.toast("更新用户信息失败,错误代码："+errorNo);
						}
					});
		} else {
			ViewInject.toast("请打开网络链接");
		}
	}

	public  void resetGuest() {
		// 更新用户信息
		if (SystemTool.checkNet(context)) {
			Http.get(
					"login/loginCheck",
					Http.addParams("name,pwd",getCurrentUser("gName"),getCurrentUser("gPwd")),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String t) {
							if (!t.equals("0")) {
								PreferenceHelper.remove(context, "ads",
										"currentUser");
								PreferenceHelper.write(context, "ads",
										"currentUser", t);

							}else{
								ViewInject.toast("更新用户信息失败");
							}

						}

						@Override
						public void onFailure(int errorNo, Throwable error,String strMsg) {
							ViewInject.toast("更新用户信息失败,错误代码："+errorNo);
						}
					});
		} else {
			ViewInject.toast("请打开网络链接");
		}
	}
}
