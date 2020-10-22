package com.bpraak.song.ringtone.love.status.musics.fragement;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bpraak.song.ringtone.love.status.musics.CustomView.CubeOutRotationTransformation;
import com.bpraak.song.ringtone.love.status.musics.R;
import com.bpraak.song.ringtone.love.status.musics.util.Const;
import com.bpraak.song.ringtone.love.status.musics.util.util;

import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static com.bpraak.song.ringtone.love.status.musics.activity.Mycollection.imageUriarr;


public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount;
    private int selectedPosition = 0;
    private Integer[] images;
    private boolean isFromGallery;
    private View v;
    private boolean isFromcollection;

    public static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        try {
            viewPager = (ViewPager) v.findViewById(R.id.viewpager);
            lblCount = (TextView) v.findViewById(R.id.lbl_count);
            images = Const.galleryList;
            selectedPosition = getArguments().getInt("position");
            isFromGallery = getArguments().getBoolean("isFromGallery", false);
            isFromcollection = getArguments().getBoolean("isFromcollection", false);
            myViewPagerAdapter = new MyViewPagerAdapter();
            viewPager.setAdapter(myViewPagerAdapter);
            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
            CubeOutRotationTransformation cubeOutRotationTransformation = new CubeOutRotationTransformation();
            viewPager.setPageTransformer(true, cubeOutRotationTransformation);
            setCurrentItem(selectedPosition);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    private void setWallpaper(int position) {
        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(getActivity());
        try {
            myWallpaperManager.setResource(images[position]);
            Toast.makeText(getActivity(), "Walpaper set Successfully.", Toast.LENGTH_SHORT).show();
            util.showInterastial(getActivity());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        try {
            if (isFromcollection)
                lblCount.setText((position + 1) + " of " + imageUriarr.size());
            else
                lblCount.setText((position + 1) + " of " + images.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {


            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);
            try {
                ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);
                if (isFromcollection)
                    Glide.with(getActivity()).load(imageUriarr.get(position))
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageViewPreview);
                else
                    Glide.with(getActivity()).load(images[position])
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageViewPreview);

                if (isFromGallery) {
                    view.findViewById(R.id.setwall).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.setwall).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setWallpaper(position);
                        }
                    });
                    view.findViewById(R.id.button2).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.setwall).setVisibility(View.GONE);
                    view.findViewById(R.id.button2).setVisibility(View.VISIBLE);
                }


                view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent shareIntent = new Intent();
                        shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUriarr.get(position).toString()));
                        shareIntent.setType("image/jpg");
                        startActivity(Intent.createChooser(shareIntent, "Share with"));
                    }
                });
                container.addView(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }

        @Override
        public int getCount() {
            if (isFromcollection)
                return imageUriarr.size();
            else return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}