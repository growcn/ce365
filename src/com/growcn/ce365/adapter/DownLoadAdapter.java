package com.growcn.ce365.adapter;

import java.util.List;
import java.util.Map;

import com.growcn.ce365.R;
import com.growcn.ce365.model.Lesson;
import com.growcn.ce365.model.ResourceEntity;
import com.growcn.ce365.util.AppConstant.Config;
import com.growcn.ce365.util.FileUtils;
import com.growcn.ce365.util.Unzip;
import com.growcn.ce365.util.AppConstant.Dir;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DownLoadAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private OnClickListener click;
	private List<ResourceEntity> data;

	public DownLoadAdapter(Context context, List<ResourceEntity> data) {
		this.context = context;

		mInflater = LayoutInflater.from(context);
		this.data = data;
	}

	public void refresh(List<ResourceEntity> data) {
		this.data = data;
		this.notifyDataSetChanged();
	}

	public void setOnclick(OnClickListener click) {
		this.click = click;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ResourceEntity bean = data.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.download_item, null);
			holder = new ViewHolder();
			holder.resouceName = (TextView) convertView
					.findViewById(R.id.tv_resouce_name);
			holder.startDownload = (Button) convertView
					.findViewById(R.id.btn_start);
			holder.pauseDownload = (Button) convertView
					.findViewById(R.id.btn_pause);
			holder.btn_unzip = (Button) convertView
					.findViewById(R.id.btn_unzip);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.resouceName.setText(bean.name);
		holder.resouceName.setTag(bean.file_url);

		holder.btn_unzip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startUnzip();
			}
		});

		return convertView;
	}

	/**
	 * 解压
	 */
	public void startUnzip() {
		String zipFileName = "ce365.zip";
		String SD_PATH = Dir.DLTMP();
		final String localfile = SD_PATH + zipFileName;
		Unzip mUnzip = new Unzip(localfile, Dir.DLAudio());
		mUnzip.setOnUnzipListener(new Unzip.OnUnzipListener() {
			@Override
			public void onSuccess() {
				Log.e(Config.TAG, "Unzip onSuccess !");
			}

			@Override
			public void onError(String msg) {
				Log.e(Config.TAG, "Unzip error:" + msg);
			}
		});
	}

	public OnClickListener getClick() {
		return click;
	}

	public void setClick(OnClickListener click) {
		this.click = click;
	}

	private class ViewHolder {
		public TextView resouceName;
		public Button startDownload;
		public Button pauseDownload;
		public Button btn_unzip;
	}

}
