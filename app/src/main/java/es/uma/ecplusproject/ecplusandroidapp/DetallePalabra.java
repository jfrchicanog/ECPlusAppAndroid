package es.uma.ecplusproject.ecplusandroidapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.VideoView;

import es.uma.ecplusproject.ecplusandroidapp.modelo.Fotografia;
import es.uma.ecplusproject.ecplusandroidapp.modelo.Pictograma;

/**
 * Created by francis on 20/4/16.
 */
public class DetallePalabra extends AppCompatActivity {

    private VideoView video;
    private GridView gridView;
    private AdaptadorImagenes adaptador;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detallepalabra);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        video = (VideoView)findViewById(R.id.video);
        gridView = (GridView)findViewById(R.id.imagenes);

        adaptador = new AdaptadorImagenes(this);
        gridView.setAdapter(adaptador);

        adaptador.add(new Pictograma(getResources().getDrawable(R.drawable.manzana)));
        adaptador.add(new Fotografia(getResources().getDrawable(R.drawable.manzanafoto)));

        video.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video));

        video.start();
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video.isPlaying()) {
                    video.pause();
                } else {
                    video.resume();
                }
            }
        });

    }
}
