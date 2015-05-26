package com.ads.ui.guest;

import java.io.File;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.widget.RoundImageView;

import com.ads.common.Tools;
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
		String img = Tools.getHeadImgUrl();
		if (new File(img).exists()) {
			Drawable drawable = new BitmapDrawable(img);
			btn_head_img.setImageDrawable(drawable);
		} else {
			btn_head_img.setImageResource(R.drawable.defaulthead);
		}
	}

}
