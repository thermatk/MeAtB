package com.thermatk.android.meatb.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.thermatk.android.meatb.helpers.DataHelper;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.yabAPIClient;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity{
    private final static int AS_RESULT_SUCCESS =1;
    private final static int AS_RESULT_ERROR_CREDENTIAL =2;
    private final static int AS_RESULT_ERROR_NETWORK =3;

    // UI references.
    private TextInputEditText mUsernameView;
    private TextInputEditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mErrorTextView;
    private boolean doingAsync = false;

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("doingAsync", doingAsync);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        ///

        // Set up the login form.
        mUsernameView = (TextInputEditText) findViewById(R.id.username);

        mPasswordView = (TextInputEditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mErrorTextView = (TextView) findViewById(R.id.errorTextView);
        if(savedInstanceState != null) {
            if(savedInstanceState.getBoolean("doingAsync", false)) {
                showProgress(true);
                RetainFragment.setActivity(this);
                doingAsync = true;
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_faulty_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_faulty_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            doingAsync = true;
            RetainFragment retainFragment =
                    RetainFragment.findOrCreateRetainFragment(getFragmentManager(), username, password);
            RetainFragment.setActivity(this);
            retainFragment.loadAsync();
        }
    }

    private boolean isUsernameValid(String username) {
        //TODO: Do the logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Do the logic
        return true;
    }

    private void showError(String error){
        mErrorTextView.setText(error);

        if(mErrorTextView.getVisibility()==View.GONE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.animate().setDuration(shortAnimTime).alpha(1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mErrorTextView.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                mErrorTextView.setVisibility(View.VISIBLE);
            }
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
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    @Override
    public void onBackPressed() {
        //preventing going back to MainActivity
    }

    private void responseFromAsync(int result) {
        Log.d(LogConst.LOG, "Got response from async");

        if(result == AS_RESULT_SUCCESS) {
            Intent returnIntent =new Intent(this, MainActivity.class);
            finish();
            startActivity(returnIntent);
        } else if(result  == AS_RESULT_ERROR_CREDENTIAL) {
            showProgress(false);
            // TODO: show different error codes
            showError("Credentials not recognized");
        } else if(result ==AS_RESULT_ERROR_NETWORK) {
            showError("Network error");
            showProgress(false);

        } else {
            showError("Unknown error");
            showProgress(false);
        }
        doingAsync = false;
    }

    public static class RetainFragment extends Fragment {
        private static final String TAG = "RetainFragmentLogin";
        private static String username;
        private static String password;
        private static LoginActivity mActivity;


        public RetainFragment() {
        }
        public static void setActivity(LoginActivity current) {
            mActivity = current;
        }
        public static RetainFragment findOrCreateRetainFragment(FragmentManager fm, String usernameAct, String passwordAct) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
                fm.beginTransaction().add(fragment, TAG).commit();
                username = usernameAct;
                password = passwordAct;
            }
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        private void loadAsync() {
            new AsyncTask<JSONObject, Void, Void>() {
                private int asyncResult;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    mActivity.showProgress(true);
                }

                @Override
                protected Void doInBackground(JSONObject... params) {

                    JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // fail by default
                            int authState = 2;
                            try {
                                authState = Integer.parseInt(response.get("auth_state").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(authState == 1) {
                                // Store credentials
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                                editor.putString("bocconiusername", username);
                                editor.putString("bocconipassword", password);

                                editor.apply();
                                // TODO: Save profile info
                                DataHelper.writeInitData(response);
                                ////
                                Log.i(LogConst.LOG, "AuthRequest success, login state 1");
                                asyncResult = AS_RESULT_SUCCESS;
                            } else {

                                Log.i(LogConst.LOG, "AuthRequest success, login state FAIL, response:" + response.toString());

                                asyncResult = AS_RESULT_ERROR_CREDENTIAL;
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                            Log.i(LogConst.LOG, "AuthRequest failed " + response);
                            asyncResult = AS_RESULT_ERROR_NETWORK;
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String response, Throwable error) {
                            Log.i(LogConst.LOG, "AuthRequest failed " + response);
                            asyncResult = AS_RESULT_ERROR_NETWORK;
                        }
                    };

                    yabAPIClient.getLogin(username,password,responseHandler, false);
                    return null;
                }

                @Override
                protected void onPostExecute(Void response) {
                    mActivity.responseFromAsync(asyncResult);
                }
            }.execute();
        }
    }
}

