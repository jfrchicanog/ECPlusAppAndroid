package es.uma.ecplusproject.ecplusandroidapp.modelo;


import android.print.PrintAttributes;

import java.util.List;
import java.util.Set;

import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;

/**
 * Created by francis on 24/11/16.
 */

public interface PalabrasDAO {
    String getHashForListOfWords(String language, Resolucion resolucion);
    void removeAllResourcesForWordsList(String language, Resolucion resolution);
    void createListOfWords(String language);
    List<Palabra> getWords(String language, Resolucion resolution);
    void addWord(Palabra word, String language, Resolucion reoslution);
    void updateWord(Palabra remote);
    void updateUso(Palabra palabra);
    void setHashForListOfWords(String language, Resolucion resolution, String hash);
    void removeWord(Palabra word);
    Set<String> getAllHashes();
}
