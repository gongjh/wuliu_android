package cn.megasound.youthbegan.wuliumanager.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import cn.megasound.youthbegan.wuliumanager.adapter.HttpAdapter;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.dao.VersionDao;
import cn.megasound.youthbegan.wuliumanager.entity.Version;

public class VersionDaoImpl implements VersionDao {

	private static final String TAG = "HttpAdapter";
	HttpAdapter adapter = new HttpAdapter();
	
	@Override
	public Version getFromServer() {
		Version v ;
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("sign", ConstantValue.SIGN));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Get_Version, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			Log.i(TAG, "status="+status);
			if("1".equals(status)){
				v = new Version();
				v.number = jsonObject.getString("version");
				v.url = jsonObject.getString("url");
				return v;
			}else if("0".equals(status)){
				String info = jsonObject.getString("info");
				Log.i(TAG, "info="+info);
			}
		}
		return null;
	}

}
