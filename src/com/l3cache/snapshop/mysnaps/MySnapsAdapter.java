package com.l3cache.snapshop.mysnaps;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapConstants;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.retrofit.DefaultResponse;
import com.l3cache.snapshop.retrofit.SnapShopService;
import com.l3cache.snapshop.volley.ExtendedImageLoader;
import com.l3cache.snapshop.volley.FeedImageView;

public class MySnapsAdapter extends RealmBaseAdapter<MySnap> {
	private Button unsnapButton;
	ExtendedImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public MySnapsAdapter(Context context, RealmResults<MySnap> realmResults, boolean automaticUpdate) {
		super(context, realmResults, automaticUpdate);
	}

	@Override
	public int getCount() {
		return realmResults.size();
	}

	@Override
	public MySnap getItem(int position) {
		return realmResults.get(position);
	}

	@Override
	public long getItemId(int position) {
		return realmResults.get(position).getPid();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null)
			convertView = inflater.inflate(R.layout.my_snaps_volley_list_row, null);

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		MySnap item = realmResults.get(position);

		FeedImageView feedImageView = (FeedImageView) convertView.findViewById(R.id.my_snap_image_view);
		TextView writerTextView = (TextView) convertView.findViewById(R.id.my_snap_writer_text_view);
		TextView titleTextView = (TextView) convertView.findViewById(R.id.my_snap_title_text_view);
		titleTextView.setText(item.getTitle());
		writerTextView.setText(item.getWriter());

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

		unsnapButton = (Button) convertView.findViewById(R.id.my_snaps_unsnap_button);
		unsnapButton.setTag(position);
		unsnapButton.setOnClickListener(new OnClickListener() {
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
				service.unSnapPost(pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0), pid,
						new Callback<DefaultResponse>() {

							@Override
							public void success(DefaultResponse defResp, Response arg1) {
								if (defResp.getStatus() == SnapConstants.SUCCESS) {
									if (getCount() == 0)
										return;
									Realm realm = Realm.getInstance(context);
									realm.beginTransaction();
									realmResults.remove(position);
									realm.commitTransaction();
									Toast.makeText(context, "Unsnap - " + pid, Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(context, "Unsnap Failed", Toast.LENGTH_SHORT).show();
								}
							}

							@Override
							public void failure(RetrofitError arg0) {
								Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
							}
						});

			}
		});

		return convertView;
	}
}
