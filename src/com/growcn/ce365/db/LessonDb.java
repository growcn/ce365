package com.growcn.ce365.db;import java.io.File;import java.util.ArrayList;import java.util.Date;import java.util.List;import com.growcn.ce365.model.Lesson;import com.growcn.ce365.util.AppConstant.Config;import android.content.ContentValues;import android.content.Context;import android.database.Cursor;import android.database.SQLException;import android.database.sqlite.SQLiteDatabase;import android.database.sqlite.SQLiteDatabase.CursorFactory;import android.database.sqlite.SQLiteOpenHelper;import android.util.Log;public class LessonDb {	private static final int DABABASE_VERSION = 1;	public static String TABLE_NAME = "lessons";	private static final String fTag = Config.TAG + TABLE_NAME;	public static final String TABLE_FIELD_ID = "id";	public static final String TABLE_FIELD_NAME = "name";	public static final String TABLE_FIELD_UUID = "uuid";	public static final String TABLE_FIELD_SORT = "sort";	public static final String TABLE_FIELD_LAST_TIME = "lastmodifytime";	public static void create(SQLiteDatabase db) {		try {			// 建表的时候，字段不能移动，读取数据的时候会按顺序读取的。			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("					+ TABLE_FIELD_ID + " INTEGER PRIMARY KEY NOT NULL, "					+ TABLE_FIELD_NAME + " VARCHER NOT NULL, "					+ TABLE_FIELD_UUID + " VARCHER NOT NULL, "					+ TABLE_FIELD_SORT + " INTEGER NOT NULL,"					+ TABLE_FIELD_LAST_TIME + " INTEGER NOT NULL " + ");");		} catch (SQLException ex) {			Log.e(fTag, ex.toString());		}	}	/**	 * 添加数据	 * 	 * @param id	 * @param name	 * @param uuid	 * @param lastmodifytime	 */	public static void add(int id, String uuid, String name, int sort,			int lastmodifytime) {		try {			SQLiteDatabase db = DBBaseHelper.getInstance()					.getWritableDatabase();			if (db.isOpen()) {				ContentValues values = new ContentValues();				// values.put(TABLE_FIELD_ID, id);				values.put(TABLE_FIELD_NAME, name);				values.put(TABLE_FIELD_UUID, uuid);				values.put(TABLE_FIELD_SORT, sort);				values.put(TABLE_FIELD_LAST_TIME, lastmodifytime);				db.insert(TABLE_NAME, null, values);				db.close();			}		} catch (SQLException e) {			// Log.e("Account DB", e.toString());		}	}	public static void find_uuid_and_update(String uuid, int id, String name,			int sort, int lastmodifytime) {		try {			SQLiteDatabase db = DBBaseHelper.getInstance()					.getWritableDatabase();			if (db.isOpen()) {				ContentValues values = new ContentValues();				values.put(TABLE_FIELD_ID, id);				values.put(TABLE_FIELD_NAME, name);				// values.put(TABLE_FIELD_UUID, uuid);				values.put(TABLE_FIELD_SORT, sort);				values.put(TABLE_FIELD_LAST_TIME, lastmodifytime);				db.update(TABLE_NAME, values, TABLE_FIELD_UUID + "=?",						new String[] { uuid });				db.close();			}		} catch (SQLException e) {			// Log.e("Account DB", e.toString());		}	}	/*	 * 查找所有的数据	 */	public static List<Lesson> findAll() {		List<Lesson> lessons = null;		SQLiteDatabase db = DBBaseHelper.getInstance().getReadableDatabase();		if (db.isOpen()) {			Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,					TABLE_FIELD_SORT + " ASC");			lessons = new ArrayList<Lesson>();			while (cursor.moveToNext()) {				Lesson mLesson = new Lesson();				mLesson = setLesson(cursor);				lessons.add(mLesson);			}			db.close();		}		return lessons;	}	/**	 * 	 * @param uuid	 * @return	 */	public static Lesson FindByUuid(String uuid) {		Lesson mLesson = null;		SQLiteDatabase db = DBBaseHelper.getInstance().getReadableDatabase();		if (db.isOpen()) {			Cursor cursor = db.query(TABLE_NAME, null, TABLE_FIELD_UUID + "=?",					new String[] { uuid }, null, null, TABLE_FIELD_SORT							+ " ASC " + " limit 1");			while (cursor.moveToNext()) {				mLesson = setLesson(cursor);			}			db.close();		}		return mLesson;	}	private static Lesson setLesson(Cursor cursor) {		Lesson mLesson = new Lesson();		mLesson.id = cursor.getInt(cursor.getColumnIndex(TABLE_FIELD_ID));		mLesson.name = cursor				.getString(cursor.getColumnIndex(TABLE_FIELD_NAME));		mLesson.uuid = cursor				.getString(cursor.getColumnIndex(TABLE_FIELD_UUID));		mLesson.sort = cursor.getInt(cursor.getColumnIndex(TABLE_FIELD_SORT));		mLesson.lastmodifytime = cursor.getInt(cursor				.getColumnIndex(TABLE_FIELD_LAST_TIME));		return mLesson;	}	/**	 * uuid 是否在	 * 	 * @param uuid	 * @return true| false	 */	public static boolean find_by_uuid_exists(String uuid) {		boolean result = false;		SQLiteDatabase db = DBBaseHelper.getInstance().getReadableDatabase();		if (db.isOpen()) {			Cursor cursor = db.query(TABLE_NAME, null, TABLE_FIELD_UUID + "=?",					new String[] { uuid }, null, null, null);			if (cursor.moveToFirst()) {				result = true;			}			cursor.close();			db.close();		}		return result;	}	/**	 * 同步book数据	 */	public static void sync(Lesson lesson) {		Lesson mLesson = FindByUuid(lesson.uuid);		if (mLesson != null) {			// Lesson是否要更新			if (mLesson.lastmodifytime != lesson.lastmodifytime) {				find_uuid_and_update(lesson.uuid, lesson.id, lesson.name,						lesson.sort, lesson.lastmodifytime);			}		} else {			// Lesson不存在			add(lesson.id, lesson.uuid, lesson.name, lesson.sort,					lesson.lastmodifytime);		}		// if (find_by_uuid_exists(lesson.uuid)) {		// find_uuid_and_update(lesson.uuid, lesson.id, lesson.name,		// lesson.sort, lesson.lastmodifytime);		// } else {		// add(lesson.id, lesson.uuid, lesson.name, lesson.sort,		// lesson.lastmodifytime);		// }	}	// private isOpenDb() {	//	// }	// @Override	// public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)	// {	// // If you need to add a new column	// if (newVersion > oldVersion) {	// int upgradeTo = oldVersion + 1;	// while (upgradeTo <= newVersion) {	// switch (upgradeTo) {	// case 7:	//	// break;	// case 8:	//	// break;	// case 9:	//	// break;	// case 11:	//	// break;	// }	//	// upgradeTo++;	// }	// }	//	// }}