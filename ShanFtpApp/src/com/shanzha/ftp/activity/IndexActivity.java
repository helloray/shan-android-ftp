package com.shanzha.ftp.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.shanzha.ftp.R;
import com.shanzha.ftp.inter.IBase;
import com.shanzha.ftp.util.DialogUtil;
import com.shanzha.ftp.util.SharePreferenceUtil;

/**
 * Index页面，即第一次进入项目的入口
 * 
 * @author ShanZha
 * @date 2012-9-25 11:33
 */
public class IndexActivity extends Activity implements IBase, OnClickListener {

	private static final String TAG = "MainActivity";
	/**
	 * 自动搜索
	 */
	private Button mBtnSearch;
	/**
	 * 手动输入
	 */
	private Button mBtnInput;
	/**
	 * 等待框
	 */
	private ProgressDialog mProDialog;
	/**
	 * 设备的Ip（String类型）
	 */
	private String mDeviceIp;
	/**
	 * 设备ip（int类型）
	 */
	private int mDeviceIpInt;
	/**
	 * ping成功的ip集合
	 */
	private ArrayList<String> mSuccessIps;
	/**
	 * Wifi管理者
	 */
	private WifiManager mWifiMgr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index);

		initUI();
		initListener();
		initRes();
	}

	@Override
	public void initUI() {
		// TODO Auto-generated method stub
		mBtnSearch = (Button) this.findViewById(R.id.index_btn_search);
		mBtnInput = (Button) this.findViewById(R.id.index_btn_input);
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		mBtnSearch.setOnClickListener(this);
		mBtnInput.setOnClickListener(this);
	}

	@Override
	public void initRes() {
		// TODO Auto-generated method stub
		String ftpIp = SharePreferenceUtil.getFtpIp(this);
		if (TextUtils.isEmpty(ftpIp)) {
			mWifiMgr = (WifiManager) this
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = mWifiMgr.getConnectionInfo();
			mDeviceIpInt = wifiInfo.getIpAddress();
			mDeviceIp = intToIp(mDeviceIpInt);
			Log.i(TAG, " DeviceIp = " + mDeviceIp);

			mProDialog = DialogUtil.getWaittingProDialog(this, "", "正在搜索...");
		} else {
			this.finish();
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("isFirst", false);
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.index_btn_search:// 自动搜索
			search();
			break;
		case R.id.index_btn_input:// 手动输入
			Intent intent = new Intent(IndexActivity.this,
					ConnectActivity.class);
			startActivityForResult(intent, REQUEST_CODE);
			break;
		}
	}

	/**
	 * 搜索同一网段的ip
	 */
	private void search() {
		DialogUtil.showWaittingDialog(mProDialog);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String[] ips = getSegmentIps(mDeviceIpInt);
				mSuccessIps = ping(ips);
				mHandler.sendEmptyMessage(MSG_PING_OVER);
			}
		}).start();
	}

	/**
	 * 获取同一个网段能ping通ip集合
	 * 
	 * @param ips
	 * @return
	 */
	private ArrayList<String> ping(String[] ips) {
		Process[] p = new Process[255];
		ArrayList<String> result = new ArrayList<String>();
		try {
			for (int i = 0; i < ips.length; i++) {
				String ip = ips[i];
				if (ip == null)
					continue;
				p[i] = Runtime.getRuntime().exec("ping -c 1 -w 10 " + ip);
			}
			for (int i = 0; i < p.length; i++) {
				Process pp = p[i];
				if (ips[i] == null)
					continue;
				if (0 == pp.waitFor()) {
					result.add(ips[i]);
				}
				pp.destroy();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据本机ip获取同一网段的所有ip地址（255个）
	 * 
	 * @param localIp
	 * @return
	 */
	private String[] getSegmentIps(int localIp) {
		String[] ips = new String[255];
		String segment = (localIp & 0xFF) + "." + ((localIp >> 8) & 0xFF) + "."
				+ ((localIp >> 16) & 0xFF) + ".";
		for (int i = 0; i < 255; i++) {
			if (mDeviceIp.equals(segment + i))
				continue;
			ips[i] = segment + i;
		}
		return ips;
	}

	/**
	 * 把int类型的ip转为String类型
	 * 
	 * @param i
	 * @return
	 */
	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	/**
	 * 处理各种消息事件
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_PING_OVER:
				DialogUtil.dismissWaittingDialog(mProDialog);
				if (null == mSuccessIps) {
					return;
				}
				Log.i(TAG, "Ping successfully size = " + mSuccessIps.size());
				if (mSuccessIps.size() == 0) {
					Intent intent = new Intent(IndexActivity.this,
							ConnectActivity.class);
					startActivityForResult(intent, REQUEST_CODE);
				} else {
					Intent intent = new Intent(IndexActivity.this,
							FTPServersActivity.class);
					intent.putExtra("serverip", mSuccessIps);
					startActivityForResult(intent, REQUEST_CODE);
				}
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE) {
				this.finish();
				Intent intent = new Intent(this, MainActivity.class);
				intent.putExtra("isFirst", true);
				startActivity(intent);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 搜索完毕
	 */
	private static final int MSG_PING_OVER = 0;
	private static final int REQUEST_CODE = 1;
}
