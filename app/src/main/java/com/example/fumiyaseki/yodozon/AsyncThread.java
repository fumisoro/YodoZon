package com.example.fumiyaseki.yodozon;

import android.content.Context;
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
    private  ListView listView;
    private String mode;
    private LayoutInflater inflater;
    private Context context;

    DownloadTask(String urlString, ListView listView, String mode, LayoutInflater inflater, Context context){
        super();
        this.urlString = urlString;
        this.listView = listView;
        this.mode = mode;
        this.inflater = inflater;
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
            ArrayList<Commodity> commodityArrayList = new ArrayList<>();

            for(Element e: result){

                if(mode == "yodobashi") {
                    ExecutorService executorService = Executors.newFixedThreadPool(4);
                    GetImageTask getImageTask = new GetImageTask(e.select("img").attr("src"));
                    Future<Bitmap> response = executorService.submit(getImageTask);
                    String priceText = e.select("strong.red").html();
                    String regex = "\\D";
                    priceText = priceText.replaceAll(regex, "");
                    Log.d("デバッグ", priceText);
                    try {
                        Commodity c = new Commodity(e.select("strong.red").html(), e.select("div.fs14").select("strong").html(), "http://www.yodobashi.com/" + e.attr("href"), response.get(), e.select("strong.orange.ml10").html());
                        Log.d("デバッグ", e.select("strong.red").toString());
                        commodityArrayList.add(c);
                    }catch (InterruptedException e1){

                    }catch (ExecutionException e2){

                    }
                }
            }

            if (mode == "yodobashi") {
                CustomAdapter customAdapter = new CustomAdapter(context, 0, commodityArrayList);
                listView.setAdapter(customAdapter);
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

