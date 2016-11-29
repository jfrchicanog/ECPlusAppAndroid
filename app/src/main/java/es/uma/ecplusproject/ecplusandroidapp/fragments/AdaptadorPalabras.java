package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;

import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Pictograma;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.services.ResourcesStore;

/**
 * Created by francis on 20/4/16.
 */
public class AdaptadorPalabras extends ArrayAdapter<Palabra> {

    private static class ViewHolder {
        private TextView texto;
        private SVGImageView icono;
    }

    private Context contexto;
    private Resolucion resolucion;
    private ResourcesStore resourceStore;

    public AdaptadorPalabras(Context contexto) {
        super(contexto, 0);
        this.contexto=contexto;
        resourceStore = new ResourcesStore(contexto);
        // TODO: change this when resolution modification is added to the App
        resolucion = Resolucion.BAJA;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Palabra palabra = getItem(position);
        View view=null;
        if (convertView == null) {
            view = LayoutInflater.from(contexto).inflate(R.layout.entradapalabra, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.texto = (TextView)view.findViewById(R.id.textoPalabra);
            viewHolder.icono = (SVGImageView)view.findViewById(R.id.imagenPalabra);
            view.setTag(viewHolder);

        } else {
            view = convertView;
        }

        ViewHolder viewHolder = (ViewHolder)view.getTag();

        viewHolder.texto.setText(palabra.toString());
        //imagen.setImageDrawable(contexto.getResources().getDrawable(R.drawable.abrigo));
        SVGImageView icono = viewHolder.icono;

        if (palabra.getIcono()!= null && palabra.getIcono() instanceof Pictograma) {
            resourceStore.tryToUseSVG(icono, palabra.getIcono().getFicheros().get(resolucion));
        } else {
            icono.setImageDrawable(contexto.getResources().getDrawable(R.drawable.logo));
            for (RecursoAV recurso : palabra.getRecursos()) {
                if (recurso instanceof Pictograma) {
                    resourceStore.tryToUseSVG(icono, recurso.getFicheros().get(resolucion));
                    break;
                }
            }
        }
        return view;
    }

}
