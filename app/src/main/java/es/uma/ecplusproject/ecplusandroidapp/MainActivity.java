package es.uma.ecplusproject.ecplusandroidapp;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDB;
import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDBContract;
import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDBHelper;
import es.uma.ecplusproject.ecplusandroidapp.dialogs.ChooseLanguageDialog;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Palabras;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Panel;
import es.uma.ecplusproject.ecplusandroidapp.fragments.Sindromes;
import es.uma.ecplusproject.ecplusandroidapp.modelo.CargarListaPalabras;
import es.uma.ecplusproject.ecplusandroidapp.modelo.DAO;

public class MainActivity extends AppCompatActivity {

    public static final String PREFERRED_LANGUAGE = "preferred language";
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
}
