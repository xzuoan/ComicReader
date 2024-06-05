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

public class HomePageToLineAdapter extends BaseAdapter {

    private final ArrayList<MangaInfo> mangaInfo;
    private final LayoutInflater inflater;
    final static String IMAGE_FORMAT = ".jpeg";
     Context context;
    private final OnOptionClickListener optionsClickListener;

    public HomePageToLineAdapter(Context context, ArrayList<MangaInfo> mangaInfo, OnOptionClickListener listener) {
        super();
        this.context = context;
        this.mangaInfo = mangaInfo;
        this.inflater = LayoutInflater.from(context);
        this.optionsClickListener = listener;
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
            convertView = inflater.inflate(R.layout.sub_layout_homepage_linelist, parent, false);
        }
        ImageView lineImage = convertView.findViewById(R.id.lv_homepage_line_image);
        ImageView lineMore = convertView.findViewById(R.id.lv_homepage_line_more);
        TextView linePages = convertView.findViewById(R.id.lv_homepage_line_pages);
        TextView lineTitle = convertView.findViewById(R.id.lv_homepage_line_title);
        TextView lineReadStatePercentage = convertView.findViewById(R.id.lv_homepage_line_read_state_percentage);
        TextView lineReadStatePages = convertView.findViewById(R.id.lv_homepage_line_read_state_pages);
        TextView lineFileStore = convertView.findViewById(R.id.lv_homepage_line_file_store);
        TextView lineReadTime = convertView.findViewById(R.id.lv_homepage_line_read_time);
        MangaInfo item = (MangaInfo) getItem(position);

        DataOperate dataOperate = new DataOperate(context);
        String imageCancelPath;
        try {
            imageCancelPath = dataOperate.getPathPreviewImageDirName() + File.separator + dataOperate.Md5(item.getMangaName()) + IMAGE_FORMAT;
            if (new File(imageCancelPath).exists()) {
                lineImage.setImageDrawable(Drawable.createFromPath(imageCancelPath));
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.Source imageSource;
                    try {
                        byte[] byteArray = dataOperate.readZipFirst((item.getMangaPath()));
                        imageSource = ImageDecoder.createSource(ByteBuffer.wrap(byteArray));
                        Drawable drawable = ImageDecoder.decodeDrawable(imageSource);
                        lineImage.setImageDrawable(drawable);
                        dataOperate.saveImageCancel(item.getMangaName(), byteArray);
                        if (drawable instanceof Animatable) {
                            ((Animatable) drawable).start();
                        }
                    } catch (IOException e) {
                        lineImage.setImageResource(R.mipmap.image_destroy_01);
                    }
                }
            }
        } catch (NoSuchAlgorithmException e) {
            lineImage.setImageResource(R.mipmap.image_destroy_01);
        }

        lineImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        linePages.setText(String.join("", String.valueOf(item.getPages()), "P"));
        lineTitle.setText(item.getMangaName());
        lineReadStatePercentage.setText(NumberFormat.getPercentInstance().format(item.getProgress()));
        lineReadStatePages.setText(String.join("/", String.valueOf(item.getReadPage()), String.valueOf(item.getPages())));
        lineFileStore.setText(item.getFileSize());
        lineReadTime.setText(item.getCreateTime());
        lineMore.setTag(position);
        lineMore.setOnClickListener(optionsClickListener);

        return convertView;
    }

    public abstract static class OnOptionClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            OnOptionClick(v, (int) v.getTag());
        }
        public abstract void OnOptionClick(View view, int position);
    }

}
