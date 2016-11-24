package es.uma.ecplusproject.ecplusandroidapp.modelo;


import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;

/**
 * Created by francis on 24/11/16.
 */

public interface PalabrasDAO {
    List<Palabra> getPalabras(String language, Resolucion resolution);
}
