package es.uma.ecplusproject.ecplusandroidapp.database;

import android.provider.BaseColumns;

/**
 * Created by francis on 5/6/16.
 */
public final class ECPlusDBContract {

    private ECPlusDBContract() {}

    public static abstract class Ficheros {
        public static final String TABLE_NAME = "Ficheros";
        // Columns
        public static final String REF_RECURSO_AUDIOVISUAL = "RecursoAudioVisual_id";
        public static final String RESOLUCION = "resolucion";
        public static final String HASH = "hash";
    }

    public static abstract class HashesListaPalabras {

        public static final String TABLE_NAME = "HashesListaPalabras";
        public static final String REF_LISTA_PALABRAS = "ListaPalabras_id";
        public static final String HASH = "hash";
        public static final String RESOLUCION = "resolucion";
    }

    public static abstract class ListaPalabras {

        public static final String TABLE_NAME = "ListaPalabras";
        public static final String ID = "id";
        public static final String IDIOMA = "idioma";
    }

    public static abstract class ListaSindromes {
        public static final String TABLE_NAME= "ListaSindromes";
        public static final String ID = "id";
        public static final String IDIOMA = "idioma";
        public static final String HASH = "hash";
    }

    public static abstract class RecursoAudioVisual {

        public static final String TABLE_NAME = "RecursoAudioVisual";
        public static final String ID = "id";
        public static final String DTYPE  = "DTYPE";
    }

    public static abstract class HashesPalabra {

        public static final String TABLE_NAME = "HashesPalabra";
        public static final String HASH = "hash";
        public static final String RESOLUCION = "resolucion";
        public static final String REF_PALABRA = "Palabra_id";
    }

    public static abstract class Palabra {
        public static final String TABLE_NAME = "Palabra";
        public static final String ID = "id";
        public static final String ICONO_REEMPLAZABLE = "iconoReemplazable";
        public static final String NOMBRE = "nombre";
        public static final String REF_ICONO = "icono_id";
        public static final String REF_LISTA_PALABRAS  = "listapalabras";

    }

    public static abstract class PalabraRecursoAudioVisual {
        public static final String TABLE_NAME = "Palabra_RecursoAudioVisual";
        public static final String REF_PALABRA = "Palabra_id";
        public static final String REF_RECURSO_AUDIOVISUAL  = "audiovisuales_id";
    }

    public static abstract class Sindrome {
        public static final String TABLE_NAME = "Sindrome";
        public static final String ID = "id";
        public static final String CONTENIDO = "contenido";
        public static final String NOMBRE = "nombre";
        public static final String HASH = "hash";
        public static final String REF_LISTA_SINDROMES = "listasindromes";
    }
}
