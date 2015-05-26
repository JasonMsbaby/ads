package com.ads.ui.guest;

import java.io.File;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.SystemTool;
import org.kymjs.kjframe.widget.RoundImageView;

import com.ads.common.Http;
import com.ads.common.Tools;
import com.ads.custom.view.Dialog;
import com.ads.custom.view.RoundAngleImageView;
import com.ads.dao.PersonServices;
import com.ads.main.R;
import com.ads.ui.main.Login;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Guest extends KJActivity {

	@BindView(id = R.id.txt_title)
	private TextView txt_title;
	@BindView(id = R.id.back, click = true)
	private ImageButton back;
	@BindView(id = R.id.back_submit, click = true)
	private Button btn_exit;

	@BindView(id = R.id.btn_submit, click = true)
	private Button btn_submit;

	@BindView(id = R.id.person_guest, click = true)
	private LinearLayout person_guest;
	@BindView(id = R.id.img_person_guest_headimg)
	private RoundAngleImageView headImg;

	@BindView(id = R.id.person_guest_name, click = true)
	private TextView person_guest_name;
	@BindView(id = R.id.person_guest_money, click = true)
	private TextView person_guest_money;
	@BindView(id = R.id.person_guest_phone, click = true)
	private TextView person_guest_phone;
	@BindView(id = R.id.person_guest_shop_name, click = true)
	private LinearLayout person_guest_shop_name;
	@BindView(id = R.id.person_guest_shop_enviroment, click = true)
	private LinearLayout person_guest_shop_enviroment;
	@BindView(id = R.id.person_guest_shop_address, click = true)
	private LinearLayout person_guest_shop_address;
	@BindView(id = R.id.person_guest_shop_phone, click = true)
	private LinearLayout person_guest_shop_phone;
	@BindView(id = R.id.person_guest_shop_detail, click = true)
	private LinearLayout person_guest_shop_detail;

	@BindView(id = R.id.txt_person_guest_shop_name)
	private TextView txt_person_guest_shop_name;
	@BindView(id = R.id.txt_person_guest_shop_address)
	private TextView txt_person_guest_shop_address;
	@BindView(id = R.id.txt_person_guest_shop_MarketPhone)
	private TextView txt_person_guest_shop_gMarketPhone;
	@BindView(id = R.id.txt_person_guest_shop_detail)
	private TextView txt_person_guest_shop_detail;

	private PersonServices personServices = null;
	private String gId = null;
	private Dialog dialog = null;

	@Override
	public void initData() {
		dialog = new Dialog(this);
		personServices = new PersonServices(this);
		gId = personServices.getCurrentUser("gId");
		person_guest_name.setText(personServices.getCurrentUser("gName"));
		person_guest_phone.setText(personServices.getCurrentUser("gPhone"));
		person_guest_money.setText(personServices.getCurrentUser("gMoney"));
		txt_person_guest_shop_address.setText(personServices
				.getCurrentUser("gAddress"));
		String detail = personServices.getCurrentUser("gTitle");
		detail = (detail == null || detail.length() < 10) ? detail : detail
				.substring(0, 10);
		txt_person_guest_shop_detail.setText(detail);
		txt_person_guest_shop_name.setText(personServices
				.getCurrentUser("gMarketName"));
		txt_person_guest_shop_gMarketPhone.setText(personServices
				.getCurrentUser("gMarketPhone"));
	}

	@Override
	public void initWidget() {
		txt_title.setText("商家详细信息");
		btn_exit.setText("注销");
		personServices.resetGuest();
		initHeadImg();
	}

	@Override
	public void setRootView() {
		setContentView(R.layout.guest);
	}

	/**
	 * 初始化头像
	 */
	private void initHeadImg() {
		String img = Tools.getHeadImgUrl();
		if (new File(img).exists()) {
			Drawable drawable = new BitmapDrawable(img);
			headImg.setImageDrawable(drawable);
		} else {
			headImg.setImageResource(R.drawable.defaulthead);
		}
	}

	/**
	 * 点击事件
	 */
	@Override
	public void widgetClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.person_guest:
			toEdit();
			break;
		case R.id.person_guest_shop_name:
			toEditCommon(R.id.txt_person_guest_shop_name);
			break;
		case R.id.person_guest_shop_phone:
			toEditCommon(R.id.txt_person_guest_shop_MarketPhone);
			break;
		case R.id.person_guest_shop_address:
			toEditCommon(R.id.txt_person_guest_shop_address);
			break;
		case R.id.person_guest_shop_detail:
			toEditCommon(R.id.txt_person_guest_shop_detail);
			break;
		case R.id.btn_submit:
			submit();
			break;
		case R.id.back_submit:
			exit();
			break;
		case R.id.person_guest_shop_enviroment:
			Intent intent=new Intent(Guest.this,GuestEdit_Shop.class);
			startActivity(intent);
		}
	}

	/**
	 * 注销登陆
	 */
	private void exit() {
		personServices.clear();
		Intent intent = new Intent(Guest.this, Login.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 提交信息的修改
	 */
	private void submit() {
		if (SystemTool.checkNet(this)) {
			dialog.show("正在提交");
			String name = txt_person_guest_shop_name.getText().toString()
					.trim();
			String adress = txt_person_guest_shop_address.getText().toString()
					.trim();
			String phone = txt_person_guest_shop_gMarketPhone.getText()
					.toString().trim();
			String title = txt_person_guest_shop_detail.getText().toString()
					.trim();
			Http.post(this, "guest/edit", Http.addParams(
					"g.gId,g.gMarketName,g.gAddress,g.gMarketPhone,g.gTitle",
					gId, name, adress, phone, title),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String content) {
							if (content.equals("1")) {
								ViewInject.toast("更新成功");
								personServices.resetGuest();
							} else {
								ViewInject.toast("更新失败");
							}
						}

						@Override
						public void onFailure(Throwable error, String content) {
							ViewInject.toast("提交发生错误，错误代码：" + error);
						}

						@Override
						public void onFinish() {
							dialog.hide();
						}
					});
		} else {
			ViewInject.toast("请打开网络连接");
		}
	}

	/**
	 * 跳转到通用修改的界面
	 * 
	 * @param id
	 */
	public void toEditCommon(int id) {
		String content = ((TextView) findViewById(id)).getText().toString()
				.trim();
		Intent intent = new Intent(Guest.this, GuestEdit_Common.class);
		intent.putExtra("id", id);
		intent.putExtra("come", 1);
		intent.putExtra("content", content);
		startActivityForResult(intent, 1);
	}

	/**
	 * 跳转到箱详细编辑页面
	 */
	private void toEdit() {
		Intent intent = new Intent(Guest.this, GuestEdit.class);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (data != null) {
				int id = data.getIntExtra("id", 0);
				String content = data.getStringExtra("content");
				TextView textView = (TextView) findViewById(id);
				textView.setText(content);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
