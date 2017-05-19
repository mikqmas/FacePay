package com.example.samuelkim.facepay;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import com.clover.sdk.v1.merchant.MerchantConnector;
import com.clover.sdk.v1.tender.TenderConnector;
import com.clover.sdk.v3.employees.EmployeeConnector;
import com.clover.sdk.v3.inventory.InventoryConnector;
import com.clover.sdk.v3.order.OrderConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samuel.kim on 5/18/17.
 */

public class QueryUtil extends FragmentActivity {
    private static Context mContext;

    public static void getContext(Context context) {
        mContext = context;
    }

//    private void connect() {
//        if (mAccount != null) {
//            mInventoryConnector = new InventoryConnector(MainActivity.mAccount, null);
//            mOrderConnector = new OrderConnector(this, mAccount, null);
//            mMerchantConnector = new MerchantConnector(this, mAccount, null);
//            tenderConnector = new TenderConnector(this, mAccount, null);
//            mEmployeeConnector = new EmployeeConnector(this, mAccount, null);
//            mCustomerConnector = new CustomerConnector(this, mAccount, null);
//            mCustomerConnector.connect();
//            mEmployeeConnector.connect();
//            mInventoryConnector.connect();
//            mOrderConnector.connect();
//            mMerchantConnector.connect();
//            tenderConnector.connect();
//        }
//    }
//
//    private void disconnect() {
//        if (mAccount != null) {
//            mInventoryConnector.disconnect();
//            mMerchantConnector.disconnect();
//            mOrderConnector.disconnect();
//            tenderConnector.disconnect();
//            mEmployeeConnector.disconnect();
//            mCustomerConnector.disconnect();
//            mCustomerConnector = null;
//            mEmployeeConnector = null;
//            mInventoryConnector = null;
//            mOrderConnector = null;
//            mMerchantConnector = null;
//            tenderConnector = null;
//        }
//    }

    public static void checkAndCreateCustomer(final Context mContext, final String mFname, final String mLname, final String mPhone, final String mEmail, final String mImageString) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Customer mCustomer = null;
                try{
                    CustomerConnector mCustomerConnector = new CustomerConnector(mContext, MainActivity.mAccount, null);
                    mCustomerConnector.connect();
                    List<Customer> mCustomerList = mCustomerConnector.getCustomers(mLname);
                    if (mCustomerList.size() > 0) {
                        Log.i("test", "same name customer");
                        //List existing customers
                    }else {
                        mCustomer = mCustomerConnector.createCustomer(mFname, mLname, true);
                        mCustomerConnector.addPhoneNumber(mCustomer.getId(), mPhone);
                        mCustomerConnector.addEmailAddress(mCustomer.getId(), mEmail);
                    }
                    requestUserEnroll(mCustomer.getId(), mImageString);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    // Detection
    public static void requestUserDetection(final String imageString, final FragmentManager fm) throws  JSONException {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Recognizing face...");
        final String BASE_URL = "https://api.kairos.com/recognize";

        JSONObject image = new JSONObject();

        image.put("image", imageString);
        image.put("gallery_name","TestGallery");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, image, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try{
                    //No face in picture
                    if(response.has("Errors") && ((JSONObject) response.getJSONArray("Errors").get(0)).getString("ErrCode").equals("5002")){
                        MyAlertDialogFragment.newInstance(
                                R.string.noface).show(fm, "dialog");
                        Toast.makeText(mContext, ((JSONObject) response.getJSONArray("Errors").get(0)).getString("Message"), Toast.LENGTH_SHORT).show();
                        //Successful
                    }else if(((JSONObject) response.getJSONArray("images").get(0)).getJSONObject("transaction").getString("status").equals("success")) {
                        String mSubject = ((JSONObject) response.getJSONArray("images").get(0)).getJSONObject("transaction").getString("subject_id");
                        Toast.makeText(mContext, "Subject: " + mSubject + " Confidence: " + Double.parseDouble(((JSONObject) response.getJSONArray("images").get(0)).getJSONObject("transaction").getString("confidence")) * 100 + "%", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(mContext, WelcomeActivity.class);
                        i.putExtra("customerID", mSubject);
                        mContext.startActivity(i);
                        //Didn't recognize
                    }else {
                        MyAlertDialogFragment.newInstance(
                                R.string.norec).show(fm, "dialog");
                        String message = ((JSONObject) response.getJSONArray("images").get(0)).getJSONObject("transaction").getString("message");
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("Error", error.getMessage());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("app_id", mContext.getString(R.string.appid));
                params.put("app_key", mContext.getString(R.string.key));
                params.put("Content-Type", "application/json");

                return params;
            }
        };


        int MY_SOCKET_TIMEOUT_MS = 30000;
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsObjRequest);

        //show progress
        progressDialog.show();

    }


    // Enroll
    public static void requestUserEnroll(String customerId, String mImageString) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        final String BASE_URL = "https://api.kairos.com/enroll";

        JSONObject image = new JSONObject();

        image.put("image", mImageString);
        image.put("subject_id", customerId);
        image.put("gallery_name","TestGallery");

        //Request
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, image, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(((JSONObject) response.getJSONArray("images").get(0)).getJSONObject("transaction").getString("status").equals("success")) {
                        String mSubject = ((JSONObject) response.getJSONArray("images").get(0)).getJSONObject("transaction").getString("subject_id");
                        Toast.makeText(mContext, ((JSONObject) response.getJSONArray("images").get(0)).getJSONObject("transaction").getString("status").toString(), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mContext, WelcomeActivity.class);
                        i.putExtra("customerID", mSubject);
                        mContext.startActivity(i);
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("Error", error.getMessage());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("app_id", mContext.getString(R.string.appid));
                params.put("app_key", mContext.getString(R.string.key));
                params.put("Content-Type", "application/json");

                return params;
            }
        };


        int MY_SOCKET_TIMEOUT_MS = 30000;
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsObjRequest);

    }

}
