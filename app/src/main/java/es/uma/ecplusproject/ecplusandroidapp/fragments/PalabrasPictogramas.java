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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.DetallePalabra;
import es.uma.ecplusproject.ecplusandroidapp.MainActivity;
import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.Splash;
import es.uma.ecplusproject.ecplusandroidapp.modelo.CachePalabras;
import es.uma.ecplusproject.ecplusandroidapp.modelo.PalabrasDAO;
import es.uma.ecplusproject.ecplusandroidapp.modelo.PalabrasDAOImpl;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Category;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Pictograma;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;

/**
 * A placeholder fragment containing a simple view.
 */
public class PalabrasPictogramas extends Panel {


    private Comparator<Palabra> comparadorCategorias = new Comparator<Palabra>() {
        @Override
        public int compare(Palabra p1, Palabra p2) {
            if (p1.getCategoria()!=null && p2.getCategoria()==null){
                return -1;
            } else if (p1.getCategoria()==null && p2.getCategoria()!=null) {
                return 1;
            } else if (p1.getCategoria()==null && p2.getCategoria()==null) {
                return p1.getNombre().compareTo(p2.getNombre());
            } else {
                int compCat = p1.getCategoria().getNombre().compareTo(p2.getCategoria().getNombre());
                if (compCat != 0) {
                    return compCat;
                } else {
                    return p1.getNombre().compareTo(p2.getNombre());
                }
            }
        }
    };

    private RecyclerView listaPalabras;
    private AdaptadorPictogramas adaptador;
    private String preferredLanguage;
    private float padding;
    private SectionedGridRecyclerViewAdapter mSectionedAdapter;

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

        populateAdaptorDBComplete();

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


        //Add your adapter to the sectionAdapter
        mSectionedAdapter = new
                SectionedGridRecyclerViewAdapter(getActivity(), R.layout.section,R.id.section_text,listaPalabras,adaptador);

        //Apply this adapter to the RecyclerView
        listaPalabras.setAdapter(mSectionedAdapter);

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
                    boolean icon = false;
                    if (palabra.getIcono()!=null && palabra.getIcono() instanceof Pictograma) {
                        icon = true;
                    } else if (palabra.getIconoPersonalizado()!=null) {
                        icon = true;
                    }  else {
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

                Collections.sort(palabras,comparadorCategorias);

                adaptador.clear();
                adaptador.addAll(palabras);

                List<SectionedGridRecyclerViewAdapter.Section> sections =
                        new ArrayList<SectionedGridRecyclerViewAdapter.Section>();

                if (palabras.size() > 0) {
                    Category previousCategory = palabras.get(0).getCategoria();
                    sections.add(new SectionedGridRecyclerViewAdapter.Section(0, nameFromCategory(previousCategory)));

                    for (int i=0; i < palabras.size(); i++) {
                        Category nuevaCategoria = palabras.get(i).getCategoria();
                        if (nuevaCategoria != previousCategory) {
                            sections.add(new SectionedGridRecyclerViewAdapter.Section(i, nameFromCategory(nuevaCategoria)));
                            previousCategory = nuevaCategoria;
                        }
                    }
                }

                SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];
                mSectionedAdapter.setSections(sections.toArray(dummy));

            }
        }.execute();
    }

    private String nameFromCategory(Category cat) {
        if (cat!=null) {
            return cat.getNombre();
        } else {
            return getResources().getString(R.string.noCategory);
        }
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
        return contexto.getString(R.string.pictograms);
    }
}
