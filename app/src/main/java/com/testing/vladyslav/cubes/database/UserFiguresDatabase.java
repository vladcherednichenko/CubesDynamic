package com.testing.vladyslav.cubes.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.testing.vladyslav.cubes.database.entities.UserModel;

@Database(entities = {UserModel.class}, version = 1)
public abstract class UserFiguresDatabase extends RoomDatabase {
    public abstract IDataUserFiguresAccessObject daoAccess();
}
