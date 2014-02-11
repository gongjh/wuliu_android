package cn.megasound.youthbegan.wuliumanager.adapter;

import java.util.List;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.entity.Contacts;
import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactsAdapter extends BaseAdapter {
	private static final String TAG = "test";
	
	private int type = 0;
	private Activity mActivity;
	private Context context;
	public List<Contacts> failedContacts;
	public List<Contacts> notsentContacts;
	private Handler handler;
	private LayoutInflater mInflater = null;
	
	int cid;

	public void setType(int type) {
		this.type = type;
	}

	public void setFailedContacts(List<Contacts> failedContacts) {
		this.failedContacts = failedContacts;
	}

	public void setNotsentContacts(List<Contacts> notsentContacts) {
		this.notsentContacts = notsentContacts;
	}

	public ContactsAdapter(int type,Activity mActivity, Context context, List<Contacts> failedContacts, List<Contacts> notsentContacts, Handler handler) {
		super();
		this.type = type;
		this.mActivity = mActivity;
		this.context = context;
		this.failedContacts = failedContacts;
		this.notsentContacts = notsentContacts;
		this.handler = handler;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		switch(type){
			case 1:
				return failedContacts.size();
			case 2:
				return notsentContacts.size();
			default:
				return 0;
		}
	}
	
	public void addItem(int type,Contacts failed,Contacts notsent){
		switch(type){
			case 1:
				failedContacts.add(failed);
				break;
			case 2:
				notsentContacts.add(notsent);
				break;
		}
	}
	
	@Override
	public Object getItem(int position) {
		switch(type){
			case 1:
				return failedContacts.get(position);
			case 2:
				return notsentContacts.get(position);
			default:
				return null;
		}
	}

	@Override
	public long getItemId(int position) {
		switch(type){
			case 1:
				return failedContacts.get(position).id;
			case 2:
				return notsentContacts.get(position).id;
			default:
				return 0;
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,130);
			convertView = mInflater.inflate(R.layout.contact_home_lv_item, null);
			convertView.setLayoutParams(lp);
			holder = new ViewHolder();
			holder.telTV = (TextView)convertView.findViewById(R.id.contact_home_lv_item_tel_tv);
			holder.addBT = (Button)convertView.findViewById(R.id.contact_home_lv_item_right_bt);
			holder.addBT.setTag(position);
			holder.traIV = (ImageView)convertView.findViewById(R.id.contact_home_lv_item_tra_icon);
			holder.remarkTV = (TextView)convertView.findViewById(R.id.contact_home_remark_tv);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		switch(type){
			case 1:
				final Contacts contact = failedContacts.get(position);
				final PresentGoods pGoods = contact.goods;
				holder.telTV.setText(pGoods.phoneNum);
				//点击添加remark
				holder.addBT.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						int pos = (Integer) v.getTag();
						Contacts contacts = (Contacts) getItem(pos);
						final PresentGoods p = contacts.goods;
						Log.i(TAG, "contacts info:"+contacts);
						Log.i(TAG, "presentGoods info:"+p);
						Bundle data = new Bundle();
						data.putSerializable("contacts", contacts);
						data.putSerializable("presentGoods", p);
						Message msg = handler.obtainMessage();
						msg.what = 0x501;
						msg.setData(data);
						handler.sendMessage(msg);
						Log.i(TAG, "点击编辑");
					}
				});
				
				String remark = contact.remark;
				if(!TextUtils.isEmpty(remark)){
					holder.remarkTV.setText(remark);
				}else{
					holder.remarkTV.setText(pGoods.company.name);
				}
				break;
			case 2:
				final Contacts c = notsentContacts.get(position);
				final PresentGoods p = c.goods;
				holder.telTV.setText(p.phoneNum);
				//点击添加remark
				holder.addBT.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//Log.i(TAG, "position="+position+";contact="+c);
						int pos = (Integer) v.getTag();
						Contacts contacts = (Contacts) getItem(pos);
						final PresentGoods p = contacts.goods;
						Log.i(TAG, "contacts info:"+contacts);
						Log.i(TAG, "presentGoods info:"+p);
						Bundle data = new Bundle();
						data.putSerializable("contacts", contacts);
						data.putSerializable("presentGoods", p);
						Message msg = handler.obtainMessage();
						msg.what = 0x501;
						msg.setData(data);
						handler.sendMessage(msg);
						Log.i(TAG, "点击编辑");
					}
				});
				String content = c.remark;
				if(!TextUtils.isEmpty(content)){
					holder.remarkTV.setText(content);
				}else{
					holder.remarkTV.setText(p.company.name);
				}
				break;
		}
//		convertView.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Log.i("test", "goodsadapter:点击操作！");
//			}
//		});
		
//		convertView.setOnLongClickListener(new View.OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View v) {
//				Contacts c = null;
//				if(type==1){
//					c = failedContacts.get(position);
//					cid = c.id;
//				}else if(type==2){
//					c = notsentContacts.get(position);
//					cid = c.id;
//				}
//				new AlertDialog.Builder(context)
//					.setTitle("提示")
//					.setMessage("删除该项")
//					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							deleteContacts(cid);
//							dialog.dismiss();
//						}
//					})
//					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.dismiss();
//						}
//					})
//					.show();
//				return false;
//			}
//		});
		
		return convertView;
	}
	
	private class ViewHolder{
		ImageView traIV;
		TextView telTV,remarkTV;
		Button  addBT;
	}

	
	

}
