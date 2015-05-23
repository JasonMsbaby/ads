package com.ads.ui.guest;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;

import com.ads.dao.PersonServices;
import com.ads.main.R;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Guest extends KJActivity {

	@BindView(id = R.id.txt_title)
	private TextView txt_title;
	@BindView(id = R.id.back, click = true)
	private ImageButton back;
	@BindView(id = R.id.person_guest, click = true)
	private LinearLayout person_guest;

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
	@BindView(id = R.id.txt_person_guest_shop_gMarketPhone)
	private TextView txt_person_guest_shop_gMarketPhone;
	@BindView(id = R.id.txt_person_guest_shop_detail)
	private TextView txt_person_guest_shop_detail;

	private PersonServices personServices = null;

	@Override
	public void initData() {
		personServices = new PersonServices(this);
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
		personServices.resetGuest();
	}

	@Override
	public void setRootView() {
		setContentView(R.layout.guest);
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
		default:
			break;
		}
	}

	/**
	 * 跳转到通用修改的界面
	 * 
	 * @param id
	 */
	public void toEditCommon(int id) {
		Intent intent = new Intent(Guest.this, GuestEdit_Common.class);
		intent.putExtra("id", id);
		intent.putExtra("come", 1);
		startActivityForResult(intent, 1);
	}

	/**
	 * 跳转到箱详细编辑页面
	 */
	private void toEdit() {
		Intent intent = new Intent(Guest.this, GuestEdit.class);
		startActivity(intent);
	}

}
