package es.uma.ecplusproject.ecplusandroidapp.modelo;

import android.graphics.drawable.Drawable;

import java.util.Map;

/**
 * Created by francis on 18/3/16.
 */
public abstract class RecursoAV {
    private Drawable drawable;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    private String hash;


    public RecursoAV(Drawable drawable) {
        this.drawable = drawable;
    }

    public Drawable getDrawable() {
        return drawable;
    }

}
