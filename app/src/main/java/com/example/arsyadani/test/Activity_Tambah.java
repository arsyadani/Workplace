package com.example.arsyadani.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class Activity_Tambah extends AppCompatActivity {
    private EditText et_nama_tempat, et_alamat_tempat, et_telp_tempat, et_jam, et_lati, et_longi, et_order, et_kapasitas,
            et_prosedurSewa, et_waktuRamai, et_term;
    private Button btn_simpan, btn_pilih_gambar, btn_fasilitas, btn_foto_360, btn_pilih_tempat;
    private ImageView iv_gambar;
    private DatabaseReference mDatabase;
    private RadioGroup rg_radio;
    private RadioButton rb_tipe, rb_cafe;
    private final int READ_EXTERNAL_STORAGE = 0;
    private CheckBox cb_senin, cb_selasa, cb_rabu, cb_kamis, cb_jumat, cb_sabtu, cb_minggu;
    private TextView tv_senin, tv_selasa, tv_rabu, tv_kamis, tv_jumat, tv_sabtu, tv_minggu;

    // Upload Gambar
    private Uri pathThumbnail, pathGambar360;
    private final int PICK_IMAGE_REQUEST = 2;
    private int pilihButton;
    private String uriThumb;

    // Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    // Multiple Image
    private ArrayList<MultipleImage> gambarPanorama;
    private ArrayList<Uri> uriArrayList;
    private ListView lv_multipleImage;
    private Adapter_Multiple_Image adapter_multiple_image;
    private int count;
    private StorageReference ref360;

    private int PLACE_PICKER_REQUEST = 1;
    private String text, senin, selasa, rabu, kamis, jumat, sabtu, minggu, tempMulai, tempSelesai;

    private ArrayList<String> tempJam = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambah);

        setTitle("Tambah Tempat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pilihButton = 0;

        // Detail Tempat
        et_nama_tempat = (EditText) findViewById(R.id.et_nama_tempat);
        et_alamat_tempat = (EditText) findViewById(R.id.et_alamat_tempat);
        et_telp_tempat = (EditText) findViewById(R.id.et_telp_tempat);
//        et_jam = (EditText) findViewById(R.id.et_jam);
        et_lati = (EditText) findViewById(R.id.et_lati);
        et_longi = (EditText) findViewById(R.id.et_longi);

        rg_radio = (RadioGroup) findViewById(R.id.rg_radio_tempat);
        rb_cafe = (RadioButton) findViewById(R.id.rb_cafe);
        rb_cafe.setChecked(true);

        // Info tambahan
        et_order = (EditText) findViewById(R.id.et_order);
        et_kapasitas = (EditText) findViewById(R.id.et_kapasitas);
        et_prosedurSewa = (EditText) findViewById(R.id.et_prosedurSewa);
        et_waktuRamai = (EditText) findViewById(R.id.et_waktuRamai);
        et_term = (EditText) findViewById(R.id.et_term);

        // Foto
        btn_pilih_gambar = (Button) findViewById(R.id.btn_pilih_gambar);
        btn_simpan = (Button) findViewById(R.id.btn_simpan);
        btn_fasilitas = (Button) findViewById(R.id.btn_fasilitas);
        btn_foto_360 = (Button) findViewById(R.id.btn_foto_360);
        btn_pilih_tempat = (Button) findViewById(R.id.btn_pilih_tempat);

        iv_gambar = (ImageView) findViewById(R.id.iv_gambar);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("tempat");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        cb_senin = (CheckBox) findViewById(R.id.cb_senin);
        cb_selasa = (CheckBox) findViewById(R.id.cb_selasa);
        cb_rabu = (CheckBox) findViewById(R.id.cb_rabu);
        cb_kamis = (CheckBox) findViewById(R.id.cb_kamis);
        cb_jumat = (CheckBox) findViewById(R.id.cb_jumat);
        cb_sabtu = (CheckBox) findViewById(R.id.cb_sabtu);
        cb_minggu = (CheckBox) findViewById(R.id.cb_minggu);

        tv_senin = (TextView) findViewById(R.id.tv_senin);
        tv_selasa = (TextView) findViewById(R.id.tv_selasa);
        tv_rabu = (TextView) findViewById(R.id.tv_rabu);
        tv_kamis = (TextView) findViewById(R.id.tv_kamis);
        tv_jumat = (TextView) findViewById(R.id.tv_jumat);
        tv_sabtu = (TextView) findViewById(R.id.tv_sabtu);
        tv_minggu = (TextView) findViewById(R.id.tv_minggu);

        cb_senin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    dialogJam(1);
                }
            }
        });

        cb_selasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    dialogJam(2);
                }
            }
        });

        cb_rabu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    dialogJam(3);
                }
            }
        });

        cb_kamis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    dialogJam(4);
                }
            }
        });

        cb_jumat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    dialogJam(5);
                }
            }
        });

        cb_sabtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    dialogJam(6);
                }
            }
        });

        cb_minggu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    dialogJam(7);
                }
            }
        });

        gambarPanorama = new ArrayList<MultipleImage>();
        uriArrayList = new ArrayList<Uri>();
        lv_multipleImage = (ListView) findViewById(R.id.lv_multiple_image);
        adapter_multiple_image = new Adapter_Multiple_Image(Activity_Tambah.this, gambarPanorama);

        lv_multipleImage.setAdapter(adapter_multiple_image);

        lv_multipleImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Tambah.this);

                final EditText input = new EditText(Activity_Tambah.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                builder.setView(input);


                builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        text = input.getText().toString();
                        gambarPanorama.get(i).setNamaFoto(text);

                        ((BaseAdapter) lv_multipleImage.getAdapter()).notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpan();
            }
        });

        btn_pilih_gambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihButton = 1;
                pilihGambar();
            }
        });

        btn_foto_360.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihButton = 2;
                pilihGambar();
            }
        });

        btn_pilih_tempat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(Activity_Tambah.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void dialogJam(final int hari) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(Activity_Tambah.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String TempHour = "" + hourOfDay;
                String TempMinute = "" + minute;
                if (String.valueOf(minute).length() == 1) {
                    TempMinute = "0" + minute;
                }
                if (String.valueOf(hourOfDay).length() == 1) {
                    TempHour = "0" + hourOfDay;
                }
                tempMulai = TempHour + ":" + TempMinute;

                final TimePickerDialog timePickerDialog2 = new TimePickerDialog(Activity_Tambah.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String TempHour = "" + hourOfDay;
                        String TempMinute = "" + minute;

                        if (String.valueOf(minute).length() == 1) {
                            TempMinute = "0" + minute;
                        }
                        if (String.valueOf(hourOfDay).length() == 1) {
                            TempHour = "0" + hourOfDay;
                        }
                        tempSelesai = TempHour + ":" + TempMinute;

                        if (hari == 1) {
                            tv_senin.setText("(" + tempMulai + " - " + tempSelesai + ")");
                        }
                        else if (hari == 2) {
                            tv_selasa.setText("(" + tempMulai + " - " + tempSelesai + ")");
                        }
                        else if (hari == 3) {
                            tv_rabu.setText("(" + tempMulai + " - " + tempSelesai + ")");
                        }
                        else if (hari == 4) {
                            tv_kamis.setText("(" + tempMulai + " - " + tempSelesai + ")");
                        }
                        else if (hari == 5) {
                            tv_jumat.setText("(" + tempMulai + " - " + tempSelesai + ")");
                        }
                        else if (hari == 6) {
                            tv_sabtu.setText("(" + tempMulai + " - " + tempSelesai + ")");
                        }
                        else if (hari == 7) {
                            tv_minggu.setText("(" + tempMulai + " - " + tempSelesai + ")");
                        }
                    }
                }, 12, 0, true);
                timePickerDialog2.setTitle("Jam selesai");
                timePickerDialog2.show();
            }
        }, 12, 0, true);
        timePickerDialog.setTitle("Jam buka");
        timePickerDialog.show();
    }

    private void simpan() {
        final String nama = et_nama_tempat.getText().toString();
        final String alamat = et_alamat_tempat.getText().toString();
        final String telp = et_telp_tempat.getText().toString();

        if (cb_senin.isChecked()) {
            tempJam.add(0, tv_senin.getText().toString());
        }
        else {
            tempJam.add(0, "");
        }
        if (cb_selasa.isChecked()) {
            tempJam.add(1, tv_selasa.getText().toString());
        }
        else {
            tempJam.add(1, "");
        }
        if (cb_rabu.isChecked()) {
            tempJam.add(2, tv_rabu.getText().toString());
        }
        else {
            tempJam.add(2, "");
        }
        if (cb_kamis.isChecked()) {
            tempJam.add(3, tv_kamis.getText().toString());
        }
        else {
            tempJam.add(3, "");
        }
        if (cb_jumat.isChecked()) {
            tempJam.add(4, tv_jumat.getText().toString());
        }
        else {
            tempJam.add(4, "");
        }
        if (cb_sabtu.isChecked()) {
            tempJam.add(5, tv_sabtu.getText().toString());
        }
        else {
            tempJam.add(5, "");
        }
        if (cb_minggu.isChecked()) {
            tempJam.add(6, tv_minggu.getText().toString());
        }
        else {
            tempJam.add(6, "");
        }

        final String lati = et_lati.getText().toString();
        final String longi = et_longi.getText().toString();

        final String order = et_order.getText().toString();
        final String kapasitas = et_kapasitas.getText().toString();
        final String prosedurSewa = et_prosedurSewa.getText().toString();
        final String waktuRamai = et_waktuRamai.getText().toString();
        final String term = et_term.getText().toString();

        int selected = rg_radio.getCheckedRadioButtonId();
        rb_tipe = (RadioButton) findViewById(selected);
        final String tipe = rb_tipe.getText().toString();

        if(pathThumbnail != null && gambarPanorama.size() != 0) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading ...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("location/"+ UUID.randomUUID().toString());
            final String key = mDatabase.push().getKey();

            ref.putFile(pathThumbnail).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri uri = taskSnapshot.getDownloadUrl();

                    uriThumb = uri.toString();
                    progressDialog.dismiss();
                    Toast.makeText(Activity_Tambah.this, "Uploaded Thumbnail", Toast.LENGTH_SHORT).show();


                    Tempat saveTempat = new Tempat();
                    saveTempat.setNama(nama);
                    saveTempat.setTipe(tipe);
                    saveTempat.setAlamat(alamat);
                    saveTempat.setTelp(telp);
                    saveTempat.setJam(tempJam);
                    saveTempat.setFoto(uriThumb);
                    saveTempat.setLati(lati);
                    saveTempat.setLongi(longi);
                    saveTempat.setOrder(order);
                    saveTempat.setKapasitas(kapasitas);
                    saveTempat.setProsedurSewa(prosedurSewa);
                    saveTempat.setWaktuRamai(waktuRamai);
                    saveTempat.setTerm(term);
                    mDatabase.child(key).setValue(saveTempat);

                    String namaBaru = "";

                    for (int i = 0; i < gambarPanorama.size(); i++) {
                        System.out.println("count for : " + count);
                        count = i;
                        gambarPanorama.get(i).setLinkFoto(null);
                        ref360 = storageReference.child("location/panorama/" + nama + "/" + nama + " " + (count + 1) + "-" +
                                gambarPanorama.get(i).getNamaFoto());
                        ref360.putFile(uriArrayList.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri getUri = taskSnapshot.getDownloadUrl();
                                final String nama = taskSnapshot.getMetadata().getName();

                                gambarPanorama.get(count).setLinkFoto(getUri.toString());
                                mDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Tempat tempat = dataSnapshot.getValue(Tempat.class);

                                        if (tempat != null) {
                                            if (tempat.getFotoPanorama() == null) {
                                                System.out.println("Masuk null");
                                                MultipleImage multipleImage = new MultipleImage();
                                                multipleImage.setLinkFoto(gambarPanorama.get(count).getLinkFoto());
                                                multipleImage.setNamaFoto(nama);

                                                ArrayList<MultipleImage> multipleImageArrayList = new ArrayList<MultipleImage>();
                                                multipleImageArrayList.add(multipleImage);
                                                tempat.setFotoPanorama(multipleImageArrayList);

                                                mDatabase.child(key).setValue(tempat);
                                            }
                                            else if (tempat.getFotoPanorama() != null) {
                                                System.out.println("Masuk Gak null");
                                                ArrayList<MultipleImage> multipleImageArrayList = new ArrayList<MultipleImage>();
                                                multipleImageArrayList = tempat.getFotoPanorama();

                                                MultipleImage multipleImage = new MultipleImage();
                                                multipleImage.setLinkFoto(gambarPanorama.get(count).getLinkFoto());
                                                multipleImage.setNamaFoto(nama);

                                                multipleImageArrayList.add(multipleImage);
                                                tempat.setFotoPanorama(multipleImageArrayList);
                                                mDatabase.child(key).setValue(tempat);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }

//                    mDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Tempat tempat = dataSnapshot.getValue(Tempat.class);
//
//                            if (tempat != null) {
//                                ArrayList<MultipleImage> arrayList = tempat.getFotoPanorama();
//                                int size = arrayList.size();
//
//                                for (int i = 0; i < size; i++) {
//                                    arrayList.get(i).setNamaFoto(gambarPanorama.get(i).getNamaFoto());
//                                }
//
//                                tempat.setFotoPanorama(arrayList);
//                                mDatabase.child(key).setValue(tempat);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });

                    Intent main = new Intent(Activity_Tambah.this, Activity_Main_Admin.class);
                    startActivity(main);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Activity_Tambah.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                            .getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                }
            });

            progressDialog.setTitle("Upload Foto 360...");
            progressDialog.show();

        }
    }

    private void pilihGambar() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != getPackageManager().PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Call for permission", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
            }
        }
        else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            String title = "";
            if (pilihButton == 1) {
                title = "Pilih Gambar Thumbnail";
            }
            else if (pilihButton == 2) {
                title = "Pilih Gambar 360";
            }
            startActivityForResult(Intent.createChooser(intent, title), PICK_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null && pilihButton == 1) {
            pathThumbnail = data.getData();
            iv_gambar.setImageURI(pathThumbnail);

            System.out.println("path = " + pathThumbnail);
            pilihButton = 0;
        }
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null && pilihButton == 2) {
            pathGambar360 = data.getData();

            // Temp Gambar Panorama ke ArrayList
            MultipleImage multipleImage = new MultipleImage();
            multipleImage.setLinkFoto(pathGambar360.toString());
            multipleImage.setNamaFoto("Foto " + (gambarPanorama.size() + 1));
            gambarPanorama.add(multipleImage);

            // Temp uri
            uriArrayList.add(pathGambar360);

            ((BaseAdapter) lv_multipleImage.getAdapter()).notifyDataSetChanged();

            justifyListViewHeightBasedOnChildren(lv_multipleImage);

            System.out.println("path = " + pathGambar360);
            pilihButton = 0;
        }

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                et_lati.setText("" + place.getLatLng().latitude);
                et_longi.setText("" + place.getLatLng().longitude);

                et_nama_tempat.setText(place.getName());
                et_alamat_tempat.setText(place.getAddress());
                et_telp_tempat.setText(place.getPhoneNumber());


            }
        }
    }

    public void justifyListViewHeightBasedOnChildren (ListView listView) {
        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        System.out.println("height : " + totalHeight);
        par.height = (totalHeight+60) + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
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
