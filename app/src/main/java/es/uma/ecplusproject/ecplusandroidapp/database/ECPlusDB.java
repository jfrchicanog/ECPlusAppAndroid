package es.uma.ecplusproject.ecplusandroidapp.database;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by francis on 8/6/16.
 */
public class ECPlusDB {

    private static SQLiteDatabase database;
    private static ECPlusDBHelper ecplusDBHelper;

    public static synchronized SQLiteDatabase getDatabase() {
        if (database == null) {
            throw new IllegalStateException("Context not established");
        }
        return database;
    }

    public static synchronized  void setContext(Context context) {
        if (database == null) {
            ecplusDBHelper = new ECPlusDBHelper(context);
            database = ecplusDBHelper.getWritableDatabase();
        }
    }

}
