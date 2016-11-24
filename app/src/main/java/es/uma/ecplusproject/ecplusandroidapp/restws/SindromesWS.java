package es.uma.ecplusproject.ecplusandroidapp.restws;

import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.fragments.Sindromes;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Sindrome;

/**
 * Created by francis on 24/11/16.
 */

public interface SindromesWS {
    String getHashForListOfSindromes(String language);
    List<Sindrome> getSindromes(String language);
}
