package com.comic.comicreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.comic.comicreader.R;
import com.comic.comicreader.foldermanagepage.FolderInfo;

import java.util.ArrayList;

public class FolderManageAdapter extends BaseAdapter {

    ArrayList<FolderInfo> folderList;
    LayoutInflater inflater;
    Context context;

    public FolderManageAdapter(Context context, ArrayList<FolderInfo> folderList) {
        this.folderList = folderList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return folderList.size();
    }

    @Override
    public Object getItem(int position) {
        return folderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sub_layout_folder_manage_list, parent, false);
        }
        TextView pathText = convertView.findViewById(R.id.lv_folder_manage_list_path);
        CheckBox checkBox = convertView.findViewById(R.id.lv_folder_manage_list_checkbox);
        convertView.setLongClickable(true);

        FolderInfo item = (FolderInfo) folderList.get(position);
        pathText.setText((CharSequence) (position+1 + ".\t" + item.getPath()));
        checkBox.setChecked(item.getState());

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderList.get(position).setState(!item.getState());
            }
        });

        return convertView;
    }
}
