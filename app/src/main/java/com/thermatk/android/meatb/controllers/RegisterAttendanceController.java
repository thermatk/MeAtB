package com.thermatk.android.meatb.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

import com.bluelinelabs.conductor.Controller;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.yabAPIClient;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class RegisterAttendanceController extends Controller {
    private final static int AS_RESULT_SUCCESS =1;
    private final static int AS_RESULT_WARNING =2;
    private final static int AS_RESULT_NETWORK =3;
    private final static int AS_RESULT_OTHER =4;

    // UI references.
    private TextInputEditText mCodiceView;
    private View mProgressView;
    private View mAttendanceFormView;
    private TextView mErrorTextView;
    private boolean doingAsync = false;

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("doingAsync", doingAsync);

    }
    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Attendance");
        if(savedInstanceState != null) {
            if(savedInstanceState.getBoolean("doingAsync", false)) {
                RetainFragment.setFragment(this);
                doingAsync = true;
            }
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {

        View rootView = inflater.inflate(R.layout.controller_register_attendance, container, false);


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
        if(doingAsync) {
            showProgress(true);
        }
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
            mCodiceView.setError(getActivity().getString(R.string.error_field_required));
            focusView = mCodiceView;
            cancel = true;
        } else if (!isCodiceValid(codice)) {
            mCodiceView.setError(getActivity().getString(R.string.error_faulty_codice));
            focusView = mCodiceView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {


            doingAsync = true;
            RetainFragment retainFragment =
                    RetainFragment.findOrCreateRetainFragment(getActivity().getFragmentManager(), codice);
            RetainFragment.setFragment(this);
            retainFragment.loadAsync();
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

    private void responseFromAsync(int result, String message) {
        Log.d(LogConst.LOG, "Got response from async");

        if(result == AS_RESULT_SUCCESS) {
            showInfo(message, false);
        } else if (result == AS_RESULT_WARNING) {
            // TODO: Implement warnings in orange
            showInfo(message, false);
        } else if(result == AS_RESULT_OTHER) {
            // TODO: show different error codes
            showInfo(message, true);
        } else if(result == AS_RESULT_NETWORK) {
            showInfo(message, true);
        } else {
            showInfo("Unknown error", true);
        }
        showProgress(false);
        doingAsync = false;
    }

    public static class RetainFragment extends Fragment {
        private static final String TAG = "RetainFragmentAttendance";
        private static String codice;
        private static RegisterAttendanceController mFragment;


        public RetainFragment() {
        }
        public static void setFragment(RegisterAttendanceController current) {
            mFragment = current;
        }
        public static RetainFragment findOrCreateRetainFragment(FragmentManager fm, String codiceFragment) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
                fm.beginTransaction().add(fragment, TAG).commit();
                codice = codiceFragment;
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
                private String asyncResultMessage;
                private int asyncResult;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mFragment.showProgress(true);
                }

                @Override
                protected Void doInBackground(JSONObject... params) {
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
                            if(esito == 1) {
                                asyncResult = AS_RESULT_SUCCESS;
                                ////
                                Log.i(LogConst.LOG, "AttendRegRequest success");
                                asyncResultMessage =(new StringBuilder()).append("Success: ").append(message).append(" ").append(wrkMessage).toString();

                            } else if (esito == 2) {
                                asyncResult = AS_RESULT_WARNING;
                                ////
                                Log.i(LogConst.LOG, "AttendRegRequest warning");
                                asyncResultMessage =(new StringBuilder()).append("Warning: ").append(message).append(" ").append(wrkMessage).toString();
                            } else {

                                asyncResult = AS_RESULT_OTHER;
                                Log.i(LogConst.LOG, "AttendRegRequest success, state FAIL, response:" + response.toString());
                                asyncResultMessage =(new StringBuilder()).append("Something went wrong: ").append(message).append(" ").append(wrkMessage).toString();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                            Log.i(LogConst.LOG, "AuthRequest failed " + response);


                            asyncResult = AS_RESULT_NETWORK;
                            asyncResultMessage = "Network error";
                        }
                    };
                    yabAPIClient registerClient = new yabAPIClient(mFragment.getActivity(),false);
                    registerClient.registerAttendance(codice, responseHandler);

                    return null;
                }

                @Override
                protected void onPostExecute(Void response) {
                    mFragment.responseFromAsync(asyncResult, asyncResultMessage);
                }
            }.execute();
        }
    }
}
