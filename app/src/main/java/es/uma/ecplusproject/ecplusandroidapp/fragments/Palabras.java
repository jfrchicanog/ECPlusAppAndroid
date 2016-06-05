package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import es.uma.ecplusproject.ecplusandroidapp.DetallePalabra;
import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Panel;
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

        poulateAdaptorREST();

        listaPalabras.setAdapter(adaptador);
        listaPalabras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detallePalabra = new Intent(getContext(),DetallePalabra.class);
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

        new DescargaListaPalabras(adaptador).execute();


    }

    @Override
    public String getFragmentName() {
        return contexto.getString(R.string.palabras);
    }
}
