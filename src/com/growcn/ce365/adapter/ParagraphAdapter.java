package com.growcn.ce365.adapter;

import java.util.List;

import com.growcn.ce365.R;
import com.growcn.ce365.model.Lesson;
import com.growcn.ce365.model.Paragraph;
import com.growcn.ce365.util.AppConstant.Config;
import com.loopj.android.image.SmartImageView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ParagraphAdapter extends BaseAdapter {
	private static final String ftag = Config.TAG;
	private List<Paragraph> data;
	LayoutInflater layoutInflater;
	private ListView mListView;
	private Context mContext;
	protected View.OnClickListener mOnClickListener;

	public ParagraphAdapter(Context context, List<Paragraph> data,
			ListView mListView) {
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
		this.mListView = mListView;
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout adpic_layout = null;

		ViewHolder mViewHodler = null;
		if (convertView == null) {
			mViewHodler = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.lesson_item, null);
			// convertView.setTag(position);
			mViewHodler.mTitle = (TextView) convertView
					.findViewById(R.id.title);

			convertView.setTag(mViewHodler);// 绑定ViewHolder对象
		} else {
			mViewHodler = (ViewHolder) convertView.getTag();// 取出ViewHolder对
		}

		Paragraph mData = data.get(position);
		mViewHodler.mTitle.setText(mData.name);
		return convertView;
	}

	public final class ViewHolder {
		public TextView mTitle;
	}

	/**
	 * 得到数据的总数
	 */
	@Override
	public int getCount() {
		return data.size();
	}

	/**
	 * 根据数据索引得到集合所对应的数据
	 */
	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	// 根据位置，得到相应的item对象的id
	@Override
	public long getItemId(int position) {
		return position;
	}

}
