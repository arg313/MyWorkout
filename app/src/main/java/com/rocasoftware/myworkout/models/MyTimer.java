package com.rocasoftware.myworkout.models;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimer {

    double time;
    Timer timer;
    TimerTask timerTask;
    boolean timeStarted = true;

    public MyTimer() {
        timer = new Timer();
        time = 0.0;
    }

    public MyTimer(double time) {
        this.time = time;
        this.timer = new Timer();
    }

    protected MyTimer(Parcel in) {
        time = in.readDouble();
        timeStarted = in.readByte() != 0;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public TimerTask getTimerTask() {
        return timerTask;
    }

    public void setTimerTask(TimerTask timerTask) {
        this.timerTask = timerTask;
    }

    public boolean isTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(boolean timeStarted) {
        this.timeStarted = timeStarted;
    }

    public void startTimer(Activity activity, TextView timerText) {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(() -> {
                    time++;
                    if (timerText != null) timerText.setText(getTimerText());
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask,0,1000);
    }

    public String getTimerText() {
        int rounded = (int)Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    public String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }
}
