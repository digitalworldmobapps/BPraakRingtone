package com.bpraak.song.ringtone.love.status.musics.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;

import com.bpraak.song.ringtone.love.status.musics.R;
import com.bpraak.song.ringtone.love.status.musics.base.BaseActivity;
import com.bpraak.song.ringtone.love.status.musics.filters.FilterListener;
import com.bpraak.song.ringtone.love.status.musics.filters.FilterViewAdapter;
import com.bpraak.song.ringtone.love.status.musics.fragement.EmojiBSFragment;
import com.bpraak.song.ringtone.love.status.musics.fragement.PropertiesBSFragment;
import com.bpraak.song.ringtone.love.status.musics.fragement.StickerBSFragment;
import com.bpraak.song.ringtone.love.status.musics.fragement.TextEditorDialogFragment;
import com.bpraak.song.ringtone.love.status.musics.tools.EditingToolsAdapter;
import com.bpraak.song.ringtone.love.status.musics.tools.ToolType;
import com.bpraak.song.ringtone.love.status.musics.util.util;

import java.io.File;
import java.io.IOException;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.ViewType;

public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected, FilterListener {

    private static final String TAG = EditImageActivity.class.getSimpleName();
    public static final String EXTRA_IMAGE_PATHS = "extra_image_paths";

    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private Typeface mWonderFont;
    private RecyclerView mRvTools, mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;
    public static Bitmap mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_edit_image);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.photoeditor));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            mPhotoEditorView = findViewById(R.id.photoEditorView);
            mRvTools = findViewById(R.id.rvConstraintTools);
            mRvFilters = findViewById(R.id.rvFilterView);
            mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");
            mPropertiesBSFragment = new PropertiesBSFragment();
            mEmojiBSFragment = new EmojiBSFragment();
            mStickerBSFragment = new StickerBSFragment();
            mStickerBSFragment.setStickerListener(this);
            mEmojiBSFragment.setEmojiListener(this);
            mPropertiesBSFragment.setPropertiesChangeListener(this);
            LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mRvTools.setLayoutManager(llmTools);
            mRvTools.setAdapter(mEditingToolsAdapter);
            LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mRvFilters.setLayoutManager(llmFilters);
            mRvFilters.setAdapter(mFilterViewAdapter);
            Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");
            mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                    .setPinchTextScalable(true) // set flag to make text scalable when pinch
                    .setDefaultEmojiTypeface(mEmojiTypeFace)
                    .build(); // build photo editor sdk
            mPhotoEditor.setOnPhotoEditorListener(this);
            Intent intent = getIntent();
            if (mImage != null) {
                mPhotoEditorView.getSource().setImageBitmap(mImage);
            } else {
                Uri imageUri = intent.getParcelableExtra("URI");
                if (imageUri != null) {
                    mPhotoEditorView.getSource().setImageURI(imageUri);
                } else {
                    Toast.makeText(this, "No image is set to show", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        try {
            TextEditorDialogFragment textEditorDialogFragment =
                    TextEditorDialogFragment.show(this, text, colorCode);
            textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                @Override
                public void onDone(String inputText, int colorCode) {
                    mPhotoEditor.editText(rootView, inputText, colorCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showLoading("Saving...");
            File file = new File(util.saveImagePath(this) );
            try {
                file.mkdirs();
                file = new File(file.getAbsolutePath()+ File.separator + ""
                        + System.currentTimeMillis() + ".png");
                file.createNewFile();

                SaveSettings saveSettings = new SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build();

                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        hideLoading();
                        showSnackbar("Image Saved Successfully");
                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                        util.showInterastial(EditImageActivity.this);
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideLoading();
                        showSnackbar("Failed to save Image");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                hideLoading();
                showSnackbar(e.getMessage());
            }
        }
    }


    @Override
    public void onColorChanged(int colorCode) {
        try {
            mPhotoEditor.setBrushColor(colorCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpacityChanged(int opacity) {
        try {
            mPhotoEditor.setOpacity(opacity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        try {
            mPhotoEditor.setBrushSize(brushSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        try {
            mPhotoEditor.addEmoji(emojiUnicode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        try {
            mPhotoEditor.addImage(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        try {
            if (isGranted) {
                saveImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSaveDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
            builder.setMessage("Are you want to exit without saving image ?");
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveImage();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        try {
            mPhotoEditor.setFilterEffect(photoFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        try {
            switch (toolType) {
                case BRUSH:
                    mPhotoEditor.setBrushDrawingMode(true);
                    mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                    break;
                case TEXT:
                    TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(EditImageActivity.this);
                    textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                        @Override
                        public void onDone(String inputText, int colorCode) {
                            mPhotoEditor.addText(inputText, colorCode);
                        }
                    });
                    break;
                case ERASER:
                    mPhotoEditor.brushEraser();
                    break;
                case FILTER:
                    mIsFilterVisible = !mRvFilters.isShown();
                    showFilter(mIsFilterVisible);
                    break;
                case EMOJI:
                    mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
                    break;
                case STICKER:
                    mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void showFilter(boolean isVisible) {
        try {
            if (isVisible) {
                mIsFilterVisible =false;
                mRvFilters.setVisibility(View.VISIBLE);
                mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
                mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.START);
                mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END,
                        ConstraintSet.PARENT_ID, ConstraintSet.END);
            } else {
                mIsFilterVisible =true;
                mRvFilters.setVisibility(View.GONE);
                mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.END);
                mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
            }
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(350);
            changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mRvFilters.isShown()) {
                showFilter(false);
            } else if (!mPhotoEditor.isCacheEmpty()) {
                showSaveDialog();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        try {
            MenuInflater inflator = getMenuInflater();
            inflator.inflate(R.menu.editoptionsmenu, menu);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.imgUndo:
                    mPhotoEditor.undo();
                    break;
                case R.id.imgRedo:
                    mPhotoEditor.redo();
                    break;
                case R.id.imgSave:
                    saveImage();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
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
