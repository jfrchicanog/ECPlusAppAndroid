package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caverock.androidsvg.SVGImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Pictograma;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.services.ResourcesStore;

/**
 * Created by francis on 20/4/16.
 */
public class AdaptadorPictogramas extends RecyclerView.Adapter<AdaptadorPictogramas.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SVGImageView icono;
        private Palabra palabra;
        private ClickPalabraListener listener;

        public ViewHolder(View itemView, ClickPalabraListener listener) {
            super(itemView);
            icono = (SVGImageView)itemView.findViewById(R.id.imagenPalabra);
            itemView.setOnClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClickPalabra(palabra);
            }
        }
    }

    public interface ClickPalabraListener {
        void onClickPalabra(Palabra palabra);
    }

    private List<Palabra> listaPalabras = new ArrayList<>();

    private Context contexto;
    private Resolucion resolucion;
    private ResourcesStore resourceStore;
    private ClickPalabraListener listener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contexto).inflate(R.layout.entradapictograma, null);
        ViewHolder viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Palabra palabra = listaPalabras.get(position);
        //imagen.setImageDrawable(contexto.getResources().getDrawable(R.drawable.abrigo));
        SVGImageView icono = holder.icono;
        holder.palabra = palabra;

        if (palabra.getIcono()!= null && palabra.getIcono() instanceof Pictograma) {
            String hash = palabra.getIcono().getFicheros().get(resolucion);
            if (hash != null) {
                resourceStore.tryToUseSVG(icono, hash);
            } else {
                icono.setSVG(resourceStore.getApplicationLogoSVG());
            }
        } else {
            String hash = null;
            for (RecursoAV recurso : palabra.getRecursos()) {
                if (recurso instanceof Pictograma) {
                    hash = recurso.getFicheros().get(resolucion);
                    if (hash != null) {
                        resourceStore.tryToUseSVG(icono, hash);
                        break;
                    }
                }
            }
            if (hash == null) {
                icono.setSVG(resourceStore.getApplicationLogoSVG());
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaPalabras.size();
    }

    public AdaptadorPictogramas(Context contexto) {
        this.contexto=contexto;
        resourceStore = new ResourcesStore(contexto);
        // TODO: change this when resolution modification is added to the App
        resolucion = Resolucion.BAJA;
    }

    public void setOnClickPalabraListener(ClickPalabraListener listener) {
        this.listener = listener;
    }

    public void addAll(Collection<Palabra> items) {
        listaPalabras.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        listaPalabras.clear();
        notifyDataSetChanged();
    }

}
