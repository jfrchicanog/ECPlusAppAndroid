package es.uma.ecplusproject.ecplusandroidapp.modelo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import java.nio.charset.Charset;

import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDB;
import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDBContract;
import es.uma.ecplusproject.ecplusandroidapp.modelo.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.modelo.Sindrome;

/**
 * Created by francis on 8/6/16.
 */
public class CargaListaSindromes extends AsyncTask<String, Sindrome, Void> {

    public static final String NOMBRE = "nombre";
    public static final String CONTENIDO = "contenido";
    private static final String consulta = "select " +
            "s."+ ECPlusDBContract.Sindrome.NOMBRE+" as " + NOMBRE
            + ", s."+ECPlusDBContract.Sindrome.CONTENIDO+" as " + CONTENIDO
            + " from "+ECPlusDBContract.Sindrome.TABLE_NAME+" s " +
            "inner join "+ECPlusDBContract.ListaSindromes.TABLE_NAME+" ls on " +
            "s."+ECPlusDBContract.Sindrome.REF_LISTA_SINDROMES+" = ls."+ECPlusDBContract.ListaSindromes.ID
            +" where ls."+ECPlusDBContract.ListaSindromes.IDIOMA+"=?";

    private ArrayAdapter<Sindrome> adaptador;

    public CargaListaSindromes(ArrayAdapter<Sindrome> adaptador) {
        this.adaptador = adaptador;
    }


    @Override
    protected Void doInBackground(String... params) {
        String idioma = params[0];
        SQLiteDatabase db = ECPlusDB.getDatabase();
        Cursor c = db.rawQuery(consulta, new String[]{idioma});
        if (c.moveToFirst()) {
            do {
                Sindrome sindrome =  new Sindrome(c.getString(c.getColumnIndex(NOMBRE)));
                sindrome.setDescripcion(new String(c.getBlob(c.getColumnIndex(CONTENIDO)), Charset.forName("UTF-8")));
                publishProgress(sindrome);
            } while (c.moveToNext());
        }
        c.close();

        return null;
    }

    @Override
    protected void onProgressUpdate(Sindrome... values) {
        adaptador.add(values[0]);
    }
}
