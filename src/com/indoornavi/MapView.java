package com.indoornavi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import com.almeros.android.multitouch.gesturedetectors.MoveGestureDetector;
import com.almeros.android.multitouch.gesturedetectors.RotateGestureDetector;
import com.caverock.androidsvg.SVG;

import java.util.Set;

public class MapView extends ImageView implements View.OnTouchListener {
    private static final String TAG = App.TAG + " MapView";

    private SVG svg;

    private Matrix matrix = new Matrix();
    private float scaleFactor = 1.0f;
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
        App.pagerDisallowInterceptListener.onTouch(v, event);

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

    public void setSvg(SVG svg) {
        this.svg = svg;
        float dpi = getResources().getDisplayMetrics().xdpi;
        float documentWidth = svg.getDocumentWidth(dpi);
        float documentHeight = svg.getDocumentHeight(dpi);
        Log.d(TAG, "Svg size: " + documentWidth + "x" + documentHeight);

        //TODO: dynamic resolution depending on scale
        int bmWidth = (int)(Math.ceil(documentWidth));
        int bmHeight = (int)(Math.ceil(documentHeight));
        Log.d(TAG, "Bitmap size: " + bmWidth + "x" + bmHeight);
        Bitmap bm = Bitmap.createBitmap(bmWidth, bmHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bm);
        canvas.drawRGB(255, 255, 255);
        Log.d(TAG, "Canvas size: " + canvas.getWidth() + "x" + canvas.getHeight());

        Set<String> viewList = svg.getViewList();
        Log.d(TAG, "Svg views: " + viewList);

        //svg.renderToCanvas(canvas, null, dpi, SVG.AspectRatioAlignment.xMidYMid, SVG.AspectRatioScale.MEET);
        svg.renderToCanvas(canvas, null, dpi, SVG.AspectRatioAlignment.none, SVG.AspectRatioScale.MEET);
        setImageBitmap(bm);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //Log.d(TAG, "SCALE: " + detector);
            scaleFactor *= detector.getScaleFactor(); // scale change since previous event

            // Don't let the object get too small or too large.
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            Log.d(TAG, "SCALE END: sf=" + scaleFactor);
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
