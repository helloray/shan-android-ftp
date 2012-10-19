package com.shanzha.ftp.core;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.shanzha.ftp.model.Contact;
import com.shanzha.ftp.model.DataKinds;
import com.shanzha.ftp.model.DataKinds.Email;
import com.shanzha.ftp.model.DataKinds.Name;
import com.shanzha.ftp.model.DataKinds.Phone;
import com.shanzha.ftp.model.Sms;

import android.content.Context;
import android.util.Log;

/**
 * JSON格式数据管理类
 * @author ShanZha
 * @date 2012-10-11 15:11
 *
 */
public class JsonManager {

	private static final String TAG = "JsonManager";
	private static JsonManager instance;
	private JsonManager(){}
	/**
	 * 提供外部唯一实例
	 * @return
	 */
	public synchronized static JsonManager getInstance()
	{
		if(null==instance)
		{
			instance = new JsonManager();
		}
		return instance;
	}
	
	/**
	 * 把短信列表转为json格式字符串
	 * @param smss
	 * @param context
	 * @return
	 */
	public String buildJsonStrFromList(List<Sms> smss,Context context) throws JSONException
	{
		JSONObject rootJsonObj = new JSONObject();
		JSONArray rootJsonArr = new JSONArray();
		try {
			rootJsonObj.put(SMS_JSON_ROOT_KEY, rootJsonArr);
			if (null != smss) {
				for (int i = 0; i < smss.size(); i++) {
					Sms sms = smss.get(i);
					// 暂时不要草稿箱里的
					if ("3".equals(sms.getType())) {
						continue;
					}
					JSONObject jsonObj = new JSONObject();
					rootJsonArr.put(i, jsonObj);
					String phoneNumber = sms.getPhoneNumber();
//					if (null != phoneNumber && phoneNumber.length() > 0) {
//						String contactName = ContactManager.getInstance(
//								context).getNameByNumber(phoneNumber);
//						jsonObj.put(SMS_JSON_CONTACTNAME, contactName);
//					}
					jsonObj.put(SMS_JSON_ID, sms.getId());
					jsonObj.put(SMS_JSON_THREAD_ID, sms.getThread_id());
					jsonObj.put(SMS_JSON_ADDRESS, sms.getPhoneNumber());
					jsonObj.put(SMS_JSON_BODY, sms.getBody());
					jsonObj.put(SMS_JSON_TYPE, sms.getType());
//					jsonObj.put(SMS_JSON_TIME, StringUtil.formatDate(sms.getDate(),
//							"yyyy-MM-dd hh:mm"));
					jsonObj.put(SMS_JSON_TIME, sms.getDate());
				}
			}
//			Log.i(TAG, rootJsonObj.toString());
			return rootJsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			throw new JSONException("短信转Json格式时出错");
		}
	}
	/**
	 * 把json形式字符串转为短信息列表
	 * @param result
	 * @return
	 */
	public  List<Sms> parseSmsToList(String result)
	{
		List<Sms> smss = new ArrayList<Sms>();
		try {
			JSONObject rootJsonObj = new JSONObject(result);
			JSONArray rootJsonArr = rootJsonObj.getJSONArray(SMS_JSON_ROOT_KEY);
		    if(null!=rootJsonArr)
		    {
		    	for(int i=0;i<rootJsonArr.length();i++)
		    	{
		    		JSONObject jsonObj = rootJsonArr.getJSONObject(i);
		    		Sms sms = new Sms();
		    		sms.setId(jsonObj.getString(SMS_JSON_ID));
		    		sms.setThread_id(jsonObj.getString(SMS_JSON_THREAD_ID));
		    		sms.setBody(jsonObj.getString(SMS_JSON_BODY));
		    		sms.setPhoneNumber(jsonObj.getString(SMS_JSON_ADDRESS));
		    		sms.setType(jsonObj.getString(SMS_JSON_TYPE));
//		    		String sTime = jsonObj.getString(SMS_JSON_TIME);
//		    		sms.setDate(StringUtil.transferStrTimeToLong(sTime));
		    		sms.setDate(jsonObj.getLong(SMS_JSON_TIME));
		    		smss.add(sms);
		    	}
		    }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return smss;
	}
	/**
	 * 把一个联系人列表构造成一个json字符串
	 * @param contacts
	 * @return
	 */
	public  String buildJsonContactFromList(List<Contact> contacts)
	{
		JSONObject rootJsonObj = new JSONObject();
		try {
			JSONArray rootJsonArr = new JSONArray();
			rootJsonObj.put(CONTACT_JSON_ROOT_KEY, rootJsonArr);
			for (int i = 0; i < contacts.size(); i++) {
				JSONObject jsonObj = new JSONObject();
				rootJsonArr.put(i, jsonObj);
				Contact contact = contacts.get(i);
				ArrayList<Phone> phones = contact.getPhones();
				if (null != phones) {
					JSONArray phoneArray = new JSONArray();
					for (int j = 0; j < phones.size(); j++) {

						phoneArray.put(j, phones.get(j).getNumber());
					}
					jsonObj.put(CONTACT_JSON_PHONE, phoneArray);
				}
				ArrayList<Email> emails = contact.getEmails();
				if (null != emails) {
					JSONArray emailArr = new JSONArray();
					for (int k = 0; k < emails.size(); k++) {
						emailArr.put(k, emails.get(k).getEmail());
					}
					jsonObj.put(CONTACT_JSON_EMAIL, emailArr);
				}
				//(暂时只管display_name)
				Name name = contact.getName();
				if (null != name) {
					String displayName = name.getDisplayName();
					jsonObj.put(CONTACT_JSON_NAME, displayName);
				}
			}
//			Log.i(TAG, rootJsonObj.toString());
			return rootJsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 把json形式字符串转为联系人息列表
	 * @param result
	 * @return
	 */
	public  List<Contact> parseContactToList(String result)
	{
		List<Contact> contacts = new ArrayList<Contact>();
		try {
			JSONObject rootJsonObj = new JSONObject(result);
			JSONArray rootJsonArr = rootJsonObj.getJSONArray(CONTACT_JSON_ROOT_KEY);
			if(null!=rootJsonArr)
			{
				for(int i=0;i<rootJsonArr.length();i++)
				{
					JSONObject jsonObj = rootJsonArr.getJSONObject(i);
					Contact contact = new Contact();
					//电话号码
					JSONArray phoneArr = jsonObj.getJSONArray(CONTACT_JSON_PHONE);
					ArrayList<DataKinds.Phone> phones = new ArrayList<DataKinds.Phone>();
					for(int j=0;j<phoneArr.length();j++)
					{
						String num = phoneArr.get(j)+"";
						Phone phone = new Phone();
						phone.setNumber(num);
						phones.add(phone);
					}
					contact.setPhones(phones);
					
					//邮箱
					JSONArray emailArr = jsonObj.getJSONArray(CONTACT_JSON_EMAIL);
					ArrayList<DataKinds.Email> emails = new ArrayList<DataKinds.Email>();
					for(int j=0;j<emailArr.length();j++)
					{
						String temp = emailArr.get(j)+"";
						Email email = new Email();
						email.setEmail(temp);
						emails.add(email);
					}
					contact.setEmails(emails);
					
					//姓名(暂时只管display_name)
					String sName = jsonObj.getString(CONTACT_JSON_NAME);
					Name name = new Name();
					name.setDisplayName(sName);
					contact.setName(name);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contacts;
	}
	
	private static final String SMS_JSON_ROOT_KEY = "sms";
	private static final String SMS_JSON_ID = "id";
	private static final String SMS_JSON_THREAD_ID = "thread_id";
	private static final String SMS_JSON_ADDRESS = "phoneNumber";
	private static final String SMS_JSON_BODY = "body";
	private static final String SMS_JSON_TYPE = "type";
	private static final String SMS_JSON_TIME = "time";
	private static final String SMS_JSON_CONTACTNAME = "contactname";
	
	private static final String CONTACT_JSON_ROOT_KEY = "contact";
	private static final String CONTACT_JSON_PHONE = "phone";
	private static final String CONTACT_JSON_EMAIL = "email";
	private static final String CONTACT_JSON_NAME = "name";
}
