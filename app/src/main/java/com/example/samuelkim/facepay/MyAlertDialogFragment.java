package com.example.samuelkim.facepay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by samuel.kim on 5/16/17.
 */

public class MyAlertDialogFragment extends DialogFragment {
    private Activity mActivity;

    public static MyAlertDialogFragment newInstance(int title) {
        MyAlertDialogFragment frag = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        if(title == R.string.norec) {
            return new AlertDialog.Builder(getActivity())
//                .setIcon(R.drawable.alert_dialog_icon)
                    .setTitle(title)
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((MainActivity)getActivity()).enroll();
                                }
                            }
                    )
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((MainActivity)getActivity()).tryagain();
                                }
                            }
                    )
                    .create();
        }else if(title == R.string.noface) {
            return new AlertDialog.Builder(getActivity())
//                .setIcon(R.drawable.alert_dialog_icon)
                    .setTitle(title)
                    .setPositiveButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((MainActivity)getActivity()).tryagain();
                                }
                            }
                    )
                    .setNegativeButton("Exit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mActivity.finish();
                                }
                            }
                    )
                    .create();
        }else {
            return null;
        }
    }
}


