package cn.megasound.youthbegan.wuliumanager.application;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
/**
 * 程序的骨架
 * @author hellsing
 *
 */
public class MyApplication extends Application {
	public List<Activity> activitys;
	
	@Override
	public void onCreate() {
		super.onCreate();
		activitys = new ArrayList<Activity>();
		
	}

}
