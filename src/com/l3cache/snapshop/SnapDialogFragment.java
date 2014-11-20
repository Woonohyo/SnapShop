package com.l3cache.snapshop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class SnapDialogFragment extends DialogFragment {
	 
	 @Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
	     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	     builder.setTitle(R.string.dialog_snap)
	            .setItems(R.array.snap_array, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                	switch (which) {
	    				case 0:
	    					Log.i("Snap", which + ": Camera");
	    					break;
	    				case 1:
	    					Log.i("Snap", which + ": Album");
	    					break;
	    				case 2:
	    					Log.i("Snap", which + ": Web");
	    					break;
	    				}
	            }
	     });
	     return builder.create();
	 }
}
