package com.example.fumiyaseki.yodozon;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import android.os.StrictMode;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    private EditText editText;
    private TextView yodobashiTextView, amazonTextView;
    private String query = "";
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        yodobashiTextView = (TextView)findViewById(R.id.textView);
        amazonTextView = (TextView)findViewById(R.id.textView2);


        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                query = editText.getText().toString();
                String yodobashiUrl = String.format("http://www.yodobashi.com/ec/category/index.html?cate=&word=%s&gint=\"\"", query);
                String amazonUrl = String.format("http://www.amazon.co.jp/s?url=search-alias=aps&field-keywords=%s", query);
                try {
                    getHtmlSource(yodobashiUrl, yodobashiTextView);
                    getHtmlSource(amazonUrl, amazonTextView);

                }catch (IOException e) {

                }

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


    private void getHtmlSource(String urlString, TextView textView) throws IOException {
        DownloadTask task = new DownloadTask(urlString, textView);
        task.execute("start");
    }

    public class DownloadTask extends AsyncTask<String, Integer, Elements> {

        private String urlString;
        private TextView textView;
        private Document document;

        DownloadTask(String urlString, TextView textView){
            this.urlString = urlString;
            this.textView = textView;
        }

        @Override
        protected Elements doInBackground(String... params) {
            Elements title = null;
            try {
                document = Jsoup.connect(urlString).get();
                title = document.getElementsByTag("title");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return title;
        }

        @Override
        protected void onPostExecute(Elements result) {
            if (result == null) {

            }
            else {
                textView.setText(result.toString());
            }
        }


    }
}
