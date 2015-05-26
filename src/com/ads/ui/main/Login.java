package com.ads.ui.main;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.utils.SystemTool;
import org.kymjs.kjframe.widget.RoundImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.common.DESUtil;
import com.ads.common.Http;
import com.ads.custom.view.Dialog;
import com.ads.dao.PersonServices;
import com.ads.main.R;
import com.ads.ui.guest.GuestMain;
import com.ads.ui.user.UserMain;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Login extends KJActivity {

	@BindView(id = R.id.login_head_img)
	private RoundImageView login_head_img;

	private int time = 60;
	private TextView txt_regist_user, txt_regist_guest, info, txt_regist_role,
			txt_login;
	private LinearLayout login, regist;
	private Button btn_login, btn_regist, btn_regist_getvalidecode;
	private EditText edit_regist_phone, edit_regist_pwd1, edit_regist_pwd2,
			edit_regist_validecode, edit_login_name, edit_login_pwd;

	private PersonServices personServices = null;

	private Dialog dialog;

	// *****************************************************************************
	@Override
	public void setRootView() {
		dialog = new Dialog(this);
		setContentView(R.layout.login);
	}

	@Override
	public void initWidget() {
		txt_login = (TextView) findViewById(R.id.txt_login);
		txt_regist_user = (TextView) findViewById(R.id.txt_regist_user);
		txt_regist_guest = (TextView) findViewById(R.id.txt_regist_guest);
		txt_regist_role = (TextView) findViewById(R.id.txt_regist_role);
		info = (TextView) findViewById(R.id.info);
		login = (LinearLayout) findViewById(R.id.login);
		regist = (LinearLayout) findViewById(R.id.regist);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_regist = (Button) findViewById(R.id.btn_regist);
		edit_regist_phone = (EditText) findViewById(R.id.edit_regist_phone);
		edit_regist_pwd1 = (EditText) findViewById(R.id.edit_regist_pwd1);
		edit_regist_pwd2 = (EditText) findViewById(R.id.edit_regist_pwd2);
		edit_regist_validecode = (EditText) findViewById(R.id.edit_regist_validecode);
		btn_regist_getvalidecode = (Button) findViewById(R.id.btn_regist_getvalidecode);
		edit_login_name = (EditText) findViewById(R.id.edit_login_name);
		edit_login_pwd = (EditText) findViewById(R.id.edit_login_pwd);
		txt_regist_user.setOnClickListener(this);
		txt_regist_guest.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		btn_regist.setOnClickListener(this);
		btn_regist_getvalidecode.setOnClickListener(this);
		txt_login.setOnClickListener(this);
		// initHeadImg();
	}

	@Override
	public void initData() {
		personServices = new PersonServices(this);
	}

	/**
	 * 初始化头像
	 */
	// private void initHeadImg() {
	// String img = personServices.getCurrentUser("uHeadImg");
	// if (img == null||img.equals("0")) {// 如果用户头像为空，则显示默认头像
	// login_head_img.setImageResource(R.drawable.defaulthead);
	// } else {// 不为空则显示预先设置的头像
	// Drawable drawable = new BitmapDrawable(img);
	// login_head_img.setImageDrawable(drawable);
	// }
	// }
	@Override
	public void widgetClick(View v) {
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.txt_regist_user:
			// 1 用户
			txt_regist_role.setText("1");
			toRegist();
			break;
		case R.id.txt_regist_guest:
			// 2 商家
			txt_regist_role.setText("2");
			toRegist();
			break;
		case R.id.btn_regist:
			registSubmit();
			break;
		case R.id.btn_login:
			loginSubmit();
			break;
		case R.id.btn_regist_getvalidecode:
			getValideCode();
			break;
		case R.id.txt_login:
			toLogin();
			break;
		default:
			break;
		}
	}

	/**
	 * 获取短信验证码
	 */
	private void getValideCode() {
		btn_regist_getvalidecode.setClickable(false);
		btn_regist_getvalidecode
				.setBackgroundResource(R.drawable.button_disable);
		Handler.post(TimeRunable);
	}

	/**
	 * 登陆校验
	 */
	private void loginSubmit() {
		String name = edit_login_name.getText().toString().trim();
		String pwd = edit_login_pwd.getText().toString().trim();
		if (SystemTool.checkNet(getApplicationContext())) {
			dialog.show("正在登陆...");
			Http.post(this,"login/loginCheck",
					Http.addParams("name,pwd", name, DESUtil.MD5(pwd)),
					new AsyncHttpResponseHandler() {
						public void onSuccess(String content) {
							if (!content.equals("0")) {
								JSONObject t = JSONObject.parseObject(DESUtil
										.decrypt(content));
								if (t.getString("uId") != null) {
									PreferenceHelper.write(
											getApplicationContext(), "ads",
											"currentUser", content);
									Intent intent = new Intent(Login.this,
											UserMain.class);
									startActivity(intent);
									finish();
								} else {
									PreferenceHelper.write(
											getApplicationContext(), "ads",
											"currentUser", content);
									Intent intent = new Intent(Login.this,
											GuestMain.class);
									startActivity(intent);
									finish();
								}

							} else {
								info.setText("用户名或密码错误");
							}
						};

						public void onFailure(int statusCode, Throwable error,
								String content) {
							info.setText("登陆失败，错误代码-" + statusCode);
						};

						@Override
						public void onFinish() {
							dialog.hide();
						}
					});
		} else {
			ViewInject.toast("请打开网络链接");
		}
	}

	/**
	 * 注册
	 */
	private void registSubmit() {
		final String phone = edit_regist_phone.getText().toString().trim();
		String valide = edit_regist_validecode.getText().toString().trim();
		String pwd1 = edit_regist_pwd1.getText().toString().trim();
		String pwd2 = edit_regist_pwd2.getText().toString().trim();
		if (!pwd1.equals(pwd2)) {
			info.setText("两次密码不一致");
		} else {
			if (SystemTool.checkNet(getApplicationContext())) {
				dialog.show("正在注册");
				String role = txt_regist_role.getText().toString().trim();
				Http.post(this,"login/regist",
						Http.addParams("phone,pwd,role", phone, pwd1, role),
						new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(String t) {
								if (t.equals("0")) {
									ViewInject.toast("注册失败");
								} else if (t.equals("11")) {
									info.setText("该手机已经注册");
								} else {
									ViewInject.toast("注册成功，请登陆");
									edit_login_name.setText(phone);
									toLogin();
								}
							}

							@Override
							public void onFailure(int statusCode,
									Throwable error, String content) {
								info.setText("注册失败，错误代码-" + statusCode);
							}

							@Override
							public void onFinish() {
								dialog.hide();
							}
						});
			} else {
				ViewInject.toast("请打开网络链接");
			}
		}
	}

	/**
	 * 跳转到注册
	 */
	private void toRegist() {
		login.setVisibility(View.GONE);
		regist.setVisibility(View.VISIBLE);
		String role = txt_regist_role.getText().toString().trim();
		if (role.equals("1")) {
			info.setText("您正在进行用户的注册");
		} else {
			info.setText("您正在进行商家的注册");
		}
	}

	/**
	 * 跳转到登陆
	 */
	private void toLogin() {
		info.setText("");
		regist.setVisibility(View.GONE);
		login.setVisibility(View.VISIBLE);
	}

	Handler Handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			btn_regist_getvalidecode.setText(String.valueOf(msg.arg1));
			Handler.postDelayed(TimeRunable, 1000);
		}

	};

	/**
	 * 倒计时
	 * 
	 * @author Jason
	 * 
	 */
	private Runnable TimeRunable = new Runnable() {
		@Override
		public void run() {
			Message msg = Handler.obtainMessage();
			msg.arg1 = time;
			time--;
			if (time < -1) {
				Handler.removeCallbacks(TimeRunable);
				btn_regist_getvalidecode.setText("获 取");
				btn_regist_getvalidecode.setClickable(true);
				btn_regist_getvalidecode
						.setBackgroundResource(R.drawable.button);
				time = 60;
			} else {
				Handler.sendMessage(msg);
			}
		}

	};

}
