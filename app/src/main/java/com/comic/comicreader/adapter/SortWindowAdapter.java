package com.comic.comicreader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.comic.comicreader.R;

public class SortWindowAdapter extends BaseAdapter {

    private final String[] sort = { "时间 - 升序", "大小 - 升序", "名称 - 升序",
                                    "时间 - 降序", "大小 - 降序", "名称 - 降序"};

    private final LayoutInflater inflater;
    Context context;
    Integer selector;


    public SortWindowAdapter(Context context, int selector) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.selector = selector;
    }

    @Override
    public int getCount() {
        return sort.length;
    }

    @Override
    public CharSequence getItem(int position) {
        return sort[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sub_layout_sort_icon, parent, false);
        }
        if (selector == null) {
            setSelector(0);
            convertView.setBackgroundResource(R.drawable.ic_shape_rectangle5);
        } else if (selector == position) {
            convertView.setBackgroundResource(R.drawable.ic_shape_rectangle5);
        } else
            convertView.setBackgroundColor(Color.TRANSPARENT);
        TextView decText = convertView.findViewById(R.id.lv_tools_button_sort_options_text);
        decText.setText(getItem(position));

        return convertView;
    }

    public void setSelector(int position) {
        this.selector = position;
    }

    public Integer getSelector() {
        return selector;
    }
}
