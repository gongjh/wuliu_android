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
import cn.megasound.youthbegan.wuliumanager.dao.UsersDao;

public class UsersDaoImpl implements UsersDao {

	private static final String TAG = "HttpAdapter";
	HttpAdapter adapter = new HttpAdapter();
	
	@Override
	public int login(String name, String pwd) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("username", name));
		vlist.add(new BasicNameValuePair("password", pwd));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Login, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			Log.i(TAG, "status="+status);
			if("1".equals(status)){
				int uid = jsonObject.getIntValue("uid");
				String station = jsonObject.getString("city");
				Log.i(TAG, "uid="+uid+";city="+station);
				ConstantValue.station = station;
				return uid;
			}else if("0".equals(status)){
				String info = jsonObject.getString("info");
				Log.i(TAG, "info="+info);
				return 0;
			}
		}
		return -1;
	}

	@Override
	public int logout(int uid) {
		Log.i(TAG, "logout:uid="+uid);
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("uid", ""+uid));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Logout, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			Log.i(TAG, "status="+status);
			if("1".equals(status)){
				return 1;
			}else if("0".equals(status)){
				String info = jsonObject.getString("info");
				Log.i(TAG, "info="+info);
			}
		}
		return -1;
	}

	/**
	 * 验证快递单号
	 * 返回：0：快递单号可用
	 *      1：及时派货表中存在
	 *      2：已派货表中存在
	 *      3：遣返表中存在
	 *      -1：连接不上服务器
	 */
	@Override
	public int checkCode(String orderNum) {
		List<NameValuePair>  vlist = new ArrayList<NameValuePair>();
		vlist.add(new BasicNameValuePair("code", orderNum));
		String result = adapter.sendPost(ConstantValue.HOST_NAME+ConstantValue.Check_Code, vlist);
		if(!TextUtils.isEmpty(result)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("status");
			Log.i(TAG, "status="+status);
			if("0".equals(status)){
				return 0;
			}else if("1".equals(status)){
				return 1;
			}else if("2".equals(status)){
				return 2;
			}else if("3".equals(status)){
				return 3;
			}
			String info = jsonObject.getString("info");
			Log.i(TAG, "info="+info);
		}
		return -1;
	}

}
