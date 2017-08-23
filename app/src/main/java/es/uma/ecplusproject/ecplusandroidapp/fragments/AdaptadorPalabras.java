package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.caverock.androidsvg.SVGImageView;

import org.springframework.util.StringUtils;

import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Pictograma;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.services.ResourcesStore;

/**
 * Created by francis on 20/4/16.
 */
public class AdaptadorPalabras extends ArrayAdapter<Palabra> implements SectionIndexer, View.OnLongClickListener {

    private Map<Character, Integer> letraASeccion;
    private Object [] seccionALetra;
    private Integer [] inicioSeccion;

    private final Comparator<String> comparador;
    private final Collator collator;

    private class ViewHolder implements ResourcesStore.ImageViewContainer{
        private TextView texto;
        private SVGImageView icono;
        private CardView externalCardView;

        private ResourcesStore.CargaBitmapEscalado loader;

        public void asynchronousBitmapLoad(String hash) {
            this.loader=resourceStore. new CargaBitmapEscalado(this, hash, null);
            resourceStore.tryToUseBitmap(icono, loader);
        }

        public void cancelAsynchronousBitmapLoad(){
            this.loader= null;
        }

        @Override
        public void setImageBitmap(ResourcesStore.CargaBitmapEscalado cargador, Bitmap bm) {
            if (cargador == loader) {
                icono.setImageBitmap(bm);
            }
        }

        @Override
        public void setImageDrawable(ResourcesStore.CargaBitmapEscalado cargador, Drawable dr) {
            if (cargador== loader) {
                icono.setImageDrawable(dr);
            }
        }
    }

    private Context contexto;
    private Resolucion resolucion;
    private ResourcesStore resourceStore;
    private ChangePictureListener changePictureListener;

    public AdaptadorPalabras(Context contexto) {
        super(contexto, 0);
        this.contexto=contexto;
        resourceStore = new ResourcesStore(contexto);
        // TODO: change this when resolution modification is added to the App
        resolucion = Resolucion.BAJA;

        collator = Collator.getInstance();
        collator.setStrength(Collator.PRIMARY);
        comparador = new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return collator.compare(eliminarNoLetras(lhs), eliminarNoLetras(rhs));
            }
        };
        changePictureListener = null;

    }

    public void setChangePictureListener(ChangePictureListener changePictureListener) {
        this.changePictureListener=changePictureListener;
    }

    @Override
    public boolean onLongClick(View view) {
        if (changePictureListener!=null) {
            changePictureListener.requestToChangePictureForWord((Palabra)view.getTag());
        }
        return true;
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
            viewHolder.externalCardView = (CardView) view.findViewById(R.id.externalCardView);
            viewHolder.externalCardView.setOnLongClickListener(this);
            view.setTag(viewHolder);

        } else {
            view = convertView;
        }

        ViewHolder viewHolder = (ViewHolder)view.getTag();
        viewHolder.texto.setText(palabra.toString());
        //imagen.setImageDrawable(contexto.getResources().getDrawable(R.drawable.abrigo));
        SVGImageView icono = viewHolder.icono;
        CardView externalCardView = viewHolder.externalCardView;

        externalCardView.setCardBackgroundColor(getContext().getResources().getColor(
                palabra.getIconoReemplazable()?R.color.colorPrimary:R.color.cardview_light_background));
        externalCardView.setLongClickable(palabra.getIconoReemplazable());
        externalCardView.setTag(palabra);

        if (palabra.getIconoPersonalizado()!= null) {
            Log.d("AdaptadorPalabras", "Poniendo imagen en "+icono);
            viewHolder.asynchronousBitmapLoad(palabra.getIconoPersonalizado());
        }
        else if (palabra.getIcono()!= null && palabra.getIcono() instanceof Pictograma) {
            String hash = palabra.getIcono().getFicheros().get(resolucion);
            viewHolder.cancelAsynchronousBitmapLoad();
            if (hash != null) {
                resourceStore.tryToUseSVG(icono, hash);
            } else {
                icono.setSVG(resourceStore.getApplicationLogoSVG());
            }
        } else {
            String hash = null;
            viewHolder.cancelAsynchronousBitmapLoad();
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
        return view;
    }

    @Override
    public Object[] getSections() {
        return seccionALetra;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return inicioSeccion[sectionIndex];
    }

    @Override
    public int getSectionForPosition(int position) {
        Palabra palabra = getItem(position);
        return letraASeccion.get(primeraLetra(palabra.getNombre()));
    }

    @Override
    public void addAll(Palabra... items) {
        super.addAll(items);
        recalcularIndice();
    }

    @Override
    public void addAll(Collection<? extends Palabra> collection) {
        super.addAll(collection);
        recalcularIndice();
    }

    private void recalcularIndice() {
        sort(new Comparator<Palabra>() {
            @Override
            public int compare(Palabra lhs, Palabra rhs) {
                return comparador.compare(lhs.getNombre(), rhs.getNombre());
            }
        });

        List<Character> letras = new ArrayList<>();
        List<Integer> secciones = new ArrayList<>();

        for (int posicion=0; posicion < getCount(); posicion++) {
            Character letra = primeraLetra(getItem(posicion).getNombre());
            if (letras.isEmpty() || collator.compare(""+letra, ""+letras.get(letras.size()-1))!=0) {
                letras.add(letra);
                secciones.add(posicion);
            }
        }


        letraASeccion = new HashMap<>();
        for (int i=0; i < letras.size(); i++) {
            letraASeccion.put(letras.get(i), i);
        }
        seccionALetra = letras.toArray();
        inicioSeccion = secciones.toArray(new Integer[0]);

    }

    private String eliminarNoLetras(String cadena) {
        StringBuilder builder = new StringBuilder();
        for (char c: cadena.toCharArray()) {
            if (Character.isLetter(c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private String normalizar(String palabra) {
        palabra = Normalizer.normalize(palabra, Normalizer.Form.NFD);
        return palabra.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }

    private char primeraLetra(String palabra) {
        String transformada = normalizar(eliminarNoLetras(palabra)).toUpperCase();
        if (transformada.isEmpty()) {
            return '#';
        } else {
            return transformada.charAt(0);
        }
    }


}
