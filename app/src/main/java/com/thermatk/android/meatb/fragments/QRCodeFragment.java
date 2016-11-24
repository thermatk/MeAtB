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
    String authScript="javascript:(function main(){Android.currentState('PR');var check_dest=false;var values=new Array(\"Appelli\");for(i in values){if(location.href.toLowerCase().indexOf(values[i].toLowerCase())!=-1){check_dest=true}}if(check_dest){Android.currentState('OK')}else if(location.href.toLowerCase().indexOf('loginfailed=true')!=-1){Android.currentState('KO')}else{var check_login=false;var loginURLs=new Array();loginURLs[0]='https://idp.unibocconi.it:443/idp/Authn/UserPassword';loginURLs[1]='http://idp.unibocconi.it:443/idp/Authn/UserPassword';loginURLs[2]='https://idp.unibocconi.it/idp/Authn/UserPassword';loginURLs[3]='http://idp.unibocconi.it/idp/Authn/UserPassword';for(i in loginURLs){if(location.href.indexOf(loginURLs[i])!=-1){check_login=true}}if(check_login){var usernameFieldName='j_username';var passwordFieldName='j_password';var usernameField=null;var passwordField=null;var submitButton=null;var inputElements=document.getElementsByTagName('input');for(i in inputElements){if(inputElements[i].type=='text'&&inputElements[i].name==usernameFieldName){usernameField=inputElements[i]}else if(inputElements[i].type=='password'&&inputElements[i].name==passwordFieldName){passwordField=inputElements[i]}if(usernameField&&passwordField){break}}var buttonElements=document.getElementsByTagName(\"button\");for(b in buttonElements){if(buttonElements[b].type==\"submit\"){submitButton=buttonElements[b]}if(submitButton){break}}if(usernameField&&passwordField&&submitButton){usernameField.value='{username}';passwordField.value='{password}';submitButton.click()}}}})();";
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
            Log.d(LogConst.LOG, "Webview state msg " + toast);
        }
        @JavascriptInterface
        public void addLog(String toast) {
        }
    }
}
