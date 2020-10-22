package com.bpraak.song.ringtone.love.status.musics.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bpraak.song.ringtone.love.status.musics.R;
import com.bpraak.song.ringtone.love.status.musics.adapter.GalleryAdapter;
import com.bpraak.song.ringtone.love.status.musics.fragement.SlideshowDialogFragment;
import com.bpraak.song.ringtone.love.status.musics.util.Const;
import com.bpraak.song.ringtone.love.status.musics.util.util;


public class GalleryActivity extends AppCompatActivity {

    private String fromclass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.gallery);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            fromclass = getIntent().getStringExtra("fromactivity");
            if (fromclass.equalsIgnoreCase("gallery")) {
                toolbar.setTitle(getString(R.string.wallpapper));
            }
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            GalleryAdapter mAdapter = new GalleryAdapter(getApplicationContext(), Const.galleryList);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    if (fromclass.equalsIgnoreCase("gallery")) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        bundle.putBoolean("isFromGallery", true);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                        newFragment.setArguments(bundle);
                        newFragment.show(ft, "slideshow");
                        util.showInterastial(GalleryActivity.this);
                    }
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
            util.showBannerAd2(this);
            util.showBannerAd(this);
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