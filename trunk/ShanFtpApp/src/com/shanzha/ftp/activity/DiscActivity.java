package com.shanzha.ftp.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shanzha.ftp.R;
import com.shanzha.ftp.adapter.DiscAdapter;
import com.shanzha.ftp.core.FTPManager;
import com.shanzha.ftp.core.FtpApp;
import com.shanzha.ftp.dao.FTPDBService;
import com.shanzha.ftp.inter.IBase;
import com.shanzha.ftp.inter.IFTPBaseListener;
import com.shanzha.ftp.inter.IFTPDataListener;
import com.shanzha.ftp.inter.IFTPDataMultiListener;
import com.shanzha.ftp.inter.IFTPGetFilesListener;
import com.shanzha.ftp.model.Disc;
import com.shanzha.ftp.model.FTPFile;
import com.shanzha.ftp.model.Record;
import com.shanzha.ftp.util.Constant;
import com.shanzha.ftp.util.DialogUtil;
import com.shanzha.ftp.util.FileUtil;
import com.shanzha.ftp.util.StringUtil;
import com.shanzha.ftp.util.ToastUtils;

/**
 * 网盘管理界面
 * 
 * @author ShanZha
 * @date 2012-9-28 9:49
 * 
 */
public class DiscActivity extends Activity implements IBase, OnClickListener,
		DialogInterface.OnClickListener, OnItemClickListener,
		IFTPGetFilesListener, IFTPDataListener, IFTPDataMultiListener {

	private static final String TAG = "DiscActivity";
	/** 返回按钮 **/
	private Button mBtnBack;
	/** Title **/
	private TextView mTvTitle;
	/** 下载按钮 **/
	private Button mBtnDownload;
	/** ListView **/
	private ListView mListView;
	/** 底部布局 **/
	private RelativeLayout mRlBottom;
	/** 全选按钮 **/
	private Button mBtnCheckAll;
	/** 确定按钮 **/
	private Button mBtnOk;
	/** 反选按钮 **/
	private Button mBtnCheckReserve;
	/** 获取文件结构的等待框 **/
	private ProgressDialog mPdGetFiles;
	/** 下载的等待框 **/
	private ProgressDialog mPdDownload;
	/** FTP请求管理者 ***/
	private FTPManager mFtpMgr;
	/** 当前网盘路径 **/
	private String mCurrRemoteDir;
	/** 网盘缓存 **/
	private Stack<Disc> mCacheDisc = new Stack<Disc>();
	/** 数据适配器 **/
	private DiscAdapter mAdaper;
	/** 当前展示数据的集合 **/
	private List<FTPFile> mDataList;
	/** 当前正在下载的文件对象 **/
	private Record mCurrDownload;
	// /** 当前正在下载的文件已经下载的长度(单个文件)（多个文件交给FTPClient控制） **/
	private int mCurrTransferedLength;
	/** 当前正在下载的文件总长度(单个/多个) **/
	private long mCurrDownloadTotalSize;
	/** 当前正在下载的文件总个数(多个) **/
	private int mCurrDownloadTotalCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.disc);

		initUI();
		initListener();
		initRes();
	}

	@Override
	public void initUI() {
		// TODO Auto-generated method stub
		mBtnBack = (Button) this.findViewById(R.id.disc_btn_back);
		mTvTitle = (TextView) this.findViewById(R.id.disc_tv_title);
		mBtnDownload = (Button) this.findViewById(R.id.disc_btn_download);
		mListView = (ListView) this.findViewById(R.id.disc_listview);
		mRlBottom = (RelativeLayout) this.findViewById(R.id.disc_rl_bottom);
		mBtnCheckAll = (Button) this
				.findViewById(R.id.disc_btn_bottom_allcheck);
		mBtnOk = (Button) this.findViewById(R.id.disc_btn_bottom_ok);
		mBtnCheckReserve = (Button) this
				.findViewById(R.id.disc_btn_bottom_nocheck);
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		mBtnBack.setOnClickListener(this);
		mBtnDownload.setOnClickListener(this);
		mBtnCheckAll.setOnClickListener(this);
		mBtnOk.setOnClickListener(this);
		mBtnCheckReserve.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void initRes() {
		// TODO Auto-generated method stub

		mDataList = new ArrayList<FTPFile>();

		mPdGetFiles = DialogUtil.getWaittingProDialog(this, "", "请稍等...");
		mPdDownload = DialogUtil.getHorizontalWaittingProDialog(this, "下载提示信息",
				"");

		mPdGetFiles.setButton("取消", mClickCancelListener);
		mPdGetFiles.setOnCancelListener(mDialogCancelListener);
		mPdDownload.setButton("取消", mClickCancelListener);
		mPdDownload.setOnCancelListener(mDialogCancelListener);

		mFtpMgr = FTPManager.getInstance();
		mCurrRemoteDir = Constant.FTP_ROOT;
		// mCurrRemoteDir = "/iPhone Simulator/PIC";
		mTvTitle.setText(mCurrRemoteDir);

		startGetFiles();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		return super.onCreateDialog(id);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.disc_btn_back:// 返回
			onBack();
			break;
		case R.id.disc_btn_download:// 下载
			setDownloadModel(true);
			break;
		case R.id.disc_btn_bottom_allcheck:// 全选
			onCheckAll();
			break;
		case R.id.disc_btn_bottom_nocheck:// 反选
			onCheckReverse();
			break;
		case R.id.disc_btn_bottom_ok:// 确定下载
			onDownloadCheck();
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		setDownloadModel(false);
		FTPFile file = mDataList.get(position);
		if (file.isDirectory()) {
			Disc disc = new Disc();
			disc.setPath(mCurrRemoteDir);
			List<FTPFile> tempList = new ArrayList<FTPFile>();
			tempList.addAll(mDataList);
			disc.setAllFiles(tempList);
			mCacheDisc.push(disc);
			if (!Constant.FTP_ROOT.equals(mCurrRemoteDir)) {
				mCurrRemoteDir = mCurrRemoteDir + Constant.SEPERATOR
						+ file.getName();
			} else {
				mCurrRemoteDir = mCurrRemoteDir + file.getName();
			}
			mTvTitle.setText(mCurrRemoteDir);
			startGetFiles();
		} else if (file.isFile()) {
			mCurrDownloadTotalSize = file.getSize();
			onDownloadSingle(file.getName());
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		onBack();
	}

	/**
	 * 获取文件--本地方法
	 */
	private void startGetFiles() {
		DialogUtil.showWaittingDialog(mPdGetFiles);
		try {
			mFtpMgr.getFilesByDir(mCurrRemoteDir, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "获取文件出错喽");
			mHandler.sendEmptyMessage(Constant.MSG_GETFILES_ERROR);
			e.printStackTrace();
		}
	}

	/**
	 * 下载单个文件--本地方法
	 * 
	 * @param filename
	 */
	private void onDownloadSingle(String filename) {
		try {
			String localPath = FileUtil.getStoragePathByFilename(filename);
			if (FileUtil.isExist(filename, localPath)) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				String type = FileUtil.getMIMEType(filename);
				// intent.setDataAndType(Uri.parse(localPath+Constant.SEPERATOR+filename),
				// type);
				File file = new File(localPath + Constant.SEPERATOR + filename);
				intent.setDataAndType(Uri.fromFile(file), type);
				startActivity(intent);
			} else {
				DialogUtil.showWaittingDialog(mPdDownload);

				mCurrDownload = new Record();
				String uri = "";
				if (Constant.FTP_ROOT.equals(mCurrRemoteDir)) {
					uri = mCurrRemoteDir + filename;
				} else {
					uri = mCurrRemoteDir + Constant.SEPERATOR + filename;
				}
				mCurrDownload.setType(Record.TYPE_DOWNLOAD_RECORD);
				mCurrDownload.setUri(uri);
				mCurrDownload.setFilename(filename);
				mCurrDownload.setLocalDir(localPath);
				mCurrDownload.setRemoteDir(mCurrRemoteDir);
				mCurrDownload.setTime(StringUtil.formatDate(
						System.currentTimeMillis(), "yyyy-MM--dd hh:mm"));
				mFtpMgr.download(mCurrDownload, this);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "出错了，出错了");
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_ERROR);
			e.printStackTrace();
		}
	}

	/**
	 * 点击“返回”
	 */
	private void onBack() {
		if (mCacheDisc.size() == 0) {
			this.finish();
			return;
		}
		Disc disc = mCacheDisc.pop();
		List<FTPFile> tempList = disc.getAllFiles();
		if (null != mDataList && null != tempList) {
			mDataList.clear();
			mDataList.addAll(tempList);
		}
		// mDataList = disc.getAllFiles();
		mCurrRemoteDir = disc.getPath();
		mTvTitle.setText(mCurrRemoteDir);

		setDownloadModel(false);
		mAdaper.getmCheckMap().clear();

		mHandler.sendEmptyMessage(Constant.MSG_NOTIFY_DATA_CHANGED);
	}

	/**
	 * 设置“下载”
	 * 
	 * @param isClick
	 *            是否是点击“下载”按钮
	 */
	private void setDownloadModel(boolean isClick) {
		boolean isDownloadModel = mAdaper.isDownloadModel();
		if (isClick) {
			if (isDownloadModel) {
				mBtnDownload.setText("下载");
				mRlBottom.setVisibility(View.GONE);
				mAdaper.setDownloadModel(false);
			} else {
				mAdaper.setDownloadModel(true);
				mBtnDownload.setText("取消");
				mRlBottom.setVisibility(View.VISIBLE);
			}
		} else {
			if (View.VISIBLE == mRlBottom.getVisibility()) {
				mRlBottom.setVisibility(View.GONE);
				mBtnDownload.setText("下载");
				mAdaper.setDownloadModel(false);
			}
		}
	}

	/**
	 * 点击“全选”，仅仅限于文件，不包括文件夹
	 */
	private void onCheckAll() {
		Map<Integer, Boolean> checkMap = mAdaper.getmCheckMap();
		if (null == checkMap) {
			return;
		}
		checkMap.clear();
		for (int i = 0; i < mDataList.size(); i++) {
			FTPFile file = mDataList.get(i);
			if (file.isFile()) {
				checkMap.put(i, true);
			}
		}
		if (checkMap.size() == 0) {
			ToastUtils.show(this, "此目录下没有文件可下载", false);
		} else {
			mHandler.sendEmptyMessage(Constant.MSG_NOTIFY_DATA_CHANGED);
		}
	}

	/**
	 * 点击“反选”，仅仅针对文件
	 */
	private void onCheckReverse() {
		Map<Integer, Boolean> checkMap = mAdaper.getmCheckMap();
		if (null == checkMap) {
			return;
		}
		// Set<Integer> set = checkMap.keySet();
		// Iterator<Integer> it = set.iterator();
		// while(it.hasNext())
		// {
		// int key = it.next();
		// boolean value = checkMap.get(key);
		// if(value)
		// {
		// // checkMap.remove(key);
		// it.remove();
		// }else
		// {
		// checkMap.put(key, value);
		// }
		// }
		for (int i = 0; i < mDataList.size(); i++) {
			if (checkMap.containsKey(i) && checkMap.get(i)) {
				checkMap.remove(i);
			} else {
				checkMap.put(i, true);
			}
		}
		mHandler.sendEmptyMessage(Constant.MSG_NOTIFY_DATA_CHANGED);

	}

	/**
	 * 点击“确定下载”--下载所选中的文件（至少一个）
	 */
	public void onDownloadCheck() {
		Map<Integer, Boolean> checkMap = mAdaper.getmCheckMap();
		if (null == checkMap) {
			return;
		}
		if (checkMap.size() <= 0) {
			ToastUtils.show(this, "请您选择所要下载的文件", false);
			return;
		}
		DialogUtil.showWaittingDialog(mPdDownload);
		Map<String, String> downloadedMap = FtpApp.getInstance()
				.getmMapDownloaded();
		List<Record> downloadList = new ArrayList<Record>();
		Set<Integer> set = checkMap.keySet();
		Iterator<Integer> it = set.iterator();
		while (it.hasNext()) {
			int key = it.next();
			boolean value = checkMap.get(key);
			// 选中并且没有下载过
			if (value && !downloadedMap.containsKey(key)) {
				FTPFile file = mDataList.get(key);
				Record download = new Record();
				String filename = file.getName();
				String localPath = FileUtil.getStoragePathByFilename(filename);
				String uri = "";
				if (Constant.FTP_ROOT.equals(mCurrRemoteDir)) {
					uri = mCurrRemoteDir + filename;
				} else {
					uri = mCurrRemoteDir + Constant.SEPERATOR + filename;
				}
				download.setUri(uri);
				download.setType(Record.TYPE_DOWNLOAD_RECORD);
				download.setFilename(filename);
				download.setLocalDir(localPath);
				download.setRemoteDir(mCurrRemoteDir);
				download.setTime(StringUtil.formatDate(
						System.currentTimeMillis(), "yyyy-MM-dd hh:mm"));
				downloadList.add(download);

				mCurrDownloadTotalSize += file.getSize();
			}
		}
		if (downloadList.size() <= 0) {
			ToastUtils.show(this, "没有新要上传的文件", false);
			return;
		}
		mCurrDownloadTotalCount = downloadList.size();
		try {
			mFtpMgr.downloadMulti(downloadList, mCurrRemoteDir, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "下载多个文件出错喽");
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_MULTI_ERROR);
			e.printStackTrace();
		}
	}

	/**
	 * 重置
	 */
	private void reset() {
		mPdDownload.setProgress(0);
		mCurrDownload = null;
		mCurrDownloadTotalSize = 0;
		mCurrDownloadTotalCount = 0;
		mCurrTransferedLength = 0;
		if (null != mAdaper && null != mAdaper.getmCheckMap()) {
			mAdaper.getmCheckMap().clear();
			mHandler.sendEmptyMessage(Constant.MSG_NOTIFY_DATA_CHANGED);
		}
	}

	/**
	 * 终止当前数据请求（正在请求时，用户主动取消）
	 */
	private void abortCurrDataRequest() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					mFtpMgr.abortCurrDataConnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 用户点击ProgressDialog上的“取消”按钮时的监听
	 */
	private DialogInterface.OnClickListener mClickCancelListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			abortCurrDataRequest();
		}
	};
	/**
	 * 用户按返回键时，ProgressDialog取消的监听
	 */
	private DialogInterface.OnCancelListener mDialogCancelListener = new OnCancelListener() {

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			abortCurrDataRequest();
		}
	};
	/**
	 * 处理各种消息事件
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.MSG_NOTIFY_DATA_CHANGED:// 通知数据改变
				if (null == mAdaper) {
					mAdaper = new DiscAdapter(mDataList, DiscActivity.this);
					mListView.setAdapter(mAdaper);
				} else {
					mAdaper.notifyDataSetChanged();
				}
				break;
			case Constant.MSG_GETFILES_START:// 获取文件--开始

				break;
			case Constant.MSG_GETFILES_ERROR:// 获取文件--出错
				DialogUtil.dismissWaittingDialog(mPdGetFiles);
				ToastUtils.show(DiscActivity.this, "获取文件失败", false);
				break;
			case Constant.MSG_GETFILES_COMPLETE:// 获取文件--已完成
				DialogUtil.dismissWaittingDialog(mPdGetFiles);
				// mDataList = (List<FTPFile>)msg.obj;
				List<FTPFile> tempList = (List<FTPFile>) msg.obj;
				if (null != mDataList && null != tempList) {
					mDataList.clear();
					mDataList.addAll(tempList);
				}
				this.sendEmptyMessage(Constant.MSG_NOTIFY_DATA_CHANGED);
				break;
			case Constant.MSG_DOWNLOAD_START:// 下载--单个文件--开始

				break;
			case Constant.MSG_DOWNLOAD_TRANSFERRED:// 下载--单个文件--已下载多少
				// 与下载多个文件不同，这里的len是指每次下载的长度，并不是总供下载的长度（自己控制）
				int transferedLength = (Integer) msg.obj;
				mCurrTransferedLength += transferedLength;
				int progress = (int) (mCurrTransferedLength * (100.0 / (float) mCurrDownloadTotalSize));
				mPdDownload.setProgress(progress);
				break;
			case Constant.MSG_DOWNLOAD_ERROR:// 下载--单个文件--出错
				DialogUtil.dismissWaittingDialog(mPdDownload);
				ToastUtils.show(DiscActivity.this, "下载文件出错", false);
				reset();
				break;
			case Constant.MSG_DOWNLOAD_COMPLETE:// 下载--单个文件--已完成
				DialogUtil.dismissWaittingDialog(mPdDownload);
				// 持久化已下载--数据库
				FTPDBService ftpDB = FTPDBService
						.getInstance(DiscActivity.this);
				ftpDB.saveRecord(mCurrDownload);
				// 放入已下载--内存
				String key = mCurrDownload.getUri();
				FtpApp.getInstance().getmMapDownloaded()
						.put(key, mCurrDownload.getFilename());
				reset();
				break;
			case Constant.MSG_DOWNLOAD_MULTI_START:// 下载--多个文件--开始
				Log.i(TAG, "多个文件下载开始...");
				break;
			case Constant.MSG_DOWNLOAD_MULTI_TRANSFERED:// 下载--多个文件--已下载多少
				// 与下载单个文件不同，这里的len是指已经下载长度（在FTPClient里控制）
				int multiLen = (Integer) msg.obj;
				int multiProgress = (int) (multiLen * (100.0 / (float) mCurrDownloadTotalSize));
				mPdDownload.setProgress(multiProgress);
				// Log.i(TAG, " multiFile downloaded length = " + multiLen
				// + " progress = " + multiProgress);
				break;
			case Constant.MSG_DOWNLOAD_MULTI_ONEFILE_COMPLETE:// 下载--多个文件--其中一个完成
				Record download = (Record) msg.obj;
				Log.i(TAG, "多个下载中的单个完成 filename = " + download.getFilename()
						+ " localPath = " + download.getLocalDir());
				// 持久化已下载--数据库
				FTPDBService ftpDB2 = FTPDBService
						.getInstance(DiscActivity.this);
				ftpDB2.saveRecord(download);
				// 放入已下载--内存
				String key2 = download.getUri();
				FtpApp.getInstance().getmMapDownloaded()
						.put(key2, download.getFilename());
				break;
			case Constant.MSG_DOWNLOAD_MULTI_ERROR:// 下载--多个文件--出错
				DialogUtil.dismissWaittingDialog(mPdDownload);
				ToastUtils.show(DiscActivity.this, "下载 "
						+ mCurrDownloadTotalCount + " 个文件出错", false);
				reset();
				break;
			case Constant.MSG_DOWNLOAD_MULTI_COMPLETE:// 下载--多个文件--完成
				DialogUtil.dismissWaittingDialog(mPdDownload);
				ToastUtils.show(DiscActivity.this, "下载 "
						+ mCurrDownloadTotalCount + " 个文件完成", true);
				reset();
				break;
			}
		}

	};

	@Override
	public void onRequestFtpDataStart(int type) {
		// TODO Auto-generated method stub
		// 此页面没有上传，所以在这不考虑（@see MainActivity）
		if (type == IFTPBaseListener.TYPE_GETFILES)// 获取文件以及文件夹信息--开始
		{
			mHandler.sendEmptyMessage(Constant.MSG_GETFILES_START);
		} else if (type == IFTPBaseListener.TYPE_DOWNLOAD)// 下载--单个文件--开始
		{
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_START);
		} else if (type == IFTPBaseListener.TYPE_DOWNLOAD_MULTI)// 下载--多个文件--开始
		{
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_MULTI_START);
		}
	}

	@Override
	public void onRequestFtpDataError(String errorMsg, int type) {
		// TODO Auto-generated method stub
		// 此页面没有上传，所以在这不考虑（@see MainActivity）
		if (type == IFTPBaseListener.TYPE_GETFILES)// 获取文件以及文件夹信息--出错
		{
			mHandler.sendEmptyMessage(Constant.MSG_GETFILES_ERROR);
		} else if (type == IFTPBaseListener.TYPE_DOWNLOAD)// 下载--单个文件--出错
		{
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_ERROR);
		} else if (type == IFTPBaseListener.TYPE_DOWNLOAD_MULTI)// 下载--多个文件--出错
		{
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_MULTI_ERROR);
		}
	}

	@Override
	public void onRequestFtpDataOneFileCompleted(Record record, int type) {
		// TODO Auto-generated method stub
		// 此页面没有上传，所以在这不考虑（@see MainActivity）
		if (type == IFTPBaseListener.TYPE_DOWNLOAD_MULTI)// 下载--多个文件--其中一个完成
		{
			Message msg = mHandler.obtainMessage();
			msg.what = Constant.MSG_DOWNLOAD_MULTI_ONEFILE_COMPLETE;
			msg.obj = record;
			msg.sendToTarget();
		} else if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI)// 上传--多个文件--其中一个完成
		{
			// 在此界面暂时没有上传功能
		}
	}

	@Override
	public void onRequestFtpDataTransfered(int transeredLength, int type) {
		// TODO Auto-generated method stub
		// 此页面没有上传，所以在这不考虑（@see MainActivity）
		Message msg = mHandler.obtainMessage();
		msg.obj = transeredLength;
		if (type == IFTPBaseListener.TYPE_DOWNLOAD)// 下载--单个文件--已下载多少
		{
			msg.what = Constant.MSG_DOWNLOAD_TRANSFERRED;
		} else if (type == IFTPBaseListener.TYPE_DOWNLOAD_MULTI)// 下载--多个文件--已下载多少
		{
			msg.what = Constant.MSG_DOWNLOAD_MULTI_TRANSFERED;
		}
		msg.sendToTarget();
	}

	@Override
	public void onRequestFtpDataCompleted(int type) {
		// TODO Auto-generated method stub
		// 此页面没有上传，所以在这不考虑（@see MainActivity）
		if (type == IFTPBaseListener.TYPE_DOWNLOAD)// 下载--单个文件--完成
		{
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_COMPLETE);
		} else if (type == IFTPBaseListener.TYPE_DOWNLOAD_MULTI)// 下载--多个文件--完成
		{
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_MULTI_COMPLETE);
		}
	}

	@Override
	public void onRequestFtpFileListCompleted(List<FTPFile> files) {
		// TODO Auto-generated method stub
		Message msg = mHandler.obtainMessage();
		msg.what = Constant.MSG_GETFILES_COMPLETE;
		msg.obj = files;
		msg.sendToTarget();
	}
}
