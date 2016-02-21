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
    private String query = "";
    private Button button;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView);

        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                query = editText.getText().toString();
                String yodobashiUrl = String.format("http://www.yodobashi.com/ec/category/index.html?cate=&word=%s&gint=\"\"", query);
                String amazonUrl = String.format("http://www.amazon.co.jp/s?url=search-alias=aps&field-keywords=%s", query);
                getHtmlSource(yodobashiUrl, "yodobashi");
                getHtmlSource(amazonUrl, "amazon");
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


    private void getHtmlSource(String urlString, String mode){
        Log.d("デバッグ", urlString);
        DownloadTask task = new DownloadTask(urlString, mode);
        task.execute("start");
    }

    public class DownloadTask extends AsyncTask<String, Integer, Elements> {

        private String urlString;
        private Document document;
        private String mode;

        DownloadTask(String urlString, String mode){
            super();
            this.urlString = urlString;
            this.mode = mode;
        }

        @Override
        protected Elements doInBackground(String... params) {
            Elements commodities = null;
            try {
                document = Jsoup.connect(urlString).get();
                if (mode == "yodobashi") {
                    commodities = document.select("a.productListPostTag.clicklog.cl-schRlt");
                }else if(mode == "amazon"){
                    commodities = document.select("a.productListPostTag.clicklog.cl-schRlt");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return commodities;
        }

        @Override
        protected void onPostExecute(Elements result) {
            if (result == null) {

            }
            else {
                ArrayList<String> list = new ArrayList<>();
                for(Element e: result){
                    if(mode == "yodobashi") {
                        list.add(e.select("div.fs14").toString());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(listView.getContext(), R.layout.list, list);
                Log.d("デバッグ", list.toString());
                if (mode == "yodobashi") {
                    listView.setAdapter(adapter);
                }
            }
        }


    }
}
