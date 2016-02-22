package com.example.fumiyaseki.yodozon;


import android.media.Image;

/**
 * Created by fumiyaseki on 2016/02/21.
 */
public class Commodity {
    private int price;
    private String name;
    private String url;
    private Image image;

    Commodity(int price, String name, String url, Image image){
        this.price = price;
        this.name = name;
        this.url = url;
        this.image = image;
    }
}
