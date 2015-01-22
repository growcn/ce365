package com.growcn.ce365.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.growcn.ce365.R;
import com.growcn.ce365.util.AppConstant.Config;
import com.growcn.ce365.util.AppConstant.Dir;
import com.growcn.ce365.util.DbVersion;
import com.growcn.ce365.util.Log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DBBaseHelper extends SQLiteOpenHelper {
	private static final int DABABASE_VERSION = 5;

	public static final String DB_NAME = "ce365" + ".DB";
	public static final String DB_PATH = Dir.DB();
	private static String databaseFilename = Dir.DB() + DB_NAME;

	private static DBBaseHelper mInstance = null;
	private Context mContext;
	private SQLiteDatabase myDataBase = null;

	//

	public static void init(Context context) {
		if (mInstance == null) {
			mInstance = new DBBaseHelper(context);
		}
	}

	public static DBBaseHelper getInstance() {
		return mInstance;
	}

	public DBBaseHelper(Context context) {
		// 必须通过super调用父类当中的构造函数

		// 保存在指定义的位置
		super(context, DB_PATH + DB_NAME, null, DABABASE_VERSION);
		// 系统默认路径
		// super(context, DB_NAME, null, DABABASE_VERSION);

		this.mContext = context;
	}

	public DBBaseHelper(Context context, String action) {
		this(context);
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		LessonDb.create(db);
		ParagraphDb.create(db);
		DownLoadDB.create(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// If you need to add a new column
		if (newVersion > oldVersion) {
			int upgradeTo = oldVersion + 1;
			while (upgradeTo <= newVersion) {
				switch (upgradeTo) {
				case 3:
					// DownLoadDB.create(db);
					break;
				case 4:
					// update jqk_tab set sort=-1,is_hide=0 where name = "前(Ｊ2)"

					break;
				case 9:
					// update jqk_tab set sort=-1,is_hide=0 where name = "前(Ｊ2)"

					break;
				case 11:

					break;
				}

				upgradeTo++;
			}
		}

	}

	// ///////////////////////////////////////////
	public void createDataBase() throws IOException {
		Log.e(Config.TAG, "createDataBase.............");
		// 检测是否要删除
		DbVersion mDbVersion = new DbVersion(mContext);

		Log.e(Config.TAG, "getVersion():" + mDbVersion.getVersion());

		if (mDbVersion.isUpdate()) {
			Log.e(Config.TAG, "deleteDataBase...true..");
			deleteDataBase();
		} else {
			Log.e(Config.TAG, "deleteDataBase...false..");
		}

		boolean dbExist = checkDataBase();
		if (dbExist) {
			// 数据库已存在，do nothing.
		} else {
			// 创建数据库
			try {
				File dir = new File(DB_PATH);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File dbf = new File(DB_PATH + DB_NAME);
				if (dbf.exists()) {
					dbf.delete();
				}
				SQLiteDatabase.openOrCreateDatabase(dbf, null);
				// 复制asseets中的db文件到DB_PATH下
				copyDataBase();
				mDbVersion.setCurrentVersion();
			} catch (IOException e) {
				// throw new Error("数据库创建失败");
			}
		}

		init(mContext);
	}

	// 删除数据库
	public void deleteDataBase() {

		try {
			File dir = new File(DB_PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File dbf = new File(DB_PATH + DB_NAME);
			if (dbf.exists()) {
				dbf.delete();
			}
		} catch (Exception e) {
			// throw new Error("数据库创建失败");
			Log.e(Config.TAG, "deleteDataBase  error:" + e.getMessage());
		}

	}

	@Override
	public synchronized void close() {
		if (myDataBase != null) {
			myDataBase.close();
		}
		super.close();
	}

	// 检查数据库是否有效
	private boolean checkDataBase() {
		String myPath = DB_PATH + DB_NAME;

		// SQLiteDatabase checkDB = null;
		// try {
		// checkDB = SQLiteDatabase.openDatabase(myPath, null,
		// SQLiteDatabase.OPEN_READONLY);
		// } catch (SQLiteException e) {
		// // Log.e(Config.TAG, "checkDB SQLiteException error:" +
		// // e.getMessage());
		// } catch (Exception e) {
		// // Log.e(Config.TAG, "checkDB error:" + e.getMessage());
		// }
		// if (checkDB != null) {
		// checkDB.close();
		// }
		// return checkDB != null ? true : false;

		return doesDatabaseExist(mContext, myPath);
	}

	private static boolean doesDatabaseExist(Context context, String dbName) {
		File dbFile = context.getDatabasePath(dbName);
		return dbFile.exists();
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {
		// Open your local db as the input stream
		// InputStream myInput = mContext.getResources().openRawResource(
		// R.raw.ce365);
		InputStream myInput = mContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	// 复制assets下的大数据库文件时用这个
	private void copyBigDataBase() throws IOException {

	}

}
