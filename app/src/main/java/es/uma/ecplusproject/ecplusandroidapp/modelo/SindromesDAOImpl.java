package es.uma.ecplusproject.ecplusandroidapp.modelo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDB;
import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDBContract;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Sindrome;

/**
 * Created by francis on 24/11/16.
 */

public class SindromesDAOImpl implements SindromesDAO {
    private static final String NOMBRE = "nombre";
    private static final String CONTENIDO = "contenido";
    private static final String consulta = "select " +
            "s."+ ECPlusDBContract.Sindrome.NOMBRE+" as " + NOMBRE
            + ", s."+ECPlusDBContract.Sindrome.CONTENIDO+" as " + CONTENIDO
            + " from "+ECPlusDBContract.Sindrome.TABLE_NAME+" s " +
            "inner join "+ECPlusDBContract.ListaSindromes.TABLE_NAME+" ls on " +
            "s."+ECPlusDBContract.Sindrome.REF_LISTA_SINDROMES+" = ls."+ECPlusDBContract.ListaSindromes.ID
            +" where ls."+ECPlusDBContract.ListaSindromes.IDIOMA+"=?";

    @Override
    public List<Sindrome> getSindromes(String language) {
        List<Sindrome> resultado = new ArrayList<>();

        String idioma = language;
        SQLiteDatabase db = ECPlusDB.getDatabase();
        Cursor c = db.rawQuery(consulta, new String[]{idioma});
        if (c.moveToFirst()) {
            do {
                Sindrome sindrome =  new Sindrome(c.getString(c.getColumnIndex(NOMBRE)));
                sindrome.setDescripcion(new String(c.getBlob(c.getColumnIndex(CONTENIDO)), Charset.forName("UTF-8")));
                resultado.add(sindrome);
            } while (c.moveToNext());
        }
        c.close();

        return resultado;
    }
}
