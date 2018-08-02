package com.example.arsyadani.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Arsyadani on 01-Jun-18.
 */

public class Adapter_Multiple_Image extends ArrayAdapter<MultipleImage> {
    private Context context;
    private ArrayList<MultipleImage> data;

    public Adapter_Multiple_Image(Context context, ArrayList<MultipleImage> data) {
        super(context, R.layout.row_multiple_image, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public MultipleImage getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null ) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_multiple_image, parent, false);
        }

        ImageView iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
        TextView tv_nama = (TextView) convertView.findViewById(R.id.tv_nama);

        MultipleImage multipleImage = getItem(position);

        Glide.with(context.getApplicationContext())
                .load(multipleImage.getLinkFoto())
                .into(iv_image);

        tv_nama.setText(multipleImage.getNamaFoto());

        return convertView;
    }
}
