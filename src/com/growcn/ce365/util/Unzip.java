package com.growcn.ce365.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.os.Handler;
import android.os.Message;

public class Unzip implements Serializable {
	private static final int SUCCESS = 1;
	private static final int ERROR = 2;
	private static final int PROGRESS = 3;
	private OnUnzipListener mOnUnzipListener;

	/**
	 * Unzip the zip file to target dir
	 * 
	 * @param zipFile
	 * @param targetDir
	 */
	public void toUnzip(String zipFile, String targetDir) {

		Message message = new Message();
		message.what = SUCCESS;

		int SIZE = 4096; // buffer size: 4KB
		String strEntry; // each zip entry name
		try {
			BufferedOutputStream dest = null; // buffer output stream
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry; // each zip entry
			while ((entry = zis.getNextEntry()) != null) {
				try {
					int count;
					byte data[] = new byte[SIZE];
					strEntry = entry.getName();

					File entryFile = new File(targetDir + strEntry);
					File entryDir = new File(entryFile.getParent());
					if (!entryDir.exists()) {
						entryDir.mkdirs();
					}

					FileOutputStream fos = new FileOutputStream(entryFile);
					dest = new BufferedOutputStream(fos, SIZE);
					while ((count = zis.read(data, 0, SIZE)) != -1) {
						dest.write(data, 0, count);

						Message msg = Message.obtain();
						msg.what = PROGRESS;
						msg.obj = count;
						handler.sendMessage(msg);
					}
					dest.flush();
					dest.close();
				} catch (Exception ex) {
					message.what = ERROR;
					message.obj = ex.getMessage();
					ex.printStackTrace();
				}
			}
			zis.close();
			File fileszipe = new File(zipFile);
			fileszipe.delete();
		} catch (Exception cwj) {
			message.what = ERROR;
			message.obj = cwj.getMessage();
			cwj.printStackTrace();
		}
		handler.sendMessage(message);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				mOnUnzipListener.onSuccess();
				break;

			case PROGRESS:
				mOnUnzipListener
						.onProgress(Integer.parseInt(msg.obj.toString()));
				break;
			case ERROR:
				mOnUnzipListener.onError(msg.obj.toString());
				break;
			}
		}

	};

	public Unzip(final String zipFile, final String targetDir) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				toUnzip(zipFile, targetDir);
			}
		}).start();
	}

	public Unzip setOnUnzipListener(OnUnzipListener mOnUnzipListener) {
		this.mOnUnzipListener = mOnUnzipListener;
		return this;
	}

	public interface OnUnzipListener {
		public void onSuccess(); // 回调成功

		public void onError(String msg);

		public void onProgress(int percentage);
	}
}
