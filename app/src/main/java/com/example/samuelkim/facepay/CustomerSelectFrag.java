package com.example.samuelkim.facepay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;

import android.content.DialogInterface;
import com.clover.sdk.v1.customer.Customer;

import org.json.JSONException;

import java.util.List;

/**
 * Created by samuel.kim on 5/18/17.
 */

public class CustomerSelectFrag extends DialogFragment{
    static private List<Customer> mCustomers;
    static private String mImageString;
    private static String mFname;
    private static String mLname;
    private static String mPhone;
    private static String mEmail;

    public static CustomerSelectFrag newInstance(List<Customer> customers, String imageString, String Fname, String Lname, String Phone, String Email) {
        CustomerSelectFrag mCSF = new CustomerSelectFrag();
        mCustomers = customers;
        mImageString = imageString;
        mFname = Fname;
        mLname = Lname;
        mPhone = Phone;
        mEmail = Email;
        return mCSF;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CharSequence[] cArr = new CharSequence[mCustomers.size() + 1];
        for(int i = 0; i < mCustomers.size(); i++) {
            Customer c = mCustomers.get(i);
            cArr[i] = c.getFirstName() + " " + c.getLastName() + ": " + c.getPhoneNumbers();
        }
        cArr[mCustomers.size()] = "Add New Customer";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Existing Customers")
                .setItems(cArr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        try {
                            if(which < mCustomers.size()){
                                QueryUtil.requestUserEnroll(mCustomers.get(which).getId(), mImageString);
                            }else {
                                new AsyncTask<Void, Void, Customer>() {
                                    @Override
                                    protected Customer doInBackground(Void... params) {
                                        Customer customer = QueryUtil.createCustomer(mFname, mLname, mPhone, mEmail);
                                        return customer;
                                    }

                                    @Override
                                    protected void onPostExecute(Customer customer) {
                                        try {
                                            QueryUtil.requestUserEnroll(customer.getId(), mImageString);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.execute();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        return builder.create();
    }
}
