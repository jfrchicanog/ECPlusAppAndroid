package es.uma.ecplusproject.ecplusandroidapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDB;
import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDBContract;
import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDBHelper;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Palabras;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Panel;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Sindromes;
import es.uma.ecplusproject.ecplusandroidapp.modelo.CargarListaPalabras;
import es.uma.ecplusproject.ecplusandroidapp.modelo.DAO;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DAO.setContext(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                getPanelPalabras(), getPanelSindromes());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        ECPlusDB.setContext(this);

        System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, "archivo.obb");
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(file));
            pw.println("hola caracola");
            pw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        // Pruebas de la BBDD
/*
        ECPlusDBHelper helper = new ECPlusDBHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        String [] projection = new String [] {ECPlusDBContract.Ficheros.RESOLUCION,
                ECPlusDBContract.Ficheros.HASH, ECPlusDBContract.Ficheros.REF_RECURSO_AUDIOVISUAL};
        Cursor c = database.query(ECPlusDBContract.Ficheros.TABLE_NAME, projection, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                String hash = c.getString(c.getColumnIndex(ECPlusDBContract.Ficheros.HASH));
                String resolucion = c.getString(c.getColumnIndex(ECPlusDBContract.Ficheros.RESOLUCION));
                Long ravId = c.getLong(c.getColumnIndex(ECPlusDBContract.Ficheros.REF_RECURSO_AUDIOVISUAL));
                System.out.println(ravId+", "+hash+", "+resolucion);
            } while (c.moveToNext());
        }
*/
    }

    @NonNull
    private Palabras getPanelPalabras() {
        Palabras palabras = new Palabras();
        palabras.setContext(this);
        return palabras;
    }

    @NonNull
    private Sindromes getPanelSindromes() {
        Sindromes sindromes = new Sindromes();
        sindromes.setContext(this);
        return sindromes;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
