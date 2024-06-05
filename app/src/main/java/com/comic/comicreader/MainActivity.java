package com.comic.comicreader;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.comic.comicreader.adapter.SortWindowAdapter;
import com.comic.comicreader.foldermanagepage.FolderGlanceDialog;
import com.comic.comicreader.foldermanagepage.FolderInfo;
import com.comic.comicreader.foldermanagepage.FragmentFolderManage;
import com.comic.comicreader.homepage.FragmentHomePageContent;
import com.comic.comicreader.adapter.PagerAdapter;
import com.comic.comicreader.homepage.MangaInfo;
import com.comic.comicreader.homepage.ScrollerLayout;
import com.comic.comicreader.util.DataOperate;
import com.comic.comicreader.util.Sort;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    List<Fragment> fragmentList = new ArrayList<>();
    ImageButton buttonMore, buttonClose, buttonRedisplay, buttonSort;
    ImageView imageViewMenu;
    LinearLayout homepageLinear;
    RelativeLayout homepageContent;
    int screenWidth;
    int screenHeight;
    ImageView searchView;
    EditText searchbar;
    ImageView moreView;
    boolean isImageButtonOpen = false;
    boolean isDisplayButtonOpen = true;
    String defaultDisplayStyle = "LINE";
    private static final int ROW_COLUMNS = 3;
    ArrayList<MangaInfo> mangaInfoArrayList;
    int defaultSelector = 0;
    FragmentHomePageContent fragmentHomePageContent;
    FragmentFolderManage fragmentFolderManage;

    static  Map<Integer, Sort> sortMode = new HashMap<Integer, Sort>() {
        {
            put(0, Sort.BY_DATE_ASC);
            put(1, Sort.BY_STORE_SIZE_ASC);
            put(2, Sort.BY_NAME_ASC);
            put(3, Sort.BY_DATE_DESC);
            put(4, Sort.BY_STORE_SIZE_DESC);
            put(5, Sort.BY_NAME_DESC);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.lv_homepage_tab_layout);
        viewPager = findViewById(R.id.lv_homepage_viewpager);
        buttonMore = findViewById(R.id.lv_tools_button_more);
        imageViewMenu = findViewById(R.id.lv_tools_menu);
        homepageLinear = findViewById(R.id.homepage_linear_layout);
        homepageContent = findViewById(R.id.homepage_content);
        searchView = findViewById(R.id.lv_tools_search);
        searchbar = findViewById(R.id.lv_tools_searchbar);
        moreView = findViewById(R.id.lv_tools_more);
        buttonClose = findViewById(R.id.lv_tools_button_close);
        buttonRedisplay = findViewById(R.id.lv_tools_button_redisplay);
        buttonSort = findViewById(R.id.lv_tools_button_sort);
        fragmentHomePageContent = new FragmentHomePageContent();
        fragmentFolderManage = new FragmentFolderManage();
        fragmentList.add(fragmentHomePageContent);
        fragmentList.add(fragmentFolderManage);
        checkPermission();
        onCreatePager();
        getScreenSize();
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            switch (i) {
                case 0: {
                    Objects.requireNonNull(tabLayout.getTabAt(i))
                            .setIcon(R.mipmap.home_checked)
                            .setTag("TAG_HOME");
                    break;
                }
                case 1: {
                    Objects.requireNonNull(tabLayout.getTabAt(i))
                            .setIcon(R.mipmap.folder_add_unchecked)
                            .setTag("TAG_FOLDER");
                    break;
                }
            }
        }

        imageViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeftWindow(v);
            }
        });

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    Drawable drawable = ContextCompat.getDrawable(searchbar.getContext(), R.mipmap.close_mini);
                    if (drawable != null) drawable.setAlpha(125);
                    searchbar.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                }
                else {
                    searchbar.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageViewBack = findViewById(R.id.lv_tools_back);
                ViewGroup.LayoutParams tabLayoutParams = (ViewGroup.LayoutParams) tabLayout.getLayoutParams();
                ViewGroup.LayoutParams editTextLayoutParams = (ViewGroup.LayoutParams) searchbar.getLayoutParams();

                // 缩放动画预设
                Animation tabAnimation = AnimationUtils.loadAnimation(
                        tabLayout.getContext(),
                        R.anim.tab_scale_animation_from_right);
                tabAnimation.setFillAfter(true);
                tabLayout.startAnimation(tabAnimation);

                // 旋转动画预设
                Animation buttionAnimation = AnimationUtils.loadAnimation(
                        imageViewMenu.getContext(),
                        R.anim.menubutton_rotate_animation);
                buttionAnimation.setFillAfter(true);
                imageViewMenu.startAnimation(buttionAnimation);

                imageViewMenu.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageViewMenu.setAlpha(0.0f);
                        imageViewMenu.setClickable(false);
                        imageViewBack.setAlpha(1.0f);
                        imageViewBack.setClickable(true);
                        imageViewMenu.clearAnimation();
                    }
                }, 500);

                tabLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabLayoutParams.width = 0;
                        tabLayout.setLayoutParams(tabLayoutParams);
                        tabLayout.clearAnimation();
                    }
                }, 500);

                searchbar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editTextLayoutParams.width = dpi2px(240);
                        searchbar.setLayoutParams(editTextLayoutParams);
                        searchbar.setVisibility(View.VISIBLE);
                        searchbar.setClickable(true);
                        searchbar.requestFocus();
                        openKeyboard(searchbar.getContext(), searchbar);
                    }
                }, 500);


                imageViewBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 回旋
                        Animation backAnimation = AnimationUtils.loadAnimation(
                                imageViewBack.getContext(),
                                R.anim.backbutton_rotate_animation);
                        backAnimation.setFillAfter(true);
                        backAnimation.setStartOffset(500);
                        imageViewBack.startAnimation(backAnimation);

                        // 回伸
                        Animation tabAnimation = AnimationUtils.loadAnimation(
                                tabLayout.getContext(),
                                R.anim.tab_scale_animation_from_left);
                        tabAnimation.setFillAfter(true);
                        tabAnimation.setStartOffset(500);
                        tabLayout.startAnimation(tabAnimation);

                        tabLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tabLayoutParams.width = dpi2px(240);
                                tabLayout.setLayoutParams(tabLayoutParams);
                            }
                        }, 200);

                        searchbar.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                editTextLayoutParams.width = 0;
                                searchbar.setLayoutParams(editTextLayoutParams);
                                searchbar.setVisibility(View.INVISIBLE);
                                searchbar.setClickable(false);
                                searchbar.setText("");
                            }
                        }, 200);

                        // 图标变换
                        imageViewBack.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                imageViewBack.setAlpha(0.0f);
                                imageViewBack.setClickable(false);
                                imageViewMenu.setAlpha(1.0f);
                                imageViewMenu.setClickable(true);
                                tabLayout.clearAnimation();
                                imageViewBack.clearAnimation();
                                // 收起键盘
                                closeKeyboard(searchbar.getContext(), searchbar);
                            }
                        }, 1000);
                    }
                });
            }
        });

        moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreWindow(v);
            }
        });

        buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更换图标，展开样式
                if (!isImageButtonOpen) {
                    deployAnimation();
                    buttonRedisplay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isDisplayButtonOpen) {
                                if (defaultDisplayStyle.equalsIgnoreCase("ROW")) {
                                    buttonRedisplay.setImageResource(R.mipmap.change_01);
                                    fragmentHomePageContent.refresh(MainActivity.this, "LINE", 0);
                                    defaultDisplayStyle = "LINE";
                                }
                                else {
                                    buttonRedisplay.setImageResource(R.mipmap.change_02);
                                    fragmentHomePageContent.refresh(MainActivity.this, "ROW", ROW_COLUMNS);
                                    defaultDisplayStyle = "ROW";
                                }
                            }
                            // 点击事件后收起按钮
                            foldAnimation();
                        }
                    });
                    buttonClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isDisplayButtonOpen) {
                                buttonClose.setImageResource(R.mipmap.eye_close_01);
                                fragmentHomePageContent.refresh(MainActivity.this);
                                isDisplayButtonOpen = false;
                            }
                            else {
                                buttonClose.setImageResource(R.mipmap.eye_01);
                                if (defaultDisplayStyle.equalsIgnoreCase("ROW")) {
                                    fragmentHomePageContent.refresh(MainActivity.this, "ROW", ROW_COLUMNS);
                                }
                                else {
                                    fragmentHomePageContent.refresh(MainActivity.this, "LINE", 0);
                                }
                                isDisplayButtonOpen = true;
                            }
                            foldAnimation();
                        }
                    });
                    buttonSort.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSortWindow(v, fragmentHomePageContent);
                        }
                    });
                }
                else {
                    foldAnimation();
                }
            }
        });
    }

    private void refreshView() {
        ArrayList<String> MangaFiles = getMangaFiles();
        try {
            fragmentHomePageContent.setMangaInfo(MangaFiles);
            fragmentHomePageContent.updateLocalMangaInfo();
        } catch (IOException e) {
            Toast.makeText(this, "ERROR: 更新内容失败", Toast.LENGTH_SHORT).show();
            return;
        }
        if (defaultDisplayStyle.equalsIgnoreCase("ROW")) {
            fragmentHomePageContent.refresh(MainActivity.this, "ROW", ROW_COLUMNS);
        }
        else {
            fragmentHomePageContent.refresh(MainActivity.this, "LINE", 0);
        }
    }

    private ArrayList<String> getMangaFiles() {
        ArrayList<FolderInfo> folderInfoArrayList = fragmentFolderManage.getFolderList();
        ArrayList<String> checkedFolderNames = new ArrayList<>();
        for (int i = 0; i < folderInfoArrayList.size(); i++) {
            if (folderInfoArrayList.get(i).getState()) {
                checkedFolderNames.add(folderInfoArrayList.get(i).getPath());
            }
        }
        DataOperate dataOperate = new DataOperate(MainActivity.this);
        dataOperate.duplicate(checkedFolderNames);
        return dataOperate.FileFilter(checkedFolderNames, "zip");
    }

    private void showMoreWindow(View view) {
        View v = LayoutInflater.from(this).inflate(R.layout.sub_layout_more, null);
        PopupWindow window = new PopupWindow(v, dpi2px(150), dpi2px(120));
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.showAsDropDown(view, px2dpi(0), px2dpi(20));

        View refreshView = v.findViewById(R.id.lv_tools_more_refresh);
        View addView = v.findViewById(R.id.lv_tools_more_add);
        View favoriteView = v.findViewById(R.id.lv_tools_more_favorite);
        View historyView = v.findViewById(R.id.lv_tools_more_history);
        ViewGroup.LayoutParams addViewLayoutParams = addView.getLayoutParams();
        switch (tabLayout.getSelectedTabPosition()) {
            case 0:
                addViewLayoutParams.height = 0;
                addView.setLayoutParams(addViewLayoutParams);
                break;
            case 1:
                addViewLayoutParams.height = dpi2px(30);
                addView.setLayoutParams(addViewLayoutParams);
                break;
        }
        ViewGroup.LayoutParams vLayoutParams = v.getLayoutParams();
        vLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        v.setLayoutParams(vLayoutParams);

        refreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        refreshView();
                        break;
                    case 1:
                        fragmentFolderManage.refresh();
                        break;
                }
                Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
            }
        });
        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                FolderGlanceDialog folderGlanceDialog = new FolderGlanceDialog(view.getContext(), R.layout.dialog_layout_folder_glance);
                window.dismiss();
                folderGlanceDialog.show();
                folderGlanceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (folderGlanceDialog.getOnCompleteClickEvent()) {
                            String selectedDirectory = folderGlanceDialog.getRootDirectory().getAbsolutePath();
                            try {
                                fragmentFolderManage.appendFolderList(selectedDirectory, true);
                                fragmentFolderManage.updateLocalFolderList();
                                fragmentFolderManage.refresh();
                            } catch (JSONException | IOException e) {
                                Toast.makeText(MainActivity.this, "添加文件路径失败\n" + selectedDirectory, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        favoriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "收藏", Toast.LENGTH_SHORT).show();
                window.dismiss();
            }
        });
        historyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "历史", Toast.LENGTH_SHORT).show();
                window.dismiss();
            }
        });
    }

    private void showSortWindow(View view, FragmentHomePageContent fragmentHomePageContent) {
        final int width = 300;
        final int height = 170;

        View v = LayoutInflater.from(this).inflate(R.layout.sub_layout_sort, null);
        PopupWindow window = new PopupWindow(v, dpi2px(width), dpi2px(height));
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.showAtLocation(view, Gravity.NO_GRAVITY, dpi2px((float) (px2dpi(screenWidth)-width)/2), dpi2px((float) (px2dpi(screenHeight)-height)/2));
        setBackgroundAlpha(0.3f);

        GridView sortView = v.findViewById(R.id.lv_tools_button_sort_options);
        sortView.setAdapter(new SortWindowAdapter(this, defaultSelector));

        sortView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SortWindowAdapter adapter = new SortWindowAdapter(sortView.getContext(), position);
                defaultSelector = position;
                adapter.setSelector(defaultSelector);
                sortView.setAdapter(adapter);
                sortView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        window.dismiss();
                    }
                }, 200);

                Sort sortBy = sortMode.get(defaultSelector);
                mangaInfoArrayList = fragmentHomePageContent.sort(fragmentHomePageContent.getMangaInfoArrayList(), sortBy);
                fragmentHomePageContent.setMangaInfoArrayList(mangaInfoArrayList);
                fragmentHomePageContent.refresh();
                try {
                    fragmentHomePageContent.updateLocalMangaInfo();
                    Toast.makeText(MainActivity.this, String.valueOf(sortBy), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
    }

    private void showLeftWindow(View view) {
        ScrollerLayout leftView = (ScrollerLayout) LayoutInflater.from(this).inflate(R.layout.drawer_layout_menu, null);
        PopupWindow window = new PopupWindow(leftView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

        window.setAnimationStyle(R.style.anim);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.showAtLocation(view, Gravity.START, 0, 0);
        setBackgroundAlpha(0.3f);
        leftView.findViewById(R.id.item_menu_sidebar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
        leftView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollX - oldScrollX > screenWidth * 0.1) {
                    window.dismiss();
                }
            }
        });
    }


    private void buttonAnimation(View view, float fromYValue, float toYValue, long duration) {

        // imageButton 弹出动效实现
        TranslateAnimation translateAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, fromYValue,
                TranslateAnimation.RELATIVE_TO_SELF, toYValue);
        // setDuration 过度动画时间
        translateAnimation.setDuration(duration);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
                view.startAnimation(translateAnimation);
            }
        }, 100);
        // 动画结束后保留结束状态
        translateAnimation.setFillAfter(true);
    }


    public void deployAnimation() {
        buttonMore.setImageResource(R.mipmap.more_02);
        isImageButtonOpen = true;

        buttonClose.postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonClose.setVisibility(View.VISIBLE);
                buttonClose.setClickable(true);
            }
        }, 700);
        buttonRedisplay.postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonRedisplay.setVisibility(View.VISIBLE);
                buttonRedisplay.setClickable(true);
            }
        }, 600);
        buttonSort.postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonSort.setVisibility(View.VISIBLE);
                buttonSort.setClickable(true);
            }
        }, 500);
        buttonAnimation(buttonSort, 4.0f, 0, 500);
        buttonAnimation(buttonRedisplay, 2.8f, 0, 600);
        buttonAnimation(buttonClose, 1.6f, 0, 700);
    }


    public void foldAnimation() {
        buttonMore.setImageResource(R.mipmap.more_03);
        isImageButtonOpen = false;
        buttonClose.postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonClose.setVisibility(View.INVISIBLE);
                buttonClose.setClickable(false);
                buttonClose.clearAnimation();
            }
        }, 501);
        buttonRedisplay.postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonRedisplay.setVisibility(View.INVISIBLE);
                buttonRedisplay.setClickable(false);
                buttonRedisplay.clearAnimation();
            }
        }, 601);
        buttonSort.postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonSort.setVisibility(View.INVISIBLE);
                buttonSort.setClickable(false);
                buttonSort.clearAnimation();
            }
        }, 701);
        buttonAnimation(buttonSort, 0, 4.0f, 700);
        buttonAnimation(buttonRedisplay, 0, 2.8f, 600);
        buttonAnimation(buttonClose, 0, 1.6f, 500);
    }

    private void onCreatePager() {

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragmentList);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        // 添加tab监听事件
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (Objects.equals(tab.getTag(), "TAG_HOME") ) {
                    tab.setIcon(R.mipmap.home_checked);
                    buttonAnimation(buttonMore, 2, 0, 250);
                    buttonMore.setClickable(true);
                } else if (Objects.equals(tab.getTag(), "TAG_FOLDER")) {
                    tab.setIcon(R.mipmap.folder_add_checked);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                if (Objects.equals(tab.getTag(), "TAG_HOME")) {
                    tab.setIcon(R.mipmap.home_unchecked);
                    buttonAnimation(buttonMore, 0, 2, 250);
                    buttonMore.setClickable(false);
                    if (isImageButtonOpen) {
                        foldAnimation();
                    }
                } else if (Objects.equals(tab.getTag(), "TAG_FOLDER")) {
                    tab.setIcon(R.mipmap.folder_add_unchecked);
                }
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.screenWidth = dm.widthPixels;
        this.screenHeight = dm.heightPixels;
    }

    public int dpi2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dpi(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public void openKeyboard(Context context, View editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, 0);
    }

    public void closeKeyboard(Context context, View editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void checkPermission() {
        final String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
        };
        for (String permission: permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {permission}, 1);
            }
        }
    }

    public void setBackgroundAlpha(float alphaValue) {
        WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.alpha = alphaValue;
        this.getWindow().setAttributes(layoutParams);
    }
}
