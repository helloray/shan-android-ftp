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
 * ��ϵ�˹����ߣ���Ҫ�Ǹ����ڴ�����ϵ�˵Ĺ����Լ�һЩ��ϵ����ز���
 * 
 * @author ShanZha
 * @date 2012-10-15 10:03
 * @see ContactService
 */
public class ContactManager {

	private static final String TAG = "ContactManager";
	/**
	 * ����ʵ��
	 */
	private static ContactManager instance;
	/**
	 * ������Ӧ�û���
	 */
	private Context mContext;
	/**
	 * ������ϵ�˼���
	 */
	private List<Contact> contacts;
	/**
	 * ��ϵ�����ݿ��������
	 */
	private ContactService mContactDB;
	/**
	 * ����Json��ʽ����
	 */
	private JsonManager mJsonMgr;
	/**
	 * �̳߳أ����Ĭ��3�������ظ�ʹ��
	 */
	private ExecutorService mExecutors = Executors.newFixedThreadPool(3);
	/**
	 * ��������ϵ��д��sdcard֮����ļ�·��
	 */
	private String localFilePath;

	/**
	 * ������
	 */
	private ContactManager(Context context) {
		this.mContext = context.getApplicationContext();
		contacts = new ArrayList<Contact>();
		mContactDB = ContactService.getInstance(context);
		mJsonMgr = JsonManager.getInstance();
	}

	/**
	 * �ṩ���ⲿʵ��
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
	 * �ڴ��е�������ϵ�˼���
	 * 
	 * @return
	 */
	public List<Contact> getContacts() {
		return contacts;
	}

	// /**
	// * ����isRefresh�����Ƿ����¶�ȡ����ͨѶ¼
	// *
	// * @param isRefresh
	// * @return
	// */
	// public void readAllContacts(IWriteCallback writeCallback) {
	// ContactService.getInstance(mContext).getAllContact(this, writeCallback);
	// }
	//
	// /**
	// * �ѱ�����ϵ����Json��ʽд��sdcard��
	// *
	// * @return �洢�����ص�·��
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
	// * ����sdcard·����ȡ��һ����ϵ�˼���
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
	 * ������ϵ����Ϣ��д��sdcard�Json��ʽ�� ���������裺 1����ͨѶ¼���ȡ������ϵ����Ϣ 2���ѻ�ȡ������ϵ����ϢתΪjson�ַ���
	 * 3����json��ʽ���ַ�����.txt�ļ���ʽд��sdcard��
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
					// ����1
					List<Contact> contactList = mContactDB.queryAllContact();
					// �����ڴ棬������ʹ��
					if (null != contacts && null != contactList) {
						contacts.addAll(contactList);
					}
					// ����2
					String temp = mJsonMgr
							.buildJsonContactFromList(contactList);
					String dir = FileUtil.getSdcardPath() + Constant.SEPERATOR
							+ FileUtil.PATH_UPLOAD;
					// ����3
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
	 * ����number��ȡ��ϵ������
	 * 
	 * @param number
	 * @return
	 */
	public String getNameByNumber(String number) {
		String name = "";
		if (!TextUtils.isEmpty(number)) {
			return "";
		}
		// ȥ��+86
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
		// ������ڴ���û�л�ȡ����ϵ��������������ݿ��л�ȡ
		if (null == name || "".equals(name)) {
			name = ContactService.getInstance(mContext).getContactNameByNumber(
					realNum);
		}
		return name;
	}

}
