package com.aiosleeve.aiosleeve.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aiosleeve.aiosleeve.MainActivity;
import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.view.ProgressHUD;

public class FragmentAboutUs extends Fragment {
    View mCreateView;
    private MainActivity mMainActivity;
    public WebView mWebView;
    private ProgressHUD mProgressHUD;
    private String mStringWebURL = "http://komodotec.com:81/API/index.php/Aboutus/";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity=(MainActivity) getActivity();

//        mMainActivity.mImageViewDrawer.setVisibility(View.GONE);
//        mMainActivity.mImageViewBack.setVisibility(View.VISIBLE);
//        mMainActivity.mRelativeLayoutMain.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.custom_header_color));
//        mMainActivity.mUtility.changeStatusbarColor(R.color.color_white);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mCreateView = inflater.inflate(R.layout.fragment_about_us, container, false);

        mWebView = (WebView) mCreateView.findViewById(R.id.web_viewer_webview_new);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebViewClient(new MyWebClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.loadUrl(mStringWebURL);

        mMainActivity.mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });

        return mCreateView;
    }
    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            if (newProgress < 100) {
                if (mProgressHUD == null || !mProgressHUD.isShowing()) {
                    mProgressHUD = ProgressHUD.showDialog(getActivity(), true, false, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mProgressHUD.dismiss();
                        }
                    });
                }
            }

            if (newProgress == 100) {
                if (mProgressHUD != null || mProgressHUD.isShowing()) {
                    mProgressHUD.dismiss();
                }
            }
        }
    }

    public class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }



}
