package com.aiosleeve.aiosleeve.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aiosleeve.aiosleeve.MainActivity;
import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.view.ProgressHUD;

/**
 * Created by oneclickpc001 on 28/3/17.
 */
public class FragmentWebView extends Fragment {

    MainActivity mActivity;

    View createView;

    String mStringTitle = "";
    String mStringLink = "";

    ProgressHUD progressDialog;

    WebView mWebView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (MainActivity) getActivity();
        mActivity.mUtility.changeStatusbarColor(R.color.custom_header_color);

        mStringTitle = getArguments().getString("mStringTitle", "");
        mStringLink = getArguments().getString("mStringLink", "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        createView = inflater.inflate(R.layout.fragment_webview, container, false);

        mWebView = (WebView) createView.findViewById(R.id.fragment_webview);



        mWebView.setWebViewClient(new MyBrowser());
        startWebView(mStringLink);

        mWebView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    if (progressDialog == null || !progressDialog.isShowing())
                        progressDialog = ProgressHUD.showDialog(mActivity, true, false, new DialogInterface.OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                progressDialog.dismiss();
                            }
                        });
                }

                if (progress == 100) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                }
            }
        });

        return createView;
    }

    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.loadUrl(url);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
