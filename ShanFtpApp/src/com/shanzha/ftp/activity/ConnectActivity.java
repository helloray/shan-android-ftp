package com.shanzha.ftp.activity;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shanzha.ftp.R;
import com.shanzha.ftp.core.FTPManager;
import com.shanzha.ftp.core.FtpApp;
import com.shanzha.ftp.inter.IBase;
import com.shanzha.ftp.net.FTPClient;
import com.shanzha.ftp.util.Constant;
import com.shanzha.ftp.util.DialogUtil;
import com.shanzha.ftp.util.SharePreferenceUtil;

/**
 * ����ftp�������Ľ���
 * 
 * @author ShanZha
 * @date 2012-9-26 10:44
 * 
 */
public class ConnectActivity extends Activity implements OnClickListener, IBase {

	private static final String TAG = "ConnectActivity";
	private String ExtraIp = "";
	/**
	 * �����ţ���ip�����
	 */
	private EditText mEtHost;
	/**
	 * �û��������
	 */
	private EditText mEtUsername;
	/**
	 * ���������
	 */
	private EditText mEtPswd;
	/**
	 * ȷ����ť
	 */
	private Button mBtnOk;
	/**
	 * ȡ����ť
	 */
	private Button mBtnCancel;
	/**
	 * �ȴ���
	 */
	private ProgressDialog mPd;
	// /**
	// * ftp ���Ӷ���
	// */
	// private FTPClient client;
	/**
	 * �ն�����
	 */
	private String device_name;
	/**
	 * Զ���ļ�������
	 */
	// private FTPFileManager mFileMgr;
	/**
	 * ��Ļ���
	 */
	private int mScreenWidth;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.MSG_CONNECT_START:
				DialogUtil.showWaittingDialog(mPd);
				break;
			case Constant.MSG_CONNECT_SUCCESS:
				Toast.makeText(ConnectActivity.this, "���ӳɹ�", Toast.LENGTH_SHORT)
						.show();
				DialogUtil.dismissWaittingDialog(mPd);
				// ע�⣺setResult()��finish()��˳�򣬷���������
				setResult(RESULT_OK);
				ConnectActivity.this.finish();
				break;
			case Constant.MSG_CONNECT_FAIL:
				Toast.makeText(ConnectActivity.this, "����ʧ��", Toast.LENGTH_SHORT)
						.show();
				DialogUtil.dismissWaittingDialog(mPd);
				setResult(RESULT_CANCELED);
				ConnectActivity.this.finish();
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		WindowManager manager = getWindowManager();
		Display display = manager.getDefaultDisplay();
		mScreenWidth = display.getWidth();

		setContentView(R.layout.ftp_input);

		initUI();
		initListener();
		initRes();
	}

	@Override
	public void initUI() {
		// TODO Auto-generated method stub
		mEtHost = (EditText) this.findViewById(R.id.ftp_input_et_ftpip);
		mEtUsername = (EditText) this
				.findViewById(R.id.ftp_input_et_ftpusername);
		mEtPswd = (EditText) this.findViewById(R.id.ftp_input_et_ftppassword);
		mBtnOk = (Button) this.findViewById(R.id.ftp_input_btn_ok);
		mBtnCancel = (Button) this.findViewById(R.id.ftp_input_btn_cancel);
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		mBtnOk.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
	}

	@Override
	public void initRes() {
		// TODO Auto-generated method stub
		mPd = DialogUtil.getWaittingProDialog(this, "", "��������...");
		device_name = Build.MODEL;

		// ���ô�activity�Ŀ��
		LinearLayout mLlAll = (LinearLayout) this
				.findViewById(R.id.ftp_input_ll_all);
		ViewGroup.LayoutParams params = mLlAll.getLayoutParams();
		params.width = mScreenWidth / 2;

		Intent intent = getIntent();
		if (null != intent && null != intent.getExtras()) {
			ExtraIp = intent.getExtras().getString("ip");
		}
		if (!TextUtils.isEmpty(ExtraIp)) {
			mEtHost.setText(ExtraIp);
			mEtUsername.setText("");
			mEtPswd.setText("");
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ftp_input_btn_ok:// ����
			onConnect();
			break;
		case R.id.ftp_input_btn_cancel:// ȡ��
			setResult(RESULT_CANCELED);
			this.finish();
			break;
		}
	}

	/**
	 * ����ftp������
	 */
	private void onConnect() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(Constant.MSG_CONNECT_START);
				String ip = mEtHost.getText().toString();
				String user = mEtUsername.getText().toString();
				String pswd = mEtPswd.getText().toString();
				FTPManager ftpMgr = FTPManager.getInstance();
				boolean isSuccess = false;
				ftpMgr.setFTPClient(new FTPClient());
				ftpMgr.setmTimeOut(30 * 1000);
				try {
					ftpMgr.connect(ip, FtpApp.DEFAULT_PORT);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					mHandler.sendEmptyMessage(Constant.MSG_CONNECT_FAIL);
					e.printStackTrace();
				}
				try {
					isSuccess = ftpMgr.login(user, pswd);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					isSuccess = false;
					mHandler.sendEmptyMessage(Constant.MSG_CONNECT_FAIL);
					e.printStackTrace();
				}

				if (isSuccess) {
					// �־û�ip��username��pswd
					SharePreferenceUtil.setFtpIp(ConnectActivity.this, ip);
					SharePreferenceUtil.setUserName(ConnectActivity.this, user);
					SharePreferenceUtil.setPassword(ConnectActivity.this, pswd);
					// ��½�ɹ�֮��һ�����豸����Ϊ�ļ������ֵ��ļ���
					try {
						ftpMgr.createDirectory(FtpApp.DEVICE_NAME);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mHandler.sendEmptyMessage(Constant.MSG_CONNECT_SUCCESS);

				} else {
					mHandler.sendEmptyMessage(Constant.MSG_CONNECT_FAIL);
				}
			}
		}).start();
	}

}
