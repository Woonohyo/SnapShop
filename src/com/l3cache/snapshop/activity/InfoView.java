package com.l3cache.snapshop.activity;

import android.content.Intent;
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

import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapPreference;

public class InfoView extends Fragment {

	private TextView emailTextView;
	private Button signoutButton;
	private Button deactivateButton;
	private SnapPreference pref;
	private TextView versionTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_info, container, false);

		pref = new SnapPreference(getActivity());
		emailTextView = (TextView) view.findViewById(R.id.info_email_text_view);
		emailTextView.setText(pref.getValue(SnapPreference.PREF_CURRENT_USER_EMAIL, "No Email Address"));

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
					Intent intent = new Intent(getActivity(), LoginView.class);
					getActivity().finish();
					startActivity(intent);
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
