package cn.megasound.youthbegan.wuliumanager.util;

import cn.megasound.youthbegan.wuliumanager.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 网络工具类
 * 
 * @author Administrator
 * 
 */
public class NetUtil {
	/**
	 * 处理网络状态
	 * @param activity
	 * @return
	 */
	public static boolean netWork(final Activity activity) {
		checkNet(activity);
		
		if (!checkNet(activity) && !checkWifi(activity)) {
			Builder b = new AlertDialog.Builder(activity).setTitle(activity.getResources().getString(R.string.prompt))
					.setMessage(activity.getResources().getString(R.string.network_msg));
			b.setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
				}
			}).show();
			return false;
		}
		return true;

	}
	/**
	 * 得到网络状态（2、3g）
	 * @return
	 */
	public static boolean checkNet(Activity activity){
		boolean netSataus = false;
		ConnectivityManager cwjManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

		cwjManager.getActiveNetworkInfo();

		if (cwjManager.getActiveNetworkInfo() != null) {
			netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
		}
		return netSataus;
	}
	/**
	 * 判断wifi状态
	 * @param activitiy
	 * @return
	 */
	public static boolean checkWifi(Activity activitiy) {  
        WifiManager mWifiManager = (WifiManager) activitiy  
                .getSystemService(Context.WIFI_SERVICE);  
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();  
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();  
        if (mWifiManager.isWifiEnabled() && ipAddress != 0) {  
                System.out.println("**** WIFI is on");  
                    return true;  
        } else {  
                    System.out.println("**** WIFI is off");  
                    return false;      
        }  
	}  

}
