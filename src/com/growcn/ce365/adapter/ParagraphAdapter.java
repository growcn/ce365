package com.growcn.ce365.adapter;

import java.io.IOException;
import java.util.List;

import com.growcn.ce365.R;
import com.growcn.ce365.model.Lesson;
import com.growcn.ce365.model.Paragraph;
import com.growcn.ce365.util.AppConstant.Config;
import com.growcn.ce365.util.AppConstant.Dir;
import com.growcn.ce365.util.Download;
import com.loopj.android.image.SmartImageView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ParagraphAdapter extends BaseAdapter {
	private static final String ftag = Config.TAG;
	private List<Paragraph> data;
	LayoutInflater layoutInflater;
	private ListView mListView;
	private Context mContext;
	protected View.OnClickListener mOnClickListener;
	protected MediaPlayer mMediaPlayer;

	public ParagraphAdapter(Context context, List<Paragraph> data,
			ListView mListView, MediaPlayer mMediaPlayer) {
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
		this.mListView = mListView;
		this.mContext = context;
		this.mMediaPlayer = mMediaPlayer;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout adpic_layout = null;

		ViewHolder mViewHodler = null;
		if (convertView == null) {
			mViewHodler = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.lesson_item, null);
			// convertView.setTag(position);
			mViewHodler.mTitle = (TextView) convertView
					.findViewById(R.id.title);

			convertView.setTag(mViewHodler);// 绑定ViewHolder对象
		} else {
			mViewHodler = (ViewHolder) convertView.getTag();// 取出ViewHolder对
		}

		Paragraph mData = data.get(position);
		final String audio_url = mData.audio_url;
		final String DL_file_name = mData.token_name + ".mp3";
		mViewHodler.mTitle.setText(mData.name);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Log.e(Config.TAG, "......audio_url:" + audio_url);
				DLplayAudio(audio_url, DL_file_name);
			}

		});
		return convertView;
	}

	private void playAudio(String audio_url) {

		try {
			Uri load_file = Uri.parse(audio_url);
			MediaPlayer mp = MediaPlayer.create(mContext, load_file);
			mp.setLooping(false);
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.stop();
					mp.release();
				}
			});
		} catch (Exception e) {
			Log.e(Config.TAG, "playAudio:" + e.getMessage());
			// showToast("找不到发音文件", "long");
		}

		//
		// if (mMediaPlayer == null) {
		// mMediaPlayer = new MediaPlayer();
		// }
		//
		// try {
		// mMediaPlayer.isPlaying();
		// } catch (IllegalStateException e) {
		// mMediaPlayer = null;
		// mMediaPlayer = new MediaPlayer();
		// }
		//
		// try {
		// mMediaPlayer.setDataSource(audio_url);
		// mMediaPlayer.prepare();
		// mMediaPlayer.start();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// Log.e(Config.TAG,
		// "mMediaPlayer IllegalArgumentException:" + e.getMessage());
		// } catch (IllegalStateException e) {
		// e.printStackTrace();
		// Log.e(Config.TAG,
		// "mMediaPlayer IllegalStateException:" + e.getMessage());
		// } catch (IOException e) {
		// e.printStackTrace();
		// Log.e(Config.TAG, "mMediaPlayer IOException:" + e.getMessage());
		// }
		// mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
		// @Override
		// public void onCompletion(MediaPlayer mp) {
		// mp.stop();
		// mp.release();
		// mp = null;
		// }
		// });
	}

	private void DLplayAudio(String audio_url, final String dl_filename) {
		Download d = new Download(1, audio_url, Dir.DLAudio(), dl_filename);
		d.setOnDownloadListener(new Download.OnDownloadListener() {
			@Override
			public void onSuccess(int downloadId) {
				System.out.println(downloadId + "下载成功");
				String file = Dir.DLAudio() + dl_filename;

				// Log.e(Config.TAG, "......file:" + file);
				playAudio(file);
			}

			@Override
			public void onStart(int downloadId, long fileSize) {
				System.out.println(downloadId + "开始下载，文件大小：" + fileSize);
			}

			@Override
			public void onPublish(int downloadId, long size) {
				System.out.println("更新文件" + downloadId + "大小：" + size);
			}

			@Override
			public void onPause(int downloadId) {
				System.out.println("暂停下载" + downloadId);
			}

			@Override
			public void onGoon(int downloadId, long localSize) {
				System.out.println("继续下载" + downloadId);
			}

			@Override
			public void onError(int downloadId) {
				System.out.println("下载出错" + downloadId);
			}

			@Override
			public void onCancel(int downloadId) {
				System.out.println("取消下载" + downloadId);
			}

		});

		d.start(false);
	}

	public final class ViewHolder {
		public TextView mTitle;
	}

	/**
	 * 得到数据的总数
	 */
	@Override
	public int getCount() {
		return data.size();
	}

	/**
	 * 根据数据索引得到集合所对应的数据
	 */
	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	// 根据位置，得到相应的item对象的id
	@Override
	public long getItemId(int position) {
		return position;
	}

}
