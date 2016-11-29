package es.uma.ecplusproject.ecplusandroidapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Video;
import es.uma.ecplusproject.ecplusandroidapp.services.ResourcesStore;

/**
 * Created by francis on 20/4/16.
 */
public class DetallePalabra extends AppCompatActivity {

    public static final String HASHES = "hashes";
    public static final String PALABRA = "palabra";
    private VideoView video;
    private GridView gridView;
    private AdaptadorImagenes adaptador;
    private File hashesDir;
    private TextView nombre;
    private Resolucion resolution = Resolucion.BAJA;
    private ResourcesStore resourcesStore;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detallepalabra);

        resourcesStore = new ResourcesStore(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        video = (VideoView)findViewById(R.id.video);

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                MediaController mediaController = new MediaController(DetallePalabra.this);
                mediaController.setAnchorView(video);
                video.setMediaController(mediaController);
            }
        });


        gridView = (GridView)findViewById(R.id.imagenes);

        adaptador = new AdaptadorImagenes(this);
        gridView.setAdapter(adaptador);

        Palabra palabra = (Palabra) getIntent().getSerializableExtra(PALABRA);
        nombre = (TextView) findViewById(R.id.nombre);
        nombre.setText(palabra.getNombre());


        for (RecursoAV rav: palabra.getRecursos()) {
            if (rav instanceof  Video) {
                File file = resourcesStore.getFileResource(rav.getFicheros().get(resolution));
                if (file.exists()) {
                    video.setVideoPath(file.getPath());
                }
            } else {
                adaptador.add(rav);
            }
        }
    }

}
