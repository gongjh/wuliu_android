package cn.megasound.youthbegan.wuliumanager.adapter;

import java.util.List;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.dao.PresentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.PresentGoodsDaoImpl;
import cn.megasound.youthbegan.wuliumanager.entity.Company;
import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;
import cn.megasound.youthbegan.wuliumanager.view.UpdateActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GoodsAdapter extends BaseAdapter {
	
	private int type;
	private Activity mActivity;
	private Context context;
	public List<PresentGoods> presentGoodsAll;
	public List<PresentGoods> presentGoodsCondition;
	private LayoutInflater mInflater = null;
	private float x,ux;
	private TextView curOrder_tv;
	private Button curDel_btn;
	
	private PresentGoodsDao pDao ;
	PresentGoods pAll ;
	PresentGoods pCondition;
	int pid;
	
	private Handler handler;
	
	public void setType(int type) {
		this.type = type;
	}

	public void setPresentGoodsAll(List<PresentGoods> presentGoodsAll) {
		this.presentGoodsAll = presentGoodsAll;
	}

	public void setPresentGoodsCondition(List<PresentGoods> presentGoodsCondition) {
		this.presentGoodsCondition = presentGoodsCondition;
	}

	public GoodsAdapter(int type,Activity mActivity, Context context, List<PresentGoods> goodsListAll, 
			List<PresentGoods> goodsListCondition,Handler handler) {
		super();
		this.type = type;
		this.mActivity = mActivity;
		this.context = context;
		this.presentGoodsAll = goodsListAll;
		this.presentGoodsCondition = goodsListCondition;
		this.handler=handler;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		pDao = new PresentGoodsDaoImpl();
	}

	@Override
	public int getCount() {
		switch(type){
			case 1:
				return presentGoodsAll.size();
			case 2:
				return presentGoodsCondition.size();
		}
		return 0;
	}
	
	public void addItem(int type, PresentGoods all, PresentGoods condition){
		switch(type){
			case 1:
				presentGoodsAll.add(all);
				break;
			case 2:
				presentGoodsCondition.add(condition);
				break;
		}
	}
	
	@Override
	public Object getItem(int position) {
		switch(type){
			case 1:
				return presentGoodsAll.get(position);
			case 2:
				return presentGoodsCondition.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		switch(type){
			case 1:
				return presentGoodsAll.get(position).id;
			case 2:
				return presentGoodsCondition.get(position).id;
		}
		return 0;
	}

	ViewHolder holder;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,110);
			convertView = mInflater.inflate(R.layout.input_home_lv_item, null);
			convertView.setLayoutParams(lp);
			holder = new ViewHolder();
			holder.companyTV = (TextView)convertView.findViewById(R.id.input_home_lv_item_left_tv);
			holder.orderTV = (TextView)convertView.findViewById(R.id.input_home_lv_item_right_tv);
			holder.typeTV = (TextView)convertView.findViewById(R.id.input_home_lv_item_center_tv);
//			holder.delBT = (Button)convertView.findViewById(R.id.input_home_lv_item_del_bt);
//			holder.delBT.setTag(position);
			convertView.setTag(holder);
		}
		String strtype = "";
		holder = (ViewHolder) convertView.getTag();
		
		switch(type){
			case 1:
				PresentGoods pType1=presentGoodsAll.get(position);
				Company c = pType1.company;
				holder.companyTV.setText(c.name);
				switch(pType1.type){//1:普通、2:同城、3:加急、4:遗留
					case 1:
						strtype = "普通件";
						break;
					case 2:
						strtype = "同城件";
						break;
					case 3:
						strtype = "加急件";
						break;
					case 4:
						strtype = "滞留件";
						break;
					default:
						break;
				}
				holder.typeTV.setText(strtype);
				holder.orderTV.setText(pType1.orderNum);
				break;
			case 2:
				PresentGoods pType2 = presentGoodsCondition.get(position);
				Company company = pType2.company;
				holder.companyTV.setText(company.name);
				switch(pType2.type){//1:普通、2:同城、3:加急、4:遗留
					case 1:
						strtype = "普通件";
						break;
					case 2:
						strtype = "同城件";
						break;
					case 3:
						strtype = "加急件";
						break;
					case 4:
						strtype = "滞留件";
						break;
					default:
						break;
				}
				holder.typeTV.setText(strtype);
				holder.orderTV.setText(pType2.orderNum);
				break;
		}
		
		convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("test", "goodsadapter:点击操作！;position="+position);
				if(type==1){
					pAll = presentGoodsAll.get(position);
				}else{
					pCondition = presentGoodsCondition.get(position);
				}
				Intent intent = new Intent(mActivity,UpdateActivity.class);
				Bundle data = new Bundle();
				if(type==1){
					data.putSerializable("presentGoods", pAll);
//					data.putString("company", pAll.company.name);
//					data.putString("tel", pAll.phoneNum);
//					data.putString("name", pAll.name);
//					String type = "";
//					switch(pAll.type){
//						case 1:
//							type = "普通件";
//							break;
//						case 2:
//							type = "同城件";
//							break;
//						case 3:
//							type = "加急件";
//							break;
//						case 4:
//							type = "滞留件";
//							break;
//						default:
//							break;
//					}
//					data.putString("type", type);
//					data.putString("order", pAll.orderNum);
//					data.putString("shelf", pAll.shelfNum);
				}else{
					data.putSerializable("presentGoods", pCondition);
//					data.putString("company", pCondition.company.name);
//					data.putString("tel", pCondition.phoneNum);
//					data.putString("name", pCondition.name);
//					String type = "";
//					switch(pCondition.type){
//						case 1:
//							type = "普通件";
//							break;
//						case 2:
//							type = "同城件";
//							break;
//						case 3:
//							type = "加急件";
//							break;
//						case 4:
//							type = "滞留件";
//							break;
//						default:
//							break;
//					}
//					data.putString("type", type);
//					data.putString("order", pCondition.orderNum);
//					data.putString("shelf", pCondition.shelfNum);
				}
				intent.putExtras(data);
				mActivity.startActivity(intent);
			}
		});
		
		convertView.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if(type==1){
					pAll = presentGoodsAll.get(position);
					pid = pAll.id;
				}else{
					pCondition = presentGoodsCondition.get(position);
					pid = pCondition.id;
				} 
				Log.i("test", "goodsadapter:长按操作！;pid="+pid);
				new AlertDialog.Builder(mActivity).setTitle("删除该项")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							deletePresentGoods(pid);							
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
				return false;
			}
		});
		return convertView;
	}
	
	private class ViewHolder{
		TextView companyTV,typeTV,orderTV;
		//Button delBT;
	}

	public void deletePresentGoods(final int pid){
		Log.i("test", "goodsadapter:删除操作！");
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				Log.i("test", "delete:pid="+pid);
				return pDao.deletePresentGoods(pid);
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==-1){
					Toast.makeText(context, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return;
				}
				if(result==0){
					Toast.makeText(context, "删除操作失败。", Toast.LENGTH_SHORT).show();
					return;
				}
				Toast.makeText(context, "成功删除。", Toast.LENGTH_SHORT).show();
//				curDel_btn.setVisibility(View.INVISIBLE);
//				curOrder_tv.setVisibility(View.VISIBLE);
				Message msg = handler.obtainMessage();
				msg.what = 0x202;
				handler.sendMessage(msg);
			}
			
		}.execute();
	}
}
