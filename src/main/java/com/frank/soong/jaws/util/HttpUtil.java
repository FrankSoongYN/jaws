package com.frank.soong.jaws.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.frank.soong.jaws.qunar.bean.QunarInfoList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

public class HttpUtil {


	private static final CloseableHttpClient httpClient;
	public static final String CHARSET = "UTF-8";
	private static final int TIMEOUT = 6000;
	private static final int SOCKETTIMEOUT = 15000;

	static {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(TIMEOUT).setSocketTimeout(SOCKETTIMEOUT).build();
		httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	}

	public static String doGet(String url, Map<String, String> params) {
		return doGet(url, params, CHARSET);
	}

	public static String doPost(String url, Map<String, String> params) {
		return doPost(url, params, CHARSET);
	}
	public static String filterUrl(String baseUrl, Map<String, String> params){
		return  filterUrl(baseUrl, params,CHARSET);
	}
	public static String filterUrl(String baseUrl, Map<String, String> params, String charset) {
		if (StringUtils.isBlank(baseUrl)) {
			return null;
		}
		if (params != null && !params.isEmpty()) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
			for (Map.Entry<String, String> entry : params.entrySet()) {
				String value = entry.getValue();
				if (value != null) {
					pairs.add(new BasicNameValuePair(entry.getKey(), value));
				}
			}
			try {
				baseUrl += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return baseUrl;
		}
		return baseUrl;
	}
	public static String concatUrl(String baseUrl, Map<String, String> params) {
		if (StringUtils.isBlank(baseUrl)) {
			return null;
		}
		if (params != null && !params.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				String value = entry.getValue();
				if (value != null) {
					sb.append("&"+entry.getKey()+"="+value);
				}
			}
			try {
				baseUrl += "?" + sb.toString().substring(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return baseUrl;
		}
		return baseUrl;
	}
	public static String urlParam(Map<String, String> params, String charset) {
		String url=null;
		if (params != null && !params.isEmpty()) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
			for (Map.Entry<String, String> entry : params.entrySet()) {
				String value = entry.getValue();
				if (value != null) {
					pairs.add(new BasicNameValuePair(entry.getKey(), value));
				}
			}
			try {
				url= EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return url;
	}

	/**
	 * HTTP Get 获取内容
	 * 
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param charset
	 *            编码格式
	 * @return 页面内容
	 */
	public static String doGet(String url, Map<String, String> params, String charset) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
				url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
			}

			HttpGet httpGet = new HttpGet(url);

			CloseableHttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String doGet(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		try {
			HttpGet httpGet = new HttpGet(url);

			CloseableHttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * HTTP Post 获取内容
	 * 
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param charset
	 *            编码格式
	 * @return 页面内容
	 */
	public static String doPost(String url, Map<String, String> params, String charset) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		try {
			List<NameValuePair> pairs = null;
			if (params != null && !params.isEmpty()) {
				pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
			}
			HttpPost httpPost = new HttpPost(url);		
			if (pairs != null && pairs.size() > 0) {
				httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
			}else{
				pairs = new ArrayList<NameValuePair>();
				httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
			}
			CloseableHttpResponse response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		/*Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("isTouch", "0");
		paramMap.put("t", "all");
		paramMap.put("o", "pop-desc");
		paramMap.put("lm", "40%2C20");
		paramMap.put("fhLimit", "0%2C20");
		paramMap.put("q", "丽江");
		paramMap.put("d", "上海");
		paramMap.put("s", "all");
		paramMap.put("qs_ts", "1459820617572");
		paramMap.put("tm", "ign_newb");
		paramMap.put("sourcepage", "list");
		paramMap.put("qssrc", "eyJ0cyI6IjE0NTk4MjA2MTc1NzIiLCJzcmMiOiJhbGwuZW52YiIsImFjdCI6InNjcm9sbCJ9");
		paramMap.put("m", "l%2CbookingInfo%2Clm");
		paramMap.put("displayStatus", "pc");
		paramMap.put("lines6To10", "0");
		String baseUrl="http://dujia.qunar.com/golfz/routeList/adaptors/pcTop";
		String string= doGet(baseUrl, paramMap);//(baseUrl, paramMap);
		System.out.println(string);*/
		qunarCrawlJob();
		
		
	}
	
	
	public static  boolean isUrl(String urlStr){
		try{
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			/**
			 * public int getResponseCode()throws IOException
			 * 从 HTTP 响应消息获取状态码。
			 * 例如，就以下状态行来说： 
			 * HTTP/1.0 200 OK
			 * HTTP/1.0 401 Unauthorized
			 * 将分别返回 200 和 401。
			 * 如果无法从响应中识别任何代码（即响应不是有效的 HTTP），则返回 -1。 
			 * 
			 * 返回 HTTP 状态码或 -1
			 */
			int state = conn.getResponseCode();
			if(state == 200){
				return true;
			}
			else{
				return false;
			}
		}
		catch(IOException e){
			return false;
		}
	}
	
	
	public static void qunarCrawlJob(){
		long total=getNumFound();
		for(long i=0;i<total;i=i+100){
			getQunarInfo(i,100)	;
		}
	}
	
	public static void getQunarInfo(Long start,int limit){
		 Date d= new Date();
	        SimpleDateFormat sdf= new SimpleDateFormat ("yyyyMMdd");
	        String s=sdf.format(d);
			String url="http://dujia.qunar.com/golfz/routeList/adaptors/pcTop?isTouch=0&t=all&o=pop-desc&lm="+start+"%2C"+limit+"&fhLimit=0%2C20&q=%E4%B8%BD%E6%B1%9F&d=%E4%B8%8A%E6%B5%B7&s=all&qs_ts=1459820617572&tm=ign_newb&sourcepage=list&qssrc=eyJ0cyI6IjE0NTk4MjA2MTc1NzIiLCJzcmMiOiJhbGwuZW52YiIsImFjdCI6InNjcm9sbCJ9&m=l%2CbookingInfo%2Clm&displayStatus=pc&lines6To10=0";
			String re=HttpUtil.doGet(url);
			System.out.println(re);
			if(null!=re){
				Gson g=new Gson();
				Map<String,Object> reMap=g.fromJson(re, new TypeToken<Map<String,Object>>(){}.getType());
				System.out.println(reMap.get("ret")); 

				if(null!=reMap.get("ret")){
					boolean rb=(Boolean) reMap.get("ret");
					if(rb){
						@SuppressWarnings("unchecked")
						Map<String,Object> DataMap=(Map<String, Object>) reMap.get("data");
						System.out.println(""+DataMap.get("limit"));
						
						@SuppressWarnings("unchecked")
						Map<String,Object> LisMap=(Map<String, Object>) DataMap.get("list");
						if(null!=LisMap.get("results")){
							System.out.println(LisMap.get("numFound"));
							String qlistStr=g.toJson(LisMap.get("results"));
							System.out.println("infoList:"+qlistStr);
							JsonArray ja=g.fromJson(qlistStr,new TypeToken<JsonArray>(){}.getType());
							//List<QunarInfoList> infoList=g.fromJson(qlistStr, new TypeToken<List<QunarInfoList>>(){}.getType());
							
							MongoDBJDBC.insert(ja,start.intValue(),s);
							
						}

					}
				}
			}
	}
	
	public static  Long getNumFound(){
		Long count=0l;
		String url="http://dujia.qunar.com/golfz/routeList/adaptors/pcTop?isTouch=0&t=all&o=pop-desc&lm=0%2C2&fhLimit=0%2C20&q=%E4%B8%BD%E6%B1%9F&d=%E4%B8%8A%E6%B5%B7&s=all&qs_ts=1459820617572&tm=ign_newb&sourcepage=list&qssrc=eyJ0cyI6IjE0NTk4MjA2MTc1NzIiLCJzcmMiOiJhbGwuZW52YiIsImFjdCI6InNjcm9sbCJ9&m=l%2CbookingInfo%2Clm&displayStatus=pc&lines6To10=0";
		String re=HttpUtil.doGet(url);
		if(null!=re){
			Gson g=new Gson();
			Map<String,Object> reMap=g.fromJson(re, new TypeToken<Map<String,Object>>(){}.getType());
			System.out.println(reMap.get("ret")); 

			if(null!=reMap.get("ret")){
				boolean rb=(Boolean) reMap.get("ret");
				if(rb){
					@SuppressWarnings("unchecked")
					Map<String,Object> DataMap=(Map<String, Object>) reMap.get("data");
					System.out.println(""+DataMap.get("limit"));
					
					@SuppressWarnings("unchecked")
					Map<String,Object> LisMap=(Map<String, Object>) DataMap.get("list");
					if(null!=LisMap.get("results")){
						System.out.println("商品总数:  "+LisMap.get("numFound"));
						Double nf=(Double) LisMap.get("numFound");
						count=nf.longValue();
					}

				}
			}
			}
		return count;
	}
	
}
