package com.example.samuelkim.facepay;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.tender.Tender;
import com.clover.sdk.v1.tender.TenderConnector;

/**
 * Created by samuel.kim on 5/18/17.
 */

public class InitTenderActivity extends Activity {
    Tender result;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createTenderType(this);
    }

    private void createTenderType(final Context context) {
        new AsyncTask<Void, Void, Exception>() {

            private TenderConnector tenderConnector;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tenderConnector = new TenderConnector(context, CloverAccount.getAccount(context), null);
                tenderConnector.connect();
            }

            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    result = tenderConnector.checkAndCreateTender(getString(R.string.tender_name), getPackageName(), true, false);
                } catch (Exception exception) {
                    Log.e("TAG", exception.getMessage(), exception.getCause());
                    return exception;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception exception) {
                tenderConnector.disconnect();
                tenderConnector = null;
                if(result != null){
                    Toast.makeText(context, "FacePay Tender Exists. You can use FacePay to pay in Register", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "FacePay Tender Failed Creation", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
        this.finish();
    }
}
