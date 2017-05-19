package com.example.samuelkim.facepay;

import android.accounts.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.customer.CustomerConnector;
import com.clover.sdk.v1.merchant.Merchant;
import com.clover.sdk.v1.merchant.MerchantConnector;
import com.clover.sdk.v1.tender.TenderConnector;
import com.clover.sdk.v3.employees.EmployeeConnector;
import com.clover.sdk.v3.inventory.InventoryConnector;
import com.clover.sdk.v3.order.OrderConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private int TAKE_PHOTO = 1;
    private Bitmap imageBitmap;
    protected ImageView mImageView;
    private String TAG = "TAG";
    private String mSubject;
    String imageString;


    //Clover
    public static Account mAccount;
    Context mContext = this;
    private InventoryConnector mInventoryConnector;
    private MerchantConnector mMerchantConnector;
    private CustomerConnector mCustomerConnector;
    private OrderConnector mOrderConnector;
    private EmployeeConnector mEmployeeConnector;
    private TenderConnector tenderConnector;
    private Merchant mMerchant;

    @Override
    protected void onResume() {
        super.onResume();
        QueryUtil.getContext(this);
        if (mAccount == null) {
            mAccount = CloverAccount.getAccount(this);

            if (mAccount == null) {
                return;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mImageView = (ImageView) findViewById(R.id.mImageView);

        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO) {
            if(resultCode == RESULT_OK && data != null) {
                try{
                    Bundle extras = data.getExtras();

                    imageBitmap = (Bitmap) extras.get("data");
                    //        mImageView.setImageBitmap(imageBitmap);

                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
                    imageString = Base64.encodeToString(byteArray.toByteArray(), Base64.NO_WRAP);
                    Log.d(TAG, imageString);

                    QueryUtil.requestUserDetection(imageString, getFragmentManager());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this, R.string.photo_fail, Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    public void enroll() {
        // Enroll
        try {
            EnrollDialogFrag.newInstance(R.string.norec, imageString).show(getFragmentManager(), "dialog");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("FragmentAlertDialog", "Positive click!");
    }

    public void tryagain() {
        // Try Again
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_PHOTO);
        Log.i("FragmentAlertDialog", "Negative click!");
    }

}
