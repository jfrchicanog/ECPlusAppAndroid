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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Iterator;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.DetalleSindrome;
import es.uma.ecplusproject.ecplusandroidapp.MainActivity;
import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.Splash;
import es.uma.ecplusproject.ecplusandroidapp.modelo.SindromesDAO;
import es.uma.ecplusproject.ecplusandroidapp.modelo.SindromesDAOImpl;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Sindrome;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.TipoDocumento;

/**
 * A placeholder fragment containing a simple view.
 */
public class Comunicacion extends Panel {
    private ListView listaSindromes;
    private ArrayAdapter<Sindrome> adaptador;
    private String preferredLanguage;

    public Comunicacion() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sindromes, container, false);
        listaSindromes = (ListView)rootView.findViewById(R.id.listaSindromes);

        adaptador = new ArrayAdapter<Sindrome>(getContext(), android.R.layout.simple_list_item_1);

        SharedPreferences preferences = getActivity().getSharedPreferences(Splash.ECPLUS_MAIN_PREFS, Context.MODE_PRIVATE);
        preferredLanguage = preferences.getString(MainActivity.PREFERRED_LANGUAGE, MainActivity.DEFAULT_LANGUAGE);


        reloadSyndromes();

        listaSindromes.setAdapter(adaptador);
        listaSindromes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detalleSindrome = new Intent(getContext(), DetalleSindrome.class);
                detalleSindrome.putExtra(DetalleSindrome.SINDROME, adaptador.getItem(position));
                startActivity(detalleSindrome);
            }
        });


        return rootView;
    }

    private void populateAdaptorDBComplete() {
        final SindromesDAO daoSindromes =new SindromesDAOImpl(getContext());
        new AsyncTask<Void, Void, List<Sindrome>>(){
            @Override
            protected List<Sindrome> doInBackground(Void... params) {
                List<Sindrome> sindromes = daoSindromes.getSindromes(preferredLanguage);
                Iterator<Sindrome> iterator = sindromes.iterator();
                while (iterator.hasNext()) {
                    Sindrome sindrome = iterator.next();
                    if (!TipoDocumento.GENERALIDAD.equals(sindrome.getTipo())) {
                        iterator.remove();
                    }
                }
                return sindromes;
            }

            @Override
            protected void onPostExecute(List<Sindrome> sindromes) {
                adaptador.clear();
                adaptador.addAll(sindromes);
            }
        }.execute();
    }

    public void reloadSyndromes() {
        if (preferredLanguage != null) {
            populateAdaptorDBComplete();
        }
    }

    @Override
    public String getFragmentName() {
        return contexto.getString(R.string.communication);
    }
}
