package com.l3cache.snapshop.myposts;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapConstants;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.newsfeed.Newsfeed;
import com.l3cache.snapshop.retrofit.DefaultResponse;
import com.l3cache.snapshop.retrofit.SnapShopService;
import com.l3cache.snapshop.volley.ExtendedImageLoader;
import com.l3cache.snapshop.volley.FeedImageView;

public class MyPostsAdapter extends RealmBaseAdapter<MyPost> {
	private Button deleteButton;
	ExtendedImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public MyPostsAdapter(Context context, RealmResults<MyPost> realmResults, boolean automaticUpdate) {
		super(context, realmResults, automaticUpdate);
	}

	@Override
	public int getCount() {
		return realmResults.size();
	}

	@Override
	public MyPost getItem(int location) {
		return realmResults.get(location);
	}

	@Override
	public long getItemId(int position) {
		return realmResults.get(position).getPid();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null)
			convertView = inflater.inflate(R.layout.my_posts_volley_list_row, null);

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		MyPost item = realmResults.get(position);

		FeedImageView feedImageView = (FeedImageView) convertView.findViewById(R.id.my_post_image_view);
		TextView titleTextView = (TextView) convertView.findViewById(R.id.my_post_title_text_view);
		titleTextView.setText(item.getTitle());

		TextView snapsTextView = (TextView) convertView.findViewById(R.id.my_post_snaps_text_view);
		snapsTextView.setText("+ " + item.getNumLike());

		deleteButton = (Button) convertView.findViewById(R.id.my_posts_delete_button);
		deleteButton.setTag(position);
		deleteButton.setOnClickListener(new OnClickListener() {
			RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL)
					.setConverter(new GsonConverter(new Gson())).build();
			SnapShopService service = restAdapter.create(SnapShopService.class);
			SnapPreference pref = new SnapPreference(context);
			int position;
			int pid;

			@Override
			public void onClick(View v) {
				position = (int) v.getTag();
				pid = (int) getItemId(position);
				AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
				alt_bld.setMessage("Do you want to delete your post?").setCancelable(true)
						.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								service.deletePost(pid, pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0),
										new Callback<DefaultResponse>() {
											@Override
											public void success(DefaultResponse defResp, retrofit.client.Response arg1) {
												if (defResp.getStatus() == SnapConstants.SUCCESS) {
													Realm realm = Realm.getInstance(context);
													realm.beginTransaction();
													realmResults.remove(position);
													realm.commitTransaction();
													Toast.makeText(context, "The post was deleted", Toast.LENGTH_SHORT)
															.show();

												} else {
													Toast.makeText(context, "Deletion failed", Toast.LENGTH_SHORT)
															.show();
												}
											}

											@Override
											public void failure(RetrofitError arg0) {
												Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
											}
										});

							}
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				AlertDialog alert = alt_bld.create();
				alert.setTitle("Delete Post");
				alert.show();
			}
		});

		// Feed image
		if (item.getImageUrl() != null) {
			feedImageView.setImageUrl(item.getImageUrl(), imageLoader);
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
