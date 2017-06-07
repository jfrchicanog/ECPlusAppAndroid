package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;

import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
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
public class AdaptadorPictogramas extends ArrayAdapter<Palabra> {

    private Map<Character, Integer> letraASeccion;
    private Object [] seccionALetra;
    private Integer [] inicioSeccion;

    private final Comparator<String> comparador;
    private final Collator collator;

    private static class ViewHolder {
        private TextView texto;
        private SVGImageView icono;
    }

    private Context contexto;
    private Resolucion resolucion;
    private ResourcesStore resourceStore;

    public AdaptadorPictogramas(Context contexto) {
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

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("AdaptadorPicto", ""+position);
        Palabra palabra = getItem(position);
        View view=null;
        if (convertView == null) {
            view = LayoutInflater.from(contexto).inflate(R.layout.entradapictograma, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.icono = (SVGImageView)view.findViewById(R.id.imagenPalabra);
            view.setTag(viewHolder);

        } else {
            view = convertView;
        }

        ViewHolder viewHolder = (ViewHolder)view.getTag();

        //imagen.setImageDrawable(contexto.getResources().getDrawable(R.drawable.abrigo));
        SVGImageView icono = viewHolder.icono;

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

        return view;
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