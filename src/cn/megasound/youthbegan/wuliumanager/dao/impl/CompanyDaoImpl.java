package cn.megasound.youthbegan.wuliumanager.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.megasound.youthbegan.wuliumanager.adapter.HttpAdapter;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.dao.CompanyDao;
import cn.megasound.youthbegan.wuliumanager.entity.Company;

public class CompanyDaoImpl implements CompanyDao {

	private static final String TAG = "HttpAdapter";
	HttpAdapter adapter = new HttpAdapter();
	
	@Override
	public List<Company> getCompany() {
		List<Company> list = new ArrayList<Company>();
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		vlist.add(new BasicNameValuePair("uid", ""+ConstantValue.user.id));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_Company_List, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			Log.i(TAG, "status="+status);
			if("1".equals(status)){
				JSONArray jArray = jsonObject.getJSONArray("Rows");
				for(Object o:jArray){
					JSONObject jo = (JSONObject)o;
					Company c = new Company();
					c.id = jo.getInteger("id");
					c.name = jo.getString("name");
					list.add(c);
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

}
