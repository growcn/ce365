package com.growcn.ce365.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.growcn.ce365.R;
import com.growcn.ce365.activity.DownLoadActivity;
import com.growcn.ce365.model.Lesson;
import com.growcn.ce365.model.LoadInfo;
import com.growcn.ce365.model.ResourceEntity;
import com.growcn.ce365.thread.Downloader;
import com.growcn.ce365.util.AppConstant.Config;
import com.growcn.ce365.util.FileUtils;
import com.growcn.ce365.util.Unzip;
import com.growcn.ce365.util.AppConstant.Dir;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class DownLoadAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private OnClickListener click;
	private List<ResourceEntity> data;

	//

	// 固定存放下载的音乐的路径：SD卡目录下
	private static final String SD_PATH = Dir.DLTMP();
	// 存放各个下载器
	private Map<String, Downloader> downloaders = new HashMap<String, Downloader>();
	// 存放与下载器对应的进度条
	private Map<String, ProgressBar> ProgressBars = new HashMap<String, ProgressBar>();

	// private String zipFileName = "ce365.zip";

	// /

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
			holder.bar = (ProgressBar) convertView
					.findViewById(R.id.ProgressBar_load);
			// holder.btn_unzip = (Button) convertView
			// .findViewById(R.id.btn_unzip);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.resouceName.setText(bean.name);
		holder.resouceName.setTag(bean.file_url);
		final String down_url = bean.file_url;
		final String localfile = SD_PATH + bean.uuid + ".zip";
		final ProgressBar mBar = holder.bar;
		holder.startDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBar.setVisibility(View.VISIBLE);
				startDownload(v, down_url, localfile);
			}
		});

		holder.pauseDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseDownload(v, down_url);
			}
		});

		// holder.btn_unzip.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// startUnzip();
		// }
		// });

		return convertView;
	}

	/**
	 * 响应开始下载按钮的点击事件
	 */
	public void startDownload(View v, String down_url, String localfile) {
		// // 得到textView的内容
		// LinearLayout layout = (LinearLayout) v.getParent();
		// String resouceName = ((TextView) layout
		// .findViewById(R.id.tv_resouce_name)).getTag().toString();
		// String urlstr = resouceName;
		//
		// String localfile = SD_PATH + zipFileName;
		// 设置下载线程数为4，这里是我为了方便随便固定的
		String threadcount = "4";
		DownloadTask downloadTask = new DownloadTask(v);
		downloadTask.execute(down_url, localfile, threadcount);

	};

	/**
	 * 响应暂停下载按钮的点击事件
	 */
	public void pauseDownload(View v, String urlstr) {
		// LinearLayout layout = (LinearLayout) v.getParent();
		// String resouceName = ((TextView) layout
		// .findViewById(R.id.tv_resouce_name)).getTag().toString();
		// String urlstr = resouceName;

		downloaders.get(urlstr).pause();

		Button btn_start = (Button) ((View) v.getParent())
				.findViewById(R.id.btn_start);
		Button btn_pause = (Button) ((View) v.getParent())
				.findViewById(R.id.btn_pause);
		btn_pause.setVisibility(View.GONE);
		btn_start.setVisibility(View.VISIBLE);
	}

	class DownloadTask extends AsyncTask<String, Integer, LoadInfo> {
		Downloader downloader = null;
		View v = null;
		String urlstr = null;

		public DownloadTask(final View v) {
			this.v = v;
		}

		@Override
		protected void onPreExecute() {
			Button btn_start = (Button) ((View) v.getParent())
					.findViewById(R.id.btn_start);
			Button btn_pause = (Button) ((View) v.getParent())
					.findViewById(R.id.btn_pause);
			btn_start.setVisibility(View.GONE);
			btn_pause.setVisibility(View.VISIBLE);
		}

		@Override
		protected LoadInfo doInBackground(String... params) {
			urlstr = params[0];
			String localfile = params[1];
			int threadcount = Integer.parseInt(params[2]);
			// 初始化一个downloader下载器
			downloader = downloaders.get(urlstr);
			if (downloader == null) {
				downloader = new Downloader(urlstr, localfile, threadcount,
						context, mHandler);
				downloaders.put(urlstr, downloader);
			}
			if (downloader.isdownloading())
				return null;
			// 得到下载信息类的个数组成集合
			return downloader.getDownloaderInfors();
		}

		@Override
		protected void onPostExecute(LoadInfo loadInfo) {
			if (loadInfo != null) {
				// 显示进度条
				showProgress(loadInfo, urlstr, v);
				// 调用方法开始下载
				downloader.download();
			}
		}

	};

	/**
	 * 显示进度条
	 */
	private void showProgress(LoadInfo loadInfo, String url, View v) {
		ProgressBar bar = ProgressBars.get(url);
		if (bar == null) {
			bar = (ProgressBar) ((View) v.getParent())
					.findViewById(R.id.ProgressBar_load);
			bar.setVisibility(View.VISIBLE);
			bar.setMax(loadInfo.getFileSize());
			bar.setProgress(loadInfo.getComplete());
			ProgressBars.put(url, bar);
		}
	}

	/**
	 * 利用消息处理机制适时更新进度条
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				String url = (String) msg.obj;
				int length = msg.arg1;
				ProgressBar bar = ProgressBars.get(url);
				if (bar != null) {
					// 设置进度条按读取的length长度更新
					bar.incrementProgressBy(length);
					if (bar.getProgress() == bar.getMax()) {
						RelativeLayout layout = (RelativeLayout) bar
								.getParent();
						TextView resouceName = (TextView) layout
								.findViewById(R.id.tv_resouce_name);

						Toast.makeText(context,
								"[" + resouceName.getText() + "]下载完成！",
								Toast.LENGTH_SHORT).show();
						// 下载完成后清除进度条并将map中的数据清空
						layout.removeView(bar);

						//
						startUnzip(resouceName, downloaders.get(url)
								.getlocalfile());

						ProgressBars.remove(url);
						downloaders.get(url).delete(url);
						downloaders.get(url).reset();
						downloaders.remove(url);

						Button btn_start = (Button) layout
								.findViewById(R.id.btn_start);
						Button btn_pause = (Button) layout
								.findViewById(R.id.btn_pause);
						btn_pause.setVisibility(View.GONE);
						btn_start.setVisibility(View.GONE);

					}
				}
			}
		}
	};

	/**
	 * 解压
	 */
	public void startUnzip(final TextView mTextView, String localfile) {
		final String text = mTextView.getText().toString();
		mTextView.setText(text + "[开始解压]");
		Log.e(Config.TAG, "Unzip onStart !");
		// String zipFileName = "ce365.zip";
		// String SD_PATH = Dir.DLTMP();
		// final String localfile = SD_PATH + zipFileName;
		Unzip mUnzip = new Unzip(localfile, Dir.DLAudio());
		mUnzip.setOnUnzipListener(new Unzip.OnUnzipListener() {
			@Override
			public void onSuccess() {
				// Log.e(Config.TAG, "Unzip onSuccess !");
				mTextView.setText(text + "[解压成功]");
			}

			@Override
			public void onError(String msg) {
				// Log.e(Config.TAG, "Unzip error:" + msg);
				mTextView.setText(text + "[解压出错]");
			}

			@Override
			public void onProgress(int percentage) {
				// Log.e(Config.TAG, "Unzip percentage:" + percentage);
				mTextView.setText(text + "[解压中..]");
			}
		});
	}

	// public OnClickListener getClick() {
	// return click;
	// }
	//
	// public void setClick(OnClickListener click) {
	// this.click = click;
	// }

	private class ViewHolder {
		public TextView resouceName;
		public Button startDownload;
		public Button pauseDownload;
		public Button btn_unzip;
		public ProgressBar bar;
	}

}
