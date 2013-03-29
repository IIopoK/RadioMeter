package com.indoornavi;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class MapPage extends Fragment {
    private static final String TAG = "MapPage";
    private MapView view;
    private int cnt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        view = new MapView(context);
        view.setBackgroundColor(Color.WHITE);

        view.setImageBitmap(rendSvg());

        final Handler uiHandler = new Handler();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                final Bitmap image = rendSvg();
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setImageBitmap(image);
                        view.invalidate();
                    }
                });
            }
        }, 10000, 3 * 1000);
        return view;
    }

    //TODO: move to MapView and apply transformations before setting image to imageview
    private Bitmap rendSvg() {
        try {
            String svgString = loadText(R.raw.rects);
            if(cnt != 0) {
                svgString = svgString.replace("fill=\"none\" id=\"rect_" + cnt + "\"", "fill=\"#0000FF\" id=\"rect_" + cnt + "\"");
            }
            Log.d(TAG, "rendSvg rend: "+ svgString);
            SVG svg = SVG.getFromString(svgString);
            Bitmap bm = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            canvas.drawRGB(255, 255, 255);
            svg.renderToCanvas(canvas, null, 96f, SVG.AspectRatioAlignment.xMidYMid, SVG.AspectRatioScale.MEET);
            cnt++;
            return bm;
        } catch (SVGParseException e) {
            Log.d(TAG, "rendSvg exception", e);
            throw new RuntimeException(e);
        }
    }

   /* private void drawSvg(ImageView view) {
        SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.shapes);
        BitmapDrawable bitmapDrawable = createBitmapDrawable(view.getContext(), svg);
        view.setImageDrawable(bitmapDrawable);
    }

    public BitmapDrawable createBitmapDrawable(Context context, SVG svg) {
        Bitmap bitmap = Bitmap.createBitmap(svg.getPicture().getWidth(), svg.getPicture().getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        PictureDrawable drawable = new PictureDrawable(svg.getPicture());
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return new BitmapDrawable(context.getResources(), bitmap);
    }*/


    public String loadText(int resourceId) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream is = this.getResources().openRawResource(resourceId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            is.close();
            br.close();

        } catch (IOException e) {
            Log.d(TAG, "loadText", e);
        }
        return stringBuilder.toString();
    }

}
