package com.bpraak.song.ringtone.love.status.musics.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.bpraak.song.ringtone.love.status.musics.BuildConfig;
import com.bpraak.song.ringtone.love.status.musics.R;
import com.bpraak.song.ringtone.love.status.musics.adapter.RingtoneListAdapter;
import com.bpraak.song.ringtone.love.status.musics.util.Const;
import com.bpraak.song.ringtone.love.status.musics.util.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FirstActivity extends AppCompatActivity {
    private SearchView searchview;
    RecyclerView recyclerView;
    private RingtoneListAdapter adapter;
    private TextView tvnorecords;
    private Context mContext;
    private AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private List<Object> mRecyclerViewItems = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    int totalItemCount;
    int lastplacedAdindex = 4;
    int posofads = 4;
    private int totalistsize;
    private int loadedlistSize;
    private int remaininglistsize;
    private List<SongVo> arr_songlist = new ArrayList<>();
    // For search purposr
    private List<SongVo> tmparr_songlist;
    // For display purpose
    private List<SongVo> mainvos;
    // For displayed ads or not
    boolean ischeckloaded = false;
    // number of loaded ads count
    int countadsload = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_first);
            mContext = FirstActivity.this;
            recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            mLayoutManager = new WrapContentLinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            searchview = (SearchView) findViewById(R.id.searchview);
            tvnorecords = (TextView) findViewById(R.id.tv_norecords);
            searchview.onActionViewExpanded();
            searchview.clearFocus();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.ringtone));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            // Get data
            arr_songlist = generateArr();
            // Assing to display array
            mainvos = new ArrayList<>(arr_songlist);
            // Initial Data Loads
            if (util.isConnected(this)) {
                loadData();
            } else {
                util.showNetworkDialog(this);
            }
            // Initalize array for search purpose
            tmparr_songlist = new ArrayList<>();
            // Search functionality
            searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    try {
                        if (arr_songlist != null && arr_songlist.size() > 0) {
                            tmparr_songlist.clear();
                            countadsload = 0;
                            if (newText.length() == 0) {
                                tmparr_songlist.addAll(arr_songlist);
                            } else {
                                for (SongVo wp : arr_songlist) {
                                    if (wp.getName().toLowerCase(Locale.getDefault()).contains(newText)) {
                                        tmparr_songlist.add(wp);
                                    }
                                }
                            }
                            setAdapterData(tmparr_songlist);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });

            // Scroll view
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) { //check for scroll down
                        try {
                            nextadsload();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            util.showBannerAd2(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadData() {
        try {
            // Calaculate total size
            totalistsize = mainvos.size();
            boolean isdisplayads = false;
            isdisplayads = totalistsize >= posofads;
            List<SongVo> listArr = mainvos.subList(0, (isdisplayads) ? posofads : totalistsize);
            loadedlistSize = listArr.size();
            mRecyclerViewItems.addAll(listArr);
            if (isdisplayads)
                loadNativeAds();
            adapter = new RingtoneListAdapter(mRecyclerViewItems, mContext);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setAdapterData(List<SongVo> ringVos) {
        try {
            mRecyclerViewItems.clear();
            if (ringVos != null && ringVos.size() > 0) {
                mainvos = ringVos;
                mNativeAds.clear();
                lastplacedAdindex = posofads;
                // Initial loads data for first time
                loadData();
                recyclerView.setVisibility(View.VISIBLE);
                tvnorecords.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                tvnorecords.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nextadsload() {
        try {
            if (util.isConnected(this)) {
                // check ads is loaded or not
                if (mRecyclerViewItems.size() > 0) {
                    if (ischeckloaded) {
                        totalItemCount = mLayoutManager.getItemCount();
                        int lastVisiblesItems = mLayoutManager.findLastVisibleItemPosition();
                        if (lastVisiblesItems + 1 == totalItemCount || countadsload == 1) {
                            remaininglistsize = totalistsize - loadedlistSize;
                            mRecyclerViewItems.remove(null);
                            if (remaininglistsize > posofads) {
                                List<SongVo> listArr = mainvos.subList(loadedlistSize, loadedlistSize + posofads);
                                loadedlistSize = loadedlistSize + posofads;
                                mRecyclerViewItems.addAll(listArr);
                                ischeckloaded = false;
                                loadNativeAds();
                            } else {
                                List<SongVo> listArr = mainvos.subList(loadedlistSize, loadedlistSize + remaininglistsize);
                                loadedlistSize = loadedlistSize + remaininglistsize;
                                mRecyclerViewItems.addAll(listArr);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            } else {
                util.showNetworkDialog(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<SongVo> generateArr() {
        try {
            for (int i = 0; i < Const.Songname.length; i++) {
                arr_songlist.add(new SongVo(i, Const.Songname[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr_songlist;
    }

    private void loadNativeAds() {
        try {
            mRecyclerViewItems.add(null);
            AdLoader.Builder builder = new AdLoader.Builder(this, BuildConfig.nativeads);
            adLoader = builder.forUnifiedNativeAd(
                    new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            // A native ad loaded successfully, check if the ad loader has finished loading
                            // and if so, insert the ads into the list.
                            mNativeAds.add(unifiedNativeAd);
                            if (!adLoader.isLoading()) {
                                insertAdsInMenuItems();
                            }
                        }
                    }).withAdListener(
                    new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            // A native ad failed to load, check if the ad loader has finished loading
                            // and if so, insert the ads into the list.
                            Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                    + " load another.");
                            if (!adLoader.isLoading()) {
                                insertAdsInMenuItems();
                            }
                        }
                    }).build();

            // Load the Native ads.
            adLoader.loadAds(util.AdRequest(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertAdsInMenuItems() {
        try {
            if (mRecyclerViewItems.size() > 0) {
                int offset = posofads;
                for (UnifiedNativeAd ad : mNativeAds) {
                    mRecyclerViewItems.add(lastplacedAdindex, ad);
                    lastplacedAdindex = lastplacedAdindex + 1 + offset;
                    break;
                }
                if (totalItemCount == 0)
                    adapter.notifyDataSetChanged();
                else {
                    adapter.notifyItemRangeInserted(totalItemCount, mRecyclerViewItems.size() - 1);
                }
            }
            ischeckloaded = true;
            countadsload++;
            if (countadsload == 1)
                nextadsload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(int listPosition) {
        try {
            if (util.isConnected(this)) {
                if (listPosition != 0) {
                    if (listPosition % 2 == 0) {
                        util.showInterastial(this);
                    }
                }
                Intent intent = new Intent(mContext, RingActivity.class);
                intent.putExtra("pos", listPosition);
                startActivity(intent);
            } else {
                util.showNetworkDialog(this);
            }
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


    public class SongVo {
        private final int i;
        private final String name;

        public int getI() {
            return i;
        }

        public String getName() {
            return name;
        }

        public SongVo(int i, String name) {
            this.i = i;
            this.name = name;
        }
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        //... constructor
        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("TAG", "meet a IOOBE in RecyclerView");
            }
        }
    }
}


