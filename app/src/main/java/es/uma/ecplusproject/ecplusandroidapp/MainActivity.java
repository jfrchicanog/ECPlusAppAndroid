package es.uma.ecplusproject.ecplusandroidapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.uma.ecplusproject.ecplusandroidapp.dialogs.ChangePictureDialog;
import es.uma.ecplusproject.ecplusandroidapp.dialogs.ChooseLanguageDialog;
import es.uma.ecplusproject.ecplusandroidapp.fragments.ChangePictureListener;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Comunicacion;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Palabras;
import es.uma.ecplusproject.ecplusandroidapp.fragments.PalabrasAvanzadas;
import es.uma.ecplusproject.ecplusandroidapp.fragments.PalabrasPictogramas;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Sindromes;
import es.uma.ecplusproject.ecplusandroidapp.modelo.CachePalabras;
import es.uma.ecplusproject.ecplusandroidapp.modelo.PalabrasDAO;
import es.uma.ecplusproject.ecplusandroidapp.modelo.PalabrasDAOImpl;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.services.ResourcesStore;
import es.uma.ecplusproject.ecplusandroidapp.services.UpdateListener;
import es.uma.ecplusproject.ecplusandroidapp.services.UpdateListenerEvent;
import es.uma.ecplusproject.ecplusandroidapp.services.UpdateService;

public class MainActivity extends AppCompatActivity implements ChangePictureListener {

    public static final String PREFERRED_LANGUAGE = "preferred language";
    public static final String DEFAULT_LANGUAGE = "es";
    private static final String TAG="EC+ MainActivity";
    private static final int REQUEST_TAKE_PICTURE=1;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private Palabra palabraToAddCustomizedPicture;
    private String mCurrentPhotoPath;
    private ResourcesStore resourcesStore;
    private UpdateService service;

    private Sindromes panelSindromes;
    private Comunicacion panelComunicacion;
    private Palabras panelPalabras;
    private PalabrasAvanzadas panelPalabrasAvanzadas;
    private PalabrasPictogramas panelPalabrasPictogramas;
    private ProgressBar barraProgreso;
    private PalabrasDAO daoPalabras;

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
                CachePalabras.getTheInstance().clearCache();
                getPanelSindromes().reloadSyndromes();
                getPanelComunicacion().reloadSyndromes();
            } else if (UpdateListenerEvent.Element.WORDS.equals(event.getElement())
                    && UpdateListenerEvent.Action.STOP_DATABASE.equals(event.getAction())
                    && event.isSomethingChanged()) {
                CachePalabras.getTheInstance().clearCache();
                getPanelPalabras().reloadWords();
                getPanelPalabrasAvanzadas().reloadWords();
                getPanelPictogramas().reloadWords();
            } else if (UpdateListenerEvent.Element.WORDS.equals(event.getElement())
                    && UpdateListenerEvent.Action.STOP_FILE.equals(event.getAction())) {
                getPanelPalabras().dataChanged();
                getPanelPalabrasAvanzadas().dataChanged();
                getPanelPictogramas().dataChanged();
            }
        }
    };


    private PalabrasDAO getDAOPalabras() {
        if (daoPalabras==null) {
            daoPalabras = new PalabrasDAOImpl(this);
        }
        return daoPalabras;
    }

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

        resourcesStore = new ResourcesStore(this);

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

            CachePalabras.getTheInstance().clearCache();

            finish();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

    }
    @Override
    public void requestToChangePictureForWord(Palabra palabra) {
        ChangePictureDialog dialog = new ChangePictureDialog();
        dialog.setPalabra(palabra);
        dialog.setOnSourceSelectionListener(new ChangePictureDialog.OnSourceSelectionListener() {
            @Override
            public void onSourceSelection(Palabra palabra, ChangePictureDialog.PictureSource source) {
                switch (source) {
                    case RESTORE:
                        palabra.setIconoPersonalizado(null);
                        getDAOPalabras().updateWord(palabra);
                        updatePanels();
                        break;
                    case CAMERA:
                        palabraToAddCustomizedPicture=palabra;
                        takePictureWithCamera();
                        break;
                }
            }
        });

        dialog.show(getSupportFragmentManager(),"Change Picture for Word");
    }

    private void updatePanels() {
        panelPalabras.dataChanged();
        panelPalabrasAvanzadas.dataChanged();
        panelPalabrasPictogramas.reloadWords();
    }

    private void takePictureWithCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            mCurrentPhotoPath = null;
            try {
                photoFile = createImageFile();
                mCurrentPhotoPath = photoFile.getName();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "es.uma.ecplusproject.ecplusandroidapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "EC+_" + timeStamp + "_.jpg";
        File image = resourcesStore.getFileResource(imageFileName);

        /*File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );*/

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_TAKE_PICTURE && resultCode==RESULT_OK) {
            palabraToAddCustomizedPicture.setIconoPersonalizado(mCurrentPhotoPath);
            getDAOPalabras().updateWord(palabraToAddCustomizedPicture);
            updatePanels();

        }
    }
}
