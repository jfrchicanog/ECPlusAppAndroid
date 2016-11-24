package es.uma.ecplusproject.ecplusandroidapp.modelo;

import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.fragments.Sindromes;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Sindrome;

/**
 * Created by francis on 24/11/16.
 */

public interface SindromesDAO {
    List<Sindrome> getSindromes(String language);
}
