package com.example.arsyadani.test;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Arsyadani on 13-Apr-18.
 */

public class Adapter_Tempat extends ArrayAdapter<Tempat> {
    private Context context;
    private ArrayList<Tempat> data;
    private String uid;
    private double rating, count;

    public Adapter_Tempat(Context context, ArrayList<Tempat> data, String uid) {
        super(context, R.layout.row_tempat, data);
        this.context = context;
        this.data = data;
        this.uid = uid;
    }

    @Override
    public Tempat getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null ) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_tempat, parent, false);
        }

        ImageView iv_foto = (ImageView) convertView.findViewById(R.id.iv_foto);
        final TextView tv_nama = (TextView) convertView.findViewById(R.id.tv_nama);
        final TextView tv_jam = (TextView) convertView.findViewById(R.id.tv_jam);

        final Tempat tempat = getItem(position);

        Glide.with(getContext())
                .load(tempat.getFoto())
                .into(iv_foto);

        tv_nama.setText(tempat.getNama());
        tv_jam.setText(tempat.getAlamat());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uid != null && uid.equalsIgnoreCase("yuYiVCiPVve7BOOe5ZZC2sQRcCj1")) {
//                    Toast.makeText(context, tempat.getKey(), Toast.LENGTH_SHORT).show();
                    Intent detailTempat = new Intent(getContext(), Activity_Edit_Hapus.class);
                    detailTempat.putExtra("key", tempat.getKey());
                    context.startActivity(detailTempat);
                }
                else {
//                    Toast.makeText(context, tempat.getKey(), Toast.LENGTH_SHORT).show();
                    Intent detailTempat = new Intent(getContext(), Activity_Detail_Tempat.class);
                    detailTempat.putExtra("key", tempat.getKey());
                    detailTempat.putExtra("uid", uid);
                    detailTempat.putExtra("nama", tempat.getNama());
                    context.startActivity(detailTempat);
                }
            }
        });

        return convertView;
    }
}
