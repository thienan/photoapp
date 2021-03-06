package org.quantum.silverphoto.opencamera.Camera;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import org.quantum.silverphoto.R;
import org.quantum.silverphoto.base.ThemedActivity;
import org.quantum.silverphoto.editor.EditImageActivity;
import org.quantum.silverphoto.editor.FileUtils;
import org.quantum.silverphoto.gallery.util.AlertDialogsHelper;
import org.quantum.silverphoto.share.SharingActivity;
import org.quantum.silverphoto.utilities.ActivitySwitchHelper;
import org.quantum.silverphoto.utilities.Constants;
import org.quantum.silverphoto.utilities.SnackBarHandler;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.quantumphotoview.QuantumPhotoView;

import static org.quantum.silverphoto.share.SharingActivity.EXTRA_OUTPUT;


public class PhotoActivity extends ThemedActivity {

    public static String FILE_PATH = "file_path";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.parentLayout)
    View parent;
    @BindView(R.id.imageView)
    QuantumPhotoView imageView;
    @BindView(R.id.toolbar_bottom_preview)
    ActionMenuView preview_toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        toolbar.setBackgroundColor(getPrimaryColor());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_arrow_back));
        loadFromIntent();
        setUI();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(PhotoActivity.this, getDialogStyle());
                AlertDialogsHelper.getTextDialog(PhotoActivity.this, deleteDialog,
                        R.string.delete, R.string.delete_photo_message, null);
                deleteDialog.setNegativeButton(getApplicationContext().getString(R.string.cancel).toUpperCase(), null);
                deleteDialog.setPositiveButton(getApplicationContext().getString(R.string.delete).toUpperCase(),
                        new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                deleteFile();
                            }
                        });
                AlertDialog dialog = deleteDialog.create();
                dialog.show();
                AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface
                        .BUTTON_NEGATIVE}, getAccentColor(), dialog);
            }
        });
    }

    private void setUI() {
        Menu bottomMenu = preview_toolbar.getMenu();
        parent.setBackgroundColor(getBackgroundColor());
        int colorBottomIcons = setIconColor(getPrimaryColor());
        getMenuInflater().inflate(R.menu.menu_preview, bottomMenu);
        for (int i = 0; i < bottomMenu.size(); i++) {
            bottomMenu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onOptionsItemSelected(item);
                }
            });
            bottomMenu.getItem(i).getIcon().setColorFilter(colorBottomIcons, PorterDuff.Mode.SRC_ATOP);
        }
        setStatusBarColor();
        preview_toolbar.setBackgroundColor(getPrimaryColor());
    }

    private int setIconColor(int color) {
        if (Color.red(color) + Color.green(color) + Color.blue(color) < 300)
            return Color.WHITE;
        else
            return Color.BLACK;
    }

    private void loadFromIntent() {
        if (null != getIntent() && null != getIntent().getExtras()) {
            FILE_PATH = getIntent().getExtras().getString("filepath");
            if (!TextUtils.isEmpty(FILE_PATH)) {
                Glide.with(this)
                        .load(new File(FILE_PATH))
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .thumbnail(0.5f)
                        .into(imageView);
                return;
            }
        }
        try {
            SnackBarHandler.show(parent, R.string.image_invalid);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile() {
        String[] projection = {MediaStore.Images.Media._ID};

        // Match on the file path
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{FILE_PATH};

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
        } else {
            // File not found in media store DB
        }
        c.close();
        finish();
    }

    public void editImage() {
        String extension = FileUtils.getExtension(FILE_PATH);
        if (extension != null) {
            Intent intent = new Intent(PhotoActivity.this, EditImageActivity.class);
            intent.putExtra("extra_input", FILE_PATH);
            intent.putExtra("extra_output", FileUtils.genEditFile(extension).getAbsolutePath());
            intent.putExtra("requestCode", 1);
            startActivity(intent);
            finish();
        } else
            SnackBarHandler.show(parent, R.string.image_invalid);
    }

    public void saveOriginal() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photoactivity, menu);
        Drawable shareIcon = getResources().getDrawable(R.drawable.ic_others_black, getTheme());
        shareIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        menu.findItem(R.id.menu_share).setIcon(shareIcon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                Intent share = new Intent(PhotoActivity.this, SharingActivity.class);
                share.putExtra(EXTRA_OUTPUT, FILE_PATH);
                startActivityForResult(share, Constants.REQUEST_SHARE_RESULT);
                return true;

            case R.id.delete:
                final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(PhotoActivity.this, getDialogStyle());
                AlertDialogsHelper.getTextDialog(PhotoActivity.this, deleteDialog,
                        R.string.delete, R.string.delete_photo_message, null);
                deleteDialog.setNegativeButton(this.getString(R.string.cancel).toUpperCase(), null);
                deleteDialog.setPositiveButton(this.getString(R.string.delete).toUpperCase(),
                        new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                deleteFile();
                            }
                        });
                final AlertDialog dialog = deleteDialog.create();
                dialog.show();
                AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface
                        .BUTTON_NEGATIVE}, getAccentColor(), dialog);
                return true;

            case R.id.save:
                saveOriginal();
                return true;

            case R.id.edit:
                editImage();
                return true;

            default:
                // invoke super.
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivitySwitchHelper.setContext(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_SHARE_RESULT && resultCode == RESULT_OK && data != null) {
            int result = data.getIntExtra(Constants.SHARE_RESULT, Constants.FAIL);
            if(result == Constants.SUCCESS) {
                finish();
            }
        }
    }
}
