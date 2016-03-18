package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Panel;
import es.uma.ecplusproject.ecplusandroidapp.modelo.DAO;
import es.uma.ecplusproject.ecplusandroidapp.modelo.Palabra;

/**
 * A placeholder fragment containing a simple view.
 */
public class Palabras extends Panel {

    private ListView listaPalabras;
    private ArrayAdapter<Palabra> adaptador;
    private DAO dao;

    public Palabras(Context ctx) {
        super(ctx);
        dao = new DAO();
    }

    @Override
    public String getFragmentName() {
        return ctx.getString(R.string.palabras);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.palabras, container, false);
        listaPalabras = (ListView)rootView.findViewById(R.id.listaPalabras);
        adaptador = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1,dao.getPalabras());
        listaPalabras.setAdapter(adaptador);
        return rootView;
    }
}
