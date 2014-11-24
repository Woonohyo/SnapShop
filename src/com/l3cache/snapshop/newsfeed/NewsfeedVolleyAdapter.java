package com.l3cache.snapshop.newsfeed;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.data.NewsfeedData;

public class NewsfeedVolleyAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private ArrayList<NewsfeedData> newsfeedDatas;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public NewsfeedVolleyAdapter(Activity activity, ArrayList<NewsfeedData> newsfeedDatas) {
		this.activity = activity;
		this.newsfeedDatas = newsfeedDatas;
	}

	@Override
	public int getCount() {
		return newsfeedDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return newsfeedDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		   if (inflater == null)
	            inflater = (LayoutInflater) activity
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        if (convertView == null)
	            convertView = inflater.inflate(R.layout.newsfeed_volley_list_row, null);
	 
	        if (imageLoader == null)
	            imageLoader = AppController.getInstance().getImageLoader();
	 
	        FeedImageView feedImageView = (FeedImageView) convertView
	                .findViewById(R.id.feedImage1);
	 
	        NewsfeedData item = newsfeedDatas.get(position);
	 
	        // Feed image
	        if (item.getImage() != null) {
	            feedImageView.setImageUrl(item.getImage(), imageLoader);
	            feedImageView.setVisibility(View.VISIBLE);
	            feedImageView
	                    .setResponseObserver(new FeedImageView.ResponseObserver() {
	                        @Override
	                        public void onError() {
	                        }
	 
	                        @Override
	                        public void onSuccess() {
	                        }
	                    });
	        } else {
	            feedImageView.setVisibility(View.GONE);
	        }
	 
	        return convertView;
	}

}
