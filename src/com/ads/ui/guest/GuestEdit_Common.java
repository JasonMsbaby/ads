package com.ads.ui.guest;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;

import android.R.integer;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ads.main.R;

public class GuestEdit_Common extends KJActivity {

	@BindView(id = R.id.back, click = true)
	private ImageButton back;
	@BindView(id = R.id.back_submit, click = true)
	private Button submit;
	@BindView(id = R.id.txt_content)
	private EditText content;

	private int id;
	private int come;
	private String c = "";

	@Override
	public void initWidget() {
		if (c != null) {
			content.setText(c);
			submit.setText("提交");
		}
	}

	@Override
	public void setRootView() {
		setContentView(R.layout.edit_common);
		id = getIntent().getIntExtra("id", 0);
		come = getIntent().getIntExtra("come", 0);
		c = getIntent().getStringExtra("content");
	}

	@Override
	public void widgetClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.back_submit:
			String t = content.getText().toString().trim();
			if (!t.equals("")) {
				Intent intent = null;
				if (come == 1) {
					intent = new Intent(GuestEdit_Common.this, Guest.class);
				} else {
					intent = new Intent(GuestEdit_Common.this, GuestEdit.class);
				}
				intent.putExtra("id", id);
				intent.putExtra("content", t);
				setResult(come, intent);
				finish();
			} else {
				ViewInject.toast("为空不能提交");
			}
			break;

		default:
			break;
		}
	}

}
