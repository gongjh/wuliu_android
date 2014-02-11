package cn.megasound.youthbegan.wuliumanager.view;

import cn.megasound.youthbegan.wuliumanager.application.MyApplication;
import android.app.Activity;
import android.os.Bundle;
/**
 * 所有activity的基类
 * @author 
 *
 */
public abstract class BaseActivity extends Activity {
	private MyApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (MyApplication) getApplication();
		app.activitys.add(this);
		setContentView();
		findViewById();
		init();
		setListener();
	}
	/**
	 * 设置界面
	 */
	protected abstract  void setContentView();
	
	/**
	 * 获取id 
	 */
	protected abstract void findViewById();
	
	/**
	 * 初始化
	 */
	protected void init(){};
	
	/**
	 * 设置各种监听器
	 */
	protected void setListener(){};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		app.activitys.remove(this);
	}

}
