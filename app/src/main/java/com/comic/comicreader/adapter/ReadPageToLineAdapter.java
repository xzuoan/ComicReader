package com.comic.comicreader.adapter;

import android.content.Context;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.comic.comicreader.R;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ReadPageToLineAdapter extends BaseAdapter {


    ArrayList<?> imageSourceList;
    LayoutInflater inflater;
    Context context;

    public ReadPageToLineAdapter(Context context, ArrayList<?> imageSourceList) {
        super();
        this.context = context;
        this.imageSourceList = imageSourceList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageSourceList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageSourceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sub_layout_readpage_line, parent, false);
        }
        ImageView imageView = convertView.findViewById(R.id.lv_read_page_line_image_view);
        imageView.setAdjustViewBounds(true);
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        imageView.setMaxWidth(screenWidth);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.Source imageSource = ImageDecoder.createSource(ByteBuffer.wrap((byte[]) getItem(position)));
            try {
                Drawable drawable = ImageDecoder.decodeDrawable(imageSource);
                imageView.setImageDrawable(drawable);
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return convertView;
    }
}
