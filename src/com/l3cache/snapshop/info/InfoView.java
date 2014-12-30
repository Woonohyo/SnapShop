package com.l3cache.snapshop.info;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapConstants;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.retrofit.SnapShopService;
import com.l3cache.snapshop.retrofit.TotalPriceResponse;

public class InfoView extends Fragment {

	private TextView emailTextView;
	private TextView totalPostPriceTextView;
	private TextView totalSnapPriceTextView;
	private Button signoutButton;
	private Button deactivateButton;
	private SnapPreference pref;
	private TextView versionTextView;
	private RestAdapter restAdapter;
	private SnapShopService service;
	private NumberFormat format;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_info, container, false);
		emailTextView = (TextView) view.findViewById(R.id.info_email_text_view);
		totalPostPriceTextView = (TextView) view.findViewById(R.id.info_total_post_price_text_view);
		totalSnapPriceTextView = (TextView) view.findViewById(R.id.info_total_snap_price_text_view);

		pref = new SnapPreference(getActivity());

		int userId = pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0);
		emailTextView.setText(pref.getValue(SnapPreference.PREF_CURRENT_USER_EMAIL, "No Email Address"));

		format = NumberFormat.getCurrencyInstance(new Locale("ko_KR"));
		format.setParseIntegerOnly(true);

		restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL)
				.setConverter(new GsonConverter(new Gson())).build();
		service = restAdapter.create(SnapShopService.class);

		service.totalPostPrice(userId, new Callback<TotalPriceResponse>() {
			@Override
			public void success(TotalPriceResponse tpResp, Response resp) {
				if (tpResp.getResult() == SnapConstants.SUCCESS) {
					totalPostPriceTextView.setText(format.format(tpResp.getTotal()));
				} else {
					totalPostPriceTextView.setText("0");
				}
			}

			@Override
			public void failure(RetrofitError error) {
				totalPostPriceTextView.setText("0");
				Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
			}
		});

		service.totalSnapPrice(userId, new Callback<TotalPriceResponse>() {
			@Override
			public void success(TotalPriceResponse tpResp, Response resp) {
				if (tpResp.getResult() == SnapConstants.SUCCESS) {
					totalSnapPriceTextView.setText(format.format(tpResp.getTotal()));
				} else {
					totalSnapPriceTextView.setText("0");
				}
			}

			@Override
			public void failure(RetrofitError error) {
				totalSnapPriceTextView.setText("0");
				Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
			}
		});

		versionTextView = (TextView) view.findViewById(R.id.info_version_text_view);
		try {
			versionTextView.setText("Version: "
					+ getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName
					+ "  Code: "
					+ getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		signoutButton = (Button) view.findViewById(R.id.info_signout_button);
		signoutButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					pref.removeCurrentUser();
					getActivity().finish();
					return true;
				}

				return false;
			}
		});

		deactivateButton = (Button) view.findViewById(R.id.info_deactivate_button);
		deactivateButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Toast.makeText(getActivity(), "가입할 땐 자유지만 탈퇴할 땐 아니란다", Toast.LENGTH_SHORT).show();
					return true;
				}
				return false;
			}
		});

		return view;
	}
}
