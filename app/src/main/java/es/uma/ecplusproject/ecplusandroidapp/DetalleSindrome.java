package es.uma.ecplusproject.ecplusandroidapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Created by francis on 20/4/16.
 */
public class DetalleSindrome extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detallesindrome);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        WebView web = (WebView)findViewById(R.id.webView);
        InputStreamReader reader = new InputStreamReader(getResources().openRawResource(R.raw.angelman));
        StringWriter writer = new StringWriter();
        int car = 0;
        try {
            while ((car = reader.read()) != -1) {
                writer.append((char)car);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        web.loadData(writer.toString(), "text/html; charset=UTF-8", null);

    }
}
