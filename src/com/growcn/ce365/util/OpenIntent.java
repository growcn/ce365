package com.growcn.ce365.util;import com.growcn.ce365.MainActivity;import com.growcn.ce365.ParagraphActivity;import com.growcn.ce365.util.AppConstant.ActivityParams;import android.content.Context;import android.content.Intent;public class OpenIntent {	public static void inParagraphActivity(Context mContext, String LessonUid) {		Intent intent = new Intent(mContext, ParagraphActivity.class);		intent.putExtra(ActivityParams.UUID, LessonUid);		mContext.startActivity(intent);	}	public static void index(Context mContext) {		Intent intent = new Intent(mContext, MainActivity.class);		mContext.startActivity(intent);	}}