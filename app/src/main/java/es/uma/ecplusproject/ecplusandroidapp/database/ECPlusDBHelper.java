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

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_FILE = "ecplusdb-android.db";

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
            "  "+ECPlusDBContract.Palabra.REF_ICONO+" bigint(20) DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Palabra.REF_LISTA_PALABRAS+" bigint(20) DEFAULT NULL,\n" +
            "  PRIMARY KEY ("+ECPlusDBContract.Palabra.ID+"),\n" +
            "  CONSTRAINT `FKrfwuygvxilyqojx92fgys5xq2` FOREIGN KEY ("+ECPlusDBContract.Palabra.REF_LISTA_PALABRAS+") REFERENCES "+ECPlusDBContract.ListaPalabras.TABLE_NAME+" ("+ECPlusDBContract.ListaPalabras.ID+"),\n" +
            "  CONSTRAINT `FKrsd5gspwqehrhvny29n6y1xki` FOREIGN KEY ("+ECPlusDBContract.Palabra.REF_ICONO+") REFERENCES "+ECPlusDBContract.RecursoAudioVisual.TABLE_NAME+" ("+ECPlusDBContract.RecursoAudioVisual.ID+")";

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
            "  PRIMARY KEY ("+ECPlusDBContract.RecursoAudioVisual.ID+")";

    private static final String SQL_CREATE_SINDROME = "CREATE TABLE "+ECPlusDBContract.Sindrome.TABLE_NAME+" (\n" +
            "  "+ECPlusDBContract.Sindrome.ID+" bigint(20) NOT NULL,\n" +
            "  "+ECPlusDBContract.Sindrome.CONTENIDO+" longblob,\n" +
            "  "+ECPlusDBContract.Sindrome.HASH + " varchar(255) DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Sindrome.NOMBRE+" varchar(255) DEFAULT NULL,\n" +
            "  "+ECPlusDBContract.Sindrome.REF_LISTA_SINDROMES+" bigint(20) DEFAULT NULL,\n" +
            "  PRIMARY KEY ("+ECPlusDBContract.Sindrome.ID+"),\n" +
            "  CONSTRAINT `FKql0et6pco82pijfv4ule2u8br` FOREIGN KEY ("
            +ECPlusDBContract.Sindrome.REF_LISTA_SINDROMES+") REFERENCES "
            +ECPlusDBContract.ListaSindromes.TABLE_NAME+" ("+ECPlusDBContract.ListaSindromes.ID+")\n" +
            ") ";


    private Context context;
    public ECPlusDBHelper(Context context) {
        super(context, DATABASE_FILE, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        System.out.println("Me han llamado?");

        try {
            //loadFromRawResource(db, APKExpansionSupport.getAPKExpansionZipFile(context, 3, 0).getInputStream("ecplusdb-android.sql"));
            loadFromRawResource(db, new GZIPInputStream(context.getResources().openRawResource(R.raw.ecplusdb)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadFromRawResource(SQLiteDatabase db, InputStream is) {
        Scanner sc = new Scanner(is);
        StringBuilder sb = new StringBuilder();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            sb.append(line);
            if (line.endsWith(";")) {
                db.execSQL(sb.toString());
                sb = new StringBuilder();
            }
        }
        if (sb.length() > 0) {
            db.execSQL(sb.toString());
        }
        sc.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
