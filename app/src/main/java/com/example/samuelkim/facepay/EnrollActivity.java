package com.example.samuelkim.facepay;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.clover.sdk.v1.customer.CustomerConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EnrollActivity extends AppCompatActivity {
    private String imageString;
    String mSubject;
    private Context mContext = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        imageString = savedInstanceState.getString("ImageString");

    }



    private void getSubject(View view) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                showDialog();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    requestUserEnroll();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
//        create customer in clover and return id
//        findViewById(R.id.)
    }

    void showDialog() {
        EnrollDialogFrag.newInstance(
                R.string.norec, imageString).show(getFragmentManager(), "dialog");
    }

    public void doPositiveClick() {
        // Do stuff here.
        try {
            Intent i = new Intent(this, EnrollActivity.class);
            i.putExtra("ImageString", imageString);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("FragmentAlertDialog", "Positive click!");
    }

    public void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }

    // Enroll
    public void requestUserEnroll() throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String BASE_URL = "https://api.kairos.com/enroll";

        JSONObject image = new JSONObject();

        image.put("image", imageString);
        image.put("subject_id", mSubject);
        image.put("gallery_name","TestGallery");

        //Request
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, image, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    //No recognized
                    if(response.has("Errors") && ((JSONObject) response.getJSONArray("Errors").get(0)).getString("ErrCode").equals("5002")){
                        Toast.makeText(EnrollActivity.this, ((JSONObject) response.getJSONArray("Errors").get(0)).getString("Message"), Toast.LENGTH_SHORT).show();
                        //Success
                    }else if(((JSONObject) response.getJSONArray("images").get(0)).getJSONObject("transaction").getString("status").equals("success")) {
                        mSubject = ((JSONObject) response.getJSONArray("images").get(0)).getJSONObject("transaction").getString("subject_id");
                        Toast.makeText(EnrollActivity.this, "Subject: " + mSubject + " Confidence: " + Double.parseDouble(((JSONObject) response.getJSONArray("images").get(0)).getJSONObject("transaction").getString("confidence")) * 100 + "%", Toast.LENGTH_LONG).show();
                        //No face found
                    }else {
                        String message = ((JSONObject) response.getJSONArray("images").get(0)).getJSONObject("transaction").getString("message");
                        Toast.makeText(EnrollActivity.this, message, Toast.LENGTH_SHORT).show();
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
                params.put("app_id", getString(R.string.appid));
                params.put("app_key", getString(R.string.key));
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
