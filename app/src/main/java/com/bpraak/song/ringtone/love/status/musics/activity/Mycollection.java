package com.bpraak.song.ringtone.love.status.musics.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bpraak.song.ringtone.love.status.musics.R;
import com.bpraak.song.ringtone.love.status.musics.adapter.GalleryAdapter;
import com.bpraak.song.ringtone.love.status.musics.adapter.MyCollectionAdapter;
import com.bpraak.song.ringtone.love.status.musics.fragement.SlideshowDialogFragment;
import com.bpraak.song.ringtone.love.status.musics.util.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Mycollection extends AppCompatActivity {

    public static List<Uri> imageUriarr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.my_collection);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.mycollection));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.collection);
            imageUriarr = new ArrayList<>();
            try {
                File file = new File(util.saveImagePath(this));
                file.mkdirs();
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if(files[i].length()>0) {
                        imageUriarr.add(Uri.fromFile(files[i]));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            if (imageUriarr != null && imageUriarr.size() > 0) {
                MyCollectionAdapter mAdapter = new MyCollectionAdapter(this, imageUriarr);
                recyclerView.setAdapter(mAdapter);
            }
            recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    bundle.putBoolean("isFromcollection", true);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "slideshow");
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
            util.showBannerAd(this);
            util.showBannerAd2(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        try {
            onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
