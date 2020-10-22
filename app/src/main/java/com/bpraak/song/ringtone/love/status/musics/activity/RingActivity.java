package com.bpraak.song.ringtone.love.status.musics.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bpraak.song.ringtone.love.status.musics.CustomView.CubeOutRotationTransformation;
import com.bpraak.song.ringtone.love.status.musics.R;
import com.bpraak.song.ringtone.love.status.musics.adapter.SetMenuOptionAdapter;
import com.bpraak.song.ringtone.love.status.musics.util.Const;
import com.bpraak.song.ringtone.love.status.musics.util.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class RingActivity extends AppCompatActivity {
    AudioManager manager;
    ViewPager viewpager;
    Integer[] songidlist;
    MediaPlayer mp;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int SETTING_DIALOG = 25;
    Context mContext;
    private TextView lblCount;
    private int currenttrack;
    private ImageView play;
    private AppCompatSeekBar seekbar;
    private Runnable mRunnable;
    private Handler mHandler;
    private TextView mDuration;
    private TextView mPass;
    private boolean isFromSetting;
    private Dialog setusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_ring);
            mContext = RingActivity.this;
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.ringtone));
            this.setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            play = (ImageView) findViewById(R.id.btn_play);
            ImageView next = (ImageView) findViewById(R.id.btn_next);
            ImageView back = (ImageView) findViewById(R.id.btn_prev);
            mDuration = findViewById(R.id.tv_duration);
            mPass = findViewById(R.id.tv_pass);
            songidlist = Const.songidlist;
            currenttrack = getIntent().getIntExtra("pos", 0);
            manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            viewpager = (ViewPager) findViewById(R.id.viewpager);
            lblCount = (TextView) findViewById(R.id.lbl_count);
            seekbar = (AppCompatSeekBar) findViewById(R.id.seekbar);
            MyringViewPagerAdapter myringViewPagerAdapter = new MyringViewPagerAdapter();
            viewpager.setAdapter(myringViewPagerAdapter);
            CubeOutRotationTransformation cubeOutRotationTransformation = new CubeOutRotationTransformation();
            viewpager.setPageTransformer(true, cubeOutRotationTransformation);
            mp = new MediaPlayer();
            mHandler = new Handler();
            lblCount.setText(Const.Songname[currenttrack]);
            playSong();
            viewpager.setCurrentItem(currenttrack);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playSong();
                }
            });
            viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    if (position > currenttrack) {
                        currenttrack = currenttrack + 1;
                    } else {
                        currenttrack = currenttrack - 1;
                    }
                    if (currenttrack >= 0 && currenttrack <= (songidlist.length - 1)) playSong();
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mp != null)
                        if (mp.isPlaying()) {
                            mp.pause();/* Changing button image to play button*/
                            play.setImageResource(R.drawable.ic_play);
                        } else {/* Resume song*/
                            mp.start();/* Changing button image to pause button*/
                            play.setImageResource(R.drawable.ic_pause);
                        }
                }
            });
            setseekbar();
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {/* The track change code*/
                    if (currenttrack > 0) viewpager.setCurrentItem(currenttrack - 1, true);
                }
            });/* functionality for the next button*/
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currenttrack < songidlist.length - 1)
                        viewpager.setCurrentItem(currenttrack + 1, true);
                }
            });
            util.showBannerAd2(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void getAudioStats() {
        int duration = mp.getDuration(); // In milliseconds
        int due = (mp.getDuration() - mp.getCurrentPosition());
        int pass = duration - due;
        mPass.setText("" + millisecondsToTime(pass) + "");
        mDuration.setText("" + millisecondsToTime(duration) + "");
    }

    private String millisecondsToTime(long milliseconds) {
        try {
            long minutes = (milliseconds / 1000) / 60;
            long seconds = (milliseconds / 1000) % 60;
            String secondsStr = Long.toString(seconds);
            String secs;
            if (secondsStr.length() >= 2) {
                secs = secondsStr.substring(0, 2);
            } else {
                secs = "0" + secondsStr;
            }
            return minutes + ":" + secs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    protected void initializeSeekBar() {
        try {
            seekbar.setMax(mp.getDuration() / 1000);
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mp != null) {
                            int mCurrentPosition = mp.getCurrentPosition() / 1000; // In milliseconds
                            seekbar.setProgress(mCurrentPosition);
                            getAudioStats();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mHandler.postDelayed(mRunnable, 1000);
                }
            };
            mHandler.postDelayed(mRunnable, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setseekbar() {
        try {
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (mp != null && b) {
                        mp.seekTo(i * 1000);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyringViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyringViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = null;
            try {
                layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);
                ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);
                if (songidlist.length > position) {
                    Glide.with(RingActivity.this).load(Const.galleryList[position]).thumbnail(0.5f).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewPreview);
                    container.addView(view);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }

        @Override
        public int getCount() {
            return Const.galleryList.length;
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

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) return true;
        else return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {
            switch (requestCode) {
                case PERMISSION_REQUEST_CODE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.e("value", "Permission Granted, Now you can save image .");
                        if (Build.VERSION.SDK_INT >= 23)
                            if (Settings.System.canWrite(mContext))
                                setting();
                            else {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).setData(Uri.parse("package:" + getPackageName())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(intent, SETTING_DIALOG);
                                isFromSetting = true;
                            }
                    } else Log.e("value", "Permission Denied, You cannot save image.");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSong() {
        try {
            if (util.isConnected(this)) {
                mp.reset();
                AssetFileDescriptor afd = getResources().openRawResourceFd(songidlist[currenttrack]);
                if (afd == null) return;
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mp.prepare();
                mp.start();
                initializeSeekBar();
                getAudioStats();
                play.setImageResource(R.drawable.ic_pause);
                lblCount.setText(Const.Songname[currenttrack]);
            } else {
                util.showNetworkDialog(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareRingtone() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND).setType("audio/ogg");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(saveFile()));
            startActivity(Intent.createChooser(intent, "Share ringtone via:"));
        } catch (Exception e) {/* TODO: handle exception*/
            e.printStackTrace();
        }
    }

    private File saveFile() {
        try {
            File newSoundFile = new File(getExternalCacheDir(), "/bpraakringtonedw.mp3");
            Uri mUri = Uri.parse("android.resource://" + getPackageName() + "/" + songidlist[currenttrack]);
            ContentResolver mCr = getContentResolver();
            AssetFileDescriptor soundFile = mCr.openAssetFileDescriptor(mUri, "r");
            byte[] readData = new byte[1024];
            FileInputStream fis = soundFile.createInputStream();
            FileOutputStream fos = new FileOutputStream(newSoundFile);
            int i = fis.read(readData);
            while (i != -1) {
                fos.write(readData, 0, i);
                i = fis.read(readData);
            }
            fos.close();
            return newSoundFile;
        } catch (Exception e) {/* TODO: handle exception*/
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveas(boolean isRingtone, boolean isAlarm, boolean isNotification) {
        try {
            File k = saveFile();
            if (k != null && k.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.TITLE, "bpraakringtonedw");
                values.put(MediaStore.MediaColumns.SIZE, k.length());
                values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                values.put(MediaStore.Audio.Media.IS_RINGTONE, isRingtone);
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, isNotification);
                values.put(MediaStore.Audio.Media.IS_ALARM, isAlarm);
                Uri uri = Uri.fromFile(k);
                Uri newUri = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    newUri = this.getContentResolver()
                            .insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                    if (newUri != null) {
                        try (OutputStream os = getContentResolver().openOutputStream(newUri)) {

                            byte[] bytes = new byte[(int) k.length()];
                            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(k));
                            buf.read(bytes, 0, bytes.length);
                            buf.close();

                            os.write(bytes);
                            os.close();
                            os.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                } else {
                    values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
                    uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
                    if (uri != null) {
                        getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + k.getAbsolutePath() + "\"", null);
                        newUri = getContentResolver().insert(uri, values);
                    }
                    this.getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath()), values);
                }
                if (isAlarm) {
                    RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_ALARM, newUri);
                    Toast.makeText(mContext, "Alarm tone set successfully", Toast.LENGTH_LONG).show();
                    util.showInterastial(this);
                }
                if (isNotification) {
                    RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_NOTIFICATION, newUri);
                    Toast.makeText(mContext, "Notification tone set successfully", Toast.LENGTH_LONG).show();
                    util.showInterastial(this);
                }
                if (isRingtone) {
                    RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE, newUri);
                    Toast.makeText(mContext, "Ringtone set successfully", Toast.LENGTH_LONG).show();
                    util.showInterastial(this);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (util.isConnected(this)) {
                if (manager.isMusicActive() && mp.isPlaying()) play.performClick();
            } else {
                util.showNetworkDialog(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (util.isConnected(this)) {
                if (isFromSetting)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(mContext))
                            setting();
                    }
            } else {
                util.showNetworkDialog(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Setting Menu
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.optionsmenu, menu);
        return true;
    }/* Handling the Click Events of the Menu*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {/* TODO Auto-generated method stub*/
        try {
            switch (item.getItemId()) {
                case R.id.action_settings:
                    if (Build.VERSION.SDK_INT >= 23)
                        if (checkPermission())
                            if (Settings.System.canWrite(mContext)) setting();
                            else {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).setData(Uri.parse("package:" + getPackageName())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(intent, SETTING_DIALOG);
                                isFromSetting = true;
                            }
                        else
                            ActivityCompat.requestPermissions(RingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    else setting();

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setting() {
        try {
            isFromSetting = false;
            setusDialog = new Dialog(this);
            setusDialog.setContentView(R.layout.setas_dialog);
            setusDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout ll = (LinearLayout) setusDialog.findViewById(R.id.ll);
            RecyclerView recyclerView = (RecyclerView) setusDialog.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            ArrayList<String> strArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.set_menu_name)));
            ArrayList<Integer> integers = new ArrayList<>();
            integers.add(R.drawable.ic_library_music);
            integers.add(R.drawable.alarm);
            integers.add(R.drawable.ic_bell);
            integers.add(R.drawable.sharen);
            SetMenuOptionAdapter adapter = new SetMenuOptionAdapter(mContext, strArray, integers);
            recyclerView.setAdapter(adapter);
            util.nativead(ll);
            setusDialog.show();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onOptionItemclick(int position) {
        try {
            if (position == 0) {
                saveas(true, false, false);
                setusDialog.dismiss();
            } else if (position == 1) {
                saveas(false, true, false);
                setusDialog.dismiss();
            } else if (position == 2) {
                saveas(false, false, true);
                setusDialog.dismiss();
            } else if (position == 3) {
                shareRingtone();
                setusDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (manager.isMusicActive() && mp.isPlaying()) mp.stop();
            if (mHandler != null) {
                mHandler.removeCallbacks(mRunnable);
            }
        } catch (IllegalStateException e) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mp.release();
            if (mHandler != null) {
                mHandler.removeCallbacks(mRunnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}