package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import java.util.Iterator;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.DetallePalabra;
import es.uma.ecplusproject.ecplusandroidapp.MainActivity;
import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.Splash;
import es.uma.ecplusproject.ecplusandroidapp.modelo.PalabrasDAO;
import es.uma.ecplusproject.ecplusandroidapp.modelo.PalabrasDAOImpl;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Pictograma;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;

/**
 * A placeholder fragment containing a simple view.
 */
public class PalabrasPictogramas extends Panel {

    private RecyclerView listaPalabras;
    private AdaptadorPictogramas adaptador;
    private String preferredLanguage;
    private float padding;

    public PalabrasPictogramas() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pictogramas, container, false);
        listaPalabras = (RecyclerView)rootView.findViewById(R.id.listaPalabras);
        adaptador = new AdaptadorPictogramas(getContext());

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        float columnWidth = getContext().getResources().getDimension(R.dimen.anchoColumnaGrid);
        int numColumns = (int)Math.floor(metrics.widthPixels/columnWidth);

        padding = getContext().getResources().getDimension(R.dimen.gridPadding);

        GridLayoutManager layout = new GridLayoutManager(getContext(), numColumns);
        listaPalabras.setLayoutManager(layout);


        SharedPreferences preferences = getActivity().getSharedPreferences(Splash.ECPLUS_MAIN_PREFS, Context.MODE_PRIVATE);
        preferredLanguage = preferences.getString(MainActivity.PREFERRED_LANGUAGE, MainActivity.DEFAULT_LANGUAGE);

        populateAdaptorDBComplete();

        listaPalabras.setAdapter(adaptador);
        listaPalabras.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom=(int)padding;
                outRect.left=(int)padding;
            }
        });

        adaptador.setOnClickPalabraListener(new AdaptadorPictogramas.ClickPalabraListener() {
            @Override
            public void onClickPalabra(Palabra palabra) {
                Intent detallePalabra = new Intent(getContext(),DetallePalabra.class);
                detallePalabra.putExtra(DetallePalabra.PALABRA, palabra);
                startActivity(detallePalabra);
            }
        });

        /*
        listaPalabras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detallePalabra = new Intent(getContext(),DetallePalabra.class);
                detallePalabra.putExtra(DetallePalabra.PALABRA, adaptador.getItem(position));
                startActivity(detallePalabra);
            }
        });*/

        //listaPalabras.setFastScrollEnabled(true);

        return rootView;
    }

    private void populateAdaptorDBComplete() {
        final PalabrasDAO daoPalabras =new PalabrasDAOImpl();
        new AsyncTask<Void, Void, List<Palabra>>(){
            @Override
            protected List<Palabra> doInBackground(Void... params) {
                List<Palabra> palabras = daoPalabras.getWords(preferredLanguage, Resolucion.BAJA);
                Iterator<Palabra> iterator = palabras.iterator();
                while (iterator.hasNext()) {
                    Palabra palabra = iterator.next();
                    boolean icon = false;
                    if (palabra.getIcono()!=null && palabra.getIcono() instanceof Pictograma) {
                        icon = true;
                    } else {
                        for (RecursoAV recurso : palabra.getRecursos()) {
                            if (recurso instanceof Pictograma) {
                                icon = true;
                            }
                        }

                    }
                    if (!icon) {
                        iterator.remove();
                    }

                }
                return palabras;
            }

            @Override
            protected void onPostExecute(List<Palabra> palabras) {
                Log.d(getClass().toString(), "palabras:"+palabras.size());
                adaptador.clear();
                adaptador.addAll(palabras);
            }
        }.execute();
    }

    public void reloadWords() {
        if (preferredLanguage!=null) {
            populateAdaptorDBComplete();
        }
    }


    @Override
    public String getFragmentName() {
        return contexto.getString(R.string.pictograms);
    }
}
