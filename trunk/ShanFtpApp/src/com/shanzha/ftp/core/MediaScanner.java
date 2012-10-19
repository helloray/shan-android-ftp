package com.shanzha.ftp.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.shanzha.ftp.activity.FileUploadActivity;
import com.shanzha.ftp.inter.ILoadBaseListener;
import com.shanzha.ftp.inter.ILoadPicListener;
import com.shanzha.ftp.util.FileUtil;

/**
 * 扫描器类，在此主要是扫描sdcard里所有的图片
 * 
 * @author ShanZha
 * @date 2012-10-16 9:50
 * 
 */
public class MediaScanner {

	private static final String TAG = "MediaScanner";
	/**
	 * 扫描到的所有图片的路径集合
	 */
	private List<String> picPathList;
	/**
	 * 所要扫描的路径的集合（类似队列）
	 */
	private List<String> queue;
	/**
	 * 加载完成的监听
	 */
	private ILoadPicListener listener;

	public MediaScanner(String rootPath) {
		picPathList = new ArrayList<String>();
		// 创建一个同步的集合（由于删除和添加操作比较多，因此用LinkedList）
		queue = Collections.synchronizedList(new LinkedList<String>());
		queue.add(rootPath);
	}
	
	/**
	 * 开始扫描的入口
	 */
	public void startScanning()
	{
		new Thread(new ScannerRunnable(queue.get(0))).start();
	}

	/**
	 * 扫描的实现类
	 * @author ShanZha
	 * @date 2012-10-16 14:05
	 *
	 */
	private class ScannerRunnable implements Runnable {
		
		private static final String TAG = "MediaScanner&ScannerRunnable";
		private String path;

		public ScannerRunnable(String path) {
			this.path = path;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try
			{
				synchronized (queue) {
					if(null!=path)
					{
						if(null!=listener)
						{
							listener.onLoadPreparation(ILoadBaseListener.TYPE_PIC);
						}
						File tempFile = new File(path);
						File[] fileList = tempFile.listFiles();
						if(null!=fileList)
						{
							for(File file:fileList)
							{
								String absolutePath = file.getAbsolutePath();
								String filename = file.getName();
								if(!file.canRead())
								{
									continue;
								}
//								Log.i(TAG," absolutePath = "+absolutePath
//										+" path = "+file.getPath()+" name = "+filename);
								if(file.isDirectory()&&!filename.startsWith("."))
								{
									if(!queue.contains(absolutePath))
									{
										queue.add(absolutePath);
									}
								}else if(file.isFile())
								{
									if(FileUtil.isPic(filename))
									{
										picPathList.add(absolutePath);
									}
								}
							}
							//遍历完第一个路径下的所有文件，即移除
							queue.remove(0);
							if(queue.size()==0)
							{
								//完成
								Log.i(TAG,"scanner completed");
								if(null!=listener)
								{
									listener.onLoadPicCompleted(picPathList);
								}
							}else
							{
								startScanning();
							}
						}
					}
				}
			}catch(Exception e)
			{
				if(null!=listener)
				{
					listener.onLoadError(e.getMessage(),ILoadBaseListener.TYPE_PIC);
				}
				e.printStackTrace();
			}
			
			
		}

	}

	public ILoadPicListener getListener() {
		return listener;
	}

	public void setListener(ILoadPicListener listener) {
		this.listener = listener;
	}
	
}
