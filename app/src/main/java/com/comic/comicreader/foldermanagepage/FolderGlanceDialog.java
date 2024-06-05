package com.comic.comicreader.foldermanagepage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.comic.comicreader.R;
import com.comic.comicreader.adapter.FolderGlanceAdapter;
import com.comic.comicreader.util.DataOperate;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class FolderGlanceDialog extends Dialog {
    ArrayList<File> fileNameList;
    String[] sort = {"↓名称", "↑名称", "↓时间", "↑时间"};
    File rootDirectory;
    TextView countText;
    TextView pathText;
    ListView listView;
    boolean onCompleteClickEvent = false;

    public FolderGlanceDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        View dialogView =  getLayoutInflater().inflate(themeResId, null, false);
        View backView = dialogView.findViewById(R.id.lv_folder_glance_back_dir);
        TextView sortText = dialogView.findViewById(R.id.lv_folder_glance_sort);
        TextView cancelView = dialogView.findViewById(R.id.lv_folder_glance_cancel);
        TextView completeView = dialogView.findViewById(R.id.lv_folder_glance_complete);
        listView = dialogView.findViewById(R.id.lv_folder_glance_list_view);
        pathText = dialogView.findViewById(R.id.lv_folder_glance_path);
        countText = dialogView.findViewById(R.id.lv_folder_glance_count);

        DataOperate operate = new DataOperate(context);
        setRootDirectory(new File(operate.getPathExternalStorage()));
//        setRootDirectory(new File(operate.getPathInternalFiles()));
        initView(context, rootDirectory);
        sortText.setText(sort[0]);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File itemFile = fileNameList.get(position);
                if (itemFile.isDirectory()) {
                    setRootDirectory(itemFile);
                    initView(context, rootDirectory);
                }
            }
        });
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRootDirectory(rootDirectory.getParentFile());
                initView(context, rootDirectory);
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        completeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCompleteClickEvent = true;
                Toast.makeText(context, rootDirectory.getName(), Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (event.getAction()) {
                    case KeyEvent.KEYCODE_BACK & KeyEvent.ACTION_UP:
                        if (rootDirectory.getAbsolutePath().equals(operate.getPathExternalStorage())) {
                            dismiss();
                            break;
                        }
                        setRootDirectory(rootDirectory.getParentFile());
                        initView(context, rootDirectory);
                        break;
                }
                return true;
            }
        });
        setContentView(dialogView);
    }

    private void initView(Context context, File rootPath) {
        pathText.setText(rootPath.getAbsolutePath());
        int count;
        try {
            File[] files = Objects.requireNonNull(rootPath.listFiles());
            count = files.length;
            fileNameList = new ArrayList<>(Arrays.asList(files));
        } catch (NullPointerException e) {
            count = 0;
        }
        countText.setText(String.join("", String.valueOf(count), "项"));
        listView.setAdapter(new FolderGlanceAdapter(context, fileNameList));
    }

    public void setRootDirectory(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public boolean getOnCompleteClickEvent() {
        return onCompleteClickEvent;
    }
}
