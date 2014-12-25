package com.growcn.ce365.db;

import java.io.File;

import com.growcn.ce365.util.AppConstant.Dir;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DBBaseHelper extends SQLiteOpenHelper {
	private static final int DABABASE_VERSION = 1;

	public static final String DB_NAME = "ce365" + ".DB";
	private static DBBaseHelper mInstance = null;

	public static void init(Context context) {
		if (mInstance == null) {
			mInstance = new DBBaseHelper(context);
		}
	}

	public static DBBaseHelper getInstance() {
		return mInstance;
	}

	public DBBaseHelper(Context context) {
		// 保存在指定义的位置
		super(context, Dir.Root() + DB_NAME, null, DABABASE_VERSION);

		// 系统默认路径
		// super(context, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// AccountDB.create(db);
		LessonDb.create(db);
		ParagraphDb.create(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}
}
