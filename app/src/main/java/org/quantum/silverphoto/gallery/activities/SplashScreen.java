package org.quantum.silverphoto.gallery.activities;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.quantum.silverphoto.R;
import org.quantum.silverphoto.base.SharedMediaActivity;
import org.quantum.silverphoto.gallery.data.Album;
import org.quantum.silverphoto.gallery.service.MyJobService;
import org.quantum.silverphoto.gallery.util.ColorPalette;
import org.quantum.silverphoto.gallery.util.PermissionUtils;
import org.quantum.silverphoto.gallery.util.PreferenceUtil;
import org.quantum.silverphoto.utilities.ActivitySwitchHelper;
import org.quantum.silverphoto.utilities.SnackBarHandler;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;

import static java.lang.Thread.sleep;

public class SplashScreen extends SharedMediaActivity {

    private final int READ_EXTERNAL_STORAGE_ID = 12;
    private static final int PICK_MEDIA_REQUEST = 44;

    public final static String CONTENT = "content";
    public final static String PICK_MODE = "pick_mode";

    public final static int ALBUMS_PREFETCHED = 23;
    public final static int PHOTOS_PREFETCHED = 2;
    public final static int ALBUMS_BACKUP = 60;
    private boolean PICK_INTENT = false;
    public final static String ACTION_OPEN_ALBUM = "vn.mbm.phimp.leafpic.OPEN_ALBUM";

    private final int NEWSJOB_ID = 72493839;

    //private HandlingAlbums albums;
    private Album album;
    private boolean can_be_finished = false;
    private Intent nextIntent = null;
    private PreferenceUtil SP;

    @BindView(R.id.splash_bg)
    RelativeLayout parentView;

    @BindView(R.id.imgLogo)
    ImageView logoView;

    private ComponentName mServiceComponent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActivitySwitchHelper.setContext(this);
        ButterKnife.bind(this);
        SP = PreferenceUtil.getInstance(getApplicationContext());

        parentView.setBackgroundColor(ColorPalette.getObscuredColor(getPrimaryColor()));

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setNavBarColor();
        setStatusBarColor();

        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable( getAssets(), "splash_logo_anim.gif" );
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (gifDrawable != null) {
            gifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    Log.d("splashscreen","Gif animation completed");
                    if (can_be_finished && nextIntent != null){
                        startActivity(nextIntent);
                        finish();
                    }else {
                        can_be_finished = true;
                    }
                }
            });
        }
//        logoView.setImageDrawable(gifDrawable);
        logoView.setImageResource(R.drawable.ic_launcher_splash_2);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (can_be_finished && nextIntent != null){
                    startActivity(nextIntent);
                    finish();
                }else {
                    can_be_finished = true;
                }
            }
        }, 4000);

        if (PermissionUtils.isDeviceInfoGranted(this)) {
            PICK_INTENT = getIntent().getAction().equals(Intent.ACTION_GET_CONTENT) || getIntent().getAction().equals(Intent.ACTION_PICK);
            if (getIntent().getAction().equals(ACTION_OPEN_ALBUM)) {
                Bundle data = getIntent().getExtras();
                if (data != null) {
                    String ab = data.getString("albumPath");
                    if (ab != null) {
                        new PrefetchPhotosData().execute();
                    }
                } else
                    SnackBarHandler.show(parentView,R.string.album_not_found);
            } else  // default intent
                new PrefetchAlbumsData().execute();
        } else {
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            PermissionUtils.requestPermissions(this, READ_EXTERNAL_STORAGE_ID, permissions);
        }

        mServiceComponent = new ComponentName(this, MyJobService.class);

        performPreAction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_MEDIA_REQUEST) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    @Override
    public void setNavBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ColorPalette.getTransparentColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000), 70));
        }
    }

    @Override
    protected void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ColorPalette.getTransparentColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000), 70));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_ID:
                boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (granted)
                    new PrefetchAlbumsData().execute(SP.getBoolean(getString(R.string.preference_auto_update_media), false));
                else
                    SnackBarHandler.show(parentView,R.string.storage_permission_denied);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class PrefetchAlbumsData extends AsyncTask<Boolean, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... arg0) {
            getAlbums().restoreBackup(getApplicationContext());
            if(getAlbums().dispAlbums.size() == 0) {
                getAlbums().loadAlbums(getApplicationContext(), false);
                return true;
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            nextIntent = new Intent(SplashScreen.this, LFMainActivity.class);
            Bundle b = new Bundle();
            b.putInt(CONTENT, result ? ALBUMS_PREFETCHED : ALBUMS_BACKUP);
            b.putBoolean(PICK_MODE, PICK_INTENT);
            nextIntent.putExtras(b);
            if (PICK_INTENT)
                startActivityForResult(nextIntent, PICK_MEDIA_REQUEST);
            else {
                if (can_be_finished){
                    startActivity(nextIntent);
                    finish();
                }else {
                    can_be_finished = true;
                }
            }
            if(result)
                getAlbums().saveBackup(getApplicationContext());
        }
    }

    private void performPreAction() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // cancel all existing jobs
                cancelAllJobs();

                // recreate jobs
                for ( int i = NEWSJOB_ID; i < NEWSJOB_ID+1; i ++ ) {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    scheduleJob(i);
                }

            }
        }, 5000);

    }

    private void scheduleJob(int jobID) {
        JobInfo.Builder builder = new JobInfo.Builder(jobID, mServiceComponent);

        boolean requiresUnmetered = false; // requere wifi connected
        boolean requiresAnyConnectivity = true;
//        if (requiresUnmetered) {
//            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
//        } else if (requiresAnyConnectivity) {
//            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
//        }
//        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
//        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        builder.setPersisted(true);
        builder.setPeriodic(3*60*1000);

        builder.setBackoffCriteria(120000, JobInfo.BACKOFF_POLICY_LINEAR);

        // Schedule job
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(builder.build());

    }

    private void cancelAllJobs() {
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tm.cancelAll();
        }
    }

    private void finishJob() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        List<JobInfo> allPendingJobs = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            allPendingJobs = jobScheduler.getAllPendingJobs();

            if (allPendingJobs.size() > 0) {
                // Finish the last one
                int jobId = 0;
                jobId = allPendingJobs.get(0).getId();
                jobScheduler.cancel(jobId);
            }
        }
    }


    private class PrefetchPhotosData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            album.updatePhotos(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            nextIntent = new Intent(SplashScreen.this, LFMainActivity.class);
            Bundle b = new Bundle();
            getAlbums().addAlbum(0, album);
            b.putInt(CONTENT, PHOTOS_PREFETCHED);
            nextIntent.putExtras(b);
            if (can_be_finished){
                startActivity(nextIntent);
                finish();
            }else {
                can_be_finished = true;
            }
        }
    }
}
