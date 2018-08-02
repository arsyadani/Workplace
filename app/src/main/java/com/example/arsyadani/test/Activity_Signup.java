package com.example.arsyadani.test;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Activity_Signup extends AppCompatActivity {
    private EditText et_email_signup, et_telp, et_username, et_password_signup, et_password_signup_check;
    private Button btn_signup_signup;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private static final String TAG = "Activity_Signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        et_email_signup = (EditText) findViewById(R.id.et_email_signup);
        et_telp = (EditText) findViewById(R.id.et_telp);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password_signup = (EditText) findViewById(R.id.et_password_signup);
        et_password_signup_check = (EditText) findViewById(R.id.et_password_signup_check);
        btn_signup_signup = (Button) findViewById(R.id.btn_signup_signup);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        btn_signup_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = true;

                if (TextUtils.isEmpty(et_email_signup.getText().toString())) {
                    et_email_signup.setError("Email tidak bisa kosong");
                    check = false;
                }
                if (TextUtils.isEmpty(et_telp.getText().toString())) {
                    et_telp.setError("Nomor telpon tidak bisa kosong");
                    check = false;
                }
                if (TextUtils.isEmpty(et_username.getText().toString())) {
                    et_username.setError("Username tidak bisa kosong");
                    check = false;
                }

                // Password Check
                if (TextUtils.isEmpty(et_password_signup.getText().toString())) {
                    et_password_signup.setError("Password tidak bisa kosong");
                    check = false;
                }
                if (et_password_signup.getText().toString().length() < 6) {
                    et_password_signup.setError("Password min. 6 karakter");
                }
                if (TextUtils.isEmpty(et_password_signup_check.getText().toString())) {
                    et_password_signup_check.setError("Isi kembali password anda");
                    check = false;
                }
                if (!et_password_signup_check.getText().toString().equals(et_password_signup.getText().toString())) {
                    et_password_signup_check.setError("Password tidak sama");
                    check = false;
                }

                if (check) {
                    String email = et_email_signup.getText().toString();
                    String telp = et_telp.getText().toString();
                    String username = et_username.getText().toString();
                    String password = et_password_signup.getText().toString();

                    reg(email, telp, username, password);
                }
                else {
                    Log.w(TAG, "Activity_Signup:!check");
                }
            }
        });
    }

    public void reg(final String email, final String telp, final String username, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser fUser = mAuth.getCurrentUser();
                    String id = fUser.getUid();

                    User user = new User(email, telp, username, password, "user");
                    mDatabase.child("users").child(id).setValue(user);
                    Log.d(TAG, "createUserWithEmail:success");
                    Toast.makeText(Activity_Signup.this, "Signup berhasil", Toast.LENGTH_SHORT).show();

                    mAuth.signInWithEmailAndPassword(email, password);

                    Intent main = new Intent(Activity_Signup.this, Activity_Main_User.class);
                    main.putExtra("uid", id);
                    startActivity(main);

                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(Activity_Signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}



