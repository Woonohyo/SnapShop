package com.l3cache.snapshop.upload;

import java.io.File;
import java.io.IOException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.app.AppController.TrackerName;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.photocrop.CropUtil;
import com.l3cache.snapshop.retrofit.SnapShopService;
import com.l3cache.snapshop.volley.ExtendedImageLoader;
import com.l3cache.snapshop.volley.FeedImageView;

public class UploadPostView extends Activity {
	private ExtendedImageLoader imageLoader = AppController.getInstance().getImageLoader();
	private EditText titleEditText;
	private EditText contentsEditText;
	private FeedImageView uploadingImageView;
	private EditText priceEditText;
	private EditText shopUrlEditText;
	private Button uploadingButton;
	private String mImageUrl;
	private TypedFile imageTypedFile;
	private String TAG = UploadPostView.class.getSimpleName();
	private int handlerId;
	private RestAdapter restAdapter;
	private SnapShopService service;
	private SnapPreference pref;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_upload_snap_view);

		// Get tracker.
		Tracker t = ((AppController) getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName(UploadPostView.class.getSimpleName());
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());

		pref = new SnapPreference(getApplicationContext());

		titleEditText = (EditText) findViewById(R.id.upload_snap_editText_title);
		contentsEditText = (EditText) findViewById(R.id.upload_snap_editText_contents);
		uploadingImageView = (FeedImageView) findViewById(R.id.upload_snap_image_view);
		priceEditText = (EditText) findViewById(R.id.upload_snap_editText_price);
		shopUrlEditText = (EditText) findViewById(R.id.upload_snap_editText_link);
		uploadingButton = (Button) findViewById(R.id.upload_snap_button_upload);

		handlerId = getIntent().getExtras().getInt("handler");
		Log.i(TAG, "Image handler - " + handlerId);

		switch (handlerId) {
		case SnapConstants.CAMERA_BUTTON: {
			break;
		}

		case SnapConstants.GALLERY_BUTTON: {
			Uri imageUri = (Uri) getIntent().getExtras().get("data");
			File imageFile = CropUtil.getFromMediaUri(getContentResolver(), imageUri);
			ExifInterface exifResult;
			try {
				exifResult = new ExifInterface(imageFile.getAbsolutePath());
				int orientation = exifResult.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);
				pref.put(SnapPreference.PREF_EXIF_ORIENTATION, orientation);
				Log.i(TAG, "Orientation - " + orientation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			uploadingImageView.setImageUrl(imageUri.toString(), imageLoader);
			imageTypedFile = new TypedFile("image/jpeg", new File(imageUri.getPath()));
			Log.i(TAG, imageTypedFile.toString());
			break;

		}
		case SnapConstants.INTERNET_BUTTON: {
			Log.i(TAG, "INTERNET!");
			Bundle extras = getIntent().getExtras();
			titleEditText.setText(extras.getString("title"));
			mImageUrl = extras.getString("image");
			uploadingImageView.setImageUrl(mImageUrl, imageLoader);
			priceEditText.setText(extras.getInt("price") + "");
			shopUrlEditText.setText(extras.getString("shopUrl"));
			break;
		}
		}

		uploadingButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					initRestfit();
					
					if(priceEditText.getText().length() == 0) {
						Toast.makeText(getApplicationContext(), "Please enter price of your item", Toast.LENGTH_SHORT).show();
					}

					switch (handlerId) {
					case SnapConstants.CAMERA_BUTTON:
					case SnapConstants.GALLERY_BUTTON: {
						upload(imageTypedFile);
						break;

					}

					case SnapConstants.INTERNET_BUTTON: {
						upload(mImageUrl);
						break;
					}
					}
				}
				return false;
			}

		});

	}

	public int exifOrientationToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	public Bitmap rotate(Bitmap bitmap, int degrees) {
		if (degrees != 0 && bitmap != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

			try {
				Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
				if (bitmap != converted) {
					bitmap.recycle();
					bitmap = converted;
				}
			} catch (OutOfMemoryError ex) {
				// 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
			}
		}
		return bitmap;
	}

	public String getPathFromUri(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToNext();
		String path = cursor.getString(cursor.getColumnIndex("_data"));
		cursor.close();

		return path;
	}

	private void initRestfit() {
		if (restAdapter == null) {
			restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL)
					.setConverter(new GsonConverter(new Gson())).build();
		}
		if (service == null) {
			service = restAdapter.create(SnapShopService.class);
		}

	}

	private void upload(TypedFile imageFile) {
		service.uploadSnap(new TypedString(titleEditText.getText().toString()), new TypedString(shopUrlEditText
				.getText().toString()), new TypedString(contentsEditText.getText().toString()), imageTypedFile,
				new TypedString(priceEditText.getText().toString()), pref.getValue(SnapPreference.PREF_CURRENT_USER_ID,
						0), new Callback<UploadResponse>() {

					@Override
					public void failure(RetrofitError error) {
						Log.i(TAG, error.getLocalizedMessage());
						Toast.makeText(getApplicationContext(), "Error(image) - " + error.getLocalizedMessage(),
								Toast.LENGTH_LONG).show();
						;
					}

					@Override
					public void success(UploadResponse uploadResponse, Response response) {
						if (uploadResponse.getStatus() == SnapConstants.SUCCESS) {
							Toast.makeText(getApplicationContext(), "Your Snap Successfully Added!", Toast.LENGTH_LONG)
									.show();
						} else if (uploadResponse.getStatus() == SnapConstants.ERROR) {
							Toast.makeText(getApplicationContext(), "Error(image) - " + uploadResponse.getStatus(),
									Toast.LENGTH_LONG).show();
						}
					}

				});

	}

	private void upload(String imageUrl) {
		service.uploadSnap(titleEditText.getText().toString(), shopUrlEditText.getText().toString(), contentsEditText
				.getText().toString(), imageUrl, priceEditText.getText().toString(), pref.getValue(
				SnapPreference.PREF_CURRENT_USER_ID, 0), new Callback<UploadResponse>() {

			@Override
			public void failure(RetrofitError error) {
				Log.i(TAG, error.getLocalizedMessage());
				Toast.makeText(getApplicationContext(), "Error(url) - " + error.getLocalizedMessage(),
						Toast.LENGTH_LONG).show();
				;
			}

			@Override
			public void success(UploadResponse uploadResponse, Response response) {
				Log.i(TAG, uploadResponse.getStatus() + "");
				if (uploadResponse.getStatus() == SnapConstants.SUCCESS) {
					Toast.makeText(getApplicationContext(), "Your Snap Successfully Added!", Toast.LENGTH_LONG).show();
					((Activity) mContext).setResult(RESULT_OK);
					finish();
				}
			}

		});
	}
}
