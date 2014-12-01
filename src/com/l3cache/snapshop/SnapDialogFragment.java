package com.l3cache.snapshop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.search.SearchResultsView;

public class SnapDialogFragment extends DialogFragment {

	private Uri fileUri;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.dialog_snap).setItems(R.array.snap_array, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0: {
					Log.i("Snap", which + ": Camera");
					// create Intent to take a picture and return control to the
					// calling application
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					fileUri = getOutputMediaFileUri(SnapConstants.MEDIA_TYPE_IMAGE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

					// start the image capture Intent
					getActivity().startActivityForResult(intent, SnapConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
					break;
				}

				case 1: {
					// Intent intent = new Intent(Intent.ACTION_PICK,
					// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					// startActivityForResult(intent,
					// SnapConstants.RESULT_LOAD_IMAGE);
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType(Images.Media.CONTENT_TYPE);
					intent.setData(Images.Media.EXTERNAL_CONTENT_URI);
					getActivity().startActivityForResult(intent, SnapConstants.RESULT_LOAD_IMAGE);
					break;
				}
				case 2: {
					Intent intent = new Intent(getActivity(), SearchResultsView.class);
					getActivity().startActivity(intent);
					break;
				}
				}
			}

		});
		return builder.create();
	}
	
	private Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == SnapConstants.MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}
}
