package com.ads.ui.guest;

import java.io.File;

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
import android.widget.TextView;

import com.ads.common.Http;
import com.ads.common.Tools;
import com.ads.custom.view.RoundAngleImageView;
import com.ads.dao.PersonServices;
import com.ads.main.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class GuestEdit extends KJActivity {

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
	
	@BindView(id=R.id.img_guest_guest_info_headimg,click=true)
	private RoundAngleImageView headImg;
	
	
	private String gId;
	private PersonServices personServices;
	
	@Override
	public void setRootView() {
		setContentView(R.layout.guest_edit);
	}
	@Override
	public void initData() {
		personServices=new PersonServices(this);
		gId=personServices.getCurrentUser("gId");
	}
	@Override
	public void initWidget() {
	}
	@Override
	public void widgetClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.img_guest_guest_info_headimg:
			Dialog();
			break;
		default:
			break;
		}
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
			headImg.setImageDrawable(drawable);
			if (SystemTool.checkNet(this)) {
				Http.get(
						"user/edit",
						Http.addParams("g.gHeadImg,g.gId", SAVEPATH
								+ IMAGE_FILE_NAME, gId), new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(String t) {
								if (t.equals("1")) {
									ViewInject.toast("修改头像成功");
									personServices.resetGuest();
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
