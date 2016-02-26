package com.example.fumiyaseki.yodozon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jdeferred.Deferred;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by fumiyaseki on 2016/02/22.
 */


class DownloadTask extends AsyncTask<String, Integer, Elements> {

    private String urlString;
    private Document document;
    private ListView listView;
    private String mode;
    private Context context;

    DownloadTask(String urlString, ListView listView, String mode, Context context){
        super();
        this.urlString = urlString;
        this.listView = listView;
        this.mode = mode;
        this.context = context;
    }

    @Override
    protected Elements doInBackground(String... params) {
        Log.d("デバッグ", "タスク起動");
        Log.d("デバッグ", mode);
        Elements commodities = null;
        try {
            document = Jsoup.connect(urlString).get();
            if (mode == "yodobashi") {
                commodities = document.select("a.productListPostTag.clicklog.cl-schRlt");
            }else if(mode == "amazon"){
                Log.d("デバッグ", "ドキュメント切り取り");
                commodities = document.select("li.s-result-item.celwidget ");
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
            ArrayList<Commodity> commodityArrayList = new ArrayList<>();
            Log.d("デバッグ", result.size() + "");

            for(Element e: result){
                Log.d("デバッグ", e.toString());

                if(mode == "yodobashi") {
                    ExecutorService executorService = Executors.newFixedThreadPool(1);
                    GetImageTask getImageTask = new GetImageTask(e.select("img").attr("src"));
                    Future<Bitmap> response = executorService.submit(getImageTask);
                    try {
                        String price = e.select("strong.red").html();
                        String name = e.select("div.fs14").select("strong").html();
                        String url = "http://www.yodobashi.com/" + e.attr("href");
                        Bitmap image = response.get();
                        String point = e.select("strong.orange.ml10").html();
                        Commodity c = new Commodity(price, name, url, image, point);
                        commodityArrayList.add(c);
                    }catch (InterruptedException e1){


                    }catch (ExecutionException e2){

                    }
                }else if(mode == "amazon"){
                    ExecutorService executorService = Executors.newFixedThreadPool(1);
                    GetImageTask getImageTask = new GetImageTask(e.select("img").attr("src"));
                    Future<Bitmap> response = executorService.submit(getImageTask);
                    try {
                        String price = e.select("span.a-size-base.a-color-price.a-text-bold").first().html();
                        String name = e.select("h2.a-size-base.a-color-null.s-inline.s-access-title.a-text-normal").html();
                        String url = "http://www.amazon.co.jp/" + e.select("a.a-link-normal.a-text-normal").attr("href");
                        Bitmap image = response.get();
                        Log.d("デバッグ", "値段は"+price);
                        Log.d("デバッグ", "名前は"+name);
                        Log.d("デバッグ", "URLは"+url);
                        Commodity c = new Commodity(price, name, url, image, "0");
                        commodityArrayList.add(c);
                    } catch(InterruptedException e1){

                    } catch (ExecutionException e2){

                    } catch (NullPointerException e3){

                    }
                }
            }
            CustomAdapter customAdapter = new CustomAdapter(context, 0, commodityArrayList);
            listView.setAdapter(customAdapter);
        }
    }

}

class GetImageTask implements Callable<Bitmap>{
    private String url;

    GetImageTask(String url){
        this.url = url;
    }

    @Override
    public Bitmap call(){
        return getImageBitmap(url);
    }


    public Bitmap getImageBitmap(String url){
        Bitmap image = null;
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            image = BitmapFactory.decodeStream(input);
            return image;
        }catch (IOException e2){
            e2.printStackTrace();
            return image;
        }
    }
}

class GetCommodityInfoTask implements Callable<Commodity>{
    private String name;
    GetCommodityInfoTask(String name){
        this.name = name;
    }

    @Override
    public Commodity call(){
        return getCommodityInfo();
    }

    public Commodity getCommodityInfo() {
        Document document = null;
        String amazonUrl = String.format("http://www.amazon.co.jp/s?url=search-alias=aps&field-keywords=%s", name);
        try {
            document = Jsoup.connect(amazonUrl).get();
        }catch (IOException e) {
            e.printStackTrace();
        }
        Element e = document.select("div.sx-table-item").first();
        String price = e.select("span.a-size-small.a-color-price.a-text-bold").html();
        String name = e.select("h5.a-size-base.a-color-base.sx-title a-text-normal").select("strong").html();
        String url = "http://www.amazon.co.jp/"+e.select("a.a-spacing-none.a-link-normal.sx-table-product.aw-search-results").attr("href");
        Commodity c = new Commodity(price, name, url,null, "0");
        return c;
    }
}

