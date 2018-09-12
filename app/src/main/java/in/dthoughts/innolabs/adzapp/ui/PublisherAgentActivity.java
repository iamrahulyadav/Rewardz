package in.dthoughts.innolabs.adzapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import in.dthoughts.innolabs.adzapp.R;

public class PublisherAgentActivity extends AppCompatActivity {

    WebView loginWebView;
   public ProgressBar WebViewprogressBar;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher_agent_login);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.title_Ad_publisher_and_Agent));
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        WebViewprogressBar = findViewById(R.id.WebViewProgressBar);
        WebViewprogressBar.setMax(100);
        loginWebView = findViewById(R.id.loginWebView);

        loginWebView.loadUrl("http://www.adzapp.in/");
        loginWebView.setWebViewClient(new WebViewClientImpl(this));
        WebSettings webSettings = loginWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        loginWebView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress){
                WebViewprogressBar.setVisibility(View.VISIBLE);
                WebViewprogressBar.setProgress(progress);

                mToolbar.setTitle("Loading...");

                if(progress == 100){
                    WebViewprogressBar.setVisibility(View.GONE);
                    mToolbar.setTitle(view.getTitle());
                }
                super.onProgressChanged(view, progress);
            }
        });
        WebViewprogressBar.setProgress(0);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.loginWebView.canGoBack()) {
            this.loginWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}

