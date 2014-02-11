package cn.megasound.youthbegan.wuliumanager.view;

import java.util.ArrayList;
import java.util.List;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.dao.PresentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.PresentGoodsDaoImpl;
import cn.megasound.youthbegan.wuliumanager.entity.Company;
import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "UpdateActivity";
	
	private Spinner companySP;
	private EditText telET,nameET,orderET,shelfET;
	private TextView ptTV,jjTV,tcTV,zlTV;
	private Button updateBT, scanBT, sendMessageBT;
	private PresentGoods p;
	private PresentGoodsDao presentgoodsDao;
	private mServiceReceiver mReceiver01, mReceiver02;
	private boolean isChoose = false;
	
	private List<String> cList ;
	private List<Company> companyList ;
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.output_update);
	}
	
	@Override
	protected void findViewById() {
		companySP = (Spinner)findViewById(R.id.output_update_company_sp);
		telET = (EditText)findViewById(R.id.output_update_tel_et);
		nameET = (EditText)findViewById(R.id.output_update_name_et); 
		orderET = (EditText)findViewById(R.id.output_update_order_et);
		shelfET = (EditText)findViewById(R.id.output_update_shelf_et);
		ptTV = (TextView)findViewById(R.id.output_update_type_pt);
		jjTV = (TextView)findViewById(R.id.output_update_type_jj);
		tcTV = (TextView)findViewById(R.id.output_update_type_tc);
		zlTV = (TextView)findViewById(R.id.output_update_type_zl);
		updateBT = (Button)findViewById(R.id.output_update_save_bt);
		scanBT = (Button)findViewById(R.id.output_update_order_bt);
		sendMessageBT = (Button)findViewById(R.id.output_update_send_message);
	}
	
	@Override
	protected void init() {
		p = new PresentGoods();
		presentgoodsDao = new PresentGoodsDaoImpl();
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		p = (PresentGoods) data.getSerializable("presentGoods");
		
		//填充快递公司的下拉框
		companyList = ConstantValue.companys;
		cList = new ArrayList<String>();
		for(Company c:companyList){
			cList.add(c.name);
		}
		ArrayAdapter<String> spadapter=new ArrayAdapter<String>
			(UpdateActivity.this, android.R.layout.simple_spinner_item, cList);
		spadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		companySP.setAdapter(spadapter);
		int position = -1;
		for(int i=0;i<cList.size();i++){
			position++;
			if(p.company.name.equals(cList.get(i))){
				break;
			}
		}
		companySP.setSelection(position);
		
		telET.setText(p.phoneNum);
		nameET.setText(p.name);
		Log.i(TAG, "type="+p.type);
		//1:普通、2:同城、3:加急、4:遗留
		switch(p.type){
			case 1:
				Log.i(TAG, "type=普通件");
				ptTV.setBackgroundResource(R.drawable.input_newform_type_down);
				isChoose = true;
				break;
			case 2:
				tcTV.setBackgroundResource(R.drawable.input_newform_type_down);
				isChoose = true;
				break;
			case 3:
				jjTV.setBackgroundResource(R.drawable.input_newform_type_down);
				isChoose = true;
				break;
			case 4:
				zlTV.setBackgroundResource(R.drawable.input_newform_type_down);
				isChoose = true;
				break;
			default:
				break;
		}
		orderET.setText(p.orderNum);
		shelfET.setText(p.shelfNum);
	}

	@Override
	protected void setListener() {
		ptTV.setOnClickListener(this);
		jjTV.setOnClickListener(this);
		tcTV.setOnClickListener(this);
		zlTV.setOnClickListener(this);
		updateBT.setOnClickListener(this);
		scanBT.setOnClickListener(this);
		sendMessageBT.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		/* 自定义IntentFilter为SENT_SMS_ACTIOIN Receiver */
	    IntentFilter mFilter01;
	    mFilter01 = new IntentFilter(ConstantValue.SMS_SEND_ACTIOIN);
	    mReceiver01 = new mServiceReceiver();
	    registerReceiver(mReceiver01, mFilter01);
	    
	    /* 自定义IntentFilter为DELIVERED_SMS_ACTION Receiver */
	    mFilter01 = new IntentFilter(ConstantValue.SMS_DELIVERED_ACTION);
	    mReceiver02 = new mServiceReceiver();
	    registerReceiver(mReceiver02, mFilter01);
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.output_update_save_bt://修改后的保存按钮
				//如果没有选择类型type:则点击无效	
				if(isChoose){
					String s = companySP.getSelectedItem().toString();
					int companyId = 0;
					List<Company> cl = ConstantValue.companys;
					for(Company c: cl){
						if(c.name.equals(s)){
							companyId = c.id;
						}
					}
					p.company.id = companyId;
					p.company.name = s ;
					p.phoneNum = ""+telET.getText();
					p.name = ""+nameET.getText();
					p.orderNum = ""+orderET.getText();
					p.shelfNum = ""+shelfET.getText();
					if(p.phoneNum.length()!=11){
						Toast.makeText(this, "您填写的电话号码不正确，请填写正确的电话号码。", Toast.LENGTH_SHORT).show();
						return;
					}
					if( TextUtils.isEmpty(p.orderNum)){
						Toast.makeText(this, "请填写货单编号。", Toast.LENGTH_SHORT).show();
						return;
					}
					if(TextUtils.isEmpty(p.shelfNum)){
						Toast.makeText(this, "请填写货柜编号。", Toast.LENGTH_SHORT).show();
						return;
					}
					updatePresentGoods();
				}else{
					Toast.makeText(this, "请选择货单类型", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.output_update_type_pt://普通类型
				if(!isChoose){//初始没有选
					p.type = 1 ;
					ptTV.setBackgroundResource(R.drawable.input_newform_type_down);
					jjTV.setBackgroundResource(R.drawable.input_newform_type_up);
					tcTV.setBackgroundResource(R.drawable.input_newform_type_up);
					zlTV.setBackgroundResource(R.drawable.input_newform_type_up);
					isChoose = true ;
				}else{
					ptTV.setBackgroundResource(R.drawable.input_newform_type_up);
					isChoose = false ;
				}
				break;
			case R.id.output_update_type_jj://加急类型
				if(!isChoose){
					p.type = 3 ;
					jjTV.setBackgroundResource(R.drawable.input_newform_type_down);
					ptTV.setBackgroundResource(R.drawable.input_newform_type_up);
					tcTV.setBackgroundResource(R.drawable.input_newform_type_up);
					zlTV.setBackgroundResource(R.drawable.input_newform_type_up);
					isChoose = true ;
				}else{
					jjTV.setBackgroundResource(R.drawable.input_newform_type_up);
					isChoose = false ;
				}
				break;
			case R.id.output_update_type_tc://同城类型
				if(!isChoose){
					p.type = 2 ;
					tcTV.setBackgroundResource(R.drawable.input_newform_type_down);
					ptTV.setBackgroundResource(R.drawable.input_newform_type_up);
					jjTV.setBackgroundResource(R.drawable.input_newform_type_up);
					zlTV.setBackgroundResource(R.drawable.input_newform_type_up);
					isChoose = true ;
				}else{
					tcTV.setBackgroundResource(R.drawable.input_newform_type_up);
					isChoose = false ;
				}
				break;
			case R.id.output_update_type_zl://滞留类型
				if(!isChoose){
					p.type = 4 ;
					zlTV.setBackgroundResource(R.drawable.input_newform_type_down);
					ptTV.setBackgroundResource(R.drawable.input_newform_type_up);
					jjTV.setBackgroundResource(R.drawable.input_newform_type_up);
					tcTV.setBackgroundResource(R.drawable.input_newform_type_up);
					isChoose = true ;
				}else{
					zlTV.setBackgroundResource(R.drawable.input_newform_type_up);
					isChoose = false ;
				}
				break;
			case R.id.output_update_order_bt://扫描按钮
				Intent intent = new Intent(UpdateActivity.this, CaptureActivity.class);
				intent.putExtra("type", 2);
				startActivityForResult(intent, 0x211);
				break;
			case R.id.output_update_send_message://修改页面中，如果滞留件第二天重新需要发送短信的按钮，但是得先修改。
				sentMessage();
				break;
			default:
				break;
		}
	}
	
	public void updatePresentGoods(){
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				return presentgoodsDao.updateGoods(p.id, p.orderNum,
						p.phoneNum, p.company.id, p.shelfNum, p.type, p.city, p.name);
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==-1){
					Toast.makeText(UpdateActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return;
				}
				if(result==0){
					Toast.makeText(UpdateActivity.this, "修改失败。", Toast.LENGTH_SHORT).show();
					return;
				}
				Toast.makeText(UpdateActivity.this, "修改成功。", Toast.LENGTH_SHORT).show();
			}
			
		}.execute();
	}
	
	public void getInfoBeforeSend(final int pid){
		
	}
	
	/**
	 * 调用系统的发短信的功能
	 */
	public void sentMessage(){
		Log.i(TAG, "sentMessage()");
		SmsManager smsManager = SmsManager.getDefault();
		Intent send = new Intent();
		send.setAction(ConstantValue.SMS_SEND_ACTIOIN);
		PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, send, 0);
		Intent delivery = new Intent();
		delivery.setAction(ConstantValue.SMS_DELIVERED_ACTION);
		PendingIntent deliveryIntent = PendingIntent.getBroadcast(this, 0, delivery, 0);
		//编辑短信
		String message = p.name+"您有一件"+p.company.name+"的货件在"+
				ConstantValue.station+"站点"+ConstantValue.station_detail+"，请速来领取，取货号是"+
				p.shelfNum+"，快递单号是"+p.orderNum;
		Log.i(TAG, message);
		smsManager.sendTextMessage(p.phoneNum, null, message, sentIntent, null);//deliveryIntent
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
							Toast.makeText(UpdateActivity.this, "短信发送成功", 
			                        Toast.LENGTH_SHORT).show();
							break;
						default:// SmsManager.RESULT_ERROR_GENERIC_FAILURE:
							/* 发送短信失败 */
							Toast.makeText(UpdateActivity.this, "短信发送失败", 
			                        Toast.LENGTH_SHORT).show();
							break;
					}      
		      }
		      catch(Exception e){
		    	  	e.getStackTrace();
		      }
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==0x211){
			if(resultCode==1){
				String str = data.getStringExtra("resultStr");
				int isUseful = data.getIntExtra("isUseful", 0);
				Log.i("test", "211:resultCode = "+str+";isUseful="+isUseful);
				if(isUseful==-1){
					Toast.makeText(UpdateActivity.this, "对不起，连不上服务器。", Toast.LENGTH_SHORT).show();
				}else if(isUseful==0){
					orderET.setText(str);
				}else if(isUseful==1){
					Toast.makeText(UpdateActivity.this, "该订单号已存在", Toast.LENGTH_SHORT).show();
				}else if(isUseful==2){
					Toast.makeText(UpdateActivity.this, "该订单号已派送。", Toast.LENGTH_SHORT).show();
				}else if(isUseful==3){
					Toast.makeText(UpdateActivity.this, "该订单号已遣返。", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	protected void onPause() {
		/* 取消注册自定义Receiver */
	    unregisterReceiver(mReceiver01);
	    unregisterReceiver(mReceiver02);
		super.onPause();
	}

	
	
//	@Override
//	public void onBackPressed() {
//		Intent data = new Intent();
//		data.putExtra("company_id", p.company.id);
//		data.putExtra("company_name", p.company.name);
//		data.putExtra("tel", p.phoneNum);
//		data.putExtra("name", p.name);
//		data.putExtra("type", p.type);
//		data.putExtra("order", p.orderNum);
//		data.putExtra("shelf", p.shelfNum);
//		setResult(update_result, data);
//		this.finish();
//	}

}
