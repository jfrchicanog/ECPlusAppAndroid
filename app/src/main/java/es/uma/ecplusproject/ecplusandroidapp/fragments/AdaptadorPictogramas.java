package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.services.ResourcesStore;

/**
 * Created by francis on 20/4/16.
 */
public class AdaptadorPictogramas extends RecyclerView.Adapter<AdaptadorPictogramas.MyViewHolder> {

    public static final int TIPO_FOTO = 0;
    public static final int TIPO_PICTOGRAMA=1;

    public abstract class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MyViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindRecurso(Palabra palabra);
    }

    public class Foto extends MyViewHolder {
        public ImageView foto;
        public TextView texto;
        public Bitmap bitmap;
        public Palabra palabra;
        private ClickPalabraListener listener;

        public Foto(View foto, ClickPalabraListener listener) {
            super(foto);
            this.foto = (ImageView)foto.findViewById(R.id.foto);
            this.texto = (TextView)foto.findViewById(R.id.texto);
            itemView.setOnClickListener(this);
            this.listener = listener;
        }

        public void bindRecurso(Palabra palabra) {
            this.palabra=palabra;
            String hash = palabra.getIconoPersonalizado();
            if (hash != null) {
                resourceStore.tryToUseBitmap(foto, hash,
                        new ResourcesStore.BitmapLoadListener() {
                            @Override
                            public void finishedBitmapLoad(Bitmap bitmap) {
                                Foto.this.bitmap = bitmap;
                                if (bitmap != null) {
                                    texto.setVisibility(View.GONE);
                                } else {
                                    texto.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClickPalabra(palabra);
            }
        }
    }

    public class Pictograma extends MyViewHolder {
        public SVGImageView pictograma;
        public TextView texto;
        private Palabra palabra;
        private ClickPalabraListener listener;

        public Pictograma(View pictograma, ClickPalabraListener listener) {
            super(pictograma);
            this.pictograma=(SVGImageView)pictograma.findViewById(R.id.pictograma);
            this.texto = (TextView)pictograma.findViewById(R.id.texto);
            itemView.setOnClickListener(this);
            this.listener = listener;
        }

        public void bindRecurso(Palabra palabra) {
            this.palabra = palabra;

            if (palabra.getIcono()!= null && palabra.getIcono() instanceof es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Pictograma) {
                String hash = palabra.getIcono().getFicheros().get(resolucion);
                if (hash != null) {
                    resourceStore.tryToUseSVG(pictograma, hash);
                    texto.setVisibility(View.GONE);
                } else {
                    pictograma.setSVG(resourceStore.getApplicationLogoSVG());
                    texto.setVisibility(View.VISIBLE);
                }
            } else {
                String hash = null;
                for (RecursoAV recurso : palabra.getRecursos()) {
                    if (recurso instanceof es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Pictograma) {
                        hash = recurso.getFicheros().get(resolucion);
                        if (hash != null) {
                            resourceStore.tryToUseSVG(pictograma, hash);
                            texto.setVisibility(View.GONE);
                            break;
                        }
                    }
                }
                if (hash == null) {
                    pictograma.setSVG(resourceStore.getApplicationLogoSVG());
                    texto.setVisibility(View.VISIBLE);
                }
            }

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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TIPO_PICTOGRAMA:
                view = LayoutInflater.from(contexto).inflate(R.layout.detallepictograma, null);
                Pictograma viewHolder = new Pictograma(view, listener);
                return viewHolder;
            case TIPO_FOTO:
                view = LayoutInflater.from(contexto).inflate(R.layout.detallefoto, null);
                return new Foto(view, listener);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Palabra palabra = listaPalabras.get(position);
        //imagen.setImageDrawable(contexto.getResources().getDrawable(R.drawable.abrigo));
        holder.bindRecurso(palabra);
    }

    @Override
    public int getItemViewType(int position) {
        if (listaPalabras.get(position).getIconoPersonalizado()!=null) {
            return TIPO_FOTO;
        } else {
            return TIPO_PICTOGRAMA;
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
