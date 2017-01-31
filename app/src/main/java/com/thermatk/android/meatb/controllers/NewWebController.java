package com.thermatk.android.meatb.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bluelinelabs.conductor.Controller;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.yabAPIClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import io.realm.RealmResults;


public class NewWebController extends Controller {
    private WebView theWebView;
    private View mProgressView;
    String authScript="javascript:(function main(){JsMobileApp.addLog('script '+location.href);JsMobileApp.currentState('PR');var usernameField=null;var passwordField=null;var inputElements=document.getElementsByTagName('input');for(i in inputElements){if(inputElements[i].type=='text'&&inputElements[i].name=='j_username'){usernameField=inputElements[i]}else if(inputElements[i].type=='password'&&inputElements[i].name=='j_password'){passwordField=inputElements[i]}if(usernameField&&passwordField){break}}if(usernameField&&passwordField){JsMobileApp.addLog('script shibboleth tento autologin');usernameField.value='{username}';passwordField.value='{password}';JsMobileApp.currentState('OK');document.forms[0].submit()}else{JsMobileApp.currentState('KO')}})()";

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        getActivity().setTitle("NewAtB");
        // TODO: orientation change fun etc

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.controller_qr, container, false);
        theWebView = (WebView) rootView.findViewById(R.id.webview);
        mProgressView = rootView.findViewById(R.id.qr_progress);

        showProgress(true);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String username = sharedPrefs.getString("bocconiusername", null);
        String password = sharedPrefs.getString("bocconipassword", null);
        if(username == null || password == null) {
            Log.d(LogConst.LOG," WEBVIEW: Couldn't load credentials");
        }
        authScript = authScript.replace("{username}", username).replace("{password}", password);

        WebSettings webSettings = theWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT <= 18) {
            webSettings.setSavePassword(false);
        }
        webSettings.setDomStorageEnabled(true);
        WebAppInterface wInt = new WebAppInterface(getActivity());
        theWebView.addJavascriptInterface(wInt, "Android");

        theWebView.setWebViewClient(new WebViewClient() {
                @SuppressWarnings("deprecation")
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
                @TargetApi(Build.VERSION_CODES.N)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return false;
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (url.contains("idp.unibocconi")) {
                        Log.d(LogConst.LOG,"NewAtB in a case of login");

                        if (Build.VERSION.SDK_INT >= 19) {
                            view.evaluateJavascript(NewWebController.this.authScript, new ValueCallback<String>() {
                                public void onReceiveValue(String s) {
                                    Log.d(LogConst.LOG,"WebView onReceiveValue " + s);
                                }
                            });
                        } else {
                            view.loadUrl(NewWebController.this.authScript);
                        }

                    }
                }
        });
        theWebView.setWebChromeClient(new WebChromeClient());
        //qrWebView.loadUrl("https://badge.unibocconi.it/index.php?age=s");

        RetainFragment retainFragment =
                RetainFragment.findOrCreateRetainFragment(getActivity().getFragmentManager());
        RetainFragment.setFragment(this);
        retainFragment.waitAsync();

        return rootView;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            theWebView.setVisibility(show ? View.GONE : View.VISIBLE);
            theWebView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    theWebView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            theWebView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
        }
        @JavascriptInterface
        public void currentState(String toast) {
            Log.d(LogConst.LOG, "Webview state msg " + toast);
            if(toast.equals("OK")) {
                // show webview
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                    }
                });
            }
        }
        @JavascriptInterface
        public void addLog(String toast) {
        }
    }

    public void requestDoneSuccess(String username, String token, String url) {
        showProgress(false);
        Log.d(LogConst.LOG, "token: " + token + ", url: " + url);

        final HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", token);
        hashMap.put("cn", username);
        theWebView.loadUrl(url + "/AppMobile", (Map)hashMap);
    }
    public void requestDoneFail() {
        showProgress(false);
        Log.d(LogConst.LOG, "token not received?");
    }

    public static class RetainFragment extends Fragment {
        private static final String TAG = "RetainFragmentNewAtB";
        private static NewWebController mFragment;

        public RetainFragment() {
        }
        public static void setFragment(NewWebController current) {
            mFragment = current;
        }
        public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
                fm.beginTransaction().add(fragment, TAG).commit();
            }
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        private void waitAsync() {
            // DO WORK
            new AsyncTask<JSONObject, Void, Void>() {
                private String username;
                private int state;
                private String token;
                private String baseUrl;

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
                            try {
                                username = response.getString("username");
                                state = response.getInt("statoAuth");
                                token = response.getString("token");
                                baseUrl = response.getString("appMobileBaseUrl");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.i(LogConst.LOG, "TokenRequest success:" + response.toString());
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                            Log.i(LogConst.LOG, "TokenRequest failed " + response);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String response, Throwable error) {
                            Log.i(LogConst.LOG, "TokenRequest failed " + response);
                        }
                    };

                    Log.i(LogConst.LOG, "Starting yabAPIClient");
                    yabAPIClient tokenClient = new yabAPIClient(mFragment.getApplicationContext(),false);
                    Log.i(LogConst.LOG, "getting token request...");
                    tokenClient.getNewAtBToken(responseHandler);
                    return null;
                }

                @Override
                protected void onPostExecute(Void response) {

                    // state OK
                    if(state == 1) {
                        mFragment.requestDoneSuccess(username, token, baseUrl);
                    } else {
                        mFragment.requestDoneFail();
                    }
                }
            }.execute();
        }
    }
}
