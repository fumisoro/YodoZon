package com.example.fumiyaseki.yodozon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by fumiyaseki on 2016/02/22.
 */


class DownloadTask extends AsyncTask<String, Integer, Elements> {

    private String urlString;
    private Document document;
    private  ListView listView;
    private String mode;

    DownloadTask(String urlString, ListView listView, String mode, ImageView imageView){
        super();
        this.urlString = urlString;
        this.listView = listView;
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
                    list.add(e.select("a.productListPostTag.clicklog.cl-schRlt").toString());
//                    Log.d("デバッグ", "http://www.yodobashi.com/" + e.attr("href"));
                    Bitmap bitmap = getImageBitmap(e.select("img").attr("src").toString());



                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(listView.getContext(), R.layout.list, list);
            Log.d("デバッグ", list.toString());
            if (mode == "yodobashi") {
                listView.setAdapter(adapter);
            }
        }
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
