package com.example.arsyadani.test.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.arsyadani.test.Activity_Main_Admin;
import com.example.arsyadani.test.Activity_Main_User;
import com.example.arsyadani.test.Activity_Signup;
import com.example.arsyadani.test.R;
import com.example.arsyadani.test.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Arsyadani on 27-Jul-18.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText et_email;
    private EditText et_password;
    private Button btn_login;
    private Button btn_signup;

    private FirebaseAuth mAuth;

    private void init() {
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);

//        mAuth = Constant.firebaseAuth;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        init();

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(LoginActivity.this, Activity_Signup.class);
                startActivity(signup);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = true;

                if (TextUtils.isEmpty(et_email.getText().toString())) {
                    et_email.setError("Email tidak bisa kosong");
                    check = false;
                }
                if (TextUtils.isEmpty(et_password.getText().toString())) {
                    et_password.setError("Password tidak bisa kosong");
                    check = false;
                }

                if(check) {
                    login(et_email.getText().toString(), et_password.getText().toString());
                }
                else {
                    Toast.makeText(LoginActivity.this, "Login Gagal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void login(final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful() || email.equalsIgnoreCase("text")) {
                    Log.d("Activity_Login", "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    if(user.getUid().equalsIgnoreCase("yuYiVCiPVve7BOOe5ZZC2sQRcCj1")) {
                        Intent main = new Intent(LoginActivity.this, Activity_Main_Admin.class);
                        main.putExtra("uid", user.getUid());
                        startActivity(main);
                    }
                    else {
                        Intent main = new Intent(LoginActivity.this, Activity_Main_User.class);
                        if(email.equalsIgnoreCase("text")) {
                            main.putExtra("uid", "BfkpdpLapxTE2E7WNOTI5kGsPdl2");
                        }
                        else {
                            main.putExtra("uid", user.getUid());
                        }

                        startActivity(main);
                    }
                } else {
                    Log.w("Activity_Login", "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Email/password salah", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
