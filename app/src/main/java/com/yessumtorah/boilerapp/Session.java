package com.yessumtorah.boilerapp;

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;

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

    public Session() {

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



}
