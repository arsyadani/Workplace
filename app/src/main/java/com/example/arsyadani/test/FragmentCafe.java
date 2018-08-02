package com.example.arsyadani.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import javax.security.auth.callback.Callback;

public class FragmentCafe extends Fragment {
    private ListView lv_list;
    private ArrayList<Tempat> arrTempat;
    private Adapter_Tempat adapterTempat;
    private int index;
    private DatabaseReference ref;
    private String uid;
    private SearchView sv_search;
    private boolean check;

    public FragmentCafe() {

    }

    @SuppressLint("ValidFragment")
    public FragmentCafe(String uid) {
        this.uid = uid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cafe, container, false);
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        lv_list = (ListView) view.findViewById(R.id.lv_list);
        lv_list.setFocusable(false);
        arrTempat = new ArrayList<>();

        // Cari tempat
        sv_search = (SearchView) view.findViewById(R.id.sv_search_cafe);
//        sv_search.setIconified(false);
        ref = FirebaseDatabase.getInstance().getReference().child("tempat");
        check = false;

        sv_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String namaTempat) {
                ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Tempat tempat = dataSnapshot.getValue(Tempat.class);
                        tempat.setKey(dataSnapshot.getKey());

                        if (tempat != null) {
                            if (tempat.getTipe().equalsIgnoreCase("cafe")) {
                                if (tempat.getNama().equalsIgnoreCase(namaTempat)) {
                                    check = true;

                                    Intent cariTempat;

                                    if (uid.equalsIgnoreCase("yuYiVCiPVve7BOOe5ZZC2sQRcCj1")) {
                                        cariTempat = new Intent(getActivity(), Activity_Edit_Hapus.class);
                                        cariTempat.putExtra("uid", uid);
                                        cariTempat.putExtra("key", tempat.getKey());
                                    }
                                    else {
                                        cariTempat = new Intent(getActivity(), Activity_Detail_Tempat.class);
                                        cariTempat.putExtra("uid", uid);
                                        cariTempat.putExtra("key", tempat.getKey());
                                        cariTempat.putExtra("nama", tempat.getNama());
                                    }

                                    startActivity(cariTempat);
                                }

                                if (!check) {
                                    Toast.makeText(getContext(), "Tempat tidak ditemukan", Toast.LENGTH_SHORT).show();
                                }
                            }
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

                check = false;

                return check;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // Tampilkan data
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Tempat tempat = dataSnapshot.getValue(Tempat.class);
                if (tempat.getTipe() != null && tempat.getTipe().equalsIgnoreCase("cafe")) {
                    if (tempat.getFoto() != null && !tempat.getFoto().equals("")) {
                        tempat.setKey(dataSnapshot.getKey());

                        arrTempat.add(tempat);
                        adapterTempat = new Adapter_Tempat(getActivity(), arrTempat, uid);
                        lv_list.setAdapter(adapterTempat);
                    }
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



        return view;
    }
}
