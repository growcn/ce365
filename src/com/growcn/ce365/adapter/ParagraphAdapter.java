package com.growcn.ce365.adapter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.growcn.ce365.R;
import com.growcn.ce365.base.ActivityUtil;
import com.growcn.ce365.model.Lesson;
import com.growcn.ce365.model.Paragraph;
import com.growcn.ce365.service.PlayerService;
import com.growcn.ce365.util.AppConstant.Config;
import com.growcn.ce365.util.AppConstant.Dir;
import com.growcn.ce365.util.Download;
import com.growcn.ce365.util.GrowcnAudio;
import com.loopj.android.image.SmartImageView;

import android.app.Activity;
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
import android.widget.ImageView;
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
	private Activity mActivity;

	private ImageView mPlay_control;
	protected View.OnClickListener mOnClickListener;

	public ParagraphAdapter(Activity mActivity, List<Paragraph> data,
			ListView mListView, ImageView mPlay_control) {
		this.mContext = mActivity;
		this.mActivity = mActivity;
		layoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
		this.mListView = mListView;

		this.mPlay_control = mPlay_control;
		load_play();
	}

	public void load_play() {
		mPlay_control.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				play_word();
			}
		});
	}

	public void play_word() {

		Intent intent = new Intent();
		intent.setClass(mActivity, PlayerService.class);

		// intent.putExtra("mp3Info", mp3Info);
		// intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
		mContext.startService(intent);

		// for (int i = 0; i < data.size(); i++) {
		// Paragraph mData = data.get(i);
		// final String audio_url = mData.audio_url;
		// final String DL_file_name = mData.token_name + ".mp3";
		// DLplayAudio(audio_url, DL_file_name);
		// }
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout adpic_layout = null;

		ViewHolder mViewHodler = null;
		if (convertView == null) {
			mViewHodler = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.garagraph_item, null);
			// convertView.setTag(position);
			mViewHodler.mName = (TextView) convertView.findViewById(R.id.name);
			mViewHodler.mTranslation = (TextView) convertView
					.findViewById(R.id.translation);

			convertView.setTag(mViewHodler);// 绑定ViewHolder对象
		} else {
			mViewHodler = (ViewHolder) convertView.getTag();// 取出ViewHolder对
		}

		Paragraph mData = data.get(position);
		final String audio_url = mData.audio_url;
		final String DL_file_name = mData.uuid + ".mp3";
		mViewHodler.mName.setText(mData.name);
		mViewHodler.mTranslation.setText(mData.translation);

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Log.e(Config.TAG, "......audio_url:" + audio_url);
				DLplayAudio(audio_url, DL_file_name);
			}

		});
		return convertView;
	}

	private void DLplayAudio(String audio_url, final String dl_filename) {
		GrowcnAudio.getInstance(mContext).onlinePlay(audio_url, Dir.DLAudio(),
				dl_filename);
	}

	public final class ViewHolder {
		public TextView mName;
		public TextView mTranslation;
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
