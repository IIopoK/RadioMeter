package com.indoornavi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.caverock.androidsvg.SVG;

public class MapPage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView view = new ImageView(getActivity());
        view.setBackgroundColor(Color.WHITE);

        try {
            SVG svg = SVG.getFromResource(getActivity(), R.raw.gradients);
            Bitmap  newBM = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
            Canvas  bmcanvas = new Canvas(newBM);
            // Clear background to white
            bmcanvas.drawRGB(255, 255, 255);
            // Render our document scaled to fit
            // inside our canvas dimensions
            svg.renderToCanvas(bmcanvas, null, 96f, SVG.AspectRatioAlignment.xMidYMid, SVG.AspectRatioScale.MEET);
            view.setImageBitmap(newBM);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return view;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView view = new ImageView(getActivity());
        view.setBackgroundColor(Color.WHITE);

        try {
            SVG svg = SVG.getFromResource(getActivity(), R.raw.gradients);
            Bitmap  newBM = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
            Canvas  bmcanvas = new Canvas(newBM);
            // Clear background to white
            bmcanvas.drawRGB(255, 255, 255);
            // Render our document scaled to fit
            // inside our canvas dimensions
            svg.renderToCanvas(bmcanvas, null, 96f, SVG.AspectRatioAlignment.xMidYMid, SVG.AspectRatioScale.MEET);
            view.setImageBitmap(newBM);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return view;
    }

}
