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
import cn.megasound.youthbegan.wuliumanager.dao.ReturnGoodsDao;
import cn.megasound.youthbegan.wuliumanager.entity.Company;
import cn.megasound.youthbegan.wuliumanager.entity.ReturnGoods;

public class ReturnGoodsDaoImpl implements ReturnGoodsDao {

	private static final String TAG = "HttpAdapter";
	HttpAdapter adapter = new HttpAdapter();
	
	@Override
	public int getCount() {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_ReturnGoods_Today_Count, vlist);
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
	 * 查询 详情 获取数量
	 */
	@Override
	public int getCount(int companyId, String firstDate, String endDate) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("company_id", ""+companyId));
		vlist.add(new BasicNameValuePair("firstDate", firstDate));
		vlist.add(new BasicNameValuePair("endDate", endDate));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_ReturnGoods_Count_Condition, vlist);
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
	 * 查询详情  遣返列表
	 */
	@Override
	public List<ReturnGoods> getReturnGoodsList(int page, int companyId,
			String firstDate, String endDate) {
		List<ReturnGoods> list = new ArrayList<ReturnGoods>();
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("page", ""+page));
		vlist.add(new BasicNameValuePair("company_id", ""+companyId));
		vlist.add(new BasicNameValuePair("firstDate", firstDate));
		vlist.add(new BasicNameValuePair("endDate", endDate));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_ReturnGoods_Condition_List, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			int total = jsonObject.getIntValue("Total");
			ConstantValue.LOOKFOR_BACK_TOTAL = total;
			Log.i(TAG, "status="+status+";total="+total);
			if("1".equals(status)){
				JSONArray jArray = jsonObject.getJSONArray("Rows");
				if(jArray!=null){
					Log.i(TAG, "rows!=null");
					for(Object o:jArray){
						JSONObject jo = (JSONObject)o;
						ReturnGoods r = new ReturnGoods();
						Company c = new Company();
						r.id = jo.getInteger("id");
						r.orderNum = jo.getString("code");
						r.phoneNum = jo.getString("tel");
						r.name = jo.getString("name");
						long inputdate = jo.getLong("receive_time");
						r.inputDate = new Date(inputdate*1000);
						long getdate = jo.getLong("back_time");
						r.getDate = new Date(getdate*1000);
						c.id = jo.getInteger("company_id");
						c.name = jo.getString("company_name");
						r.company = c;
						r.city = jo.getString("city");
						r.type = jo.getInteger("type");
						r.shelfNum = jo.getString("shelf");
						list.add(r);
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
	public int deleteReturnGoods(int id) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("id", ""+id));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Delete_ReturnGoods, vlist);
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
