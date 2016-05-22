package es.uma.ecplusproject.ecplusandroidapp;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.lang.reflect.Field;

import es.uma.ecplusproject.ecplusandroidapp.modelo.RecursoAV;

/**
 * Created by francis on 13/5/16.
 */
public class AdaptadorImagenes extends ArrayAdapter<RecursoAV> {
    private Context ctx;

    public AdaptadorImagenes(Context ctx) {
        super(ctx,0);
        this.ctx = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imagen;
        if (convertView != null) {
            imagen = (ImageView)convertView;
        } else {
            imagen = new ImageView(ctx);
        }
        RecursoAV recurso = getItem(position);


        int ancho = getColumnWidth(((GridView)parent));
        imagen.setImageDrawable(recurso.getDrawable());

        imagen.setLayoutParams(new GridView.LayoutParams(ancho,ancho));
        imagen.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        return imagen;

    }

    private int getColumnWidth(GridView gridView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            return gridView.getColumnWidth();
        else {
            try {
                Field field = GridView.class.getDeclaredField("mColumnWidth");
                field.setAccessible(true);
                Integer value = (Integer) field.get(gridView);
                field.setAccessible(false);
                return value;
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
