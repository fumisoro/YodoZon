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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import java.util.ArrayList;
import java.util.List;

import android.os.StrictMode;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

public class MainActivity extends ActionBarActivity {
    private EditText editText;
    private TextView yodobashiTextView, amazonTextView;
    private String query = "";
    private Button button;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yodobashiTextView = (TextView)findViewById(R.id.textView);
        amazonTextView = (TextView)findViewById(R.id.textView2);

        listView = (ListView)findViewById(R.id.listView);



        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                query = editText.getText().toString();
                String yodobashiUrl = String.format("http://www.yodobashi.com/ec/category/index.html?cate=&word=%s&gint=\"\"", query);
                String amazonUrl = String.format("http://www.amazon.co.jp/s?url=search-alias=aps&field-keywords=%s", query);
                getHtmlSource(yodobashiUrl, yodobashiTextView, "yodobashi");
                getHtmlSource(amazonUrl, amazonTextView, "amazon");
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


    private void getHtmlSource(String urlString, TextView textView, String mode){
        Log.d("デバッグ", urlString);
        DownloadTask task = new DownloadTask(urlString, textView, mode);
        task.execute("start");
    }

    public class DownloadTask extends AsyncTask<String, Integer, Elements> {

        private String urlString;
        private TextView textView;
        private Document document;
        private String mode;

        DownloadTask(String urlString, TextView textView, String mode){
            super();
            this.urlString = urlString;
            this.textView = textView;
            this.mode = mode;
        }

        @Override
        protected Elements doInBackground(String... params) {
            Elements title = null;
            try {
                document = Jsoup.connect(urlString).get();
                title = document.select("a.productListPostTag.clicklog.cl-schRlt");
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
                ArrayList<String> list = new ArrayList<String>();
                for(Element e: result){
                    list.add(e.toString());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(listView.getContext(), R.layout.list, list);
                Log.d("デバッグ", list.toString());
                listView.setAdapter(adapter);
                textView.setText(result.toString());
            }
        }


    }
}
