package es.uma.ecplusproject.ecplusandroidapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDB;
import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDBContract;
import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDBHelper;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Splash extends AppCompatActivity {

    public static final String ECPLUS_MAIN_PREFS = "ecplus-main";
    public static final String LANGUAGES_KEY_PREFS = "languages";
    private View mContentView;
    private boolean activityAlive;

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        activityAlive = true;

        mContentView = findViewById(R.id.fullscreen_content);

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        new PreparaDB().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityAlive = false;
    }

    private class PreparaDB extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ECPlusDB.setContext(Splash.this);
            ECPlusDBHelper helper = new ECPlusDBHelper(Splash.this);
            SQLiteDatabase db = helper.getWritableDatabase();

            SharedPreferences preferences = getSharedPreferences(ECPLUS_MAIN_PREFS, MODE_PRIVATE);
            if (!preferences.contains(LANGUAGES_KEY_PREFS)) {
                // Consulta el idioma de los recursos
                // y los guarda en las preferencias
                Set<String> idiomas = new HashSet<>();
                idiomasPalabras(db, idiomas);
                idiomasSindromes(db, idiomas);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putStringSet(LANGUAGES_KEY_PREFS, idiomas);
                editor.commit();
            }

            return null;
        }

        private void idiomasSindromes(SQLiteDatabase db, Set<String> idiomas) {
            Cursor c;

            c = db.query(true, ECPlusDBContract.ListaSindromes.TABLE_NAME, new String[]{
                    ECPlusDBContract.ListaSindromes.IDIOMA},null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    idiomas.add(c.getString(c.getColumnIndex(ECPlusDBContract.ListaSindromes.IDIOMA)));
                } while (c.moveToNext());
            }
            c.close();
        }

        private void idiomasPalabras(SQLiteDatabase db, Set<String> idiomas) {
            Cursor c = db.query(true, ECPlusDBContract.ListaPalabras.TABLE_NAME, new String[]{
                    ECPlusDBContract.ListaPalabras.IDIOMA},null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    idiomas.add(c.getString(c.getColumnIndex(ECPlusDBContract.ListaPalabras.IDIOMA)));
                } while (c.moveToNext());
            }
            c.close();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (activityAlive) {
                finish();
                Intent i = new Intent(Splash.this, MainActivity.class);
                startActivity(i);
            }
        }
    }

}
