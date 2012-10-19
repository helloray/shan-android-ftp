package com.shanzha.ftp.model;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;


import android.graphics.Bitmap;

/**
 * 本地文件对象
 * 
 * @author hmy
 * @author ShanZha
 * @time 2012-10-15 13:52 
 */
public class FileItem implements Comparable<FileItem> {
	/**
	 * 文件名
	 */
	private String name;
	/**
	 * 文件路径
	 */
	private String path;
	/**
	 * 文件大小
	 */
	private long length;
	/**
	 * 是否是文件
	 */
	private boolean isFile;
	/**
	 * 是否可读
	 */
	private boolean isRead;
	/**
	 * 是否可写
	 */
	private boolean isWrite;

	public FileItem(String name, String path, boolean isFile, Bitmap icon,
			boolean isRead, boolean isWrite) {
		this.name = name;
		this.path = path;
		this.isFile = isFile;
		this.isRead = isRead;
		this.isWrite = isWrite;
	}

	public FileItem() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public boolean isWrite() {
		return isWrite;
	}

	public void setWrite(boolean isWrite) {
		this.isWrite = isWrite;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * 获取该file下的子flies
	 * 
	 * @author hmy
	 * @author ShanZha
	 * @return
	 * @time 2012-10-15 15:57
	 */
	public ArrayList<FileItem> getSubFiles() {
		if (isFile) {
			return null;
		}

		File file = new File(path);
		File[] files = file.listFiles();
		if (files == null) {
			return new ArrayList<FileItem>();
		}
		if (!isRead) {
			return null;
		}

		ArrayList<FileItem> lists = new ArrayList<FileItem>();
		for (File fileItem : files) {
			String filename = fileItem.getName();
			//排除以"."开始的系统文件夹和名字为空的
			if(filename.startsWith(".")
					||filename.equals(""))
			{
				continue;
			}
			FileItem item = new FileItem();
			item.name = fileItem.getName();
			item.path = fileItem.getPath();
			item.isRead = fileItem.canRead();
			item.isWrite = fileItem.canWrite();
			item.length = fileItem.length();

			if (fileItem.isFile()) {
				item.isFile = true;
			} else {
				item.isFile = false;
			}
			lists.add(item);
		}
		return lists;
	}

	@Override
	public int compareTo(FileItem another) {
		if (!isFile && another.isFile) {
			return -1;
		} else if (isFile && !another.isFile) {
			return 1;
		} else if(isFile && another.isFile){
			Collator cmp = Collator.getInstance(java.util.Locale.getDefault());
			return cmp.compare(name,another.getName());
		} else if(!isFile && !another.isFile) {
			Collator cmp = Collator.getInstance(java.util.Locale.getDefault());
			return cmp.compare(name,another.getName());
		}else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
