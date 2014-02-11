package cn.megasound.youthbegan.wuliumanager.view;

import java.util.ArrayList;
import java.util.List;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.adapter.ContactsAdapter;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.dao.ContactsDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.ContactsDaoImpl;
import cn.megasound.youthbegan.wuliumanager.entity.Company;
import cn.megasound.youthbegan.wuliumanager.entity.Contacts;
import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;
import cn.megasound.youthbegan.wuliumanager.view.ContactAddDialog.OnOkListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ContactActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "ContactActivity";
	private static final int FAILED = 1;
	private static final int NOTSENT = 2;
	private int type = 0;
	private int nowPageFailed = 1,nowPageNotsent = 1, msgState = 0;
	private boolean isCanLoadMore = false, isLoading = false;
	private int messageState = 0;
	private Button sentfailedBT, notsentBT;
	private ListView contactLV;
	private ContactsDao cDao;
	private ContactsAdapter cAdapter;
	
	private int cid = 0;
	private int mState=-1;
	
	private Contacts contacts = new Contacts();
	private PresentGoods presentGoods=new PresentGoods();
	private ContactAddDialog addDialog;
	List<String> actionList;
	ListAdapter actionAdapter;
	
	private mServiceReceiver mReceiver01, mReceiver02;
	/* 自定义ACTION常数，作为广播的Intent Filter识别常数 */
	private String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
	private String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

	@Override
	protected void setContentView() {
		setContentView(R.layout.contact_home);
	}
	
	@Override
	protected void findViewById() {
		sentfailedBT = (Button) findViewById(R.id.contact_home_sentfailed_bt);
		notsentBT = (Button)findViewById(R.id.contact_home_notsent_bt);
		contactLV = (ListView)findViewById(R.id.contact_home_lv);
		//contactLV.setDivider(getApplicationContext().getResources().getDrawable(R.drawable.contact_home_lv_divider));
	}

	@Override
	protected void init() {
		cDao = new ContactsDaoImpl();
		cAdapter = new ContactsAdapter(FAILED,ContactActivity.this, ContactActivity.this, 
				new ArrayList<Contacts>(),new ArrayList<Contacts>(),handler);
		type = FAILED;
		
		actionList = new ArrayList<String>();
		actionList.add("重新发送短信");
		actionList.add("添加/修改备注");
		actionList.add("删除该项");
		actionList.add("查看详情");
	}

	@Override
	protected void setListener() {
		sentfailedBT.setOnClickListener(this);
		notsentBT.setOnClickListener(this);
		contactLV.setOnScrollListener(new MyScrollListener());
		contactLV.setAdapter(cAdapter);
		
	}

	@Override
	protected void onResume() {
		/* 自定义IntentFilter为SENT_SMS_ACTIOIN Receiver */
		IntentFilter mFilter01;
		mFilter01 = new IntentFilter(SMS_SEND_ACTIOIN);
		mReceiver01 = new mServiceReceiver();
		registerReceiver(mReceiver01, mFilter01);
		
		/* 自定义IntentFilter为DELIVERED_SMS_ACTION Receiver */
		mFilter01 = new IntentFilter(SMS_DELIVERED_ACTION);
		mReceiver02 = new mServiceReceiver();
		registerReceiver(mReceiver02, mFilter01);
		super.onResume();
		Log.i(TAG, "on resume");
		nowPageFailed = 1;
		nowPageNotsent = 1;
		switch(type){
			case NOTSENT:
				type = NOTSENT;
				cAdapter.setType(NOTSENT);
				sentfailedBT.setBackgroundResource(R.drawable.contact_home_sentfailed_up);
				notsentBT.setBackgroundResource(R.drawable.contact_home_notsent_down);
				msgState = 1;
				fillLVNotsent(nowPageNotsent,msgState);
				break;
			case FAILED:
			default:
				type = FAILED;
				cAdapter.setType(FAILED);
				sentfailedBT.setBackgroundResource(R.drawable.contact_home_sentfailed_down);
				notsentBT.setBackgroundResource(R.drawable.contact_home_notsent_up);
				msgState = 0;
				fillLVFailed(nowPageFailed,msgState);
				break;
		}
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what){
				case 0x501:
					Bundle data = msg.getData();
					contacts = (Contacts) data.getSerializable("contacts");
					presentGoods = (PresentGoods) data.getSerializable("presentGoods");
					Log.i(TAG, "contacts info:"+contacts);
//					new ArrayAdapter(ContactActivity.this, 
//							android.R.layout.simple_list_item_activated_1, actionList);
					actionAdapter = new ArrayAdapter(ContactActivity.this, 
							R.layout.contact_menu, R.id.contact_menu_tv, actionList);
					cid = contacts.id;
					new AlertDialog.Builder(ContactActivity.this)
						.setAdapter(actionAdapter, 
							new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Log.i(TAG, "choose:"+which);
									switch(which){
										case 0://重新发送
											mState = contacts.messagestate;
											Log.i(TAG, "id="+cid+";mState="+mState);
											if(presentGoods.phoneNum.length()!=11){
												Toast.makeText(ContactActivity.this, "您填写的电话号码不正确，请填写正确的电话号码。", 
														Toast.LENGTH_SHORT).show();
												return;
											}
											sentMessage(cid,presentGoods);
											break;
										case 1://添加/修改备注
											addDialog = new ContactAddDialog(ContactActivity.this, contacts.remark);
											addDialog.show();
											//添加/修改备注       接收回调数据
											addDialog.setOkListener(new OnOkListener() {
												
												@Override
												public void setOnClickListener(String con) {
													Log.i(TAG, "cid="+cid);
													updateContacts(cid,con);
												}
											});
											dialog.dismiss();
											break;
										case 2://删除该项
											deleteContacts(cid);
											dialog.dismiss();
											break;
										case 3://查看详情,跳到派件详情页面
											Intent i = new Intent(ContactActivity.this,DetailActivity.class);
											Bundle data = new Bundle();
											
											data.putSerializable("presentGoods", presentGoods);
											i.putExtras(data);
											startActivity(i);
											break;
									}
								}
							}).show();	
					break;
				case 0x412:
					Log.i(TAG,"0x412:onResume()");
					onResume();
					break;
				case 0x414:
					Log.i(TAG, "mState="+mState+";id="+cid+";state="+messageState);
					if( messageState==1){
						updateMessageState(cid,messageState);
					}
					break;
				default:
					break;
			}
		}
		
	};

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.contact_home_sentfailed_bt://未发送成功
				sentfailedBT.setBackgroundResource(R.drawable.contact_home_sentfailed_down);
				notsentBT.setBackgroundResource(R.drawable.contact_home_notsent_up);
				type = FAILED;
				cAdapter.setType(type);
				nowPageFailed = 1;
				msgState = 0;
				fillLVFailed(nowPageFailed,msgState);
				break;
			case R.id.contact_home_notsent_bt://货物未取
				sentfailedBT.setBackgroundResource(R.drawable.contact_home_sentfailed_up);
				notsentBT.setBackgroundResource(R.drawable.contact_home_notsent_down);
				type = NOTSENT;
				cAdapter.setType(type);
				nowPageNotsent = 1;
				msgState = 1;
				fillLVNotsent(nowPageNotsent,msgState);
				break;
		}
	}
	
	/**
	 * 用  未发送成功列表   填充ListView
	 */
	public void fillLVFailed(final int page,final int messagestate){
		new AsyncTask<Void, Void, List<Contacts>>(){

			@Override
			protected List<Contacts> doInBackground(Void... params) {
				isLoading = true;
				return cDao.getContacts(page, messagestate);
			}

			@Override
			protected void onPostExecute(List<Contacts> result) {
				if(result==null){
					Toast.makeText(ContactActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return ;
				}
				//判断是否有更多加载的
				if(isCanLoadMore){
					for(Contacts c:result){
						cAdapter.addItem(FAILED,c,null);
					}
				}else{
					cAdapter.setFailedContacts(result);
				}
				
				if(result.size()==0){
					Toast.makeText(ContactActivity.this, "暂时没有该条件的数据。", Toast.LENGTH_SHORT).show();
					cAdapter.setFailedContacts(result);
				}
				
				//判断是否可继续加载，每页固定15条
				if(result.size()<15){
					isCanLoadMore = false;
				}else if(result.size()==15 && (nowPageFailed==ConstantValue.CONTACT_TOTAL/15) && (ConstantValue.CONTACT_TOTAL%15==0)){
					isCanLoadMore = false;
				}
				else{
					isCanLoadMore = true;
				}
				cAdapter.notifyDataSetChanged();
				nowPageFailed++;
				isLoading = false;
			}
			
		}.execute();
	}
	
	/**
	 * 用  未发送成功列表   填充ListView
	 */
	public void fillLVNotsent(final int page,final int messagestate){
		new AsyncTask<Void, Void, List<Contacts>>(){

			@Override
			protected List<Contacts> doInBackground(Void... params) {
				isLoading = true;
				return cDao.getContacts(page, messagestate);
			}

			@Override
			protected void onPostExecute(List<Contacts> result) {
				if(result==null){
					Toast.makeText(ContactActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return ;
				}
				//判断是否有更多加载的
				if(isCanLoadMore){
					for(Contacts c:result){
						cAdapter.addItem(NOTSENT,null,c);
					}
				}else{
					cAdapter.setNotsentContacts(result);
				}
				//判断是否可继续加载，每页固定15条
				if(result.size()<15){
					isCanLoadMore = false;
				}else if(result.size()==15 && (nowPageNotsent==ConstantValue.CONTACT_TOTAL/15) && (ConstantValue.CONTACT_TOTAL%15==0)){
					isCanLoadMore = false;
				}
				else{
					isCanLoadMore = true;
				}
				
				if(result.size()==0){
					Toast.makeText(ContactActivity.this, "暂时没有该条件的数据。", Toast.LENGTH_SHORT).show();
					cAdapter.setNotsentContacts(result);
				}
				
				cAdapter.notifyDataSetChanged();
				nowPageNotsent++;
				isLoading = false;
			}
			
		}.execute();
	}
	
	/**
	 * 添加remark
	 */
	public void updateContacts(final int id,final String remark){
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				return cDao.updateContacts(id, remark);
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==-1){
					Toast.makeText(ContactActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return;
				}
				if(result==0){
					Toast.makeText(ContactActivity.this, "备注添加/修改失败。", Toast.LENGTH_SHORT).show();
					return;
				}
				Toast.makeText(ContactActivity.this, "备注添加/修改成功。", Toast.LENGTH_SHORT).show();
				Message msg = handler.obtainMessage();
				msg.what = 0x412;
				handler.sendMessage(msg);
			}
			
		}.execute();		
	}
	
	/**
	 * 调用系统的发短信的功能
	 */
	public void sentMessage(final int id,final PresentGoods p){
		SmsManager smsManager = SmsManager.getDefault();
		Intent send = new Intent();
		send.setAction(SMS_SEND_ACTIOIN);
		PendingIntent sentIntent = PendingIntent.getBroadcast(ContactActivity.this, 0, send, 0);
		Intent delivery = new Intent();
		delivery.setAction(SMS_DELIVERED_ACTION);
		PendingIntent deliveryIntent = PendingIntent.getBroadcast(ContactActivity.this, 0, delivery, 0);
		//编辑短信
		String message = p.name+"您有一件"+p.company.name+"的货件在"+
				ConstantValue.station+"站点"+ConstantValue.station_detail+"，请速来领取，取货号是"+
				p.shelfNum+"，快递单号是"+p.orderNum;
		smsManager.sendTextMessage(p.phoneNum, null, message, sentIntent, null);//deliveryIntent
	}
		
     /**
      * 修改短信状态
      * @param id
      * @param message
      */
     public void updateMessageState(final int id,final int message){
    	 new AsyncTask<Void, Void, Integer>() {

 			@Override
 			protected Integer doInBackground(Void... params) {
 				return cDao.updateContacts(id, message);
 			}

 			@Override
 			protected void onPostExecute(Integer result) {
 				if(result==-1){
					Toast.makeText(ContactActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return;
				}
				if(result==0){
					Toast.makeText(ContactActivity.this, "状态修改失败。", Toast.LENGTH_SHORT).show();
					return;
				}
 				if(result==1){
	 				Toast.makeText(ContactActivity.this, "状态修改成功。", Toast.LENGTH_SHORT).show();
	 				//需要向handler请求刷新
	 				Message me = handler.obtainMessage();
	 				me.what = 0x412;
	 				handler.sendMessage(me);
 				}
 			}
 			
 		}.execute();
     }
     
	 private class MyScrollListener implements AbsListView.OnScrollListener {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (firstVisibleItem + visibleItemCount == totalItemCount
					&& totalItemCount > 1) {
				switch(type){
					case FAILED:
						if (isCanLoadMore && !isLoading) {
							isLoading = true;
							fillLVFailed(nowPageFailed,msgState);
						}
						break;
					case NOTSENT:
						if (isCanLoadMore && !isLoading) {
							isLoading = true;
							fillLVNotsent(nowPageNotsent,msgState);
						}
						break;
				}
			}
		}
	}
	 
	/**
	 * 接收短信发送成功与否的广播
	 * @author Administrator
	 *
	 */
	private class mServiceReceiver extends BroadcastReceiver{
	    
		@Override
	    public void onReceive(Context context, Intent intent){
			try {
				/* android.content.BroadcastReceiver.getResultCode()方法 */
				switch(getResultCode()){
				case Activity.RESULT_OK:
					/* 发送短信成功 */
					Toast.makeText(ContactActivity.this, "短信发送成功", 
	                        Toast.LENGTH_SHORT).show();
					messageState = 1;
					Log.i(TAG, "messageState="+messageState);
					//当原来的短信状态为0才能修改，向handler请求更新短信状态
					Message msg = handler.obtainMessage();
					msg.what = 0x414;
					handler.sendMessage(msg);
					
					break;
				default:// SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					/* 发送短信失败 */
					Toast.makeText(ContactActivity.this, "短信发送失败", 
	                        Toast.LENGTH_SHORT).show();
					messageState = 0;
					Log.i(TAG, "messageState="+messageState);
					break;
				
				}   
			}
			catch(Exception e){
	    	  	e.getStackTrace();
	      	}
			
	   }
	}
	
	/**
	 * 删除联系人
	 * @param cid
	 */
	public void deleteContacts(final int cid){
		Log.i("test", "goodsadapter:删除操作！");
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				Log.i("test", "delete:cid="+cid);
				return cDao.deleteContacts(cid);
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==-1){
					Toast.makeText(ContactActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return;
				}
				if(result==0){
					Toast.makeText(ContactActivity.this, "删除操作失败。", Toast.LENGTH_SHORT).show();
					return;
				}
				Toast.makeText(ContactActivity.this, "成功删除。", Toast.LENGTH_SHORT).show();
				Message msg = handler.obtainMessage();
				msg.what = 0x412;
				handler.sendMessage(msg);
			}
			
		}.execute();
	}

	@Override
	protected void onPause() {
		/* 取消注册自定义Receiver */
	    unregisterReceiver(mReceiver01);
	    unregisterReceiver(mReceiver02);
		super.onPause();
	}

}
