package com.indoornavi;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class MapPage extends Fragment implements Observer {
    private static final String TAG = "MapPage";
    private MapView view;
    private Map<Integer, SVG> mapRes = new HashMap<Integer, SVG>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mapRes.put(0, SVG.getFromResource(getActivity(), R.raw.lux2_4));
         /*   mapRes.put(1, SVG.getFromResource(getActivity(), R.raw.lux2_4_1));
            mapRes.put(2, SVG.getFromResource(getActivity(), R.raw.lux2_4_2));
            mapRes.put(3, SVG.getFromResource(getActivity(), R.raw.lux2_4_3));
            mapRes.put(4, SVG.getFromResource(getActivity(), R.raw.lux2_4_4));
            mapRes.put(5, SVG.getFromResource(getActivity(), R.raw.lux2_4_5));
            mapRes.put(6, SVG.getFromResource(getActivity(), R.raw.lux2_4_6));
            mapRes.put(7, SVG.getFromResource(getActivity(), R.raw.lux2_4_7));

            mapRes.put(0, SVG.getFromResource(getActivity(), R.raw.lux2_4));
            mapRes.put(1, SVG.getFromResource(getActivity(), R.raw.lux2_4_1));
            mapRes.put(2, SVG.getFromResource(getActivity(), R.raw.lux2_4_3));
            mapRes.put(3, SVG.getFromResource(getActivity(), R.raw.lux2_4_5));*/

        } catch (SVGParseException e) {
            Log.d(TAG, "rendSvg exception", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        view = new MapView(context);
        view.setBackgroundColor(Color.WHITE);
        view.setSvg(mapRes.get(0));
        App.client.addObserver(this);
        return view;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Integer) {
            Integer position = (Integer) data;
            view.setSvg(mapRes.get(position));
        }
    }

    /*private Bitmap rendSvg() {
        SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.lux2_1);
        Bitmap bitmap = Bitmap.createBitmap(svg.getPicture().getWidth(), svg.getPicture().getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        PictureDrawable drawable = new PictureDrawable(svg.getPicture());
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return  bitmap;
    }
*/
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
