package com.l3cache.snapshop.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedFile;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.newsfeed.FeedImageView;
import com.l3cache.snapshop.retrofit.SnapShopService;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_snap_view);

		titleEditText = (EditText) findViewById(R.id.upload_snap_editText_title);
		contentsEditText = (EditText) findViewById(R.id.upload_snap_editText_contents);
		uploadingImageView = (FeedImageView) findViewById(R.id.upload_snap_image_view);
		priceEditText = (EditText) findViewById(R.id.upload_snap_editText_price);
		shopUrlEditText = (EditText) findViewById(R.id.upload_snap_editText_link);
		uploadingButton = (Button) findViewById(R.id.upload_snap_button_upload);

		int handlerId = getIntent().getExtras().getInt("handler");

		switch (handlerId) {
		case SnapConstants.GALLERY_BUTTON: {
			Intent data = (Intent) getIntent().getExtras().get("data");
			Uri uri = getRealPathUri(data.getData());
			filePath = uri.toString();
			fileName = uri.getLastPathSegment();

			bitmap = BitmapFactory.decodeFile(filePath);
			Bitmap resized = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
			uploadingImageView.setImageBitmap(resized);

			break;

		}
		case SnapConstants.INTERNET_BUTTON: {
			Log.i("Upload", "INTERNET!");
			Bundle extras = getIntent().getExtras();
			uploadingImageView.setImageUrl(extras.getString("image"), imageLoader);
			priceEditText.setText(extras.getInt("price") + "");
			shopUrlEditText.setText(extras.getString("shopUrl"));

			break;
		}
		}

		uploadingButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL())
							.setConverter(new GsonConverter(new Gson())).build();
					Drawable d = uploadingImageView.getDrawable();
					Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
					File bitmapFile = persistImage(bitmap, "uploading");
					TypedFile imageFile = new TypedFile("image/jpeg", bitmapFile);

					SnapShopService service = restAdapter.create(SnapShopService.class);
					service.uploadSnap(titleEditText.getText().toString(), shopUrlEditText.getText().toString(),
							contentsEditText.getText().toString(), imageFile, priceEditText.getText().toString(), 1214,
							new Callback<UploadResponse>() {

								@Override
								public void failure(RetrofitError error) {
									Log.i("Upload", error.toString());
									Log.i("Upload", error.getLocalizedMessage());
								}

								@Override
								public void success(UploadResponse uploadResponse, Response response) {
									if (uploadResponse.getStatus() == SnapConstants.SUCCESS) {
										Toast.makeText(getApplicationContext(), "Your Snap Successfully Added!",
												Toast.LENGTH_LONG).show();
										;

									}

								}

							});
					return true;
				}
				return false;
			}
		});

	}

	private File persistImage(Bitmap bitmap, String name) {
		File filesDir = getApplicationContext().getFilesDir();
		File imageFile = new File(filesDir, name + ".jpeg");

		OutputStream os;
		try {
			os = new FileOutputStream(imageFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
			os.flush();
			os.close();
		} catch (Exception e) {
			Log.e("Upload", "Error writing bitmap", e);
		}

		return imageFile;
	}

	private Uri getRealPathUri(Uri uri) {
		Uri filePathUri = uri;
		if (uri.getScheme().toString().compareTo("content") == 0) {
			Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
			if (cursor.moveToFirst()) {
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				filePathUri = Uri.parse(cursor.getString(column_index));
			}
		}
		return filePathUri;
	}
}
