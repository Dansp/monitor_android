package com.amenuo.monitor.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amenuo.monitor.R;
import com.amenuo.monitor.utils.PLog;
import com.amenuo.monitor.view.TitleBar;

public class WebviewActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView mWebView;
    private ImageView mBack;
    private ImageView mForward;
    private ImageView mRefresh;
    private View mProgressView;
    private int mScreenWidth;
    private ViewWrapper mViewWrapper;
    private TitleBar mTitleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String address = intent.getStringExtra("address");

        // TODELETE
        if (TextUtils.isEmpty(address)){
            address = "http://www.baidu.com";
        }
        mTitleBar = (TitleBar)findViewById(R.id.webview_titlebar);
        mTitleBar.setMiddleTitle(name);

        mWebView = (WebView)this.findViewById(R.id.webview_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.setWebViewClient(mMyWebViewClient);
        mWebView.setWebChromeClient(mMyWebChromeClient);
        mWebView.loadUrl(address);

        mBack = (ImageView)this.findViewById(R.id.webview_back);
        mForward = (ImageView)this.findViewById(R.id.webview_forward);
        mRefresh = (ImageView)this.findViewById(R.id.webview_refresh);
        mProgressView = this.findViewById(R.id.webview_progress);
        mBack.setOnClickListener(this);
        mForward.setOnClickListener(this);
        mRefresh.setOnClickListener(this);

        WindowManager wm = this.getWindowManager();
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mViewWrapper = new ViewWrapper(mProgressView);

        mTitleBar = (TitleBar)this.findViewById(R.id.webview_titlebar);
        mTitleBar.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.stopLoading();
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack())
        {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private WebViewClient mMyWebViewClient = new WebViewClient(){

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            PLog.e("start");
            startProgress();
            setButtonState();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            endProgress();
            setButtonState();
        }
    };

    private void setButtonState(){
        mForward.setEnabled(mWebView.canGoForward());
        mBack.setEnabled(mWebView.canGoBack());
    }

    private WebChromeClient mMyWebChromeClient = new WebChromeClient(){

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            setProgressView(newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mTitleBar.setMiddleTitle(title);
        }
    };

    public void startProgress(){
        int width = 1;
        ObjectAnimator animator = ObjectAnimator.ofInt(mViewWrapper, "width", width);
        animator.setDuration(100).start();
    }

    public void setProgressView(int progress){
        PLog.e("progress : " + progress);
        int width = (int)(progress * 1.0 * mScreenWidth / 100.0);
        ObjectAnimator animator = ObjectAnimator.ofInt(mViewWrapper, "width", width);
        animator.setDuration(100).start();
    }

    public void endProgress(){
        int width = 0;
        ObjectAnimator animator = ObjectAnimator.ofInt(mViewWrapper, "width", mScreenWidth, width);
        animator.setDuration(100).start();
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.webview_back){
            if(mWebView.canGoBack()){
                mWebView.goBack();
            }
        }else if(resId == R.id.webview_forward){
            if (mWebView.canGoForward()){
                mWebView.goForward();
            }
        }else if(resId == R.id.webview_refresh){
            mWebView.reload();
        }
    }

    class ViewWrapper {
        View mTargetView;

        public ViewWrapper(View v) {
            mTargetView = v;
        }

        public void setWidth(int width) {
            mTargetView.getLayoutParams().width = width;
            mTargetView.requestLayout();
        }

        // for view's width
        public int getWidth() {
            int width = mTargetView.getLayoutParams().width;
            return width;
        }
    }
}
