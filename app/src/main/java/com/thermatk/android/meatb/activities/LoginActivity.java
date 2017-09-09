package com.thermatk.android.meatb.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.thermatk.android.meatb.NewBocconiAPI;
import com.thermatk.android.meatb.data.model.UserApi;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.helpers.DataHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via username/password.
 */

public class LoginActivity extends AppCompatActivity {
    public enum LOGIN_RESULTS {
        SUCCESS, ERROR_CREDENTIALS, ERROR_NETWORK, ERROR_UNKNOWN
    }

    private boolean doingAsync = false;

    // UI references.
    @BindView(R.id.username)
    TextInputEditText mUsernameView;
    @BindView(R.id.password)
    TextInputEditText mPasswordView;
    @BindView(R.id.login_progress)
    View mProgressView;
    @BindView(R.id.login_form)
    View mLoginFormView;
    @BindView(R.id.errorTextView)
    TextView mErrorTextView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @OnClick(R.id.sign_in_button)
    public void submit(Button button) {
        attemptLogin();
    }

    @OnEditorAction(R.id.password)
    public boolean editorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            attemptLogin();
            return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("doingAsync", doingAsync);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        this.setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("doingAsync", false)) {
                showProgress(true);
                RetainFragment.setActivity(this);
                doingAsync = true;
            }
        }
    }

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

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_faulty_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // If there was an error
        if (cancel) {
            focusView.requestFocus();
        } else {
            doingAsync = true;
            showProgress(true);
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

    private void showError(String error) {
        mErrorTextView.setText(error);

        if (mErrorTextView.getVisibility() == View.GONE) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.animate().setDuration(shortAnimTime).alpha(1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mErrorTextView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void showProgress(final boolean show) {
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
    }

    @Override
    public void onBackPressed() {
        //preventing going back to MainActivity
    }

    private void responseFromAsync(LOGIN_RESULTS result) {
        Log.d(LogConst.LOG, "Got response from async");

        switch (result) {
            case ERROR_CREDENTIALS:
                // TODO: understand the error codes
                showError("Credentials not recognized");
                break;

            case ERROR_NETWORK:
                showError("Network error");
                break;

            default:
                showError("Unknown error");
                break;
        }
        showProgress(false);
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
            }

            username = usernameAct;
            password = passwordAct;

            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        private void loadAsync() {
            NewBocconiAPI.getLoginAPIService().getLogin(NewBocconiAPI.basicAuthHeader(username, password)).enqueue(new Callback<UserApi>() {
                @Override
                public void onResponse(Call<UserApi> call, Response<UserApi> response) {
                    if (response.isSuccessful()) {
                        UserApi userApi = response.body();
                        if (userApi.getStatoAuth() == 1) {
                            Log.d(LogConst.LOG, "All Good! " + userApi.getToken());

                            DataHelper.writeNewInitData(userApi);
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString("bocconiusername", username);
                            editor.putString("bocconipassword", password);
                            editor.apply();

                            Intent returnIntent = new Intent(mActivity, NewActivity.class);
                            returnIntent.putExtra("userApi", userApi);
                            mActivity.finish();
                            startActivity(returnIntent);
                        } else {
                            Log.i(LogConst.LOG, "AuthRequest success, login state FAIL, response:" + response.body().toString());
                            mActivity.responseFromAsync(LOGIN_RESULTS.ERROR_CREDENTIALS);
                        }
                    } else {
                        Log.i(LogConst.LOG, "AuthRequest success, but FAILED, response:" + response.body().toString());
                        mActivity.responseFromAsync(LOGIN_RESULTS.ERROR_NETWORK);
                    }
                }

                @Override
                public void onFailure(Call<UserApi> call, Throwable t) {
                    Log.i(LogConst.LOG, "AuthRequest failed, network ");
                    mActivity.responseFromAsync(LOGIN_RESULTS.ERROR_NETWORK);
                }
            });
        }
    }
}

