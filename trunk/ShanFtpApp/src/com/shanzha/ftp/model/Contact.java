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
 * 联系人模型
 * <p>
 * 包含全部信息，id，头像，备注，名字，号码，邮件，地址，组织，即时通信等...
 * 
 * @author mty
 * @update ShanZha
 * @time 2012-10-11 15:15
 */
public class Contact {
	/******************* 基础信息 **********************/
	/**
	 * 联系人id
	 */
	private String id;
	/**
	 * 拼音与汉字的结合体。
	 */
	private String sortKey;
	/**
	 * 头像Uri
	 */
	private String photoUri;
	/**
	 * 头像
	 */
	private byte[] photo;
	/**
	 * 备注
	 */
	private String memo;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 网址
	 */
	private ArrayList<String> websites;
	/**
	 * 生日
	 */
	private String birthday;
	 
	/******************* DataKinds内部模型 *********************/

	/**
	 * 名字
	 */
	private Name name;
	/**
	 * 电话
	 */
	private ArrayList<Phone> phones;
	/**
	 * 邮件
	 */
	private ArrayList<Email> emails;
	/**
	 * 地址
	 */
	private ArrayList<Address> address;
	/**
	 * 组织
	 */
	private ArrayList<Organization> organizations;
	/**
	 * 即时通信
	 */
	private ArrayList<Im> ims;

	/******************* Call log 相关 *********************/
	/**
	 * 是否有通话记录
	 */
	private boolean hasCallLog;
	/**
	 * 最近的通话记录
	 */
	private CallLog lastCallLog;

	/**
	 * 是否匹配
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
