package org.quantum.silverphoto.gallery.util;

import com.bumptech.glide.signature.StringSignature;

import org.quantum.silverphoto.gallery.data.Media;

/**
 * Created by SilverLive Team on 21/08/16.
 */

public class MediaSignature extends StringSignature {

  private MediaSignature(String path, long lastModified, int orientation) {
    super(path + lastModified + orientation);
  }

  public MediaSignature(Media media) {
    this(media.getPath(), media.getDateModified(), media.getOrientation());
  }
}
