package com.example.arsyadani.test;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Arsyadani on 20-Apr-18.
 */

public class Adapter_Fasilitas extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> data;

    public Adapter_Fasilitas(Context context, ArrayList<String> data) {
        super(context, R.layout.row_fasilitas, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null ) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_fasilitas, parent, false);
        }

        final TextView tv_nama_fasilitas = (TextView) convertView.findViewById(R.id.tv_nama_fasilitas);

        final String fasilitas = getItem(position); //dapatkan data per record

        System.out.println("masuk" + fasilitas);

        tv_nama_fasilitas.setText(fasilitas);

        return convertView;
    }
}
