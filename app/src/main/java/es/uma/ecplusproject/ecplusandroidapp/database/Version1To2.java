package es.uma.ecplusproject.ecplusandroidapp.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by francis on 6/12/16.
 */

public class Version1To2 extends DatabaseVersionChangeAdapter {

    public Version1To2() {
        super(1,2);
    }

    @Override
    protected void upgrade(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE "+ECPlusDBContract.Palabra.TABLE_NAME
                +" ADD "+ECPlusDBContract.Palabra.AVANZADA+" BIT(1) DEFAULT 0");

        db.execSQL("ALTER TABLE "+ECPlusDBContract.Sindrome.TABLE_NAME
                +" ADD "+ECPlusDBContract.Sindrome.TIPO+" VARCHAR(255) DEFAULT 'SINDROME'");

        resetHashesWordList(db);
        resetHashesSyndromeList(db);
    }

    private void resetHashesWordList(SQLiteDatabase db) {
        ContentValues values= new ContentValues();
        values.put(ECPlusDBContract.HashesListaPalabras.HASH,"");
        db.update(ECPlusDBContract.HashesListaPalabras.TABLE_NAME,values,"",null);

        values = new ContentValues();
        values.put(ECPlusDBContract.HashesPalabra.HASH, "");
        db.update(ECPlusDBContract.HashesPalabra.TABLE_NAME, values, "", null);
    }

    private void resetHashesSyndromeList(SQLiteDatabase db) {
        ContentValues values= new ContentValues();
        values.put(ECPlusDBContract.ListaSindromes.HASH, "");
        db.update(ECPlusDBContract.ListaSindromes.TABLE_NAME,values,"",null);

        values = new ContentValues();
        values.put(ECPlusDBContract.Sindrome.HASH, "");
        db.update(ECPlusDBContract.Sindrome.TABLE_NAME, values, "", null);
    }

    @Override
    protected void downgrade(SQLiteDatabase db) {
        // TODO: we cannot remove columns in SQLite

    }
}
