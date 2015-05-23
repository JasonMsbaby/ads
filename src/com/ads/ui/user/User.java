package com.ads.ui.user;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.utils.SystemTool;
import org.kymjs.kjframe.utils.ViewUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ads.main.R;
import com.ads.plus.sortlist.SchoolListActivity;
import com.ads.ui.main.Login;
import com.ads.ui.user.UserEdit;
import com.ads.common.Http;
import com.ads.custom.view.Dialog;
import com.ads.dao.CityServices;
import com.ads.dao.PersonServices;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 用户个人中心
 * 
 * @author Jason
 * 
 */
public class User extends KJActivity implements OnItemSelectedListener {

	@BindView(id = R.id.txt_title)
	private TextView txt_title;
	@BindView(id = R.id.back, click = true)
	private LinearLayout back;
	@BindView(id = R.id.btn_to_info, click = true)
	private ImageView btn_to_info;
	@BindView(id = R.id.person_user_info, click = true)
	private LinearLayout person_user_info;
	@BindView(id = R.id.btn_submit, click = true)
	private Button btn_submit;
	@BindView(id = R.id.back_submit, click = true)
	private Button btn_exit;
	@BindView(id = R.id.person_user_school, click = true)
	private LinearLayout person_user_school;
	@BindView(id = R.id.img_person_user_school)
	private ImageView img_person_user_school;
	@BindView(id = R.id.img_person_user_headimg)
	private ImageView img_person_user_headimg;

	private Spinner spinner_person_user_type, spinner_person_user_province,
			spinner_person_user_city, spinner_person_user_area;
	private TextView person_user_name, person_user_phone, person_user_money,
			txt_person_user_school;

	private ArrayAdapter<String> deptAdapt, cityAdapt, provinceAdapt,
			areaAdapt;

	private CityServices cityServices = null;
	private String uId = null;
	private String province = null;

	private PersonServices personServices = null;
	private boolean first = true;

	private Dialog dialog;

	@Override
	public void setRootView() {
		dialog = new Dialog(this);
		setContentView(R.layout.user);
	}

	@Override
	public void initWidget() {
		initHeadImg();
		// 初始化控件
		person_user_name = (TextView) findViewById(R.id.person_user_name);
		person_user_phone = (TextView) findViewById(R.id.person_user_phone);
		person_user_money = (TextView) findViewById(R.id.person_user_money);
		txt_person_user_school = (TextView) findViewById(R.id.txt_person_user_school);

		spinner_person_user_type = (Spinner) findViewById(R.id.spinner_person_user_type);
		spinner_person_user_province = (Spinner) findViewById(R.id.spinner_person_user_province);
		spinner_person_user_city = (Spinner) findViewById(R.id.spinner_person_user_city);
		spinner_person_user_area = (Spinner) findViewById(R.id.spinner_person_user_area);

		// 设置适配器
		spinner_person_user_province.setAdapter(provinceAdapt);
		spinner_person_user_type.setAdapter(deptAdapt);
		txt_person_user_school.setText("请选择院校");

		// 设置监听器
		spinner_person_user_type.setOnItemSelectedListener(this);
		spinner_person_user_type.setOnItemSelectedListener(this);
		spinner_person_user_province.setOnItemSelectedListener(this);
		spinner_person_user_city.setOnItemSelectedListener(this);
		spinner_person_user_area.setOnItemSelectedListener(this);

		// 设置文本
		person_user_name.setText(personServices.getCurrentUser("uName"));
		person_user_phone.setText(personServices.getCurrentUser("uPhone"));
		person_user_money.setText(String.valueOf(personServices
				.getCurrentUser("uMoney")));

	}

	@Override
	public void initData() {
		personServices = new PersonServices(this);
		txt_title.setText("个人中心");
		uId = personServices.getCurrentUser("uId");
		cityServices = new CityServices(getApplicationContext());
		btn_exit.setText("注销登陆");
		provinceAdapt = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.spinner_drop_down_item, cityServices.getProvinces());

		deptAdapt = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.spinner_drop_down_item, getResources().getStringArray(
						R.array.dept));
	}

	@Override
	public void widgetClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.person_user_info:
			gotoPersonInfo();
			break;
		case R.id.back_submit:
			exit();
			break;
		case R.id.btn_submit:
			Submit();
			break;
		case R.id.person_user_school:
			toSchoolList();
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化头像
	 */
	private void initHeadImg() {
		String headImg_url = personServices.getCurrentUser("uHeadImg");
		if (headImg_url == null || headImg_url.equals("0")) {// 如果用户头像为空，则显示默认头像
			img_person_user_headimg.setImageResource(R.drawable.defaulthead);
		} else {// 不为空则显示预先设置的头像
			Drawable drawable = new BitmapDrawable(headImg_url);
			img_person_user_headimg.setImageDrawable(drawable);
		}
	}

	/**
	 * 跳转到选择学校activity
	 */
	private void toSchoolList() {
		Intent intent = new Intent(User.this, SchoolListActivity.class);
		intent.putExtra("come", "user");
		intent.putExtra("province", province);
		startActivityForResult(intent, 0);
	}

	/**
	 * 选择学校完成后返回数据
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			txt_person_user_school.setText(data.getStringExtra("return"));
		}
	}

	/**
	 * 提交修改的信息
	 */
	private void Submit() {
		if (spinner_person_user_type.getSelectedItem().toString().equals("学生")
				&& txt_person_user_school.getText().toString().trim()
						.equals("请选择院校")) {
			ViewInject.toast("请选择院校后进行提交");
		} else {
			new AlertDialog.Builder(this).setTitle("确定要提交？提交后将无法进行修改")
					.setPositiveButton("是", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String dept = spinner_person_user_type
									.getSelectedItem().toString();
							String province = spinner_person_user_province
									.getSelectedItem().toString();
							String city = spinner_person_user_city
									.getSelectedItem().toString();
							String area = spinner_person_user_area
									.getSelectedItem().toString();
							String school = txt_person_user_school.getText()
									.toString().trim();

							if (SystemTool.checkNet(getApplicationContext())) {
								User.this.dialog.show("正在提交");
								Http.get(
										"user/edit",
										Http.addParams(
												"u.uDept,u.uProvince,u.uCity,u.uArea,u.uId,u.uAddress",
												dept, province, city, area,
												uId, school),
										new AsyncHttpResponseHandler() {
											@Override
											public void onSuccess(String t) {
												if (!t.equals("1")) {
													ViewInject.toast("更新失败");
												} else {
													ViewInject.toast("更新成功");
													personServices.resetUser();
													submitFinish();
												}
											}

											@Override
											public void onFailure(int errorNo,
													Throwable error,
													String strMsg) {
												ViewInject.toast("更新失败,错误代码-"
														+ errorNo);
											}
											@Override
											public void onFinish() {
												User.this.dialog.hide();
											}
										});
							} else {
								ViewInject.toast("请检查网络链接");
							}
						}
					}).setNegativeButton("否", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();

		}

	}

	/**
	 * 注销登陆
	 */
	private void exit() {
		PreferenceHelper.remove(getApplicationContext(), "ads", "currentUser");
		Intent intent = new Intent(User.this, Login.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 跳转到个人详细信息
	 */
	private void gotoPersonInfo() {
		Intent intent = new Intent(User.this, UserEdit.class);
		startActivity(intent);
	}

	/**
	 * 下拉选择
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {
		case R.id.spinner_person_user_type:
			deptSelect(parent);
			break;
		case R.id.spinner_person_user_province:
			listCity(parent);
			break;
		case R.id.spinner_person_user_city:
			listArea(parent);
			break;
		}
	}

	/**
	 * 选取职业信息触发
	 * 
	 * @param parent
	 */
	private void deptSelect(AdapterView<?> parent) {
		if (parent.getSelectedItem().toString().equals("学生")) {
			person_user_school.setVisibility(View.VISIBLE);
			txt_person_user_school.setText("请选择院校");
		} else {
			person_user_school.setVisibility(View.GONE);
		}
	}

	/**
	 * 获取城市列表
	 * 
	 * @param parent
	 */
	private void listCity(AdapterView<?> parent) {
		final String province = parent.getSelectedItem().toString();
		this.province = province;
		cityAdapt = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.spinner_drop_down_item,
				cityServices.getCitys(province));
		spinner_person_user_city.setAdapter(cityAdapt);
	}

	/**
	 * 获取地区列表
	 * 
	 * @param parent
	 */
	private void listArea(AdapterView<?> parent) {
		final String city = parent.getSelectedItem().toString();
		areaAdapt = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.spinner_drop_down_item, cityServices.getArea(province,
						city));
		spinner_person_user_area.setAdapter(areaAdapt);
		setSelection();
	}

	/**
	 * 设置选择框的默认选项
	 */
	private void setSelection() {
		String school = personServices.getCurrentUser("uAddress");
		if (personServices.getCurrentUser("uProvince") != null
				&& school != null) {
			// 设置默认选择项
			spinner_person_user_type.setSelection(deptAdapt
					.getPosition(personServices.getCurrentUser("uDept")), true);

			// 设置省份默认选项
			spinner_person_user_province.setSelection(provinceAdapt
					.getPosition(personServices.getCurrentUser("uProvince")),
					true);
			// 设置城市默认选项
			spinner_person_user_city.setSelection(cityAdapt
					.getPosition(personServices.getCurrentUser("uCity")), true);
			// 设置地区默认选项
			spinner_person_user_area.setSelection(areaAdapt
					.getPosition(personServices.getCurrentUser("uArea")), true);
			txt_person_user_school.setText(school);
			submitFinish();
		} else {
			submitReset();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	/**
	 * 提交完成后隐藏内容
	 */
	private void submitFinish() {
		img_person_user_school.setVisibility(View.GONE);
		spinner_person_user_province.setEnabled(false);
		spinner_person_user_city.setEnabled(false);
		spinner_person_user_area.setEnabled(false);
		spinner_person_user_type.setEnabled(false);
		person_user_school.setClickable(false);
		btn_submit.setVisibility(View.GONE);
		img_person_user_school.setVisibility(View.INVISIBLE);
	}

	/**
	 * 重新修改
	 */
	private void submitReset() {
		img_person_user_school.setVisibility(View.VISIBLE);
		spinner_person_user_province.setEnabled(true);
		spinner_person_user_city.setEnabled(true);
		spinner_person_user_area.setEnabled(true);
		spinner_person_user_type.setEnabled(true);
		person_user_school.setClickable(true);
		btn_submit.setVisibility(View.VISIBLE);
		img_person_user_school.setVisibility(View.INVISIBLE);
		person_user_school.setClickable(true);
		txt_person_user_school.setText("请选择院校");
	}

}
