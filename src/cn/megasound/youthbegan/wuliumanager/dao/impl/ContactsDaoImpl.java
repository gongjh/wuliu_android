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
import cn.megasound.youthbegan.wuliumanager.dao.ContactsDao;
import cn.megasound.youthbegan.wuliumanager.entity.Company;
import cn.megasound.youthbegan.wuliumanager.entity.Contacts;
import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;

public class ContactsDaoImpl implements ContactsDao {

	private static final String TAG = "HttpAdapter";
	HttpAdapter adapter = new HttpAdapter();
	
	/**
	 * 添加联系人
	 */
	@Override
	public int addContacts(int hid, int message, int goodsstate,
			String remark) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("sid", ""+hid));
		vlist.add(new BasicNameValuePair("sms_status", ""+message));
		vlist.add(new BasicNameValuePair("goods_status", ""+goodsstate));
		vlist.add(new BasicNameValuePair("comment", remark));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Add_Contacts, vlist);
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
	public int updateContacts(int id, int hid, int message, int goodsstate,
			String remark) {
		
		return -1;
	}

	@Override
	public int updateContacts(int id, int message) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("id", ""+id));
		vlist.add(new BasicNameValuePair("sms_status", ""+message));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Update_Contacts_Sms, vlist);
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
	public int updateContacts(int id, String remark) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("id", ""+id));
		vlist.add(new BasicNameValuePair("comment", remark));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Update_Contacts_Remark, vlist);
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
	 * 获取联系人中    未发送成功/货物未取    的列表
	 * messagestate:失败:0     成功:1
	 */
	@Override
	public List<Contacts> getContacts(int page, int messagestate) {
		List<Contacts> list = new ArrayList<Contacts>();
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("page", ""+page));
		vlist.add(new BasicNameValuePair("sms_status", ""+messagestate));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_Contacts_List, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			int total = jsonObject.getIntValue("Total");
			ConstantValue.CONTACT_TOTAL = total;
			Log.i(TAG, "status="+status+";total="+total);
			if("1".equals(status)){
				JSONArray jArray = jsonObject.getJSONArray("Rows");
				for(Object o:jArray){
					JSONObject jo = (JSONObject)o;
					Contacts contact = new Contacts();
					PresentGoods p = new PresentGoods();
					Company c = new Company();
					p.company = c;
					contact.id = jo.getInteger("id");
					p.id = jo.getInteger("sid");
					p.orderNum = jo.getString("code");
					p.phoneNum = jo.getString("tel");
					long time = jo.getLong("receive_time");
					p.inputDate = new Date(time*1000);
					p.company.id = jo.getInteger("company_id");
					p.company.name = jo.getString("company_name");
					p.type = jo.getInteger("type");
					p.shelfNum = jo.getString("shelf");
					p.city = jo.getString("city");
					p.name = jo.getString("name");
					contact.goods = p ;
					contact.goodsstate = jo.getInteger("goods_status");
					contact.messagestate = jo.getInteger("sms_status");
					contact.remark = jo.getString("comment");
					list.add(contact);
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
	public int sentMessage(String phone, String message) {
		List<NameValuePair>  vlist=new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("account", "544828662"));
		vlist.add(new BasicNameValuePair("pwd", "123456"));
		vlist.add(new BasicNameValuePair("product", "1060000009"));
		vlist.add(new BasicNameValuePair("needstatus", "true"));
		vlist.add(new BasicNameValuePair("mobile",phone));
		vlist.add(new BasicNameValuePair("message",message));
		vlist.add(new BasicNameValuePair("senddate",""));
		vlist.add(new BasicNameValuePair("extno", ""));
		String result = adapter.sendPost(ConstantValue.Send_Message, vlist);
		if(!TextUtils.isEmpty(result)){
			return 1;
		}
		return -1;
	}

	@Override
	public int deleteContacts(int id) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		vlist.add(new BasicNameValuePair("id", ""+id));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Delete_Contacts, vlist);
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
