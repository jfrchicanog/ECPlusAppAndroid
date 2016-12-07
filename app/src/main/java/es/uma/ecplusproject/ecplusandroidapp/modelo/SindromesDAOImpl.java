package es.uma.ecplusproject.ecplusandroidapp.modelo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDB;
import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDBContract;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Sindrome;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.TipoDocumento;

/**
 * Created by francis on 24/11/16.
 */

public class SindromesDAOImpl implements SindromesDAO {
    private static final String NOMBRE = "nombre";
    private static final String CONTENIDO = "contenido";
    private static final String HASH = "hash";
    private static final String TIPO = "tipo";
    private static final String ID = "ID";
    private static final String consulta = "select " +
            "s."+ ECPlusDBContract.Sindrome.NOMBRE+" as " + NOMBRE
            + ", s."+ECPlusDBContract.Sindrome.CONTENIDO+" as " + CONTENIDO
            + ", s."+ECPlusDBContract.Sindrome.HASH+" as " + HASH
            + ", s."+ECPlusDBContract.Sindrome.TIPO+" as " + TIPO
            + ", s."+ECPlusDBContract.Sindrome.ID+" as " + ID
            + " from "+ECPlusDBContract.Sindrome.TABLE_NAME+" s " +
            "inner join "+ECPlusDBContract.ListaSindromes.TABLE_NAME+" ls on " +
            "s."+ECPlusDBContract.Sindrome.REF_LISTA_SINDROMES+" = ls."+ECPlusDBContract.ListaSindromes.ID
            +" where ls."+ECPlusDBContract.ListaSindromes.IDIOMA+"=?";

    private Context contexto;
    private SQLiteDatabase db;

    public SindromesDAOImpl() {
        this(null);
    }

    public SindromesDAOImpl(Context context) {
        this.contexto = contexto;
        db = ECPlusDB.getDatabase(contexto);
    }

    @Override
    public void createListOfSyndromes(String language) {
        ContentValues values = new ContentValues();
        values.put(ECPlusDBContract.ListaSindromes.IDIOMA, language);
        // TODO: Esto es una arreglo para evitar un problema de null
        // Para arreglarlo hayq ue cambiar la DB
        values.put(ECPlusDBContract.ListaSindromes.ID, language.hashCode());
        db.insert(ECPlusDBContract.ListaSindromes.TABLE_NAME, null,values);
    }

    @Override
    public List<Sindrome> getSindromes(String language) {
        List<Sindrome> resultado = new ArrayList<>();

        String idioma = language;
        Cursor c = db.rawQuery(consulta, new String[]{idioma});
        if (c.moveToFirst()) {
            do {
                Sindrome sindrome =  new Sindrome(c.getString(c.getColumnIndex(NOMBRE)));
                sindrome.setDescripcion(new String(c.getBlob(c.getColumnIndex(CONTENIDO)), Charset.forName("UTF-8")));
                sindrome.setId(c.getLong(c.getColumnIndex(ID)));
                sindrome.setHash(c.getString(c.getColumnIndex(HASH)));
                sindrome.setTipo(TipoDocumento.valueOf(c.getString(c.getColumnIndex(TIPO))));

                resultado.add(sindrome);
            } while (c.moveToNext());
        }
        c.close();

        return resultado;
    }

    @Override
    public void removeSyndromeList(String language) {
        Long idList = getIDForListOfSindromes(language);
        if (idList == null) {
            throw new IllegalArgumentException("The list of syndromes in language " + language + " does not exist");
        }

        db.delete(ECPlusDBContract.Sindrome.TABLE_NAME,
                ECPlusDBContract.Sindrome.REF_LISTA_SINDROMES + "=?",
                new String[]{idList.toString()});
        db.delete(ECPlusDBContract.ListaSindromes.TABLE_NAME,
                ECPlusDBContract.ListaSindromes.ID,
                new String[]{idList.toString()});

    }

    @Nullable
    private Long getIDForListOfSindromes(String language) {
        Cursor c = db.query(ECPlusDBContract.ListaSindromes.TABLE_NAME,
                new String[]{ECPlusDBContract.ListaSindromes.ID},
                ECPlusDBContract.ListaSindromes.IDIOMA+"=?",
                new String[]{language},null,null,null);

        Long id = null;
        if (c.moveToFirst()) {
            id = c.getLong(c.getColumnIndex(ECPlusDBContract.ListaSindromes.ID));
        }
        c.close();
        return id;
    }

    @Override
    public String getHashForListOfSyndromes(String language) {
        Cursor c = db.query(ECPlusDBContract.ListaSindromes.TABLE_NAME,
                new String[]{ECPlusDBContract.ListaSindromes.HASH},
                ECPlusDBContract.ListaSindromes.IDIOMA+"=?",new String[]{language},null,null,null);
        String hash = null;
        if (c.moveToFirst()) {
            hash = c.getString(c.getColumnIndex(ECPlusDBContract.ListaSindromes.HASH));
        }
        c.close();

        return hash;
    }

    @Override
    public void setHashForListOfSyndromes(String language, String hash) {
        ContentValues values = new ContentValues();
        values.put(ECPlusDBContract.ListaSindromes.HASH, hash);
        db.update(ECPlusDBContract.ListaSindromes.TABLE_NAME,
                values,
                ECPlusDBContract.ListaSindromes.IDIOMA+"=?",
                new String[]{language});
    }

    @Override
    public void removeSyndrome(Sindrome sindrome) {
        db.delete(ECPlusDBContract.Sindrome.TABLE_NAME,
                ECPlusDBContract.Sindrome.ID+"=?",
                new String[]{""+sindrome.getId()});
    }

    @Override
    public void updateSyndrome(Sindrome sindrome) {
        ContentValues values = new ContentValues();
        values.put(ECPlusDBContract.Sindrome.CONTENIDO,
                sindrome.getDescripcion().getBytes(Charset.forName("UTF-8")));
        values.put(ECPlusDBContract.Sindrome.HASH, sindrome.getHash());
        values.put(ECPlusDBContract.Sindrome.NOMBRE, sindrome.getTexto());
        values.put(ECPlusDBContract.Sindrome.TIPO, sindrome.getTipo().toString());
        db.update(ECPlusDBContract.Sindrome.TABLE_NAME, values,
                ECPlusDBContract.Sindrome.ID+"=?", new String[]{""+sindrome.getId()});
    }

    @Override
    public void addSyndrome(Sindrome sindrome, String language) {
        Long idList = getIDForListOfSindromes(language);

        if (idList == null) {
            throw new IllegalArgumentException ("The list of syndromes in language "+language+" does not exist");
        }

        ContentValues values = new ContentValues();
        values.put(ECPlusDBContract.Sindrome.CONTENIDO,
                sindrome.getDescripcion().getBytes(Charset.forName("UTF-8")));
        values.put(ECPlusDBContract.Sindrome.HASH, sindrome.getHash());
        values.put(ECPlusDBContract.Sindrome.NOMBRE, sindrome.getTexto());
        values.put(ECPlusDBContract.Sindrome.ID, sindrome.getId());
        values.put(ECPlusDBContract.Sindrome.TIPO, sindrome.getTipo().toString());
        values.put(ECPlusDBContract.Sindrome.REF_LISTA_SINDROMES, idList);

        db.insert(ECPlusDBContract.Sindrome.TABLE_NAME, null, values);

    }
}
