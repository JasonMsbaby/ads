package com.ads.ui.user;

import java.io.File;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.SystemTool;

import com.ads.common.Tools;
import com.ads.common.Http;
import com.ads.dao.PersonServices;
import com.ads.main.R;
import com.ads.ui.main.Login;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.R.integer;
import android.R.layout;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class UserEdit extends KJActivity {

	@BindView(id = R.id.txt_title)
	private TextView txt_title;
	@BindView(id = R.id.back, click = true)
	private Button back;
	@BindView(id = R.id.btn_save, click = true)
	private Button btn_save;
	@BindView(id = R.id.img_person_user_info_headimg, click = true)
	private ImageView img_person_user_info_headimg;

	@BindView(id = R.id.person_user_info_name, click = true)
	private LinearLayout person_user_info_name;
	@BindView(id = R.id.person_user_info_phone, click = true)
	private LinearLayout person_user_info_phone;
	@BindView(id = R.id.person_user_info_email, click = true)
	private LinearLayout person_user_info_email;
	@BindView(id = R.id.person_user_info_alipay, click = true)
	private LinearLayout person_user_info_alipay;
	@BindView(id = R.id.person_user_info_idcard, click = true)
	private LinearLayout person_user_info_idcard;
	@BindView(id = R.id.person_user_info_bank, click = true)
	private LinearLayout person_user_info_bank;

	@BindView(id = R.id.txt_person_user_info_name)
	private TextView txt_person_user_info_name;
	@BindView(id = R.id.txt_person_user_info_phone)
	private TextView txt_person_user_info_phone;
	@BindView(id = R.id.txt_person_user_info_email)
	private TextView txt_person_user_info_email;
	@BindView(id = R.id.txt_person_user_info_alipay)
	private TextView txt_person_user_info_alipay;
	@BindView(id = R.id.txt_person_user_info_idcard)
	private TextView txt_person_user_info_idcard;
	@BindView(id = R.id.txt_person_user_info_bank)
	private TextView txt_person_user_info_bank;

	@BindView(id = R.id.spinner_person_user_info_sex)
	private Spinner spinner_person_user_info_sex;
	@BindView(id = R.id.spinner_person_user_info_age)
	private Spinner spinner_person_user_info_age;
	private PersonServices personServices = null;
	private String uId = null;

	private String[] ages = null;
	private ArrayAdapter<String> ageAdapter = null;
	private String[] sex = new String[] { "男", "女" };
	private ArrayAdapter<String> sexAdapter = null;
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private static final int Common_REQUEST_CODE = 3;
	private static final int Phone_REQUEST_CODE = 4;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/* 头像名称 */
	private String SAVEPATH = null;
	private static final String IMAGE_FILE_NAME = "headImg.jpg";

	@Override
	public void setRootView() {
		setContentView(R.layout.user_edit);
	}

	@Override
	public void initData() {
		SAVEPATH = Tools.getSystemDir() + "headImg";
		personServices = new PersonServices(this);
		txt_title.setText("信息编辑");
		uId = personServices.getCurrentUser("uId");
		ages = new String[64];
		for (int i = 0; i < 64; i++) {
			ages[i] = i + 16 + "";
		}
		sexAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_drop_down_item, sex);
		ageAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_drop_down_item, ages);
		initHeadImg();

	}

	@Override
	public void initWidget() {
		txt_person_user_info_name.setText(personServices
				.getCurrentUser("uName"));
		txt_person_user_info_phone.setText(personServices
				.getCurrentUser("uPhone"));
		txt_person_user_info_email.setText(personServices
				.getCurrentUser("uEmail"));
		txt_person_user_info_alipay.setText(personServices
				.getCurrentUser("uAliPay"));
		txt_person_user_info_idcard.setText(personServices
				.getCurrentUser("uIdCard"));
		txt_person_user_info_bank.setText(personServices
				.getCurrentUser("uBankId"));
		spinner_person_user_info_age.setAdapter(ageAdapter);
		spinner_person_user_info_sex.setAdapter(sexAdapter);
		int select = ageAdapter.getPosition(personServices
				.getCurrentUser("uAge"));
		spinner_person_user_info_age.setSelection(select);
		select = sexAdapter.getPosition(personServices.getCurrentUser("uSex"));
		spinner_person_user_info_sex.setSelection(select);
	}

	/**
	 * 初始化头像
	 */
	private void initHeadImg() {
		String img = personServices.getCurrentUser("uHeadImg");
		if (img == null||img.equals("0")) {// 如果用户头像为空，则显示默认头像
			img_person_user_info_headimg.setImageResource(R.drawable.defaulthead);
		} else {// 不为空则显示预先设置的头像
			Drawable drawable = new BitmapDrawable(img);
			img_person_user_info_headimg.setImageDrawable(drawable);
		}
	}

	@Override
	public void widgetClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.img_person_user_info_headimg:
			Dialog();
			break;
		case R.id.person_user_info_name:
			toEdit(R.id.txt_person_user_info_name);
			break;
		case R.id.person_user_info_phone:
			toEdit(R.id.txt_person_user_info_phone);
			break;
		case R.id.person_user_info_email:
			toEdit(R.id.txt_person_user_info_email);
			break;
		case R.id.person_user_info_alipay:
			toEdit(R.id.txt_person_user_info_alipay);
			break;
		case R.id.person_user_info_idcard:
			toEdit(R.id.txt_person_user_info_idcard);
			break;
		case R.id.person_user_info_bank:
			toEdit(R.id.txt_person_user_info_bank);
			break;
		case R.id.btn_save:
			Submit();
			break;
		default:
			break;
		}
	}

	/**
	 * 提交要修改的信息
	 */
	private void Submit() {
		String name = txt_person_user_info_name.getText().toString().trim();
		String phone = txt_person_user_info_phone.getText().toString().trim();
		String sex = spinner_person_user_info_sex.getSelectedItem().toString()
				.trim();
		String age = spinner_person_user_info_age.getSelectedItem().toString()
				.trim();
		String email = txt_person_user_info_email.getText().toString().trim();
		String idcard = txt_person_user_info_idcard.getText().toString().trim();
		String alipay = txt_person_user_info_alipay.getText().toString().trim();
		String bank = txt_person_user_info_bank.getText().toString().trim();
		if (SystemTool.checkNet(this)) {
			Http.get(
					"user/edit",
					Http.addParams(
							"u.uId,u.uName,u.uSex,u.uAge,u.uPhone,u.uEmail,u.uIdCard,u.uBankId,u.uAliPay",
							uId, name, sex, age, phone, email, idcard, bank,
							alipay), new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String t) {
							if (t.equals("1")) {
								if (txt_person_user_info_name
										.getText()
										.toString()
										.trim()
										.equals(personServices
												.getCurrentUser("uName"))) {
									personServices.resetUser();
									ViewInject.toast("修改信息成功");
								} else {
									ViewInject.longToast("信息修改成功，请重新登陆");
									Intent intent = new Intent(UserEdit.this,
											Login.class);
									startActivity(intent);
									finish();
								}

							} else {
								ViewInject.toast("服务器修改信息失败");
							}
						}

						@Override
						public void onFailure(int errorNo,Throwable error,String strMsg) {
							ViewInject.toast("修改信息失败,错误代码：" + errorNo);
						}
					});
		} else {
			ViewInject.toast("请检查网络链接");
		}

	}

	/**
	 * 跳转到编辑页面
	 */
	private void toEdit(int id) {
		TextView txtView = (TextView) findViewById(id);
		Intent intent = new Intent(UserEdit.this, UserEdit_Common.class);
		intent.putExtra("id", id);
		intent.putExtra("content", txtView.getText());
		startActivityForResult(intent, Common_REQUEST_CODE);
	}

	/**
	 * 弹出选取图片的对话框
	 */
	private void Dialog() {
		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:

							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (Tools.hasSdcard()
									&& Tools.isFolderExists(SAVEPATH)) {
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT, Uri
												.fromFile(new File(SAVEPATH,
														IMAGE_FILE_NAME)));
							}
							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	/**
	 * 选取照片返回时接收
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					File tempFile = new File(SAVEPATH + IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					ViewInject.toast("未找到存储卡，无法保存图片");
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			case Common_REQUEST_CODE: {
				int id = data.getIntExtra("id", 0);
				String content = data.getStringExtra("content");
				TextView textView = (TextView) findViewById(id);
				textView.setText(content);
			}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			// 保存头像
			if (Tools.hasSdcard() && Tools.isFolderExists(SAVEPATH)) {
				Tools.saveBitmapToFile(photo, SAVEPATH + IMAGE_FILE_NAME);
			}
			img_person_user_info_headimg.setImageDrawable(drawable);
			if (SystemTool.checkNet(this)) {
				Http.get(
						"user/edit",
						Http.addParams("u.uHeadImg,u.uId", SAVEPATH
								+ IMAGE_FILE_NAME, uId), new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(String t) {
								if (t.equals("1")) {
									ViewInject.toast("修改头像成功");
									personServices.resetUser();
								} else {
									ViewInject.toast("修改头像失败");
								}
							}

							@Override
							public void onFailure(int errorNo,Throwable error, String strMsg) {
								ViewInject.toast("修改头像失败,错误代码：" + errorNo);
							}
						});
			} else {
				ViewInject.toast("没有网络，上传修改过的头像!");
			}
		}
	}
}
