package es.uma.ecplusproject.ecplusandroidapp.restws;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;

/**
 * Created by francis on 24/11/16.
 */

public interface PalabrasWS {
    String getHashForListOfWords(String language, Resolucion resolucion);
    List<Palabra> getWords(String language, Resolucion resolution);
    InputStream getResource(String hash) throws IOException;
}
