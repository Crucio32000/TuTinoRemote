package com.helloworld.nicita.nightfox_hw;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by nicita on 17/02/16.
 */
public class webViewHandler extends WebViewClient {
    int customTimeout = 5000;
    ProgressDialog progressBar;
    AlertDialog.Builder alert;
    int s_width = 640;
    int s_height = 480;

    public webViewHandler(Context context, int timeout) {

        this.customTimeout = timeout;
        this.progressBar = new ProgressDialog(context);
        this.alert = new AlertDialog.Builder(context).setTitle("Error!");
    }

    CountDownTimer timeout = new CountDownTimer(this.customTimeout,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
                //Do Something
        }

        @Override
        public void onFinish() {
            // In case loading is taking too much time
            if(progressBar.isShowing()) {
                progressBar.dismiss();
            }
        }
    };

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        //It enters inside this loop twice. Once when initiated , two when url changes
        view.setVisibility(View.INVISIBLE);
        view.setBackgroundColor(Color.TRANSPARENT);
        this.progressBar = ProgressDialog.show(this.progressBar.getContext(), url, "Loading...");
        this.timeout.start();
        //
        //view.setInitialScale(this.getScale());
    }

    public void onLoadResource(WebView view, String url) {
        //Enters just once, when an url is set and start downloading its content. (Regardless the response)
        view.setVisibility(view.VISIBLE);
    }


    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (this.progressBar.isShowing()) {
            this.progressBar.dismiss();
        }
        this.alert.setMessage(request.toString());
        AlertDialog alertToShow = this.alert.create();
        alertToShow.show();
    }

    public void onPageFinished(WebView view, String url) {
        //When URL has been loaded, dismiss progressBar
        if (this.progressBar.isShowing()) {
            this.progressBar.dismiss();
        }

        view.setVisibility(View.VISIBLE);
    }

    private int getScale() {
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        int valW = this.s_width/width;
        valW = valW * 100;
        int valH = this.s_height/height;
        valH = valH * 100;
        int val = Math.min(valW,valH);
        return val;
    }
}
