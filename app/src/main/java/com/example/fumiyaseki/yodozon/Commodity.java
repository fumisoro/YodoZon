package com.example.fumiyaseki.yodozon;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
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
    public int price;
    public String name;
    public String url;
    public Bitmap image;

    Commodity(int price, String name, String url, Bitmap image){
        this.price = price;
        this.name = name;
        this.url = url;
        this.image = image;
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
        Commodity item = (Commodity)getItem(position);

        if(null == convertView){
            convertView = inflater.inflate(R.layout.list, null);
        }
        ImageView imageView;
        imageView = (ImageView)convertView.findViewById(R.id.imageView);
        imageView.setImageBitmap(item.image);

        TextView nameTextView;
        nameTextView = (TextView)convertView.findViewById(R.id.nameTextView);
        nameTextView.setText(item.name);

        return convertView;
    }
}