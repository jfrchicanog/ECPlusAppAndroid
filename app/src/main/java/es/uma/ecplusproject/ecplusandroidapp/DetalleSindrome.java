package es.uma.ecplusproject.ecplusandroidapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;


import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Sindrome;

/**
 * Created by francis on 20/4/16.
 */
public class DetalleSindrome extends AppCompatActivity {

    public static final String SINDROME = "Sindrome";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detallesindrome);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        WebView web = (WebView)findViewById(R.id.webView);
        Sindrome sindrome = (Sindrome) getIntent().getSerializableExtra(SINDROME);
        web.loadData(sindrome.getDescripcion(), "text/html; charset=UTF-8", null);
    }
}
