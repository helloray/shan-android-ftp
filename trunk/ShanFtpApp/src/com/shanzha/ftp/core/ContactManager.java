package com.shanzha.ftp.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.text.TextUtils;

import com.shanzha.ftp.dao.ContactService;
import com.shanzha.ftp.inter.ILoadSmsContactListener;
import com.shanzha.ftp.model.Contact;
import com.shanzha.ftp.model.DataKinds.Name;
import com.shanzha.ftp.model.DataKinds.Phone;
import com.shanzha.ftp.util.Constant;
import com.shanzha.ftp.util.FileUtil;
import com.shanzha.ftp.util.StringUtil;

/**
 * 联系人管理者，主要是负责内存中联系人的管理，以及一些联系人相关操作
 * 
 * @author ShanZha
 * @date 2012-10-15 10:03
 * @see ContactService
 */
public class ContactManager {

	private static final String TAG = "ContactManager";
	/**
	 * 自身实例
	 */
	private static ContactManager instance;
	/**
	 * 上下文应用环境
	 */
	private Context mContext;
	/**
	 * 所有联系人集合
	 */
	private List<Contact> contacts;
	/**
	 * 联系人数据库操作对象
	 */
	private ContactService mContactDB;
	/**
	 * 处理Json格式对象
	 */
	private JsonManager mJsonMgr;
	/**
	 * 线程池，最大默认3个，可重复使用
	 */
	private ExecutorService mExecutors = Executors.newFixedThreadPool(3);
	/**
	 * 把所有联系人写到sdcard之后的文件路径
	 */
	private String localFilePath;

	/**
	 * 构造子
	 */
	private ContactManager(Context context) {
		this.mContext = context.getApplicationContext();
		contacts = new ArrayList<Contact>();
		mContactDB = ContactService.getInstance(context);
		mJsonMgr = JsonManager.getInstance();
	}

	/**
	 * 提供给外部实例
	 * 
	 * @return
	 */
	public synchronized static ContactManager getInstance(Context context) {
		if (null == instance) {
			instance = new ContactManager(context);
		}
		return instance;
	}

	/**
	 * 内存中的所有联系人集合
	 * 
	 * @return
	 */
	public List<Contact> getContacts() {
		return contacts;
	}

	// /**
	// * 根据isRefresh决定是否重新读取本地通讯录
	// *
	// * @param isRefresh
	// * @return
	// */
	// public void readAllContacts(IWriteCallback writeCallback) {
	// ContactService.getInstance(mContext).getAllContact(this, writeCallback);
	// }
	//
	// /**
	// * 把本地联系人以Json格式写到sdcard里
	// *
	// * @return 存储到本地的路径
	// */
	// public String writeContactsToSdcard(List<Contact> contacts) {
	// String path = "";
	// if (null != contacts) {
	// String result = JsonUtil.buildJsonContactFromList(contacts);
	// path = FileUtil.writeToFile(FileUtil.CONTACT_FILENAME,
	// result);
	// }
	// return path;
	// }
	// /**
	// * 跟据sdcard路径获取到一个联系人集合
	// * @param path
	// * @return
	// */
	// public List<Contact> getContactsFromSdcard(String path)
	// {
	// String result = StringUtil.transferFileToStr(path);
	// List<Contact> contacts = JsonUtil.parseContactToList(result);
	// return contacts;
	// }

	public String getLocalFilePath() {
		return localFilePath;
	}

	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}

	/**
	 * 加载联系人信息并写到sdcard里（Json格式） 分三个步骤： 1、从通讯录里读取所有联系人信息 2、把获取到的联系人信息转为json字符串
	 * 3、把json格式的字符串以.txt文件形式写到sdcard里
	 * 
	 * @param listener
	 */
	public void writeContactListToSdcard(final ILoadSmsContactListener listener) {
		mExecutors.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					if (null != listener) {
						listener.onLoadPreparation(ILoadSmsContactListener.TYPE_CONTACT);
					}
					// 步骤1
					List<Contact> contactList = mContactDB.queryAllContact();
					// 放入内存，供其他使用
					if (null != contacts && null != contactList) {
						contacts.addAll(contactList);
					}
					// 步骤2
					String temp = mJsonMgr
							.buildJsonContactFromList(contactList);
					String dir = FileUtil.getSdcardPath() + Constant.SEPERATOR
							+ FileUtil.PATH_UPLOAD;
					// 步骤3
					String path = FileUtil.writeToFile(dir,
							FileUtil.CONTACT_FILENAME, temp);
					if (null != listener) {
						listener.onLoadCompleted(path,
								ILoadSmsContactListener.TYPE_CONTACT);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if (null != listener) {
						listener.onLoadError(e.getMessage(),
								ILoadSmsContactListener.TYPE_CONTACT);
					}
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 根据number获取联系人姓名
	 * 
	 * @param number
	 * @return
	 */
	public String getNameByNumber(String number) {
		String name = "";
		if (!TextUtils.isEmpty(number)) {
			return "";
		}
		// 去掉+86
		String realNum = StringUtil.removeCode(number);
		List<Contact> contacts = this.getContacts();
		if (null != contacts) {
			for (int i = 0; i < contacts.size(); i++) {
				Contact contact = contacts.get(i);
				ArrayList<Phone> phones = contact.getPhones();
				if (null != phones) {
					for (Phone phone : phones) {
						String contactNum = StringUtil.removeCharacter(
								phone.getNumber(), "-");
						if (realNum.equals(contactNum)) {
							Name temp = contact.getName();
							name = temp.getDisplayName();
							break;
						}
					}
				}
			}
		}
		// 如果从内存中没有获取到联系人姓名，则从数据库中获取
		if (null == name || "".equals(name)) {
			name = ContactService.getInstance(mContext).getContactNameByNumber(
					realNum);
		}
		return name;
	}

}
