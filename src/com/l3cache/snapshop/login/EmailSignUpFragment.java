package com.l3cache.snapshop.login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import android.annotation.SuppressLint;
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
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.retrofit.SnapShopService;

public class EmailSignUpFragment extends DialogFragment {
	private EditText emailField;
	private EditText passwordField;
	private Button signupButton;
	private EditText passwordAgainField;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sign_up_email, container, false);
		signupButton = (Button) view.findViewById(R.id.button_sign_up);
		emailField = (EditText) view.findViewById(R.id.editText_email);
		passwordField = (EditText) view.findViewById(R.id.editText_password);
		passwordAgainField = (EditText) view.findViewById(R.id.editText_password_again);

		signupButton.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (emailField.getText().toString().length() == 0) {
						Toast.makeText(getActivity(), "What's your Email Address?", Toast.LENGTH_LONG).show();
						return true;
					}

					if (isEmailValid(emailField.getText().toString()) == false) {
						Toast.makeText(getActivity(), "Invalid Email Address", Toast.LENGTH_LONG).show();
						return true;
					}

					if (passwordField.getText().toString().length() <= 6) {
						Toast.makeText(getActivity(), "Password is too short (minimum 7)", Toast.LENGTH_LONG).show();
						return true;
					}
					if (passwordField.getText().toString().equals(passwordAgainField.getText().toString()) == false) {
						Toast.makeText(getActivity(), "Password do not match", Toast.LENGTH_LONG).show();
						return true;
					}

					authorizeSignup();
					return true;
				}
				return false;
			}
		});

		return view;
	}

	protected void authorizeSignup() {
		Log.i("SignUp", "Authorizing");
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

	/**
	 * method is used for checking valid email id format.
	 * 
	 * @param email
	 * @return boolean true for valid false for invalid
	 */
	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		getDialog().getWindow().setTitle("Create Account");
	}

}
