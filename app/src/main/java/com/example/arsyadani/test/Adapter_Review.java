package com.example.arsyadani.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Arsyadani on 20-Apr-18.
 */

public class Adapter_Review extends ArrayAdapter<Review> {
    private Context context;
    private ArrayList<Review> data;

    public Adapter_Review(Context context, ArrayList<Review> data) {
        super(context, R.layout.row_review, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public Review getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null ) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_review, parent, false);
        }

        final ImageView iv_foto_review = (ImageView) convertView.findViewById(R.id.profile_picture);
        final TextView tv_username = (TextView) convertView.findViewById(R.id.tv_username);
        final TextView tv_rating_review = (TextView) convertView.findViewById(R.id.tv_rating_review);
        final TextView tv_review = (TextView) convertView.findViewById(R.id.tv_review);
        final TextView tv_waktu = (TextView) convertView.findViewById(R.id.tv_waktu);

        final Review review = getItem(position); //dapatkan data per record

        System.out.println(review);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(review.getIdPengguna());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                try {
                    if (user != null) {
                        if (user.getFoto() != null) {
                            Glide.with(getContext()).load(user.getFoto()).into(iv_foto_review);
                        }
                        if (user.getFoto() == null || user.getFoto() == "") {
                            Glide.with(getContext()).load(R.drawable.icon_profile_black).into(iv_foto_review);
                        }
                        if (user.getUsername() != null) {
                            tv_username.setText(user.getUsername());
                        }
                    }
                }
                catch (IllegalArgumentException e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tv_rating_review.setText("" + review.getRating());
        tv_review.setText(review.getReview());
        tv_waktu.setText(review.getWaktu());

        return convertView;
    }
}
