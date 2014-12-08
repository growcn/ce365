package com.growcn.ce365.plugin.upload_apk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;

import com.growcn.ce365.R;
import com.growcn.ce365.util.AppConstant.Config;
import com.growcn.ce365.util.AppConstant.Dir;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UpdateManager {
	// 下载中
	private static final int DOWNLOAD = 1;
	// 下载结束
	private static final int DOWNLOAD_FINISH = 2;
	// 下载保存路径
	private String mSavePath;
	// 记录进度条数量
	private int progress;
	// 是否取消更新
	private boolean cancelUpdate = false;

	private Context mContext;
	// 更新进度条
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	// 地本的版本号
	private int versionCode = 0;
	// 地本的包名
	private String packageName;
	private String mJump_activity;
	private VersionCheck mVercheck;
	// private String mSavePath;
	private boolean isShowToast = true;

	private String soft_update_no = "已经是最新版本";
	private String soft_update_title = "软件更新";
	private String soft_update_info = "检测到新版本，立即更新吗?";
	private String soft_update_updatebtn = "更新";
	private String soft_update_later = "稍后更新";
	private String soft_updating = "正在更新";
	private String soft_update_cancel = "取消";

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 
	 * @param context
	 * @param server_code
	 *            服务器的code
	 * @param server_name
	 *            服务器的name
	 * @param server_url
	 *            服务器的下载地址
	 * @param jump_activity
	 *            跳转到哪个Activity
	 */

	public UpdateManager(Context context, int server_code, String server_name,
			String server_url, String jump_activity) {
		this.mJump_activity = jump_activity;
		this.mContext = context;
		isShowToast = false;
		// 获取包名
		getVersionCode(mContext);
		// 查看服务版本
		mVercheck = VersionCheck.getData(packageName);

		VersionCheck mVercheck = new VersionCheck();
		mVercheck.name = server_name;
		mVercheck.code = server_code;
		mVercheck.file_url = server_url;

		mSavePath = save_path(mContext);
	}

	public UpdateManager(Context context) {
		this.mContext = context;
		// 获取包名
		getVersionCode(mContext);

		// 查看服务版本
		mVercheck = VersionCheck.getData(packageName);
		mSavePath = save_path(mContext);
	}

	/**
	 * 检测软件更新
	 * 
	 * @return
	 */
	public boolean checkUpdate() {
		if (isUpdate()) {
			// 显示提示对话框
			showNoticeDialog();
			return true;
		} else {
			// 如果从首页检查，那么就不显示
			if (isShowToast) {
				Toast.makeText(mContext, soft_update_no, Toast.LENGTH_LONG)
						.show();
			}
			return false;

		}

	}

	/**
	 * 检查软件是否有更新版本
	 * 
	 * @return
	 */
	private boolean isUpdate() {
		// 版本判断
		if (mVercheck.code > versionCode) {
			return true;
		}

		return false;
	}

	private String save_path(Context context) {
		// Dir.DLPath();
		FileUtils.getInstance(context);
		FileUtils.createSDDir(Config.PRO_DIR);
		String down_dir = Config.PRO_DIR + "/tmp";
		FileUtils.createSDDir(down_dir);
		// 获得存储卡的路径
		String sdpath = Environment.getExternalStorageDirectory() + "/"
				+ down_dir;
		return sdpath;
	}

	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	private void getVersionCode(Context context) {
		PackageInfo info;

		try {
			info = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
			String versionName = info.versionName; // 当前应用的版本名称
			packageName = info.packageName; // 当前版本的包名
			versionCode = info.versionCode; // 当前版本的版本号
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog() {
		// 构造对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(soft_update_title);
		builder.setMessage(soft_update_info);
		// 更新
		builder.setPositiveButton(soft_update_updatebtn, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 显示下载对话框
				showDownloadDialog();
			}
		});
		// 稍后更新
		builder.setNegativeButton(soft_update_later, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				cancel_button(dialog);
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * 显示软件下载对话框
	 */

	private void showDownloadDialog() {
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(soft_updating);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_bar, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// 取消更新
		builder.setNegativeButton(soft_update_cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				cancel_button(dialog);
				// 设置取消状态
				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// 现在文件
		downloadApk();
	}

	// 消除更新的按钮
	private void cancel_button(DialogInterface dialog) {
		if (mJump_activity.equals("")) {
			dialog.dismiss();
			// } else {
			// // Intent intent = new Intent(mContext,
			// // MainTabCardJnActivity.class);
			// // mContext.startActivity(intent);
			// OpenIntent.openMainTabCardAll(mContext, MenuShare.J2Val);

			// ((Activity) mContext).finish();
		}
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 * @author coolszy
	 * @date 2012-4-26
	 * @blog http://blog.92coding.com
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {

					URL url = new URL(mVercheck.file_url);
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, Config.APK_NAME);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do {
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, Config.APK_NAME);
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
