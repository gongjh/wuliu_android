package cn.megasound.youthbegan.wuliumanager.view;

import java.util.ArrayList;
import java.util.List;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.adapter.LookforGoodsAdapter;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.dao.PresentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.ReturnGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.SentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.PresentGoodsDaoImpl;
import cn.megasound.youthbegan.wuliumanager.dao.impl.ReturnGoodsDaoImpl;
import cn.megasound.youthbegan.wuliumanager.dao.impl.SentGoodsDaoImpl;
import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;
import cn.megasound.youthbegan.wuliumanager.entity.ReturnGoods;
import cn.megasound.youthbegan.wuliumanager.entity.SentGoods;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LookforDetailActivity extends BaseActivity implements
		OnClickListener {
	
	private static final String TAG = "LookforDetailActivity";
	private static final int SENT = 1 ;
	private static final int REMAIN = 2;
	private static final int BACK = 3;
	private static final int WAIT = 4;
	
	private TextView  companyTV,timeTV,infoTV;
	private Button sentBT,remainBT,backBT,waitBT; 
	private ListView lookforLV;
	private LookforGoodsAdapter lAdapter;
	private int type = 0;
	private int nowPageSent = 1, nowPageRemain = 1, nowPageBack = 1, nowPageWait = 1;
	private boolean isCanLoadMore = false, isLoading = false;
	private Intent intent ;
	
	private int companyId=0, remainCount=0, sentCount=0, backCount=0,waitCount=0, totalCount=0;
	private String companyStr="", firstStr="2013.1.1", endStr="2013.1.1";
	
	private PresentGoodsDao remainDao = new PresentGoodsDaoImpl();
	private SentGoodsDao sentDao = new SentGoodsDaoImpl();
	private ReturnGoodsDao backDao = new ReturnGoodsDaoImpl();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		companyId = intent.getIntExtra("companyId", 0);
		companyStr = intent.getStringExtra("companyName");
		firstStr = intent.getStringExtra("first");
		endStr = intent.getStringExtra("end");
		//Log.i(TAG, "companyName="+companyStr+";firstStr="+firstStr+";endStr="+endStr);
		//获取滞留数量、已送出数量、遣返数量、待发送数
		getRemainCount(companyId, firstStr, endStr);
		getSentCount(companyId, firstStr, endStr);
		getBackCount(companyId, firstStr, endStr);
		getWaitCount(companyId, firstStr, endStr);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.lookfor_detail);
	}
	
	@Override
	protected void findViewById() {
		companyTV = (TextView)findViewById(R.id.lookfor_detail_company_top_left_tv);
		timeTV = (TextView)findViewById(R.id.lookfor_detail_time_tv);
		infoTV = (TextView)findViewById(R.id.lookfor_detail_info_tv);
		sentBT = (Button)findViewById(R.id.lookfor_detail_sent_bt);
		remainBT = (Button)findViewById(R.id.lookfor_detail_remain_bt);
		backBT = (Button)findViewById(R.id.lookfor_detail_back_bt);
		waitBT = (Button)findViewById(R.id.lookfor_detail_wait_bt);
		lookforLV = (ListView)findViewById(R.id.lookfor_detail_lv);
	}

	@Override
	protected void init() {
		lAdapter = new LookforGoodsAdapter(REMAIN, LookforDetailActivity.this, getApplicationContext(), 
				new ArrayList<PresentGoods>(), new ArrayList<SentGoods>(), 
				new ArrayList<ReturnGoods>(), new ArrayList<PresentGoods>(),handler);
		lookforLV.setDividerHeight(0);
		type = REMAIN;
	}

	@Override
	protected void setListener() {
		remainBT.setOnClickListener(this);
		sentBT.setOnClickListener(this);
		backBT.setOnClickListener(this);
		waitBT.setOnClickListener(this);
		
		//设置滚动监听
		lookforLV.setOnScrollListener(new MyScrollListener());
		lookforLV.setAdapter(lAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//Log.i(TAG, "init，公司名："+companyStr);
		companyTV.setText(companyStr);
		timeTV.setText(firstStr+"——"+endStr);
		setInfoText();
		
		//默认用滞留填充
		nowPageSent = 1;
		nowPageRemain = 1;
		nowPageBack = 1;
		nowPageWait = 1;
		for(int i=0;i<lAdapter.sentGoods.size();i++){
			lAdapter.sentGoods.remove(i);
		}
		for(int i=0;i<lAdapter.presentGoods.size();i++){
			lAdapter.presentGoods.remove(i);
		}
		for(int i=0;i<lAdapter.returnGoods.size();i++){
			lAdapter.returnGoods.remove(i);
		}
		for(int i=0;i<lAdapter.waitGoods.size();i++){
			lAdapter.waitGoods.remove(i);
		}
		switch(type){
			case SENT:
				type = SENT;
				lAdapter.setType(SENT);
				sentBT.setBackgroundResource(R.drawable.lookfor_detail_sent_down);
				remainBT.setBackgroundResource(R.drawable.lookfor_detail_remain_up);
				backBT.setBackgroundResource(R.drawable.lookfor_detail_back_up);
				waitBT.setBackgroundResource(R.drawable.lookfor_detail_wait_up);
				fillLVSent(nowPageSent);
				break;
			case BACK:
				type = BACK;
				lAdapter.setType(BACK);
				sentBT.setBackgroundResource(R.drawable.lookfor_detail_sent_up);
				remainBT.setBackgroundResource(R.drawable.lookfor_detail_remain_up);
				backBT.setBackgroundResource(R.drawable.lookfor_detail_back_down);
				waitBT.setBackgroundResource(R.drawable.lookfor_detail_wait_up);
				fillLVBack(nowPageBack);
				break;
			case WAIT:
				type = WAIT;
				lAdapter.setType(WAIT);
				sentBT.setBackgroundResource(R.drawable.lookfor_detail_sent_up);
				remainBT.setBackgroundResource(R.drawable.lookfor_detail_remain_up);
				backBT.setBackgroundResource(R.drawable.lookfor_detail_back_up);
				waitBT.setBackgroundResource(R.drawable.lookfor_detail_wait_down);
				fillLVWait(nowPageWait);
				break;
			case REMAIN:
			default:
				type = REMAIN;
				lAdapter.setType(REMAIN);
				sentBT.setBackgroundResource(R.drawable.lookfor_detail_sent_up);
				remainBT.setBackgroundResource(R.drawable.lookfor_detail_remain_down);
				backBT.setBackgroundResource(R.drawable.lookfor_detail_back_up);
				waitBT.setBackgroundResource(R.drawable.lookfor_detail_wait_up);
				fillLVRemain(nowPageRemain);
				break;
		}
	}
	
	Handler handler  = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x410){
				onResume();
			}
		}
		
	};

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.lookfor_detail_sent_bt://已派送
				sentBT.setBackgroundResource(R.drawable.lookfor_detail_sent_down);
				remainBT.setBackgroundResource(R.drawable.lookfor_detail_remain_up);
				backBT.setBackgroundResource(R.drawable.lookfor_detail_back_up);
				waitBT.setBackgroundResource(R.drawable.lookfor_detail_wait_up);
				type = SENT;
				lAdapter.setType(SENT);
				nowPageSent = 1;
				fillLVSent(nowPageSent);
				break;
			case R.id.lookfor_detail_remain_bt://滞留
				sentBT.setBackgroundResource(R.drawable.lookfor_detail_sent_up);
				remainBT.setBackgroundResource(R.drawable.lookfor_detail_remain_down);
				backBT.setBackgroundResource(R.drawable.lookfor_detail_back_up);
				waitBT.setBackgroundResource(R.drawable.lookfor_detail_wait_up);
				type = REMAIN;
				lAdapter.setType(REMAIN);
				nowPageRemain = 1;
				fillLVRemain(nowPageRemain);
				break;
			case R.id.lookfor_detail_back_bt://已遣返
				sentBT.setBackgroundResource(R.drawable.lookfor_detail_sent_up);
				remainBT.setBackgroundResource(R.drawable.lookfor_detail_remain_up);
				backBT.setBackgroundResource(R.drawable.lookfor_detail_back_down);
				waitBT.setBackgroundResource(R.drawable.lookfor_detail_wait_up);
				type = BACK;
				lAdapter.setType(BACK);
				nowPageBack = 1;
				fillLVBack(nowPageBack);
				break;
			case R.id.lookfor_detail_wait_bt://待发送
				sentBT.setBackgroundResource(R.drawable.lookfor_detail_sent_up);
				remainBT.setBackgroundResource(R.drawable.lookfor_detail_remain_up);
				backBT.setBackgroundResource(R.drawable.lookfor_detail_back_up);
				waitBT.setBackgroundResource(R.drawable.lookfor_detail_wait_down);
				type = WAIT;
				lAdapter.setType(WAIT);
				nowPageWait = 1;
				fillLVWait(nowPageWait);
				break;
			default:
				break;
		}
	}
	
	/**
	 * 用送出  列表  填充ListView
	 */
	public void fillLVSent(final int page){
		new AsyncTask<Void, Void, List<SentGoods>>(){

			@Override
			protected List<SentGoods> doInBackground(Void... params) {
				isLoading = true;
				Log.i(TAG, "page="+page+";companyId="+companyId+";first="+firstStr+";end="+endStr);
				return sentDao.getSentGoodsList(page, companyId, firstStr, endStr);
			}

			@Override
			protected void onPostExecute(List<SentGoods> result) {
				if(result==null){
					Toast.makeText(LookforDetailActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return ;
				}
				//判断是否有更多加载的
				if(isCanLoadMore){
					for(SentGoods s:result){
						lAdapter.addItem(SENT,null,s,null,null);
					}
				}else{
					lAdapter.setSentGoods(result);
				}
				
				if(result.size()==0){
					Toast.makeText(getApplicationContext(), "暂时没有该条件的数据。", Toast.LENGTH_SHORT).show();
					lAdapter.setSentGoods(result);
				}
				
				//判断是否可继续加载，每页固定15条
				if(result.size()<15){
					isCanLoadMore = false;
				}else if(result.size()==15 && (nowPageSent==ConstantValue.LOOKFOR_SENT_TOTAL/15) && (ConstantValue.LOOKFOR_SENT_TOTAL%15==0)){
					isCanLoadMore = false;
				}
				else{
					isCanLoadMore = true;
				}
				lAdapter.notifyDataSetChanged();
				nowPageSent++;
				isLoading = false;
			}
			
		}.execute();
	}
	
	/**
	 * 用  滞留列表  填充ListView
	 */
	public void fillLVRemain(final int page){
		new AsyncTask<Void, Void, List<PresentGoods>>(){

			@Override
			protected List<PresentGoods> doInBackground(Void... params) {
				isLoading = true;
				Log.i(TAG, "page="+page+";companyId="+companyId+";first="+firstStr+";end="+endStr+";type=4");
				return remainDao.getPresentGoodsList(page, companyId, firstStr, endStr, 4);
			}

			@Override
			protected void onPostExecute(List<PresentGoods> result) {
				if(result==null){
					Toast.makeText(LookforDetailActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return ;
				}
				//判断是否有更多加载的
				if(isCanLoadMore){
					for(PresentGoods p:result){
						lAdapter.addItem(REMAIN,p,null,null,null);
					}
				}else{
					lAdapter.setPresentGoods(result);
				}
				
				
				if(result.size()==0){
					Toast.makeText(getApplicationContext(), "暂时没有该条件的数据。", Toast.LENGTH_SHORT).show();
					lAdapter.setPresentGoods(result);
				}
				
				//判断是否可继续加载，每页固定15条
				if(result.size()<15){
					isCanLoadMore = false;
				}else if(result.size()==15 && (nowPageRemain==ConstantValue.LOOKFOR_REMAIN_TOTAL/15) && (ConstantValue.LOOKFOR_REMAIN_TOTAL%15==0)){
					isCanLoadMore = false;
				}
				else{
					isCanLoadMore = true;
				}
				lAdapter.notifyDataSetChanged();
				nowPageRemain++;
				isLoading = false;
			}
			
		}.execute();
	}

	
	/**
	 * 用送出  列表  填充ListView
	 */
	public void fillLVBack(final int page){
		new AsyncTask<Void, Void, List<ReturnGoods>>(){

			@Override
			protected List<ReturnGoods> doInBackground(Void... params) {
				isLoading = true;
				Log.i(TAG, "page="+page+";companyId="+companyId+";first="+firstStr+";end="+endStr);
				return backDao.getReturnGoodsList(page, companyId, firstStr, endStr);
			}

			@Override
			protected void onPostExecute(List<ReturnGoods> result) {
				if(result==null){
					Toast.makeText(LookforDetailActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return ;
				}
				//判断是否有更多加载的
				if(isCanLoadMore){
					for(ReturnGoods r:result){
						lAdapter.addItem(BACK,null,null,r,null);
					}
				}else{
					lAdapter.setReturnGoods(result);
				}
				
				if(result.size()==0){
					Toast.makeText(getApplicationContext(), "暂时没有该条件的数据。", Toast.LENGTH_SHORT).show();
					lAdapter.setReturnGoods(result);
				}
				
				//判断是否可继续加载，每页固定15条
				if(result.size()<15){
					isCanLoadMore = false;
				}else if(result.size()==15 && (nowPageBack==ConstantValue.LOOKFOR_BACK_TOTAL/15) && (ConstantValue.LOOKFOR_BACK_TOTAL%15==0)){
					isCanLoadMore = false;
				}
				else{
					isCanLoadMore = true;
				}
				lAdapter.notifyDataSetChanged();
				nowPageBack++;
				isLoading = false;
			}
			
		}.execute();
	}
	
	/**
	 * 获取滞留件数
	 * @param companyId
	 * @param firstDate
	 * @param endDate
	 */
	public void getRemainCount(final int companyId,final String firstDate,final String endDate){
		new AsyncTask<Void, Void, Integer>() {

			@Override
			protected Integer doInBackground(Void... params) {
				return remainDao.getCount(companyId, firstDate, endDate, 4);
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==-1){
					Toast.makeText(LookforDetailActivity.this, "服务器忙，请稍后。",
							Toast.LENGTH_SHORT).show();
					return;
				}
				remainCount = result;
				Log.i(TAG, "remainCount="+remainCount);
				setInfoText();
			}
			
		}.execute();
	}
	
	/**
	 * 用  待发送列表  填充ListView
	 */
	public void fillLVWait(final int page){
		new AsyncTask<Void, Void, List<PresentGoods>>(){

			@Override
			protected List<PresentGoods> doInBackground(Void... params) {
				isLoading = true;
				Log.i(TAG, "page="+page+";companyId="+companyId+";first="+firstStr+";end="+endStr+";type=4");
				return remainDao.getPresentGoodsList(page, companyId, firstStr, endStr, 1);
			}

			@Override
			protected void onPostExecute(List<PresentGoods> result) {
				if(result==null){
					Toast.makeText(LookforDetailActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return ;
				}
				//判断是否有更多加载的
				if(isCanLoadMore){
					for(PresentGoods w:result){
						lAdapter.addItem(WAIT,null,null,null,w);
					}
				}else{
					lAdapter.setWaitGoods(result);
				}
				
				if(result.size()==0){
					Toast.makeText(getApplicationContext(), "暂时没有该条件的数据。", Toast.LENGTH_SHORT).show();
					lAdapter.setWaitGoods(result);
				}
				
				//判断是否可继续加载，每页固定15条
				if(result.size()<15){
					isCanLoadMore = false;
				}else if(result.size()==15 && (nowPageWait==ConstantValue.LOOKFOR_REMAIN_TOTAL/15) && (ConstantValue.LOOKFOR_REMAIN_TOTAL%15==0)){
					isCanLoadMore = false;
				}
				else{
					isCanLoadMore = true;
				}
				lAdapter.notifyDataSetChanged();
				nowPageWait++;
				isLoading = false;
			}
			
		}.execute();
	}
	
	/**
	 * 获取待发送数
	 * @param companyId
	 * @param firstDate
	 * @param endDate
	 */
	public void getWaitCount(final int companyId,final String firstDate,final String endDate){
		new AsyncTask<Void, Void, Integer>() {

			@Override
			protected Integer doInBackground(Void... params) {
				return remainDao.getCount(companyId, firstDate, endDate, 1);
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==-1){
					Toast.makeText(LookforDetailActivity.this, "服务器忙，请稍后。",
							Toast.LENGTH_SHORT).show();
					return;
				}
				waitCount = result;
				Log.i(TAG, "waitCount="+waitCount);
				setInfoText();
			}
			
		}.execute();
	}
	
	/**
	 * 获取已送出数量
	 * @param companyId
	 * @param firstDate
	 * @param endDate
	 */
	public void getSentCount(final int companyId,final String firstDate,final String endDate){
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				return sentDao.getCount(companyId, firstDate, endDate);
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==-1){
					Toast.makeText(LookforDetailActivity.this, "服务器忙，请稍后。",
							Toast.LENGTH_SHORT).show();
					return;
				}
				sentCount = result;
				Log.i(TAG, "sentCount="+sentCount);
				setInfoText();
			}
			
		}.execute();
	}
	
	/**
	 * 获取遣返数量
	 * @param companyId
	 * @param firstDate
	 * @param endDate
	 */
	public void getBackCount(final int companyId,final String firstDate,final String endDate){
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				return backDao.getCount(companyId, firstDate, endDate);
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==-1){
					Toast.makeText(LookforDetailActivity.this, "服务器忙，请稍后。",
							Toast.LENGTH_SHORT).show();
					return;
				}
				backCount = result;
				Log.i(TAG, "sentCount="+backCount);
				setInfoText();
			}
			
		}.execute();
	}
	
	/**
	 * 设置信息文本框
	 */
	public void setInfoText(){
		totalCount = remainCount+sentCount+backCount+waitCount;
		infoTV.setText("共收件"+totalCount+"件，已派送"+sentCount+"件，滞留"+remainCount+"件，遣返"+backCount+"件，待发送"+waitCount+"件");
		Log.i(TAG, "totalCount="+totalCount+";sentCount="+sentCount+";remainCount="+remainCount+";waitCount="+waitCount);
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
					case SENT:
						if (isCanLoadMore && !isLoading) {
							isLoading = true;
							fillLVSent(nowPageSent);
						}
						break;
					case REMAIN:
						if (isCanLoadMore && !isLoading) {
							isLoading = true;
							fillLVRemain(nowPageRemain);
						}
						break;
					case BACK:
						if (isCanLoadMore && !isLoading) {
							isLoading = true;
							fillLVBack(nowPageBack);
						}
						break;
					case WAIT:
						if (isCanLoadMore && !isLoading) {
							isLoading = true;
							fillLVWait(nowPageWait);
						}
						break;
				}
			}
		}
	}

}
