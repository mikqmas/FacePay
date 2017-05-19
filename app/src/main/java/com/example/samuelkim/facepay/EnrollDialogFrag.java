package com.example.samuelkim.facepay;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.clover.sdk.v1.customer.Customer;
import com.clover.sdk.v1.customer.CustomerConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samuel.kim on 5/17/17.
 */

public class EnrollDialogFrag extends DialogFragment {
    private int TAKE_PHOTO = 1;
    private String mFname;
    private String mLname;
    private String mEmail;
    private String mPhone;
    private Context mContext;
    private static String mImageString;

    public static EnrollDialogFrag newInstance(int title, String imageString) {
        EnrollDialogFrag frag = new EnrollDialogFrag();
        mImageString = imageString;
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity().getBaseContext();
        int title = getArguments().getInt("title");
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.enroll_layout, null);

        final EditText fname = (EditText) dialogLayout.findViewById(R.id.fName);
        final EditText lname = (EditText) dialogLayout.findViewById(R.id.lName);
        final EditText email = (EditText) dialogLayout.findViewById(R.id.email);
        final EditText phone = (EditText) dialogLayout.findViewById(R.id.phone);


        return new AlertDialog.Builder(getActivity())
//                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(title)
                .setView(dialogLayout)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    mFname = fname.getText().toString();
                                    mLname = lname.getText().toString();
                                    mEmail = email.getText().toString();
                                    mPhone = phone.getText().toString();
                                    QueryUtil.checkAndCreateCustomer(mContext, mFname, mLname, mPhone, mEmail, mImageString);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do something when not enrolling
                                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_PHOTO);
                            }
                        }
                )
                .create();

    }
}
