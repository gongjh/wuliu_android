package cn.megasound.youthbegan.wuliumanager.view;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.dao.CompanyDao;
import cn.megasound.youthbegan.wuliumanager.dao.VersionDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.CompanyDaoImpl;
import cn.megasound.youthbegan.wuliumanager.dao.impl.VersionDaoImpl;
import cn.megasound.youthbegan.wuliumanager.entity.Company;
import cn.megasound.youthbegan.wuliumanager.entity.Version;
import cn.megasound.youthbegan.wuliumanager.util.PromptUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnClickListener{
	
	private int reCount = 5;
	private boolean isConnected = false;//false:没有连接上    true:连接上了
	private Button inputBT, outputBT, contactBT, lookforBT;
	private Intent intent;
	
	private CompanyDao companyDao ;
	private VersionDao vDao;
	private static Version version;
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void findViewById() {
		inputBT = (Button)findViewById(R.id.home_input_bt);
		outputBT = (Button)findViewById(R.id.home_output_bt);
		contactBT = (Button)findViewById(R.id.home_contact_bt);
		lookforBT = (Button)findViewById(R.id.home_lookfor_bt);
	}
	
	@Override
	protected void init() {
		//初始化快递公司列表
		companyDao = new CompanyDaoImpl();
		getCompanys();
		vDao = new VersionDaoImpl();
		version = new Version();
		getVersionFromServer();
	}

	@Override
	protected void setListener() {
		inputBT.setOnClickListener(this);
		outputBT.setOnClickListener(this);
		contactBT.setOnClickListener(this);
		lookforBT.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.home_input_bt:
			if(isConnected){
				intent = new Intent(MainActivity.this, InputActivity.class);
				startActivity(intent);
			}else{
				Toast.makeText(MainActivity.this, "没有连接上服务器~~~", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.home_output_bt:
			if(isConnected){
				intent = new Intent(MainActivity.this, OutputActivity.class);
				startActivity(intent);
			}else{
				Toast.makeText(MainActivity.this, "没有连接上服务器~~~", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.home_contact_bt:
			if(isConnected){
				intent = new Intent(MainActivity.this, ContactActivity.class);
				startActivity(intent);
			}else{
				Toast.makeText(MainActivity.this, "没有连接上服务器~~~", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.home_lookfor_bt:
			if(isConnected){
				intent = new Intent(MainActivity.this, LookforActivity.class);
				startActivity(intent);
			}else{
				Toast.makeText(MainActivity.this, "没有连接上服务器~~~", Toast.LENGTH_SHORT).show();
			}
			break;
			default:
				break;
		}
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x101){
				getCompanys();
			}
			if(msg.what==0x102){
				checkVersion();
			}
			if(msg.what==0x103){
				//对话框通知用户升级程序      
	        	showUpdateEditionDialog();
			}
			if(msg.what==0x104){
				//下载apk失败     
	        	Toast.makeText(MainActivity.this, "下载新版本失败。", Toast.LENGTH_SHORT).show(); 
	        	pd.dismiss();
			}
		}
		
	};
	
	

	/**
	 * 获取快递公司
	 */
	public void getCompanys(){
		new AsyncTask<Void, Void, List<Company>>(){

			@Override
			protected List<Company> doInBackground(Void... params) {
				return companyDao.getCompany();
			}

			@Override
			protected void onPostExecute(List<Company> result) {
				if(result==null){
					//连不上服务器，开启对话框
					if(reCount>=0){
						showPromptDialog();
						reCount--;
					}else{
						Toast.makeText(MainActivity.this, "对不起，重新连接不能超过5次。", Toast.LENGTH_SHORT).show();
					}
					return;
				}
				if(result.size()==0){
					Toast.makeText(MainActivity.this, "没有数据，请联系管理员。", Toast.LENGTH_SHORT).show();
					return;
				}
				isConnected = true;
				ConstantValue.companys = result ;
			}
			
		}.execute();
	}
	
	public void showPromptDialog(){
		new AlertDialog.Builder(this)
			.setTitle("提示")
			.setMessage("连接不上服务器，确定要重试吗？")
			.setCancelable(false)
			.setNeutralButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Message msg = handler.obtainMessage();
					msg.what = 0x101;
					handler.sendMessage(msg);
					dialog.dismiss();
				}
			}).show();
	}
	
	/**
	 * 获取当前版本
	 * @return
	 * @throws Exception 
	 */
	public String getPresentVersion(){
		PackageManager packageManager = getPackageManager();
		//getPackageName()是获取当前的类名，0代表 是获取版本信息
		PackageInfo packageInfo=null;
		try {
			packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packageInfo.versionName;
	}
	
	public void checkVersion(){
		String precVersion = getPresentVersion() ;
		if(version==null){
			Log.i("test", "-----version is null----");
			return;
		}
		String url = version.url;
		float serviceV = Float.parseFloat(version.number.substring(0, 3));
		float presentV = Float.parseFloat(precVersion.substring(0,3));
		Log.i("test", "当前版本信息:"+precVersion+" 服务端："+version.number+" url:"+url);
		if(serviceV > presentV){
			Message msg = handler.obtainMessage();    
            msg.what = 0x103;    
            handler.sendMessage(msg);
		}else if(serviceV==presentV){
			if(version.number.length()>precVersion.length()){
				Message msg = handler.obtainMessage();    
	            msg.what = 0x103;    
	            handler.sendMessage(msg);
	            return;
			}
			if((version.number.length()==6 && precVersion.length()==6)){
				float s = Float.parseFloat(version.number.substring(5));
				
				float p = Float.parseFloat(precVersion.substring(5));
				if(s>p){
					Message msg = handler.obtainMessage();    
		            msg.what = 0x103;    
		            handler.sendMessage(msg);
				}
			}
		}
	}
	
	/**
	 * 获取服务器端的版本号
	 */
	public void getVersionFromServer(){
		new AsyncTask<Void, Void, Version>(){

			@Override
			protected Version doInBackground(Void... params) {
				return vDao.getFromServer();
			}

			@Override
			protected void onPostExecute(Version result) {
				if(result==null)
					return;
				version = result;
				Log.i("test", "version="+version.number+";url="+version.url);
				Message msg = handler.obtainMessage();
				msg.what = 0x102;
				handler.sendMessage(msg);
			}
			
		}.execute();
	}
	
	/**
	 * 显示更新版本对话框
	 */
	private void showUpdateEditionDialog(){
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("新的版本"+version.number+"已经发布，是否立即下载升级？");
		builder.setTitle("提示");
		builder.setPositiveButton(getResources().getString(R.string.ok),
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Log.i("test","下载apk,更新");    
				        downLoadApk();
					}
				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	ProgressDialog pd;    //进度条对话框     
	/**
	 * 更新版本
	 * 1.下载新的版本
	 * 2.安装
	 */
	public void downLoadApk(){
	    pd = new  ProgressDialog(this);    
	    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);    
	    pd.setMessage("正在下载更新");    
	    pd.show();    
	    new Thread(){    
	        @Override    
	        public void run() {    
	            try {    
	                File file = getFileFromServer(version.url, pd);    
	                sleep(3000);    
	                installApk(file); //安装新的apk   
	                pd.dismiss(); //结束掉进度条对话框     
	            } catch (Exception e) {    
	                Message msg = handler.obtainMessage();    
	                msg.what = 0x104;    
	                handler.sendMessage(msg);    
	                e.printStackTrace();    
	            }    
	        }}.start();    
	}
	
	/**
	 * 从服务器端下载新的apk
	 * @param path
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public static File getFileFromServer(String path, ProgressDialog pd) throws Exception{     
	    //如果相等的话表示当前的sdcard挂载在手机上并且是可用的     
	    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){     
	        URL url = new URL(path);     
	        Log.i("test", "getFileFromServer"+"：path="+path);
	        HttpURLConnection conn =  (HttpURLConnection) url.openConnection();     
	        conn.setConnectTimeout(5000);     
	        //获取到文件的大小       
	        pd.setMax(conn.getContentLength());     
	        InputStream is = conn.getInputStream();     
	        File file = new File(Environment.getExternalStorageDirectory(), "WuLiuManager_"+version.number+".apk");     
	        FileOutputStream fos = new FileOutputStream(file);     
	        BufferedInputStream bis = new BufferedInputStream(is);     
	        byte[] buffer = new byte[1024];     
	        int len ;     
	        int total=0;     
	        while((len =bis.read(buffer))!=-1){     
	            fos.write(buffer, 0, len);     
	            total+= len;     
	            //获取当前下载量      
	            pd.setProgress(total);     
	        }     
	        fos.close();     
	        bis.close();     
	        is.close();     
	        return file;     
	    }     
	    else{     
	        return null;     
	    }     
	} 
	
	/**
	 * 安装apk
	 * @param file
	 */
	protected void installApk(File file) { 
		if(!file.exists()){
			return;
		}
	    Intent intent = new Intent();    
	    //执行动作     
	    intent.setAction(Intent.ACTION_VIEW);    
	    //执行的数据类型                                                                  此处Android应为android，否则造成安装不了
	    //intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");    
	    intent.setDataAndType(Uri.parse("file://"+file.toString()), "application/vnd.android.package-archive");
	    startActivity(intent);    
	} 
	
	@Override
	protected void onPause() {
		super.onPause();
		
	}

	@Override
	public void onBackPressed() {
		PromptUtil.exitDialog(MainActivity.this);
	}

}
