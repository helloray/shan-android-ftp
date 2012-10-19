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
 * ɨ�����࣬�ڴ���Ҫ��ɨ��sdcard�����е�ͼƬ
 * 
 * @author ShanZha
 * @date 2012-10-16 9:50
 * 
 */
public class MediaScanner {

	private static final String TAG = "MediaScanner";
	/**
	 * ɨ�赽������ͼƬ��·������
	 */
	private List<String> picPathList;
	/**
	 * ��Ҫɨ���·���ļ��ϣ����ƶ��У�
	 */
	private List<String> queue;
	/**
	 * ������ɵļ���
	 */
	private ILoadPicListener listener;

	public MediaScanner(String rootPath) {
		picPathList = new ArrayList<String>();
		// ����һ��ͬ���ļ��ϣ�����ɾ������Ӳ����Ƚ϶࣬�����LinkedList��
		queue = Collections.synchronizedList(new LinkedList<String>());
		queue.add(rootPath);
	}
	
	/**
	 * ��ʼɨ������
	 */
	public void startScanning()
	{
		new Thread(new ScannerRunnable(queue.get(0))).start();
	}

	/**
	 * ɨ���ʵ����
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
							//�������һ��·���µ������ļ������Ƴ�
							queue.remove(0);
							if(queue.size()==0)
							{
								//���
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
