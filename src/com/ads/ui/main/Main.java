package com.ads.ui.main;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.SystemTool;

import android.content.Intent;

import com.ads.common.DESUtil;
import com.ads.common.Http;
import com.ads.common.Tools;
import com.ads.custom.view.Dialog;
import com.ads.dao.PersonServices;
import com.ads.ui.guest.GuestMain;
import com.ads.ui.user.UserMain;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Main extends KJActivity {
	private PersonServices personServices;
	private Dialog dialog;

	@Override
	public void setRootView() {
		personServices = new PersonServices(this);
		final String res = personServices.getCurrentUser();
		if (res==null||res.equals("0")) {
			Intent intent = new Intent(Main.this, Login.class);
			startActivity(intent);
			finish();
		} else {
			// 初始化数据
			Tools.copyDataBase(getApplicationContext());
			// 初始化等待框
			// 重新判断该用户用户名或密码是否正确
			String name = personServices.getCurrentUser("uName");
			String pwd = personServices.getCurrentUser("uPwd");
			dialog = new Dialog(this);
			if (name == null) {
				name = personServices.getCurrentUser("gName");
				pwd = personServices.getCurrentUser("gPwd");
			}
			if (SystemTool.checkNet(getApplicationContext())) {
				dialog.show("正在校验用户名和密码");
				Http.post(this,"login/loginCheck",
						Http.addParams("name,pwd", name, pwd),
						new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(String t) {
								if (!t.equals("0")) {
									JSONObject object = JSONObject
											.parseObject(DESUtil.decrypt(t));
									if (object.getString("uId") != null) {
										// 重新初始化用户信息
										personServices.resetUser();
										Intent intent = new Intent(Main.this,
												UserMain.class);
										startActivity(intent);
										finish();

									} else {
										Intent intent = new Intent(Main.this,
												GuestMain.class);
										startActivity(intent);
										finish();
										personServices.resetGuest();
									}

								} else {
									ViewInject.toast("用户名获取密码已经发生变化，请重新登陆");
									Intent intent = new Intent(Main.this,
											Login.class);
									startActivity(intent);
									finish();
								}
							}
							@Override
							public void onFailure(int statusCode,
									Throwable error, String content) {
								ViewInject.toast("登陆失败");
								Jump(res);

							}
							@Override
							public void onFinish() {
								dialog.hide();
							}
						});
			} else {
				Jump(res);
				ViewInject.toast("请打开网络链接");
			}
		}
	}

	/**
	 * 根据本地的存储用户信息进行跳转
	 * 
	 * @param res
	 */
	private void Jump(final String res) {
		JSONObject object = JSONObject.parseObject(res);
		if (object.getString("uId") != null) {
			Intent intent = new Intent(Main.this, UserMain.class);
			startActivity(intent);
			finish();
		} else {
			Intent intent = new Intent(Main.this, GuestMain.class);
			startActivity(intent);
			finish();
		}
	}

}
