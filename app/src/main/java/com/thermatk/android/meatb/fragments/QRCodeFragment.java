package com.thermatk.android.meatb.fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;

import static com.thermatk.android.meatb.helpers.CalendarHelper.findAndDeleteCalendars;
import static com.thermatk.android.meatb.helpers.NotificationsHelper.doNotify;


public class QRCodeFragment extends Fragment{
    String authScript="javascript:(function main()  \\u000d\\u000a{\\u000d\\u000a    var check_login = false;\\u000d\\u000a    var values = new Array(        \\u000d\\u000a        'https:\\/\\/idp.unibocconi.it:443\\/idp\\/Authn\\/UserPassword',\\u000d\\u000a        'http:\\/\\/idp.unibocconi.it:443\\/idp\\/Authn\\/UserPassword',\\u000d\\u000a        'https:\\/\\/idp.unibocconi.it\\/idp\\/Authn\\/UserPassword',\\u000d\\u000a        'http:\\/\\/idp.unibocconi.it\\/idp\\/Authn\\/UserPassword'\\u000d\\u000a    );\\u000d\\u000a        \\u000d\\u000a    for (i in values) {\\u000d\\u000a        if (location.href.toLowerCase().indexOf(values[i].toLowerCase()) != -1) {\\u000d\\u000a            check_login = true;    \\u000d\\u000a        }\\u000d\\u000a    }\\u000d\\u000a    \\u000d\\u000a    var values_fail = new Array(        \\u000d\\u000a        'loginfailed',\\u000d\\u000a        'credentials not recognized'\\u000d\\u000a    );\\u000d\\u000a    \\u000d\\u000a    var loginFailed = false;\\u000d\\u000a    for (i in values_fail) {\\u000d\\u000a        if (location.href.toLowerCase().indexOf(values_fail[i].toLowerCase()) != -1 \\u000d\\u000a            || document.getElementsByTagName('html')[0].innerHTML.toLowerCase().indexOf(values_fail[i].toLowerCase()) > -1) {\\u000d\\u000a            loginFailed = true;    \\u000d\\u000a        }\\u000d\\u000a    }\\u000d\\u000a    \\u000d\\u000a    Android.addLog('checklogin ' + check_login + ' --- ' + location.href);\\u000d\\u000a    if (loginFailed) {\\u000d\\u000a        Android.currentState('KO');\\u000d\\u000a    }\\u000d\\u000a    else if (check_login)\\u000d\\u000a    {\\u000d\\u000a        Android.currentState('PR');\\u000d\\u000a        var usernameFieldName = 'j_username';\\u000d\\u000a        var passwordFieldName = 'j_password';\\u000d\\u000a                \\u000d\\u000a        var usernameField   = null;\\u000d\\u000a        var passwordField   = null;\\u000d\\u000a        var submitButton    = null;\\u000d\\u000a \\u000d\\u000a        var inputElements = document.getElementsByTagName('input');\\u000d\\u000a        for (i in inputElements) {\\u000d\\u000a            if(inputElements[i].type == 'text' && inputElements[i].name == usernameFieldName) {\\u000d\\u000a                usernameField = inputElements[i];\\u000d\\u000a            }\\u000d\\u000a            else if(inputElements[i].type == 'password' && inputElements[i].name == passwordFieldName) {\\u000d\\u000a                passwordField = inputElements[i];\\u000d\\u000a            }\\u000d\\u000a\\u000d\\u000a            if (usernameField && passwordField) {\\u000d\\u000a                break;\\u000d\\u000a            }\\u000d\\u000a        }\\u000d\\u000a        \\u000d\\u000a        var buttonElements = document.getElementsByTagName(\\\"button\\\");\\u000d\\u000a        for (b in buttonElements) {\\u000d\\u000a            if(buttonElements[b].type == \\\"submit\\\") {\\u000d\\u000a                submitButton = buttonElements[b];\\u000d\\u000a            }\\u000d\\u000a\\u000d\\u000a            \\/\\/ detected submit button, stop searching...\\u000d\\u000a            if (submitButton) {\\u000d\\u000a                break;\\u000d\\u000a            }\\u000d\\u000a        }\\u000d\\u000a                \\u000d\\u000a        \\u000d\\u000a        if (!usernameField || !passwordField || !submitButton) {\\u000d\\u000a            Android.currentState('KO');\\u000d\\u000a        }\\u000d\\u000a \\u000d\\u000a        usernameField.value = '{username}';\\u000d\\u000a        passwordField.value = '{password}';\\u000d\\u000a        submitButton.click();\\u000d\\u000a    }\\u000d\\u000a    else\\u000d\\u000a    {\\u000d\\u000a        Android.currentState('OK');\\u000d\\u000a    }\\u000d\\u000a}) ()\\u000d\\u000a";

    public QRCodeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("QR Code");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_qr, container, false);
        WebView qrWebView = (WebView) rootView.findViewById(R.id.webview);


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String username = sharedPrefs.getString("bocconiusername", null);
        String password = sharedPrefs.getString("bocconipassword", null);
        if(username == null || password == null) {
            Log.d(LogConst.LOG," WEBVIEW: Couldn't load credentials");
        }
        authScript = authScript.replace("{username}", username).replace("{password}", password);

        WebSettings webSettings = qrWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT <= 18) {
            webSettings.setSavePassword(false);
        } else {
            // "Saving passwords in WebView will not be supported in future versions"
        }
        //webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);
        qrWebView.setInitialScale(100);
        WebAppInterface wInt = new WebAppInterface(getActivity());
        qrWebView.addJavascriptInterface(wInt, "Android");

        qrWebView.setWebViewClient(new WebViewClient() {
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
                        if (Build.VERSION.SDK_INT >= 19) {
                            view.evaluateJavascript(QRCodeFragment.this.authScript, new ValueCallback<String>() {
                                public void onReceiveValue(String s) {
                                    Log.d(LogConst.LOG,"WebView onReceiveValue " + s);
                                }
                            });
                        } else {
                            view.loadUrl(QRCodeFragment.this.authScript);
                        }
                }
        });
        qrWebView.loadUrl("https://badge.unibocconi.it/index.php?age=s");
        //Button mSignInButton = (Button) rootView.findViewById(R.id.button);

        return rootView;
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
        }
        @JavascriptInterface
        public void addLog(String toast) {
        }
    }
}
