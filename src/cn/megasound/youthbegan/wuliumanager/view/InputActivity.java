package cn.megasound.youthbegan.wuliumanager.view;

import java.util.ArrayList;
import java.util.List;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.adapter.GoodsAdapter;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.dao.PresentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.PresentGoodsDaoImpl;
import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class InputActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "InputActivity";
	private static final int ALL = 1;
	private static final int CONDITION = 2;
	
	private EditText searchET ;
	private Button searchBT, newformBT, newformBTn, scanBT;
	private ListView newformLV;
	private GoodsAdapter gAdapter;
	
	private PresentGoodsDao presentDao ;
	private String searchStr = "";
	private int nowPageAll = 1, nowPageCondition = 1;
	private boolean isCanLoadMore = false, isLoading = false;
	private Intent intent ;
	
	private String lvType = "all";//all  ,  condition

	@Override
	protected void setContentView() {
		//更改软件盘的位置
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.input_home);
	}

	@Override
	protected void findViewById() {
		searchET = (EditText)findViewById(R.id.input_home_search_et);
		searchBT = (Button)findViewById(R.id.input_home_search_bt);
		newformBT = (Button)findViewById(R.id.input_home_newform_bt);
		newformBTn = (Button)findViewById(R.id.input_home_newform_btn);
		scanBT = (Button)findViewById(R.id.input_home_scan_bt);
		newformLV = (ListView)findViewById(R.id.input_home_lv);
	}

	@Override
	protected void init() {
		gAdapter = new GoodsAdapter(ALL, InputActivity.this,getApplicationContext(), new ArrayList<PresentGoods>(),
				new ArrayList<PresentGoods>(),handler);
		newformLV.setDividerHeight(0);
		presentDao = new PresentGoodsDaoImpl();

	}
	
	PresentGoods pAll ;
	PresentGoods pCondition;
	@Override
	protected void setListener() {
		searchBT.setOnClickListener(this);
		newformBT.setOnClickListener(this);
		newformBTn.setOnClickListener(this);
		scanBT.setOnClickListener(this);
		//设置滚动监听
		newformLV.setOnScrollListener(new MyScrollListener());
		newformLV.setAdapter(gAdapter);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "refresh_onResume");
		//在这里填充ListView
		lvType = "all";
		gAdapter.setType(ALL);
		searchET.setText("");
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
			if(msg.what==0x202){
				onResume();
			}
		}
		
	};

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.input_home_search_bt://搜索按钮
				searchStr = ""+searchET.getText();
				if(TextUtils.isEmpty(searchStr)){
					Toast.makeText(InputActivity.this, "查询条件不能为空。", Toast.LENGTH_SHORT).show();
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
			case R.id.input_home_newform_bt://新建表单
				intent = new Intent(InputActivity.this, NewformActivity.class);
				startActivity(intent);
				break;
			case R.id.input_home_newform_btn://新建表单
				intent = new Intent(InputActivity.this, NewformActivity.class);
				startActivity(intent);
				break;
			case R.id.input_home_scan_bt://扫描按钮:0代表入库的扫描，1代码取货的扫描
				intent = new Intent(InputActivity.this, CaptureActivity.class);
				intent.putExtra("type", 1);
				startActivityForResult(intent, 0x201);
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
					Toast.makeText(InputActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
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
				
				//判断是否可继续加载，每页固定15条，如果是最后一页，并且最后一页有15条记录
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
					Toast.makeText(InputActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(InputActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return ;
				}
				if(result.size()==0){
					Toast.makeText(getApplicationContext(), "暂时没有该条件的数据。", Toast.LENGTH_SHORT).show();
					return;
				}
				PresentGoods presentGoods = result.get(0);
				intent = new Intent(InputActivity.this,UpdateActivity.class);
				Bundle data = new Bundle();
				data.putSerializable("presentGoods", presentGoods);
				intent.putExtras(data);
				startActivity(intent);
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

				if (isCanLoadMore && !isLoading) {
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
		if(requestCode==0x201){
			if(resultCode==1){
				String str = data.getStringExtra("resultStr");
				int isUseful = data.getIntExtra("isUseful", 0);
				Log.i("test", "201:resultCode = "+str+";isUseful="+isUseful);
				if(isUseful==-1){
					Toast.makeText(InputActivity.this, "对不起，连不上服务器。", Toast.LENGTH_SHORT).show();
				}else if(isUseful==0){
					Toast.makeText(InputActivity.this, "该订单号不存在", Toast.LENGTH_SHORT).show();
				}else if(isUseful==1){
					//跳到详情页面
					findByCondition(str);
				}else if(isUseful==2){
					Toast.makeText(InputActivity.this, "该订单号已派送。", Toast.LENGTH_SHORT).show();
				}else if(isUseful==3){
					Toast.makeText(InputActivity.this, "该订单号已遣返。", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	
}