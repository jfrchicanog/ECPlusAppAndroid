package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.modelo.DAO;
import es.uma.ecplusproject.ecplusandroidapp.modelo.Sindrome;

/**
 * A placeholder fragment containing a simple view.
 */
public class Sindromes extends Panel {
    private ListView listaSindromes;
    private ArrayAdapter<Sindrome> adaptador;
    private DAO dao;

    public Sindromes(Context ctx) {
        super(ctx);
        dao = new DAO();
    }

    @Override
    public String getFragmentName() {
        return ctx.getString(R.string.sindrome);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sindromes, container, false);
        listaSindromes = (ListView)rootView.findViewById(R.id.listaSindromes);
        adaptador = new ArrayAdapter<Sindrome>(ctx, android.R.layout.simple_list_item_1,dao.getSindromes());
        listaSindromes.setAdapter(adaptador);
        return rootView;
    }
}
