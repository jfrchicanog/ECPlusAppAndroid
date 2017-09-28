package es.uma.ecplusproject.ecplusandroidapp.modelo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDB;
import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDBContract;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;

/**
 * Created by francis on 24/11/16.
 */

public class PalabrasDAOImpl implements PalabrasDAO {
    private static final String PID = "pid";
    private static final String RID = "rid";
    private static final String NOMBRE = "nombre";
    private static final String LISTA_PALABRAS_ID = "lpid";
    private static final String DTYPE = "dtype";
    private static final String ICON = "icon";
    private static final String PERSONALIZED_ICON = "pers_icon";
    private static final String REPLACEABLE_ICON = "replaceableIcon";
    private static final String AVANZADA = "advanced";
    private static final String HASH = "hash";
    private static final String HASH_PALABRA = "hashp";
    private static final String ACCESOS = "acceso";
    private static final String ULTIMO_USO = "ultuso";

    private static final String megaconsulta = "select " +
            "lp."+ ECPlusDBContract.ListaPalabras.ID+" as "+LISTA_PALABRAS_ID+", " +
            "p."+ ECPlusDBContract.Palabra.NOMBRE+" as "+NOMBRE+", " +
            "p."+ECPlusDBContract.Palabra.ID+" as "+PID+", " +
            "p."+ECPlusDBContract.Palabra.ICONO_REEMPLAZABLE+" as "+REPLACEABLE_ICON+", "+
            "p."+ECPlusDBContract.Palabra.AVANZADA+" as "+AVANZADA+", "+
            "p."+ECPlusDBContract.Palabra.REF_ICONO+" as "+ICON+", "+
            "p."+ECPlusDBContract.Palabra.ICONO_PERSONALIZADO+" as "+PERSONALIZED_ICON+", "+
            "r."+ECPlusDBContract.RecursoAudioVisual.ID+" as "+RID+", "+
            "r."+ECPlusDBContract.RecursoAudioVisual.DTYPE+" as "+DTYPE+", " +
            "f."+ECPlusDBContract.Ficheros.HASH+" as "+HASH+", " +
            "hp."+ECPlusDBContract.HashesPalabra.HASH+" as "+HASH_PALABRA+", "+
            "up."+ECPlusDBContract.UsoPalabra.ACCESOS+" as "+ACCESOS+", "+
            "up."+ECPlusDBContract.UsoPalabra.ULTIMO_USO+" as "+ULTIMO_USO+" "+
            "from "+ECPlusDBContract.Palabra.TABLE_NAME+" p " +
            "inner join "+ECPlusDBContract.HashesPalabra.TABLE_NAME+" hp on p."+
            ECPlusDBContract.Palabra.ID+" = hp."+ECPlusDBContract.HashesPalabra.REF_PALABRA+" "+
            "left join "+ECPlusDBContract.PalabraRecursoAudioVisual.TABLE_NAME+" pr on p."
            +ECPlusDBContract.Palabra.ID+" = pr."+ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA+" " +
            "left join "+ECPlusDBContract.RecursoAudioVisual.TABLE_NAME+" r on pr."
            +ECPlusDBContract.PalabraRecursoAudioVisual.REF_RECURSO_AUDIOVISUAL+" = r."+ECPlusDBContract.RecursoAudioVisual.ID+" " +
            "left join "+ECPlusDBContract.Ficheros.TABLE_NAME+" f on f."
            +ECPlusDBContract.Ficheros.REF_RECURSO_AUDIOVISUAL+" = r."+ECPlusDBContract.RecursoAudioVisual.ID+" " +
            "and f."+ECPlusDBContract.Ficheros.RESOLUCION+"= hp."+ECPlusDBContract.HashesPalabra.RESOLUCION+" "+
            "inner join "+ECPlusDBContract.ListaPalabras.TABLE_NAME+" lp on lp."
            +ECPlusDBContract.ListaPalabras.ID+" = p."+ECPlusDBContract.Palabra.REF_LISTA_PALABRAS+" " +
            "left join "+ECPlusDBContract.UsoPalabra.TABLE_NAME+" up on p."
            +ECPlusDBContract.Palabra.ID+" = up."+ECPlusDBContract.UsoPalabra.REF_PALABRA+" "+
            "where hp."+ECPlusDBContract.Ficheros.RESOLUCION+" = ? and lp."+ECPlusDBContract.ListaPalabras.IDIOMA+"=? " +
            "order by p."+ECPlusDBContract.Palabra.NOMBRE+"," +
            "p."+ECPlusDBContract.Palabra.ID+" "+
            " ASC";

    private static final String hashQuery = "select "+
            "h."+ECPlusDBContract.HashesListaPalabras.HASH+" as "+HASH+" "+
            "from "+ECPlusDBContract.HashesListaPalabras.TABLE_NAME+ " h "+
            "inner join "+ECPlusDBContract.ListaPalabras.TABLE_NAME+ " l "+
            "on h."+ECPlusDBContract.HashesListaPalabras.REF_LISTA_PALABRAS+" = l."+ECPlusDBContract.ListaPalabras.ID+" "+
            "where h."+ECPlusDBContract.HashesListaPalabras.RESOLUCION+"=? "+
            "and l."+ECPlusDBContract.ListaPalabras.IDIOMA+" =?";

    private static final String resourcesForWordList = "select " +
            "pr."+ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA+" as "+PID+
            ", pr."+ECPlusDBContract.PalabraRecursoAudioVisual.REF_RECURSO_AUDIOVISUAL+" as "+RID+" " +
            "from "+ECPlusDBContract.PalabraRecursoAudioVisual.TABLE_NAME+" pr " +
            "inner join "+ECPlusDBContract.Palabra.TABLE_NAME+" p " +
            "on p."+ECPlusDBContract.Palabra.ID+"=pr."+ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA+" " +
            "inner join "+ECPlusDBContract.RecursoAudioVisual.TABLE_NAME+" r " +
            "on r."+ECPlusDBContract.RecursoAudioVisual.ID+"=pr."+ECPlusDBContract.PalabraRecursoAudioVisual.REF_RECURSO_AUDIOVISUAL+" " +
            "where p."+ECPlusDBContract.Palabra.REF_LISTA_PALABRAS+"=? " +
            "order by pr."+ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA+" ASC";

    private static final String allHashesQuery = "select "+
            ECPlusDBContract.Ficheros.HASH+ " "+
            "from " + ECPlusDBContract.Ficheros.TABLE_NAME;

    private static final String TAG="PalabrasDAOImpl";

    private Context contexto;
    private SQLiteDatabase db;

    public PalabrasDAOImpl() {
        this(null);
    }

    public PalabrasDAOImpl(Context context) {
        this.contexto = contexto;
        db = ECPlusDB.getDatabase(contexto);
        //Log.d(TAG, megaconsulta);
    }

    @Override
    public String getHashForListOfWords(String language, Resolucion resolucion) {
        db = ECPlusDB.getDatabase(contexto);
        Cursor c = db.rawQuery(hashQuery, new String[]{resolucion.toString(), language});
        String hash = null;
        if (c.moveToFirst()) {
            hash = c.getString(c.getColumnIndex(HASH));
        }
        c.close();
        return hash;
    }

    @Override
    public List<Palabra> getWords(String language, Resolucion resolution) {
        String idioma = language;
        List<Palabra> resultado = new ArrayList<>();

        db = ECPlusDB.getDatabase(contexto);
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
                    palabra.setListaPalabrasId(c.getLong(c.getColumnIndex(LISTA_PALABRAS_ID)));
                    palabra.getHashes().put(resolution, c.getString(c.getColumnIndex(HASH_PALABRA)));
                    palabra.setIconoReemplazable(c.getInt(c.getColumnIndex(REPLACEABLE_ICON))>0);
                    palabra.setAvanzada(c.getInt(c.getColumnIndex(AVANZADA))>0);
                    if (!c.isNull(c.getColumnIndex(PERSONALIZED_ICON))) {
                        palabra.setIconoPersonalizado(c.getString(c.getColumnIndex(PERSONALIZED_ICON)));
                    }

                    if (!c.isNull(c.getColumnIndex(ACCESOS))) {
                        palabra.setAccesos(c.getLong(c.getColumnIndex(ACCESOS)));
                    }

                }
                if (!c.isNull(c.getColumnIndex(DTYPE))) {
                    RecursoAV rav = RecursoAV.createRecursoAV(c.getString(c.getColumnIndex(DTYPE)));
                    rav.getFicheros().put(resolution, c.getString(c.getColumnIndex(HASH)));
                    rav.setId(c.getLong(c.getColumnIndex(RID)));
                    palabra.addRecurso(rav);
                    if (rav.getId() == c.getLong(c.getColumnIndex(ICON))) {
                        palabra.setIcono(rav);
                    }
                }

            } while (c.moveToNext());
        }
        if (palabra != null) {
            resultado.add(palabra);
        }
        c.close();

        return resultado;

    }

    private Long getIDForWordList(String language) {
        Cursor c = db.query(ECPlusDBContract.ListaPalabras.TABLE_NAME,
                new String[]{ECPlusDBContract.ListaPalabras.ID},
                ECPlusDBContract.ListaPalabras.IDIOMA+"=?",
                new String[]{language},null,null,null);

        Long id = null;
        if (c.moveToFirst()) {
            id = c.getLong(c.getColumnIndex(ECPlusDBContract.ListaPalabras.ID));
        }
        c.close();
        return id;
    }

    @Override
    public void removeAllResourcesForWordsList(String language, Resolucion resolution) {
        Long id = getIDForWordList(language);
        if (id !=null) {
            db.delete(ECPlusDBContract.HashesListaPalabras.TABLE_NAME,
                    ECPlusDBContract.HashesListaPalabras.RESOLUCION+"=? and"+
                    ECPlusDBContract.HashesListaPalabras.REF_LISTA_PALABRAS+"=?",
                    new String[]{resolution.toString(), ""+id});
            Cursor c = db.query(ECPlusDBContract.HashesListaPalabras.TABLE_NAME,
                    new String[]{ECPlusDBContract.HashesListaPalabras.RESOLUCION},
                    ECPlusDBContract.HashesListaPalabras.REF_LISTA_PALABRAS+"=?",
                    new String[]{""+id},null,null,null);
            boolean removeAll = !c.moveToFirst();
            c.close();

            if (removeAll) {
                removeWordsListById(id);
            } else {
                removeResourcesForWordListWithResolution(id, resolution);
            }
        }
    }

    private void removeResourcesForWordListWithResolution(Long id, Resolucion resolution) {
        Cursor c = db.rawQuery(resourcesForWordList, new String[]{""+id});
        Long prevWordId = null;
        if (c.moveToFirst()) {
            do {
                long idWord = c.getLong(c.getColumnIndex(PID));
                long idRav = c.getLong(c.getColumnIndex(RID));

                db.delete(ECPlusDBContract.Ficheros.TABLE_NAME,
                        ECPlusDBContract.Ficheros.REF_RECURSO_AUDIOVISUAL+"=? and "+
                        ECPlusDBContract.Ficheros.RESOLUCION+"=?",
                        new String[]{""+idRav,resolution.toString()});

                if (prevWordId==null || prevWordId != idWord) {
                    db.delete(ECPlusDBContract.HashesPalabra.TABLE_NAME,
                            ECPlusDBContract.HashesPalabra.REF_PALABRA + "=? and "+
                            ECPlusDBContract.HashesPalabra.RESOLUCION+"=?",
                            new String[]{"" + idWord, resolution.toString()});
                }

            } while (c.moveToNext());
        }
        c.close();
    }

    private void removeWordsListById(Long id) {
        Cursor cursor = db.query(ECPlusDBContract.Palabra.TABLE_NAME,
                new String[]{ECPlusDBContract.Palabra.ID},
                ECPlusDBContract.Palabra.REF_LISTA_PALABRAS+"=?",
                new String[]{""+id},null,null,null);
        if (cursor.moveToFirst()) {
            do {
                long idWord = cursor.getLong(cursor.getColumnIndex(ECPlusDBContract.Palabra.ID));
                removeWordById(idWord);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.delete(ECPlusDBContract.ListaPalabras.TABLE_NAME,
                ECPlusDBContract.ListaPalabras.ID+"=?",
                new String[]{""+id});
    }

    @Override
    public void createListOfWords(String language) {
        ContentValues values = new ContentValues();
        values.put(ECPlusDBContract.ListaPalabras.IDIOMA, language);
        // TODO: Esto es una arreglo para evitar un problema de null
        // Para arreglarlo hayq ue cambiar la DB
        values.put(ECPlusDBContract.ListaPalabras.ID, language.hashCode());
        db.insert(ECPlusDBContract.ListaPalabras.TABLE_NAME, null, values);
    }


    @Override
    public void addWord(Palabra word, String language, Resolucion resolution) {
        Long idList = getIDForWordList(language);

        ContentValues values = new ContentValues();
        values.put(ECPlusDBContract.Palabra.ID, word.getId());
        values.put(ECPlusDBContract.Palabra.ICONO_REEMPLAZABLE, word.getIconoReemplazable());
        values.put(ECPlusDBContract.Palabra.AVANZADA, word.getAvanzada());
        values.put(ECPlusDBContract.Palabra.NOMBRE, word.getNombre());
        if (word.getIconoPersonalizado()!=null) {
            values.put(ECPlusDBContract.Palabra.ICONO_PERSONALIZADO, word.getIconoPersonalizado());
        }
        if (word.getIcono()!=null) {
            values.put(ECPlusDBContract.Palabra.REF_ICONO, word.getIcono().getId());
        }
        values.put(ECPlusDBContract.Palabra.REF_LISTA_PALABRAS, idList);
        long i = db.replace(ECPlusDBContract.Palabra.TABLE_NAME,null, values);
        Log.d(getClass().getSimpleName(), ""+i);

        for (Map.Entry<Resolucion,String> entry: word.getHashes().entrySet()) {
            values = new ContentValues();
            values.put(ECPlusDBContract.HashesPalabra.REF_PALABRA, word.getId());
            values.put(ECPlusDBContract.HashesPalabra.RESOLUCION, entry.getKey().toString());
            values.put(ECPlusDBContract.HashesPalabra.HASH, entry.getValue());
            db.replace(ECPlusDBContract.HashesPalabra.TABLE_NAME, null, values);
        }

        for (RecursoAV rav: word.getRecursos()) {
            values = new ContentValues();
            values.put(ECPlusDBContract.RecursoAudioVisual.DTYPE,rav.getDType());
            values.put(ECPlusDBContract.RecursoAudioVisual.ID, rav.getId());
            db.replace(ECPlusDBContract.RecursoAudioVisual.TABLE_NAME, null, values);

            values = new ContentValues();
            values.put(ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA, word.getId());
            values.put(ECPlusDBContract.PalabraRecursoAudioVisual.REF_RECURSO_AUDIOVISUAL, rav.getId());
            db.replace(ECPlusDBContract.PalabraRecursoAudioVisual.TABLE_NAME, null, values);

            for (Map.Entry<Resolucion, String> fichero: rav.getFicheros().entrySet()){
                values = new ContentValues();
                values.put(ECPlusDBContract.Ficheros.REF_RECURSO_AUDIOVISUAL, rav.getId());
                values.put(ECPlusDBContract.Ficheros.RESOLUCION, fichero.getKey().toString());
                values.put(ECPlusDBContract.Ficheros.HASH, fichero.getValue());
                db.replace(ECPlusDBContract.Ficheros.TABLE_NAME, null, values);
            }
        }
    }

    @Override
    public void updateWord(Palabra remote) {
        if (remote.getListaPalabrasId()==null) {
            throw new IllegalArgumentException("I don¡t identify the word list to add this word");
        }
        //Long idList = getIDForWordList(language);
        Long idList = remote.getListaPalabrasId();
        ContentValues values = new ContentValues();

        for (RecursoAV rav: remote.getRecursos()) {
            values = new ContentValues();
            values.put(ECPlusDBContract.RecursoAudioVisual.ID, rav.getId());
            values.put(ECPlusDBContract.RecursoAudioVisual.DTYPE, rav.getDType());
            db.replace(ECPlusDBContract.RecursoAudioVisual.TABLE_NAME, null, values);

            for (Map.Entry<Resolucion,String> entry: rav.getFicheros().entrySet()) {
                values = new ContentValues();
                values.put(ECPlusDBContract.Ficheros.REF_RECURSO_AUDIOVISUAL, rav.getId());
                values.put(ECPlusDBContract.Ficheros.RESOLUCION, entry.getKey().toString());
                values.put(ECPlusDBContract.Ficheros.HASH, entry.getValue());
                db.replace(ECPlusDBContract.Ficheros.TABLE_NAME,null,values);
            }
            values = new ContentValues();
            values.put(ECPlusDBContract.PalabraRecursoAudioVisual.REF_RECURSO_AUDIOVISUAL, rav.getId());
            values.put(ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA, remote.getId());
            db.replace(ECPlusDBContract.PalabraRecursoAudioVisual.TABLE_NAME,null,values);
        }

        for (Map.Entry<Resolucion, String> entry: remote.getHashes().entrySet()) {
            values = new ContentValues();
            values.put(ECPlusDBContract.HashesPalabra.RESOLUCION,entry.getKey().toString());
            values.put(ECPlusDBContract.HashesPalabra.HASH,entry.getValue());
            values.put(ECPlusDBContract.HashesPalabra.REF_PALABRA,remote.getId());
            db.replace(ECPlusDBContract.HashesPalabra.TABLE_NAME, null, values);
        }

        values = new ContentValues();
        values.put(ECPlusDBContract.Palabra.NOMBRE, remote.getNombre());
        values.put(ECPlusDBContract.Palabra.ICONO_REEMPLAZABLE, remote.getIconoReemplazable());
        values.put(ECPlusDBContract.Palabra.AVANZADA, remote.getAvanzada());
        values.put(ECPlusDBContract.Palabra.ID, remote.getId());
        values.put(ECPlusDBContract.Palabra.REF_LISTA_PALABRAS, idList);
        if (remote.getIcono()!=null) {
            values.put(ECPlusDBContract.Palabra.REF_ICONO, remote.getIcono().getId());
        }
        if (remote.getIconoPersonalizado()!=null) {
            values.put(ECPlusDBContract.Palabra.ICONO_PERSONALIZADO, remote.getIconoPersonalizado());
        }
        db.replace(ECPlusDBContract.Palabra.TABLE_NAME,null, values);
    }

    public void updateUso(Palabra palabra) {
        ContentValues values = new ContentValues();
        values.put(ECPlusDBContract.UsoPalabra.REF_PALABRA, palabra.getId());
        values.put(ECPlusDBContract.UsoPalabra.ACCESOS, palabra.getAccesos());
        db.replace(ECPlusDBContract.UsoPalabra.TABLE_NAME, null, values);
    }

    @Override
    public void setHashForListOfWords(String language, Resolucion resolution, String hash) {
        Long idList = getIDForWordList(language);

        ContentValues values = new ContentValues();
        values.put(ECPlusDBContract.HashesListaPalabras.REF_LISTA_PALABRAS, idList);
        values.put(ECPlusDBContract.HashesListaPalabras.RESOLUCION, resolution.toString());
        values.put(ECPlusDBContract.HashesListaPalabras.HASH, hash);

        db.replace(ECPlusDBContract.HashesListaPalabras.TABLE_NAME, null, values);
    }

    @Override
    public void removeWord(Palabra word) {
        removeWordById(word.getId());
    }

    private void removeWordById(Long wordId) {
        db.delete(ECPlusDBContract.HashesPalabra.TABLE_NAME,
                ECPlusDBContract.HashesPalabra.REF_PALABRA+"=?",
                new String[]{""+ wordId});
        Cursor c = db.query(ECPlusDBContract.PalabraRecursoAudioVisual.TABLE_NAME,
                new String[]{ECPlusDBContract.PalabraRecursoAudioVisual.REF_RECURSO_AUDIOVISUAL},
                ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA+"=?",
                new String[]{""+ wordId},null,null,null);
        if (c.moveToFirst()) {
            do {
                Long idRav = c.getLong(c.getColumnIndex(ECPlusDBContract.PalabraRecursoAudioVisual.REF_RECURSO_AUDIOVISUAL));
                db.delete(ECPlusDBContract.Ficheros.TABLE_NAME,
                        ECPlusDBContract.Ficheros.REF_RECURSO_AUDIOVISUAL+"=?",
                        new String[]{""+idRav});
                db.delete(ECPlusDBContract.RecursoAudioVisual.TABLE_NAME,
                        ECPlusDBContract.RecursoAudioVisual.ID+"=?",
                        new String[]{""+idRav});
            } while (c.moveToNext());
        }
        c.close();
        db.delete(ECPlusDBContract.PalabraRecursoAudioVisual.TABLE_NAME,
                ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA+"=?",
                new String[]{""+ wordId});
    }

    @Override
    public Set<String> getAllHashes() {
        Set<String> result = new HashSet<>();
        Cursor c = db.query(true, ECPlusDBContract.Ficheros.TABLE_NAME,new String[]{ECPlusDBContract.Ficheros.HASH},
                null, null, null, null, null, null);

        if (c.moveToFirst()){
            do {
                result.add(c.getString(c.getColumnIndex(ECPlusDBContract.Ficheros.HASH)).toLowerCase());
            } while (c.moveToNext());
        }
        c.close();

        c = db.query(true, ECPlusDBContract.Palabra.TABLE_NAME,new String[]{ECPlusDBContract.Palabra.ICONO_PERSONALIZADO},
                null,null,null,null,null,null);

        if (c.moveToFirst()) {
            do {
                if (!c.isNull(c.getColumnIndex(ECPlusDBContract.Palabra.ICONO_PERSONALIZADO))) {
                    result.add(c.getString(c.getColumnIndex(ECPlusDBContract.Palabra.ICONO_PERSONALIZADO)));
                }
            } while (c.moveToNext());
        }
        c.close();

        return result;
    }
}
