package com.ads.dao;

import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.utils.SystemTool;

import android.app.Activity;
import android.content.Context;

import com.ads.common.DESUtil;
import com.ads.common.Http;
import com.ads.custom.view.Dialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class PersonServices {

	private Context context;
	private Dialog dialog;

	public PersonServices(Activity activity) {
		this.context = activity.getApplicationContext();
		dialog = new Dialog(activity);
	}
	/**
	 * 根据字段信息获取用户的信息 不存在则返回0
	 * 
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

	public void resetUser() {
		// 更新用户信息
		if (SystemTool.checkNet(context)) {
			// dialog.show("更新用户信息");
			Http.post(context,"user/userInfo",
					Http.addParams("uId", getCurrentUser("uId")),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String t) {
							if (!t.equals("0")) {
								PreferenceHelper.remove(context, "ads",
										"currentUser");
								PreferenceHelper.write(context, "ads",
										"currentUser", t);

							} else {
								ViewInject.toast("更新用户信息失败");
							}

						}

						@Override
						public void onFailure(int errorNo, Throwable error,
								String strMsg) {
							ViewInject.toast("更新用户信息失败,错误代码：" + errorNo);
						}

						@Override
						public void onFinish() {
							// dialog.hide();
						}
					});
		} else {
			ViewInject.toast("请打开网络链接");
		}
	}

	public void resetGuest() {
		// dialog.show("更新用户信息");
		// 更新用户信息
		if (SystemTool.checkNet(context)) {
			Http.post(context,"guest/guestInfo",
					Http.addParams("gId", getCurrentUser("gId")),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String t) {
							if (!t.equals("0")) {
								String string = DESUtil.decrypt(t);
								PreferenceHelper.remove(context, "ads",
										"currentUser");
								PreferenceHelper.write(context, "ads",
										"currentUser", t);

							} else {
								ViewInject.toast("更新用户信息失败");
							}

						}

						@Override
						public void onFinish() {
							// dialog.hide();
						}

						@Override
						public void onFailure(int errorNo, Throwable error,
								String strMsg) {
							ViewInject.toast("更新用户信息失败,错误代码：" + errorNo);
						}
					});
		} else {
			ViewInject.toast("请打开网络链接");
		}
	}

	/**
	 * 清空登陆保存的信息
	 */
	public void clear() {
		PreferenceHelper.remove(context, "ads", "currentUser");
	}
}
