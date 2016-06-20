package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.io.InputStream;

import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.Splash;
import es.uma.ecplusproject.ecplusandroidapp.modelo.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.Pictograma;
import es.uma.ecplusproject.ecplusandroidapp.modelo.RecursoAV;

/**
 * Created by francis on 20/4/16.
 */
public class AdaptadorPalabras extends ArrayAdapter<Palabra> {
    private Context contexto;

    public AdaptadorPalabras(Context contexto) {
        super(contexto, 0);
        this.contexto=contexto;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Palabra palabra = getItem(position);
        View view=null;
        if (convertView == null) {
            view = LayoutInflater.from(contexto).inflate(R.layout.entradapalabra, null);
        } else {
            view = convertView;
        }
        TextView texto = (TextView)view.findViewById(R.id.textoPalabra);
        SVGImageView imagen = (SVGImageView)view.findViewById(R.id.imagenPalabra);

        texto.setText(palabra.toString());
        //imagen.setImageDrawable(contexto.getResources().getDrawable(R.drawable.abrigo));
        imagen.setImageDrawable(contexto.getResources().getDrawable(R.drawable.logo));

        for (RecursoAV recurso: palabra.getRecursos()) {
            if (recurso instanceof Pictograma) {
                try {
                    String hash = recurso.getHash();
                    InputStream is = APKExpansionSupport.getAPKExpansionZipFile(contexto, Splash.MAIN_VERSION, 0).getInputStream(hash.toLowerCase());
                    SVG svg = SVG.getFromInputStream(is);

                    svg.renderToPicture();
                    SVG.Box box = svg.getDocumentBoundingBox();
                    svg.setDocumentViewBox(box.minX, box.minY, box.width, box.height);
                    imagen.setSVG(svg);
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException (e);
                } catch (SVGParseException e) {
                    throw new RuntimeException (e);
                }
                break;
            }
        }
        return view;
    }
}
