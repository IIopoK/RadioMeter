package com.indoornavi;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

public class MapPage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView view = new ImageView(getActivity());
        view.setBackgroundColor(Color.WHITE);

        try {
            drawSvg(view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return view;
    }

    /*private void drawSvg(ImageView view) throws SVGParseException {
        SVG svg = SVG.getFromResource(getActivity(), R.raw.transformations);

        Bitmap  bm = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        Canvas  canvas = new Canvas(bm);
        canvas.drawRGB(255, 255, 255);
        svg.renderToCanvas(canvas, null, 96f, SVG.AspectRatioAlignment.xMidYMid, SVG.AspectRatioScale.MEET);
        view.setImageBitmap(bm);

    }
*/
    private void drawSvg(ImageView view) {
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
    }
}
