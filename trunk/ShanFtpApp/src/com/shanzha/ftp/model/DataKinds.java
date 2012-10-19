package com.shanzha.ftp.model;

/**
 * ��������ģ��
 * <p>
 * �������֣����룬�ʼ�����ַ����֯����ʱͨ��
 * 
 * @author mty
 * @update ShanZha
 * @time 2012-10-11 15:16
 */
public class DataKinds {
	/**
	 * ��������ģ��
	 * 
	 * @author mty
	 * @time 2011-8-15����03:02:45
	 */
	private static class BaseTypes {
		/**
		 * ����
		 */
		private int type;
		/**
		 * ��ǩ
		 */
		private String label;
		/**
		 * ����
		 */
		private String body;

		@Override
		public String toString() {
			return "BaseTypes [label=" + label + ", type=" + type + ", body="
					+ body + "]";
		}

		protected String getLabel() {
			return label;
		}

		protected void setLabel(String label) {
			this.label = label;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		protected String getNumber() {
			return body;
		}

		protected void setNumber(String body) {
			this.body = body;
		}

	}

	/**
	 * ������Ϣģ��
	 * 
	 * @author mty
	 * @time 2011-8-16����10:24:42
	 */
	public static class Name {
		/**
		 * The name that should be used to display the contact
		 * <p>
		 * ��ʾ����
		 */
		private String displayName;

		/**
		 * The given name for the contact:First(Given)Name
		 * <p>
		 * ����
		 */
		private String givenName;

		/**
		 * The family name for the contact.
		 * <p>
		 * last name������
		 */
		private String familyName;

		/**
		 * The contact's honorific prefix, e.g. "Sir"
		 * <p>
		 * ǰ׺
		 */
		private String prefix;

		/**
		 * The contact's middle name
		 * <p>
		 * �м���
		 */
		private String middleName;

		/**
		 * The contact's honorific suffix, e.g. "Jr"
		 * <p>
		 * ��׺
		 */
		private String suffix;

		/**
		 * The phonetic version of the given name for the contact.
		 * <p>
		 * ����ƴ��
		 */
		private String phoneticGivenName;

		/**
		 * The phonetic version of the additional name for the contact
		 * <p>
		 * �м���ƴ��
		 */
		private String phoneticMiddleName;

		/**
		 * The phonetic version of the family name for the contact
		 * <p>
		 * ����ƴ����
		 */
		private String phoneticFamilyName;

		@Override
		public String toString() {
			return "Name [displayName=" + displayName + ", givenName="
					+ givenName + ", familyName=" + familyName + ", prefix="
					+ prefix + ", middleName=" + middleName + ", suffix="
					+ suffix + ", phoneticGivenName=" + phoneticGivenName
					+ ", phoneticMiddleName=" + phoneticMiddleName
					+ ", phoneticFamilyName=" + phoneticFamilyName + "]";
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public String getGivenName() {
			return givenName;
		}

		public void setGivenName(String givenName) {
			this.givenName = givenName;
		}

		public String getFamilyName() {
			return familyName;
		}

		public void setFamilyName(String familyName) {
			this.familyName = familyName;
		}

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getMiddleName() {
			return middleName;
		}

		public void setMiddleName(String middleName) {
			this.middleName = middleName;
		}

		public String getSuffix() {
			return suffix;
		}

		public void setSuffix(String suffix) {
			this.suffix = suffix;
		}

		/*
		 * һ��ƴ������ϵͳ��ά�������ÿ�������protected��������ʱ����Ҫʹ�ã����ṩ���á�
		 */
		protected String getPhoneticGivenName() {
			return phoneticGivenName;
		}

		protected void setPhoneticGivenName(String phoneticGivenName) {
			this.phoneticGivenName = phoneticGivenName;
		}

		protected String getPhoneticMiddleName() {
			return phoneticMiddleName;
		}

		protected void setPhoneticMiddleName(String phoneticMiddleName) {
			this.phoneticMiddleName = phoneticMiddleName;
		}

		protected String getPhoneticFamilyName() {
			return phoneticFamilyName;
		}

		protected void setPhoneticFamilyName(String phoneticFamilyName) {
			this.phoneticFamilyName = phoneticFamilyName;
		}

	}

	/***
	 * ��ַ��Ϣģ��
	 * 
	 * @author mty
	 * @time 2011-8-15����02:57:24
	 */
	public static class Address {
		/**
		 * ����
		 */
		private int type;
		/**
		 * ��ʾ��ַ����ʽ���ĵ�ַ��
		 */
		private String displayAddress;

		/**
		 * Can be street, avenue, road, etc. This element also includes the
		 * house number and room/apartment/flat/floor number.
		 * <p>
		 * �ֵ���С���š����ƺŵ�
		 */
		private String street;

		/**
		 * Covers actual P.O. boxes, drawers, locked bags, etc. This is usually
		 * but not always mutually exclusive with street.
		 * <p>
		 * ��������
		 */
		private String pobox;

		/**
		 * This is used to disambiguate ���������壩a street address when a city
		 * contains more than one street with the same name, or to specify a
		 * small place whose mail is routed through a larger postal town. In
		 * China it could be a county or a minor city.
		 * <p>
		 * �أ�����
		 */
		private String neighborhood;

		/**
		 * Can be city, village, town, borough, etc. This is the postal town and
		 * not necessarily the place of residence or place of business.
		 * <p>
		 * ����
		 */
		private String city;

		/**
		 * A state, province, county (in Ireland), Land (in Germany),
		 * departement (in France), etc.
		 * <p>
		 * ʡ��ֱϽ�С�������
		 */
		private String region;

		/**
		 * Postal code. Usually country-wide, but sometimes specific to the city
		 * (e.g. "2" in "Dublin 2, Ireland" addresses).
		 * <p>
		 * ��������
		 */
		private String postcode;

		/**
		 * The name or code of the country.
		 * <p>
		 * ����
		 */
		private String country;
		/**
		 * custom label
		 */
		private String label;

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return "Address [type=" + type + ", displayAddress="
					+ displayAddress + ", street=" + street + ", pobox="
					+ pobox + ", neighborhood=" + neighborhood + ", city="
					+ city + ", region=" + region + ", postcode=" + postcode
					+ ", country=" + country + ", label=" + label + "]";
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getDisplayAddress() {
			return displayAddress;
		}

		public void setDisplayAddress(String displayAddress) {
			this.displayAddress = displayAddress;
		}

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public String getPobox() {
			return pobox;
		}

		public void setPobox(String pobox) {
			this.pobox = pobox;
		}

		public String getNeighborhood() {
			return neighborhood;
		}

		public void setNeighborhood(String neighborhood) {
			this.neighborhood = neighborhood;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getRegion() {
			return region;
		}

		public void setRegion(String region) {
			this.region = region;
		}

		public String getPostcode() {
			return postcode;
		}

		public void setPostcode(String postcode) {
			this.postcode = postcode;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

	}

	/**
	 * �ֻ�������Ϣģ��
	 * 
	 * @author mty
	 * @time 2011-8-15����03:14:21
	 */
	public static class Phone extends BaseTypes {
		public String getNumber() {
			return super.getNumber();
		}

		public void setNumber(String num) {
			super.setNumber(num);
		}
	}

	/**
	 * �ʼ���Ϣģ��
	 * 
	 * @author mty
	 * @time 2011-8-15����04:46:56
	 */
	public static class Email extends BaseTypes {
		public String getLabel() {
			return super.getLabel();
		}

		public void setLabel(String label) {
			super.setLabel(label);
		}

		public String getEmail() {
			return super.getNumber();
		}

		public void setEmail(String email) {
			super.setNumber(email);
		}
	}

	/**
	 * ��֯�ṹ��Ϣģ��
	 * 
	 * @author mty
	 * @time 2011-8-15����05:55:24
	 */
	public static class Organization extends BaseTypes {
		/**
		 * The position title at this company as the user entered it.
		 * <P>
		 * ְλ
		 */
		private String title;

		@Override
		public String toString() {
			return "Organization [title=" + title + "]";
		}

		public String getLabel() {
			return super.getLabel();
		}

		public void setLabel(String label) {
			super.setLabel(label);
		}

		public String getCompany() {
			return super.getNumber();
		}

		public void setCompany(String company) {
			super.setNumber(company);
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}

	/**
	 * ��ʱ��ͨ��Ϣ��Ϣģ��
	 * 
	 * @author mty
	 * @time 2011-8-15����03:02:45
	 */
	public static class Im extends BaseTypes {
		public String getLabel() {
			return super.getLabel();
		}

		public void setLabel(String label) {
			super.setLabel(label);
		}

		public String getIm() {
			return super.getNumber();
		}

		public void setIm(String im) {
			super.setNumber(im);
		}
	}

}
