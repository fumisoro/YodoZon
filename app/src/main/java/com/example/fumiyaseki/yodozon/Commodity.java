package com.example.fumiyaseki.yodozon;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by fumiyaseki on 2016/02/21.
 */
public class Commodity {
    public String yodobashiPrice;
    public String name;
    public String url;
    public Bitmap image;
    public int yodobashiPriceInt;
    public String point;
    public int pointInt;
    public String amazonPrice;

    Commodity(String yodobashiPrice, String amazonPrice, String name, String url, Bitmap image, String point){
        Commodity c = new Commodity(yodobashiPrice, name, url, image, point);
        c.amazonPrice = amazonPrice;
    }

    Commodity(String yodobashiPrice, String name, String url, Bitmap image, String point){
        this.yodobashiPrice = yodobashiPrice;
        this.name = name;
        this.url = url;
        this.image = image;
        this.yodobashiPriceInt = convertInt(yodobashiPrice);
        this.point = point;
        this.pointInt = convertInt(point);
    }

    Commodity(String yodobashiPrice, String name, String url){
        this.yodobashiPrice = yodobashiPrice;
        this.name = name;
        this.url = url;
        this.image = null;
        this.yodobashiPriceInt = convertInt(yodobashiPrice);
        this.point = null;
        this.pointInt = 0;
    }

    int convertInt(String text){
        String regex = "\\D";
        text = text.replaceAll(regex, "");
        return Integer.parseInt(text);
    }
}

class CustomAdapter extends ArrayAdapter<Commodity>{
    private LayoutInflater inflater;

    public CustomAdapter(Context context, int textViewResourceId, List<Commodity> objs){
        super(context, textViewResourceId, objs);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Commodity item = getItem(position);

        if(null == convertView){
            convertView = inflater.inflate(R.layout.list, null);
        }
        ImageView imageView;
        imageView = (ImageView)convertView.findViewById(R.id.imageView);
        imageView.setImageBitmap(item.image);

        TextView nameTextView;
        nameTextView = (TextView)convertView.findViewById(R.id.nameTextView);
        nameTextView.setText(item.name);

        TextView yodobashiPriceTextView;
        yodobashiPriceTextView = (TextView)convertView.findViewById(R.id.yodobashiPriceTextView);
        yodobashiPriceTextView.setText(item.yodobashiPrice);

        TextView pointTextView;
        pointTextView = (TextView)convertView.findViewById(R.id.pointTextView);
        pointTextView.setText(item.point);

        TextView amazonPriceTextView;
        amazonPriceTextView = (TextView)convertView.findViewById(R.id.amazonPriceTextView);
        amazonPriceTextView.setText(item.amazonPrice);

        return convertView;
    }
}