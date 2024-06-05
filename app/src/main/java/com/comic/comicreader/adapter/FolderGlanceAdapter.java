package com.comic.comicreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comic.comicreader.R;
import com.comic.comicreader.util.StoreConvert;
import com.comic.comicreader.util.TimeConvert;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class FolderGlanceAdapter extends BaseAdapter {

    ArrayList<File> fileNameList;
    LayoutInflater inflater;
    Context context;

    public FolderGlanceAdapter(Context context, ArrayList<File> fileNameList) {
        this.context = context;
        this.fileNameList = fileNameList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fileNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sub_layout_folder_glance_list, parent, false);
        }
        ImageView fileView = convertView.findViewById(R.id.lv_folder_glance_description);
        TextView fileName = convertView.findViewById(R.id.lv_folder_glance_name);
        TextView fileCount = convertView.findViewById(R.id.lv_folder_glance_count);
        TextView fileDate = convertView.findViewById(R.id.lv_folder_glance_date);
        TextView fileGuide = convertView.findViewById(R.id.lv_folder_glance_enter_dir);
        File file = (File) getItem(position);
        if (file.exists()) {
            fileDate.setText(String.valueOf(new TimeConvert().timestamp2Date(file.lastModified())));
            fileName.setText(file.getName());
            if (file.isDirectory()) {
                fileView.setImageResource(R.mipmap.folder_02);
                int count;
                try {
                    count = Objects.requireNonNull(file.list()).length;
                } catch (NullPointerException e) {count = 0;}
                fileCount.setText(String.join("", "共", String.valueOf(count), "项"));
                fileGuide.setText(">");
            } else if (file.isFile()) {
                fileView.setImageResource(R.mipmap.folder_03);
                fileCount.setText(new StoreConvert().getStore(file.length()));
                fileGuide.setText("");
            }
        }
        return convertView;
    }
}
