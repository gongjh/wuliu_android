package cn.megasound.youthbegan.wuliumanager.view;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.dao.ContactsDao;
import cn.megasound.youthbegan.wuliumanager.dao.PresentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.ContactsDaoImpl;
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
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewformActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "NewformActivity";
	
	private Spinner companySP;
	private TextView ptTV, tcTV, jjTV, zlTV;
	private EditText orderET, telephoneET, shelfET, nameET;
	private Button saveBT, orderBT;
	
	private List<String> cList ;
	private List<Company> companyList ;
	private int type = 1;
	
	private PresentGoods presentGoods= new PresentGoods();
	private boolean isChoose = false;
	private int messageState = -1; //发送信息失败:0     成功:1
	public PresentGoodsDao presentgoodsDao ;
	public ContactsDao contactsDao ;
	
	private mServiceReceiver mReceiver01, mReceiver02;
	private int isBack = 0;//0:未操作过   1:有操作过
	//控制按BACK键
	private boolean canBack = true;
	//是否是从遣返表中回滚的
	private boolean canGetFromWeb = false;

	@Override
	protected void setContentView() {
		setContentView(R.layout.input_newform);

	}

	@Override
	protected void findViewById() {
		companySP = (Spinner)findViewById(R.id.input_newform_company_sp);
		ptTV = (TextView)findViewById(R.id.input_newform_type_pt);
		jjTV = (TextView)findViewById(R.id.input_newform_type_jj);
		tcTV = (TextView)findViewById(R.id.input_newform_type_tc);
		zlTV = (TextView)findViewById(R.id.input_newform_type_zl);
		orderET = (EditText)findViewById(R.id.input_newform_order_et);
		telephoneET = (EditText)findViewById(R.id.input_newform_tel_et);
		shelfET = (EditText)findViewById(R.id.input_newform_shelf_et);
		nameET = (EditText)findViewById(R.id.input_newform_name_et);
		
		saveBT = (Button)findViewById(R.id.input_newform_save_bt);
		orderBT = (Button)findViewById(R.id.input_newform_order_bt);
	}
	
	@Override
	protected void init() {
		presentgoodsDao = new PresentGoodsDaoImpl();
		contactsDao  = new ContactsDaoImpl();
		
		//填充快递公司的下拉框
		companyList = ConstantValue.companys;
		cList = new ArrayList<String>();
		for(Company c:companyList){
			cList.add(c.name);
		}
		ArrayAdapter<String> spadapter=new ArrayAdapter<String>
			(NewformActivity.this, android.R.layout.simple_spinner_item, cList);
		spadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		companySP.setAdapter(spadapter);
		companySP.setSelection(ConstantValue.lastCompany-1, true);
		//设置默认值
		isChoose = true;
		ptTV.setBackgroundResource(R.drawable.input_newform_type_down);
		jjTV.setBackgroundResource(R.drawable.input_newform_type_up);
		tcTV.setBackgroundResource(R.drawable.input_newform_type_up);
		zlTV.setBackgroundResource(R.drawable.input_newform_type_up);
		shelfET.setText("121");
		
	}
	
	@Override
	protected void setListener() {
		saveBT.setOnClickListener(this);
		orderBT.setOnClickListener(this);
		ptTV.setOnClickListener(this);
		jjTV.setOnClickListener(this);
		tcTV.setOnClickListener(this);
		zlTV.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		Log.i(TAG, "onResume()");
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
			/*先保存到服务器，从服务器端返回当前的货件id
			      然后发送短信
			      通过返回的货件id，将发送短信的情况保存到服务器端*/
			case R.id.input_newform_save_bt:
				if(canGetFromWeb){
					canGetFromWeb = false;
					if(isChoose){
						String s = companySP.getSelectedItem().toString();
						//通过名字查找到对应的id
						int companyId = 0;
						List<Company> cl = ConstantValue.companys;
						for(Company c: cl){
							if(c.name.equals(s)){
								companyId = c.id;
							}
						}
						//将这次选中的作为下次的保存起来
						ConstantValue.lastCompany = companyId;
						Company company = new Company();
						company.id = companyId;
						company.name = s;
						presentGoods.company = company;
						presentGoods.phoneNum = ""+telephoneET.getText();
						presentGoods.name = ""+nameET.getText();
						presentGoods.orderNum = ""+orderET.getText();
						presentGoods.shelfNum = ""+shelfET.getText();
						presentGoods.name = ""+nameET.getText();
						presentGoods.type = type;
						
						if( TextUtils.isEmpty(presentGoods.orderNum)){
							Toast.makeText(this, "请填写货单编号。", Toast.LENGTH_SHORT).show();
							return;
						}
						if(presentGoods.phoneNum.length()!=11){
							Toast.makeText(this, "您填写的电话号码不正确，请填写正确的电话号码。", Toast.LENGTH_SHORT).show();
							return;
						}
						if(TextUtils.isEmpty(presentGoods.shelfNum)){
							Toast.makeText(this, "请填写货柜编号。", Toast.LENGTH_SHORT).show();
							return;
						}
						if(!checkOrderNum(s,presentGoods.orderNum)){
							Toast.makeText(this, "订单号位数不正确，请查看订单号是否输入正确。", Toast.LENGTH_SHORT).show();
							return;
						}
						Log.i(TAG, "s="+s+",companyId="+companyId+",telephone="+presentGoods.phoneNum+",name="+presentGoods.name
								+",type="+type+",order="+presentGoods.orderNum+",shelf="+presentGoods.shelfNum);
						//请求线程处理多线程
						Message msg = handler.obtainMessage();
						msg.what = 0x224;
						handler.sendMessage(msg);
					}else{
						Toast.makeText(this, "请选择货单类型", Toast.LENGTH_SHORT).show();
					}
					return;
				}
				//如果没有选择类型type:则点击无效	
				if(isChoose){
					String s = companySP.getSelectedItem().toString();
					//通过名字查找到对应的id
					int companyId = 0;
					List<Company> cl = ConstantValue.companys;
					for(Company c: cl){
						if(c.name.equals(s)){
							companyId = c.id;
						}
					}
					//将这次选中的作为下次的保存起来
					ConstantValue.lastCompany = companyId;
					Company company = new Company();
					company.id = companyId;
					company.name = s;
					presentGoods.company = company;
					presentGoods.phoneNum = ""+telephoneET.getText();
					presentGoods.name = ""+nameET.getText();
					presentGoods.orderNum = ""+orderET.getText();
					presentGoods.shelfNum = ""+shelfET.getText();
					presentGoods.name = ""+nameET.getText();
					presentGoods.type = type;
					
					if( TextUtils.isEmpty(presentGoods.orderNum)){
						Toast.makeText(this, "请填写货单编号。", Toast.LENGTH_SHORT).show();
						return;
					}
					if(presentGoods.phoneNum.length()!=11){
						Toast.makeText(this, "您填写的电话号码不正确，请填写正确的电话号码。", Toast.LENGTH_SHORT).show();
						return;
					}
					if(TextUtils.isEmpty(presentGoods.shelfNum)){
						Toast.makeText(this, "请填写货柜编号。", Toast.LENGTH_SHORT).show();
						return;
					}
					if(!checkOrderNum(s,presentGoods.orderNum)){
						Toast.makeText(this, "订单号位数不正确，请查看订单号是否输入正确。", Toast.LENGTH_SHORT).show();
						return;
					}
					Log.i(TAG, "s="+s+",companyId="+companyId+",telephone="+presentGoods.phoneNum+",name="+presentGoods.name
							+",type="+type+",order="+presentGoods.orderNum+",shelf="+presentGoods.shelfNum);
					//请求线程处理多线程
					Message msg = handler.obtainMessage();
					msg.what = 0x221;
					handler.sendMessage(msg);
				}else{
					Toast.makeText(this, "请选择货单类型", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.input_newform_type_pt://普通类型
				if(!isChoose){//初始没有选
					type = 1 ;
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
			case R.id.input_newform_type_jj://加急类型
				if(!isChoose){
					type = 3 ;
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
			case R.id.input_newform_type_tc://同城类型
				if(!isChoose){
					type = 2 ;
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
			case R.id.input_newform_type_zl://滞留类型
				if(!isChoose){
					type = 4 ;
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
			case R.id.input_newform_order_bt://扫描:0代表入库的扫描，1代码取货的扫描
				Intent intent = new Intent(NewformActivity.this, CaptureActivity.class);
				intent.putExtra("type", 3);
				startActivityForResult(intent, 0x224);
				break;
			default:
				break;
		}
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			//添加货件信息
			if(msg.what==0x221){
				addGoods(presentGoods.company.id,presentGoods.orderNum,presentGoods.shelfNum); 
			}
			if(msg.what==0x222){
				//sentMessageByPlatform();
				sentMessage();
			}
			if(msg.what == 0x223){
				if(presentGoods.id!=0 && isBack==1){
					addContacts(presentGoods.id);
				}
			}
			if(msg.what == 0x224){
				updatePresentGoods();
			}
		}
		
	};
	
	/**
	 * 添加货件
	 * @param companyId
	 * @param phone
	 * @param name
	 * @param order
	 * @param shelf
	 */
	public void addGoods(final int companyId,
			final String order,final String shelf){
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				return presentgoodsDao.addGoods(presentGoods.orderNum, presentGoods.phoneNum,
						companyId, presentGoods.shelfNum, type, ConstantValue.station, presentGoods.name);
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==0){
					Toast.makeText(NewformActivity.this, "服务器忙，货件入库失败，请重新入库。", Toast.LENGTH_SHORT).show();
					canBack = true;
					return;
				}else{
					Log.i(TAG, "hid="+result);
					Toast.makeText(NewformActivity.this, "货件已成功入库。"+result, Toast.LENGTH_SHORT).show();
					//发送到handler，应请求发送短信
					presentGoods.id = result;
					Message msg = handler.obtainMessage();
					msg.what = 0x222;
					handler.sendMessage(msg);
				}
				canBack = false;
				//将除物流公司以外的信息清除
				telephoneET.setText("");
				nameET.setText("");
				orderET.setText("");
				type = 1;
				isChoose = true;
				ptTV.setBackgroundResource(R.drawable.input_newform_type_down);
				jjTV.setBackgroundResource(R.drawable.input_newform_type_up);
				tcTV.setBackgroundResource(R.drawable.input_newform_type_up);
				zlTV.setBackgroundResource(R.drawable.input_newform_type_up);
			}
			
		}.execute();
	}
	
	/**
	 * 修改货件
	 */
	public void updatePresentGoods(){
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				return presentgoodsDao.updateGoods(presentGoods.id, presentGoods.orderNum, 
						presentGoods.phoneNum, presentGoods.company.id, presentGoods.shelfNum, 
						type, ConstantValue.station, presentGoods.name);
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==0 || result ==-1){
					Toast.makeText(NewformActivity.this, "货件修改失败。", Toast.LENGTH_SHORT).show();
					canBack = true;
					return;
				}
				Log.i(TAG, "hid="+result);
				Toast.makeText(NewformActivity.this, "货件修改成功。"+result, Toast.LENGTH_SHORT).show();
				//发送到handler，应请求发送短信
				Message msg = handler.obtainMessage();
				msg.what = 0x222;
				handler.sendMessage(msg);
				canBack = false;
				//将除物流公司以外的信息清除
				telephoneET.setText("");
				nameET.setText("");
				orderET.setText("");
				type = 1;
				isChoose = true;
				ptTV.setBackgroundResource(R.drawable.input_newform_type_down);
				jjTV.setBackgroundResource(R.drawable.input_newform_type_up);
				tcTV.setBackgroundResource(R.drawable.input_newform_type_up);
				zlTV.setBackgroundResource(R.drawable.input_newform_type_up);
			}
			
		}.execute();
	}
	
	/**
	 * 调用短信平台发送短信
	 */
	public void sentMessageByPlatform(){
		Log.i(TAG,"malina:发送短信");
		//编辑短信
		final String message = presentGoods.name+"您有一件"+presentGoods.company.name+"的货件在"+
				ConstantValue.station+"站点"+ConstantValue.station_detail+"，请速来领取，取货号是"+
				presentGoods.shelfNum+"，快递单号是"+presentGoods.orderNum;
		new Thread(){

			@Override
			public void run() {
				
				try {
//					HttpPost post = new HttpPost("http://www.6610086.cn//smsComputer/smsComputersend.asp");
//					List<NameValuePair>  list=new ArrayList<NameValuePair>();
//					list.add(new BasicNameValuePair("zh", "wuliu2013"));
//					list.add(new BasicNameValuePair("mm", "123456"));
//					list.add(new BasicNameValuePair("product", "1060000009"));
//					list.add(new BasicNameValuePair("needstatus", "true"));
//					list.add(new BasicNameValuePair("hm",presentGoods.phoneNum));
//					list.add(new BasicNameValuePair("nr",message));
//					list.add(new BasicNameValuePair("dxlbid","52"));
//					list.add(new BasicNameValuePair("extno", ""));
//					HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
//					post.setEntity(entity);
//					HttpClient client = new DefaultHttpClient();
//					HttpResponse response = client.execute(post);
					
					String to = URLEncoder.encode(message, "gb2312");
					String uriAPI = "http://www.6610086.cn//smsComputer/smsComputersend.asp?zh=wuliu2013&mm=123456&hm="+
							presentGoods.phoneNum+"&nr="+to+"&dxlbid=52";
					Log.i(TAG, "uri:"+uriAPI);
					HttpGet get = new HttpGet(uriAPI);
					HttpResponse response = new DefaultHttpClient().execute(get);
					Log.i(TAG, "response="+response);
					HttpEntity respEntity = response.getEntity();
					String responseStr = EntityUtils.toString(respEntity);
					if(responseStr.equals("0")){
						Toast.makeText(NewformActivity.this, "短信发送成功。", Toast.LENGTH_SHORT).show();
						messageState = 1;
						Log.i(TAG, "中：responseStr="+responseStr+";messageState="+messageState);
						isBack = 1;
						Log.i(TAG, "后：isBack="+isBack+";messageState="+messageState);
						//发送消息到handler，应请求添加联系人
						Message hmsg = handler.obtainMessage();
						hmsg.what = 0x223;
						handler.sendMessage(hmsg);
					}else{
						Toast.makeText(NewformActivity.this, "短信发送失败。", Toast.LENGTH_SHORT).show();
						messageState = 0;
						isBack = 1;
						Log.i(TAG, "后：isBack="+isBack+";messageState="+messageState);
						//发送消息到handler，应请求添加联系人
						Message hmsg = handler.obtainMessage();
						hmsg.what = 0x223;
						handler.sendMessage(hmsg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			
		}.start();
	}
	
	/**
	 * 调用系统的发短信的功能
	 */
	public void sentMessage(){
		SmsManager smsManager = SmsManager.getDefault();
		Intent send = new Intent();
		send.setAction(ConstantValue.SMS_SEND_ACTIOIN);
		PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, send, 0);
		Intent delivery = new Intent();
		delivery.setAction(ConstantValue.SMS_DELIVERED_ACTION);
		PendingIntent deliveryIntent = PendingIntent.getBroadcast(this, 0, delivery, 0);
		//编辑短信
		String message = presentGoods.name+"您有一件"+presentGoods.company.name+"的货件在"+
				ConstantValue.station+"站点"+ConstantValue.station_detail+"，请速来领取，取货号是"+
				presentGoods.shelfNum+"，快递单号是"+presentGoods.orderNum;
		Log.i(TAG, message);
		smsManager.sendTextMessage(presentGoods.phoneNum, null, message, sentIntent, null);//deliveryIntent
	}
	
	/**
	 * 添加联系人
	 * @param hid
	 */
	public void addContacts(final int hid){
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				Log.i(TAG, "addContacts:hid="+presentGoods.id+";messageState="+messageState);
				return contactsDao.addContacts(presentGoods.id, messageState, 0, "");//messageState
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==-1){
					Toast.makeText(NewformActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					canBack = true;
					return;
				}
				if(result==0){
					Toast.makeText(NewformActivity.this, "联系人添加失败。", Toast.LENGTH_SHORT).show();
					canBack = true;
					return;
				}
				if(result==1){
					Toast.makeText(NewformActivity.this, "联系人添加成功。", Toast.LENGTH_SHORT).show();
					isBack = 0;
					canBack = true;
				}
			}
			
		}.execute();
	}
	
	/**
	 * 获取回滚的及时派货件
	 */
	public void getForms(final String order){
		new AsyncTask<Void, Void, List<PresentGoods>>(){

			@Override
			protected List<PresentGoods> doInBackground(Void... params) {
				return presentgoodsDao.getPresentGoodsByCondition(1, order);
			}

			@Override
			protected void onPostExecute(List<PresentGoods> result) {
				if(result==null || result.size()==0){
					Log.i(TAG, "没有获取到回滚的及时派货信息。。。");
				}else if(result!=null){
					presentGoods = result.get(0);
					Log.i(TAG, "回滚的货件信息："+presentGoods);
					fillForms();
				}
			}
			
		}.execute();
	}
	
	/**
	 * 填充表单
	 */
	public void fillForms(){
		int position = -1;
		Log.i(TAG, "company name="+presentGoods.company.name);
		for(int i=0;i<companyList.size();i++){
			position++;
			if(presentGoods.company.name.equals(cList.get(i))){
				break;
			}
		}
		companySP.setSelection(position);
		switch(presentGoods.type){//1:普通、2:同城、3:加急、4:遗留
			case 1:
				type =1;
				ptTV.setBackgroundResource(R.drawable.input_newform_type_down);
				jjTV.setBackgroundResource(R.drawable.input_newform_type_up);
				tcTV.setBackgroundResource(R.drawable.input_newform_type_up);
				zlTV.setBackgroundResource(R.drawable.input_newform_type_up);
				isChoose = true;
				break;
			case 2:
				type = 2 ;
				tcTV.setBackgroundResource(R.drawable.input_newform_type_down);
				ptTV.setBackgroundResource(R.drawable.input_newform_type_up);
				jjTV.setBackgroundResource(R.drawable.input_newform_type_up);
				zlTV.setBackgroundResource(R.drawable.input_newform_type_up);
				isChoose = true ;
				break;
			case 3:
				type = 3 ;
				jjTV.setBackgroundResource(R.drawable.input_newform_type_down);
				ptTV.setBackgroundResource(R.drawable.input_newform_type_up);
				tcTV.setBackgroundResource(R.drawable.input_newform_type_up);
				zlTV.setBackgroundResource(R.drawable.input_newform_type_up);
				isChoose = true ;
				break;
			case 4:
				type = 4 ;
				zlTV.setBackgroundResource(R.drawable.input_newform_type_down);
				ptTV.setBackgroundResource(R.drawable.input_newform_type_up);
				jjTV.setBackgroundResource(R.drawable.input_newform_type_up);
				tcTV.setBackgroundResource(R.drawable.input_newform_type_up);
				isChoose = true ;
				break;
		}
		orderET.setText(presentGoods.orderNum);
		telephoneET.setText(presentGoods.phoneNum);
		shelfET.setText("");
		nameET.setText(presentGoods.name);
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
						Toast.makeText(NewformActivity.this, "短信发送成功", 
		                        Toast.LENGTH_SHORT).show();
						messageState = 1;
						Log.i(TAG, "bcReceiver:messageState="+messageState);
						break;
					default:// SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						/* 发送短信失败 */
						Toast.makeText(NewformActivity.this, "短信发送失败", 
		                        Toast.LENGTH_SHORT).show();
						messageState = 0;
						Log.i(TAG, "messageState="+messageState);
						break;
				}      
			}
			catch(Exception e){
	    	  	e.getStackTrace();
			}
			canBack = false;
			isBack = 1;
			//发送消息到handler，应请求添加联系人
			Message hmsg = handler.obtainMessage();
			hmsg.what = 0x223;
			handler.sendMessage(hmsg);
	   }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==0x224){
			if(resultCode==1){
				String str = data.getStringExtra("resultStr");
				int isUseful = data.getIntExtra("isUseful", 0);
				canGetFromWeb = data.getBooleanExtra("canGetFromWeb", false);
				Log.i(TAG, "onActivityResult:resultCode = "+str+";isUseful="+isUseful+";canGetFromWeb="+canGetFromWeb);
				if(isUseful==-1){
					Toast.makeText(NewformActivity.this, "对不起，连不上服务器。", Toast.LENGTH_SHORT).show();
				}else if(isUseful==0){
					orderET.setText(str);
				}else if(isUseful==1){
					Toast.makeText(NewformActivity.this, "该订单号已存在", Toast.LENGTH_SHORT).show();
				}else if(isUseful==2){
					Toast.makeText(NewformActivity.this, "该订单号已派送。", Toast.LENGTH_SHORT).show();
				}else if(isUseful==3){
					if(canGetFromWeb){
						getForms(str);
					}else{
						Toast.makeText(NewformActivity.this, "该订单号已遣返。", Toast.LENGTH_SHORT).show();
					}
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==event.KEYCODE_BACK){
			if(canBack){
				Log.i("test", "可以点击返回键。");
			}else{
				Log.i("test", "不可点击返回键。");
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 检测订单号的位数
	 * @param companyName
	 * @param orderNum
	 * @return
	 */
	public boolean checkOrderNum(String companyName,String orderNum){
		if("韵达快递".equals(companyName)){
			if(orderNum.length()!=13){
				return false;
			}
		}
		if("天天快递".equals(companyName)){
			if(orderNum.length()!=12){
				return false;
			}
		}
		if("快捷快递".equals(companyName)){//12、13
			if(orderNum.length()<12){
				return false;
			}
		}
		if("汇通快递".equals(companyName)){//12、13、14
			if(orderNum.length()<12){
				return false;
			}
		}
		if("圆通快递".equals(companyName)){
			if(orderNum.length()!=10){
				return false;
			}
		}
		if("顺丰快递".equals(companyName)){
			if(orderNum.length()!=12){
				return false;
			}
		}
		if("宅急送".equals(companyName)){
			if(orderNum.length()!=10){
				return false;
			}
		}
		if("EMS".equals(companyName)){
			if(orderNum.length()!=13){
				return false;
			}
		}
		return true;
	}


}
