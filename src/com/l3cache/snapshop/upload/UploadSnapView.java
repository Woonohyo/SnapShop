package com.l3cache.snapshop.upload;

import io.realm.Realm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.gson.Gson;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.data.User;
import com.l3cache.snapshop.retrofit.SnapShopService;
import com.l3cache.snapshop.volley.FeedImageView;

public class UploadSnapView extends Activity {
	private Bitmap bitmap;
	private String filePath;
	private String fileName;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();
	private EditText titleEditText;
	private EditText contentsEditText;
	private FeedImageView uploadingImageView;
	private EditText priceEditText;
	private EditText shopUrlEditText;
	private Button uploadingButton;
	private String mImageUrl;
	private TypedFile imageTypedFile;
	private String TAG = UploadSnapView.class.getSimpleName();
	private int handlerId;
	private RestAdapter restAdapter;
	private SnapShopService service;
	private User currentUser;
	private String fileProtocolPrefix = "file://";
	private String UPLOAD_PREFIX = "Upload";
	private String JPEG_POSTFIX = ".jpg";
	private File fileFromUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_snap_view);

		Realm realm = Realm.getInstance(this);
		currentUser = realm.where(User.class).findFirst();

		titleEditText = (EditText) findViewById(R.id.upload_snap_editText_title);
		contentsEditText = (EditText) findViewById(R.id.upload_snap_editText_contents);
		uploadingImageView = (FeedImageView) findViewById(R.id.upload_snap_image_view);
		priceEditText = (EditText) findViewById(R.id.upload_snap_editText_price);
		shopUrlEditText = (EditText) findViewById(R.id.upload_snap_editText_link);
		uploadingButton = (Button) findViewById(R.id.upload_snap_button_upload);

		handlerId = getIntent().getExtras().getInt("handler");

		switch (handlerId) {
		case SnapConstants.CAMERA_BUTTON: {

			break;
		}

		case SnapConstants.GALLERY_BUTTON: {
			Uri imageUri = (Uri) getIntent().getExtras().get("data");
			 compressToJpeg(imageUri);
//			fileFromUri = new File(imageUri.getPath());
//			imageTypedFile = new TypedFile("image/jpeg", fileFromUri);
			Log.i(TAG, imageTypedFile.toString());
			uploadingImageView.setImageUrl(imageUri.toString(), imageLoader);
			
			break;

		}
		case SnapConstants.INTERNET_BUTTON: {
			Log.i("Upload", "INTERNET!");
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

					switch (handlerId) {
					case SnapConstants.GALLERY_BUTTON: {
						upload(imageTypedFile);
						break;

					}

					case SnapConstants.INTERNET_BUTTON: {
						upload(mImageUrl);
						break;
					}

					default:
						break;
					}

				}
				return false;
			}

		});

	}

	private void compressToJpeg(Uri imageUri) {
		String imageUriString = imageUri.toString();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		Bitmap bitmap = BitmapFactory.decodeFile(imageUriString.substring(imageUriString.indexOf(fileProtocolPrefix)
				+ fileProtocolPrefix.length()), options);
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		File imageFileFolder = new File(getCacheDir(), UPLOAD_PREFIX);
		if (!imageFileFolder.exists()) {
			imageFileFolder.mkdir();
		}
		File imageFileName = new File(imageFileFolder, UPLOAD_PREFIX + System.currentTimeMillis() + JPEG_POSTFIX);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(imageFileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();

		} catch (IOException e) {
			Log.e(TAG, "Failed to convert image to JPEG", e);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}

			} catch (IOException e) {
				Log.e(TAG, "Failed to close output stream", e);
			}
		}

		imageTypedFile = new TypedFile("image/jpeg", imageFileName);
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
				new TypedString(priceEditText.getText().toString()), currentUser.getUid(),
				new Callback<UploadResponse>() {

					@Override
					public void failure(RetrofitError error) {
						if (error.getResponse() != null) {
							Log.i(TAG, error.getResponse().getStatus() + "");
						}
					}

					@Override
					public void success(UploadResponse uploadResponse, Response response) {
						Log.i("Upload", uploadResponse.getStatus() + "");
						if (uploadResponse.getStatus() == SnapConstants.SUCCESS) {
							Toast.makeText(getApplicationContext(), "Your Snap Successfully Added!", Toast.LENGTH_LONG)
									.show();

							finish();
						}
					}

				});

	}

	private void upload(String imageUrl) {
		service.uploadSnap(titleEditText.getText().toString(), shopUrlEditText.getText().toString(), contentsEditText
				.getText().toString(), imageUrl, priceEditText.getText().toString(), currentUser.getUid(),
				new Callback<UploadResponse>() {

					@Override
					public void failure(RetrofitError error) {
						Log.i("Upload", error.toString());
						Log.i("Upload", error.getLocalizedMessage());
					}

					@Override
					public void success(UploadResponse uploadResponse, Response response) {
						Log.i("Upload", uploadResponse.getStatus() + "");
						if (uploadResponse.getStatus() == SnapConstants.SUCCESS) {
							Toast.makeText(getApplicationContext(), "Your Snap Successfully Added!", Toast.LENGTH_LONG)
									.show();

							finish();
						}
					}

				});
	}

}
