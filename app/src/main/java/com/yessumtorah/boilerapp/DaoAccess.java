package com.yessumtorah.boilerapp;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    Long insertSession(Session session);

    @Update
    void updateTask(Session session);

    @Delete
    void deleteTask(Session session);

    @Query("SELECT * FROM Session")
    List<Session> listSessions();

    @Query("DELETE FROM Session")
    void deleteAll();

   /* @Query("SELECT started_by, total_time FROM Session")
    List<String> displaySessions();*/

}
