package com.ads.ui.guest;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;

import android.R.integer;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.ads.main.R;

public class GuestEdit_Common extends KJActivity {

	@BindView(id = R.id.txt_title)
	private EditText content;
	private int id;
	private int come;

	@Override
	public void setRootView() {
		setContentView(R.layout.edit_common);
		id = getIntent().getIntExtra("id", 0);
		come = getIntent().getIntExtra("come", 0);
	}

	@Override
	public void widgetClick(View v) {
		switch (v.getId()) {
		case R.id.back:

			break;
		case R.id.back_submit:
			if (!content.getText().equals("")) {
				Intent intent = null;
				if (come == 1) {
					intent = new Intent(GuestEdit_Common.this, Guest.class);
				} else {
					intent = new Intent(GuestEdit_Common.this, GuestEdit.class);
				}
				setResult(id, intent);
			} else {
				ViewInject.toast("为空不能提交");
			}
			break;

		default:
			break;
		}
	}

}
