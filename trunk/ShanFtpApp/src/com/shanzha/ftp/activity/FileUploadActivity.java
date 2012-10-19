package com.shanzha.ftp.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.shanzha.ftp.R;
import com.shanzha.ftp.adapter.FileUploadAdapter;
import com.shanzha.ftp.core.FTPManager;
import com.shanzha.ftp.core.FtpApp;
import com.shanzha.ftp.dao.FTPDBService;
import com.shanzha.ftp.inter.IBase;
import com.shanzha.ftp.inter.IFTPBaseListener;
import com.shanzha.ftp.inter.IFTPDataMultiListener;
import com.shanzha.ftp.model.FileItem;
import com.shanzha.ftp.model.Record;
import com.shanzha.ftp.util.Constant;
import com.shanzha.ftp.util.DialogUtil;
import com.shanzha.ftp.util.FileUtil;
import com.shanzha.ftp.util.StringUtil;
import com.shanzha.ftp.util.ToastUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;

/**
 * 本地文件浏览activity（上传其他文件）
 * 
 * @author hmy
 * @update ShanZha
 * @time 2012-10-15 14:04
 */
public class FileUploadActivity extends Activity implements IBase,
		DialogInterface.OnClickListener, OnItemClickListener, OnClickListener,
		IFTPDataMultiListener {

	private final String TAG = "FileBrowserActivity";
	/**
	 * 是否上传的确定dialog
	 */
	private static final int DIALOG_ID_UPLOAD = 0;
	/**
	 * 是否上传的dialog
	 */
	private AlertDialog mUploadDialog;
	/**
	 * 标题
	 */
	private TextView mTvTitle;
	/**
	 * 返回
	 */
	private Button mBtnBack;
	/**
	 * 上传按钮
	 */
	private Button mBtnUpload;
	/**
	 * 全选
	 */
	private Button mBtnAllCheck;
	/**
	 * 取消全选
	 */
	private Button mBtnAllCancel;
	/**
	 * ListView
	 */
	private ListView mLv;
	/**
	 * 适配器
	 */
	private FileUploadAdapter mAdapter;
	/**
	 * 目录图标
	 */
	private Bitmap dirIcon;
	/**
	 * 父目录
	 */
	private FileItem lastItem;
	/**
	 * 文件lists
	 */
	private ArrayList<FileItem> mDataList = new ArrayList<FileItem>();
	/**
	 * 已选中集合
	 */
	private Map<String, Boolean> mCheckedMap;
	/**
	 * 当前上传文件已经上传的长度(单个/多个)(此处仅有多个文件上传，已经上传长度交给FTPClient控制)
	 */
	// private int mCurrUploadedLength;
	/**
	 * 当前上传文件的总长度(单个/多个)
	 */
	private long mCurrUploadTotalSize;
	/**
	 * 上传多个文件时，总数
	 */
	private int mUploadTotalCount;
	/**
	 * 上传多个时，表示已上传的个数
	 */
	private int mUploadedCount;
	/**
	 * 等待框
	 */
	private ProgressDialog mPd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.disc);

		initUI();
		initListener();
		initRes();
	}

	@Override
	public void initUI() {
		// TODO Auto-generated method stub
		mBtnBack = (Button) findViewById(R.id.disc_btn_back);
		mBtnUpload = (Button) findViewById(R.id.disc_btn_bottom_ok);
		mBtnAllCheck = (Button) findViewById(R.id.disc_btn_bottom_allcheck);
		mBtnAllCancel = (Button) findViewById(R.id.disc_btn_bottom_nocheck);
		// mLlBottom = (LinearLayout) findViewById(R.id.disc_ll_bottom);
		mTvTitle = (TextView) findViewById(R.id.disc_tv_title);
		mLv = (ListView) findViewById(R.id.disc_listview);

		mBtnBack.setVisibility(View.VISIBLE);
		// mLlBottom.setVisibility(View.VISIBLE);
		mBtnUpload.setVisibility(View.VISIBLE);
		mTvTitle.setVisibility(View.VISIBLE);
		findViewById(R.id.disc_rl_bottom).setVisibility(View.VISIBLE);
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		mBtnBack.setOnClickListener(this);
		mBtnUpload.setOnClickListener(this);
		mBtnAllCheck.setOnClickListener(this);
		mBtnAllCancel.setOnClickListener(this);

	}

	@Override
	public void initRes() {
		// TODO Auto-generated method stub
		mBtnBack.setText("返回");
		mTvTitle.setText(FileUtil.getSdcardPath());
		mBtnUpload.setText("上传");

		dirIcon = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.file_browser_folder);

		List<FileItem> tempList = new FileItem("/sdcard",
				FileUtil.getSdcardPath(), false, dirIcon, true, false)
				.getSubFiles();
		if (null != tempList) {
			mDataList.addAll(tempList);
		}
		mAdapter = new FileUploadAdapter(this, mDataList);
		mCheckedMap = mAdapter.getmCheckedMap();
		Collections.sort(mDataList);
		mLv.setOnItemClickListener(this);
		mLv.setAdapter(mAdapter);

		handleWaittingDialog();
	}

	/**
	 * 等待框处理
	 */
	private void handleWaittingDialog() {
		mPd = DialogUtil.getHorizontalWaittingProDialog(this, "同步信息提示",
				"正在同步...");
		// 正在请求时，用户主动点击”取消”按钮取消
		mPd.setButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				abortCurrDataRequest();
			}
		});
		// 正在请求时，用户主动点击物理返回键取消
		mPd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				abortCurrDataRequest();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FileItem item = mDataList.get(position);
		if (item.isFile()) {
			File localFile = new File(item.getPath());
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			String filetype = FileUtil.getMIMEType(localFile.getName());
			intent.setDataAndType(Uri.fromFile(localFile), filetype);
			startActivity(intent);
		} else if (!item.isRead() && !item.isFile()) {// 目录不能读
			return;
		} else {// 进入目录内部
			if (null != mCheckedMap) {
				mCheckedMap.clear();
			}
			lastItem = item;
			mDataList.clear();
			mTvTitle.setText(item.getPath() + Constant.SEPERATOR
					+ item.getName());
			List<FileItem> tempList = item.getSubFiles();
			if (null != tempList) {
				mDataList.addAll(tempList);
			}
			// files = item.getSubFiles();
			Collections.sort(mDataList);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_ID_UPLOAD:// 确定是否上传
			return mUploadDialog = DialogUtil.getCommonDialg(this, "同步提示信息",
					"您确定要上传选中这些文件到服务器吗？", this);
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:// 确定
			onUpload();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.disc_btn_back:// 返回
			onBack();
			break;
		case R.id.disc_btn_bottom_allcheck:// 全选
			if (null != mDataList) {
				for (int i = 0; i < mDataList.size(); i++) {
					FileItem item = mDataList.get(i);
					if (item.isFile()) {
						String key = item.getPath();
						mCheckedMap.put(key, true);

					}

				}
				if (null != mAdapter) {
					mAdapter.notifyDataSetChanged();
				}
			}
			break;
		case R.id.disc_btn_bottom_nocheck:// 反选
			for (int i = 0; i < mDataList.size(); i++) {
				FileItem item = mDataList.get(i);
				String key = item.getPath();
				if (!mCheckedMap.containsKey(key)) {
					mCheckedMap.put(key, true);
				} else {
					mCheckedMap.remove(key);
				}
			}
			if (null != mAdapter) {
				mAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.disc_btn_bottom_ok:// 上传
			showDialog(DIALOG_ID_UPLOAD);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		onBack();
	}

	/**
	 * 返回
	 */
	private void onBack() {
		if (lastItem != null) {
			File currDir = new File(lastItem.getPath());
			if (!currDir.getParent().equals(FileUtil.getSdcardParentPath())) {// 当到达最上层时
				File parentDir = new File(currDir.getParent());
				FileItem last = new FileItem(parentDir.getName(),
						parentDir.getPath(), false, dirIcon,
						parentDir.canRead(), parentDir.canWrite());
				List<FileItem> tempList = last.getSubFiles();
				lastItem = last;
				mDataList.clear();
				mTvTitle.setText(parentDir.getPath());
				// lastItem = null;
				if (null != tempList) {
					mDataList.addAll(tempList);
					Collections.sort(mDataList);
					mAdapter.notifyDataSetChanged();
				}
			} else {
				this.finish();
				return;
			}
		} else {
			finish();
			return;
		}
	}

	/**
	 * 上传文件
	 */
	private void onUpload() {
		if (null != mCheckedMap && mCheckedMap.size() > 0) {
			DialogUtil.showWaittingDialog(mPd);
			FTPManager ftpMgr = FTPManager.getInstance();
			List<Record> recordList = new ArrayList<Record>();
			Set<String> set = mCheckedMap.keySet();
			Iterator<String> it = set.iterator();
			Map<String, String> map = FtpApp.getInstance().getmMapUploaded();
			while (it.hasNext()) {
				String path = it.next();
				// 已经上传过，则不重复上传
				if (map.containsKey(path)) {
					break;
				}
				File tempFile = new File(path);
				mCurrUploadTotalSize += tempFile.length();
				String remoteDir = Constant.FTP_ROOT + FtpApp.DEVICE_NAME;
				Record record = new Record();
				record.setUri(path);
				record.setType(Record.TYPE_UPLOAD_RECORD);
				List<String> tempList = StringUtil
						.getFilenameAndDirByPath(path);
				if (null != tempList) {
					int size = tempList.size();
					if (size > 0) {
						record.setFilename(tempList.get(0));
					}
					if (size > 1) {
						record.setLocalDir(tempList.get(1));
					}
				}
				record.setRemoteDir(remoteDir);
				record.setTime(StringUtil.formatDate(
						System.currentTimeMillis(), "yyyy-MM-dd hh:mm"));
				recordList.add(record);

			}
			// 选中的都已经上传过
			if (recordList.size() == 0) {
				DialogUtil.dismissWaittingDialog(mPd);
				ToastUtils.show(this, "没有新文件需要上传", false);
				return;
			}
			mUploadTotalCount = recordList.size();

			try {
				ftpMgr.uploadMulti(recordList, this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			ToastUtils.show(this, "请选择需要上传的文件", false);
		}
	}

	/**
	 * 终止当前数据请求（正在请求时，用户主动取消）
	 */
	private void abortCurrDataRequest() {
		ToastUtils.show(FileUploadActivity.this, "此次成功上传 " + mUploadedCount
				+ " 个文件，剩余 " + (mUploadTotalCount - mUploadedCount) + " 个未上传",
				false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					FTPManager.getInstance().abortCurrDataConnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 重置
	 */
	private void reset() {
		mUploadedCount = 0;
		mUploadTotalCount = 0;
		mPd.setProgress(0);
		if (null != mCheckedMap) {
			mCheckedMap.clear();
		}
		mAdapter.notifyDataSetChanged();
		mCurrUploadTotalSize = 0;
		// mCurrUploadedLength = 0;
	}

	/**
	 * UI助手
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.MSG_UPLOAD_MULTI_START:// 上传--多个文件--开始

				break;
			case Constant.MSG_UPLOAD_ERROR:// 上传--多个文件--出错
				DialogUtil.dismissWaittingDialog(mPd);
				ToastUtils.show(FileUploadActivity.this, "上传出错", false);
				reset();
				break;
			case Constant.MSG_UPLOAD_MULTI_TRANSFERED:// 上传--多个文件--已经传输多少
				int len = (Integer) msg.obj;
				// mCurrUploadedLength += len;
				int progress = (int) (len * (100.0 / (float) mCurrUploadTotalSize));
				mPd.setProgress(progress);
				break;
			case Constant.MSG_UPLOAD_MULTI_ONEFILE_COMPLETE:// 上传--多个文件--其中一个完成
				mUploadedCount++;
				Record record = (Record) msg.obj;
				// 持久化“记录”
				FTPDBService.getInstance(FileUploadActivity.this).saveRecord(
						record);
				// 内存“记录”
				FtpApp.getInstance().getmMapUploaded()
						.put(record.getUri(), record.getFilename());
				break;
			case Constant.MSG_UPLOAD_MULTI_COMPLETE:// 上传--多个文件--完成
				DialogUtil.dismissWaittingDialog(mPd);
				ToastUtils.show(FileUploadActivity.this, "上传 "
						+ mUploadTotalCount + " 个文件成功", true);
				reset();
				break;
			}
		}

	};

	@Override
	public void onRequestFtpDataStart(int type) {
		// TODO Auto-generated method stub
		// 此页面只需要考虑上传多个文件即可
		if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI) {
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_MULTI_START);
		}
	}

	@Override
	public void onRequestFtpDataError(String errorMsg, int type) {
		// TODO Auto-generated method stub
		// 此页面只需要考虑上传多个文件即可
		if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI) {
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_MULTI_ERROR);
		}
	}

	@Override
	public void onRequestFtpDataOneFileCompleted(Record record, int type) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onRequestFtpDataOneFileCompleted() type=" + type);
		// 此页面只需要考虑上传多个文件即可
		if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI) {
			Message msg = mHandler.obtainMessage();
			msg.what = Constant.MSG_UPLOAD_MULTI_ONEFILE_COMPLETE;
			msg.obj = record;
			msg.sendToTarget();
		}
	}

	@Override
	public void onRequestFtpDataTransfered(int transeredLength, int type) {
		// TODO Auto-generated method stub
		// 此页面只需要考虑上传多个文件即可
		if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI) {
			Message msg = mHandler.obtainMessage();
			msg.what = Constant.MSG_UPLOAD_MULTI_TRANSFERED;
			msg.obj = transeredLength;
			msg.sendToTarget();
		}
	}

	@Override
	public void onRequestFtpDataCompleted(int type) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onRequestFtpDataCompleted() type=" + type);
		// 此页面只需要考虑上传多个文件即可
		if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI) {
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_MULTI_COMPLETE);
		}
	}

}
