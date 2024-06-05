package com.comic.comicreader.foldermanagepage;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.comic.comicreader.R;
import com.comic.comicreader.adapter.FolderManageAdapter;
import com.comic.comicreader.util.DataOperate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class FragmentFolderManage extends Fragment {

    ListView listView;
    ArrayList<FolderInfo> folderList;
    FolderManageAdapter folderManageAdapter;
    private static final String recordPath = "record.json";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View folderView = inflater.inflate(R.layout.fragment_layout_folder_manage, container, false);
        listView = folderView.findViewById(R.id.lv_folder_manage_list_view);
        readFolderList();
        folderManageAdapter = new FolderManageAdapter(getContext(), folderList);
        listView.setAdapter(folderManageAdapter);
        TextView textDescription = folderView.findViewById(R.id.lv_folder_manage_description);
        if (folderList.isEmpty()) {
            textDescription.setText("还没有文件夹, 点击添加路径");
        } else textDescription.setText("点击管理文件夹, 长按可删除选项");

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Tip")
                        .setMessage("确定删除该条选项吗?\n" + folderList.get(position).getPath())
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                folderList.remove(position);
                                listView.setAdapter(new FolderManageAdapter(getContext(), folderList));
                                Toast.makeText(getContext(), position+1 + ".删除成功", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            }
        });

        Button buttonSave = folderView.findViewById(R.id.lv_folder_manage_button_save);
        buttonSave.setVisibility(View.VISIBLE);
        buttonSave.setClickable(true);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateLocalFolderList();
                    Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getContext(), "保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return folderView;
    }

    public void refresh() {
        readFolderList();
        listView.setAdapter(new FolderManageAdapter(getContext(), folderList));
    }

    private void readFolderList() {
        if (folderList == null) {
            folderList = new ArrayList<>();
        }
        try {
            DataOperate dataOperate = new DataOperate(requireContext());
            if (!dataOperate.isInternalStorageExists("CACHE", recordPath)) {
                dataOperate.copyFromAssets(recordPath, "CACHE", recordPath);
            }
            String dataRecord = dataOperate.readInternalCache(recordPath);
            JSONArray jsonArray = (JSONArray) new JSONObject(dataRecord).get("data");
            ArrayList<FolderInfo> tempArrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = new JSONObject(String.valueOf(jsonArray.get(i)));
            FolderInfo folderInfo = new FolderInfo(
                (String) object.get("Path"),
                (Boolean) object.get("State"));
            tempArrayList.add(folderInfo);
            }
            folderList = tempArrayList;
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendFolderList(String path, boolean state) throws JSONException {
        FolderInfo folderInfo = new FolderInfo(path, state);
        folderList.add(folderInfo);
    }

    public void updateLocalFolderList() throws IOException {
        DataOperate dataOperate = new DataOperate(requireContext());
        String folderListData = "{\n" + "\"data\": " + folderList.toString() + "\n}";
        dataOperate.writeInternalCache(recordPath, folderListData);
    }

    public ArrayList<FolderInfo> getFolderList() {
        return folderList;
    }
}