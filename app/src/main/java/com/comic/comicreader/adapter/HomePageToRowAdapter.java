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
import android.widget.TextView;

import com.comic.comicreader.R;
import com.comic.comicreader.homepage.MangaInfo;
import com.comic.comicreader.util.DataOperate;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.ArrayList;

public class HomePageToRowAdapter extends BaseAdapter {

    private final ArrayList<MangaInfo> mangaInfo;
    private final LayoutInflater inflater;
    Context context;
    Float scaleValue;
    private static final int DEFAULT_VIEW_WIDTH = 125;
    private static final int DEFAULT_VIEW_HEIGHT = 210;
    final static String IMAGE_FORMAT = ".jpeg";
    OnOptionClickListener onOptionClickListener;

    public HomePageToRowAdapter(Context context, ArrayList<MangaInfo> mangaInfo, OnOptionClickListener listener) {
        super();
        this.context = context;
        this.mangaInfo = mangaInfo;
        this.inflater = LayoutInflater.from(context);
        this.onOptionClickListener = listener;
    }

    @Override
    public int getCount() {
        return mangaInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return mangaInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sub_layout_homepage_rowlist, parent, false);
        }
        ImageView rowImage = convertView.findViewById(R.id.lv_homepage_row_image);
        ImageView rowMore = convertView.findViewById(R.id.lv_homepage_row_more);
        TextView rowPages = convertView.findViewById(R.id.lv_homepage_row_pages);
        TextView rowTitle = convertView.findViewById(R.id.lv_homepage_row_title);
        TextView rowReadStatePages = convertView.findViewById(R.id.lv_homepage_row_read_state_pages);
        TextView rowReadStatePercentage = convertView.findViewById(R.id.lv_homepage_row_read_state_percentage);
        TextView rowFileStore = convertView.findViewById(R.id.lv_homepage_row_file_store);
        TextView rowReadTime = convertView.findViewById(R.id.lv_homepage_row_read_time);
        MangaInfo item = (MangaInfo) getItem(position);

        rowPages.setTextSize(14);
        rowTitle.setTextSize(12);
        rowReadStatePages.setTextSize(12);
        rowReadStatePercentage.setTextSize(12);
        rowFileStore.setTextSize(12);
        rowReadTime.setTextSize(11);

        ViewGroup.LayoutParams convertViewLayoutParams = convertView.getLayoutParams();
        convertViewLayoutParams.width  = dpi2px(context, DEFAULT_VIEW_WIDTH);
        convertViewLayoutParams.height = dpi2px(context, DEFAULT_VIEW_HEIGHT);
        convertView.setLayoutParams(convertViewLayoutParams);
        ViewGroup.LayoutParams rowImageLayoutParams = rowImage.getLayoutParams();
        rowImageLayoutParams.width = dpi2px(context, 125);
        rowImageLayoutParams.height = dpi2px(context, 150);
        rowImage.setLayoutParams(rowImageLayoutParams);
        ViewGroup.LayoutParams rowMoreLayoutParams = rowMore.getLayoutParams();
        rowMoreLayoutParams.width = dpi2px(context, 20);
        rowMoreLayoutParams.height = dpi2px(context, 20);
        rowMore.setLayoutParams(rowMoreLayoutParams);

        DataOperate dataOperate = new DataOperate(context);
        String imageCancelPath;
        try {
            imageCancelPath = dataOperate.getPathPreviewImageDirName() + File.separator + dataOperate.Md5(item.getMangaName()) + IMAGE_FORMAT;
            if (new File(imageCancelPath).exists()) {
                rowImage.setImageDrawable(Drawable.createFromPath(imageCancelPath));
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.Source imageSource;
                    try {
                        byte[] byteArray = dataOperate.readZipFirst((item.getMangaPath()));
                        imageSource = ImageDecoder.createSource(ByteBuffer.wrap(byteArray));
                        Drawable drawable = ImageDecoder.decodeDrawable(imageSource);
                        rowImage.setImageDrawable(drawable);
                        dataOperate.saveImageCancel(item.getMangaName(), byteArray);
                        if (drawable instanceof Animatable) {
                            ((Animatable) drawable).start();
                        }
                    } catch (IOException e) {
                        rowImage.setImageResource(R.mipmap.image_destroy_01);
                    }
                }
            }
        } catch (NoSuchAlgorithmException e) {
            rowImage.setImageResource(R.mipmap.image_destroy_01);
        }
        rowImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        rowPages.setText(String.join("", String.valueOf(item.getPages()), "P"));
        rowTitle.setText(item.getMangaName());
        rowReadStatePercentage.setText(NumberFormat.getPercentInstance().format(item.getProgress()));
        rowReadStatePages.setText(String.join("/", String.valueOf(item.getReadPage()), String.valueOf(item.getPages())));
        rowFileStore.setText(item.getFileSize());
        rowReadTime.setText(item.getCreateTime());
        rowMore.setTag(position);
        rowMore.setOnClickListener(onOptionClickListener);

        if (scaleValue == null) scaleValue = 1.0f;
        scaleView(scaleValue, convertView, rowImage, rowMore, rowPages, rowTitle, rowReadStatePages, rowReadStatePercentage, rowFileStore, rowReadTime);
        return convertView;
    }

    private void scaleView(float scale, View ...views) {
        for (View v: views) {
            if (v instanceof TextView) {
                ((TextView) v).setTextSize(((TextView) v).getTextSize() * scale*0.4f);
                v.setPadding(
                        Math.round(v.getPaddingLeft()*scale),
                        Math.round(v.getPaddingTop()*scale),
                        Math.round(v.getPaddingRight()*scale),
                        Math.round(v.getPaddingBottom()*scale));
            }
            else {
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.width  = Math.round(layoutParams.width  * scale);
                layoutParams.height = Math.round(layoutParams.height * scale);
                v.setLayoutParams(layoutParams);
            }
        }
    }

    private int dpi2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setScaleValue(float scale) {
        this.scaleValue = scale;
    }

    public int getDefaultViewHeight(Context context) {
        return dpi2px(context, DEFAULT_VIEW_HEIGHT);
    }

    public int getDefaultViewWidth(Context context) {
        return dpi2px(context, DEFAULT_VIEW_WIDTH);
    }

    public abstract static class OnOptionClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            OnOptionClick(v, (int) v.getTag());
        }
        public abstract void OnOptionClick(View view, int position);
    }
}
