package cn.megasound.youthbegan.wuliumanager.view;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.dao.UsersDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.UsersDaoImpl;
import cn.megasound.youthbegan.wuliumanager.entity.Users;
import cn.megasound.youthbegan.wuliumanager.zxing.Intents;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class LoginActivity extends BaseActivity implements OnClickListener{
	
	private EditText nameET,pwdET;
	private Button loginBT;
	private String name,pwd;
	private UsersDao usersDao;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(ConstantValue.user!=null){
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void setContentView() {
		//更改软件盘的位置
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.activity_login);
	}

	@Override
	protected void findViewById() {
		nameET = (EditText)findViewById(R.id.login_name_et);
		pwdET = (EditText)findViewById(R.id.login_pwd_et);
		loginBT = (Button)findViewById(R.id.login_bt);
	}
	
	

	@Override
	protected void init() {
		usersDao = new UsersDaoImpl();
	}

	@Override
	protected void setListener() {
		loginBT.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.login_bt:
				name = ""+nameET.getText();
				pwd = ""+pwdET.getText();
				if(TextUtils.isEmpty(name) ){
					Toast.makeText(LoginActivity.this, "用户名不能为空。", Toast.LENGTH_SHORT).show();
					return;
				}
				if(TextUtils.isEmpty(pwd)){
					Toast.makeText(LoginActivity.this, "密码不能为空。", Toast.LENGTH_SHORT).show();
					return;
				}
				login(name,pwd);
				break;
		}
		
	}

	/**
	 * 判断登录成功与否
	 * @param name
	 * @param pwd
	 */
	public void login(final String name,final String pwd){
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				return usersDao.login(name, pwd);
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==-1){
					Toast.makeText(LoginActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return;
				}
				if(result==0){
					Toast.makeText(LoginActivity.this, "用户名或密码不正确。", Toast.LENGTH_SHORT).show();
					return;
				}
				//Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
				Users users = new Users();
				users.id = result;
				users.name = name;
				users.pwd = pwd;
				ConstantValue.user = users;
				Intent intent = new Intent(LoginActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
			
		}.execute();
	}

}
