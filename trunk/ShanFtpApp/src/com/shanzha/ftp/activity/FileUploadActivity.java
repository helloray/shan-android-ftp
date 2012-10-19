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
 * �����ļ����activity���ϴ������ļ���
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
	 * �Ƿ��ϴ���ȷ��dialog
	 */
	private static final int DIALOG_ID_UPLOAD = 0;
	/**
	 * �Ƿ��ϴ���dialog
	 */
	private AlertDialog mUploadDialog;
	/**
	 * ����
	 */
	private TextView mTvTitle;
	/**
	 * ����
	 */
	private Button mBtnBack;
	/**
	 * �ϴ���ť
	 */
	private Button mBtnUpload;
	/**
	 * ȫѡ
	 */
	private Button mBtnAllCheck;
	/**
	 * ȡ��ȫѡ
	 */
	private Button mBtnAllCancel;
	/**
	 * ListView
	 */
	private ListView mLv;
	/**
	 * ������
	 */
	private FileUploadAdapter mAdapter;
	/**
	 * Ŀ¼ͼ��
	 */
	private Bitmap dirIcon;
	/**
	 * ��Ŀ¼
	 */
	private FileItem lastItem;
	/**
	 * �ļ�lists
	 */
	private ArrayList<FileItem> mDataList = new ArrayList<FileItem>();
	/**
	 * ��ѡ�м���
	 */
	private Map<String, Boolean> mCheckedMap;
	/**
	 * ��ǰ�ϴ��ļ��Ѿ��ϴ��ĳ���(����/���)(�˴����ж���ļ��ϴ����Ѿ��ϴ����Ƚ���FTPClient����)
	 */
	// private int mCurrUploadedLength;
	/**
	 * ��ǰ�ϴ��ļ����ܳ���(����/���)
	 */
	private long mCurrUploadTotalSize;
	/**
	 * �ϴ�����ļ�ʱ������
	 */
	private int mUploadTotalCount;
	/**
	 * �ϴ����ʱ����ʾ���ϴ��ĸ���
	 */
	private int mUploadedCount;
	/**
	 * �ȴ���
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
		mBtnBack.setText("����");
		mTvTitle.setText(FileUtil.getSdcardPath());
		mBtnUpload.setText("�ϴ�");

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
	 * �ȴ�����
	 */
	private void handleWaittingDialog() {
		mPd = DialogUtil.getHorizontalWaittingProDialog(this, "ͬ����Ϣ��ʾ",
				"����ͬ��...");
		// ��������ʱ���û����������ȡ������ťȡ��
		mPd.setButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				abortCurrDataRequest();
			}
		});
		// ��������ʱ���û�������������ؼ�ȡ��
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
		} else if (!item.isRead() && !item.isFile()) {// Ŀ¼���ܶ�
			return;
		} else {// ����Ŀ¼�ڲ�
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
		case DIALOG_ID_UPLOAD:// ȷ���Ƿ��ϴ�
			return mUploadDialog = DialogUtil.getCommonDialg(this, "ͬ����ʾ��Ϣ",
					"��ȷ��Ҫ�ϴ�ѡ����Щ�ļ�����������", this);
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:// ȷ��
			onUpload();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.disc_btn_back:// ����
			onBack();
			break;
		case R.id.disc_btn_bottom_allcheck:// ȫѡ
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
		case R.id.disc_btn_bottom_nocheck:// ��ѡ
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
		case R.id.disc_btn_bottom_ok:// �ϴ�
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
	 * ����
	 */
	private void onBack() {
		if (lastItem != null) {
			File currDir = new File(lastItem.getPath());
			if (!currDir.getParent().equals(FileUtil.getSdcardParentPath())) {// ���������ϲ�ʱ
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
	 * �ϴ��ļ�
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
				// �Ѿ��ϴ��������ظ��ϴ�
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
			// ѡ�еĶ��Ѿ��ϴ���
			if (recordList.size() == 0) {
				DialogUtil.dismissWaittingDialog(mPd);
				ToastUtils.show(this, "û�����ļ���Ҫ�ϴ�", false);
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
			ToastUtils.show(this, "��ѡ����Ҫ�ϴ����ļ�", false);
		}
	}

	/**
	 * ��ֹ��ǰ����������������ʱ���û�����ȡ����
	 */
	private void abortCurrDataRequest() {
		ToastUtils.show(FileUploadActivity.this, "�˴γɹ��ϴ� " + mUploadedCount
				+ " ���ļ���ʣ�� " + (mUploadTotalCount - mUploadedCount) + " ��δ�ϴ�",
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
	 * ����
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
	 * UI����
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.MSG_UPLOAD_MULTI_START:// �ϴ�--����ļ�--��ʼ

				break;
			case Constant.MSG_UPLOAD_ERROR:// �ϴ�--����ļ�--����
				DialogUtil.dismissWaittingDialog(mPd);
				ToastUtils.show(FileUploadActivity.this, "�ϴ�����", false);
				reset();
				break;
			case Constant.MSG_UPLOAD_MULTI_TRANSFERED:// �ϴ�--����ļ�--�Ѿ��������
				int len = (Integer) msg.obj;
				// mCurrUploadedLength += len;
				int progress = (int) (len * (100.0 / (float) mCurrUploadTotalSize));
				mPd.setProgress(progress);
				break;
			case Constant.MSG_UPLOAD_MULTI_ONEFILE_COMPLETE:// �ϴ�--����ļ�--����һ�����
				mUploadedCount++;
				Record record = (Record) msg.obj;
				// �־û�����¼��
				FTPDBService.getInstance(FileUploadActivity.this).saveRecord(
						record);
				// �ڴ桰��¼��
				FtpApp.getInstance().getmMapUploaded()
						.put(record.getUri(), record.getFilename());
				break;
			case Constant.MSG_UPLOAD_MULTI_COMPLETE:// �ϴ�--����ļ�--���
				DialogUtil.dismissWaittingDialog(mPd);
				ToastUtils.show(FileUploadActivity.this, "�ϴ� "
						+ mUploadTotalCount + " ���ļ��ɹ�", true);
				reset();
				break;
			}
		}

	};

	@Override
	public void onRequestFtpDataStart(int type) {
		// TODO Auto-generated method stub
		// ��ҳ��ֻ��Ҫ�����ϴ�����ļ�����
		if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI) {
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_MULTI_START);
		}
	}

	@Override
	public void onRequestFtpDataError(String errorMsg, int type) {
		// TODO Auto-generated method stub
		// ��ҳ��ֻ��Ҫ�����ϴ�����ļ�����
		if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI) {
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_MULTI_ERROR);
		}
	}

	@Override
	public void onRequestFtpDataOneFileCompleted(Record record, int type) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onRequestFtpDataOneFileCompleted() type=" + type);
		// ��ҳ��ֻ��Ҫ�����ϴ�����ļ�����
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
		// ��ҳ��ֻ��Ҫ�����ϴ�����ļ�����
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
		// ��ҳ��ֻ��Ҫ�����ϴ�����ļ�����
		if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI) {
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_MULTI_COMPLETE);
		}
	}

}
