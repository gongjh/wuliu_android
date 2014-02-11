package cn.megasound.youthbegan.wuliumanager.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.megasound.youthbegan.wuliumanager.adapter.HttpAdapter;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.dao.SentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.entity.Company;
import cn.megasound.youthbegan.wuliumanager.entity.SentGoods;

public class SentGoodsDaoImpl implements SentGoodsDao {

	private static final String TAG = "HttpAdapter";
	HttpAdapter adapter = new HttpAdapter();
	
	@Override
	public int getCount() {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_SentGoods_Today_Count, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			Log.i(TAG, "status="+status);
			if("1".equals(status)){
				int total = jsonObject.getInteger("total");
				return total;
			}else if("0".equals(status)){
				String info = jsonObject.getString("info");
				Log.i(TAG, "info="+info);
			}
		}
		return -1;
	}

	/**
	 * 查询详情  获取数量
	 */
	@Override
	public int getCount(int companyId, String firstDate, String endDate) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("company_id", ""+companyId));
		vlist.add(new BasicNameValuePair("firstDate", firstDate));
		vlist.add(new BasicNameValuePair("endDate", endDate));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_SentGoods_Count_Condition, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			Log.i(TAG, "status="+status);
			if("1".equals(status)){
				int total = jsonObject.getInteger("total");
				return total;
			}else if("0".equals(status)){
				String info = jsonObject.getString("info");
				Log.i(TAG, "info="+info);
			}
		}
		return 0;
	}

	/**
	 * 查询详情  已派货列表
	 */
	@Override
	public List<SentGoods> getSentGoodsList(int page, int companyId,
			String firstDate, String endDate) {
		List<SentGoods> list = new ArrayList<SentGoods>();
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("page", ""+page));
		vlist.add(new BasicNameValuePair("company_id", ""+companyId));
		vlist.add(new BasicNameValuePair("firstDate", firstDate));
		vlist.add(new BasicNameValuePair("endDate", endDate));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_SentGoods_Condition_List, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			int total = jsonObject.getIntValue("Total");
			ConstantValue.LOOKFOR_SENT_TOTAL = total;
			Log.i(TAG, "status="+status+";total="+total);
			if("1".equals(status)){
				JSONArray jArray = jsonObject.getJSONArray("Rows");
				if(jArray!=null){
					Log.i(TAG, "rows!=null");
					for(Object o:jArray){
						JSONObject jo = (JSONObject)o;
						SentGoods s = new SentGoods();
						Company c = new Company();
						s.id = jo.getInteger("id");
						s.orderNum = jo.getString("code");
						s.phoneNum = jo.getString("tel");
						s.name = jo.getString("name");
						long inputdate = jo.getLong("receive_time");
						s.inputDate = new Date(inputdate*1000);
						long outputdate = jo.getLong("send_time");
						s.getDate = new Date(outputdate*1000);
						c.id = jo.getInteger("company_id");
						c.name = jo.getString("company_name");
						s.company = c;
						s.city = jo.getString("city");
						s.type = jo.getInteger("type");
						s.shelfNum = jo.getString("shelf");
						list.add(s);
					}
				}
				return list;
			}else if("0".equals(status)){
				String info = jsonObject.getString("info");
				Log.i(TAG, "info="+info);
				return list;
			}
		}
		return null;
	}

	@Override
	public int deleteSentGoods(int id) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("id", ""+id));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Delete_SentGoods, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			Log.i(TAG, "status="+status);
			if("1".equals(status)){
				return 1;
			}else if("0".equals(status)){
				String info = jsonObject.getString("info");
				Log.i(TAG, "info="+info);
				return 0;
			}
		}
		return -1;
	}

}
