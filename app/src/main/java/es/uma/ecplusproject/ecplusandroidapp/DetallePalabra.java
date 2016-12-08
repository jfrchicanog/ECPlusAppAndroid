package es.uma.ecplusproject.ecplusandroidapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.GridView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.vending.expansion.zipfile.APKExpansionSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Video;
import es.uma.ecplusproject.ecplusandroidapp.services.ResourcesStore;

/**
 * Created by francis on 20/4/16.
 */
public class DetallePalabra extends AppCompatActivity {

    public static final String PALABRA = "palabra";
    private VideoView video;
    private GridView gridView;
    private AdaptadorRecursos adaptador;
    private TextView nombre;
    private Resolucion resolution = Resolucion.BAJA;
    private ResourcesStore resourcesStore;

    private RecyclerView recursos;
    private RecyclerView.Adapter mAdapter;
    private Comparator<RecursoAV> comparator = new Comparator<RecursoAV>() {
        @Override
        public int compare(RecursoAV lhs, RecursoAV rhs) {
            boolean lhsVideo = lhs instanceof Video;
            boolean rhsVideo = rhs instanceof Video;
            if (lhsVideo && !rhsVideo) {
                return -1;
            } else if (rhsVideo && !lhsVideo) {
                return 1;
            } else {
                return 0;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detallepalabra);

        resourcesStore = new ResourcesStore(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Palabra palabra = (Palabra) getIntent().getSerializableExtra(PALABRA);
        nombre = (TextView) findViewById(R.id.nombre);
        nombre.setText(palabra.getNombre());

        recursos = (RecyclerView) findViewById(R.id.recursosAV);
        recursos.setHasFixedSize(true);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recursos.setLayoutManager(mLayoutManager);

        adaptador = new AdaptadorRecursos(this);
        List<RecursoAV> listaRecursos = palabra.getRecursos();

        Collections.sort(listaRecursos, comparator);
        adaptador.setRecursos(listaRecursos);

        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adaptador.getItemViewType(position)==AdaptadorRecursos.TIPO_VIDEO?2:1;
            }
        });

        recursos.setAdapter(adaptador);

    }

}
