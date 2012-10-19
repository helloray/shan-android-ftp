package com.shanzha.ftp.model;

import java.util.List;

/**
 * 一个网盘对象
 * @author ShanZha
 * @date 2012-9-28 17:10
 */
public class Disc {

	private List<FTPFile> allFiles;
	private List<FTPFile> files;
	private List<FTPFile> directories;
	private String path;
	private String parentPath;
	private int nodes;
	public List<FTPFile> getAllFiles() {
		return allFiles;
	}
	public void setAllFiles(List<FTPFile> allFiles) {
		this.allFiles = allFiles;
	}
	public List<FTPFile> getFiles() {
		return files;
	}
	public void setFiles(List<FTPFile> files) {
		this.files = files;
	}
	public List<FTPFile> getDirectories() {
		return directories;
	}
	public void setDirectories(List<FTPFile> directories) {
		this.directories = directories;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getParentPath() {
		return parentPath;
	}
	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}
	public int getNodes() {
		return nodes;
	}
	public void setNodes(int nodes) {
		this.nodes = nodes;
	}
	
}
