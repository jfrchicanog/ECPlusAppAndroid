package es.uma.ecplusproject.ecplusandroidapp.modelo.dominio;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by francis on 18/3/16.
 */
public abstract class RecursoAV implements Serializable {
    private Map<Resolucion, String> ficheros;
    private Long id;


    public Map<Resolucion, String> getFicheros() {
        if (ficheros==null) {
            ficheros = new HashMap<>();
        }
        return ficheros;
    }

    public void setFicheros(Map<Resolucion, String> ficheros) {
        this.ficheros = ficheros;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Nullable
    public static RecursoAV createRecursoAV(String dtype) {
        RecursoAV rav = null;

        switch (dtype) {
            case "Pictograma":
                rav = new Pictograma();
                break;
            case "Video":
                rav = new Video();
                break;
            case "Fotografia":
                rav = new Fotografia();
                break;
            case "Audio":
                rav = new Audio();
                break;
        }
        return rav;
    }

    public String getDType() {
        return getClass().getSimpleName();
    }

}
