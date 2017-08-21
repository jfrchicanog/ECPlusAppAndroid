package es.uma.ecplusproject.ecplusandroidapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import es.uma.ecplusproject.ecplusandroidapp.dialogs.ChangePictureDialog;
import es.uma.ecplusproject.ecplusandroidapp.dialogs.ChooseLanguageDialog;
import es.uma.ecplusproject.ecplusandroidapp.fragments.ChangePictureListener;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Comunicacion;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Palabras;
import es.uma.ecplusproject.ecplusandroidapp.fragments.PalabrasAvanzadas;
import es.uma.ecplusproject.ecplusandroidapp.fragments.PalabrasPictogramas;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Sindromes;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.services.UpdateListener;
import es.uma.ecplusproject.ecplusandroidapp.services.UpdateListenerEvent;
import es.uma.ecplusproject.ecplusandroidapp.services.UpdateService;

public class MainActivity extends AppCompatActivity implements ChangePictureListener {

    public static final String PREFERRED_LANGUAGE = "preferred language";
    public static final String DEFAULT_LANGUAGE = "es";
    private static final String TAG="EC+ MainActivity";
    public static final String NOMBRE_PALABRA = "nombrePalabra";
    public static final String CUSTOMIZED_ICON = "customizedIcon";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private UpdateService service;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((UpdateService.UpdateServiceBinder)binder).getService();
            service.addUpdateListener(updateListener);
            updateProgressBar();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service.removeUpdateListener(updateListener);
            service=null;
        }
    };

    private UpdateListener updateListener = new UpdateListener() {
        @Override
        public void onUpdateEvent(UpdateListenerEvent event) {
            reportUpdateEvent(event);
            updateProgressBar();

            if (UpdateListenerEvent.Element.SYNDROMES.equals(event.getElement()) &&
                    event.isSomethingChanged()) {
                getPanelSindromes().reloadSyndromes();
            } else if (UpdateListenerEvent.Element.WORDS.equals(event.getElement())
                    && UpdateListenerEvent.Action.STOP_DATABASE.equals(event.getAction())
                    && event.isSomethingChanged()) {
                getPanelPalabras().reloadWords();
                getPanelPalabrasAvanzadas().reloadWords();
            }
        }
    };


    private Sindromes panelSindromes;
    private Comunicacion panelComunicacion;
    private Palabras panelPalabras;
    private PalabrasAvanzadas panelPalabrasAvanzadas;
    private PalabrasPictogramas panelPalabrasPictogramas;
    private ProgressBar barraProgreso;

    private void reportUpdateEvent(UpdateListenerEvent event) {
        String cadena = event.getAction()+" "
                + event.getElement()+" "
                +event.isSomethingChanged();

        Log.d(TAG, cadena);
    }

    private void updateProgressBar() {
        if (service != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    barraProgreso.setVisibility(service.isUpdating()?View.VISIBLE:View.GONE);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barraProgreso = (ProgressBar) findViewById(R.id.progressBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                getPanelPictogramas(), getPanelPalabras(), getPanelPalabrasAvanzadas(), getPanelSindromes(), getPanelComunicacion());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        updateDatabase();

    }

    private void updateDatabase() {
        SharedPreferences preferences = getSharedPreferences(Splash.ECPLUS_MAIN_PREFS, Context.MODE_PRIVATE);
        String preferredLanguage = preferences.getString(MainActivity.PREFERRED_LANGUAGE, DEFAULT_LANGUAGE);

        UpdateService.startUpdateSyndromes(this, preferredLanguage);
        UpdateService.startUpdateWords(this, preferredLanguage, Resolucion.BAJA.toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, UpdateService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (service!=null) {
            service.removeUpdateListener(updateListener);
            unbindService(connection);
        }
    }

    @NonNull
    private Palabras getPanelPalabras() {
        if (panelPalabras == null) {
            panelPalabras = new Palabras();
            panelPalabras.setContext(this);
        }
        return panelPalabras;
    }

    @NonNull
    private PalabrasPictogramas getPanelPictogramas() {
        if (panelPalabrasPictogramas == null) {
            panelPalabrasPictogramas = new PalabrasPictogramas();
            panelPalabrasPictogramas.setContext(this);
        }
        return panelPalabrasPictogramas;
    }

    @NonNull
    private PalabrasAvanzadas getPanelPalabrasAvanzadas() {
        if (panelPalabrasAvanzadas == null) {
            panelPalabrasAvanzadas = new PalabrasAvanzadas();
            panelPalabrasAvanzadas.setContext(this);
        }
        return panelPalabrasAvanzadas;
    }

    @NonNull
    private Sindromes getPanelSindromes() {
        if (panelSindromes == null) {
            panelSindromes = new Sindromes();
            panelSindromes.setContext(this);
        }
        return panelSindromes;
    }

    @NonNull
    private Comunicacion getPanelComunicacion() {
        if (panelComunicacion == null) {
            panelComunicacion = new Comunicacion();
            panelComunicacion.setContext(this);
        }
        return panelComunicacion;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_language:
                ChooseLanguageDialog dialogo = new ChooseLanguageDialog();
                dialogo.show(getSupportFragmentManager(),"Choose Language");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeLanguage(String localeCode) {
        SharedPreferences preferences = getSharedPreferences(Splash.ECPLUS_MAIN_PREFS, MODE_PRIVATE);
        if (!preferences.contains(PREFERRED_LANGUAGE) || !preferences.getString(PREFERRED_LANGUAGE,"").equals(localeCode)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREFERRED_LANGUAGE, localeCode);
            editor.commit();

            finish();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

    }
    @Override
    public void requestToChangePictureForWord(Palabra palabra) {
        ChangePictureDialog dialog = new ChangePictureDialog();
        Bundle arguments = new Bundle();
        arguments.putString(NOMBRE_PALABRA, palabra.getNombre());
        arguments.putBoolean(CUSTOMIZED_ICON, palabra.getIconoPersonalizado()!=null);
        dialog.setArguments(arguments);

        dialog.show(getSupportFragmentManager(),"Change Picture for Word");
    }
}
