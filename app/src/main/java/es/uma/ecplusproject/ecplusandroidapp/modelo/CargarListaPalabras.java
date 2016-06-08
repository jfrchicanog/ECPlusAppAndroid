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
                +"=? order by p."+ECPlusDBContract.Palabra.ID;

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
                    publishProgress(palabra);
                    palabra = new Palabra(c.getString(c.getColumnIndex(NOMBRE)));
                    palabra.setId(idPalabra);
                }
                RecursoAV rav = createRecursoAV(c.getString(c.getColumnIndex(DTYPE)));
                rav.setHash(c.getString(c.getColumnIndex(HASH)));
                palabra.addRecurso(rav);
            } while (c.moveToNext());
        }
        publishProgress(palabra);

        /*
        SQLiteDatabase db = ECPlusDB.getDatabase();
        Cursor c = db.query(ECPlusDBContract.ListaPalabras.TABLE_NAME, new String[]{ECPlusDBContract.ListaPalabras.ID},
                ECPlusDBContract.ListaPalabras.IDIOMA+"=?",new String [] {idioma}, null, null, null);
        c.moveToFirst();
        long id = c.getLong(c.getColumnIndex(ECPlusDBContract.ListaPalabras.ID));

        c = db.query(ECPlusDBContract.Palabra.TABLE_NAME, new String[]{ECPlusDBContract.Palabra.ID
        , ECPlusDBContract.Palabra.NOMBRE, ECPlusDBContract.Palabra.ICONO_REEMPLAZABLE, ECPlusDBContract.Palabra.REF_ICONO},
                ECPlusDBContract.Palabra.REF_LISTA_PALABRAS+"=?",new String[]{""+id},null,null,ECPlusDBContract.Palabra.NOMBRE+" ASC");

        if (c.moveToFirst()) {
            do {
                Palabra palabra = new Palabra(c.getString(c.getColumnIndex(ECPlusDBContract.Palabra.NOMBRE)));

                Cursor c2= db.query(ECPlusDBContract.PalabraRecursoAudioVisual.TABLE_NAME, new String[] {ECPlusDBContract.PalabraRecursoAudioVisual.REF_RECURSO_AUDIOVISUAL
                ,ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA}, ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA+"=?",
                        new String[]{""+c.getLong(c.getColumnIndex(ECPlusDBContract.Palabra.ID))},null, null, null);

                if (c2.moveToFirst()) {
                    do {
                        Cursor c3 = db.query(ECPlusDBContract.RecursoAudioVisual.TABLE_NAME, new String[]{
                        ECPlusDBContract.RecursoAudioVisual.DTYPE}, ECPlusDBContract.RecursoAudioVisual.ID+"=?",new String[]{
                                ""+c2.getLong(c2.getColumnIndex(ECPlusDBContract.PalabraRecursoAudioVisual.REF_RECURSO_AUDIOVISUAL)},null,null,null
                        );
                    } while (c2.moveToNext());
                }

                publishProgress(palabra);
            } while (c.moveToNext());
        }

        c.close();

*/
        return null;
    }

    @Nullable
    private RecursoAV createRecursoAV(String dtype) {
        RecursoAV rav = null;

        switch (dtype) {
            case "Pictograma":
                rav = new Pictograma(null);
                break;
            case "Video":
                rav = new Video(null);
                break;
            case "Foto":
                rav = new Fotografia(null);
                break;
        }
        return rav;
    }

    @Override
    protected void onProgressUpdate(Palabra... values) {
        if (values[0] != null) {
            adaptador.add(values[0]);
        }
    }
}
