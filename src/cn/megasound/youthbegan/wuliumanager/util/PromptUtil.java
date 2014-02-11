package cn.megasound.youthbegan.wuliumanager.util;

import java.util.List;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.application.MyApplication;
import cn.megasound.youthbegan.wuliumanager.dao.UsersDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.UsersDaoImpl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Toast;

public class PromptUtil {

	/**
	 * 退出
	 * @param activity
	 */
	public static void exitDialog(final Activity activity) {
		AlertDialog.Builder builder = new Builder(activity);
		builder.setMessage(activity.getResources().getString(
				R.string.home_exit_message));

		builder.setTitle(activity.getResources().getString(
				R.string.home_exit_title));

		builder.setPositiveButton(activity.getResources()
				.getString(R.string.ok), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//需要处理的一些方法
				final UsersDao usersDao = new UsersDaoImpl();
				new AsyncTask<Void, Void, Integer>(){

					@Override
					protected Integer doInBackground(Void... params) {
						return usersDao.logout(ConstantValue.user.id);
					}

					@Override
					protected void onPostExecute(Integer result) {
						if(result==-1){
							Toast.makeText(activity, "对不起，连接不上服务器。", Toast.LENGTH_SHORT).show();
							return;
						}
						//将ConstantValue中的user置空
						ConstantValue.user = null;
						//将activity挨个finish掉
						MyApplication application = (MyApplication) activity
								.getApplication();
						List<Activity> activitys = application.activitys;
						for (Activity a : activitys) {
							a.finish();
						}
					}
					
				}.execute();
				
			}
		});

		builder.setNegativeButton(
				activity.getResources().getString(R.string.cancle),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}
	
}
