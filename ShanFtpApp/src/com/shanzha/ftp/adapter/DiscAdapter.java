package com.shanzha.ftp.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.shanzha.ftp.R;
import com.shanzha.ftp.core.FtpApp;
import com.shanzha.ftp.model.FTPFile;
import com.shanzha.ftp.util.Constant;
import com.shanzha.ftp.util.FileUtil;
import com.shanzha.ftp.util.StringUtil;

/**
 * @author ShanZha
 * @date 2012-9-29（中秋前夕） 10:19
 */
public class DiscAdapter extends BaseAdapter {

	private static final String TAG = "DiscAdapter";
	/** 所要展示的数据 **/
	private List<FTPFile> mDataList;
	/** 应用上下文环境对象 **/
	private Context mContext;
	/** 文件图标 **/
	private Bitmap mBmFile;
	/** 文件夹图标 **/
	private Bitmap mBmDirectory;
	/** 是否选中的缓存 key--position value: isCheck **/
	private Map<Integer, Boolean> mCheckMap = new HashMap<Integer, Boolean>();
	/** 是不是下载模式 **/
	private boolean isDownloadModel = false;

	public DiscAdapter(List<FTPFile> data, Context context) {
		this.mDataList = data;
		this.mContext = context;
		mBmFile = BitmapFactory.decodeResource(this.mContext.getResources(),
				R.drawable.disc_file);
		mBmDirectory = BitmapFactory.decodeResource(
				this.mContext.getResources(), R.drawable.disc_folder);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataList == null ? 0 : mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDataList == null ? null : mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.disc_item, null);
			holder.mIvIcon = (ImageView) convertView
					.findViewById(R.id.disc_item_iv_icon);
			holder.mIvDownloaded = (ImageView) convertView
					.findViewById(R.id.disc_item_iv_icon_downloaded);
			holder.mTvName = (TextView) convertView
					.findViewById(R.id.disc_item_tv_name);
			holder.mTvSize = (TextView) convertView
					.findViewById(R.id.disc_item_tv_size);
			holder.mTvTime = (TextView) convertView
					.findViewById(R.id.disc_item_tv_time);
			holder.mCheckBox = (CheckBox) convertView
					.findViewById(R.id.disc_item_checkbox);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FTPFile file = mDataList.get(position);
		if (file.isDirectory()) {
			holder.mTvSize.setVisibility(View.GONE);
			holder.mCheckBox.setVisibility(View.GONE);
			holder.mIvIcon.setImageBitmap(mBmDirectory);
		} else if (file.isFile()) {
			holder.mTvSize.setVisibility(View.VISIBLE);
			// holder.mCheckBox.setVisibility(View.VISIBLE);
			holder.mIvIcon.setImageBitmap(mBmFile);
			if (isDownloadModel) {
				holder.mCheckBox.setVisibility(View.VISIBLE);
			} else {
				holder.mCheckBox.setVisibility(View.GONE);
			}
			holder.mTvSize.setText(FileUtil.formatFileSize(file.getSize()));
		}
		holder.mTvName.setText(file.getName());
		holder.mTvTime.setText(file.getTime());

		setDownloaded(holder, file);

		setCheckBox(holder, position);

		return convertView;
	}

	private class ViewHolder {
		private ImageView mIvIcon;
		private ImageView mIvDownloaded;
		private TextView mTvName;
		private TextView mTvSize;
		private TextView mTvTime;
		private CheckBox mCheckBox;
	}

	/**
	 * 设置是否已经下载过
	 * 
	 * @param holder
	 * @param file
	 */
	private void setDownloaded(ViewHolder holder, FTPFile file) {
		Map<String, String> mapDownloaded = FtpApp.getInstance()
				.getmMapDownloaded();
		String filename = file.getName();
		String remotePath = file.getRemotePath();
		String key = "";
		if (remotePath.equals(Constant.FTP_ROOT)) {
			key = remotePath + filename;
		} else {
			key = remotePath + Constant.SEPERATOR + filename;
		}
		// Log.i(TAG,"remoteDir = "+key+" filename = "+filename);
		if (mapDownloaded.containsKey(key)) {
			String value = mapDownloaded.get(key);
			if (value.equals(filename)) {
				holder.mIvDownloaded.setVisibility(View.VISIBLE);
			} else {
				holder.mIvDownloaded.setVisibility(View.GONE);
			}
		} else {
			holder.mIvDownloaded.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置是否选中的CheckBox
	 * 
	 * @param holder
	 * @param pos
	 */
	private void setCheckBox(ViewHolder holder, final int pos) {
		if (mCheckMap.containsKey(pos)) {
			boolean isChecked = mCheckMap.get(pos);
			if (isChecked) {
				holder.mCheckBox.setChecked(true);
			} else {
				holder.mCheckBox.setChecked(false);
			}
		} else {
			holder.mCheckBox.setChecked(false);
		}
		holder.mCheckBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox cb = (CheckBox) v;
				if (cb.isChecked()) {
					if (mCheckMap.containsKey(pos)) {
						mCheckMap.remove(pos);
					} else {
						mCheckMap.put(pos, true);
					}
				} else {
					mCheckMap.put(pos, false);
				}
			}
		});
	}

	public Map<Integer, Boolean> getmCheckMap() {
		return mCheckMap;
	}

	public boolean isDownloadModel() {
		return isDownloadModel;
	}

	public void setDownloadModel(boolean isDownloadModel) {
		this.isDownloadModel = isDownloadModel;
	}

}
