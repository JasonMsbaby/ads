package com.ads.ui.user;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;

import com.ads.main.R;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserEdit_Common extends KJActivity {

	@BindView(id = R.id.txt_content)
	private EditText txt_content;
	@BindView(id = R.id.back, click = true)
	private Button btn_back;
	@BindView(id = R.id.back_submit, click = true)
	private Button back_submit;
	@BindView(id = R.id.txt_title)
	private TextView txt_title;

	private int id = 0;

	@Override
	public void setRootView() {
		setContentView(R.layout.edit_common);
	}

	@Override
	public void initWidget() {
		txt_title.setText("编辑");
		back_submit.setText("确定");
		id = getIntent().getIntExtra("id", 0);
		txt_content.setHint(getIntent().getStringExtra("content"));
	}

	@Override
	public void widgetClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			setResult(5);
			finish();
			break;
		case R.id.back_submit:
			String content = txt_content.getText().toString().trim();
			if (!content.equals("")) {
				Intent intent = new Intent(UserEdit_Common.this, UserEdit.class);
				intent.putExtra("content", content);
				intent.putExtra("id", id);
				setResult(3, intent);
				finish();

			} else {
				ViewInject.toast("请输入内容后提交");
			}
			break;

		default:
			break;
		}
	}
}
