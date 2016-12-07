package es.uma.ecplusproject.ecplusandroidapp.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by francis on 6/12/16.
 */

public interface DatabaseVersionChange {
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
