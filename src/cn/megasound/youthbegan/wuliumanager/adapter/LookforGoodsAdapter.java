package cn.megasound.youthbegan.wuliumanager.adapter;

import java.util.List;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.dao.PresentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.ReturnGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.SentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.PresentGoodsDaoImpl;
import cn.megasound.youthbegan.wuliumanager.dao.impl.ReturnGoodsDaoImpl;
import cn.megasound.youthbegan.wuliumanager.dao.impl.SentGoodsDaoImpl;
import cn.megasound.youthbegan.wuliumanager.entity.Company;
import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;
import cn.megasound.youthbegan.wuliumanager.entity.ReturnGoods;
import cn.megasound.youthbegan.wuliumanager.entity.SentGoods;
import cn.megasound.youthbegan.wuliumanager.view.LookforInfoActivity;
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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.LayoutParams;

public class LookforGoodsAdapter extends BaseAdapter {
	
	private int type = 0;
	private Activity mActivity;
	private Context context;
	public List<PresentGoods> presentGoods;
	public List<PresentGoods> waitGoods;
	public List<SentGoods> sentGoods;
	public List<ReturnGoods> returnGoods;
	private Handler handler;
	private LayoutInflater mInflater = null;
	private float x,ux;
	private TextView curOrder_tv;
	private Button curDel_btn;
	
	private PresentGoodsDao pDao;
	private SentGoodsDao sDao;
	private ReturnGoodsDao rDao;
	PresentGoods pGoods;
	PresentGoods wGoods;
	SentGoods sGoods;
	ReturnGoods rGoods;
	int pid,sid,rid,wid;

	public void setType(int type) {
		this.type = type;
	}

	public void setWaitGoods(List<PresentGoods> waitGoods) {
		this.waitGoods = waitGoods;
	}
	
	public void setPresentGoods(List<PresentGoods> presentGoods) {
		this.presentGoods = presentGoods;
	}

	public void setSentGoods(List<SentGoods> sentGoods) {
		this.sentGoods = sentGoods;
	}

	public void setReturnGoods(List<ReturnGoods> returnGoods) {
		this.returnGoods = returnGoods;
	}

	public LookforGoodsAdapter(int type,Activity mActivity, Context context,
			List<PresentGoods> presentGoods, List<SentGoods> sentGoods,
			List<ReturnGoods> returnGoods,List<PresentGoods> waitGoods,Handler handler) {
		this.type = type;
		this.mActivity = mActivity;
		this.context = context;
		this.presentGoods = presentGoods;
		this.sentGoods = sentGoods;
		this.returnGoods = returnGoods;
		this.waitGoods = waitGoods;
		this.handler = handler;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		pDao = new PresentGoodsDaoImpl();
		sDao = new SentGoodsDaoImpl();
		rDao = new ReturnGoodsDaoImpl();
	}
	
	public void addItem(int type,PresentGoods p,SentGoods s,ReturnGoods r,PresentGoods w){
		switch(type){
			case 1://sent
				sentGoods.add(s);
				break;
			case 2://remain
				presentGoods.add(p);
				break;
			case 3://back
				returnGoods.add(r);
				break;
			case 4://wait
				waitGoods.add(w);
		}
	}

	@Override
	public int getCount() {
		switch(type){
			case 1://sent
				return sentGoods.size();
			case 2://remain
				return presentGoods.size();
			case 3://back
				return returnGoods.size();
			case 4://wait
				return waitGoods.size();
			default:
				return 0;
		}
		
	}

	@Override
	public Object getItem(int position) {
		switch(type){
			case 1://sent
				return sentGoods.get(position);
			case 2://remain
				return presentGoods.get(position);
			case 3://back
				return returnGoods.get(position);
			case 4://wait
				return waitGoods.get(position);
			default:
				return null;
		}
	}

	@Override
	public long getItemId(int position) {
		switch(type){
			case 1://sent
				return sentGoods.get(position).id;
			case 2://remain
				return presentGoods.get(position).id;
			case 3://back
				return returnGoods.get(position).id;
			case 4://wait
				return waitGoods.get(position).id;
			default:
				return 0;
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,110);
			convertView = mInflater.inflate(R.layout.lookfor_detail_lv_item, null);
			convertView.setLayoutParams(lp);
			holder = new ViewHolder();
			holder.iconTV = (TextView)convertView.findViewById(R.id.lookfor_detail_lv_item_icon);
			holder.companyTV = (TextView)convertView.findViewById(R.id.lookfor_detail_lv_item_left_tv);
			holder.orderTV = (TextView)convertView.findViewById(R.id.lookfor_detail_lv_item_right_tv);
			holder.typeTV = (TextView)convertView.findViewById(R.id.lookfor_detail_lv_item_center_tv);
			holder.delBT = (Button)convertView.findViewById(R.id.lookfor_detail_lv_item_del_bt);
			holder.delBT.setTag(position);
			convertView.setTag(holder);
		}
		String gtype = "";
		holder = (ViewHolder) convertView.getTag();
		switch(type){
			case 1://sent
				SentGoods gType1 = sentGoods.get(position);
				holder.iconTV.setBackgroundResource(R.drawable.lookfor_detail_lv_item_sent_icom);
				Company c = gType1.company;
				holder.companyTV.setText(c.name);
				switch(gType1.type){//1:普通、2:同城、3:加急、4:遗留
					case 1:
						gtype = "普通件";
						break;
					case 2:
						gtype = "同城件";
						break;
					case 3:
						gtype = "加急件";
						break;
					case 4:
						gtype = "滞留件";
						break;
					default:
						break;
				}
				holder.typeTV.setText(gtype);
				holder.orderTV.setText(gType1.orderNum);
				break;
			case 2://remain
				PresentGoods gType2 = presentGoods.get(position);
				holder.iconTV.setBackgroundResource(R.drawable.lookfor_detail_lv_item_remain_icom);
				Company com = gType2.company;
				holder.companyTV.setText(com.name);
				switch(gType2.type){//1:普通、2:同城、3:加急、4:遗留
					case 1:
						gtype = "普通件";
						break;
					case 2:
						gtype = "同城件";
						break;
					case 3:
						gtype = "加急件";
						break;
					case 4:
						gtype = "滞留件";
						break;
					default:
						break;
				}
				holder.typeTV.setText(gtype);
				holder.orderTV.setText(gType2.orderNum);
				break;
			case 3://back
				ReturnGoods gType3 = returnGoods.get(position);
				holder.iconTV.setBackgroundResource(R.drawable.lookfor_detail_lv_item_back_icom);
				Company company = gType3.company;
				holder.companyTV.setText(company.name);
				switch(gType3.type){//1:普通、2:同城、3:加急、4:遗留
					case 1:
						gtype = "普通件";
						break;
					case 2:
						gtype = "同城件";
						break;
					case 3:
						gtype = "加急件";
						break;
					case 4:
						gtype = "滞留件";
						break;
					default:
						break;
				}
				holder.typeTV.setText(gtype);
				holder.orderTV.setText(gType3.orderNum);
				break;
			case 4://remain
				PresentGoods gType4 = waitGoods.get(position);
				holder.iconTV.setBackgroundResource(R.drawable.lookfor_detail_lv_item_wait_icom);
				Company comp = gType4.company;
				holder.companyTV.setText(comp.name);
				switch(gType4.type){//1:普通、2:同城、3:加急、4:遗留
					case 1:
						gtype = "普通件";
						break;
					case 2:
						gtype = "同城件";
						break;
					case 3:
						gtype = "加急件";
						break;
					case 4:
						gtype = "滞留件";
						break;
					default:
						break;
				}
				holder.typeTV.setText(gtype);
				holder.orderTV.setText(gType4.orderNum);
				break;
		}
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(type==1){
					sGoods = sentGoods.get(position);
				}else if(type==2){
					pGoods = presentGoods.get(position);
				}else if(type==3){
					rGoods = returnGoods.get(position);
				}else{
					wGoods = waitGoods.get(position);
				}
				Intent intent = new Intent(mActivity,LookforInfoActivity.class);
				Bundle data = new Bundle();
				switch(type){
					case 1:
						data.putString("company", sGoods.company.name);
						data.putString("tel", sGoods.phoneNum);
						data.putString("name", sGoods.name);
						data.putString("order", sGoods.orderNum);
						data.putString("shelf", sGoods.shelfNum);
						data.putInt("type", sGoods.type);
						break;
					case 2:
						data.putString("company", pGoods.company.name);
						data.putString("tel", pGoods.phoneNum);
						data.putString("name", pGoods.name);
						data.putString("order", pGoods.orderNum);
						data.putString("shelf", pGoods.shelfNum);
						data.putInt("type", pGoods.type);
						break;
					case 3:
						data.putString("company", rGoods.company.name);
						data.putString("tel", rGoods.phoneNum);
						data.putString("name", rGoods.name);
						data.putString("order", rGoods.orderNum);
						data.putString("shelf", rGoods.shelfNum);
						data.putInt("type", rGoods.type);
						break;
					case 4:
						data.putString("company", wGoods.company.name);
						data.putString("tel", wGoods.phoneNum);
						data.putString("name", wGoods.name);
						data.putString("order", wGoods.orderNum);
						data.putString("shelf", wGoods.shelfNum);
						data.putInt("type", wGoods.type);
						break;
				}
				intent.putExtras(data);
				mActivity.startActivity(intent);
			}
		});
		
		convertView.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if(type==1){
					sGoods = sentGoods.get(position);
					sid = sGoods.id;
					Log.i("test", "goodsadapter:长按操作！;sid="+sid);
					new AlertDialog.Builder(mActivity).setTitle("删除该项")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							deleteSentGoods(sid);							
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
				}else if(type==2){
					pGoods = presentGoods.get(position);
					pid = pGoods.id;
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
				}else if(type==3){
					rGoods = returnGoods.get(position);
					rid = rGoods.id;
					Log.i("test", "goodsadapter:长按操作！;rid="+rid);
					new AlertDialog.Builder(mActivity).setTitle("删除该项")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							deleteReturnGoods(rid);							
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
				}else if(type==4){
					wGoods = waitGoods.get(position);
					wid = wGoods.id;
					Log.i("test", "goodsadapter:长按操作！;wid="+wid);
					new AlertDialog.Builder(mActivity).setTitle("删除该项")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							deletePresentGoods(wid);						
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
				}
				return false;
			}
		});
		return convertView;
	}
	
	private class ViewHolder{
		TextView iconTV,companyTV,typeTV,orderTV;
		Button delBT;
	}
	
	/**
	 * 删除及时派货表中的数据
	 * @param pid
	 */
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
				curDel_btn.setVisibility(View.INVISIBLE);
				curOrder_tv.setVisibility(View.VISIBLE);
				Message msg = handler.obtainMessage();
				msg.what = 0x410;
				handler.sendMessage(msg);
			}
			
		}.execute();
	}
	
	/**
	 * 删除已派货表中的数据
	 * @param sid
	 */
	public void deleteSentGoods(final int sid){
		Log.i("test", "goodsadapter:删除操作！");
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				Log.i("test", "delete:sid="+sid);
				return sDao.deleteSentGoods(sid);
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
				curDel_btn.setVisibility(View.INVISIBLE);
				curOrder_tv.setVisibility(View.VISIBLE);
				Message msg = handler.obtainMessage();
				msg.what = 0x310;
				handler.sendMessage(msg);
			}
			
		}.execute();
	}
	
	/**
	 * 删除已遣还表中的数据
	 * @param rid
	 */
	public void deleteReturnGoods(final int rid){
		Log.i("test", "goodsadapter:删除操作！");
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				Log.i("test", "delete:rid="+rid);
				return rDao.deleteReturnGoods(rid);
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
				msg.what = 0x310;
				handler.sendMessage(msg);
			}
			
		}.execute();
	}

}
