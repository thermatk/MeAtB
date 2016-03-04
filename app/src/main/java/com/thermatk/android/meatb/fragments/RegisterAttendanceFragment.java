package com.thermatk.android.meatb.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.yabAPIClient;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class RegisterAttendanceFragment extends Fragment {

    // UI references.
    private TextInputEditText mCodiceView;
    private View mProgressView;
    private View mAttendanceFormView;
    private TextView mErrorTextView;

    public RegisterAttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_register_attendance, container, false);


        // Set up the form.
        mCodiceView = (TextInputEditText) rootView.findViewById(R.id.codice);

        mCodiceView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.attendance || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) rootView.findViewById(R.id.attendance_sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mAttendanceFormView = rootView.findViewById(R.id.attendance_form);
        mProgressView = rootView.findViewById(R.id.attendance_progress);
        mErrorTextView = (TextView) rootView.findViewById(R.id.errorTextView);

        return rootView;
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {

        // Reset errors.
        mCodiceView.setError(null);

        // Store values at the time of the login attempt.
        final String codice = mCodiceView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty(codice)) {
            mCodiceView.setError(getString(R.string.error_field_required));
            focusView = mCodiceView;
            cancel = true;
        } else if (!isCodiceValid(codice)) {
            mCodiceView.setError(getString(R.string.error_faulty_codice));
            focusView = mCodiceView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // fail by default
                    int esito = 0;
                    String message = null;
                    String wrkMessage = null;

                    try {
                        esito = Integer.parseInt(response.get("esito").toString());
                        message = response.get("message").toString();
                        wrkMessage = response.get("wrkmessage").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showProgress(false);
                    if(esito == 1) {
                        ////
                        Log.i(LogConst.LOG, "AttendRegRequest success");
                        // TODO: Show success
                        showInfo("Success: " + message + " " + wrkMessage, false);
                    } else if (esito == 2) {
                        ////
                        Log.i(LogConst.LOG, "AttendRegRequest warning");
                        // TODO: Implement warnings in orange
                        showInfo("Warning: " + message + " " + wrkMessage, false);
                    } else {

                        Log.i(LogConst.LOG, "AttendRegRequest success, state FAIL, response:" + response.toString());
                        // TODO: show different error codes
                        showInfo("Something went wrong: "+ message + wrkMessage, true);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                    Log.i(LogConst.LOG, "AuthRequest failed " + response);


                    showInfo("Network error", true);
                    showProgress(false);
                }
            };
            yabAPIClient registerClient = new yabAPIClient(getActivity());
            registerClient.registerAttendance(codice, responseHandler);
        }
    }

    private boolean isCodiceValid(String username) {
        //TODO: Do the logic
        return true;
    }

    private void showInfo(String info, boolean isError){
        //TODO: less dirty-hacky success :)
        mErrorTextView.setText(info);
        if(isError) {
            mErrorTextView.setTextColor(Color.parseColor("#f50b0b"));
        } else {
            mErrorTextView.setTextColor(Color.parseColor("#0bf513"));
        }
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

            mAttendanceFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAttendanceFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAttendanceFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mAttendanceFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
