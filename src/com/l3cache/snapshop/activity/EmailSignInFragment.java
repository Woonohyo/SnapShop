package com.l3cache.snapshop.activity;

import io.realm.Realm;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapConstants;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.model.User;
import com.l3cache.snapshop.retrofit.SignInResponse;
import com.l3cache.snapshop.retrofit.SnapShopService;

public class EmailSignInFragment extends DialogFragment {
	EditText emailField;
	EditText passwordField;
	String mEmail;
	String mPassword;
	Context mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sign_in_email, container, false);
		Button signinButton = (Button) view.findViewById(R.id.button_sign_in);
		emailField = (EditText) view.findViewById(R.id.editText_email);
		passwordField = (EditText) view.findViewById(R.id.editText_password);
		emailField.setText("test@test.com");
		passwordField.setText("test");
		mContext = getActivity();
		signinButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					authorizeSignin(emailField.getText().toString(), passwordField.getText().toString());
				}

				return true;
			}

		});

		return view;
	}

	private void intentTabHostActivity() {
		Intent intent = new Intent(getActivity(), MainTabHostView.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	public void authorizeSignin(Context context, String email, String password) {
		mContext = context;
		authorizeSignin(email, password);
	}

	private void authorizeSignin(String email, String password) {
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL)
				.setConverter(new GsonConverter(new Gson())).build();
		// 콜백함수에서 사용할 수 있도록 email을 지역변수에 저장
		mEmail = email;
		mPassword = password;
		SnapShopService service = restAdapter.create(SnapShopService.class);
		service.login(email, password, new Callback<SignInResponse>() {

			@Override
			public void failure(RetrofitError error) {
				Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
				Log.i("Login", error.toString());
			}

			@Override
			public void success(SignInResponse loginResponse, Response response) {
				int status = loginResponse.getStatus();

				switch (status) {
				case SnapConstants.SUCCESS: {
					SnapPreference pref = new SnapPreference(getActivity());
					pref.put(SnapPreference.PREF_CURRENT_USER_ID, loginResponse.getId());
					pref.put(SnapPreference.PREF_CURRENT_USER_PASSWORD, mPassword);
					pref.put(SnapPreference.PREF_CURRENT_USER_EMAIL, mEmail);

					Toast.makeText(
							getActivity(),
							"Welcome back, " + pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0) + " - "
									+ pref.getValue(SnapPreference.PREF_CURRENT_USER_EMAIL, "No Email"),
							Toast.LENGTH_SHORT).show();
					;

					intentTabHostActivity();
					break;
				}
				case SnapConstants.EMAIL_ERROR: {
					Toast.makeText(getActivity(), "Email Error " + loginResponse.getId(), Toast.LENGTH_LONG).show();
					break;
				}
				case SnapConstants.PASSWORD_ERROR: {
					Toast.makeText(getActivity(), "Password Error " + loginResponse.getId(), Toast.LENGTH_LONG).show();
					break;
				}

				case SnapConstants.ERROR: {
					Toast.makeText(getActivity(), "Unrecognized Server Error!" + loginResponse.getId(),
							Toast.LENGTH_LONG).show();
					break;
				}

				default:
					break;
				}

			}
		});

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		getDialog().getWindow().setTitle("Sign In");
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return super.onCreateDialog(savedInstanceState);

	}
}
