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
 * 主界面
 * 
 * @author ShanZha
 * @date 2012-9-25 15:18
 * 
 */
public class MainActivity extends Activity implements IBase, OnClickListener,
		DialogInterface.OnClickListener, ILoadSmsContactListener,
		IFTPDataListener, IFTPDataMultiListener, ILoadPicListener {

	private static final String TAG = "MainActivity";
	/** 顶部提示用户的布局 **/
	private RelativeLayout mRlNotify;
	/** 顶部提示用户的信息 **/
	private TextView mTvNotify;
	/** 顶部重新连接的按钮 **/
	private Button mBtnRefresh;
	/** 一键同步 **/
	private Button mBtnOneKey;
	/** 同步图片 **/
	private Button mBtnPic;
	/** 同步联系人 **/
	private Button mBtnContact;
	/** 同步短信 **/
	private Button mBtnSms;
	/** 上传文件 **/
	private Button mBtnUpload;
	/** 网盘管理 **/
	private Button mBtnDisc;
	/** 连接的ProgressDialog **/
	private ProgressDialog mPdConnect;
	/** 同步的ProgressDialog **/
	private ProgressDialog mPdUpload;
	/** 是否上传的dialog **/
	private AlertDialog mUploadDialog;
	/** FTP管理者（一切关于FTP的请求均靠它） **/
	private FTPManager mFtpMgr;
	/** 当前点击的按钮id **/
	private int mClickId = -1;
	/** 当前上传的单个文件“记录”对象 **/
	private Record mCurrUpload;
	/** 当前已经上传的长度(单个/多个) **/
	private int mCurrUploadedLength;
	/** 当前要上传文件的总长度(单个/多个) **/
	private long mCurrUploadTotalSize;
	/** 当前已经上传的文件个数 **/
	private int mCurrUploadedCount;
	/** 当前所要上传的文件总数 **/
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

		mPdConnect = DialogUtil.getWaittingProDialog(this, "", "正在连接FTP服务器...");
		mPdUpload = DialogUtil.getHorizontalWaittingProDialog(this, "同步信息提示",
				"     正在同步...");
		mPdUpload.setButton("取消", mClickCancelListener);
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
		// 退出程序
		if (id == Constant.DIALOG_LOGOUT) {
			return DialogUtil.getCommonDialg(this, "提示信息", "您确定要退出程序吗？", this);
		} else if (id == Constant.DIALOG_UPLOAD)// 上传确定
		{
			return mUploadDialog = DialogUtil.getCommonDialg(this, "确认信息", "",
					this);
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:// 确定
			if (mClickId == R.id.main_btn_upload_onekey)// 确定“一键同步”
			{
				ToastUtils.show(MainActivity.this, "这个没做，你自己看着办吧", false);
			} else if (mClickId == R.id.main_btn_upload_pic)// 确定“同步图片”
			{
				DialogUtil.showWaittingDialog(mPdUpload);
				int picState = StateManager.getInstance().getPicState();
				if (picState == StateManager.STATE_COMPLETE) {
					onUploadPic();
				} else if (picState == StateManager.STATE_ERROR) {
					// 加载出错，重新加载
					FtpApp.getInstance().initPicFile(FileUtil.getSdcardPath(),
							this);
				} else if (picState == StateManager.STATE_PREPARE) {
					// 上传在回调里
				}
			} else if (mClickId == R.id.main_btn_upload_contact)// 确定“同步联系人”
			{
				DialogUtil.showWaittingDialog(mPdUpload);
				int contactState = StateManager.getInstance().getContactState();
				if (contactState == StateManager.STATE_COMPLETE)// 初始化完成
				{
					String uri = ContactManager.getInstance(MainActivity.this)
							.getLocalFilePath();
					onUploadSmsContact(FileUtil.CONTACT_FILENAME, uri);
				} else if (contactState == StateManager.STATE_ERROR)// 初始化出错
				{
					// 重新加载，完毕再次上传，上传也是在回调里
					FtpApp.getInstance().initContactFile(this, this);
				} else if (contactState == StateManager.STATE_PREPARE)// 初始化进行中
				{
					// 上传操作在回调里
				}
			} else if (mClickId == R.id.main_btn_upload_sms)// 确定“同步短信”
			{
				DialogUtil.showWaittingDialog(mPdUpload);
				int smsState = StateManager.getInstance().getSmsState();
				if (smsState == StateManager.STATE_COMPLETE) {
					String uri = SmsManager.getInstance(MainActivity.this)
							.getLocalFilePath();
					// 直接上传
					onUploadSmsContact(FileUtil.SMS_FILENAME, uri);
				} else if (smsState == StateManager.STATE_ERROR) {
					// 出错了，重新读写，上传也是在回调里
					FtpApp.getInstance().initSmsFile(this, this);
				} else if (smsState == StateManager.STATE_PREPARE) {
					// 正在准备，上传放到回调里
				}
			} else if (mClickId == -1)// 确定“退出程序”
			{
				MainActivity.this.stopService(mServiceIntent);
				Log.i(TAG, "一切都完了");
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
		case R.id.main_btn_refresh:// 重新连接FTP
			onConnect();
			break;
		case R.id.main_btn_upload_onekey:// 一键同步
			mClickId = R.id.main_btn_upload_onekey;
			showDialog(Constant.DIALOG_UPLOAD);
			if (null != mUploadDialog) {
				mUploadDialog.setMessage("	您确定要同步所有图片、联系人、短消息到服务器吗？");
			}
			break;
		case R.id.main_btn_upload_pic:// 图片
			mClickId = R.id.main_btn_upload_pic;
			showDialog(Constant.DIALOG_UPLOAD);
			if (null != mUploadDialog) {
				mUploadDialog.setMessage("	您确定要同步所有图片到服务器吗？");
			}
			break;
		case R.id.main_btn_upload_contact:// 联系人
			mClickId = R.id.main_btn_upload_contact;
			showDialog(Constant.DIALOG_UPLOAD);
			if (null != mUploadDialog) {
				mUploadDialog.setMessage("	您确定要同步联系人到服务器吗？");
			}
			break;
		case R.id.main_btn_upload_sms:// 短信
			mClickId = R.id.main_btn_upload_sms;
			showDialog(Constant.DIALOG_UPLOAD);
			if (null != mUploadDialog) {
				mUploadDialog.setMessage("	您确定要同步短消息到服务器吗？");
			}
			break;
		case R.id.main_btn_upload_files:// 同步文件
			Intent uploadIntent = new Intent(this, FileUploadActivity.class);
			startActivity(uploadIntent);
			break;
		case R.id.main_btn_upload_disc:// 网盘管理
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
	 * 当已经登录成功一次之后，再次进入或者重试时，登录连接
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
	 * 终止当前数据请求（正在请求时，用户主动取消）
	 */
	private void abortCurrDataRequest() {
		ToastUtils.show(MainActivity.this, "此次成功上传 " + mCurrUploadedCount
				+ " 个文件，剩余 " + (mCurrUploadTotalCount - mCurrUploadedCount)
				+ " 个未上传", false);
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
	 * 处理各种消息
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.MSG_CONNECT_START:// 开始连接
				DialogUtil.showWaittingDialog(mPdConnect);
				break;
			case Constant.MSG_CONNECT_SUCCESS:// 连接成功
				DialogUtil.dismissWaittingDialog(mPdConnect);
				mRlNotify.setVisibility(View.GONE);
				break;
			case Constant.MSG_CONNECT_FAIL:// 连接失败
				DialogUtil.dismissWaittingDialog(mPdConnect);
				mRlNotify.setVisibility(View.VISIBLE);
				mTvNotify.setText(getString(R.string.main_notify_info));
				break;
			case Constant.MSG_UPLOAD_START:// 单个文件上传--开始

				break;
			case Constant.MSG_UPLOAD_ERROR:// 单个文件上传--出错
				onCompletedOrError(true);
				reset();
				break;
			case Constant.MSG_UPLOAD_TRANSFERED:// 单个文件上传--已经上传多少
				int len = (Integer) msg.obj;
				mCurrUploadedLength += len;
				int progress = (int) (mCurrUploadedLength * (100.0 / (float) mCurrUploadTotalSize));
				mPdUpload.setProgress(progress);
				break;
			case Constant.MSG_UPLOAD_COMPLETE:// 单个文件上传--完成
				mCurrUploadedCount++;
				onCompletedOrError(false);
				// 持久化“已上传”
				FTPDBService.getInstance(MainActivity.this).saveRecord(
						mCurrUpload);
				String key = mCurrUpload.getUri();
				// 内存标记
				FtpApp.getInstance().getmMapUploaded()
						.put(key, mCurrUpload.getFilename());
				reset();
				break;
			case Constant.MSG_UPLOAD_MULTI_START:// 多个文件上传--开始

				break;
			case Constant.MSG_UPLOAD_MULTI_ERROR:// 多个文件上传--出错
				onCompletedOrError(true);
				reset();
				break;
			case Constant.MSG_UPLOAD_MULTI_TRANSFERED:// 多个文件上传--已经上传多少
				int multiLen = (Integer) msg.obj;
				// mCurrUploadedLength += multiLen;
				int multiProgress = (int) (multiLen * (100.0 / (float) mCurrUploadTotalSize));
				mPdUpload.setProgress(multiProgress);
				break;
			case Constant.MSG_UPLOAD_MULTI_ONEFILE_COMPLETE:// 多个文件上传--其中一个完成
				Record upload = (Record) msg.obj;
				// 持久化“已上传”
				FTPDBService.getInstance(MainActivity.this).saveRecord(upload);
				String key2 = upload.getUri();
				FtpApp.getInstance().getmMapUploaded()
						.put(key2, upload.getFilename());
				break;
			case Constant.MSG_UPLOAD_MULTI_COMPLETE:// 多个文件上传--完成
				onCompletedOrError(false);
				reset();
				break;
			}
		}

	};

	/**
	 * 处理上传完成/出错的本地方法
	 * 
	 * @param isError
	 */
	private void onCompletedOrError(boolean isError) {
		DialogUtil.dismissWaittingDialog(mPdUpload);
		if (isError) {
			DialogUtil.dismissWaittingDialog(mPdUpload);
			mPdUpload.setProgress(0);
			if (mClickId == R.id.main_btn_upload_contact) {
				ToastUtils.show(MainActivity.this, "上传联系人出错", false);
			} else if (mClickId == R.id.main_btn_upload_sms) {
				ToastUtils.show(MainActivity.this, "上传短信出错", false);
			} else {
				ToastUtils.show(MainActivity.this, "上传文件出错", false);
			}
		} else {
			if (mClickId == R.id.main_btn_upload_contact) {
				ToastUtils.show(MainActivity.this, "上传联系人完成", false);
			} else if (mClickId == R.id.main_btn_upload_sms) {
				ToastUtils.show(MainActivity.this, "上传短信完成", false);
			} else {
				ToastUtils.show(MainActivity.this, "上传文件完成", false);
			}
		}

	}

	private void onUploadSmsContact(String filename, String uri) {
		// 直接上传
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
				// 已经上传过，则排除
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
				ToastUtils.show(MainActivity.this, "没有新文件需要上传", false);
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
	 * 重置
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
		// 短息/联系人加载完毕时，假如用户已经点击上传短信/联系人，则上传
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
			// 当用户点击上传图片时，加载还在准备中，此时，待完成时，直接上传
			if (mClickId == R.id.main_btn_upload_pic) {
				onUploadPic();
			}
		}
	}

	@Override
	public void onRequestFtpDataStart(int type) {
		// TODO Auto-generated method stub
		// 此页面没有下载和获取文件列表，所以在这不考虑（@see DiscActivity）
		if (type == IFTPBaseListener.TYPE_UPLOAD)// 上传--单个文件--开始
		{
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_START);
		} else if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI)// 上传--多个文件--开始
		{
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_MULTI_START);
		}
	}

	@Override
	public void onRequestFtpDataError(String errorMsg, int type) {
		// TODO Auto-generated method stub
		// 此页面没有下载和获取文件列表，所以在这不考虑（@see DiscActivity）
		if (type == IFTPBaseListener.TYPE_UPLOAD)// 上传--单个文件--出错
		{
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_ERROR);
		} else if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI)// 上传--多个文件--出错
		{
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_MULTI_ERROR);
		}
	}

	@Override
	public void onRequestFtpDataOneFileCompleted(Record record, int type) {
		// TODO Auto-generated method stub
		// 此页面没有下载和获取文件列表，所以在这不考虑（@see DiscActivity）
		if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI)// 上传--多个文件--其中一个完成
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
		// 此页面没有下载和获取文件列表，所以在这不考虑（@see DiscActivity）
		Message msg = mHandler.obtainMessage();
		msg.obj = transeredLength;
		if (type == IFTPBaseListener.TYPE_UPLOAD)// 上传--单个文件--已经上传多少
		{
			msg.what = Constant.MSG_UPLOAD_TRANSFERED;
		} else if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI)// 上传--多个文件--已经上传多少
		{
			msg.what = Constant.MSG_UPLOAD_MULTI_TRANSFERED;
		}
		msg.sendToTarget();
	}

	@Override
	public void onRequestFtpDataCompleted(int type) {
		// TODO Auto-generated method stub
		// 此页面没有下载和获取文件列表，所以在这不考虑（@see DiscActivity）
		if (type == IFTPBaseListener.TYPE_UPLOAD)// 上传--单个文件--完成
		{
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_COMPLETE);
		} else if (type == IFTPBaseListener.TYPE_UPLOAD_MULTI)// 上传--多个文件--完成
		{
			mHandler.sendEmptyMessage(Constant.MSG_UPLOAD_MULTI_COMPLETE);
		}
	}

}
