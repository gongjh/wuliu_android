package cn.megasound.youthbegan.wuliumanager.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import cn.megasound.youthbegan.wuliumanager.adapter.HttpAdapter;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.dao.PresentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.entity.Company;
import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;

public class PresentGoodsDaoImpl implements PresentGoodsDao{
	
	private static final String TAG = "HttpAdapter";
	HttpAdapter adapter = new HttpAdapter();

	/**
	 * 添加货件
	 */
	@Override
	public int addGoods(String orderNum, String phoneNum, int companyId,
			String shelfNum, int type, String city, String name) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("code", orderNum));
		vlist.add(new BasicNameValuePair("tel", phoneNum));
		vlist.add(new BasicNameValuePair("company_id", companyId+""));
		vlist.add(new BasicNameValuePair("shelf", shelfNum));
		vlist.add(new BasicNameValuePair("type", type+""));
		vlist.add(new BasicNameValuePair("city", city));
		vlist.add(new BasicNameValuePair("name", name));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Add_PresentGoods, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			Log.i(TAG, "status="+status);
			if("1".equals(status)){
				int hid = jsonObject.getInteger("id");
				Log.i(TAG, "hid="+hid);
				return hid;
			}else if("0".equals(status)){
				String info = jsonObject.getString("info");
				Log.i(TAG, "info="+info);
			}
		}
		return 0;
	}
	
	/**
	 * 修改信息
	 */
	@Override
	public int updateGoods(int id, String orderNum, String phoneNum,
			int companyId, String shelfNum, int type, String city, String name) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("id", id+""));
		vlist.add(new BasicNameValuePair("code", orderNum));
		vlist.add(new BasicNameValuePair("tel", phoneNum));
		vlist.add(new BasicNameValuePair("company_id", companyId+""));
		vlist.add(new BasicNameValuePair("shelf", shelfNum));
		vlist.add(new BasicNameValuePair("type", type+""));
		vlist.add(new BasicNameValuePair("city", city));
		vlist.add(new BasicNameValuePair("name", name));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Update_PresentGoods, vlist);
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

	/**
	 * 获取所有的列表
	 */
	@Override
	public List<PresentGoods> getPresentGoods(int page) {
		List<PresentGoods> list = new ArrayList<PresentGoods>();
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("page", ""+page));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_PresentGoods_List, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			int total = jsonObject.getIntValue("Total");
			ConstantValue.PRESENT_TOTAL = total;
			Log.i(TAG, "status="+status+";total="+total);
			if("1".equals(status)){
				JSONArray jArray = jsonObject.getJSONArray("Rows");
				if(jArray!=null){
					for(Object o:jArray){
						JSONObject jo = (JSONObject)o;
						PresentGoods p = new PresentGoods();
						Company c = new Company();
						p.id = jo.getInteger("id");
						p.orderNum = jo.getString("code");
						p.phoneNum = jo.getString("tel");
						p.name = jo.getString("name");
						long inputdate = jo.getLong("receive_time");
						p.inputDate = new Date(inputdate*1000);
						c.id = jo.getInteger("company_id");
						c.name = jo.getString("company_name");
						p.company = c;
						p.city = jo.getString("city");
						p.type = jo.getInteger("type");
						p.shelfNum = jo.getString("shelf");
						list.add(p);
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

	/**
	 * 获取带条件的列表
	 */
	@Override
	public List<PresentGoods> getPresentGoodsByCondition(int page,String condition) {
		List<PresentGoods> list = new ArrayList<PresentGoods>();
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("page", ""+page));
		vlist.add(new BasicNameValuePair("condition", condition));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_PresentGoods_List, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			int total = jsonObject.getIntValue("Total");
			ConstantValue.PRESENT_CONDITION_TOTAL = total;
			Log.i(TAG, "status="+status+";total="+total);
			if("1".equals(status)){
				JSONArray jArray = jsonObject.getJSONArray("Rows");
				if(jArray!=null){
					for(Object o:jArray){
						JSONObject jo = (JSONObject)o;
						PresentGoods p = new PresentGoods();
						Company c = new Company();
						p.id = jo.getInteger("id");
						p.orderNum = jo.getString("code");
						p.phoneNum = jo.getString("tel");
						p.name = jo.getString("name");
						long inputdate = jo.getLong("receive_time");
						p.inputDate = new Date(inputdate*1000);
						c.id = jo.getInteger("company_id");
						c.name = jo.getString("company_name");
						p.company = c;
						p.city = jo.getString("city");
						p.type = jo.getInteger("type");
						p.shelfNum = jo.getString("shelf");
						list.add(p);
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

	/**
	 * 人来取件
	 */
	@Override
	public int moveToSentGoods(String orderNum) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("code", orderNum));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Move_To_SentGoods, vlist);
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

	/**
	 * 快递公司来取
	 */
	@Override
	public int moveToReturnGoods(String orderNum) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("code", orderNum));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Move_To_ReturnGoods, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			Log.i(TAG, "status="+status);
			if("1".equals(status)){
				return 1;
			}else if("0".equals(status)){
				String info = jsonObject.getString("info");
				Log.i(TAG, "info="+info);
				return 0 ;
			}
		}
		return -1;
	}

	@Override
	public int getCount() {//当天的
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_Today_Total, vlist);
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
	 * 查询详情   获取个数
	 * type:4 zhiliu如果不设置4，则获取除4以外的
	 */
	@Override
	public int getCount(int companyId, String firstDate, String endDate, int type) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("company_id", ""+companyId));
		vlist.add(new BasicNameValuePair("firstDate", firstDate));
		vlist.add(new BasicNameValuePair("endDate", endDate));
		if(type==4){
			vlist.add(new BasicNameValuePair("type", ""+type));
		}
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_Condition_Total, vlist);
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
	 * 查询详情  滞留/待发货列表
	 * type:4 zhiliu如果不设置4，则获取除4以外的
	 */
	@Override
	public List<PresentGoods> getPresentGoodsList(int page, int companyId,
			String firstDate, String endDate,int type) {
		List<PresentGoods> list = new ArrayList<PresentGoods>();
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("page", ""+page));
		vlist.add(new BasicNameValuePair("company_id", ""+companyId));
		vlist.add(new BasicNameValuePair("firstDate", firstDate));
		vlist.add(new BasicNameValuePair("endDate", endDate));
		if(type==4){
			vlist.add(new BasicNameValuePair("type", ""+type));
		}
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_PresentGoods_Condition_List, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			int total = jsonObject.getIntValue("Total");
			ConstantValue.LOOKFOR_REMAIN_TOTAL = total;
			Log.i(TAG, "status="+status+";total="+total);
			if("1".equals(status)){
				JSONArray jArray = jsonObject.getJSONArray("Rows");
				if(jArray!=null){
					for(Object o:jArray){
						JSONObject jo = (JSONObject)o;
						PresentGoods p = new PresentGoods();
						Company c = new Company();
						p.id = jo.getInteger("id");
						p.orderNum = jo.getString("code");
						p.phoneNum = jo.getString("tel");
						p.name = jo.getString("name");
						long inputdate = jo.getLong("receive_time");
						p.inputDate = new Date(inputdate*1000);
						c.id = jo.getInteger("company_id");
						c.name = jo.getString("company_name");
						p.company = c;
						p.city = jo.getString("city");
						p.type = jo.getInteger("type");
						p.shelfNum = jo.getString("shelf");
						list.add(p);
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
	public int deletePresentGoods(int id) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("id", ""+id));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Delete_PresentGoods, vlist);
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

	@Override
	public int rollback(String rid) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("code", rid));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Roll_Back, vlist);
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
