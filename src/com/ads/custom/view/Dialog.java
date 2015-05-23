package com.ads.custom.view;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * 自定义对话框
 * 
 * @author Jason
 * 
 */
public class Dialog {
	// 声明进度条对话框
	public ProgressDialog m_pDialog;

	public Dialog(Activity m_Activity) {
		// 创建ProgressDialog对象
		m_pDialog = new ProgressDialog(m_Activity);
		// 设置进度条风格，风格为圆形，旋转的
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 的进度条是否不明确
		m_pDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		m_pDialog.setCancelable(false);
	}

	/**
	 * 显示对话框
	 */
	public void show(String msg) {
		// 设置ProgressDialog 提示信息
		m_pDialog.setMessage(msg);
		m_pDialog.show();
	}

	/**
	 * 隐藏对话框
	 */
	public void hide() {
		m_pDialog.hide();
	}

}
