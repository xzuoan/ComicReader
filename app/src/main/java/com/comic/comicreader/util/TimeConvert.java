package com.comic.comicreader.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.regex.Pattern;

public class TimeConvert {

    private final String dateFormat = "yyyy-MM-dd HH:mm";
    private final Pattern patternTimestamp = Pattern.compile("[12][0-9]{11,}");
    private final Pattern patternDate = Pattern.compile("[12][0-9]{3}-[0-9]{2}-[0-9]{2} [0-2][0-9]:[0-9]{2}");

    public long getTime() {
        long timestamp = System.currentTimeMillis();
        return Long.parseLong(String.valueOf(timestamp).substring(0, 12));
    }

    public String timestamp2Date(long timeStamp) throws IllegalFormatException {

        if (!String.valueOf(timeStamp).matches("[1|2][0-9]{11,}"))
            throw new IllegalArgumentException();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.ROOT);
        Date date = new Date(timeStamp);
        return simpleDateFormat.format(date);
    }

    public String timeStamp2Date(String timestamp) throws IllegalFormatException {

        if (!patternTimestamp.matcher(timestamp).matches())
            throw new  IllegalArgumentException();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.ROOT);
        long timestampLong = Long.parseLong(timestamp);
        Date date = new Date(timestampLong);
        return simpleDateFormat.format(date);
    }

    public long date2Timestamp(String date) throws IllegalArgumentException, ParseException {

        if (!patternDate.matcher(date).matches())
            throw new IllegalArgumentException();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.ROOT);
        Date dateParse = simpleDateFormat.parse(date);
        assert dateParse != null;
        return dateParse.getTime();
    }

}
