package edu.dartmouth.cs.actiontabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

//Dialog handles all the customized dialog boxes in our project.
//Differentiated by dialog id.
// 
// Ref: http://developer.android.com/reference/android/app/DialogFragment.html

public class Dialog extends DialogFragment {

	// Different dialog IDs
	public static final int DIALOG_ID_ERROR = -1;
	public static final int DIALOG_ID_PHOTO_PICKER = 1;

	// For photo picker selection:
	public static final int ID_PHOTO_PICKER_FROM_CAMERA = 0;
	public static final int ID_PHOTO_PICKER_FROM_GALLERY = 1;

	private static final String DIALOG_ID_KEY = "dialog_id";


	/** called when creating the fragment */
	public static Dialog newInstance(int dialog_id) {
		Dialog frag = new Dialog();
		Bundle args = new Bundle();
		args.putInt(DIALOG_ID_KEY, dialog_id);
		frag.setArguments(args);
		return frag;
	}

	/** called when creating the dialog */
	@Override
	public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
		int dialog_id = getArguments().getInt(DIALOG_ID_KEY);

		final Activity parent = getActivity();

		// Setup dialog appearance and onClick Listeners
		switch (dialog_id) {
		case DIALOG_ID_PHOTO_PICKER:
			// Build picture picker dialog for choosing from camera or gallery
			AlertDialog.Builder builder = new AlertDialog.Builder(parent);
			builder.setTitle(R.string.ui_profile_photo_picker_title);
			// Set up click listener, firing intents open camera
			DialogInterface.OnClickListener dlistener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					((Camera) parent)
							.onPhotoPickerItemSelected(item);
				}
			};
			// Set the item/s to display and create the dialog
			builder.setItems(R.array.ui_profile_photo_picker_items, dlistener);
			return builder.create();
		default:
			return null;
		}
	}
}
