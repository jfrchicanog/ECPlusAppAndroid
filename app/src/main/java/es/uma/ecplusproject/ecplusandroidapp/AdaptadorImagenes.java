package es.uma.ecplusproject.ecplusandroidapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Fotografia;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Pictograma;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.services.ResourcesStore;

/**
 * Created by francis on 13/5/16.
 */
public class AdaptadorImagenes extends ArrayAdapter<RecursoAV> {
    private Context ctx;
    private ResourcesStore resourcesStore;
    private Resolucion resolucion = Resolucion.BAJA;

    public AdaptadorImagenes(Context ctx) {
        super(ctx,0);
        this.ctx = ctx;
        resourcesStore = new ResourcesStore(ctx);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imagen;
        if (convertView != null) {
            imagen = (ImageView)convertView;
        } else {
            imagen = new SVGImageView(ctx);
        }
        RecursoAV recurso = getItem(position);

        int ancho = getColumnWidth(((GridView)parent));

        if (recurso instanceof Fotografia) {
            try {
                String hash = recurso.getFicheros().get(resolucion);
                InputStream is = new FileInputStream(resourcesStore.getFileResource(hash));
                Bitmap bm = BitmapFactory.decodeStream(is);
                is.close();
                imagen.setImageBitmap(bm);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (recurso instanceof Pictograma) {
            try {
                String hash = recurso.getFicheros().get(resolucion);
                InputStream is = new FileInputStream(resourcesStore.getFileResource(hash));
                SVG svg = SVG.getFromInputStream(is);

                SVG.Box box = svg.getDocumentBoundingBox();
                Picture p = svg.renderToPicture();
                box = svg.getDocumentBoundingBox();
                svg.setDocumentViewBox(box.minX, box.minY, box.width, box.height);
                //svg.setDocumentViewBox(49,178,164,129);

                ((SVGImageView)imagen).setSVG(svg);
                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SVGParseException e) {
                throw new RuntimeException(e);
            }
        }

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
