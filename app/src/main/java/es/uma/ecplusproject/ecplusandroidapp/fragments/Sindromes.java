package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import es.uma.ecplusproject.ecplusandroidapp.DetalleSindrome;
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


    public Sindromes() {
        super();
        dao = new DAO();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sindromes, container, false);
        listaSindromes = (ListView)rootView.findViewById(R.id.listaSindromes);
        adaptador = new ArrayAdapter<Sindrome>(getContext(), android.R.layout.simple_list_item_1,dao.getSindromes());
        listaSindromes.setAdapter(adaptador);
        listaSindromes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detalleSindrome = new Intent(getContext(), DetalleSindrome.class);
                startActivity(detalleSindrome);
            }
        });
        return rootView;
    }
}
