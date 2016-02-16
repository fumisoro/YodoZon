package com.example.fumiyaseki.yodozon;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    private WebView varWebView, varWebView2;
    private EditText editText;
    private String query = "";
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        varWebView = (WebView)findViewById(R.id.webView);
        varWebView2 = (WebView)findViewById(R.id.webView2);
        varWebView.setWebViewClient(new WebViewClient());
        varWebView2.setWebViewClient(new WebViewClient());
        varWebView.getSettings().setJavaScriptEnabled(true);
        varWebView2.getSettings().setJavaScriptEnabled(true);

        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                query = editText.getText().toString();
                varWebView.loadUrl(String.format("http://www.yodobashi.com/ec/category/index.html?cate=&word=%s&gint=\"\"", query));
                varWebView2.loadUrl(String.format("http://www.amazon.co.jp/s?url=search-alias=aps&field-keywords=%s", query));
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int actionId, KeyEvent event){
                if (event.getAction() == KeyEvent.ACTION_DOWN && actionId == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    button.performClick();
                }
                return false;
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_BACK) && varWebView.canGoBack()){
            varWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
