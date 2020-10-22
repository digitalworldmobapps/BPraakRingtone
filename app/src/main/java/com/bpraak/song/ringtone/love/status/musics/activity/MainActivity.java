package com.bpraak.song.ringtone.love.status.musics.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bpraak.song.ringtone.love.status.musics.BuildConfig;
import com.bpraak.song.ringtone.love.status.musics.R;
import com.bpraak.song.ringtone.love.status.musics.adapter.DashBoardAdapter;
import com.bpraak.song.ringtone.love.status.musics.util.util;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            mContext = MainActivity.this;
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.app_name));
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            ArrayList<String> strArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menu_name)));
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_dashboard);
            ArrayList<Integer> integers = new ArrayList<>();
            integers.add(R.drawable.ringtones);
            integers.add(R.drawable.photoedit);
            integers.add(R.drawable.wallpaper);
            integers.add(R.drawable.moreapp);
            DashBoardAdapter dashBoardAdapter = new DashBoardAdapter(mContext, strArray, integers);
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));
            recyclerView.setAdapter(dashBoardAdapter);
            util.showBannerAd2(this);
            util.showBannerAd(this);
            View frm = findViewById(R.id.ll);
            util.nativead(frm);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    // Drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        try {
            // Handle navigation view item clicks here.
            int id = item.getItemId();
            if (id == R.id.nav_ring) {
                onItemclick(0);
            } else if (id == R.id.nav_editor) {
                    onItemclick(1);
            } else if (id == R.id.nav_gallery) {
                onItemclick(2);
            } else if (id == R.id.nav_more) {
                moreApp();
            } else if (id == R.id.nav_send) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalworldmobapps.github.io/policy/privacy_policy_bpr.html"));
                startActivity(browserIntent);
            } else if (id==R.id.nav_share){
                shareApp();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String shareMessage= "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void moreApp() {
        try {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=digitalworldmobapps")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onItemclick(int position) {
        try {
            switch (position){
                case 0:
                    startActivity(new Intent(MainActivity.this, FirstActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(MainActivity.this, SelectImageActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(MainActivity.this, GalleryActivity.class).putExtra("fromactivity","gallery"));
                    break;
                case 3:
                    moreApp();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rateApp(final boolean isbackpressed) {
        try {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.app_name))
                    .setMessage("Please, Rate and Review " + getString(R.string.app_name) + " app at PlayStore")
                    .setPositiveButton("RATE & REVIEW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                        }
                    })
                    .setNegativeButton("LATER", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isbackpressed) {
                                finish();
                            }
                        }
                    })
                    .setIcon(R.mipmap.ic_launcher)
                    .create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                rateApp(true);
//                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
