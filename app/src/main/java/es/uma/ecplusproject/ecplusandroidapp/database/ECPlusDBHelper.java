package es.uma.ecplusproject.ecplusandroidapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import es.uma.ecplusproject.ecplusandroidapp.R;

/**
 * Created by francis on 5/6/16.
 */
public class ECPlusDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_FILE = "ecplusdb-android.db";
    private DatabaseVersionChange [] transformaciones = new DatabaseVersionChange[]{
            new Version1To2(), new Version2To3()};

    private static final String SQL_CREATE_FICHEROS = "CREATE TABLE " + ECPlusDBContract.Ficheros.TABLE_NAME
            + "("
            + ECPlusDBContract.Ficheros.REF_RECURSO_AUDIOVISUAL + " bigint(20) NOT NULL,"
            + ECPlusDBContract.Ficheros.HASH + " varchar(255) DEFAULT NULL,"
            + ECPlusDBContract.Ficheros.RESOLUCION + " varchar(255) NOT NULL,"
            + " PRIMARY KEY (" + ECPlusDBContract.Ficheros.REF_RECURSO_AUDIOVISUAL + ","
            + ECPlusDBContract.Ficheros.RESOLUCION + ")"
            + ", CONSTRAINT `FK4r7rqa2diq9mlakqkbc1o44ax` FOREIGN KEY ("
            + ECPlusDBContract.Ficheros.REF_RECURSO_AUDIOVISUAL
            + ") REFERENCES " + ECPlusDBContract.RecursoAudioVisual.TABLE_NAME
            + " (" + ECPlusDBContract.RecursoAudioVisual.ID + ")"
            + ")";

    private static final String SQL_CREATE_HASHES_LISTA_PALABRAS = "CREATE TABLE "
            + ECPlusDBContract.HashesListaPalabras.TABLE_NAME + " (\n" +
            "  " + ECPlusDBContract.HashesListaPalabras.REF_LISTA_PALABRAS + " bigint(20) NOT NULL,\n" +
            "  " + ECPlusDBContract.HashesListaPalabras.HASH + " varchar(255) DEFAULT NULL,\n" +
            "  " + ECPlusDBContract.HashesListaPalabras.RESOLUCION + " varchar(255) NOT NULL,\n" +
            "  PRIMARY KEY ("
            + ECPlusDBContract.HashesListaPalabras.REF_LISTA_PALABRAS + ","
            + ECPlusDBContract.HashesListaPalabras.RESOLUCION + "),\n" +
            "  CONSTRAINT `FK1rg63c7bebdyuf49oku1orylp` FOREIGN KEY ("
            + ECPlusDBContract.HashesListaPalabras.REF_LISTA_PALABRAS + ") " +
            "REFERENCES " + ECPlusDBContract.ListaPalabras.TABLE_NAME + " (" + ECPlusDBContract.ListaPalabras.ID + ")\n" +
            ")";

    private static final String SQL_CREATE_HASHES_PALABRA = "CREATE TABLE " + ECPlusDBContract.HashesPalabra.TABLE_NAME + " (\n" +
            "  " + ECPlusDBContract.HashesPalabra.REF_PALABRA + " bigint(20) NOT NULL,\n" +
            "  " + ECPlusDBContract.HashesPalabra.HASH + " varchar(255) DEFAULT NULL,\n" +
            "  " + ECPlusDBContract.HashesPalabra.RESOLUCION + " varchar(255) NOT NULL,\n" +
            "  PRIMARY KEY (" + ECPlusDBContract.HashesPalabra.REF_PALABRA + "," + ECPlusDBContract.HashesPalabra.RESOLUCION + "),\n" +
            "  CONSTRAINT `FKogeetek2j7mbk5fctxj4i5i7p` FOREIGN KEY (" + ECPlusDBContract.HashesPalabra.REF_PALABRA + ") REFERENCES "+ECPlusDBContract.Palabra.TABLE_NAME+" ("+ECPlusDBContract.Palabra.ID+")\n" +
            ") ";

    private static final String SQL_CREATE_LISTA_PALABRAS = "CREATE TABLE "+ ECPlusDBContract.ListaPalabras.TABLE_NAME + " (\n" +
            "  " + ECPlusDBContract.ListaPalabras.ID + " bigint(20) NOT NULL,\n" +
            "  " + ECPlusDBContract.ListaPalabras.IDIOMA + " varchar(255) DEFAULT NULL,\n" +
            "  PRIMARY KEY ("+ECPlusDBContract.ListaPalabras.ID+")\n" +
            ") ";

    private static final String SQL_CREATE_LISTA_SINDROMES = "CREATE TABLE "+ECPlusDBContract.ListaSindromes.TABLE_NAME+" (\n" +
            "  "+ECPlusDBContract.ListaSindromes.ID+" bigint(20) NOT NULL,\n" +
            "  " + ECPlusDBContract.HashesPalabra.HASH + " varchar(255) DEFAULT NULL,\n" +
            "  " + ECPlusDBContract.ListaPalabras.IDIOMA + " varchar(255) DEFAULT NULL,\n" +
            "  PRIMARY KEY ("+ECPlusDBContract.ListaSindromes.ID+")\n" +
            ") ";

    private static final String SQL_CREATE_PALABRA = "CREATE TABLE "+ECPlusDBContract.Palabra.TABLE_NAME+" (\n" +
            "  "+ECPlusDBContract.Palabra.ID+" bigint(20) NOT NULL,\n" +
            "  "+ECPlusDBContract.Palabra.ICONO_REEMPLAZABLE+" bit(1) DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Palabra.NOMBRE+" varchar(255) DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Palabra.AVANZADA+" bit(1) DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Palabra.REF_ICONO+" bigint(20) DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Palabra.REF_LISTA_PALABRAS+" bigint(20) DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Palabra.ICONO_PERSONALIZADO+" text DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Palabra.REF_CATEGORIA+" bigint(20) DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Palabra.REF_PALABRA_CONTRARIA+" bigint(20) DEFAULT NULL,\n" +
            "  PRIMARY KEY ("+ECPlusDBContract.Palabra.ID+"),\n" +
            "  CONSTRAINT `FKrfwuygvxilyqojx92fgys5xq2` FOREIGN KEY ("+ECPlusDBContract.Palabra.REF_LISTA_PALABRAS+") REFERENCES "+ECPlusDBContract.ListaPalabras.TABLE_NAME+" ("+ECPlusDBContract.ListaPalabras.ID+"),\n" +
            "  CONSTRAINT `FKrsd5gspwqehrhvny29n6y1xki` FOREIGN KEY ("+ECPlusDBContract.Palabra.REF_ICONO+") REFERENCES "+ECPlusDBContract.RecursoAudioVisual.TABLE_NAME+" ("+ECPlusDBContract.RecursoAudioVisual.ID+"),\n" +
            "  CONSTRAINT `Palabra2Categoria` FOREIGN KEY (" + ECPlusDBContract.Palabra.REF_CATEGORIA + ") REFERENCES " +ECPlusDBContract.Categoria.TABLE_NAME+" ("+ECPlusDBContract.Categoria.ID+"),\n"+
            "  CONSTRAINT `PalabraContraria` FOREIGN KEY ("+ECPlusDBContract.Palabra.REF_PALABRA_CONTRARIA+") REFERENCES " +ECPlusDBContract.Palabra.TABLE_NAME+" ("+ECPlusDBContract.Palabra.ID+")"+
            ") ";

    private static final String SQL_CREATE_PALABRA_RECURSO_AUDIOVISUAL="CREATE TABLE "+ECPlusDBContract.PalabraRecursoAudioVisual.TABLE_NAME+" (\n" +
            "  " +ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA+ " bigint(20) NOT NULL,\n" +
            "  "+ECPlusDBContract.PalabraRecursoAudioVisual.REF_RECURSO_AUDIOVISUAL+" bigint(20) NOT NULL,\n" +
            "  PRIMARY KEY (" +ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA+ ","+ECPlusDBContract.PalabraRecursoAudioVisual.REF_RECURSO_AUDIOVISUAL+"),\n" +
            "  CONSTRAINT `FK1qkuni7y8xaf8npc6ggs2i0u7` FOREIGN KEY ("+ECPlusDBContract.PalabraRecursoAudioVisual.REF_RECURSO_AUDIOVISUAL+") REFERENCES "+ECPlusDBContract.RecursoAudioVisual.TABLE_NAME+" ("+ECPlusDBContract.RecursoAudioVisual.ID+"),\n" +
            "  CONSTRAINT `FKjxhde0rfr6t2ux98gbh1y4d1i` FOREIGN KEY (" +ECPlusDBContract.PalabraRecursoAudioVisual.REF_PALABRA+ ") REFERENCES "+ECPlusDBContract.Palabra.TABLE_NAME+" ("+ECPlusDBContract.Palabra.ID+")\n" +
            ") ";

    private static final String SQL_CREATE_RECURSOAUDIOVISUAL = "CREATE TABLE "+ECPlusDBContract.RecursoAudioVisual.TABLE_NAME+" (\n" +
            "  "+ECPlusDBContract.RecursoAudioVisual.DTYPE+" varchar(31) NOT NULL,\n" +
            "  "+ECPlusDBContract.RecursoAudioVisual.ID+" bigint(20) NOT NULL,\n" +
            "  PRIMARY KEY ("+ECPlusDBContract.RecursoAudioVisual.ID+")\n"+
            ") ";

    private static final String SQL_CREATE_SINDROME = "CREATE TABLE "+ECPlusDBContract.Sindrome.TABLE_NAME+" (\n" +
            "  "+ECPlusDBContract.Sindrome.ID+" bigint(20) NOT NULL,\n" +
            "  "+ECPlusDBContract.Sindrome.CONTENIDO+" longblob,\n" +
            "  "+ECPlusDBContract.Sindrome.HASH + " varchar(255) DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Sindrome.NOMBRE+" varchar(255) DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Sindrome.TIPO+" varchar(255) DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Sindrome.REF_LISTA_SINDROMES+" bigint(20) DEFAULT NULL,\n" +
            "  PRIMARY KEY ("+ECPlusDBContract.Sindrome.ID+"),\n" +
            "  CONSTRAINT `FKql0et6pco82pijfv4ule2u8br` FOREIGN KEY ("
            +ECPlusDBContract.Sindrome.REF_LISTA_SINDROMES+") REFERENCES "
            +ECPlusDBContract.ListaSindromes.TABLE_NAME+" ("+ECPlusDBContract.ListaSindromes.ID+")\n" +
            ") ";

    static final String SQL_CREATE_CATEGORIA = "CREATE TABLE "+ECPlusDBContract.Categoria.TABLE_NAME+" (\n" +
            "  "+ECPlusDBContract.Categoria.ID+" bigint(20) NOT NULL,\n" +
            "  "+ECPlusDBContract.Categoria.NOMBRE+" varchar(255) DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Categoria.REF_LISTA_PALABRAS+" bigint(20) DEFAULT NULL,\n" +
            "  PRIMARY KEY ("+ECPlusDBContract.Categoria.ID+"),\n" +
            "  CONSTRAINT `Categoria2ListaPalabras` FOREIGN KEY ("
            +ECPlusDBContract.Categoria.REF_LISTA_PALABRAS+") REFERENCES "
            +ECPlusDBContract.ListaPalabras.TABLE_NAME+" ("+ECPlusDBContract.ListaPalabras.ID+")\n" +
            ") ";

    static final String SQL_CREATE_USO_PALABRA = "CREATE TABLE "+ECPlusDBContract.UsoPalabra.TABLE_NAME+" (\n" +
            "  "+ECPlusDBContract.UsoPalabra.REF_PALABRA+" bigint(20) NOT NULL,\n" +
            "  "+ECPlusDBContract.UsoPalabra.ACCESOS+" bigint(20) NOT NULL,\n" +
            "  "+ECPlusDBContract.UsoPalabra.ULTIMO_USO+" text DEFAULT NULL,\n" +
            "  PRIMARY KEY ("+ECPlusDBContract.UsoPalabra.REF_PALABRA+"),\n" +
            "  CONSTRAINT `Uso2Palabra` FOREIGN KEY ("
            +ECPlusDBContract.UsoPalabra.REF_PALABRA+") REFERENCES "
            +ECPlusDBContract.Palabra.TABLE_NAME+" ("+ECPlusDBContract.Palabra.ID+")\n" +
            ") ";



    private Context context;
    public ECPlusDBHelper(Context context) {
        super(context, DATABASE_FILE, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LISTA_PALABRAS);
        db.execSQL(SQL_CREATE_HASHES_LISTA_PALABRAS);
        db.execSQL(SQL_CREATE_RECURSOAUDIOVISUAL);
        db.execSQL(SQL_CREATE_CATEGORIA);
        db.execSQL(SQL_CREATE_PALABRA);
        db.execSQL(SQL_CREATE_HASHES_PALABRA);
        db.execSQL(SQL_CREATE_PALABRA_RECURSO_AUDIOVISUAL);
        db.execSQL(SQL_CREATE_FICHEROS);
        db.execSQL(SQL_CREATE_USO_PALABRA);

        db.execSQL(SQL_CREATE_LISTA_SINDROMES);
        db.execSQL(SQL_CREATE_SINDROME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i=oldVersion; i < newVersion; i++) {
            transformaciones[i-1].onUpgrade(db, i, i+1);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i=oldVersion; i > newVersion; i--) {
            transformaciones[i-2].onDowngrade(db, i, i-1);
        }
    }
}
