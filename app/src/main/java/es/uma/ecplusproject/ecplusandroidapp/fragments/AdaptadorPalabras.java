package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.uma.ecplusproject.ecplusandroidapp.R;
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
        ImageView imagen = (ImageView)view.findViewById(R.id.imagenPalabra);

        texto.setText(palabra.toString());
        for (RecursoAV recurso: palabra.getRecursos()) {
            if (recurso instanceof Pictograma) {
                imagen.setImageDrawable(recurso.getDrawable());
                int tam = (int)contexto.getResources().getDimension(R.dimen.imagenPalabras);
                //imagen.setLayoutParams(new LinearLayout.LayoutParams(tam, tam));
                break;
            }
        }
        return view;
    }
}
