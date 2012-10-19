package com.shanzha.ftp.model;

import java.util.ArrayList;
import java.util.Arrays;

import com.shanzha.ftp.model.DataKinds.Address;
import com.shanzha.ftp.model.DataKinds.Email;
import com.shanzha.ftp.model.DataKinds.Im;
import com.shanzha.ftp.model.DataKinds.Name;
import com.shanzha.ftp.model.DataKinds.Organization;
import com.shanzha.ftp.model.DataKinds.Phone;

import android.provider.CallLog;

 
 

/**
 * ��ϵ��ģ��
 * <p>
 * ����ȫ����Ϣ��id��ͷ�񣬱�ע�����֣����룬�ʼ�����ַ����֯����ʱͨ�ŵ�...
 * 
 * @author mty
 * @update ShanZha
 * @time 2012-10-11 15:15
 */
public class Contact {
	/******************* ������Ϣ **********************/
	/**
	 * ��ϵ��id
	 */
	private String id;
	/**
	 * ƴ���뺺�ֵĽ���塣
	 */
	private String sortKey;
	/**
	 * ͷ��Uri
	 */
	private String photoUri;
	/**
	 * ͷ��
	 */
	private byte[] photo;
	/**
	 * ��ע
	 */
	private String memo;
	/**
	 * �ǳ�
	 */
	private String nickname;
	/**
	 * ��ַ
	 */
	private ArrayList<String> websites;
	/**
	 * ����
	 */
	private String birthday;
	 
	/******************* DataKinds�ڲ�ģ�� *********************/

	/**
	 * ����
	 */
	private Name name;
	/**
	 * �绰
	 */
	private ArrayList<Phone> phones;
	/**
	 * �ʼ�
	 */
	private ArrayList<Email> emails;
	/**
	 * ��ַ
	 */
	private ArrayList<Address> address;
	/**
	 * ��֯
	 */
	private ArrayList<Organization> organizations;
	/**
	 * ��ʱͨ��
	 */
	private ArrayList<Im> ims;

	/******************* Call log ��� *********************/
	/**
	 * �Ƿ���ͨ����¼
	 */
	private boolean hasCallLog;
	/**
	 * �����ͨ����¼
	 */
	private CallLog lastCallLog;

	/**
	 * �Ƿ�ƥ��
	 */
	private boolean isMatchPinYin = false;

	public boolean isMatchPinYin() {
		return isMatchPinYin;
	}

	public void setMatchPinYin(boolean isMatchPinYin) {
		this.isMatchPinYin = isMatchPinYin;
	}

	@Override
	public String toString() {
		return "Contact [id=" + id + ", sortKey=" + sortKey + ", photoUri="
				+ photoUri + ", photo=" + Arrays.toString(photo) + ", memo="
				+ memo + ", nickname=" + nickname + ", websites=" + websites
				+ ", birthday=" + birthday +", name="
				+ name + ", phones=" + phones + ", emails=" + emails
				+ ", address=" + address + ", organizations=" + organizations
				+ ", ims=" + ims + ", hasCallLog=" + hasCallLog
				+ ", lastCallLog=" + lastCallLog + ", isMatchPinYin="
				+ isMatchPinYin + "]";
	}

	public boolean isHasCallLog() {
		return hasCallLog;
	}

	public void setHasCallLog(boolean hasCallLog) {
		this.hasCallLog = hasCallLog;
	}

	public CallLog getLastCallLog() {
		return lastCallLog;
	}

	public void setLastCallLog(CallLog lastCallLog) {
		this.lastCallLog = lastCallLog;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public String getPhotoUri() {
		return photoUri;
	}

	public void setPhotoUri(String photoUri) {
		this.photoUri = photoUri;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public ArrayList<String> getWebsites() {
		return websites;
	}

	public void setWebsites(ArrayList<String> websites) {
		this.websites = websites;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public ArrayList<Phone> getPhones() {
		return phones;
	}

	public void setPhones(ArrayList<Phone> phones) {
		this.phones = phones;
	}

	public ArrayList<Email> getEmails() {
		return emails;
	}

	public void setEmails(ArrayList<Email> emails) {
		this.emails = emails;
	}

	public ArrayList<Address> getAddress() {
		return address;
	}

	public void setAddress(ArrayList<Address> address) {
		this.address = address;
	}

	public ArrayList<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(ArrayList<Organization> organizations) {
		this.organizations = organizations;
	}

	public ArrayList<Im> getIms() {
		return ims;
	}

	public void setIms(ArrayList<Im> ims) {
		this.ims = ims;
	}

}
