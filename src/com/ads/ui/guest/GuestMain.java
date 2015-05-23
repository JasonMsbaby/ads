package com.ads.ui.guest;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.widget.RoundImageView;

import com.ads.dao.PersonServices;
import com.ads.main.R;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

public class GuestMain extends KJActivity {
	@BindView(id = R.id.txt_title)
	private TextView txt_title;
	@BindView(id = R.id.btn_head_img, click = true)
	private RoundImageView btn_head_img;

	private PersonServices personServices = null;

	@Override
	public void setRootView() {
		setContentView(R.layout.guest_main);
	}

	@Override
	public void initData() {
		txt_title.setText("商家中心");
		personServices = new PersonServices(this);
	}

	@Override
	public void initWidget() {
		initHeadImg();
	}

	@Override
	public void widgetClick(View v) {
		switch (v.getId()) {
		case R.id.btn_head_img:
			toGuest();
			break;
		}
	}
	
	/**
	 * 跳转到商家中心页面
	 */
	private void toGuest() {
		Intent intent = new Intent(GuestMain.this, Guest.class);
		startActivity(intent);
	}

	/**
	 * 初始化头像
	 */
	private void initHeadImg() {
		String img = personServices.getCurrentUser("gHeadImg");
		if (img == null || img.equals("0")) {// 如果用户头像为空，则显示默认头像
			btn_head_img.setImageResource(R.drawable.defaulthead);
		} else {// 不为空则显示预先设置的头像
			Drawable drawable = new BitmapDrawable(img);
			btn_head_img.setImageDrawable(drawable);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			int id = data.getIntExtra("id", 0);
			String content = data.getStringExtra("content");
			TextView textView = (TextView) findViewById(id);
			textView.setText(content);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
