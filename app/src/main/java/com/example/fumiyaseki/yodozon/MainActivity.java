package com.example.fumiyaseki.yodozon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private String query = "";
    private Button button;
    private ListView listView;
    private LayoutInflater inflater;


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
                getHtmlSource(yodobashiUrl, "yodobashi");
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && actionId == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    button.performClick();
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                final Commodity c = (Commodity) listView.getItemAtPosition(position);
                String amazonUrl = String.format("http://www.amazon.co.jp/s?url=search-alias=aps&field-keywords=%s", c.name);
//                ExecutorService executorService = Executors.newFixedThreadPool(1);
//                GetCommodityInfoTask getCommodityInfoTask  = new GetCommodityInfoTask(c.name);
//                Future<Commodity> response = executorService.submit(getCommodityInfoTask);
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Document document = null;
                        String amazonUrl = String.format("http://www.amazon.co.jp/s?url=search-alias=aps&field-keywords=%s", c.name);
                        try {
                            document = Jsoup.connect(amazonUrl).get();
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        Element e = document.select("li.s-result-item.celwidget").first();
                        String price = e.select("span.a-size-base.a-color-price.s-price.a-text-bold").html();
                        String name = e.select("h2.a-size-base.a-color-null.s-inline.s-access-title.a-text-normal").html();
                        String url = e.select("a.a-link-normal.s-access-detail-page.a-text-normal").attr("href");
                        Commodity c = new Commodity(price, name, url);
                        Log.d("デバッグ", price);
                        Log.d("デバッグ", name);
                        Log.d("デバッグ", url);
                    }
                })).start();
//                try{
//                    Log.d("デバッグ", response.get().toString());
//                }catch (InterruptedException e1){
//
//                }catch (ExecutionException e2){
//
//                }
            }
        });
    }

    private void getHtmlSource(String urlString, String mode){
        DownloadTask task = new DownloadTask(urlString, listView, mode, this);
        task.execute("start");
    }


}
