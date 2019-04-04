package com.yessumtorah.boilerapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;
import android.provider.ContactsContract;

@Dao
public interface DaoAccess {

    @Insert
    Long insertSession(Session session);

    @Update
    void updateTask(Session session);

    @Delete
    void deleteTask(Session session);

}
