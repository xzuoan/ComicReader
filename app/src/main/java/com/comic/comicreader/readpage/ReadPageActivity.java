package com.comic.comicreader.readpage;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.comic.comicreader.R;
import com.comic.comicreader.adapter.ReadPageToLineAdapter;
import com.comic.comicreader.util.DataOperate;

import java.io.IOException;
import java.util.ArrayList;

public class ReadPageActivity extends AppCompatActivity {

    ArrayList<byte[]> imageSourceList;
    ListView listView;
    SeekBar brightnessBar;
    int initialReadPage = 1;
    boolean trackScroll = true;
    boolean usingPopBar = true;
    boolean trackSystemBrightness = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_readpage);

        listView = findViewById(R.id.lv_read_page_list_view);
        TextView titleView = findViewById(R.id.lv_read_page_title);
        TextView progressView = findViewById(R.id.lv_read_page_progress);
        ImageView backView = findViewById(R.id.lv_read_page_button_back_to_homepage);
        SeekBar progressBar = findViewById(R.id.lv_read_page_seekbar_progress);
        brightnessBar = findViewById(R.id.lv_read_page_seekbar_brightness);
        CheckBox checkBox = findViewById(R.id.lv_read_page_check_box);

        Intent intent = getIntent();
        String filename = intent.getStringExtra("FILENAME");
        String filePath = intent.getStringExtra("FILE_PATH");
        int readPage = intent.getIntExtra("READ_PAGE", initialReadPage);
        readPage = readPage == 0 ? initialReadPage : readPage;
        int totalPages = intent.getIntExtra("PAGES", initialReadPage);

        titleView.setText(filename);
        checkBox.setChecked(trackSystemBrightness);
        progressView.setText(String.join("/", String.valueOf(readPage), String.valueOf(totalPages)));
        progressBar.setProgress(Math.round(((float) initialReadPage / totalPages) * progressBar.getMax()));
        brightnessBar.setProgress((int) Math.ceil(getScreenBrightness(ReadPageActivity.this) / 255.0f * progressBar.getMax()));

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadPageActivity.this.finish();
            }
        });

        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int currentBrights = (int) ((float) progress / seekBar.getMax() * 255);
                setAppScreenBrightness(currentBrights, trackSystemBrightness);
                checkBox.setChecked(trackSystemBrightness);
                if (trackSystemBrightness) {
                    trackSystemBrightness = false;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    trackSystemBrightness = true;
                    int systemBrights = (int) Math.ceil(getScreenBrightness(ReadPageActivity.this) / 255.0f * progressBar.getMax());
                    brightnessBar.setProgress(systemBrights, true);
                }
            }
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int currentPage = (int) Math.floor(((float) progress / seekBar.getMax()) * totalPages);
                progressView.setText(String.join("/", String.valueOf(currentPage), String.valueOf(totalPages)));
                if (trackScroll) {
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            listView.smoothScrollToPosition(currentPage);
                        }
                    });
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int currentPage = listView.getLastVisiblePosition();
                trackScroll = false;
                progressBar.setProgress(Math.round(((float) currentPage / totalPages) * progressBar.getMax()), true);
                trackScroll = true;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            private final LinearLayout topLayout = (LinearLayout) findViewById(R.id.lv_read_page_pop_bar_top);
            private final LinearLayout bottomLayout = (LinearLayout) findViewById(R.id.lv_read_page_pop_bar_bottom);
            private final LinearLayout leftLayout = (LinearLayout) findViewById(R.id.lv_read_page_pop_bar_left);
            private void popAnimation() {
                final TranslateAnimation topPopTranslateAnimation = new TranslateAnimation(
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, -1,
                        TranslateAnimation.RELATIVE_TO_SELF, 0);
                topPopTranslateAnimation.setDuration(250);
                topPopTranslateAnimation.setFillAfter(true);

                final TranslateAnimation bottomPopTranslateAnimation = new TranslateAnimation(
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, 1,
                        TranslateAnimation.RELATIVE_TO_SELF, 0);
                bottomPopTranslateAnimation.setDuration(250);
                bottomPopTranslateAnimation.setFillAfter(true);

                final TranslateAnimation leftPopTranslateAnimation = new TranslateAnimation(
                        TranslateAnimation.RELATIVE_TO_SELF, -1,
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, 0);
                leftPopTranslateAnimation.setDuration(250);
                leftPopTranslateAnimation.setFillAfter(true);

                topLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        topLayout.startAnimation(topPopTranslateAnimation);
                    }
                }, 100);
                bottomLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomLayout.startAnimation(bottomPopTranslateAnimation);
                    }
                }, 100);
                leftLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        leftLayout.startAnimation(leftPopTranslateAnimation);
                    }
                }, 100);
            }

            private void foldAnimation() {
                final TranslateAnimation topFoldTranslateAnimation = new TranslateAnimation(
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, -1);
                topFoldTranslateAnimation.setDuration(250);
                topFoldTranslateAnimation.setFillAfter(true);

                final TranslateAnimation bottomFoldTranslateAnimation = new TranslateAnimation(
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, 1);
                bottomFoldTranslateAnimation.setDuration(250);
                bottomFoldTranslateAnimation.setFillAfter(true);

                final TranslateAnimation leftFoldTranslateAnimation = new TranslateAnimation(
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, -1,
                        TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, 0);
                leftFoldTranslateAnimation.setDuration(250);
                leftFoldTranslateAnimation.setFillAfter(true);

                topLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        topLayout.startAnimation(topFoldTranslateAnimation);
                    }
                }, 100);
                bottomLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomLayout.startAnimation(bottomFoldTranslateAnimation);
                    }
                }, 100);
                leftLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        leftLayout.startAnimation(leftFoldTranslateAnimation);
                    }
                }, 100);
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (usingPopBar) {
                    foldAnimation();
                    usingPopBar = false;
                    progressBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    }, 251);
                    brightnessBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            brightnessBar.setVisibility(View.GONE);

                        }
                    }, 251);
                    checkBox.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkBox.setVisibility(View.GONE);
                        }
                    }, 251);
                    backView.setClickable(false);
                }
                else {
                    popAnimation();
                    usingPopBar = true;
                    progressBar.setVisibility(View.VISIBLE);
                    brightnessBar.setVisibility(View.VISIBLE);
                    checkBox.setVisibility(View.VISIBLE);
                    backView.setClickable(true);
                }
            }
        });

        imageSourceList = new ArrayList<>();
        ReadPageToLineAdapter readPageToLineAdapter = new ReadPageToLineAdapter(this, imageSourceList);
        listView.setAdapter(readPageToLineAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_layout_loading);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    imageSourceList.addAll(new DataOperate(ReadPageActivity.this).readZip(filePath));
                } catch (IOException e) {
                    Toast.makeText(ReadPageActivity.this, "ERROR: 资源文件异常", Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
                dialog.dismiss();
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(readPageToLineAdapter);
                        listView.deferNotifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private int getScreenBrightness(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        // 当系统设置获取内容为空时的默认返回值，Brightness(0-255)
        int defVal = 125;
        return Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, defVal);
    }

    private void setAppScreenBrightness(int brightnessValue, boolean usingPreferred) {
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if (usingPreferred) {
            layoutParams.screenBrightness = -1.0f;
        } else {
            layoutParams.screenBrightness = brightnessValue / 255.0f;
        }
        window.setAttributes(layoutParams);
    }

}