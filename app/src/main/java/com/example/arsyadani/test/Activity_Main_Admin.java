package com.example.arsyadani.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_Main_Admin extends AppCompatActivity {
    //tab layout
    private Toolbar toolbar;
    private TabLayout tabLayout;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_admin);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        assert tabLayout != null;
        tabLayout.addTab(tabLayout.newTab().setText("Cafe"));
        tabLayout.addTab(tabLayout.newTab().setText("Coworking"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new FragmentCafe(uid);
                    case 1:
                        return new FragmentCoworking(uid);
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
        assert viewPager != null;
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.icon_tambah) {
            Intent tambah = new Intent(Activity_Main_Admin.this, Activity_Tambah.class);
            startActivity(tambah);
        }
        else if (id == R.id.icon_profile_black) {
            Intent profile = new Intent(Activity_Main_Admin.this, Activity_Profile.class);
            profile.putExtra("uid", uid);
            startActivity(profile);
        }
        else if (id == R.id.icon_exit) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, Activity_Login.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main_admin, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setTitle("Keluar dari aplikasi?")
            .setMessage("Tekan iya jika ingin keluar dari aplikasi")
            .setNegativeButton("Batalkan", null)
            .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), Activity_Login.class));
                }
            }).create().show();
    }
}
