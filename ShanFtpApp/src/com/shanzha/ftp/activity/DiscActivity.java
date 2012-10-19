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
 * ���̹������
 * 
 * @author ShanZha
 * @date 2012-9-28 9:49
 * 
 */
public class DiscActivity extends Activity implements IBase, OnClickListener,
		DialogInterface.OnClickListener, OnItemClickListener,
		IFTPGetFilesListener, IFTPDataListener, IFTPDataMultiListener {

	private static final String TAG = "DiscActivity";
	/** ���ذ�ť **/
	private Button mBtnBack;
	/** Title **/
	private TextView mTvTitle;
	/** ���ذ�ť **/
	private Button mBtnDownload;
	/** ListView **/
	private ListView mListView;
	/** �ײ����� **/
	private RelativeLayout mRlBottom;
	/** ȫѡ��ť **/
	private Button mBtnCheckAll;
	/** ȷ����ť **/
	private Button mBtnOk;
	/** ��ѡ��ť **/
	private Button mBtnCheckReserve;
	/** ��ȡ�ļ��ṹ�ĵȴ��� **/
	private ProgressDialog mPdGetFiles;
	/** ���صĵȴ��� **/
	private ProgressDialog mPdDownload;
	/** FTP��������� ***/
	private FTPManager mFtpMgr;
	/** ��ǰ����·�� **/
	private String mCurrRemoteDir;
	/** ���̻��� **/
	private Stack<Disc> mCacheDisc = new Stack<Disc>();
	/** ���������� **/
	private DiscAdapter mAdaper;
	/** ��ǰչʾ���ݵļ��� **/
	private List<FTPFile> mDataList;
	/** ��ǰ�������ص��ļ����� **/
	private Record mCurrDownload;
	// /** ��ǰ�������ص��ļ��Ѿ����صĳ���(�����ļ�)������ļ�����FTPClient���ƣ� **/
	private int mCurrTransferedLength;
	/** ��ǰ�������ص��ļ��ܳ���(����/���) **/
	private long mCurrDownloadTotalSize;
	/** ��ǰ�������ص��ļ��ܸ���(���) **/
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

		mPdGetFiles = DialogUtil.getWaittingProDialog(this, "", "���Ե�...");
		mPdDownload = DialogUtil.getHorizontalWaittingProDialog(this, "������ʾ��Ϣ",
				"");

		mPdGetFiles.setButton("ȡ��", mClickCancelListener);
		mPdGetFiles.setOnCancelListener(mDialogCancelListener);
		mPdDownload.setButton("ȡ��", mClickCancelListener);
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
		case R.id.disc_btn_back:// ����
			onBack();
			break;
		case R.id.disc_btn_download:// ����
			setDownloadModel(true);
			break;
		case R.id.disc_btn_bottom_allcheck:// ȫѡ
			onCheckAll();
			break;
		case R.id.disc_btn_bottom_nocheck:// ��ѡ
			onCheckReverse();
			break;
		case R.id.disc_btn_bottom_ok:// ȷ������
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
	 * ��ȡ�ļ�--���ط���
	 */
	private void startGetFiles() {
		DialogUtil.showWaittingDialog(mPdGetFiles);
		try {
			mFtpMgr.getFilesByDir(mCurrRemoteDir, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "��ȡ�ļ������");
			mHandler.sendEmptyMessage(Constant.MSG_GETFILES_ERROR);
			e.printStackTrace();
		}
	}

	/**
	 * ���ص����ļ�--���ط���
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
			Log.i(TAG, "�����ˣ�������");
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_ERROR);
			e.printStackTrace();
		}
	}

	/**
	 * ��������ء�
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
	 * ���á����ء�
	 * 
	 * @param isClick
	 *            �Ƿ��ǵ�������ء���ť
	 */
	private void setDownloadModel(boolean isClick) {
		boolean isDownloadModel = mAdaper.isDownloadModel();
		if (isClick) {
			if (isDownloadModel) {
				mBtnDownload.setText("����");
				mRlBottom.setVisibility(View.GONE);
				mAdaper.setDownloadModel(false);
			} else {
				mAdaper.setDownloadModel(true);
				mBtnDownload.setText("ȡ��");
				mRlBottom.setVisibility(View.VISIBLE);
			}
		} else {
			if (View.VISIBLE == mRlBottom.getVisibility()) {
				mRlBottom.setVisibility(View.GONE);
				mBtnDownload.setText("����");
				mAdaper.setDownloadModel(false);
			}
		}
	}

	/**
	 * �����ȫѡ�������������ļ����������ļ���
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
			ToastUtils.show(this, "��Ŀ¼��û���ļ�������", false);
		} else {
			mHandler.sendEmptyMessage(Constant.MSG_NOTIFY_DATA_CHANGED);
		}
	}

	/**
	 * �������ѡ������������ļ�
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
	 * �����ȷ�����ء�--������ѡ�е��ļ�������һ����
	 */
	public void onDownloadCheck() {
		Map<Integer, Boolean> checkMap = mAdaper.getmCheckMap();
		if (null == checkMap) {
			return;
		}
		if (checkMap.size() <= 0) {
			ToastUtils.show(this, "����ѡ����Ҫ���ص��ļ�", false);
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
			// ѡ�в���û�����ع�
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
			ToastUtils.show(this, "û����Ҫ�ϴ����ļ�", false);
			return;
		}
		mCurrDownloadTotalCount = downloadList.size();
		try {
			mFtpMgr.downloadMulti(downloadList, mCurrRemoteDir, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "���ض���ļ������");
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_MULTI_ERROR);
			e.printStackTrace();
		}
	}

	/**
	 * ����
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
	 * ��ֹ��ǰ����������������ʱ���û�����ȡ����
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
	 * �û����ProgressDialog�ϵġ�ȡ������ťʱ�ļ���
	 */
	private DialogInterface.OnClickListener mClickCancelListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			abortCurrDataRequest();
		}
	};
	/**
	 * �û������ؼ�ʱ��ProgressDialogȡ���ļ���
	 */
	private DialogInterface.OnCancelListener mDialogCancelListener = new OnCancelListener() {

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			abortCurrDataRequest();
		}
	};
	/**
	 * ���������Ϣ�¼�
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.MSG_NOTIFY_DATA_CHANGED:// ֪ͨ���ݸı�
				if (null == mAdaper) {
					mAdaper = new DiscAdapter(mDataList, DiscActivity.this);
					mListView.setAdapter(mAdaper);
				} else {
					mAdaper.notifyDataSetChanged();
				}
				break;
			case Constant.MSG_GETFILES_START:// ��ȡ�ļ�--��ʼ

				break;
			case Constant.MSG_GETFILES_ERROR:// ��ȡ�ļ�--����
				DialogUtil.dismissWaittingDialog(mPdGetFiles);
				ToastUtils.show(DiscActivity.this, "��ȡ�ļ�ʧ��", false);
				break;
			case Constant.MSG_GETFILES_COMPLETE:// ��ȡ�ļ�--�����
				DialogUtil.dismissWaittingDialog(mPdGetFiles);
				// mDataList = (List<FTPFile>)msg.obj;
				List<FTPFile> tempList = (List<FTPFile>) msg.obj;
				if (null != mDataList && null != tempList) {
					mDataList.clear();
					mDataList.addAll(tempList);
				}
				this.sendEmptyMessage(Constant.MSG_NOTIFY_DATA_CHANGED);
				break;
			case Constant.MSG_DOWNLOAD_START:// ����--�����ļ�--��ʼ

				break;
			case Constant.MSG_DOWNLOAD_TRANSFERRED:// ����--�����ļ�--�����ض���
				// �����ض���ļ���ͬ�������len��ָÿ�����صĳ��ȣ��������ܹ����صĳ��ȣ��Լ����ƣ�
				int transferedLength = (Integer) msg.obj;
				mCurrTransferedLength += transferedLength;
				int progress = (int) (mCurrTransferedLength * (100.0 / (float) mCurrDownloadTotalSize));
				mPdDownload.setProgress(progress);
				break;
			case Constant.MSG_DOWNLOAD_ERROR:// ����--�����ļ�--����
				DialogUtil.dismissWaittingDialog(mPdDownload);
				ToastUtils.show(DiscActivity.this, "�����ļ�����", false);
				reset();
				break;
			case Constant.MSG_DOWNLOAD_COMPLETE:// ����--�����ļ�--�����
				DialogUtil.dismissWaittingDialog(mPdDownload);
				// �־û�������--���ݿ�
				FTPDBService ftpDB = FTPDBService
						.getInstance(DiscActivity.this);
				ftpDB.saveRecord(mCurrDownload);
				// ����������--�ڴ�
				String key = mCurrDownload.getUri();
				FtpApp.getInstance().getmMapDownloaded()
						.put(key, mCurrDownload.getFilename());
				reset();
				break;
			case Constant.MSG_DOWNLOAD_MULTI_START:// ����--����ļ�--��ʼ
				Log.i(TAG, "����ļ����ؿ�ʼ...");
				break;
			case Constant.MSG_DOWNLOAD_MULTI_TRANSFERED:// ����--����ļ�--�����ض���
				// �����ص����ļ���ͬ�������len��ָ�Ѿ����س��ȣ���FTPClient����ƣ�
				int multiLen = (Integer) msg.obj;
				int multiProgress = (int) (multiLen * (100.0 / (float) mCurrDownloadTotalSize));
				mPdDownload.setProgress(multiProgress);
				// Log.i(TAG, " multiFile downloaded length = " + multiLen
				// + " progress = " + multiProgress);
				break;
			case Constant.MSG_DOWNLOAD_MULTI_ONEFILE_COMPLETE:// ����--����ļ�--����һ�����
				Record download = (Record) msg.obj;
				Log.i(TAG, "��������еĵ������ filename = " + download.getFilename()
						+ " localPath = " + download.getLocalDir());
				// �־û�������--���ݿ�
				FTPDBService ftpDB2 = FTPDBService
						.getInstance(DiscActivity.this);
				ftpDB2.saveRecord(download);
				// ����������--�ڴ�
				String key2 = download.getUri();
				FtpApp.getInstance().getmMapDownloaded()
						.put(key2, download.getFilename());
				break;
			case Constant.MSG_DOWNLOAD_MULTI_ERROR:// ����--����ļ�--����
				DialogUtil.dismissWaittingDialog(mPdDownload);
				ToastUtils.show(DiscActivity.this, "���� "
						+ mCurrDownloadTotalCount + " ���ļ�����", false);
				reset();
				break;
			case Constant.MSG_DOWNLOAD_MULTI_COMPLETE:// ����--����ļ�--���
				DialogUtil.dismissWaittingDialog(mPdDownload);
				ToastUtils.show(DiscActivity.this, "���� "
						+ mCurrDownloadTotalCount + " ���ļ����", true);
				reset();
				break;
			}
		}

	};

	@Override
	public void onRequestFtpDataStart(int type) {
		// TODO Auto-generated method stub
		// ��ҳ��û���ϴ����������ⲻ���ǣ�@see MainActivity��
		if (type == IFTPBaseListener.TYPE_GETFILES)// ��ȡ�ļ��Լ��ļ�����Ϣ--��ʼ
		{
			mHandler.sendEmptyMessage(Constant.MSG_GETFILES_START);
		} else if (type == IFTPBaseListener.TYPE_DOWNLOAD)// ����--�����ļ�--��ʼ
		{
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_START);
		} else if (type == IFTPBaseListener.TYPE_DOWNLOAD_MULTI)// ����--����ļ�--��ʼ
		{
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_MULTI_START);
		}
	}

	@Override
	public void onRequestFtpDataError(String errorMsg, int type) {
		// TODO Auto-generated method stub
		// ��ҳ��û���ϴ����������ⲻ���ǣ�@see MainActivity��
		if (type == IFTPBaseListener.TYPE_GETFILES)// ��ȡ�ļ��Լ��ļ�����Ϣ--����
		{
			mHandler.sendEmptyMessage(Constant.MSG_GETFILES_ERROR);
		} else if (type == IFTPBaseListener.TYPE_DOWNLOAD)// ����--�����ļ�--����
		{
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_ERROR);
		} else if (type == IFTPBaseListener.TYPE_DOWNLOAD_MULTI)// ����--����ļ�--����
		{
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_MULTI_ERROR);
		}
	}

	@Override
	public void onRequestFtpDataOneFileCompleted(Record record, int type) {
		// TODO Auto-generated method stub
		// ��ҳ��û���ϴ����������ⲻ���ǣ�@see MainActivity��
		if (type == IFTPBaseListener.TYPE_DOWNLOAD_MULTI)// ����--����ļ�--����һ�����
		{
			Message msg = mHandler.obtainMessage();
			msg.what = Constant.MSG_DOWNLOAD_MULTI_ONEFILE_COMPLETE;
			msg.obj = record;
			msg.sendToTarget();
		} else if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI)// �ϴ�--����ļ�--����һ�����
		{
			// �ڴ˽�����ʱû���ϴ�����
		}
	}

	@Override
	public void onRequestFtpDataTransfered(int transeredLength, int type) {
		// TODO Auto-generated method stub
		// ��ҳ��û���ϴ����������ⲻ���ǣ�@see MainActivity��
		Message msg = mHandler.obtainMessage();
		msg.obj = transeredLength;
		if (type == IFTPBaseListener.TYPE_DOWNLOAD)// ����--�����ļ�--�����ض���
		{
			msg.what = Constant.MSG_DOWNLOAD_TRANSFERRED;
		} else if (type == IFTPBaseListener.TYPE_DOWNLOAD_MULTI)// ����--����ļ�--�����ض���
		{
			msg.what = Constant.MSG_DOWNLOAD_MULTI_TRANSFERED;
		}
		msg.sendToTarget();
	}

	@Override
	public void onRequestFtpDataCompleted(int type) {
		// TODO Auto-generated method stub
		// ��ҳ��û���ϴ����������ⲻ���ǣ�@see MainActivity��
		if (type == IFTPBaseListener.TYPE_DOWNLOAD)// ����--�����ļ�--���
		{
			mHandler.sendEmptyMessage(Constant.MSG_DOWNLOAD_COMPLETE);
		} else if (type == IFTPBaseListener.TYPE_DOWNLOAD_MULTI)// ����--����ļ�--���
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
