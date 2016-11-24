package es.uma.ecplusproject.ecplusandroidapp.modelo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDB;
import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDBContract;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Fotografia;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Pictograma;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Video;

/**
 * Created by francis on 24/11/16.
 */

public class PalabrasDAOImpl implements PalabrasDAO {
    private static final String PID = "pid";
    private static final String NOMBRE = "nombre";
    private static final String DTYPE = "dtype";
    private static final String HASH = "hash";

    private static final String megaconsulta = "select " +
            "p."+ ECPlusDBContract.Palabra.NOMBRE+" as "+NOMBRE+", " +
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


    private Context contexto;

    public PalabrasDAOImpl() {
    }

    public PalabrasDAOImpl(Context context) {
        this.contexto = contexto;
    }

    @Override
    public List<Palabra> getPalabras(String language, Resolucion resolution) {
        String idioma = language;
        List<Palabra> resultado = new ArrayList<>();

        SQLiteDatabase db = ECPlusDB.getDatabase(contexto);
        Cursor c = db.rawQuery(megaconsulta, new String[]{resolution.toString(), idioma});
        Palabra palabra = null;
        if (c.moveToFirst()) {
            do {
                long idPalabra = c.getLong(c.getColumnIndex(PID));
                if (palabra == null || palabra.getId() != idPalabra) {
                    if (palabra != null) {
                        resultado.add(palabra);
                    }

                    palabra = new Palabra(c.getString(c.getColumnIndex(NOMBRE)));
                    palabra.setId(idPalabra);
                }
                RecursoAV rav = createRecursoAV(c.getString(c.getColumnIndex(DTYPE)));
                rav.setHash(c.getString(c.getColumnIndex(HASH)));
                palabra.addRecurso(rav);
            } while (c.moveToNext());
        }
        if (palabra != null) {
            resultado.add(palabra);
        }
        c.close();

        return resultado;

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
}
