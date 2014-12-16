package com.l3cache.snapshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapPreference;

public class InfoView extends Fragment {

	private TextView emailTextView;
	private Button signoutButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_info, container, false);

		SnapPreference pref = new SnapPreference(getActivity());
		emailTextView = (TextView) view.findViewById(R.id.info_email_text_view);
		emailTextView.setText(pref.getValue(SnapPreference.PREF_CURRENT_USER_EMAIL, "No Email Address"));

		signoutButton = (Button) view.findViewById(R.id.info_signout_button);
		signoutButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {

					SnapPreference pref = new SnapPreference(getActivity());
					pref.removeCurrentUser();

					Intent intent = new Intent(getActivity(), LoginView.class);
					getActivity().finish();
					startActivity(intent);

				}

				return false;
			}
		});

		return view;
	}
}
