package com.shanzha.ftp.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.shanzha.ftp.R;
import com.shanzha.ftp.core.FtpApp;
import com.shanzha.ftp.model.FileItem;
import com.shanzha.ftp.util.FileUtil;

/**
 * �ϴ������ļ�����������
 * 
 * @author hmy
 * @update ShanZha
 * @date 2012-10-15 14:49
 * 
 */
public class FileUploadAdapter extends BaseAdapter {

	private static final String TAG = "FileUploadAdapter";
	/** ������Ӧ�û��� **/
	private Context mContext;
	/** ���� **/
	private List<FileItem> mDataList;
	/**
	 * ѡ�еļ��� key:�ļ�����·�� value:�Ƿ�ѡ��
	 */
	private Map<String, Boolean> mCheckedMap = new HashMap<String, Boolean>();
	/**
	 * �ļ�ͼ��
	 */
	private Bitmap fileIcon;
	private Bitmap fileTxtIcon;
	private Bitmap fileApkIcon;
	private Bitmap fileMediaIcon;
	private Bitmap fileMusicIcon;
	private Bitmap filePicIcon;
	private Bitmap fileWordIcon;
	/**
	 * Ŀ¼ͼ��
	 */
	private Bitmap dirIcon;
	/**
	 * ���ɶ�Ŀ¼ͼ��
	 */
	private Bitmap dirFailIcon;
	/**
	 * �Ѿ��ϴ�����ͼ��
	 */
	private Bitmap uploadedIcon;

	public FileUploadAdapter(Context context, List<FileItem> items) {
		this.mContext = context;
		this.mDataList = items;
		initIcon();
	}

	private void initIcon() {
		fileIcon = BitmapFactory.decodeResource(this.mContext.getResources(),
				R.drawable.disc_file);
		fileTxtIcon = BitmapFactory.decodeResource(
				this.mContext.getResources(), R.drawable.file_text);
		fileApkIcon = BitmapFactory.decodeResource(
				this.mContext.getResources(), R.drawable.file_apk);
		fileMediaIcon = BitmapFactory.decodeResource(
				this.mContext.getResources(), R.drawable.file_media);
		fileMusicIcon = BitmapFactory.decodeResource(
				this.mContext.getResources(), R.drawable.file_music);
		filePicIcon = BitmapFactory.decodeResource(
				this.mContext.getResources(), R.drawable.file_picture);
		fileWordIcon = BitmapFactory.decodeResource(
				this.mContext.getResources(), R.drawable.file_word);

		dirIcon = BitmapFactory.decodeResource(this.mContext.getResources(),
				R.drawable.file_browser_folder);
		dirFailIcon = BitmapFactory.decodeResource(
				this.mContext.getResources(),
				R.drawable.file_browser_folder_false);
		uploadedIcon = BitmapFactory.decodeResource(
				this.mContext.getResources(), R.drawable.disc_upload);
	}

	public Map<String, Boolean> getmCheckedMap() {
		return mCheckedMap;
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
		return mDataList == null ? 0 : position;
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
			holder.mIvUploaded = (ImageView) convertView
					.findViewById(R.id.disc_item_iv_icon_downloaded);
			holder.mIvUploaded.setImageBitmap(uploadedIcon);
			holder.mTvName = (TextView) convertView
					.findViewById(R.id.disc_item_tv_name);
			holder.mTvSize = (TextView) convertView
					.findViewById(R.id.disc_item_tv_size);
			holder.mTvTime = (TextView) convertView
					.findViewById(R.id.disc_item_tv_time);
			holder.mCheck = (CheckBox) convertView
					.findViewById(R.id.disc_item_checkbox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FileItem item = mDataList.get(position);
		// �ļ�
		if (item.isFile()) {
			// holder.mIvIcon.setImageBitmap(fileIcon);
			setFileIcon(holder, item.getName());
			holder.mCheck.setVisibility(View.VISIBLE);
			holder.mTvSize.setVisibility(View.VISIBLE);
			holder.mTvSize.setText("("
					+ FileUtil.formatFileSize(item.getLength()) + ")");
			// �����Ƿ��Ѿ��ϴ���
			Map<String, String> map = FtpApp.getInstance().getmMapUploaded();
			String uri = item.getPath();
			String name = item.getName();
			if (map.containsKey(uri)) {
				String filename = map.get(uri);
				if (filename.equals(name)) {
					holder.mIvUploaded.setVisibility(View.VISIBLE);
				} else {
					holder.mIvUploaded.setVisibility(View.GONE);
				}
			} else {
				holder.mIvUploaded.setVisibility(View.GONE);
			}
		} else {
			holder.mCheck.setVisibility(View.GONE);
			holder.mTvSize.setVisibility(View.GONE);
			holder.mIvUploaded.setVisibility(View.GONE);
			if (item.isRead()) {
				holder.mIvIcon.setImageBitmap(dirIcon);
			} else {
				holder.mIvIcon.setImageBitmap(dirFailIcon);
			}
		}
		//
		holder.mTvName.setText(item.getName());
		holder.mTvTime.setVisibility(View.GONE);

		final String key = item.getPath();
		// ����item�Ƿ�ѡ��
		setCheckBox(holder, key);

		return convertView;

	}

	private class ViewHolder {
		ImageView mIvIcon;
		ImageView mIvUploaded;
		TextView mTvName;
		TextView mTvSize;
		TextView mTvTime;
		CheckBox mCheck;
	}

	/**
	 * �����ļ���ͼ��
	 * 
	 * @param holder
	 * @param filename
	 */
	private void setFileIcon(ViewHolder holder, String filename) {
		String filetype = FileUtil.getMIMEType(filename);
		if (filetype.indexOf("text/plain") != -1) {
			// txt
			holder.mIvIcon.setImageBitmap(fileTxtIcon);

		} else if (filetype.indexOf("image/") != -1) {
			// ͼƬ
			holder.mIvIcon.setImageBitmap(filePicIcon);
		} else if (filetype.indexOf("application/vnd.android.package-archive") != -1) {
			// apk�ļ�
			holder.mIvIcon.setImageBitmap(fileApkIcon);
		} else if (filetype.indexOf("application/msword") != -1) {
			// word�ļ�
			holder.mIvIcon.setImageBitmap(fileWordIcon);
		} else if (filetype.indexOf("video/") != -1) {
			// ��Ƶ
			holder.mIvIcon.setImageBitmap(fileMediaIcon);
		} else if (filetype.indexOf("audio/") != -1) {
			// ��Ƶ
			holder.mIvIcon.setImageBitmap(fileMusicIcon);
		} else {
			// ����
			holder.mIvIcon.setImageBitmap(fileIcon);
		}
	}

	/**
	 * �����Ƿ�ѡ��
	 * 
	 * @param holder
	 * @param key
	 */
	private void setCheckBox(ViewHolder holder, final String key) {
		if (mCheckedMap.containsKey(key)) {
			if (mCheckedMap.get(key)) {
				holder.mCheck.setChecked(true);
			} else {
				holder.mCheck.setChecked(false);
			}
		} else {
			holder.mCheck.setChecked(false);
		}
		holder.mCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox cb = (CheckBox) v;
				// �����Ƿ�ѡ������HashMapֵ
				if (cb.isChecked()) {
					if (mCheckedMap.containsKey(key)) {
						mCheckedMap.remove(key);
					} else {
						mCheckedMap.put(key, true);
					}
				} else {
					if (mCheckedMap.containsKey(key)) {
						mCheckedMap.remove(key);
					}
				}
				notifyDataSetChanged();
			}
		});
	}

}
