package com.growcn.ce365.util;import com.growcn.ce365.ParagraphActivity;import com.growcn.ce365.util.AppConstant.ActivityParams;import android.content.Context;import android.content.Intent;public class OpenIntent {	public static void inParagraphActivity(Context mContext, String LessonId) {		Intent intent = new Intent(mContext, ParagraphActivity.class);		intent.putExtra(ActivityParams.ID, LessonId);		mContext.startActivity(intent);	}}