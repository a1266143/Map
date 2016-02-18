package com.example.map;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class GoActivity extends Activity {

	private int checkedID = R.id.jiache;
	private String currentAddress;
	//起点是否是我的位置
	private boolean mineLocation = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_go);
		init();
		setListener();
	}
	
	public void init(){
		//获取我的位置
		currentAddress = getIntent().getStringExtra("currentAddress");
	}

	public void setListener() {
		((EditText)findViewById(R.id.startpoint)).setHint("我的位置("+currentAddress+")");
		((RadioGroup) findViewById(R.id.radiogroup))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup arg0, int checkedid) {
						checkedID = checkedid;
					}
				});
		findViewById(R.id.btnSearch).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String stPosition = ((EditText)findViewById(R.id.startpoint)).getText().toString();
				String edPosition = ((EditText)findViewById(R.id.endpoint)).getText().toString();
				if(stPosition.equals("")){
					stPosition = currentAddress;
					mineLocation = true;
				}else
					mineLocation = false;
					
				if(edPosition.equals("")){
					Toast.makeText(GoActivity.this, "终点不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = getIntent();
				intent.putExtra("checkedID", checkedID);
				intent.putExtra("mineLocation", mineLocation);
				intent.putExtra("stPosition", stPosition);
				intent.putExtra("edPosition", edPosition);
				GoActivity.this.setResult(0, intent);
				finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			setResult(-1);
			finish();
		}
		return true;
	}
	
	
}
