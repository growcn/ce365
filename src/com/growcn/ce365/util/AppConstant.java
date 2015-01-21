package com.growcn.ce365.util;import java.io.File;import android.content.Context;import android.os.Environment;import android.util.Log;public interface AppConstant {	public class Config {		public static final String TAG = "ce365";		public static final boolean DEVELOPMENT = true;		public static final String PRO_DIR = "growcnCE365";		public static final String DL_DIR = "tmp";		public static final String IMAGE_DIR = "image_cache";		public static final String AUDIO_DIR = "audio";		public static final String TMP_DIR = "tmp";		public static final String PIC_DIR = "pic_cache";		public static final String DB_DIR = "db";		public static final String APK_NAME = "ce365";		public static final String FileFormat = ".json";		public static final String AdmobID = "ca-app-pub-4865376519299138/5595834403";		// 是否要显示广告		public static final Boolean isAdvModel = true;	}	public class Dir {		public static final String DB() {			return BaseDir(Config.DB_DIR);		}		public static final String DLPath() {			return BaseDir(Config.DL_DIR);		}		public static final String DLAudio() {			return BaseDir(Config.AUDIO_DIR);		}		public static final String DLTMP() {			return BaseDir(Config.TMP_DIR);		}		public static final String Root() {			String sdpath = Environment.getExternalStorageDirectory() + "/"					+ Config.PRO_DIR + "/";			File dir = new File(sdpath);			if (!dir.exists()) {				dir.mkdirs();			}			return sdpath;		}		public static final String BaseDir(String myProDir) {			String sdpath = Root() + myProDir + "/";			File dir = new File(sdpath);			if (!dir.exists()) {				dir.mkdirs();			}			return sdpath;		}	}	public class PlayerMsg {		public static final int INIT_MSG = 0;		public static final int PLAY_MSG = 1;		public static final int PAUSE_MSG = 2;		public static final int STOP_MSG = 3;	}	public class ActivityParams {		public final static String ID = "_id";		public final static String UUID = "_uuid";		public final static String LessonUuid = "lesson_uuid";		public final static String MSG = "_MSG";	}	public class ServerApi {		public final static String domain = "https://growcn.com/jdi";		// public final static String domain = "https://jdi.growcn.com";		// public final static String domain = "http://192.168.1.61:3001";		// public final static String ToKen = "bc1c9870a66e0744bd79-1";		public final static String BookName = "crazyenglish365";		public final static String books_path() {			return domain + "/api/books/" + BookName;		}		public final static String LessonAll() {			return books_path() + "/lessons";		}		public final static String ParagraphAll(String id) {			return books_path() + "/lessons/" + id;		}		public final static String Resources(String pageage_name) {			String url = "";			url += domain;			url += "/api/app/resources?";			url += "package_name=" + pageage_name;			return url;		}		public final static String Audio(String uuid) {			String url = "";			url += domain;			url += "/api/books/audio/" + uuid;			return url;		}		public final static String Device(Context mContext, String pageage_name) {			String channel = new ReadManifestUtil(mContext, "UMENG_CHANNEL")					.getVal();			String url = "";			url += domain;			url += "/api/app/device?";			url += "package_name=" + pageage_name + "&";			url += "channel=" + channel;			return url;		}		public final static String uploadVerson(Context mContext,				String pageage_name) {			String channel = new ReadManifestUtil(mContext, "UMENG_CHANNEL")					.getVal();			String url = "";			url += domain;			url += "/api/app/version?";			url += "package_name=" + pageage_name + "&";			url += "channel=" + channel;			return url;		}	}}