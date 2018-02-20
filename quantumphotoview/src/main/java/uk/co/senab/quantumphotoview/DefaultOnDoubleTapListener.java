package uk.co.senab.quantumphotoview;

import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Provided default implementation of GestureDetector.OnDoubleTapListener, to be overridden with custom behavior, if needed
 * <p>&nbsp;</p>
 * To be used via {@link uk.co.senab.quantumphotoview.QuantumPhotoViewAttacher#setOnDoubleTapListener(GestureDetector.OnDoubleTapListener)}
 */
public class DefaultOnDoubleTapListener implements GestureDetector.OnDoubleTapListener {

    private QuantumPhotoViewAttacher quantumphotoviewAttacher;

    /**
     * Default constructor
     *
     * @param quantumphotoviewAttacher quantumphotoviewAttacher to bind to
     */
    public DefaultOnDoubleTapListener(QuantumPhotoViewAttacher quantumphotoviewAttacher) {
        setquantumphotoviewAttacher(quantumphotoviewAttacher);
    }

    /**
     * Allows to change quantumphotoviewAttacher within range of single instance
     *
     * @param newquantumphotoviewAttacher quantumphotoviewAttacher to bind to
     */
    public void setquantumphotoviewAttacher(QuantumPhotoViewAttacher newquantumphotoviewAttacher) {
        this.quantumphotoviewAttacher = newquantumphotoviewAttacher;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (this.quantumphotoviewAttacher == null)
            return false;

        ImageView imageView = quantumphotoviewAttacher.getImageView();

        if (null != quantumphotoviewAttacher.getOnPhotoTapListener()) {
            final RectF displayRect = quantumphotoviewAttacher.getDisplayRect();

            if (null != displayRect) {
                final float x = e.getX(), y = e.getY();

                // Check to see if the user tapped on the photo
                if (displayRect.contains(x, y)) {

                    float xResult = (x - displayRect.left)
                            / displayRect.width();
                    float yResult = (y - displayRect.top)
                            / displayRect.height();

                    quantumphotoviewAttacher.getOnPhotoTapListener().onPhotoTap(imageView, xResult, yResult);
                    return true;
                }else{
                    quantumphotoviewAttacher.getOnPhotoTapListener().onOutsidePhotoTap();
                }
            }
        }
        if (null != quantumphotoviewAttacher.getOnViewTapListener()) {
            quantumphotoviewAttacher.getOnViewTapListener().onViewTap(imageView, e.getX(), e.getY());
        }

        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent ev) {
        if (quantumphotoviewAttacher == null)
            return false;

        try {
            float scale = quantumphotoviewAttacher.getScale();
            float x = ev.getX();
            float y = ev.getY();

            if (scale < quantumphotoviewAttacher.getMediumScale()) {
                quantumphotoviewAttacher.setScale(quantumphotoviewAttacher.getMediumScale(), x, y, true);
            } else if (scale >= quantumphotoviewAttacher.getMediumScale() && scale < quantumphotoviewAttacher.getMaximumScale()) {
                quantumphotoviewAttacher.setScale(quantumphotoviewAttacher.getMaximumScale(), x, y, true);
            } else {
                quantumphotoviewAttacher.setScale(quantumphotoviewAttacher.getMinimumScale(), x, y, true);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // Can sometimes happen when getX() and getY() is called
        }

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        // Wait for the confirmed onDoubleTap() instead
        return false;
    }
}
