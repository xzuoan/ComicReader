package com.comic.comicreader.util;

import java.text.DecimalFormat;

public class StoreConvert {

    private enum TYPE {
        BYTE, KB, MB, GB, OVER
    }

    static final long BYTE = 8L / 8;
    static final long KB = BYTE * 1024;
    static final long MB = KB * 1024;
    static final long GB = MB * 1024;

    private TYPE getStoreType(String string) {

        string = string.toLowerCase();
        if (string.endsWith("bytes") || string.endsWith("byte")) {
            return TYPE.BYTE;
        }
        else if (string.endsWith("kb")) {
            return TYPE.KB;
        }
        else if (string.endsWith("mb")) {
            return TYPE.MB;
        }
        else if (string.endsWith("gb")) {
            return TYPE.GB;
        }
        return TYPE.OVER;
    }

    public long getStore(String string) {

        float store = Float.parseFloat(string.split("[a-zA-Z]+")[0]);
        switch (getStoreType(string)) {
            case BYTE:
                return Long.parseLong(string.split("[a-zA-Z]+")[0]);
            case KB:
                return Math.round(store * KB);
            case MB:
                return Math.round(store * MB);
            case GB:
                return Math.round(store * GB);
            case OVER:
                return 0;
        }
        return 0;
    }

    public String getStore(long length) {

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        if (length >= GB) {
            return decimalFormat.format(Float.parseFloat(length / GB + "." + length % GB)) + "GB";
        }
        else if (length >= MB) {
            return decimalFormat.format(Float.parseFloat(length / MB + "." + length % MB)) + "MB";
        }
        else if (length >= KB) {
            return new DecimalFormat("#.0").format(Float.parseFloat(length / KB + "." + length % KB)) + "KB";
        }
        else {
            return length + "bytes";
        }
    }
}
