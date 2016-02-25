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
import org.jdeferred.DeferredFutureTask;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.DonePipe;
import org.jdeferred.android.AndroidDeferredManager;

import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.android.DeferredAsyncTask;
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
    private AndroidDeferredManager mAdm;
    private Elements yodobashiCommoditiesHtml;


    private Promise<Bitmap, Throwable, Void> getImageFromUrl(final String imageUrlString) {
        return mAdm.when(new DeferredAsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackgroundSafe(final Void... params) throws Exception {
                URL imageUrl = new URL(imageUrlString);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(input);
                return image;
            }
        });
    }

    private Promise<ArrayList, Throwable, Void> getYodobashiCommodityArray(final Elements elements){
        return mAdm.when(new DeferredAsyncTask<Void, Void, ArrayList>() {
            ArrayList<Commodity> commodityArrayList = new ArrayList<>();
            @Override
            protected ArrayList doInBackgroundSafe(final Void... params) throws Exception {
                for(final Element e: elements) {
                    String price = e.select("strong.red").html();
                    final String name = e.select("div.fs14").select("strong").html();
                    String url = "http://www.yodobashi.com/" + e.attr("href");
                    String point = e.select("strong.orange.ml10").html();
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    GetImageTask task = new GetImageTask(e.select("img").attr("src"));
                    Future<Bitmap> response = executorService.submit(task);
                    Bitmap image = response.get();
                    commodityArrayList.add(new Commodity(price, name, url, image, point));
                }
                return commodityArrayList;
            }
        });
    }

    DownloadTask(String urlString, ListView listView, String mode, Context context){
        super();
        this.urlString = urlString;
        this.listView = listView;
        this.mode = mode;
        this.context = context;
    }

    @Override
    protected Elements doInBackground(String... params) {
        Elements commodities = null;
        try {
            document = Jsoup.connect(urlString).get();
            if (mode == "yodobashi") {
                commodities = document.select("a.productListPostTag.clicklog.cl-schRlt");
            }else if(mode == "amazon"){
                commodities = document.select("a.a-spacing-none.a-link-normal.sx-table-product.aw-search-results");
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
            if(mode == "yodobashi") {
                mAdm.when(getYodobashiCommodityArray(result)).then(new DoneCallback<ArrayList>() {
                    @Override
                    public void onDone(ArrayList result) {
                        CustomAdapter customAdapter = new CustomAdapter(context, 0, result);
                        listView.setAdapter(customAdapter);
                    }
                });

            }

            if (mode == "yodobashi") {

            }
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
        Commodity c = new Commodity(price, name, url);
        return c;
    }
}

