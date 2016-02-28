package com.example.fumiyaseki.yodozon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by fumiyaseki on 2016/02/22.
 */


class DownloadTask extends AsyncTask<String, Integer, CustomAdapter> implements OnCancelListener {

    private String urlString;
    private Document document;
    private ListView listView;
    private String mode;
    private Context context;
    private ProgressDialog dialog;

    DownloadTask(String urlString, ListView listView, String mode, Context context){
        super();
        this.urlString = urlString;
        this.listView = listView;
        this.mode = mode;
        this.context = context;
    }

    @Override
    protected void onPreExecute(){
        dialog = new ProgressDialog(context);
        dialog.setTitle("Please wait");
        dialog.setMessage("Loading data...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(this);
        dialog.setMax(100);
        dialog.setProgress(0);
        dialog.show();
    }

    @Override
    protected CustomAdapter doInBackground(String... params) {
        Elements commodities = null;
        try {
            publishProgress(10);
            document = Jsoup.connect(urlString).get();
            publishProgress(20);
            if (mode == "yodobashi") {
                commodities = document.select("a.productListPostTag.clicklog.cl-schRlt");
            }else if(mode == "amazon"){
                commodities = document.select("li.s-result-item.celwidget ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (commodities == null) {

        }
        else {
            ArrayList<Commodity> commodityArrayList = new ArrayList<>();

            for(Element e: commodities){
                publishProgress(60);
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
                        Commodity c = new Commodity(price, name, url, image, "0");
                        commodityArrayList.add(c);
                    } catch(InterruptedException e1){

                    } catch (ExecutionException e2){

                    } catch (NullPointerException e3){

                    }
                }
            }
            publishProgress(100);
            CustomAdapter customAdapter = new CustomAdapter(context, 0, commodityArrayList);
            dialog.dismiss();
            return customAdapter;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.d("デバッグ", "onProgressUpdate - " + values[0]);
        dialog.setProgress(values[0]);
    }

    @Override
    protected void onCancelled() {
        Log.d("デバッグ", "onCancelled");
        dialog.dismiss();
    }

    @Override public void onCancel(DialogInterface dialog) {
        Log.d("デバッグ", "Dialog onCancell... calling cancel(true)");
        this.cancel(true);
    }



    @Override
    protected void onPostExecute(CustomAdapter customAdapter) {
        listView.setAdapter(customAdapter);
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
