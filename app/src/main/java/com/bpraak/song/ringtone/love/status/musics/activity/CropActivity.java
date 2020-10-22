package com.bpraak.song.ringtone.love.status.musics.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.bpraak.song.ringtone.love.status.musics.R;
import com.bpraak.song.ringtone.love.status.musics.fragement.MainFragment;
import com.bpraak.song.ringtone.love.status.musics.util.CropDemoPreset;
import com.bpraak.song.ringtone.love.status.musics.util.CropImageViewOptions;
import com.bpraak.song.ringtone.love.status.musics.util.util;
import com.theartofdev.edmodo.cropper.CropImage;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

public class CropActivity extends AppCompatActivity {

    private MainFragment mCurrentFragment;
    private Uri mCropImageUri;
    public void setCurrentFragment(MainFragment fragment) {
        mCurrentFragment = fragment;
    }
    public void setCurrentOptions(CropImageViewOptions options) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_crop);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.crop));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            isReadStoragePermissionGranted();
            if (isWriteStoragePermissionGranted()) {
                readdata();
            } else {
                Toast.makeText(this,"Failed to capture image, please try again",Toast.LENGTH_LONG).show();
                finish();
            }
            if (savedInstanceState == null) {
                setMainFragmentByPreset(CropDemoPreset.RECT);
            }
            util.showBannerAd2(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readdata() {
        try {
            Intent data = (Intent) getIntent().getExtras().get("data");
            int iscamera = getIntent().getIntExtra("camera", 0);
            if (iscamera == 1) {
                if (data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    mCropImageUri = getImageUri(getApplicationContext(), photo);
                }
            } else {
                mCropImageUri = data.getData();
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (CropImage.isReadExternalStoragePermissionsRequired(this, mCropImageUri)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        try {
            mCurrentFragment.setImageUri(mCropImageUri);
            mCurrentFragment.updateCurrentCropViewOptions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (mCurrentFragment != null && mCurrentFragment.onOptionsItemSelected(item)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                    && resultCode == AppCompatActivity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                Uri imageUri = getImageUri(getApplicationContext(), photo);
                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                    mCropImageUri = imageUri;
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                } else {

                    mCurrentFragment.setImageUri(mCropImageUri);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isReadStoragePermissionGranted() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v("", "Permission is granted1");
                    return true;
                } else {

                    Log.v("", "Permission is revoked1");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
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

    public boolean isWriteStoragePermissionGranted() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v("", "Permission is granted2");
                    return true;
                } else {

                    Log.v("", "Permission is revoked2");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    return false;
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                Log.v("", "Permission is granted2");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        try {
            if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CropImage.startPickImageActivity(this);
                } else {
                    Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG)
                            .show();
                }
            }
            if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
                if (mCropImageUri != null
                        && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCurrentFragment.setImageUri(mCropImageUri);
                } else {
                    Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG)
                            .show();
                }
            }
            switch (requestCode) {
                case 2:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        readdata();
                    }
                    break;

                case 3:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
                            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                CropImage.startPickImageActivity(this);
                            } else {
                                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
                            if (mCropImageUri != null
                                    && grantResults.length > 0
                                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                mCurrentFragment.setImageUri(mCropImageUri);
                            } else {
                                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    } else {

                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMainFragmentByPreset(CropDemoPreset demoPreset) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance(demoPreset))
                    .commit();
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
        return super.onSupportNavigateUp();
    }
}
