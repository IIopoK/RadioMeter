package com.indoornavi;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import com.almeros.android.multitouch.gesturedetectors.MoveGestureDetector;
import com.almeros.android.multitouch.gesturedetectors.RotateGestureDetector;

public class MapView extends ImageView implements View.OnTouchListener {
    private static final String TAG = "MapView";

    private Matrix matrix = new Matrix();
    private float scaleFactor = .4f;
    private float rotationDegree = 0.f;
    private float mFocusX = 0.f;
    private float mFocusY = 0.f;

    private ScaleGestureDetector scaleDetector;
    private RotateGestureDetector rotateDetector;
    private MoveGestureDetector moveDetector;

    public MapView(Context context) {
        super(context);
        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(this);

        matrix.postScale(scaleFactor, scaleFactor);
        setImageMatrix(matrix);

        // Setup Gesture Detectors
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        rotateDetector = new RotateGestureDetector(context, new RotateListener());
        moveDetector = new MoveGestureDetector(context, new MoveListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch : " + event);
        scaleDetector.onTouchEvent(event);
        rotateDetector.onTouchEvent(event);
        moveDetector.onTouchEvent(event);

        float scaledImageCenterX = (getDrawable().getIntrinsicWidth() * scaleFactor) / 2;
        float scaledImageCenterY = (getDrawable().getIntrinsicHeight() * scaleFactor) / 2;

        matrix.reset();
        matrix.postScale(scaleFactor, scaleFactor);
        matrix.postRotate(rotationDegree, scaledImageCenterX, scaledImageCenterY);
        matrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY - scaledImageCenterY);
        setImageMatrix(matrix);

        return true; // indicate event was handled
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d(TAG, "SCALE: " + detector);
            scaleFactor *= detector.getScaleFactor(); // scale change since previous event

            // Don't let the object get too small or too large.
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));

            return true;
        }
    }

    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            rotationDegree -= detector.getRotationDegreesDelta();
            return true;
        }
    }

    private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {
        @Override
        public boolean onMove(MoveGestureDetector detector) {
            PointF d = detector.getFocusDelta();
            mFocusX += d.x;
            mFocusY += d.y;

            // mFocusX = detector.getFocusX();
            // mFocusY = detector.getFocusY();
            return true;
        }
    }

}
