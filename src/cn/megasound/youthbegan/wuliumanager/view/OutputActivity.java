package cn.megasound.youthbegan.wuliumanager.view;

import java.util.ArrayList;
import java.util.List;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.adapter.OutputGoodsAdapter;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.dao.CompanyDao;
import cn.megasound.youthbegan.wuliumanager.dao.PresentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.CompanyDaoImpl;
import cn.megasound.youthbegan.wuliumanager.dao.impl.PresentGoodsDaoImpl;
import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 取货的首页
 * @author Administrator
 *
 */
public class OutputActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "OutputActivity";
	private static final int ALL = 1;
	private static final int CONDITION = 2;
	
	private EditText searchET ;
	private Button searchBT, sentBT, returnBT;
	private ListView goodsLV;
	
	private PresentGoodsDao presentDao ;
	public CompanyDao companyDao ;
	private PresentGoods presentGoods;
	List<String> actionList;
	ListAdapter actionAdapter;
	
	private int nowPageAll = 1, nowPageCondition = 1;
	private String searchStr ="";
	private boolean isCanLoadMore = false, isLoading = false;
	private OutputGoodsAdapter gAdapter;
	
	private String lvType = "all";//all  ,  condition
	private Intent intent;
	private int signType=2;//1:表示点击退件按钮；2:表示点击取件按钮

	@Override
	protected void setContentView() {
		//更改软件盘的位置
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.output_home);
	}

	@Override
	protected void findViewById() {
		searchET = (EditText)findViewById(R.id.output_home_search_et);
		searchBT = (Button)findViewById(R.id.output_home_search_bt);
		returnBT = (Button)findViewById(R.id.output_home_return_bt);
		sentBT = (Button)findViewById(R.id.output_home_sent_bt);
		goodsLV = (ListView)findViewById(R.id.output_lv);
	}
	
	@Override
	protected void init() {
		gAdapter = new OutputGoodsAdapter(ALL, OutputActivity.this, getApplicationContext(), new ArrayList<PresentGoods>(),
				new ArrayList<PresentGoods>(),handler);
		goodsLV.setDividerHeight(0);
		presentDao = new PresentGoodsDaoImpl();
		//初始化快递公司列表
		companyDao = new CompanyDaoImpl();
		actionList = new ArrayList<String>();
		actionList.add("取件");
		actionList.add("退件");
	}
	
	@Override
	protected void setListener() {
		searchBT.setOnClickListener(this);
		returnBT.setOnClickListener(this);
		sentBT.setOnClickListener(this);
		goodsLV.setOnScrollListener(new MyScrollListener());
		goodsLV.setAdapter(gAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		searchET.setText("");
		//在这里填充ListView
		lvType = "all";
		gAdapter.setType(ALL);
		nowPageAll = 1;
		nowPageCondition = 1;
		for(int i=0;i<gAdapter.presentGoodsAll.size();i++){
			gAdapter.presentGoodsAll.remove(i);
		}
		for(int i=0;i<gAdapter.presentGoodsCondition.size();i++){
			gAdapter.presentGoodsCondition.remove(i);
		}
		fillLV(nowPageAll);
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			//从adapter那传来的刷新的操作
			if(msg.what==0x301){
				onResume();
			}
			//从adapter传来的点击操作
			if(msg.what == 0x302){
				Bundle data = msg.getData();
				presentGoods = (PresentGoods) data.getSerializable("presentGoods");
				Log.i(TAG, "presentGoods info:"+presentGoods);
				actionAdapter = new ArrayAdapter(OutputActivity.this, 
						R.layout.contact_menu, R.id.contact_menu_tv, actionList);
				new AlertDialog.Builder(OutputActivity.this)
				.setAdapter(actionAdapter, 
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Log.i(TAG, "choose:"+which);
							switch(which){
								case 0://取件
									intent = new Intent(OutputActivity.this, SentSignatureActivity.class);
									Bundle extras = new Bundle();
									extras.putInt("signType",1);
									extras.putSerializable("presentGoods", presentGoods);
									intent.putExtras(extras);
									startActivity(intent);
									break;
								case 1://退件
									intent = new Intent(OutputActivity.this, ReturnSignatureActivity.class);
									Bundle e = new Bundle();
									e.putInt("signType",1);
									e.putSerializable("presentGoods", presentGoods);
									intent.putExtras(e);
									startActivity(intent);
									break;
							}
						}
						
					}).show();
				}
		}
		
	};

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.output_home_search_bt://搜索
				searchStr = ""+searchET.getText();
				if(TextUtils.isEmpty(searchStr)){
					Toast.makeText(OutputActivity.this, "查询条件不能为空。", Toast.LENGTH_SHORT).show();
					return;
				}
				lvType = "condition";
				gAdapter.setType(CONDITION);
				nowPageCondition = 1;
				fillLVByCondition(nowPageCondition,searchStr);
				//将软键盘隐藏
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
				break;
			case R.id.output_home_return_bt://遣返扫描：
				signType = 1;
				intent = new Intent(OutputActivity.this, CaptureActivity.class);
				intent.putExtra("type", 5);
				startActivity(intent);
				//startActivityForResult(intent, 0x303);
				break;
			case R.id.output_home_sent_bt://取货扫描:0代表入库的扫描，1代码取货的扫描
				signType = 2;
				intent = new Intent(OutputActivity.this, CaptureActivity.class);
				intent.putExtra("type", 4);
				startActivity(intent);
				//startActivityForResult(intent, 0x302);
				break;
			default:
				break;
		}
	}
	
	/**
	 * 填充ListView
	 */
	public void fillLV(final int page){
		new AsyncTask<Void, Void, List<PresentGoods>>(){

			@Override
			protected List<PresentGoods> doInBackground(Void... params) {
				isLoading = true;
				return presentDao.getPresentGoods(page);
			}

			@Override
			protected void onPostExecute(List<PresentGoods> result) {
				if(result==null){
					Toast.makeText(OutputActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return ;
				}
				//判断是否有更多加载的
				if(isCanLoadMore){
					for(PresentGoods p:result){
						gAdapter.addItem(ALL, p, null);
					}
				}else{
					gAdapter.setPresentGoodsAll(result);
				}
				
				if(result.size()==0){
					Toast.makeText(getApplicationContext(), "暂时没有该条件的数据。", Toast.LENGTH_SHORT).show();
					gAdapter.setPresentGoodsAll(result);
				}
				
				//判断是否可继续加载，每页固定15条
				if(result.size()<15){
					isCanLoadMore = false;
				}else if(result.size()==15 && (nowPageAll==ConstantValue.PRESENT_TOTAL/15) && (ConstantValue.PRESENT_TOTAL%15==0)){
					isCanLoadMore = false;
				}
				else{
					isCanLoadMore = true;
				}
				gAdapter.notifyDataSetChanged();
				nowPageAll++;
				isLoading = false;
			}
			
		}.execute();
	}
	
	/**
	 * 通过条件填充
	 * @param page
	 * @param phoneNum
	 */
	public void fillLVByCondition(final int page,final String phoneNum){
		new AsyncTask<Void, Void, List<PresentGoods>>(){

			@Override
			protected List<PresentGoods> doInBackground(Void... params) {
				isLoading = true;
				return presentDao.getPresentGoodsByCondition(page, phoneNum);
			}

			@Override
			protected void onPostExecute(List<PresentGoods> result) {
				if(result==null){
					Toast.makeText(OutputActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return ;
				}
				//判断是否有更多加载的
				if(isCanLoadMore){
					for(PresentGoods p:result){
						gAdapter.addItem(CONDITION, null, p);
					}
				}else{
					gAdapter.setPresentGoodsCondition(result);
				}
				
				if(result.size()==0){
					Toast.makeText(getApplicationContext(), "暂时没有该条件的数据。", Toast.LENGTH_SHORT).show();
					gAdapter.setPresentGoodsCondition(result);
				}
				
				//判断是否可继续加载，每页固定15条
				if(result.size()<15){
					isCanLoadMore = false;
				}else if(result.size()==15 && (nowPageCondition==ConstantValue.PRESENT_CONDITION_TOTAL/15) && (ConstantValue.PRESENT_CONDITION_TOTAL%15==0)){
					isCanLoadMore = false;
				}
				else{
					isCanLoadMore = true;
				}
				gAdapter.notifyDataSetChanged();
				nowPageCondition++;
				isLoading = false;
			}
			
		}.execute();
	}
	
	/**
	 * 通过扫描查找并跳转
	 * @param page
	 * @param phoneNum
	 */
	public void findByCondition(final String orderNum){
		new AsyncTask<Void, Void, List<PresentGoods>>(){

			@Override
			protected List<PresentGoods> doInBackground(Void... params) {
				isLoading = true;
				return presentDao.getPresentGoodsByCondition(1, orderNum);
			}

			@Override
			protected void onPostExecute(List<PresentGoods> result) {
				if(result==null){
					Toast.makeText(OutputActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return ;
				}
				if(result.size()==0){
					Toast.makeText(getApplicationContext(), "暂时没有该条件的数据。", Toast.LENGTH_SHORT).show();
					return;
				}
				PresentGoods presentGoods = result.get(0);
				Intent i = null;
				if(signType==1){
					i = new Intent(OutputActivity.this,ReturnSignatureActivity.class);
				}else if(signType==2){
					i = new Intent(OutputActivity.this,SentSignatureActivity.class);
				}
				Bundle data = new Bundle();
				data.putSerializable("presentGoods", presentGoods);
				i.putExtras(data);
				startActivity(i);
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

				if (isCanLoadMore && !isLoading ) {
					if("all".equals(lvType)){
						isLoading = true;
						fillLV(nowPageAll);
					}else if("condition".equals(lvType)){
						isLoading = true;
						fillLVByCondition(nowPageCondition, searchStr);
					}
				}
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
			case 0x302:
			case 0x303:
				if(resultCode==1){
					String str = data.getStringExtra("resultStr");
					int isUseful = data.getIntExtra("isUseful", 0);
					Log.i("test", "303:resultCode = "+str+";isUseful="+isUseful);
					if(isUseful==0){
						Toast.makeText(OutputActivity.this, "该订单号不存在。", Toast.LENGTH_SHORT).show();
					}else if(isUseful==1){
						//跳到取件、退件页面
						findByCondition(str);
					}else if(isUseful==2){
						Toast.makeText(OutputActivity.this, "该订单号已派送。", Toast.LENGTH_SHORT).show();
					}else if(isUseful==3){
						Toast.makeText(OutputActivity.this, "该订单号已遣返。", Toast.LENGTH_SHORT).show();
					}
				}
		}
	}

}
