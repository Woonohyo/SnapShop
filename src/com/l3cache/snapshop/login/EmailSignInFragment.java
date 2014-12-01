package com.l3cache.snapshop.login;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import android.app.Dialog;
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
import com.l3cache.snapshop.MainTabHostView;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.retrofit.SnapShopService;

public class EmailSignInFragment extends DialogFragment {
	EditText emailField;
	EditText passwordField;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sign_in_email, container, false);
		Button signinButton = (Button) view.findViewById(R.id.button_sign_in);
		emailField = (EditText) view.findViewById(R.id.editText_email);
		passwordField = (EditText) view.findViewById(R.id.editText_password);
		signinButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					fakeSignin();
					authorizeSignin();

				}

				return true;
			}

		});

		return view;
	}

	protected void fakeSignin() {
		Intent intent = new Intent(getActivity(), MainTabHostView.class);
		startActivity(intent);

	}

	private void authorizeSignin() {
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://125.209.199.221:8080")
				.setConverter(new GsonConverter(new Gson())).build();

		SnapShopService service = restAdapter.create(SnapShopService.class);
		service.login(emailField.getText().toString(), passwordField.getText().toString(), new Callback<Response>() {

			@Override
			public void failure(RetrofitError error) {
				Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
				Log.i("Login", error.toString());

			}

			@Override
			public void success(Response response0, Response response1) {
				Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
				Log.i("Login", response0.toString());
				Log.i("Login", response1.toString());
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
