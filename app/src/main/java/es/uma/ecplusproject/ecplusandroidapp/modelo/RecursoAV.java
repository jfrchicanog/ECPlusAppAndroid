package es.uma.ecplusproject.ecplusandroidapp.modelo;

import android.graphics.drawable.Drawable;

/**
 * Created by francis on 18/3/16.
 */
public abstract class RecursoAV {
    private Drawable drawable;

    public RecursoAV(Drawable drawable) {
        this.drawable = drawable;
    }

    public Drawable getDrawable() {
        return drawable;
    }

}
