package com.ads.ui.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.utils.SystemTool;
import org.kymjs.kjframe.widget.RoundImageView;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ads.common.DB;
import com.ads.common.DESUtil;
import com.ads.common.Tools;
import com.ads.common.Http;
import com.ads.dao.PersonServices;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ads.main.R;
import com.ads.ui.guest.GuestMain;
import com.ads.ui.main.Login;
import com.ads.ui.user.User;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class UserMain extends KJActivity {

	private List data = new ArrayList<HashMap<String, Object>>();
	private long exitTime = 0;
	@BindView(id = R.id.btn_head_img, click = true)
	private RoundImageView btn_head_img;
	private PersonServices personServices = null;

	@Override
	public void setRootView() {
		setContentView(R.layout.user_main);
	}

	@Override
	public void initData() {
		personServices=new PersonServices(this);
	}

	@Override
	public void initWidget() {
		initHeadImg();
	}

	@Override
	public void widgetClick(View v) {
		switch (v.getId()) {
		case R.id.btn_head_img:
			toPerson();
			break;
		}
	}

	/**
	 * 初始化头像
	 */
	private void initHeadImg() {
		String img = personServices.getCurrentUser("uHeadImg");
		if (img == null||img.equals("0")) {// 如果用户头像为空，则显示默认头像
			btn_head_img.setImageResource(R.drawable.defaulthead);
		} else {// 不为空则显示预先设置的头像
			Drawable drawable = new BitmapDrawable(img);
			btn_head_img.setImageDrawable(drawable);
		}
	}

	/**
	 * 跳转到用户或商家个人中心
	 */
	private void toPerson() {
		String res = PreferenceHelper.readString(this, "ads", "currentUser",
				"0");
		String currentUser = DESUtil.decrypt(res);
		JSONObject user = JSON.parseObject(currentUser);
		if (user.getInteger("uId") != null) {// 用户
			Intent intent = new Intent(UserMain.this, User.class);
			startActivity(intent);
		}
		if (user.getInteger("gId") != null) {// 商家
			Intent intent = new Intent(UserMain.this, GuestMain.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			return false;
		case KeyEvent.KEYCODE_BACK:
			dilogExit();
			return false;
		default:
			return super.onKeyDown(keyCode, event);
		}

	}

	private void dilogExit() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			ViewInject.toast("再按一次退出应用程序");
			exitTime = System.currentTimeMillis();
		} else {
			exitTime = 0;
			finish();
		}
	}

	class adapt extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
