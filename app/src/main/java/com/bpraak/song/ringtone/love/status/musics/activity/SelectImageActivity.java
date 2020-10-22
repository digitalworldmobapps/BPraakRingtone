package com.bpraak.song.ringtone.love.status.musics.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bpraak.song.ringtone.love.status.musics.R;
import com.bpraak.song.ringtone.love.status.musics.util.util;

public class SelectImageActivity extends AppCompatActivity implements View.OnClickListener {
    View imgCamera;
    View imgGallery;
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    private View mycollection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.selectimageactivity);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.photoeditor));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            imgCamera = findViewById(R.id.imgCamera);
            imgCamera.setOnClickListener(this);
            mycollection = findViewById(R.id.mycollection);
            mycollection.setOnClickListener(this);
            imgGallery = findViewById(R.id.imgGallery);
            imgGallery.setOnClickListener(this);
            util.showBannerAd2(this);
            util.showBannerAd(this);
            View frm = findViewById(R.id.ll);
            util.nativead(frm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.imgCamera:
                    if (isReadStoragePermissionGranted(CAMERA_REQUEST)) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                    break;

                case R.id.imgGallery:
                    if (isReadStoragePermissionGranted(PICK_REQUEST)) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
                    }
                    break;
                case R.id.mycollection:
                    startActivity(new Intent(SelectImageActivity.this, Mycollection.class));
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        try {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }

                    break;
                case PICK_REQUEST:
                    try {
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isReadStoragePermissionGranted(int pickRequest) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v("", "Permission is granted1");
                    return true;
                } else {

                    Log.v("", "Permission is revoked1");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, pickRequest);
                    return false;
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                Log.v("", "Permission is granted1");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case CAMERA_REQUEST:

                        startActivity(new Intent(SelectImageActivity.this, CropActivity.class).putExtra("data", data).putExtra("camera", 1));
                        break;
                    case PICK_REQUEST:
                        try {
                            Uri uri = data.getData();
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            startActivity(new Intent(SelectImageActivity.this, CropActivity.class).putExtra("data", data).putExtra("camera", 0));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
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
}
