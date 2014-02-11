package cn.megasound.youthbegan.wuliumanager.adapter;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpAdapter {
	
	public static final String TAG = "HttpAdapter";
	
	private HttpClient client ;
	private HttpPost post ;
	private HttpEntity entity;
	private HttpResponse response ;
	
	
	public HttpAdapter(){
		client = new DefaultHttpClient();
	}
	
	public String sendPost(String uri,List<NameValuePair> list){
		try {
			Log.i(TAG, "uri="+uri);
			post = new HttpPost(uri);
			entity = new UrlEncodedFormEntity(list, "utf-8");
			post.setEntity(entity);
			response = client.execute(post);
			Log.i(TAG, "response="+response);
			//获得相应的状态编码 如果是200 意味请求处理成功
			if(response.getStatusLine().getStatusCode()==200){
				HttpEntity respEntity = response.getEntity();
				String responseStr = EntityUtils.toString(respEntity);
				Log.i(TAG, "responseStr="+responseStr);
				return responseStr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将map集合转换成NameValuePair
	 * @param map
	 * @return
	 */
	public List<NameValuePair> changeListForm(Map<String,String> map){
		List<NameValuePair> formList = new ArrayList<NameValuePair>();
		Set<String> keys = map.keySet();
		for(Iterator<String> i = keys.iterator();i.hasNext();){
			String key = (String)i.next();
			formList.add(new BasicNameValuePair(key, map.get(key)));
		}
		return formList;
	}
	
	/**
	 * 认证书工厂
	 */
	private static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}
	
}
