package com.jason.ocbcapp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class SetupActivity extends Activity {

    /**
     * Keep track of the setup task to ensure we can cancel it if requested.
     */
    private SetupTask task = null;

    // Values for name, id type, id and mobile
    private String name;
    private String id;
    private String idType;
    private String mobile;

    // UI references.
    private EditText custMobile;
    private EditText custName;
    private EditText custId;
    private Spinner idTypeSpinner;
    private View mLoginFormView;
    private View mLoginStatusView;
    
    //logger tag
    private static final String APP_TAG = MainActivity.APP_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setup);

        // Set up the setup form.
        custMobile = (EditText) findViewById(R.id.custMobile);
        custName = (EditText) findViewById(R.id.custName);
        custId = (EditText) findViewById(R.id.custId);
        idTypeSpinner = (Spinner) findViewById(R.id.idTypeSpinner);

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);

        findViewById(R.id.sign_in_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptLogin();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_setup, menu);
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (task != null) {
            return;
        }

        // Reset errors.
        custName.setError(null);
        custId.setError(null);
        custMobile.setError(null);

        // Store values at the time of the login attempt.
        name = custName.getText().toString();
        id = custId.getText().toString();
        mobile = custMobile.getText().toString();
        idType = (String) idTypeSpinner.getSelectedItem();

        boolean cancel = false;
        View focusView = null;

        // Check for valid fields
        if (TextUtils.isEmpty(name)) {
            custName.setError(getString(R.string.error_field_required));
            focusView = custName;
            cancel = true;
        } else if (TextUtils.isEmpty(id)) {
            custId.setError(getString(R.string.error_field_required));
            focusView = custName;
            cancel = true;
        } else if (TextUtils.isEmpty(mobile)) {
            custMobile.setError(getString(R.string.error_field_required));
            focusView = custMobile;
            cancel = true;
        } else if (!PhoneNumberUtils.isGlobalPhoneNumber(mobile)) {
            custMobile.setError(getString(R.string.error_invalid_mobile));
            focusView = custMobile;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt setup
            // flag form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user setup attempt.
            showProgress(true);
            JSONObject jobj = new JSONObject();
            try {
                jobj.accumulate("customerName", name);
                jobj.accumulate("customerIdNumber", id);
                jobj.accumulate("customerIdType", idType);
                jobj.accumulate("customerMobile", mobile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            task = new SetupTask();
            String url = "http://cutebalrog.com:8080/OCBC-QM-Server-web/webresources/Customer/addCustomer";
            Pair<String, JSONObject> pair = new Pair<String, JSONObject>(url, jobj);
            task.execute(pair);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime)
            .alpha(show ? 1 : 0)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginStatusView.setVisibility(show ? View.VISIBLE
                            : View.GONE);
                }
            });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime)
            .alpha(show ? 0 : 1)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE
                            : View.VISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous setup task used to register
     * the user.
     */
    public class SetupTask extends AsyncTask<Pair<String, JSONObject>, Void, String> {

        private boolean hasError = false;

        @Override
        protected String doInBackground(Pair<String, JSONObject>... pair) {
            // init our variables for the http connection
            String responseString = null;
            InputStream responseStream = null;
            HttpURLConnection urlConnection = null;
            OutputStreamWriter writer = null;

            try {
                URL url = new URL(pair[0].first);
                JSONObject data = pair[0].second;
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.addRequestProperty("Content-type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                // write our json object to the connection
                writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(data.toString());
                writer.close();

                responseStream = new BufferedInputStream( urlConnection.getInputStream());
                Log.d(APP_TAG, "response code: " + urlConnection.getResponseCode());
                responseString = CrossCutting.readStream(responseStream);
            } catch (Exception e) {
                Log.e(APP_TAG, "ERROR!");
                Log.e(APP_TAG, e.getMessage());
                hasError = true;
                return "";
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String token) {
            Log.d(APP_TAG, "Finished sending info, got user token: " + token);
            task = null;
            showProgress(false);

            if (TextUtils.isEmpty(token) || hasError) {
                showTryAgainDialog();
            } else {
                Log.d(APP_TAG, "success! got response for setup: " + token);

                showSuccessDialogReturnToMain(token);
            }
        }

        @Override
        protected void onCancelled() {
            task = null;
            showProgress(false);
        }
    }

    // something to show the user when something bad happens
    public void showTryAgainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Something bad has happened, please try again.").setTitle("Oh noes...");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog tryAgainDialog = builder.create();
        tryAgainDialog.show();
    }

    public void showSuccessDialogReturnToMain(final String token) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Welcome to OCBC Waiting Time App.").setTitle("Successfully Setup");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                returnToMainWithToken(token);
            }
        });
        AlertDialog successDialog = builder.create();
        successDialog.show();
    }

    /**
     * Brings the user to the MainActivity. It also returns
     * the userToken that we got from the server.
     * @param token that SetupTask got from server
     */
    private void returnToMainWithToken(String token) {
        Intent result = new Intent();
        result.putExtra("userToken", token);
        setResult(Activity.RESULT_OK, result);
        finish();
    }
}
