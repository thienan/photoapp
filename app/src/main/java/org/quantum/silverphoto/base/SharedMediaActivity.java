package org.quantum.silverphoto.base;

import android.os.Bundle;

import org.quantum.silverphoto.MyApplication;
import org.quantum.silverphoto.gallery.data.Album;
import org.quantum.silverphoto.gallery.data.HandlingAlbums;

/**
 * Created by SilverLive Team on 03/08/16.
 */

public class SharedMediaActivity extends ThemedActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
  }

  public static HandlingAlbums getAlbums() {
	return ((MyApplication)MyApplication.applicationContext).getAlbums();
  }

  public Album getAlbum() {
	return ((MyApplication) getApplicationContext()).getAlbum();
  }
}
