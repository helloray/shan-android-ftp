package com.shanzha.ftp.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.shanzha.ftp.inter.ILoadSmsContactListener;
import com.shanzha.ftp.model.Contact;
import com.shanzha.ftp.model.DataKinds;
import com.shanzha.ftp.model.DataKinds.Address;
import com.shanzha.ftp.model.DataKinds.Email;
import com.shanzha.ftp.model.DataKinds.Name;
import com.shanzha.ftp.model.DataKinds.Phone;
import com.shanzha.ftp.util.DBConstant;
import com.shanzha.ftp.util.StringUtil;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * 联系人数据库服务类，主要是负责与数据库打交道
 * 
 * @author ShanZha
 * @date 2012-10-15 09:10
 * @link ContactManager
 */
public class ContactService {

	private static final String TAG = "ContactService";
	/**
	 * 自身实例
	 */
	private static ContactService instance;
	/**
	 * 上下午应用环境
	 */
	private Context mContext;
	/**
	 * 获取公共数据的实例
	 */
	private ContentResolver mCr;

	public ContactService(Context context) {
		this.mContext = context.getApplicationContext();
		this.mCr = this.mContext.getContentResolver();
	}

	/**
	 * 提供给外部实例
	 * 
	 * @return
	 */
	public static ContactService getInstance(Context context) {
		if (null == instance) {
			instance = new ContactService(context);
		}
		return instance;
	}

	/**
	 * 从本地通讯录读取所有联系人信息
	 * 
	 */
	public List<Contact> queryAllContact() throws IOException {

		Cursor rawContactCursor = null;
		try {
			List<Contact> contacts = new ArrayList<Contact>();
			// 仅仅查找所有联系人在Data表里的contact_id，如果需要还可以查找姓名和姓名拼音的字段(可用于排序)
			String[] projection = new String[] { DBConstant.CONTACT_ID };
			rawContactCursor = mCr.query(DBConstant.CONTACT_RAW_URI,
					projection, null, null, null);
			while (rawContactCursor.moveToNext()) {
				long contact_id = rawContactCursor.getLong(rawContactCursor
						.getColumnIndex(DBConstant.CONTACT_ID));
				if (contact_id<0) {
					throw new IOException("联系人contact_id<0");
				}
				Contact contact = getContactInfo(contact_id);
				if (null != contact) {
					contact.setId(contact_id + "");
					contacts.add(contact);
				}
			}
			return contacts;

		} catch (Exception e) {
			throw new IOException("读取联系人出错  " + e.getMessage());
		} finally {
			if (null != rawContactCursor) {
				rawContactCursor.close();
			}
		}
	}

	/**
	 * 根据contact_id获取某个联系人信息
	 * 
	 * @param contact_id
	 * @return
	 */
	private Contact getContactInfo(long contact_id) {
		Contact contact = new Contact();
		String selection = DBConstant.CONTACT_RAW_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(contact_id) };
		Cursor dataCursor = mCr.query(DBConstant.CONTACT_DATA_URI, null,
				selection, selectionArgs, null);
		while (dataCursor.moveToNext()) {
			// 数据
			String data = dataCursor.getString(dataCursor
					.getColumnIndex("data1"));
			// 数据类型
			String mimetype = dataCursor.getString(dataCursor
					.getColumnIndex("mimetype"));
			if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {// phone
				/*
				 * 解释来自源码 <td>{@link #DATA1}</td>号码 <td>{@link #DATA2}</td>类型
				 * <td>{@link #DATA3}</td>标签,用于自定义。
				 */
				int type = dataCursor
						.getInt(dataCursor.getColumnIndex("data2"));
				Phone phone = new Phone();
				phone.setType(type);
				phone.setNumber(data);
				if (contact.getPhones() == null) {
					contact.setPhones(new ArrayList<Phone>());
				}
				contact.getPhones().add(phone);

			} else if ("vnd.android.cursor.item/name".equals(mimetype)) {// name
				/*
				 * 解释来自源码 <td>{@link #DISPLAY_NAME}</td> <td>{@link #DATA1}</td>
				 * <td>{@link #GIVEN_NAME}</td> <td>{@link #DATA2}</td>
				 * <td>{@link #FAMILY_NAME}</td> <td>{@link #DATA3}</td>
				 * <td>{@link #PREFIX}</td> <td>{@link #DATA4}</td> <td>{@link
				 * #MIDDLE_NAME}</td> <td>{@link #DATA5}</td> <td>{@link
				 * #SUFFIX}</td> <td>{@link #DATA6}</td> <td>{@link
				 * #PHONETIC_GIVEN_NAME}</td> <td>{@link #DATA7}</td> <td>{@link
				 * #PHONETIC_MIDDLE_NAME}</td> <td>{@link #DATA8}</td>
				 * <td>{@link #PHONETIC_FAMILY_NAME}</td> <td>{@link
				 * #DATA9}</td>
				 */

				Name name = new Name();
				name.setDisplayName(data);
				// System.out.println(data);
				name.setGivenName(dataCursor.getString(dataCursor
						.getColumnIndex("data2")));
				name.setFamilyName(dataCursor.getString(dataCursor
						.getColumnIndex("data3")));
				name.setPrefix(dataCursor.getString(dataCursor
						.getColumnIndex("data4")));
				name.setMiddleName(dataCursor.getString(dataCursor
						.getColumnIndex("data5")));
				name.setSuffix(dataCursor.getString(dataCursor
						.getColumnIndex("data6")));
				// name.setPhoneticGivenName(dataCursor.getString(dataCursor
				// .getColumnIndex("data7")));
				// name.setPhoneticMiddleName(dataCursor.getString(dataCursor
				// .getColumnIndex("data8")));
				// name.setPhoneticFamilyName(dataCursor.getString(dataCursor
				// .getColumnIndex("data9")));
				// String data10 = dataCursor.getString(dataCursor
				// .getColumnIndex("data10"));
				// String data11 = dataCursor.getString(dataCursor
				// .getColumnIndex("data11"));
				contact.setName(name);
			} else if ("vnd.android.cursor.item/photo".equals(mimetype)) {// photo
				/*
				 * 摘自源码 A data kind representing an photo for the contact. <td>
				 * {@link #PHOTO}</td> <td>{@link #DATA15}</td>
				 */
				byte[] data15 = dataCursor.getBlob(dataCursor
						.getColumnIndex("data15"));
				// 压缩头像
				try {
					BitmapFactory.Options opts = new BitmapFactory.Options();
					// opts.inTempStorage = new byte[16*1024];
					opts.inJustDecodeBounds = true;
					// Bitmap bm = BitmapFactory.decodeByteArray(data15, 0,
					// data15.length, opts);
					int denominator = opts.outWidth / 50;
					opts.inSampleSize = denominator > 0 ? denominator : 1;
					opts.inJustDecodeBounds = false;
					// bm = BitmapFactory.decodeByteArray(data15, 0,
					// data15.length, opts);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					// bm.compress(Bitmap.CompressFormat.PNG, 50, baos);
					// bm.recycle();
					data15 = null;
					contact.setPhoto(baos.toByteArray());
					baos.close();
				} catch (IOException e) {
					// IOExceptione
					e.printStackTrace();
				}
				// contact.setPhoto(data15);
			} else if ("vnd.android.cursor.item/note".equals(mimetype)) {// note
				contact.setMemo(data);
			} else if ("vnd.android.cursor.item/email_v2".equals(mimetype)) {// email
				// 数据列代表值和电话号码一致
				int data2 = dataCursor.getInt(dataCursor
						.getColumnIndex("data2"));
				String data3 = dataCursor.getString(dataCursor
						.getColumnIndex("data3"));
				Email email = new Email();
				email.setType(data2);
				email.setLabel(data3);
				email.setEmail(data);
				if (contact.getEmails() == null) {
					contact.setEmails(new ArrayList<Email>());
				}
				contact.getEmails().add(email);
			} else if ("vnd.android.cursor.item/website".equals(mimetype)) {// website
				if (contact.getWebsites() == null) {
					contact.setWebsites(new ArrayList<String>());
				}
				contact.getWebsites().add(data);
			} else if ("vnd.android.cursor.item/postal-address_v2"// address
			.equals(mimetype)) {
				/*
				 * 注释来自源码解释 String FORMATTED_ADDRESS DATA1 int TYPE DATA2
				 * Allowed values are: TYPE_CUSTOM. Put the actual type in
				 * LABEL. TYPE_HOME TYPE_WORK TYPE_OTHER String LABEL DATA3
				 * String STREET DATA4 String POBOX DATA5 Post Office Box number
				 * String NEIGHBORHOOD DATA6 String CITY DATA7 String REGION
				 * DATA8 String POSTCODE DATA9 String COUNTRY DATA10
				 */
				int data2 = dataCursor.getInt(dataCursor
						.getColumnIndex("data2")); // 得类型
				String data3 = dataCursor.getString(dataCursor
						.getColumnIndex("data3")); // 得标签
				String data4 = dataCursor.getString(dataCursor
						.getColumnIndex("data4")); // 得街道
				String data5 = dataCursor.getString(dataCursor
						.getColumnIndex("data5")); // 得邮箱
				String data6 = dataCursor.getString(dataCursor
						.getColumnIndex("data6")); // 得县城
				String data7 = dataCursor.getString(dataCursor
						.getColumnIndex("data7")); // 得城市
				String data8 = dataCursor.getString(dataCursor
						.getColumnIndex("data8")); // 得省/直辖市
				String data9 = dataCursor.getString(dataCursor
						.getColumnIndex("data9")); // 得邮编
				String data10 = dataCursor.getString(dataCursor
						.getColumnIndex("data10")); // 得国家
				Address address = new Address();
				address.setType(data2);
				address.setStreet(data4);
				address.setPobox(data5);
				address.setNeighborhood(data6);
				address.setCity(data7);
				address.setRegion(data8);
				address.setPostcode(data9);
				address.setCountry(data10);
				address.setLabel(data3);
				if (contact.getAddress() == null) {
					contact.setAddress(new ArrayList<DataKinds.Address>());
				}
				contact.getAddress().add(address);
			}
		}
		if (null != dataCursor) {
			dataCursor.close();
		}
		// 如果该联系人既没有姓名，有没有电话，则不添加
		if (contact.getName() == null && contact.getPhones() == null) {
			return null;
		}
		return contact;
	}

	/*
	 * 根据电话号码取得联系人姓名
	 */
	public String getContactNameByNumber(String phoneNum) {
		String name = "";
		String realNum = StringUtil.removeCode(phoneNum);
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };

		Cursor cursor = mCr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = '"
						+ realNum + "'", // WHERE clause.
				null, // WHERE clause value substitution
				null); // Sort order.

		if (cursor == null) {
			return name;
		}
		Log.i(TAG,
				"getContactNameByNumber cursor.getCount() = "
						+ cursor.getCount());
		if (cursor.moveToNext()) {
			// 取得联系人名字
			int nameFieldColumnIndex = cursor
					.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
			name = cursor.getString(nameFieldColumnIndex);
		}
		if (null != cursor) {
			cursor.close();
		}
		return name;
	}

}
