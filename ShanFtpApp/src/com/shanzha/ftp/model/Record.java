package com.shanzha.ftp.model;

/**
 * �Ѿ��ϴ�/���صĻ�������
 * 
 * @author ShanZha
 * @data 2012-10-12 11:07
 * 
 */
public class Record {

	/**
	 * ���ݿ����ʶ
	 */
	private int id;
	/**
	 * Ψһ��ʶ
	 * �ϴ�--��������·���������ļ�����
	 * ����--Զ������·���������ļ�����
	 */
	private String uri;
	/**
	 * ����Ŀ¼���������ļ���
	 */
	private String localDir;
	/**
	 * Զ��Ŀ¼���������ļ���
	 */
	private String remoteDir;
	/**
	 * �ļ���
	 */
	private String filename;
	/**
	 * ʱ��
	 */
	private String time;
	/**
	 * ��ʾRecord����-- 1�����ϴ� 2��������
	 */
	private int type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getLocalDir() {
		return localDir;
	}

	public void setLocalDir(String localDir) {
		this.localDir = localDir;
	}

	public String getRemoteDir() {
		return remoteDir;
	}

	public void setRemoteDir(String remoteDir) {
		this.remoteDir = remoteDir;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isUploadRecord() {
		return type == TYPE_UPLOAD_RECORD;
	}

	public boolean isDownloadRecord() {
		return type == TYPE_DOWNLOAD_RECORD;
	}

	public static final int TYPE_UPLOAD_RECORD = 1;
	public static final int TYPE_DOWNLOAD_RECORD = 2;
}
