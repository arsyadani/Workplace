package com.example.arsyadani.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Activity_Favorit extends AppCompatActivity {
    private ListView lv_favorit;
    private ArrayList arrFavorit;
    private Adapter_Tempat adapterTempat;
    private DatabaseReference refTempat, refUser;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorit);

        setTitle("Favorit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_favorit = (ListView) findViewById(R.id.lv_favorit);
        arrFavorit = new ArrayList();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        refUser = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("favorit");
        refTempat = FirebaseDatabase.getInstance().getReference().child("tempat");

        refUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String favorit = dataSnapshot.getValue(String.class);

                if (favorit != null && !favorit.equalsIgnoreCase("")) {
                    refTempat.child(favorit).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Tempat tempat = dataSnapshot.getValue(Tempat.class);
                            tempat.setKey(dataSnapshot.getKey());

                            if (tempat != null) {
                                arrFavorit.add(tempat);
                                adapterTempat = new Adapter_Tempat(Activity_Favorit.this, arrFavorit, uid);

                                lv_favorit.setAdapter(adapterTempat);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == 16908332) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
