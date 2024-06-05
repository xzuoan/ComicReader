package com.comic.comicreader.homepage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;


public class MangaInfo implements Serializable {
    private String MangaName;
    private String MangaPath;
    private JSONArray imageNames;
    private String createTime;
    private String fileSize;
    private int pages;
    private int readPage;
    private boolean hasRead;

    public MangaInfo(String mangaName,
                     String mangaPath,
                     JSONArray imageNames,
                     String createTime,
                     String fileSize,
                     int pages,
                     int readPage,
                     boolean hasRead) {
                        this.MangaName = mangaName;
                        this.MangaPath = mangaPath;
                        this.imageNames = imageNames;
                        this.createTime = createTime;
                        this.fileSize = fileSize;
                        this.pages = pages;
                        this.readPage = readPage;
                        this.hasRead = hasRead;
                    }

    public String getMangaName() {
        return MangaName;
    }

    public String getMangaPath() {
        return MangaPath;
    }

    public JSONArray getImageNames() {
        return imageNames;
    }

    public String getImageNames(JSONArray jsonArray) throws JSONException {
        StringBuilder imageNames = new StringBuilder("[\n");
        for (int i = 0; i < jsonArray.length() - 1; i++) {
            imageNames.append("\t\"").append(jsonArray.get(i)).append("\"").append(",\n");
        }
        imageNames.append("\t\"").append(jsonArray.get(jsonArray.length()-1)).append("\"").append("\n");
        imageNames.append("\t]");
        return imageNames.toString();
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getFileSize() {
        return fileSize;
    }

    public int getPages() {
        return pages;
    }

    public int getReadPage() {
        return readPage;
    }

    public float getProgress() {
        return (float) getReadPage() / getPages();
    }

    public boolean getHasRead() {
        return hasRead;
    }



    public void setMangaName(String mangaName) {
        this.MangaName = mangaName;
    }

    public void setMangaPath(String mangaPath) {
        this.MangaPath = mangaPath;
    }

    public void setImageNames(JSONArray imageNames) {
        this.imageNames = imageNames;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public void setReadPage(int readPage) {
        this.readPage = readPage;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    @NonNull
    @Override
    public String toString() {
        try {
            return "\n{\n" +
                    "  \"MangaName\": " + "\"" + getMangaName() + "\"" + ",\n" +
                    "  \"MangaPath\": " + "\"" + getMangaPath().replace("\\", "\\\\") + "\"" + ",\n" +
                    "  \"imageNames\": " + getImageNames(getImageNames()) + ",\n" +
                    "  \"createTime\": " + "\"" + getCreateTime() + "\"" + ",\n" +
                    "  \"fileSize\": " + "\"" + getFileSize() + "\"" + ",\n" +
                    "  \"pages\": " + getPages() + ",\n" +
                    "  \"readPage\": " + getReadPage() + ",\n" +
                    "  \"hasRead\": " + getHasRead() + "\n" +
                    "}\n";
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (obj instanceof MangaInfo) {
            return this.getMangaName().compareTo(((MangaInfo) obj).getMangaName()) == 0;
        }
        return false;
    } 
}
