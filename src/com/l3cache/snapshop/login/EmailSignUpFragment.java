package com.l3cache.snapshop.login;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.l3cache.snapshop.MainTabHostView;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.retrofit.SnapShopService;

public class EmailSignUpFragment extends DialogFragment {
	EditText emailField;
	EditText passwordField;
	Button signupButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sign_up_email, container, false);
		signupButton = (Button) view.findViewById(R.id.button_sign_up);
		emailField = (EditText) view.findViewById(R.id.editText_email);
		passwordField = (EditText) view.findViewById(R.id.editText_password);

		signupButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					authorizeSignup();

				}
				return true;
			}
		});

		return view;
	}


	protected void authorizeSignup() {
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL())
				.setConverter(new GsonConverter(new Gson())).build();

		SnapShopService service = restAdapter.create(SnapShopService.class);
		service.signUp(emailField.getText().toString(), passwordField.getText().toString(),
				new Callback<SignUpResponse>() {

					@Override
					public void failure(RetrofitError error) {
						Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
					}

					@Override
					public void success(SignUpResponse signupResponse, Response response) {
						int status = signupResponse.getStatus();

						switch (status) {
						case SnapConstants.SUCCESS: {
							Toast.makeText(getActivity(), "Welcome!", Toast.LENGTH_LONG).show();
							break;

						}

						case SnapConstants.EMAIL_DUPLICATION: {
							Toast.makeText(getActivity(), "Email Already Exists", Toast.LENGTH_LONG).show();
							break;
						}

						case SnapConstants.ERROR: {
							Toast.makeText(getActivity(), "Unrecognized Server Error!", Toast.LENGTH_LONG).show();
							break;
						}

						default:
							break;
						}

					}
				});
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		getDialog().getWindow().setTitle("Create Account");
	}

}
