package com.ads.ui.guest;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.http.Header;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.SystemTool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.common.Http;
import com.ads.common.Tools;
import com.ads.custom.view.RoundAngleImageView;
import com.ads.dao.PersonServices;
import com.ads.main.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GuestEdit extends KJActivity {

	@BindView(id = R.id.btn_save, click = true)
	private Button btn_save;
	@BindView(id = R.id.back2, click = true)
	private ImageButton back;
	@BindView(id = R.id.person_guest_info_name, click = true)
	private LinearLayout person_guest_info_name;
	@BindView(id = R.id.person_guest_info_phone, click = true)
	private LinearLayout person_guest_info_phone;
	@BindView(id = R.id.person_guest_info_alipay, click = true)
	private LinearLayout person_guest_info_alipay;
	@BindView(id = R.id.person_guest_info_email, click = true)
	private LinearLayout person_guest_info_email;

	@BindView(id = R.id.txt_person_guest_info_name, click = true)
	private TextView txt_person_guest_info_name;
	@BindView(id = R.id.txt_person_guest_info_phone, click = true)
	private TextView txt_person_guest_info_phone;
	@BindView(id = R.id.txt_person_guest_info_alipay, click = true)
	private TextView txt_person_guest_info_alipay;
	@BindView(id = R.id.txt_person_guest_info_email, click = true)
	private TextView txt_person_guest_info_email;

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private static final int Common_REQUEST_CODE = 3;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/* 头像名称 */
	private String SAVEPATH = null;
	private static final String IMAGE_FILE_NAME = "headImg.jpg";

	@BindView(id = R.id.img_guest_guest_info_headimg, click = true)
	private RoundAngleImageView headImg;

	private String gId;
	private PersonServices personServices;
	private com.ads.custom.view.Dialog dialog;

	@Override
	public void setRootView() {
		setContentView(R.layout.guest_edit);
	}

	@Override
	public void initData() {
		SAVEPATH = Tools.getSystemDir();
		personServices = new PersonServices(this);
		gId = personServices.getCurrentUser("gId");
		dialog = new com.ads.custom.view.Dialog(this);
	}

	@Override
	public void initWidget() {
		initHeadImg();
		txt_person_guest_info_name.setText(personServices
				.getCurrentUser("gName"));
		txt_person_guest_info_email.setText(personServices
				.getCurrentUser("gEmail"));
		txt_person_guest_info_alipay.setText(personServices
				.getCurrentUser("gAliPay"));
		txt_person_guest_info_phone.setText(personServices
				.getCurrentUser("gPhone"));
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

	@Override
	public void widgetClick(View v) {
		switch (v.getId()) {
		case R.id.back2:
			finish();
			break;
		case R.id.img_guest_guest_info_headimg:
			Dialog();
			break;
		case R.id.person_guest_info_name:
			toEditCommon(R.id.txt_person_guest_info_name);
			break;
		case R.id.person_guest_info_alipay:
			toEditCommon(R.id.txt_person_guest_info_alipay);
			break;
		case R.id.person_guest_info_email:
			toEditCommon(R.id.txt_person_guest_info_email);
			break;
		case R.id.person_guest_info_phone:
			toEditCommon(R.id.txt_person_guest_info_phone);
			break;
		case R.id.btn_save:
			submit();
			break;
		default:
			break;
		}
	}

	/**
	 * 跳转到通用编辑页面
	 * 
	 * @param id
	 */
	public void toEditCommon(int id) {
		TextView txt = (TextView) findViewById(id);
		Intent intent = new Intent(GuestEdit.this, GuestEdit_Common.class);
		intent.putExtra("id", id);
		intent.putExtra("come", 2);
		intent.putExtra("content", txt.getText().toString().trim());
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
							intentFromGallery.setType("image/jpg"); // 设置文件类型
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
				if (data != null) {
					int id = data.getIntExtra("id", 0);
					String content = data.getStringExtra("content");
					TextView textView = (TextView) findViewById(id);
					textView.setText(content);
				}
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
		intent.setDataAndType(uri, "image/jpg");
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
			headImg.setImageDrawable(drawable);
			if (SystemTool.checkNet(this)) {
				File file = new File(SAVEPATH + IMAGE_FILE_NAME);
				if (file.exists() && file.length() > 0) {
					RequestParams params =new RequestParams();
					params.put("g.gId", gId);
					try {
						params.put("file", file);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					Http.post(this, "guest/update_head_img", params,
							new AsyncHttpResponseHandler() {
								@Override
								public void onStart() {
									dialog.show("正在上传头像");
								}

								@Override
								public void onSuccess(String t) {
									if (t.equals("1")) {
										ViewInject.toast("修改头像成功");
										personServices.resetGuest();
									} else {
										ViewInject.toast("上传头像失败");
									}
								}

								@Override
								public void onFailure(int errorNo,
										Throwable error, String strMsg) {
									ViewInject.toast("修改头像失败,错误代码：" + errorNo);
								}

								@Override
								public void onFinish() {
									dialog.hide();
								}
							});
				} else {
					ViewInject.toast("文件不存在");
				}
			} else {
				ViewInject.toast("请打开网络连接");
			}
		}
	}

	/**
	 * 提交更新
	 */
	public void submit() {
		if (SystemTool.checkNet(this)) {
			String name = txt_person_guest_info_name.getText().toString()
					.trim();
			String email = txt_person_guest_info_email.getText().toString()
					.trim();
			String alipay = txt_person_guest_info_alipay.getText().toString()
					.trim();
			Http.post(this, "guest/edit", Http.addParams(
					"g.gId,g.gName,g.gEmail,g.gAliPay", gId, name, email,
					alipay), new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {
					dialog.show("正在更新");
				}

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
					ViewInject.toast("更新失败，错误代码:" + error);
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

}
