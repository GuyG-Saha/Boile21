package com.yessumtorah.boilerapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.provider.ContactsContract;

@Database(entities = {Session.class}, version = 1, exportSchema = false)
public abstract class SessionDatabase extends RoomDatabase {

    public abstract DaoAccess daoAccess();
}