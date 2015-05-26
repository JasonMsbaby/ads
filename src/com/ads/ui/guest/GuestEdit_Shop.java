package com.ads.ui.guest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.SystemTool;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ads.common.Http;
import com.ads.common.Tools;
import com.ads.custom.view.LoadingBar;
import com.ads.dao.PersonServices;
import com.ads.main.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 店面环境activity
 * 
 * @author Jason
 *
 */
public class GuestEdit_Shop extends KJActivity  /*implements OnItemLongClickListener*/{

	MyAdapt adapt = null;

	@BindView(id = R.id.gridView1)
	private GridView gridview;
	@BindView(id = R.id.back, click = true)
	private ImageButton back;
	@BindView(id=R.id.btn_submit_shopimg,click=true)
	private Button btn_submit;
	
	private ArrayList<String> list = null;
	private PersonServices personServices;
	private com.ads.custom.view.Dialog dialog;
	private String gId;
	
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	private String SAVEPATH;
	private String fileName;

	@Override
	public void setRootView() {
		setContentView(R.layout.guest_edit_shop);
	}

	@Override
	public void initData() {
		personServices = new PersonServices(this);
		dialog=new com.ads.custom.view.Dialog(this);
		SAVEPATH = Tools.getSystemDir() + "cache/";
		fileName="cache.jpg";
		gId=personServices.getCurrentUser("gId");
		//初始化适配器
		updateData();
	}
	@Override
	public void initWidget() {
		if (SystemTool.checkNet(this)) {
			if (list != null && list.size() > 0) {
				adapt = new MyAdapt(this, list);
				gridview.setAdapter(adapt);
				registerForContextMenu(gridview);
				//gridview.setOnItemLongClickListener(this);
			}
		} else {
			ViewInject.toast("请打开网络连接");
		}

	}

	@Override
	public void widgetClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.btn_submit_shopimg:
			Dialog();
			break;
		}
	}
	
	
	/**
	 * 更新适配器
	 *  
	 */
	public void updateData(){
		String strs = personServices.getCurrentUser("gImg");
			if (strs != null && !strs.equals("")) {
				list = new ArrayList<String>();
				String urls[] = strs.split(",");
				for (String str : urls) {
					list.add(str);
				}
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
														fileName)));
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
					File tempFile = new File(SAVEPATH + fileName);
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
		intent.putExtra("aspectX", 4);
		intent.putExtra("aspectY", 3);
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
				Tools.saveBitmapToFile(photo, SAVEPATH + fileName);
			}
			if (SystemTool.checkNet(this)) {
				final File file = new File(SAVEPATH + fileName);
				if (file.exists() && file.length() > 0) {
					RequestParams params =new RequestParams();
					params.put("g.gId", gId);
					try {
						params.put("file", file);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					Http.post(this, "guest/update_shop_img", params,
							new AsyncHttpResponseHandler() {
								@Override
								public void onStart() {
									dialog.show("正在上传图片");
								}

								@Override
								public void onSuccess(String t) {
									if (!t.equals("0")) {
										//通知适配器更新数据
										list.add(t);
										adapt.notifyDataSetChanged();
										ViewInject.toast("上传成功");
										personServices.resetGuest();
									} else {
										ViewInject.toast("上传失败");
									}
								}

								@Override
								public void onFailure(int errorNo,
										Throwable error, String strMsg) {
									ViewInject.toast("上传失败,错误代码：" + errorNo);
								}

								@Override
								public void onFinish() {
									//file.delete();
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
	 * 创建上下文菜单	
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add("删除");
		menu.addSubMenu("取消");
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	/**
	 * 上下文菜单选中
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.toString().equals("删除")){
			final AdapterContextMenuInfo menuInfo=(AdapterContextMenuInfo) item.getMenuInfo();
			final String path=list.get(menuInfo.position);
			Http.post(this, "guest/delete_shop_img",Http.addParams("gId,path", gId,path) ,new AsyncHttpResponseHandler(){
				@Override
				public void onStart() {
					dialog.show("正在删除");
				}
				@Override
				public void onSuccess(String content) {
					if(content.equals("1")){
						ViewInject.toast("删除成功");
						//ViewInject.toast(SAVEPATH+path.split("/)[2]);
						File file=new File(SAVEPATH+path.split("/")[2]);
						file.delete();
						list.remove(list.get(menuInfo.position));
						adapt.notifyDataSetChanged();
						personServices.resetGuest();
					}else{
						ViewInject.toast("删除失败");
					}
				}
				@Override
				public void onFailure(int statusCode, Throwable error,
						String content) {
					ViewInject.toast("删除失败，错误代码："+statusCode);
				}
				@Override
				public void onFinish() {
					dialog.hide();
				}
			});
		}
		return super.onContextItemSelected(item);
	}

	/*@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		gridview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				
			}
			o
		});
		ViewInject.toast(list.get(arg2));
		return false;
	}*/
	
}




/**
 * 自定义适配器
 * 
 * @author Jason
 *
 */
class MyAdapt extends BaseAdapter {

	private String SAVEPATH;
	private ArrayList<String> data;
	private Context context;

	MyAdapt(Context context, ArrayList<String> data) {
		SAVEPATH = Tools.getSystemDir() + "shop/";
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.gridview__item, null);
		}
		final ImageView img = (ImageView) convertView
				.findViewById(R.id.gridview_item_img);
		final ProgressBar progressBar = (ProgressBar) convertView
				.findViewById(R.id.progressBar);
		progressBar.setProgress(0);
		final String currentUrl = data.get(position);
		final String fileName=currentUrl.split("/")[2];
		if (Tools.hasSdcard() && Tools.isFolderExists(SAVEPATH)&&Tools.isFileExists(SAVEPATH+fileName)) {
			Bitmap bitmap=BitmapFactory.decodeFile(SAVEPATH+fileName);
			img.setImageBitmap(bitmap);
			progressBar.setVisibility(View.GONE);
		} else {
			Http.post(context, currentUrl, new BinaryHttpResponseHandler() {
				@Override
				public void onSuccess(byte[] binaryData) {
					progressBar.setVisibility(View.GONE);
					Bitmap bitmap = Tools.Bytes2Bimap(binaryData);
					img.setImageBitmap(bitmap);
					if (Tools.hasSdcard() && Tools.isFolderExists(SAVEPATH)) {
						Tools.saveBitmapToFile(bitmap,
								SAVEPATH + fileName);
					} else {
						ViewInject.toast("沒有內存卡，缓存失败");
					}
				}
				@Override
				public void onProgress(int bytesWritten, int totalSize) {
					progressBar.setMax(totalSize);
					progressBar.setProgress(bytesWritten);
				}
				@Override
				public void onFailure(int statusCode, Throwable error,
						String content) {
					progressBar.setVisibility(View.GONE);
					img.setImageResource(R.drawable.errorimg);
				}
			});
		}
		return convertView;
	}

}
