package com.shanzha.ftp.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shanzha.ftp.R;
import com.shanzha.ftp.core.ContactManager;
import com.shanzha.ftp.core.FTPManager;
import com.shanzha.ftp.core.FtpApp;
import com.shanzha.ftp.core.SmsManager;
import com.shanzha.ftp.core.StateManager;
import com.shanzha.ftp.dao.FTPDBService;
import com.shanzha.ftp.inter.IBase;
import com.shanzha.ftp.inter.IFTPBaseListener;
import com.shanzha.ftp.inter.IFTPDataListener;
import com.shanzha.ftp.inter.IFTPDataMultiListener;
import com.shanzha.ftp.inter.ILoadBaseListener;
import com.shanzha.ftp.inter.ILoadPicListener;
import com.shanzha.ftp.inter.ILoadSmsContactListener;
import com.shanzha.ftp.model.Record;
import com.shanzha.ftp.net.FTPClient;
import com.shanzha.ftp.observer.ObserverService;
import com.shanzha.ftp.util.Constant;
import com.shanzha.ftp.util.DialogUtil;
import com.shanzha.ftp.util.FileUtil;
import com.shanzha.ftp.util.SharePreferenceUtil;
import com.shanzha.ftp.util.StringUtil;
import com.shanzha.ftp.util.ToastUtils;

/**
 * ������
 * 
 * @author ShanZha
 * @date 2012-9-25 15:18
 * 
 */
public class MainActivity extends Activity implements IBase, OnClickListener,
		DialogInterface.OnClickListener, ILoadSmsContactListener,
		IFTPDataListener, IFTPDataMultiListener, ILoadPicListener {

	private static final String TAG = "MainActivity";
	/** ������ʾ�û��Ĳ��� **/
	private RelativeLayout mRlNotify;
	/** ������ʾ�û�����Ϣ **/
	private TextView mTvNotify;
	/** �����������ӵİ�ť **/
	private Button mBtnRefresh;
	/** һ��ͬ�� **/
	private Button mBtnOneKey;
	/** ͬ��ͼƬ **/
	private Button mBtnPic;
	/** ͬ����ϵ�� **/
	private Button mBtnContact;
	/** ͬ������ **/
	private Button mBtnSms;
	/** �ϴ��ļ� **/
	private Button mBtnUpload;
	/** ���̹��� **/
	private Button mBtnDisc;
	/** ���ӵ�ProgressDialog **/
	private ProgressDialog mPdConnect;
	/** ͬ����ProgressDialog **/
	private ProgressDialog mPdUpload;
	/** �Ƿ��ϴ���dialog **/
	private AlertDialog mUploadDialog;
	/** FTP�����ߣ�һ�й���FTP������������� **/
	private FTPManager mFtpMgr;
	/** ��ǰ����İ�ťid **/
	private int mClickId = -1;
	/** ��ǰ�ϴ��ĵ����ļ�����¼������ **/
	private Record mCurrUpload;
	/** ��ǰ�Ѿ��ϴ��ĳ���(����/���) **/
	private int mCurrUploadedLength;
	/** ��ǰҪ�ϴ��ļ����ܳ���(����/���) **/
	private long mCurrUploadTotalSize;
	/** ��ǰ�Ѿ��ϴ����ļ����� **/
	private int mCurrUploadedCount;
	/** ��ǰ��Ҫ�ϴ����ļ����� **/
	private int mCurrUploadTotalCount;

	private Intent mServiceIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initUI();
		initListener();
		initRes();
	}

	@Override
	public void initUI() {
		// TODO Auto-generated method stub
		mRlNotify = (RelativeLayout) this.findViewById(R.id.main_ll_notify);
		mTvNotify = (TextView) this.findViewById(R.id.main_tv_notify);
		mBtnRefresh = (Button) this.findViewById(R.id.main_btn_refresh);
		mBtnOneKey = (Button) this.findViewById(R.id.main_btn_upload_onekey);
		mBtnPic = (Button) this.findViewById(R.id.main_btn_upload_pic);
		mBtnContact = (Button) this.findViewById(R.id.main_btn_upload_contact);
		mBtnSms = (Button) this.findViewById(R.id.main_btn_upload_sms);
		mBtnUpload = (Button) this.findViewById(R.id.main_btn_upload_files);
		mBtnDisc = (Button) this.findViewById(R.id.main_btn_upload_disc);

	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		mBtnRefresh.setOnClickListener(this);
		mBtnOneKey.setOnClickListener(this);
		mBtnPic.setOnClickListener(this);
		mBtnContact.setOnClickListener(this);
		mBtnSms.setOnClickListener(this);
		mBtnUpload.setOnClickListener(this);
		mBtnDisc.setOnClickListener(this);
	}

	@Override
	public void initRes() {
		// TODO Auto-generated method stub
		mFtpMgr = FTPManager.getInstance();

		FtpApp.getInstance().queryFTPDB(this);
		FtpApp.getInstance().initSmsFile(this, this);
		FtpApp.getInstance().initContactFile(this, this);
		FtpApp.getInstance().initPicFile(FileUtil.getSdcardPath(), this);

		mServiceIntent = new Intent(this, ObserverService.class);
		this.startService(mServiceIntent);

		mPdConnect = DialogUtil.getWaittingProDialog(this, "", "��������FTP������...");
		mPdUpload = DialogUtil.getHorizontalWaittingProDialog(this, "ͬ����Ϣ��ʾ",
				"     ����ͬ��...");
		mPdUpload.setButton("ȡ��", mClickCancelListener);
		mPdUpload.setOnCancelListener(mDialogCancelListener);

		boolean isFirst = getIntent().getBooleanExtra("isFirst", false);
		if (!isFirst) {
			DialogUtil.showWaittingDialog(mPdConnect);
			onConnect();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		// �˳�����
		if (id == Constant.DIALOG_LOGOUT) {
			return DialogUtil.getCommonDialg(this, "��ʾ��Ϣ", "��ȷ��Ҫ�˳�������", this);
		} else if (id == Constant.DIALOG_UPLOAD)// �ϴ�ȷ��
		{
			return mUploadDialog = DialogUtil.getCommonDialg(this, "ȷ����Ϣ", "",
					this);
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:// ȷ��
			if (mClickId == R.id.main_btn_upload_onekey)// ȷ����һ��ͬ����
			{
				ToastUtils.show(MainActivity.this, "���û�������Լ����Ű��", false);
			} else if (mClickId == R.id.main_btn_upload_pic)// ȷ����ͬ��ͼƬ��
			{
				DialogUtil.showWaittingDialog(mPdUpload);
				int picState = StateManager.getInstance().getPicState();
				if (picState == StateManager.STATE_COMPLETE) {
					onUploadPic();
				} else if (picState == StateManager.STATE_ERROR) {
					// ���س������¼���
					FtpApp.getInstance().initPicFile(FileUtil.getSdcardPath(),
							this);
				} else if (picState == StateManager.STATE_PREPARE) {
					// �ϴ��ڻص���
				}
			} else if (mClickId == R.id.main_btn_upload_contact)// ȷ����ͬ����ϵ�ˡ�
			{
				DialogUtil.showWaittingDialog(mPdUpload);
				int contactState = StateManager.getInstance().getContactState();
				if (contactState == StateManager.STATE_COMPLETE)// ��ʼ�����
				{
					String uri = ContactManager.getInstance(MainActivity.this)
							.getLocalFilePath();
					onUploadSmsContact(FileUtil.CONTACT_FILENAME, uri);
				} else if (contactState == StateManager.STATE_ERROR)// ��ʼ������
				{
					// ���¼��أ�����ٴ��ϴ����ϴ�Ҳ���ڻص���
					FtpApp.getInstance().initContactFile(this, this);
				} else if (contactState == StateManager.STATE_PREPARE)// ��ʼ��������
				{
					// �ϴ������ڻص���
				}
			} else if (mClickId == R.id.main_btn_upload_sms)// ȷ����ͬ�����š�
			{
				DialogUtil.showWaittingDialog(mPdUpload);
				int smsState = StateManager.getInstance().getSmsState();
				if (smsState == StateManager.STATE_COMPLETE) {
					String uri = SmsManager.getInstance(MainActivity.this)
							.getLocalFilePath();
					// ֱ���ϴ�
					onUploadSmsContact(FileUtil.SMS_FILENAME, uri);
				} else if (smsState == StateManager.STATE_ERROR) {
					// �����ˣ����¶�д���ϴ�Ҳ���ڻص���
					FtpApp.getInstance().initSmsFile(this, this);
				} else if (smsState == StateManager.STATE_PREPARE) {
					// ����׼�����ϴ��ŵ��ص���
				}
			} else if (mClickId == -1)// ȷ�����˳�����
			{
				MainActivity.this.stopService(mServiceIntent);
				Log.i(TAG, "һ�ж�����");
				Process.killProcess(Process.myPid());
				try {
					mFtpMgr.logout();
					mFtpMgr.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.main_btn_refresh:// ��������FTP
			onConnect();
			break;
		case R.id.main_btn_upload_onekey:// һ��ͬ��
			mClickId = R.id.main_btn_upload_onekey;
			showDialog(Constant.DIALOG_UPLOAD);
			if (null != mUploadDialog) {
				mUploadDialog.setMessage("	��ȷ��Ҫͬ������ͼƬ����ϵ�ˡ�����Ϣ����������");
			}
			break;
		case R.id.main_btn_upload_pic:// ͼƬ
			mClickId = R.id.main_btn_upload_pic;
			showDialog(Constant.DIALOG_UPLOAD);
			if (null != mUploadDialog) {
				mUploadDialog.setMessage("	��ȷ��Ҫͬ������ͼƬ����������");
			}
			break;
		case R.id.main_btn_upload_contact:// ��ϵ��
			mClickId = R.id.main_btn_upload_contact;
			showDialog(Constant.DIALOG_UPLOAD);
			if (null != mUploadDialog) {
				mUploadDialog.setMessage("	��ȷ��Ҫͬ����ϵ�˵���������");
			}
			break;
		case R.id.main_btn_upload_sms:// ����
			mClickId = R.id.main_btn_upload_sms;
			showDialog(Constant.DIALOG_UPLOAD);
			if (null != mUploadDialog) {
				mUploadDialog.setMessage("	��ȷ��Ҫͬ������Ϣ����������");
			}
			break;
		case R.id.main_btn_upload_files:// ͬ���ļ�
			Intent uploadIntent = new Intent(this, FileUploadActivity.class);
			startActivity(uploadIntent);
			break;
		case R.id.main_btn_upload_disc:// ���̹���
			Intent disc = new Intent(this, DiscActivity.class);
			startActivity(disc);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mClickId = -1;
			showDialog(Constant.DIALOG_LOGOUT);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * ���Ѿ���¼�ɹ�һ��֮���ٴν����������ʱ����¼����
	 */
	private void onConnect() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// mHandler.sendEmptyMessage(Constant.MSG_CONNECT_START);
				String ip = SharePreferenceUtil.getFtpIp(MainActivity.this);
				String username = SharePreferenceUtil
						.getUserName(MainActivity.this);
				String pswd = SharePreferenceUtil
						.getPassword(MainActivity.this);

				boolean isSuccess = false;
				mFtpMgr.setFTPClient(new FTPClient());
				mFtpMgr.setmTimeOut(30 * 1000);
				try {
					mFtpMgr.connect(ip, FtpApp.DEFAULT_PORT);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					mHandler.sendEmptyMessage(Constant.MSG_CONNECT_FAIL);
					e.printStackTrace();
				}
				try {
					isSuccess = mFtpMgr.login(username, pswd);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					mHandler.sendEmptyMessage(Constant.MSG_CONNECT_FAIL);
					e.printStackTrace();
				}

				if (isSuccess) {
					mHandler.sendEmptyMessage(Constant.MSG_CONNECT_SUCCESS);
				} else {
					mHandler.sendEmptyMessage(Constant.MSG_CONNECT_FAIL);
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
	 * ��ֹ��ǰ����������������ʱ���û�����ȡ����
	 */
	private void abortCurrDataRequest() {
		ToastUtils.show(MainActivity.this, "�˴γɹ��ϴ� " + mCurrUploadedCount
				+ " ���ļ���ʣ�� " + (mCurrUploadTotalCount - mCurrUploadedCount)
				+ " ��δ�ϴ�", false);
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
	 * ���������Ϣ
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.MSG_CONNECT_START:// ��ʼ����
				DialogUtil.showWaittingDialog(mPdConnect);
				break;
			case Constant.MSG_CONNECT_SUCCESS:// ���ӳɹ�
				DialogUtil.dismissWaittingDialog(mPdConnect);
				mRlNotify.setVisibility(View.GONE);
				break;
			case Constant.MSG_CONNECT_FAIL:// ����ʧ��
				DialogUtil.dismissWaittingDialog(mPdConnect);
				mRlNotify.setVisibility(View.VISIBLE);
				mTvNotify.setText(getString(R.string.main_notify_info));
				break;
			case Constant.MSG_UPLOAD_START:// �����ļ��ϴ�--��ʼ

				break;
			case Constant.MSG_UPLOAD_ERROR:// �����ļ��ϴ�--����
				onCompletedOrError(true);
				reset();
				break;
			case Constant.MSG_UPLOAD_TRANSFERED:// �����ļ��ϴ�--�Ѿ��ϴ�����
				int len = (Integer) msg.obj;
				mCurrUploadedLength += len;
				int progress = (int) (mCurrUploadedLength * (100.0 / (float) mCurrUploadTotalSize));
				mPdUpload.setProgress(progress);
				break;
			case Constant.MSG_UPLOAD_COMPLETE:// �����ļ��ϴ�--���
				mCurrUploadedCount++;
				onCompletedOrError(false);
				// �־û������ϴ���
				FTPDBService.getInstance(MainActivity.this).saveRecord(
						mCurrUpload);
				String key = mCurrUpload.getUri();
				// �ڴ���
				FtpApp.getInstance().getmMapUploaded()
						.put(key, mCurrUpload.getFilename());
				reset();
				break;
			case Constant.MSG_UPLOAD_MULTI_START:// ����ļ��ϴ�--��ʼ

				break;
			case Constant.MSG_UPLOAD_MULTI_ERROR:// ����ļ��ϴ�--����
				onCompletedOrError(true);
				reset();
				break;
			case Constant.MSG_UPLOAD_MULTI_TRANSFERED:// ����ļ��ϴ�--�Ѿ��ϴ�����
				int multiLen = (Integer) msg.obj;
				// mCurrUploadedLength += multiLen;
				int multiProgress = (int) (multiLen * (100.0 / (float) mCurrUploadTotalSize));
				mPdUpload.setProgress(multiProgress);
				break;
			case Constant.MSG_UPLOAD_MULTI_ONEFILE_COMPLETE:// ����ļ��ϴ�--����һ�����
				Record upload = (Record) msg.obj;
				// �־û������ϴ���
				FTPDBService.getInstance(MainActivity.this).saveRecord(upload);
				String key2 = upload.getUri();
				FtpApp.getInstance().getmMapUploaded()
						.put(key2, upload.getFilename());
				break;
			case Constant.MSG_UPLOAD_MULTI_COMPLETE:// ����ļ��ϴ�--���
				onCompletedOrError(false);
				reset();
				break;
			}
		}

	};

	/**
	 * �����ϴ����/����ı��ط���
	 * 
	 * @param isError
	 */
	private void onCompletedOrError(boolean isError) {
		DialogUtil.dismissWaittingDialog(mPdUpload);
		if (isError) {
			DialogUtil.dismissWaittingDialog(mPdUpload);
			mPdUpload.setProgress(0);
			if (mClickId == R.id.main_btn_upload_contact) {
				ToastUtils.show(MainActivity.this, "�ϴ���ϵ�˳���", false);
			} else if (mClickId == R.id.main_btn_upload_sms) {
				ToastUtils.show(MainActivity.this, "�ϴ����ų���", false);
			} else {
				ToastUtils.show(MainActivity.this, "�ϴ��ļ�����", false);
			}
		} else {
			if (mClickId == R.id.main_btn_upload_contact) {
				ToastUtils.show(MainActivity.this, "�ϴ���ϵ�����", false);
			} else if (mClickId == R.id.main_btn_upload_sms) {
				ToastUtils.show(MainActivity.this, "�ϴ��������", false);
			} else {
				ToastUtils.show(MainActivity.this, "�ϴ��ļ����", false);
			}
		}

	}

	private void onUploadSmsContact(String filename, String uri) {
		// ֱ���ϴ�
		String serverDir = Constant.FTP_ROOT + FtpApp.DEVICE_NAME;
		// String path = ContactManager.getInstance(this).getLocalFilePath();
		// String path = SmsManager.getInstance(this).getLocalFilePath();
		File temp = new File(uri);
		mCurrUploadTotalSize = temp.length();
		mCurrUpload = new Record();
		mCurrUpload.setUri(uri);
		mCurrUpload.setType(Record.TYPE_UPLOAD_RECORD);
		mCurrUpload.setFilename(filename);
		mCurrUpload
				.setLocalDir(FileUtil.getSdcardPath() + FileUtil.PATH_UPLOAD);
		mCurrUpload.setRemoteDir(serverDir);
		mCurrUpload.setTime(StringUtil.formatDate(System.currentTimeMillis(),
				"yyyy-MM--dd hh:mm"));
		try {
			mFtpMgr.upload(mCurrUpload, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void onUploadPic() {
		List<String> pathList = FtpApp.getInstance().getmLocalPicPathList();
		if (null != pathList) {
			List<Record> uploadList = new ArrayList<Record>();
			Map<String, String> mapUpload = FtpApp.getInstance()
					.getmMapUploaded();
			for (int i = 0; i < pathList.size(); i++) {
				String path = pathList.get(i);
				// �Ѿ��ϴ��������ų�
				if (mapUpload.containsKey(path)) {
					continue;
				}
				File temp = new File(path);
				mCurrUploadTotalSize += temp.length();
				Record record = new Record();
				record.setUri(path);
				record.setType(Record.TYPE_UPLOAD_RECORD);
				record.setLocalDir(temp.getParent());
				String remoteDir = Constant.FTP_ROOT + FtpApp.DEVICE_NAME;
				record.setRemoteDir(remoteDir);
				record.setTime(StringUtil.formatDate(
						System.currentTimeMillis(), "yyyy-MM-dd hh:mm"));
				uploadList.add(record);
			}
			int size = uploadList.size();
			if (size == 0) {
				reset();
				DialogUtil.dismissWaittingDialog(mPdUpload);
				ToastUtils.show(MainActivity.this, "û�����ļ���Ҫ�ϴ�", false);
				return;
			} else {
				mCurrUploadTotalCount = size;
				try {
					mFtpMgr.uploadMulti(uploadList, this);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_MULTI_ERROR);
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ����
	 */
	private void reset() {
		mPdUpload.setProgress(0);
		mCurrUpload = null;
		mCurrUploadedLength = 0;
		mCurrUploadTotalSize = 0;
		mCurrUploadedCount = 0;
		mCurrUploadTotalCount = 0;
		mClickId = -1;

	}

	@Override
	public void onLoadPreparation(int type) {
		// TODO Auto-generated method stub
		if (type == ILoadSmsContactListener.TYPE_SMS) {
			StateManager.getInstance().setSmsState(StateManager.STATE_PREPARE);
		} else if (type == ILoadSmsContactListener.TYPE_CONTACT) {
			StateManager.getInstance().setContactState(
					StateManager.STATE_PREPARE);
		} else if (type == ILoadBaseListener.TYPE_PIC) {
			StateManager.getInstance().setPicState(StateManager.STATE_PREPARE);
		}
	}

	@Override
	public void onLoadError(String errorMsg, int type) {
		// TODO Auto-generated method stub
		if (type == ILoadSmsContactListener.TYPE_SMS) {
			StateManager.getInstance().setSmsState(StateManager.STATE_ERROR);
		} else if (type == ILoadSmsContactListener.TYPE_CONTACT) {
			StateManager.getInstance()
					.setContactState(StateManager.STATE_ERROR);
		} else if (type == ILoadBaseListener.TYPE_PIC) {
			StateManager.getInstance().setPicState(StateManager.STATE_ERROR);
		}
	}

	@Override
	public void onLoadCompleted(String path, int type) {
		// TODO Auto-generated method stub
		String filename = "";
		if (type == ILoadSmsContactListener.TYPE_SMS) {
			StateManager.getInstance().setSmsState(StateManager.STATE_COMPLETE);
			SmsManager.getInstance(MainActivity.this).setLocalFilePath(path);
			filename = FileUtil.SMS_FILENAME;
		} else if (type == ILoadSmsContactListener.TYPE_CONTACT) {
			StateManager.getInstance().setContactState(
					StateManager.STATE_COMPLETE);
			ContactManager.getInstance(MainActivity.this)
					.setLocalFilePath(path);
			filename = FileUtil.CONTACT_FILENAME;
		}
		// ��Ϣ/��ϵ�˼������ʱ�������û��Ѿ�����ϴ�����/��ϵ�ˣ����ϴ�
		if (mClickId == R.id.main_btn_upload_sms) {

			onUploadSmsContact(filename, path);
		} else if (mClickId == R.id.main_btn_upload_contact) {
			onUploadSmsContact(filename, path);
		}
	}

	@Override
	public void onLoadPicCompleted(List<String> picPathList) {
		// TODO Auto-generated method stub
		StateManager.getInstance().setPicState(StateManager.STATE_COMPLETE);
		if (null != picPathList) {
			Log.i(TAG, "Pic size = " + picPathList.size());
			List<String> tempList = FtpApp.getInstance().getmLocalPicPathList();
			tempList.addAll(picPathList);
			// ���û�����ϴ�ͼƬʱ�����ػ���׼���У���ʱ�������ʱ��ֱ���ϴ�
			if (mClickId == R.id.main_btn_upload_pic) {
				onUploadPic();
			}
		}
	}

	@Override
	public void onRequestFtpDataStart(int type) {
		// TODO Auto-generated method stub
		// ��ҳ��û�����غͻ�ȡ�ļ��б��������ⲻ���ǣ�@see DiscActivity��
		if (type == IFTPBaseListener.TYPE_UPLOAD)// �ϴ�--�����ļ�--��ʼ
		{
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_START);
		} else if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI)// �ϴ�--����ļ�--��ʼ
		{
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_MULTI_START);
		}
	}

	@Override
	public void onRequestFtpDataError(String errorMsg, int type) {
		// TODO Auto-generated method stub
		// ��ҳ��û�����غͻ�ȡ�ļ��б��������ⲻ���ǣ�@see DiscActivity��
		if (type == IFTPBaseListener.TYPE_UPLOAD)// �ϴ�--�����ļ�--����
		{
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_ERROR);
		} else if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI)// �ϴ�--����ļ�--����
		{
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_MULTI_ERROR);
		}
	}

	@Override
	public void onRequestFtpDataOneFileCompleted(Record record, int type) {
		// TODO Auto-generated method stub
		// ��ҳ��û�����غͻ�ȡ�ļ��б��������ⲻ���ǣ�@see DiscActivity��
		if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI)// �ϴ�--����ļ�--����һ�����
		{
			Message msg = mHandler.obtainMessage();
			msg.what = Constant.MSG_DOWNLOAD_MULTI_ONEFILE_COMPLETE;
			msg.obj = record;
			msg.sendToTarget();
		}
	}

	@Override
	public void onRequestFtpDataTransfered(int transeredLength, int type) {
		// TODO Auto-generated method stub
		// ��ҳ��û�����غͻ�ȡ�ļ��б��������ⲻ���ǣ�@see DiscActivity��
		Message msg = mHandler.obtainMessage();
		msg.obj = transeredLength;
		if (type == IFTPBaseListener.TYPE_UPLOAD)// �ϴ�--�����ļ�--�Ѿ��ϴ�����
		{
			msg.what = Constant.MSG_UPLOAD_TRANSFERED;
		} else if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI)// �ϴ�--����ļ�--�Ѿ��ϴ�����
		{
			msg.what = Constant.MSG_UPLOAD_MULTI_TRANSFERED;
		}
		msg.sendToTarget();
	}

	@Override
	public void onRequestFtpDataCompleted(int type) {
		// TODO Auto-generated method stub
		// ��ҳ��û�����غͻ�ȡ�ļ��б��������ⲻ���ǣ�@see DiscActivity��
		if (type == IFTPBaseListener.TYPE_UPLOAD)// �ϴ�--�����ļ�--���
		{
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_COMPLETE);
		} else if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI)// �ϴ�--����ļ�--���
		{
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_MULTI_COMPLETE);
		}
	}

}
