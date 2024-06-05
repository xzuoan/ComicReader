package com.comic.comicreader.foldermanagepage;

import androidx.annotation.NonNull;

public class FolderInfo {

    String path;
    Boolean state;

    public FolderInfo(String path, Boolean state) {
        this.path = path;
        this.state = state;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    @NonNull
    @Override
    public String toString() {
        return  "\n{\n" +
                "  \"Path\": " + "\"" + getPath().replace("\\", "\\\\") + "\"" + ",\n" +
                "  \"State\": " + getState() + "\n" +
                "}\n";
    }
}
