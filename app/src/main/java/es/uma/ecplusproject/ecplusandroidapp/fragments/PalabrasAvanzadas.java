package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Iterator;
import java.util.List;

import javax.crypto.spec.DESedeKeySpec;

import es.uma.ecplusproject.ecplusandroidapp.DetallePalabra;
import es.uma.ecplusproject.ecplusandroidapp.MainActivity;
import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.Splash;
import es.uma.ecplusproject.ecplusandroidapp.modelo.CachePalabras;
import es.uma.ecplusproject.ecplusandroidapp.modelo.PalabrasDAO;
import es.uma.ecplusproject.ecplusandroidapp.modelo.PalabrasDAOImpl;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;

/**
 * A placeholder fragment containing a simple view.
 */
public class PalabrasAvanzadas extends Panel {

    private ListView listaPalabras;
    private AdaptadorPalabras adaptador;

    public PalabrasAvanzadas() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.palabras, container, false);
        listaPalabras = (ListView)rootView.findViewById(R.id.listaPalabras);
        adaptador = new AdaptadorPalabras(getContext());
        adaptador.setChangePictureListener((MainActivity)getActivity());

        populateAdaptorDBComplete();

        listaPalabras.setAdapter(adaptador);
        listaPalabras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detallePalabra = new Intent(getContext(),DetallePalabra.class);
                detallePalabra.putExtra(DetallePalabra.PALABRA, adaptador.getItem(position));
                startActivity(detallePalabra);
            }
        });

        listaPalabras.setFastScrollEnabled(true);

        return rootView;
    }

    private void populateAdaptorDBComplete() {
        new AsyncTask<Void, Void, List<Palabra>>(){
            @Override
            protected List<Palabra> doInBackground(Void... params) {
                List<Palabra> palabras = CachePalabras.getTheInstance().getPalabras();
                Iterator<Palabra> iterator = palabras.iterator();
                while (iterator.hasNext()) {
                    Palabra palabra = iterator.next();
                    if (!palabra.getAvanzada()) {
                        iterator.remove();
                    }
                }
                return palabras;
            }

            @Override
            protected void onPostExecute(List<Palabra> palabras) {
                if (adaptador!=null) {
                    adaptador.clear();
                    adaptador.addAll(palabras);
                }
            }
        }.execute();
    }

    public void reloadWords() {

        populateAdaptorDBComplete();

    }

    public void dataChanged() {
        if (adaptador !=null) {
            adaptador.notifyDataSetChanged();
        }
    }


    @Override
    public String getFragmentName() {
        return contexto.getString(R.string.palabrasAvanzadas);
    }
}
