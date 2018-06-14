package es.uma.ecplusproject.ecplusandroidapp.modelo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.ECPlusApplication;
import es.uma.ecplusproject.ecplusandroidapp.MainActivity;
import es.uma.ecplusproject.ecplusandroidapp.Splash;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;

public class CachePalabras {
    private PalabrasDAO daoPalabras = new PalabrasDAOImpl();
    private List<Palabra> palabras=null;
    private static CachePalabras theInstance;

    private CachePalabras() {
    }

    public List<Palabra> getPalabras() {
        if (palabras==null) {
            getPalabrasFromDAO();
        }
        return new ArrayList<>(palabras);
    }

    public void clearCache() {
        palabras = null;
    }

    private void getPalabrasFromDAO() {
        SharedPreferences preferences =
                ECPlusApplication.getContext().getSharedPreferences(Splash.ECPLUS_MAIN_PREFS, Context.MODE_PRIVATE);
        String preferredLanguage = preferences.getString(MainActivity.PREFERRED_LANGUAGE, MainActivity.DEFAULT_LANGUAGE);
        palabras = daoPalabras.getWords(preferredLanguage, Resolucion.BAJA);
    }

    public static CachePalabras getTheInstance() {
        if (theInstance==null) {
            theInstance = new CachePalabras();
        }
        return theInstance;
    }
}
