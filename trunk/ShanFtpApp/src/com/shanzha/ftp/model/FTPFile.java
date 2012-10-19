package com.shanzha.ftp.model;
/**
 * FTP服务器上的文件模型
 * @author ShanZha
 * @date 2012-9-26 15:33
 *
 */
public class FTPFile {

	public static final int TYPE_FILE = 0;
	public static final int TYPE_DIRECTORY = 1;

	private int type;
	private long size;
	private String name;
	private String time;
	private String permission;
	private String remotePath;
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public boolean isDirectory() {
		return type == TYPE_DIRECTORY;
	}

	public boolean isFile() {
		return type == TYPE_FILE;
	}
	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
}
