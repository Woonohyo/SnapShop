package com.l3cache.snapshop.naversearch;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.l3cache.snapshop.R;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.volley.ExtendedImageLoader;
import com.l3cache.snapshop.volley.FeedImageView;

public class SearchResultsVolleyAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private ArrayList<NaverSearchResult> resultItems;
	private ExtendedImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public SearchResultsVolleyAdapter(Activity activity, ArrayList<NaverSearchResult> resultItems) {
		this.activity = activity;
		this.resultItems = resultItems;
	}

	@Override
	public int getCount() {
		return resultItems.size();
	}

	@Override
	public Object getItem(int position) {
		return resultItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (inflater == null)
			inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.searchresults_volley_list_row, null);

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		FeedImageView feedImageView = (FeedImageView) convertView.findViewById(R.id.searchresults_list_row_image_view);
		TextView itemNameTextView = (TextView) convertView
				.findViewById(R.id.searchresults_list_row_item_name_text_view);
		TextView itemPriceTextView = (TextView) convertView
				.findViewById(R.id.searchresults_list_row_itemPrice_text_view);

		TextView mallNameTextView = (TextView) convertView.findViewById(R.id.searchresults_list_row_mallName_text_view);

		NaverSearchResult item = resultItems.get(position);

		itemNameTextView.setText(Html.fromHtml(item.getTitle()));
		try {
			NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ko_KR"));
			format.setParseIntegerOnly(true);
			String formattedLowPrice = format.format(item.getLprice());
			itemPriceTextView.setText(formattedLowPrice);
		} catch (Exception e) {
			itemPriceTextView.setText("0");
		}
		mallNameTextView.setText(item.getMallName());
		
		if (item.getImage() != null) {
			feedImageView.setImageUrl(item.getImage(), imageLoader);
			feedImageView.setVisibility(View.VISIBLE);
			feedImageView.setResponseObserver(new FeedImageView.ResponseObserver() {
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
