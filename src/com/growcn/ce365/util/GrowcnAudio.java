package com.growcn.ce365.util;

import java.io.File;

import com.growcn.ce365.util.AppConstant.Config;
import com.growcn.ce365.util.AppConstant.Dir;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.util.Log;

public class GrowcnAudio {
	private static GrowcnAudio _instance = null;
	private Context mContext;

	public GrowcnAudio(Context context) {
		this.mContext = context;
	}

	// initialize
	synchronized public static GrowcnAudio getInstance(Context context) {
		if (_instance == null) {
			_instance = new GrowcnAudio(context);
		}
		return _instance;
	}

	
	
	
	
	/**
	 * 
	 * 下载并播放mp3文件
	 * 
	 * 
	 * @param url
	 *            下载地址
	 * 
	 * @param localPath
	 *            本地存放地址
	 * 
	 * @param DLname
	 *            下载以后的命名
	 * 
	 * 
	 */
	public void onlinePlay(String url, String localPath, String DLname) {
		String audio_url = Dir.DLAudio() + DLname;
		if (new File(audio_url).exists()) {
			GrowcnAudio.getInstance(mContext).play(audio_url);
		} else {
			down_and_play(url, localPath, DLname);
		}
	}

	/**
	 * 
	 * 播放本地的mp3文件
	 * 
	 * 
	 * @param audio_url
	 *            本地mp3地址
	 * 
	 * 
	 */
	public void play(String localFile) {

		try {
			Uri load_file = Uri.parse(localFile);
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
	}

	// 进度条
	public ProgressDialog dialog = null;

	public void ShowProgressBarDialog() {
		dialog = new ProgressDialog(mContext);
		// dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// dialog.setIcon(R.drawable.ic_launcher);
		// dialog.setTitle("正在加载...");
		dialog.setMessage("正在下载发音文件中...");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();
	}

	public void CloseProgressBarDialog() {
		dialog.dismiss();
	}

	/**
	 * 
	 * 下载并播放mp3文件
	 * 
	 * 
	 * @param url
	 *            下载地址
	 * 
	 * @param localPath
	 *            本地存放地址
	 * 
	 * @param DLname
	 *            下载以后的命名
	 * 
	 * 
	 */
	private void down_and_play(String url, String localPath, String DLname) {
		ShowProgressBarDialog();
		final String mFile = Dir.DLAudio() + DLname;
		Download d = new Download(1, url, Dir.DLAudio(), DLname);
		d.setOnDownloadListener(new Download.OnDownloadListener() {
			@Override
			public void onSuccess(int downloadId) {
				System.out.println(downloadId + "下载成功");
				CloseProgressBarDialog();
				play(mFile);
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
				Log.e(Config.TAG, "继续下载" + downloadId);
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
}
