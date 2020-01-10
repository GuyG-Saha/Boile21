package com.yessumtorah.boilerapp;

import java.io.Serializable;
import java.sql.Time;
import java.time.Duration;
import java.time.Instant;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Session implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "started_by")
    private String user;
    @ColumnInfo(name = "total_time")
    private int totalTime;
    @ColumnInfo(name = "started_at")
    private String date;

   // private localTime startTime;

    private static final int DATE_PARSE_INDEX = 19;
    public static final int SECOND = 1000;
    public static final int MINUTE = 60;

    public Session() {
    this.user = "Guy"; // Default for demo use
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public String getDate() {return date.toString();}

    public void setDate(String date) {
        this.date = date;
    }

    /** This method is to be used by toString(). Consider switching totalTime to Time object **/
    private Time convertToDisplayTime(int totalTime) {
        return new Time(totalTime / (MINUTE * MINUTE), (totalTime/MINUTE) % MINUTE, totalTime%MINUTE);
    }

    public String toString() {
        try {
            return date.substring(0, DATE_PARSE_INDEX) + ", " + convertToDisplayTime(totalTime) + ", " + user;
        } catch (NullPointerException e) { // Handles first records saved with null date
            return date + ", " + convertToDisplayTime(totalTime) + ", " + user;
        }
    }



}
