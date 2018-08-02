package com.example.arsyadani.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Arsyadani on 17-Apr-18.
 */

public class Activity_Edit_Hapus extends AppCompatActivity {
    private EditText et_nama_tempat, et_alamat_tempat, et_telp_tempat, et_jam, et_lati, et_longi, et_order, et_kapasitas,
            et_prosedurSewa, et_waktuRamai, et_term;
    private Button btn_simpan, btn_pilih_gambar, btn_fasilitas, btn_foto_360, btn_pilih_tempat;
    private ImageView iv_gambar;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private RadioGroup rg_radio;
    private RadioButton rb_cafe, rb_coworking, rb_tipe;
    private Tempat tempat;
    private final String Tag = "Activity_Edit_Hapus";
    private CheckBox cb_senin, cb_selasa, cb_rabu, cb_kamis, cb_jumat, cb_sabtu, cb_minggu;
    private TextView tv_senin, tv_selasa, tv_rabu, tv_kamis, tv_jumat, tv_sabtu, tv_minggu;

    // Upload Gambar
    private Uri pathThumbnail, pathGambar360;
    private final int PICK_IMAGE_REQUEST = 2;
    private final int READ_EXTERNAL_STORAGE = 0;
    private int pilihButton;
    private String uriThumb, uriGambar360;

    private String key, keyTempat, tempMulai, tempSelesai;

    // List fasilitas
    String [] listFasilitas;
    boolean [] checkedFasilitas;
    ArrayList<String> arrFasilitas = new ArrayList<>();

    // Multiple Image
    private ArrayList<MultipleImage> gambarPanorama;
    private ArrayList<Uri> uriArrayList;
    private ListView lv_multipleImage;
    private Adapter_Multiple_Image adapter_multiple_image;
    private int count;
    private StorageReference ref360;

    private int PLACE_PICKER_REQUEST = 1;

    private ArrayList<String> tempJam = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_hapus);
        setTitle("Edit Tempat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        pilihButton = 0;

        // Detail Tempat
        et_nama_tempat = (EditText) findViewById(R.id.et_nama_tempat);
        et_alamat_tempat = (EditText) findViewById(R.id.et_alamat_tempat);
        et_telp_tempat = (EditText) findViewById(R.id.et_telp_tempat);
        et_lati = (EditText) findViewById(R.id.et_lati);
        et_longi = (EditText) findViewById(R.id.et_longi);

        rg_radio = (RadioGroup) findViewById(R.id.rg_radio_tempat);
        rb_cafe = (RadioButton) findViewById(R.id.rb_cafe);
        rb_coworking = (RadioButton) findViewById(R.id.rb_coworking);

        // Info Tambahan
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

        gambarPanorama = new ArrayList<MultipleImage>();
        uriArrayList = new ArrayList<Uri>();
        lv_multipleImage = (ListView) findViewById(R.id.lv_multiple_image);

        arrFasilitas = new ArrayList<String>();

        iv_gambar = (ImageView) findViewById(R.id.iv_gambar);
        key = getIntent().getStringExtra("key");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("tempat").child(key);

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

        load();

        lv_multipleImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Edit_Hapus.this);

                final EditText input = new EditText(Activity_Edit_Hapus.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                builder.setView(input);

                builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String text = input.getText().toString();
                        gambarPanorama.get(i).setNamaFoto(text);

                        ((BaseAdapter) lv_multipleImage.getAdapter()).notifyDataSetChanged();
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Tempat tempat = dataSnapshot.getValue(Tempat.class);

                                if (tempat != null) {
                                    ArrayList<MultipleImage> arrayList = tempat.getFotoPanorama();
                                    arrayList.get(i).setNamaFoto(tempat.getNama() + " " + (i+1) + "-" + text);
                                    tempat.setFotoPanorama(arrayList);

                                    mDatabase.setValue(tempat);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
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

        btn_pilih_tempat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(Activity_Edit_Hapus.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_fasilitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Edit_Hapus.this);
                builder.setTitle("Pilih Fasilitas");

                mDatabase.child("fasilitas").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        arrFasilitas = (ArrayList<String>) dataSnapshot.getValue();

                        if (arrFasilitas != null) {
                            for (int i = 0; i < arrFasilitas.size(); i++) {
                                for (int j = 0; j < listFasilitas.length; j++) {
                                    if (arrFasilitas.get(i).toString().equalsIgnoreCase(listFasilitas[j])) {
                                        System.out.println("Masuk bos");
                                        checkedFasilitas[j] = true;
                                        break;
                                    }
                                }
                            }
                        }
                        else {
                            arrFasilitas = new ArrayList<String>();
                        }

                        System.out.println("Array List = " + arrFasilitas);
                        System.out.println("Boolean checked = " + Arrays.toString(checkedFasilitas));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                builder.setMultiChoiceItems(listFasilitas, checkedFasilitas, new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            arrFasilitas.add(listFasilitas[position]);
                        }
                        else {
                            arrFasilitas.remove(listFasilitas[position]);
                        }
                    }
                });

                builder.setCancelable(false);
                builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        System.out.println(arrFasilitas);
                        mDatabase.child("fasilitas").setValue(arrFasilitas);
                    }
                });

                builder.setNegativeButton("Batalkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        rg_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selected = rg_radio.getCheckedRadioButtonId();
                rb_tipe = (RadioButton) findViewById(selected);
                String tipe = rb_tipe.getText().toString();

                if (tipe.equalsIgnoreCase("cafe")) {
                    listFasilitas = getResources().getStringArray(R.array.fasilitasCafe);
                }
                else if (tipe.equalsIgnoreCase("coworking")) {
                    listFasilitas = getResources().getStringArray(R.array.fasilitasCoworking);
                }

                checkedFasilitas = new boolean [listFasilitas.length];
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpanEdit();
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
    }

    private void dialogJam(final int hari) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(Activity_Edit_Hapus.this, new TimePickerDialog.OnTimeSetListener() {
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

                final TimePickerDialog timePickerDialog2 = new TimePickerDialog(Activity_Edit_Hapus.this, new TimePickerDialog.OnTimeSetListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_edit_hapus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.icon_hapus) {
            hapus();
        }
        else if (id == 16908332) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void load() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tempat = dataSnapshot.getValue(Tempat.class);
                keyTempat = dataSnapshot.getKey();

                if(tempat != null) {
                    et_nama_tempat.setText(tempat.getNama());
                    et_alamat_tempat.setText(tempat.getAlamat());
                    et_telp_tempat.setText(tempat.getTelp());

                    tempJam = tempat.getJam();
                    if (!tempJam.get(0).equalsIgnoreCase("")) {
                        cb_senin.setChecked(true);
                        tv_senin.setText(tempJam.get(0));
                    }
                    if (!tempJam.get(1).equalsIgnoreCase("")) {
                        cb_selasa.setChecked(true);
                        tv_selasa.setText(tempJam.get(1));
                    }
                    if (!tempJam.get(2).equalsIgnoreCase("")) {
                        cb_rabu.setChecked(true);
                        tv_rabu.setText(tempJam.get(2));
                    }
                    if (!tempJam.get(3).equalsIgnoreCase("")) {
                        cb_kamis.setChecked(true);
                        tv_kamis.setText(tempJam.get(3));
                    }
                    if (!tempJam.get(4).equalsIgnoreCase("")) {
                        cb_jumat.setChecked(true);
                        tv_jumat.setText(tempJam.get(4));
                    }
                    if (!tempJam.get(5).equalsIgnoreCase("")) {
                        cb_sabtu.setChecked(true);
                        tv_sabtu.setText(tempJam.get(5));
                    }
                    if (!tempJam.get(6).equalsIgnoreCase("")) {
                        cb_minggu.setChecked(true);
                        tv_minggu.setText(tempJam.get(6));
                    }

                    et_lati.setText(tempat.getLati());
                    et_longi.setText(tempat.getLongi());
                    if (tempat.getTipe().equalsIgnoreCase("cafe")) {
                        rb_cafe.setChecked(true);
                    }
                    else if(tempat.getTipe().equalsIgnoreCase("coworking")) {
                        rb_coworking.setChecked(true);
                    }

                    et_order.setText(tempat.getOrder());
                    et_kapasitas.setText(tempat.getKapasitas());
                    et_prosedurSewa.setText(tempat.getProsedurSewa());
                    et_waktuRamai.setText(tempat.getWaktuRamai());
                    et_term.setText(tempat.getTerm());

                    gambarPanorama = tempat.getFotoPanorama();
                    System.out.println("size : " + gambarPanorama.size());
                    for (int i = 0; i < gambarPanorama.size(); i++) {
                        char[] charNama = gambarPanorama.get(i).getNamaFoto().toCharArray();
                        int charCount = 0;

                        for (int a = 0; a < charNama.length; a++) {
                            if(charNama[a] == '-') {
                                charCount = a;
                                break;
                            }
                        }

                        String namaBaru = String.valueOf(charNama);
                        String tempNama = namaBaru.substring(charCount+1, charNama.length);
                        gambarPanorama.get(i).setNamaFoto(tempNama);
                    }
                    adapter_multiple_image = new Adapter_Multiple_Image(Activity_Edit_Hapus.this, gambarPanorama);

                    lv_multipleImage.setAdapter(adapter_multiple_image);
                    justifyListViewHeightBasedOnChildren(lv_multipleImage);

                    Glide.with(getApplicationContext())
                            .load(tempat.getFoto())
                            .asBitmap()
                            .into(iv_gambar);

                    tempat.setKey(keyTempat);
//                    arrFasilitas.addAll(tempat.getFasilitas());



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void hapus() {
        hapusFoto();
        mDatabase.setValue(null);

        Intent main = new Intent(Activity_Edit_Hapus.this, Activity_Main_Admin.class);
        startActivity(main);
        finish();
    }

    private void hapusFoto() {
        if (pilihButton == 1) {
            mStorage = FirebaseStorage.getInstance().getReferenceFromUrl(tempat.getFoto());
            mStorage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(Tag, "onSuccess: deleted file");
                }
            });
        }
        else if (pilihButton == 0) {
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Tempat tempat = dataSnapshot.getValue(Tempat.class);

                    if (tempat != null) {
                        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl(tempat.getFoto());
                        mStorage.delete();

                        if (tempat.getFotoPanorama() != null) {
                            for (int i = 0; i < tempat.getFotoPanorama().size(); i++) {
                                mStorage = FirebaseStorage.getInstance().getReferenceFromUrl(tempat.getFotoPanorama().get(i).getLinkFoto());
                                mStorage.delete();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void simpanEdit() {
        int selected = rg_radio.getCheckedRadioButtonId();
        rb_tipe = (RadioButton) findViewById(selected);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tempat saveTempat = dataSnapshot.getValue(Tempat.class);

                if (saveTempat != null) {
                    saveTempat.setNama(et_nama_tempat.getText().toString());
                    saveTempat.setTipe(rb_tipe.getText().toString());
                    saveTempat.setAlamat(et_alamat_tempat.getText().toString());
                    saveTempat.setTelp(et_telp_tempat.getText().toString());

                    tempJam = tempat.getJam();
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

                    saveTempat.setLati(et_lati.getText().toString());
                    saveTempat.setLongi(et_longi.getText().toString());
                    saveTempat.setOrder(et_order.getText().toString());
                    saveTempat.setKapasitas(et_kapasitas.getText().toString());
                    saveTempat.setProsedurSewa(et_prosedurSewa.getText().toString());
                    saveTempat.setWaktuRamai(et_waktuRamai.getText().toString());
                    saveTempat.setTerm(et_term.getText().toString());
                    saveTempat.setJam(tempJam);

                    mDatabase.setValue(saveTempat);

                    Intent main = new Intent(Activity_Edit_Hapus.this, Activity_Main_Admin.class);
                    startActivity(main);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void pilihGambar() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != getPackageManager().PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Call for permission", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
            }
        }
        else{
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

            if(pathThumbnail != null)
            {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading Thumbnail...");
                progressDialog.show();

                final StorageReference ref = FirebaseStorage.getInstance().getReference().child("location/panorama/"+ UUID.randomUUID().toString());
                ref.putFile(pathThumbnail).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri uri = taskSnapshot.getDownloadUrl();

                    progressDialog.dismiss();
                    Toast.makeText(Activity_Edit_Hapus.this, "Uploaded", Toast.LENGTH_SHORT).show();

                    hapusFoto(); // hapus foto lama
                    mDatabase.child("foto").setValue(uri.toString());
                    System.out.println("asd : " + uri.toString());
                    pilihButton = 0;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Activity_Edit_Hapus.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            }
        }
        else if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null && pilihButton == 2) {
            pathGambar360 = data.getData();
//            iv_gambar_360.setImageURI(pathGambar360);
            System.out.println("path = " + pathGambar360);

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Tempat tempat = dataSnapshot.getValue(Tempat.class);

                    if (tempat != null) {
                        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("location/panorama/" + tempat.getNama() + "/" + tempat.getNama()
                                + " " + (tempat.getFotoPanorama().size() + 1));
                        ref.putFile(pathGambar360).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri uri = taskSnapshot.getDownloadUrl();

                                Toast.makeText(Activity_Edit_Hapus.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                MultipleImage multipleImage = new MultipleImage();
                                multipleImage.setLinkFoto(uri.toString());

                                tempat.getFotoPanorama().add(multipleImage);
                                mDatabase.setValue(tempat);

                                load();
                                ((BaseAdapter) lv_multipleImage.getAdapter()).notifyDataSetChanged();

                                System.out.println("asd : " + uri.toString());
                                pilihButton = 0;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Activity_Edit_Hapus.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                et_lati.setText("" + place.getLatLng().latitude);
                et_longi.setText("" + place.getLatLng().longitude);
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
}
