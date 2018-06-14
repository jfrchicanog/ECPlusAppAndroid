package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

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
public class Palabras extends Panel {

    private final PalabrasDAOImpl palabrasDAO = new PalabrasDAOImpl();
    private ListView listaPalabras;
    private AdaptadorPalabras adaptador;

    public Palabras() {
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
                Palabra palabra = adaptador.getItem(position);
                palabra.incrementAccesos();
                palabrasDAO.updateUso(palabra);
                detallePalabra.putExtra(DetallePalabra.PALABRA, palabra);
                startActivity(detallePalabra);
                adaptador.touchOrder();
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
                    if (palabra.getAvanzada()!=null && palabra.getAvanzada()) {
                        iterator.remove();
                    }
                }
                return palabras;
            }

            @Override
            protected void onPostExecute(List<Palabra> palabras) {
                Log.d(getClass().toString(), "palabras:"+palabras.size());
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
        return contexto.getString(R.string.palabras);
    }

}
