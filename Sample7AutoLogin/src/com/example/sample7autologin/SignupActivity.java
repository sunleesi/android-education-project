package com.example.sample7autologin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sample7autologin.NetworkManager.OnResultListener;

public class SignupActivity extends ActionBarActivity {

	EditText idView, pwView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		idView = (EditText)findViewById(R.id.edit_userid);
		pwView = (EditText)findViewById(R.id.edit_password);
		Button btn = (Button)findViewById(R.id.btn_login);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String userid = idView.getText().toString();
				final String password = pwView.getText().toString();
				NetworkManager.getInstance().signup(SignupActivity.this, userid, password, new OnResultListener<String>() {
					
					@Override
					public void onSuccess(String result) {
						PropertyManager.getInstance().setUserId(userid);
						PropertyManager.getInstance().setPassword(password);
						Intent intent = new Intent(SignupActivity.this, MainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						finish();
					}
					
					@Override
					public void onError(int code) {
						
					}
				});
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
