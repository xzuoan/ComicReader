package com.comic.comicreader.homepage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.comic.comicreader.R;
import com.comic.comicreader.readpage.ReadPageActivity;
import com.comic.comicreader.adapter.HomePageToLineAdapter;
import com.comic.comicreader.adapter.HomePageToRowAdapter;
import com.comic.comicreader.util.DataOperate;
import com.comic.comicreader.util.Sort;
import com.comic.comicreader.util.StoreConvert;
import com.comic.comicreader.util.TimeConvert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.zip.ZipFile;


public class FragmentHomePageContent extends Fragment implements AdapterView.OnItemClickListener {

    GridView gridView;
    int columns;
    ArrayList<MangaInfo> mangaInfoArrayList;
    ListAdapter adapter;
    private static final String dataPath = "data.json";
    DataOperate dataOperate;

    public ArrayList<MangaInfo> getMangaInfoArrayList() {
        return mangaInfoArrayList;
    }

    public void setMangaInfoArrayList(ArrayList<MangaInfo> mangaInfoArrayList) {
        this.mangaInfoArrayList = mangaInfoArrayList;
    }

    public void initView(Context context) {
        this.dataOperate = new DataOperate(context);
        this.mangaInfoArrayList = new ArrayList<>();
        this.columns = 1;
        this.adapter = new HomePageToLineAdapter(context, mangaInfoArrayList, lineClickListener);
    }
    public void initView(Context context, int columns) {
        this.dataOperate = new DataOperate(context);
        int viewWidth = gridView.getWidth() - gridView.getPaddingLeft() - gridView.getPaddingRight() - gridView.getVerticalSpacing();
        this.columns = columns;
        this.mangaInfoArrayList = new ArrayList<>();
        HomePageToRowAdapter homePageToRowAdapter = new HomePageToRowAdapter(context, mangaInfoArrayList, rowClickListener);
        homePageToRowAdapter.setScaleValue((float) viewWidth/columns/homePageToRowAdapter.getDefaultViewWidth(context));
        this.adapter = homePageToRowAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout_homepage_content, container, false);
        // 加载GridView并动态修改格式内容
        gridView = view.findViewById(R.id.lv_homepage_grid_view);
        initView(getContext());
        gridView.setNumColumns(columns);
        gridView.setAdapter(adapter);

        loadMangaInfo();
        gridView.deferNotifyDataSetChanged();
        TextView textDescription = view.findViewById(R.id.lv_homepage_grid_description);
        if (mangaInfoArrayList.isEmpty()) {
            textDescription.setText("还没有内容, 右划添加文件");
        } else textDescription.setText("主页");
        gridView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(requireContext(), ReadPageActivity.class);
        MangaInfo itemMangaInfo = mangaInfoArrayList.get(position);
        intent.putExtra("FILENAME", itemMangaInfo.getMangaName());
        intent.putExtra("READ_PAGE", itemMangaInfo.getReadPage());
        intent.putExtra("PAGES", itemMangaInfo.getPages());
        intent.putExtra("HAS_READ", itemMangaInfo.getHasRead());
        intent.putExtra("FILE_PATH", itemMangaInfo.getMangaPath());
        startActivity(intent);
    }

    private final HomePageToLineAdapter.OnOptionClickListener lineClickListener = new HomePageToLineAdapter.OnOptionClickListener() {
        @Override
        public void OnOptionClick(View view, int position) {
            itemOperate(view, position, "LINE");
        }
    };

    private final HomePageToRowAdapter.OnOptionClickListener rowClickListener = new HomePageToRowAdapter.OnOptionClickListener() {
        @Override
        public void OnOptionClick(View view, int position) {
            itemOperate(view, position, "ROW");
        }
    };

    private void itemOperate(View view, int position, String style) {
        View operateView = LayoutInflater.from(getContext()).inflate(R.layout.sub_layout_content_operate, null);
        PopupWindow window = new PopupWindow(operateView, dpi2px(100), dpi2px(51));
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.showAsDropDown(view);
        operateView.findViewById(R.id.content_operate_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mangaInfoArrayList.remove(position);
                try {
                    updateLocalMangaInfo();
                    refresh(getContext(), style, columns);
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();;
                }
                window.dismiss();
            }
        });
    }

    public void refresh(Context context, String style, int column) {
        if (style.equalsIgnoreCase("LINE"))
            initView(context);
        else if (style.equalsIgnoreCase("ROW"))
            initView(context, column);
        else {
            initView(context);
            columns = 3;
        }
        gridView.setNumColumns(columns);
        loadMangaInfo();
        gridView.setAdapter(adapter);
        gridView.invalidateViews();
    }

    public void refresh(Context context) {
        initView(context);
        gridView.setAdapter(adapter);
        gridView.invalidateViews();
    }

    public void refresh() {
        gridView.setAdapter(adapter);
        gridView.invalidateViews();
    }

    public void loadMangaInfo() {
        try {
//            dataOperate.removeInternalFile("CACHE", dataPath);
            if (!dataOperate.isInternalStorageExists("CACHE", dataPath)) {
                dataOperate.copyFromAssets(dataPath, "CACHE", dataPath);
            }
            String dataSource = dataOperate.readInternalCache(dataPath);
            JSONArray jsonArray = (JSONArray) new JSONObject(dataSource).get("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject info = new JSONObject(String.valueOf(jsonArray.get(i)));
                MangaInfo mangaInfo = new MangaInfo(
                        (String) info.get("MangaName"),
                        (String) info.get("MangaPath"),
                        (JSONArray) info.get("imageNames"),
                        (String) info.get("createTime"),
                        (String) info.get("fileSize"),
                        (int) info.get("pages"),
                        (int) info.get("readPage"),
                        (boolean) info.get("hasRead")
                );
                mangaInfoArrayList.add(mangaInfo);
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMangaInfo(ArrayList<String> MangaFiles) throws IOException {
        mangaInfoArrayList = new ArrayList<>();
        appendMangaInfo(MangaFiles);
        dataOperate.duplicate(mangaInfoArrayList);
    }

    public void appendMangaInfo(ArrayList<String> MangaFiles) throws IOException {
        for (String MangaFile: MangaFiles) {
            File file = new File(MangaFile);
            MangaInfo mangaInfo = new MangaInfo(
                    (String) file.getName(),
                    (String) file.getAbsolutePath(),
                    (JSONArray) new JSONArray().put(""),
                    (String) new TimeConvert().timestamp2Date(file.lastModified()),
                    (String) new StoreConvert().getStore(file.length()),
                    new ZipFile(file, ZipFile.OPEN_READ).size(),
                    0,
                    false
            );
            mangaInfoArrayList.add(mangaInfo);
        }
    }

    public void updateLocalMangaInfo() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n" + "  \"data\": ");
        String content = mangaInfoArrayList.toString();
        stringBuilder.append(content);
        stringBuilder.append("}");
        dataOperate.writeInternalCache(dataPath, stringBuilder.toString());
    }

    public ArrayList<MangaInfo> sort(ArrayList<MangaInfo> arrayList, Sort sortBy) {
        if (arrayList == null)
            return new ArrayList<>();
        final TimeConvert timeConvert = new TimeConvert();
        final StoreConvert storeConvert = new StoreConvert();
        switch (sortBy) {
            case BY_DATE_ASC:
                arrayList.sort(new Comparator<MangaInfo>() {

                    @Override
                    public int compare(MangaInfo o1, MangaInfo o2) {
                        try {
                            return (int) (timeConvert.date2Timestamp(o1.getCreateTime()) - timeConvert.date2Timestamp(o2.getCreateTime()));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                break;
            case BY_DATE_DESC:
                arrayList.sort(new Comparator<MangaInfo>() {
                    @Override
                    public int compare(MangaInfo o1, MangaInfo o2) {
                        try {
                            return (int) (timeConvert.date2Timestamp(o2.getCreateTime()) - timeConvert.date2Timestamp(o1.getCreateTime()));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                break;
            case BY_STORE_SIZE_ASC:
                arrayList.sort(new Comparator<MangaInfo>() {
                    @Override
                    public int compare(MangaInfo o1, MangaInfo o2) {
                        return (int) (storeConvert.getStore(o1.getFileSize()) - storeConvert.getStore(o2.getFileSize()));
                    }
                });
                break;
            case BY_STORE_SIZE_DESC:
                arrayList.sort(new Comparator<MangaInfo>() {
                    @Override
                    public int compare(MangaInfo o1, MangaInfo o2) {
                        return (int) (storeConvert.getStore(o2.getFileSize()) - storeConvert.getStore(o1.getFileSize()));
                    }
                });
                break;
            case BY_NAME_ASC:
                arrayList.sort(new Comparator<MangaInfo>() {
                    @Override
                    public int compare(MangaInfo o1, MangaInfo o2) {
                        return o1.getMangaName().hashCode() - o2.getMangaName().hashCode();
                    }
                });
                break;
            case BY_NAME_DESC:
                arrayList.sort(new Comparator<MangaInfo>() {
                    @Override
                    public int compare(MangaInfo o1, MangaInfo o2) {
                        return o2.getMangaName().hashCode() - o1.getMangaName().hashCode();
                    }
                });
                break;
        }
        return arrayList;
    }

    public int dpi2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}