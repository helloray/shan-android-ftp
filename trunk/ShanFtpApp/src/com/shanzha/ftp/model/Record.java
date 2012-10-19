package com.shanzha.ftp.model;

/**
 * 已经上传/下载的基本类型
 * 
 * @author ShanZha
 * @data 2012-10-12 11:07
 * 
 */
public class Record {

	/**
	 * 数据库里标识
	 */
	private int id;
	/**
	 * 唯一标识
	 * 上传--本地完整路径（包括文件名）
	 * 下载--远程完整路径（包括文件名）
	 */
	private String uri;
	/**
	 * 本地目录，不包括文件名
	 */
	private String localDir;
	/**
	 * 远程目录，不包括文件名
	 */
	private String remoteDir;
	/**
	 * 文件名
	 */
	private String filename;
	/**
	 * 时间
	 */
	private String time;
	/**
	 * 标示Record类型-- 1：已上传 2：已下载
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
