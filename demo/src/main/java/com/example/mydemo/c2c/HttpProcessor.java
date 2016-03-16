package com.example.mydemo.c2c;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.example.mydemo.utils.Constant;

import android.text.TextUtils;
import android.util.Log;

public class HttpProcessor {

	public final static String TAG = HttpProcessor.class.getName();
	
	public static String doHttpRequest(String baseURL,List<NameValuePair> pairList){		
	
		try {					
			
	        HttpEntity requestHttpEntity = new UrlEncodedFormEntity( pairList);        
            HttpPost httpPost = new HttpPost(baseURL);           
            httpPost.setEntity(requestHttpEntity);            
            HttpClient httpClient = new DefaultHttpClient();          
            HttpResponse response = httpClient.execute(httpPost);        
            if (null == response){
                return "";
            }	                
            int ret = response.getStatusLine().getStatusCode();
			if (ret == 200) {
				HttpEntity entity = response.getEntity();          
				String retSrc = EntityUtils.toString(entity);	        
	            return retSrc;
			}else{
				Log.e(TAG,"http response error:" + ret);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.e(TAG,e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG,e.toString());
			e.printStackTrace();
		}
		return "";
	} 
	
	
	public static final String removeBOM(String data) {
		if (TextUtils.isEmpty(data)) {
			return data;
		}

		if (data.startsWith("\ufeff")) {
			return data.substring(1);
		} else {
			return data;
		}
	}
	
	private static int commonJsonParse(String response,StringBuilder retMsg){
		try { 				
		    if(response.length() == 0){
		    	Log.e(TAG,"register response null");
		    	return -1;
		    }
		    JSONTokener jsonParser = new JSONTokener(removeBOM(response));  	 
		    JSONObject jsonResult = (JSONObject) jsonParser.nextValue();  
		
		    int ret = jsonResult.getInt("ret_code");		    
		    if(ret != 0 ){		    	
		    	retMsg.delete(0,retMsg.length());	
		    	retMsg.append(jsonResult.getString("ret_msg"));
	        
	    }  
		 Log.d(TAG,"retmsg:" + retMsg);
	    return ret;
	} catch (JSONException ex) {  
	    Log.e(TAG,ex.toString()); 		
	}
	return -1;
}	

	public static int doRequestRegister(String user,String pwd,StringBuilder retMsg){
		NameValuePair pair1 = new BasicNameValuePair("name", user);
	    NameValuePair pair2 = new BasicNameValuePair("password", pwd);	
	    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
	    pairList.add(pair1);
	    pairList.add(pair2);
	    
	    String strURL = Constant.BASE_URL + "register";	
	    Log.d(TAG,"doRequestRegister" + user + ":" + pwd );
	    String result=doHttpRequest(strURL,pairList);
	    return commonJsonParse(result,retMsg);

	}	
	public static int doRequestLogin(String user,String pwd,StringBuilder retMsg,StringBuilder userSig){
		NameValuePair pair1 = new BasicNameValuePair("name", user);
	    NameValuePair pair2 = new BasicNameValuePair("password", pwd);	    
	    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
	    pairList.add(pair1);
	    pairList.add(pair2);
	    
	    String strURL = Constant.BASE_URL + "login";
	    Log.d(TAG,"doRequestLogin|" + user + ":" + pwd );
	    String response=doHttpRequest(strURL,pairList);
	    Log.d(TAG,"doRequestLogin result:" + response);
	    try { 				
		    if(response.length() == 0){
		    	Log.e(TAG,"register response null");
		    	return -1;
		    }
		    JSONTokener jsonParser = new JSONTokener(response);  	 
		    JSONObject jsonResult = (JSONObject) jsonParser.nextValue();  
		
		    int ret = jsonResult.getInt("ret_code");
		    if(ret != 0 ){
		    	retMsg.delete(0,retMsg.length());	
		    	retMsg.append(jsonResult.getString("ret_msg"));
	        }else{
	        	JSONObject json_data = jsonResult.getJSONObject("ret_data");	
	        	userSig.delete(0, userSig.length());
	        	userSig.append(json_data.getString("UserSig")); 	
	        	Log.d(TAG,"doRequestLogin result:" + json_data.getString("Identifier") +":"+ userSig);
	        }	        	
		    return ret;
		} catch (JSONException ex) {  
		    Log.e(TAG,ex.toString()); 	
		    return -1;
		}
	}	
		
	public static int doRequestGetInfo(String user,StringBuilder retMsg){
		NameValuePair pair1 = new BasicNameValuePair("name", user);		    
	    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
	    pairList.add(pair1);	
	    
	    String strURL = Constant.BASE_URL + "getinfo";
	    String result=doHttpRequest(strURL,pairList);
	    Log.d(TAG,"doRequestGetInfo result:" + result);
	    return commonJsonParse(result,retMsg);
	}		

	
	public static int doRequestAddFriend(String user,String friendName,StringBuilder retMsg){
		NameValuePair pair1 = new BasicNameValuePair("name", user);
		NameValuePair pair2 = new BasicNameValuePair("friendname", friendName);	
	    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
	    pairList.add(pair1);
	    pairList.add(pair2);	
	    
	    String strURL = Constant.BASE_URL_LIST + "addfriend";
	    Log.d(TAG,"doRequestAddFriend|" + user + ":" + friendName );
	    String result= doHttpRequest(strURL,pairList);
	    Log.d(TAG,"doRequestAddFriend result:" + result);	
	    return commonJsonParse(result,retMsg);
	}		
	
	public static int doRequestDelFriend(String user,String friendName,StringBuilder retMsg){
		NameValuePair pair1 = new BasicNameValuePair("name", user);
		NameValuePair pair2 = new BasicNameValuePair("friendname", friendName);	
	    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
	    pairList.add(pair1);
	    pairList.add(pair2);	
	    
	    String strURL = Constant.BASE_URL_LIST + "delfriend";
	    Log.d(TAG,"doRequestDelFriend|" + user + ":" + friendName );
	    String result= doHttpRequest(strURL,pairList);
	    Log.d(TAG,"doRequestDelFriend result:" + result);	
	    return commonJsonParse(result,retMsg);
	}		

	public static int doRequestGetFriend(String user,StringBuilder retMsg){
		NameValuePair pair1 = new BasicNameValuePair("name", user);
	    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
	    pairList.add(pair1);

	    
	    String strURL = Constant.BASE_URL_LIST + "getfriend";
	    Log.d(TAG,"doRequestGetFriend|" + user  );
	    String response= doHttpRequest(strURL,pairList);
	    Log.d(TAG,"doRequestGetFriend|" + user + ":" + response );	    
	    try { 			
			
		    if(response.length() == 0){
		    	Log.e(TAG,"register response null");
		    	return -1;
		    }
		    JSONTokener jsonParser = new JSONTokener(response);  	 
		    JSONObject jsonResult = (JSONObject) jsonParser.nextValue();  
		
		    int ret = jsonResult.getInt("ret_code");
		    if(ret != 0 ){
		    	retMsg.delete(0,retMsg.length());
		    	retMsg.append(jsonResult.getString("ret_msg"));
	        }else{
	        	JSONArray array = jsonResult.getJSONArray("ret_data");
	        	int arraySize = array.length();
	        	Map<String,UserInfo> users = UserInfoManager.getInstance().getContactsList();
	        	users.clear();
	        	for(int i=0;i<arraySize;i++){
	        		JSONObject ob = array.getJSONObject(i); 
	        		if( ob.getString("username").isEmpty() || (ob.getString("username").length()==0)){
	        			Log.e(TAG,"doRequestGetFriend: username null");
	        			continue;
	        		}
	        		UserInfo userInfo = new UserInfo();
	        		userInfo.setName(ob.getString("username"));
	        		userInfo.ProcessHeader();
	        		users.put(userInfo.getName(), userInfo);
	        		Log.d(TAG,"friends:" + userInfo.getName()+":" + userInfo.getHeader());
	        	}     	
	        }
	        	
	    return ret;
	} catch (JSONException ex) {  
	    Log.e(TAG,ex.toString()); 		
	}
	return -1;
	}	
}
