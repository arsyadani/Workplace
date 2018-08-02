package com.example.arsyadani.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class Activity_Profile extends AppCompatActivity {
    private CircleImageView profile_picture;
    private TextView tv_username;
    private EditText et_email, et_telp, et_username, et_password, et_password_ulang;
    private Button btn_simpan, btn_favorit;

    private String uid;

    private User user;
    private DatabaseReference refUser;
    private StorageReference refStorage;

    // Upload Gambar Profile
    private Uri path;
    private final int PICK_IMAGE_REQUEST = 2;
    private final int READ_EXTERNAL_STORAGE = 0;

    EditText taskEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        profile_picture = (CircleImageView) findViewById(R.id.profile_picture);
        tv_username = (TextView) findViewById(R.id.tv_username);
        et_email = (EditText) findViewById(R.id.et_email);
        et_telp = (EditText) findViewById(R.id.et_telp);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password_ulang = (EditText) findViewById(R.id.et_password_ulang);
        btn_simpan = (Button) findViewById(R.id.btn_simpan);
        btn_favorit = (Button) findViewById(R.id.btn_favorit);

        uid = getIntent().getStringExtra("uid");

        if(uid.equalsIgnoreCase("yuYiVCiPVve7BOOe5ZZC2sQRcCj1")) {
            btn_favorit.setVisibility(View.GONE);
        }
        else {
            btn_favorit.setVisibility(View.VISIBLE);
        }

        user = new User();
        refUser = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        load();

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpan();
            }
        });

        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihGambar();
            }
        });

        btn_favorit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent favorit = new Intent(Activity_Profile.this, Activity_Favorit.class);
                startActivity(favorit);
            }
        });
    }

    private void load() {
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    if (user.getFoto() != null && user.getFoto() != "") {
                        Glide.with(getApplicationContext()).load(user.getFoto()).asBitmap().into(profile_picture);
                    }

                    tv_username.setText(user.getUsername());
                    et_email.setText(user.getEmail());
                    et_telp.setText(user.getTelp());
                    et_username.setText(user.getUsername());
                    et_password.setText(user.getPassword());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void simpan() {
        boolean check = true;
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (et_email.getText() == null || et_email.getText().toString().equals("")) {
            et_email.setError("Email tidak bisa kosong");
            check = false;
        }
        if (et_telp.getText() == null || et_telp.getText().toString().equals("")) {
            et_telp.setError("Telpon tidak bisa kosong");
            check = false;
        }
        if (et_username.getText() == null || et_username.getText().toString().equals("")) {
            et_username.setError("Username tidak bisa kosong");
            check = false;
        }
        if (et_password.getText() == null || et_password.getText().toString().equals("") || et_password.getText().toString().length() < 6) {
            if (et_password.getText() == null || et_password.getText().toString() != "") {
                et_password.setError("Password tidak bisa kosong");
                check = false;
            }
            else if (et_password.getText().toString().length() < 6) {
                et_password.setError("Password harus lebih dari 6 karakter");
                check = false;
            }
        }
        if (!et_password_ulang.getText().toString().equals(et_password.getText().toString())) {
            et_password_ulang.setError("Password tidak sama");
            check = false;
        }

        if (check == true) {
            final boolean finalCheck = true;

            firebaseUser.updateEmail(et_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("Activity_profile", "User email address updated.");

                    firebaseUser.updatePassword(et_password.getText().toString());
                    user.setEmail(et_email.getText().toString());
                    user.setTelp(et_telp.getText().toString());
                    user.setUsername(et_username.getText().toString());
                    user.setPassword(et_password.getText().toString());
                    refUser.setValue(user);
                    Toast.makeText(Activity_Profile.this, "Profil berhasil di ubah", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Activity_profile", "User email address failed to update because " + e);
                    Toast.makeText(Activity_Profile.this, "Gagal menyimpan profil", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void pilihGambar() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != getPackageManager().PERMISSION_GRANTED)
        {
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
            startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            path = data.getData();
            profile_picture.setImageURI(path);
            System.out.println("path = " + path);

            if(path != null)
            {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                final StorageReference ref = FirebaseStorage.getInstance().getReference().child("user/"+ UUID.randomUUID().toString());
                ref.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri uri = taskSnapshot.getDownloadUrl();

                    progressDialog.dismiss();
                    Toast.makeText(Activity_Profile.this, "Uploaded", Toast.LENGTH_SHORT).show();

                    if (user.getFoto() == null || user.getFoto().toString() == "") {
                        refUser.child("foto").setValue(uri.toString());
                    }
                    else {
                        hapusFoto(); // hapus foto lama
                        refUser.child("foto").setValue(uri.toString());
                    }

                    System.out.println("asd : " + uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Activity_Profile.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    }

    private void hapusFoto() {
        refStorage = FirebaseStorage.getInstance().getReferenceFromUrl(user.getFoto());

        refStorage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Activity_Profile", "onSuccess: deleted file");
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


