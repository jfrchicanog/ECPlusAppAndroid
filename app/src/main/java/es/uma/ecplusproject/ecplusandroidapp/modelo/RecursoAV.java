package es.uma.ecplusproject.ecplusandroidapp.modelo;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by francis on 18/3/16.
 */
public abstract class RecursoAV implements Serializable {
    private String hash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }



}
