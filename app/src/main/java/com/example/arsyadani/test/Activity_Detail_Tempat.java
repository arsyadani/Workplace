package com.example.arsyadani.test;


import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Activity_Detail_Tempat extends AppCompatActivity implements RatingDialogListener, OnMapReadyCallback {
    private VrPanoramaView panoWidgetView;
    private ImageLoaderTask backgroundImageLoaderTask;
    private TextView tv_nama_detail, tv_rating_detail, tv_alamat_detail, tv_review_detail, tv_namaFoto, tv_order, tv_kapasitas, tv_prosedur,
            tv_waktuRamai, tv_term;
    private ListView lv_fasilitas, lv_review;
    private ArrayList<String> arrFasilitas;
    private ArrayList<Review> arrReview;
    private Adapter_Fasilitas adapterFasilitas;
    private Adapter_Review adapterReview;
    private Button btn_review, btn_maps, btn_prev, btn_next, btn_drop_down;

    private DatabaseReference refTempat, refReview, refUser;
    private StorageReference stoTempat;

    private String key, uid, nama;
    private double rating, count;
    private boolean check, slide;
    private int countReview, countFoto, temp, max;

    private File localFile;

    private MapView mapView;
    private GoogleMap gmap;

    private final int READ_EXTERNAL_STORAGE = 0;

    // Drop down
    private View layout_drop_down;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_tempat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tv_nama_detail = (TextView) findViewById(R.id.tv_nama_detail);
        tv_rating_detail = (TextView) findViewById(R.id.tv_rating_detail);
        tv_alamat_detail = (TextView) findViewById(R.id.tv_jam);
        tv_review_detail = (TextView) findViewById(R.id.tv_review_detail);

        lv_fasilitas = (ListView) findViewById(R.id.lv_fasilitas);
        lv_review = (ListView) findViewById(R.id.lv_review);

        btn_review = (Button) findViewById(R.id.btn_review);
//        btn_maps = (Button) findViewById(R.id.btn_maps);

        arrFasilitas = new ArrayList<>();
        arrReview = new ArrayList<>();

        key = getIntent().getStringExtra("key");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        nama = getIntent().getStringExtra("nama");

        countFoto = 1;

        tv_namaFoto = (TextView) findViewById(R.id.tv_namaFoto);
        tv_order = (TextView) findViewById(R.id.tv_order);
        tv_kapasitas = (TextView) findViewById(R.id.tv_kapasitas);
        tv_prosedur = (TextView) findViewById(R.id.tv_prosedur);
        tv_waktuRamai = (TextView) findViewById(R.id.tv_waktuRamai);
        tv_term = (TextView) findViewById(R.id.tv_term);

        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        refTempat = FirebaseDatabase.getInstance().getReference().child("tempat").child(key);
        refReview = FirebaseDatabase.getInstance().getReference().child("review");
        refUser = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        btn_prev = (Button) findViewById(R.id.btn_prev);
        btn_next = (Button) findViewById(R.id.btn_next);

        btn_drop_down = (Button) findViewById(R.id.btn_drop_down);
        layout_drop_down = (View) findViewById(R.id.layout_drop_down);
        layout_drop_down.setVisibility(View.GONE);
        slide = false;

        btn_drop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!slide) {
                    layout_drop_down.setVisibility(View.VISIBLE);
                    slide = true;
                }
                else {
                    layout_drop_down.setVisibility(View.GONE);
                    slide = false;
                }
            }
        });

        max = checkMaxSize();
        checkButton();

        System.out.println("max : " + max);
        System.out.println("count : " + countFoto);

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countFoto != 1) {
                    countFoto--;
                    loadPanoImage(countFoto);
                    btn_next.setEnabled(true);
                }
                if(countFoto == 1) {
                    btn_prev.setEnabled(false);
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countFoto != max-1) {
                    countFoto++;
                    loadPanoImage(countFoto);
                    btn_prev.setEnabled(true);
                }

                if (countFoto + 1 == max) {
                    btn_next.setEnabled(false);
                }
            }
        });

        refTempat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Tempat tempat = dataSnapshot.getValue(Tempat.class);

                tv_nama_detail.setText(tempat.getNama());

                ArrayList<String> jam = tempat.getJam();
                String tempJam = "";
                if (!jam.get(0).equalsIgnoreCase("")) {
                    tempJam = "Senin " + jam.get(0);
                }
                if (!jam.get(1).equalsIgnoreCase("")) {
                    tempJam = tempJam + ", Selasa " + jam.get(1);
                }
                if (!jam.get(2).equalsIgnoreCase("")) {
                    tempJam = tempJam + ", Rabu " + jam.get(2);
                }
                if (!jam.get(3).equalsIgnoreCase("")) {
                    tempJam = tempJam + ", Kamis " + jam.get(3);
                }
                if (!jam.get(4).equalsIgnoreCase("")) {
                    tempJam = tempJam + ", Jumat " + jam.get(4);
                }
                if (!jam.get(5).equalsIgnoreCase("")) {
                    tempJam = tempJam + ", Sabtu " + jam.get(5);
                }
                if (!jam.get(6).equalsIgnoreCase("")) {
                    tempJam = tempJam + ", Minggu " + jam.get(6);
                }
                tv_alamat_detail.setText(tempJam);

                ArrayList<MultipleImage> arrayList = tempat.getFotoPanorama();
                char[] charNama = arrayList.get(0).getNamaFoto().toCharArray();
                int charCount = 0;

                for (int a = 0; a < charNama.length; a++) {
                    if(charNama[a] == '-') {
                        charCount = a;
                        break;
                    }
                }

                String namaBaru = String.valueOf(charNama);
                String tempNama = namaBaru.substring(charCount+1, charNama.length);

                tv_namaFoto.setText(tempNama + " (1/" + (max-1) + ")");
                tv_order.setText(tempat.getOrder());
                tv_kapasitas.setText(tempat.getKapasitas());
                tv_prosedur.setText(tempat.getProsedurSewa());
                tv_waktuRamai.setText(tempat.getWaktuRamai());
                tv_term.setText(tempat.getTerm());

                count = 0;

                refReview.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            final Review review = dataSnapshot.getValue(Review.class);

                            if (review.getIdLokasi().equalsIgnoreCase(key)) {
                                rating = rating + review.getRating();
                                count++;
                            }

                            double hasil = rating / count;
                            String convert = String.valueOf(hasil);
                            char[] converted = convert.toCharArray();

                            String temp = "";

                            if (converted.length > 3) {
                                for (int i = 0; i < 3; i++) {
                                    temp = temp + converted[i];
                                }
                                tv_rating_detail.setText(temp);
                            } else {
                                tv_rating_detail.setText(hasil + "");
                                if (Double.isNaN(hasil)) {
                                    tv_rating_detail.setText("0.0");
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

                refTempat.child("fasilitas").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        arrFasilitas = (ArrayList<String>) dataSnapshot.getValue();

                        if (arrFasilitas != null) {
//                            System.out.println("zxc " + arrFasilitas.get(0));
                            adapterFasilitas = new Adapter_Fasilitas(Activity_Detail_Tempat.this, arrFasilitas);

                            lv_fasilitas.setAdapter(adapterFasilitas);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                countReview = 0;

                refReview.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Review review = dataSnapshot.getValue(Review.class);

                        if (review != null) {
                            if (review.getIdLokasi().equalsIgnoreCase(key)) {
                                arrReview.add(review);
                                adapterReview = new Adapter_Review(Activity_Detail_Tempat.this, arrReview);
                                lv_review.setAdapter(adapterReview);

                                justifyListViewHeightBasedOnChildren(lv_review);
                                countReview++;
                                tv_review_detail.setText("Review (" + countReview + ")");
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        panoWidgetView = (VrPanoramaView) findViewById(R.id.pano_view);

        loadPanoImage(countFoto);

//        btn_maps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String geoUri = "http://maps.google.com/maps?q=loc:"
//                        + -7.288161 + "," + 112.6259084 + " (" + "Blablabla" + ")";
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
//                intent.setPackage("com.google.android.apps.maps");
//                startActivity(intent);
//            }
//        });

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != getPackageManager().PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Call for permission", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
            }
        }
    }

    public int checkMaxSize() {
        //Counter foto
        boolean checked = true;
        int countSize = 1;

        do {
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + nama + " " + (countSize) + ".jpg");

            if (file.exists()) {
                countSize++;
            }
            else {
                checked = false;
            }
        }while (checked != false);

        return countSize;
    }

    public void checkButton() {
        if (countFoto == 1) {
            btn_prev.setEnabled(false);

            if (max == 1) {
                btn_next.setEnabled(false);
            }
            else {
                btn_next.setEnabled(true);
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
        par.height = (totalHeight+130) + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    @Override
    public void onPause() {
        panoWidgetView.pauseRendering();
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        panoWidgetView.resumeRendering();
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        // Destroy the widget and free memory.
        panoWidgetView.shutdown();
        mapView.onDestroy();
        super.onDestroy();
    }

    private void loadPanoImage(final int countFoto) {
        ImageLoaderTask task = backgroundImageLoaderTask;
        if (task != null && !task.isCancelled()) {
            // Membatalkan semua task dari load sebelumnya
            task.cancel(true);
        }

        VrPanoramaView.Options viewOptions = new VrPanoramaView.Options();
        viewOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;

        String panoImageName = nama + " " + countFoto + ".jpg";
        System.out.println("Detail tempat : " + panoImageName);

        task = new ImageLoaderTask(panoWidgetView, viewOptions, panoImageName);
        task.execute(getAssets());
        backgroundImageLoaderTask = task;

        refTempat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tempat tempat = dataSnapshot.getValue(Tempat.class);

                if (tempat != null) {
                    char[] charNama = tempat.getFotoPanorama().get(countFoto-1).getNamaFoto().toCharArray();
                    int charCount = 0;

                    for (int a = 0; a < charNama.length; a++) {
                        if(charNama[a] == '-') {
                            charCount = a;
                            break;
                        }
                    }

                    String namaBaru = String.valueOf(charNama);
                    String tempNama = namaBaru.substring(charCount+1, charNama.length);
                    tv_namaFoto.setText(tempNama + " (" + countFoto + "/" + (max-1) + ")");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_detail_tempat, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.icon_bookmark_border) {
            favorit();
        }
        else if (id == 16908332) {
            onBackPressed();
            finish();
        }
        else if (id == R.id.icon_download) {
            try {
                downloadGambar();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void favorit() {
        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                if (user.getFavorit() != null && user.getFavorit().size() > 0 && user.getFavorit().contains(key)) {
                    user.getFavorit().remove(key);
                    refUser.child("favorit").setValue(user.getFavorit());

                    Toast.makeText(getApplicationContext(), "Favorit Dihapus", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (user.getFavorit() == null) {
                        user.setFavorit(new ArrayList<String>());
                    }

                    user.getFavorit().add(key);
                    refUser.child("favorit").setValue(user.getFavorit());

                    Toast.makeText(getApplicationContext(), "Favorit Ditambahkan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void downloadGambar() throws IOException {
        refTempat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tempat tempat = dataSnapshot.getValue(Tempat.class);

                boolean check = true;

                if (tempat != null) {
                    File [] file = new File[tempat.getFotoPanorama().size()];
                    boolean [] checkFoto = new boolean[tempat.getFotoPanorama().size()];
                    for (int i = 0; i < tempat.getFotoPanorama().size(); i++) {
                        file [i] = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + nama + " " + (i + 1) + ".jpg");

                        if (!file[i].exists()) {
                            System.out.println("Masuk false");
                            check = false;
                            checkFoto[i] = false;
                        }
                        else {
                            System.out.println("Masuk true");
                            checkFoto[i] = true;
                        }
                    }

                    if (check == true) {
                        Toast.makeText(Activity_Detail_Tempat.this, "File sudah terdownload", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        for (int i = 0; i < tempat.getFotoPanorama().size(); i++) {
                            if (!checkFoto[i]) {
                                temp = i;
                                stoTempat = FirebaseStorage.getInstance().getReferenceFromUrl(tempat.getFotoPanorama().get(i).getLinkFoto());

                                localFile = null;
                                // localFile = File.createTempFile(nama + " " + (i + 1) + "-", ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                                localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nama + " " + (i + 1) + ".jpg");

                                stoTempat.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(Activity_Detail_Tempat.this, "File sudah terdownload", Toast.LENGTH_SHORT).show();

                                        loadPanoImage(countFoto);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(Activity_Detail_Tempat.this, "Download gagal", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        max = checkMaxSize();
        checkButton();

        tv_namaFoto.setText("Foto (" + countFoto + "/" + (max-1) + ")");
    }

    private void showDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Simpan")
                .setNegativeButtonText("Batalkan")
                .setNoteDescriptions(Arrays.asList("Sangat Buruk", "Buruk", "Normal", "Baik", "Sangat Baik"))
                .setTitle("Rating tempat")
                .setDescription("Silahkan pilih bintang dan berikan masukan anda")
                .setDefaultRating(3)
                .setStarColor(R.color.lightBlue)
                .setNoteDescriptionTextColor(R.color.lightBlue)
                .setTitleTextColor(R.color.black)
                .setDescriptionTextColor(R.color.grey)
                .setHint("Silahkan berikan masukan anda disini...")
                .setHintTextColor(R.color.grey)
                .setCommentTextColor(R.color.darkGrey)
                .setCommentBackgroundColor(R.color.lightGrey)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .create(Activity_Detail_Tempat.this)
                .show();
    }

    @Override
    public void onPositiveButtonClicked(final int star, final String komen) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date date = new Date();
        final String currentTime = dateFormat.format(date).toString();

        Review review = new Review();
        review.setIdPengguna(uid);
        review.setIdLokasi(key);
        review.setReview(komen);
        review.setWaktu(currentTime);
        review.setRating(star);

        refReview.push().setValue(review);
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        refTempat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tempat tempat = dataSnapshot.getValue(Tempat.class);
                String keyTempat = dataSnapshot.getKey();

                if(tempat != null) {
                    if (keyTempat == key) {
                        System.out.println("Lati : " + tempat.getLati());
                        System.out.println("Longi : " + tempat.getLongi());
                        if (tempat.getLati() != null || !tempat.getLati().equals("") || tempat.getLongi() != null || !tempat.getLongi().equals("")) {
                            gmap.setMinZoomPreference(15);
                            UiSettings uiSettings = gmap.getUiSettings();
                            uiSettings.setIndoorLevelPickerEnabled(false);
                            uiSettings.setMyLocationButtonEnabled(true);
                            uiSettings.setMapToolbarEnabled(true);
                            uiSettings.setCompassEnabled(true);
                            uiSettings.setZoomControlsEnabled(true);
                            uiSettings.setAllGesturesEnabled(false);

                            if (tempat.getLati().equals("") || tempat.getLongi().equals("")) {
                                tempat.setLati("0");
                                tempat.setLongi("0");
                            }

                            LatLng ny = new LatLng(Double.parseDouble(tempat.getLati()), Double.parseDouble(tempat.getLongi()));

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(ny);
                            markerOptions.title(tempat.getNama());
                            gmap.addMarker(markerOptions);

                            gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
