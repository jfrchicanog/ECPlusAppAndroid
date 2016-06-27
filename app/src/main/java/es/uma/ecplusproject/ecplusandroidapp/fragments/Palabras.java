package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import es.uma.ecplusproject.ecplusandroidapp.DetallePalabra;
import es.uma.ecplusproject.ecplusandroidapp.MainActivity;
import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.Splash;
import es.uma.ecplusproject.ecplusandroidapp.modelo.CargarListaPalabras;
import es.uma.ecplusproject.ecplusandroidapp.modelo.DAO;
import es.uma.ecplusproject.ecplusandroidapp.modelo.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.restws.DescargaListaPalabras;

/**
 * A placeholder fragment containing a simple view.
 */
public class Palabras extends Panel {

    private ListView listaPalabras;
    private AdaptadorPalabras adaptador;
    private DAO dao;
    private String preferredLanguage;

    public Palabras() {
        super();
        dao = new DAO();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.palabras, container, false);
        listaPalabras = (ListView)rootView.findViewById(R.id.listaPalabras);
        adaptador = new AdaptadorPalabras(getContext());

        SharedPreferences preferences = getActivity().getSharedPreferences(Splash.ECPLUS_MAIN_PREFS, Context.MODE_PRIVATE);
        preferredLanguage = preferences.getString(MainActivity.PREFERRED_LANGUAGE, "cat");

        //poulateAdaptor();
        //populateAdaptorDB();
        poulateAdaptorREST();

        listaPalabras.setAdapter(adaptador);
        listaPalabras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detallePalabra = new Intent(getContext(),DetallePalabra.class);
                detallePalabra.putExtra(DetallePalabra.PALABRA, adaptador.getItem(position));
                startActivity(detallePalabra);
            }
        });



        return rootView;
    }

    private void poulateAdaptor() {
        for (Palabra palabra: dao.getPalabras()) {
            adaptador.add(palabra);
        }
    }

    private void poulateAdaptorREST() {
        new DescargaListaPalabras(adaptador).execute(preferredLanguage);
    }

    private void populateAdaptorDB() {
        new CargarListaPalabras(adaptador).execute(preferredLanguage);
    }



    @Override
    public String getFragmentName() {
        return contexto.getString(R.string.palabras);
    }
}
