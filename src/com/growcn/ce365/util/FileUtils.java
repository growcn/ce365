package com.growcn.ce365.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EncodingUtils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FileUtils {
	private final static String TAG = "GrowcnWord";

	public static final String ENCODING_UTF8 = "UTF-8";

	private static FileUtils _instance = null;

	// sdcard path
	private static String m_externalDir;

	// private boolean b_ContinueDownload = false;
	boolean b_inDownloadProgress = false;

	private long downLoadSize;
	private long downLoadTotalSize;

	public static FileUtils getInstance() {
		/*
		 * if (_instance == null) { _instance = new FileUtils(); }
		 */
		return _instance;
	}

	// initialize
	synchronized public static FileUtils getInstance(Context context) {
		if (_instance == null) {
			_instance = new FileUtils(context);
		}
		return _instance;
	}

	/*
	 * private FileUtils() { // get external directory String state =
	 * Environment.getExternalStorageDirectory() + "/"; //Log.i(TAG,
	 * "===  FileUtils() external storage path: " + SDPath); if
	 * (Environment.MEDIA_MOUNTED.equals(state)) { m_externalDir = state; } }
	 */

	private FileUtils(Context context) {
		// get external directory
		String state = Environment.getExternalStorageState();
		Log.i(TAG, " external storage state: " + state);
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			m_externalDir = Environment.getExternalStorageDirectory() + "/";
		} else {
			Log.w(TAG, "cannot use sdcard");
			File baseDir = context.getFilesDir();
			// File the9cnDir = new File(baseDir, SpotlightActivity.THE9CN_DIR);
			m_externalDir = baseDir.getAbsolutePath() + "/";
		}
		Log.i(TAG, "external dir: " + m_externalDir);
	}

	public static boolean noSdcardPermission(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		return noPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ctx, pm);
	}

	public static boolean noPermission(String permission, Context ctx,
			PackageManager pm) {
		return PackageManager.PERMISSION_DENIED == pm.checkPermission(
				permission, ctx.getPackageName());
	}

	public long getDownloadSize() {
		return downLoadSize;
	}

	public long getDownloadTotalSize() {
		return downLoadTotalSize;
	}

	public static String getExternalDir() {
		return m_externalDir;
	}

	// create file on SD card
	public static File createSDFile(String dir, String filename)
			throws IOException {
		// File file = new File(SDPath + filename);
		File file = new File(m_externalDir + dir, filename);
		if (file.createNewFile())
			return file;
		else {
			Log.v(TAG, " file: " + (m_externalDir + filename)
					+ " created failed or exists alrealy!");
			return null;
		}
	}

	// create dir on SD card
	public static void createSDDir(String dirname) {
		File dir = new File(m_externalDir + dirname);
		if (!dir.exists())
			dir.mkdirs();
		Log.v(TAG, "Created dir: " + m_externalDir + dirname + " on sdcard!");
	}

	public static void deleteSDFile(String filename) throws IOException {
		File file = new File(m_externalDir, filename);
		if (file.exists()) {
			file.delete();
		}
	}

	// Check whether file exists
	public static boolean isSDFileExist(String filename) {
		File file = new File(m_externalDir + filename);
		return file.exists();
	}

	// read text file, return string
	public static String downloadStr(String urlStr) {
		StringBuffer strBuf = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			// create url object
			URL url = new URL(urlStr);
			// create http connection
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			// read buffer
			buffer = new BufferedReader(new InputStreamReader(
					urlcon.getInputStream()));

			while ((line = buffer.readLine()) != null) {
				strBuf.append(line);
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		} finally {
			try {
				if (buffer != null) {
					buffer.close();
				}
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		}
		return strBuf.toString();
	}

	public boolean downloadFile(String urlStr, String path, String filename)
			throws Exception {
		InputStream input = null;
		boolean b = false;
		try {
			// Log.i(TAG, "===== Download file to sdcard...");
			input = getInputStreamFromUrl(urlStr);
			writeInput2SDcard(path, filename, input);
			// if (b_ContinueDownload)
			b = true;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Log.e(TAG, e.toString());
					return false;
				}
			}
		}
		return b;
	}

	// get inputStream from url
	public InputStream getInputStreamFromUrl(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
		InputStream input = urlCon.getInputStream();
		downLoadTotalSize = urlCon.getContentLength(); // get file total size
		if (downLoadTotalSize <= 0)
			throw new RuntimeException("Cannot get file size!");
		return input;
	}

	// write inputStream data into SD card
	public void writeInput2SDcard(String path, String filename,
			InputStream input) throws Exception {
		OutputStream output = null;

		try {
			// create dir on sdcard
			createSDDir(path);
			// file = createSDFile(path, filename);
			// Log.i(TAG, "file in sdcard: " + file.exists());
			// b_ContinueDownload = true;
			output = new FileOutputStream(new File(m_externalDir + path,
					filename));
			byte buffer[] = new byte[32768];
			int count = -1; // write to output until input comes to the end
			b_inDownloadProgress = true; // start downloading
			downLoadSize = 0;

			while ((count = input.read(buffer)) != -1) {
				output.write(buffer, 0, count);
				downLoadSize += count;
			}
			output.flush();
			b_inDownloadProgress = false;

			// check file
			if (downLoadSize < downLoadTotalSize) {
				throw new RuntimeException("Download Uncompleted");
			}
		} catch (Exception e) {

			Log.e(TAG, e.toString());
			throw e;
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		}
	}

	public boolean isDownloading() {
		return b_inDownloadProgress;
	}

	public void resetDownloadStatus() {
		b_inDownloadProgress = false;
	}

	/**
	 * read file content
	 * 
	 * @param file
	 * @return
	 */
	public static String readFileContent(File file) {
		Log.i(TAG, "read file: " + file.getPath());
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line = "";
			while (br.ready()) {
				line = br.readLine();
				Log.i(TAG, "read : " + line);
			}
			br.close();
			fr.close();
			return line;
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		return null;
	}

	/**
	 * read assets file content
	 * 
	 * @param cxt
	 * @param filename
	 * @return
	 */
	public static String readAssetsFile(Context cxt, String filename) {
		Log.i(TAG, "read assets file: " + filename);
		String retBuf = "";
		try {
			InputStream in = cxt.getResources().getAssets().open(filename);
			int length = in.available();
			byte buffer[] = new byte[length];
			in.read(buffer);
			retBuf = EncodingUtils.getString(buffer, ENCODING_UTF8);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		return retBuf;
	}

	/**
	 * get files list under the given dir
	 * 
	 * @param dirname
	 * @return
	 */
	public static String[] getDirList(String dirname) {
		File dir = new File(dirname);
		String[] fileList = null;

		if (dir.isDirectory()) {
			fileList = dir.list();
		}

		return fileList;
	}
}
