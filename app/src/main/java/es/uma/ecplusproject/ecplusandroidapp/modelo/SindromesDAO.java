package es.uma.ecplusproject.ecplusandroidapp.modelo;

import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.fragments.Sindromes;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Sindrome;

/**
 * Created by francis on 24/11/16.
 */

public interface SindromesDAO {
    void createListOfSyndromes(String language);
    List<Sindrome> getSindromes(String language);
    void removeSyndromeList(String language);
    String getHashForListOfSyndromes(String language);
    void setHashForListOfSyndromes(String language, String hash);

    void removeSyndrome(Sindrome sindrome);
    void updateSyndrome(Sindrome sindrome);
    void addSyndrome(Sindrome sindrome, String language);
}
