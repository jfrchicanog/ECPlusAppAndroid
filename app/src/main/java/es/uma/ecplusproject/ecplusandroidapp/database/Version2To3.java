package es.uma.ecplusproject.ecplusandroidapp.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by francis on 6/12/16.
 */

public class Version2To3 extends DatabaseVersionChangeAdapter {

    public Version2To3() {
        super(2,3);
    }

    @Override
    protected void upgrade(SQLiteDatabase db) {
        db.execSQL(ECPlusDBHelper.SQL_CREATE_CATEGORIA);
        db.execSQL(ECPlusDBHelper.SQL_CREATE_USO_PALABRA);

        db.execSQL("ALTER TABLE "+ECPlusDBContract.Palabra.TABLE_NAME
                +" ADD "+ECPlusDBContract.Palabra.REF_CATEGORIA+" bigint(20) default null"
                +" REFERENCES " +ECPlusDBContract.Categoria.TABLE_NAME+" ("+ECPlusDBContract.Categoria.ID+")"
        );

        db.execSQL("ALTER TABLE "+ECPlusDBContract.Palabra.TABLE_NAME
                +" ADD "+ECPlusDBContract.Palabra.REF_ICONO_PERSONALIZADO+" bigint(20) default null"
                +" REFERENCES " +ECPlusDBContract.RecursoAudioVisual.TABLE_NAME+" ("+ECPlusDBContract.RecursoAudioVisual.ID+")"
        );

        db.execSQL("ALTER TABLE "+ECPlusDBContract.Palabra.TABLE_NAME
                +" ADD "+ECPlusDBContract.Palabra.REF_PALABRA_CONTRARIA+" bigint(20) default null"
                +" REFERENCES " +ECPlusDBContract.Palabra.TABLE_NAME+" ("+ECPlusDBContract.Palabra.ID+")"
        );

        resetHashesWordList(db);
    }

    private void resetHashesWordList(SQLiteDatabase db) {
        ContentValues values= new ContentValues();
        values.put(ECPlusDBContract.HashesListaPalabras.HASH,"");
        db.update(ECPlusDBContract.HashesListaPalabras.TABLE_NAME,values,"",null);

        values = new ContentValues();
        values.put(ECPlusDBContract.HashesPalabra.HASH, "");
        db.update(ECPlusDBContract.HashesPalabra.TABLE_NAME, values, "", null);
    }

    @Override
    protected void downgrade(SQLiteDatabase db) {
        // TODO: we cannot remove columns in SQLite

    }
}
