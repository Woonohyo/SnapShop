package com.l3cache.snapshop.upload;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.l3cache.snapshop.R;

public class UploadSnapView extends Activity {
	private Bitmap bitmap;
	private String filePath;
	private String fileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_snap_view);

		ImageView uploadingImageView = (ImageView) findViewById(R.id.upload_snap_image_view);

		Intent data = (Intent) getIntent().getExtras().get("data");
		Uri uri = getRealPathUri(data.getData());
		filePath = uri.toString();
		fileName = uri.getLastPathSegment();

		bitmap = BitmapFactory.decodeFile(filePath);
		Bitmap resized = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
		uploadingImageView.setImageBitmap(resized);
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
