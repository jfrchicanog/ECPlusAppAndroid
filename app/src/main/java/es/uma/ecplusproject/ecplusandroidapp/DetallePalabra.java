package es.uma.ecplusproject.ecplusandroidapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

import es.uma.ecplusproject.ecplusandroidapp.modelo.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.Video;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detallepalabra);

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

        hashesDir = new File(getFilesDir(), HASHES);

        Palabra palabra = (Palabra) getIntent().getSerializableExtra(PALABRA);
        nombre = (TextView) findViewById(R.id.nombre);
        nombre.setText(palabra.getNombre());


        for (RecursoAV rav: palabra.getRecursos()) {
            if (rav instanceof  Video) {
                File file = decompressFileIfNotReady(rav.getHash());
                video.setVideoPath(file.getPath());

                //video.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video));

                //video.start();

            } else {
                adaptador.add(rav);
            }
        }

        //adaptador.add(new Pictograma(getResources().getDrawable(R.drawable.manzana)));
        //adaptador.add(new Fotografia(getResources().getDrawable(R.drawable.manzanafoto)));

    }

    private File decompressFileIfNotReady(String hash) {
        try {
            createHashesDirIfNecessary();
            File file = new File(hashesDir, hash.toLowerCase());
            if (!file.exists()) {
                InputStream is = obtainResourceFromOBB(hash);
                OutputStream os = new FileOutputStream(file);
                byte [] buffer = new byte [1024];
                int leidos;

                while ((leidos=is.read(buffer))>0) {
                    os.write(buffer, 0, leidos);
                }

                is.close();
                os.close();

            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream obtainResourceFromOBB(String hash) throws IOException {
        return APKExpansionSupport.getAPKExpansionZipFile(this, Splash.MAIN_VERSION, 0).getInputStream(hash.toLowerCase());
    }

    private void createHashesDirIfNecessary() {
        if (!hashesDir.exists()) {
            if (!hashesDir.mkdir()) {
                throw new RuntimeException("I was not able to create the directory "+hashesDir.getAbsolutePath());
            }
        }
    }
}
