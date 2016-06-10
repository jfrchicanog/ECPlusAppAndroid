package es.uma.ecplusproject.ecplusandroidapp.modelo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.lang.reflect.Array;

import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDB;
import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDBContract;

/**
 * Created by francis on 8/6/16.
 */
public class CargarListaPalabras extends AsyncTask<String, Palabra, Void> {

    public static final String PID = "pid";
    public static final String NOMBRE = "nombre";
    public static final String DTYPE = "dtype";
    public static final String HASH = "hash";
    private ArrayAdapter<Palabra> adaptador;

    private static final String megaconsulta = "select " +
            "p."+ECPlusDBContract.Palabra.NOMBRE+" as "+NOMBRE+", " +
            "p."+ECPlusDBContract.Palabra.ID+" as "+PID+", " +
            "r."+ECPlusDBContract.RecursoAudioVisual.DTYPE+" as "+DTYPE+", " +
            "f."+ECPlusDBContract.Ficheros.HASH+" as "+HASH+" " +
            "from "+ECPlusDBContract.Palabra.TABLE_NAME+" p " +
            "inner join "+ECPlusDBContract.PalabraRecursoAudioVisual.TABLE_NAME+" pr on p."
                +ECPlusDBContract.Palabra.ID+" = pr."+ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA+" " +
            "inner join "+ECPlusDBContract.RecursoAudioVisual.TABLE_NAME+" r on pr."
                +ECPlusDBContract.PalabraRecursoAudioVisual.REF_RECURSO_AUDIOVISUAL+" = r."+ECPlusDBContract.RecursoAudioVisual.ID+" " +
            "inner join "+ECPlusDBContract.Ficheros.TABLE_NAME+" f on f."
                +ECPlusDBContract.Ficheros.REF_RECURSO_AUDIOVISUAL+" = r."+ECPlusDBContract.RecursoAudioVisual.ID+" " +
            "inner join "+ECPlusDBContract.ListaPalabras.TABLE_NAME+" lp on lp."
                +ECPlusDBContract.ListaPalabras.ID+" = p."+ECPlusDBContract.Palabra.REF_LISTA_PALABRAS+" " +
            "where f."+ECPlusDBContract.Ficheros.RESOLUCION+" = ? and lp."+ECPlusDBContract.ListaPalabras.IDIOMA
                +"=? order by p."+ECPlusDBContract.Palabra.NOMBRE+" ASC";

    public CargarListaPalabras(ArrayAdapter<Palabra> adaptador) {
        this.adaptador = adaptador;
    }

    @Override
    protected Void doInBackground(String... params) {
        String idioma = params[0];

        SQLiteDatabase db = ECPlusDB.getDatabase();
        Cursor c = db.rawQuery(megaconsulta, new String[]{Resolucion.BAJA.toString(), idioma});
        Palabra palabra = null;
        if (c.moveToFirst()) {
            do {
                long idPalabra = c.getLong(c.getColumnIndex(PID));
                if (palabra == null || palabra.getId() != idPalabra) {
                    reportPalabra(palabra);
                    palabra = new Palabra(c.getString(c.getColumnIndex(NOMBRE)));
                    palabra.setId(idPalabra);
                }
                RecursoAV rav = createRecursoAV(c.getString(c.getColumnIndex(DTYPE)));
                rav.setHash(c.getString(c.getColumnIndex(HASH)));
                palabra.addRecurso(rav);
            } while (c.moveToNext());
        }
        reportPalabra(palabra);
        c.close();

        return null;
    }

    private void reportPalabra(Palabra palabra) {
        if (palabra != null) {
            publishProgress(palabra);
        }
    }

    @Nullable
    private RecursoAV createRecursoAV(String dtype) {
        RecursoAV rav = null;

        switch (dtype) {
            case "Pictograma":
                rav = new Pictograma();
                break;
            case "Video":
                rav = new Video();
                break;
            case "Foto":
                rav = new Fotografia();
                break;
        }
        return rav;
    }

    @Override
    protected void onProgressUpdate(Palabra... values) {
        adaptador.add(values[0]);
    }
}
