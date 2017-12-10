package com.example.dfz.myapplication.MUtils;

/**
 * Created by hp on 2017/12/9.
 */

public class TimeFormat {

    private long timeS;
    private String timeFormat;

    public TimeFormat(long timeMs) {
        this.timeS = timeMs / 1000;
    }

    public long toSecond() {
        return timeS;
    }

    public String toTimeFormat() {
        int minute = (int) (timeS / 60);
        int second = (int) (timeS % 60);
        String minuteFormat, secondFormat;
        if (minute < 10)
            minuteFormat = "0" + minute;
        else
            minuteFormat = Integer.toString(minute);
        if (second < 10)
            secondFormat = "0" + second;
        else
            secondFormat = Integer.toString(second);
        timeFormat = minuteFormat + ":" + secondFormat;
        return timeFormat;
    }
}
