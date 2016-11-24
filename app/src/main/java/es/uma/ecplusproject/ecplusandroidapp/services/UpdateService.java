package es.uma.ecplusproject.ecplusandroidapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Comparator;

import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDB;
import es.uma.ecplusproject.ecplusandroidapp.database.ECPlusDBContract;
import es.uma.ecplusproject.ecplusandroidapp.restws.webservice.PalabraRes;


public class UpdateService extends IntentService {
    private static final String UPDATE_WORDS = "es.uma.ecplusproject.ecplusandroidapp.services.action.update.words";
    private static final String UPDATE_SYNDROMES = "es.uma.ecplusproject.ecplusandroidapp.services.action.update.syndromes";

    private static final String EXTRA_LANGUAGE = "es.uma.ecplusproject.ecplusandroidapp.services.extra.language";
    private static final String EXTRA_RESOLUTION = "es.uma.ecplusproject.ecplusandroidapp.services.extra.resolution";

    private static final String HOST = "192.168.57.1:8080";
    //public static final String HOST = "ecplusproject.uma.es";
    private static final String PROTOCOL = "http://";
    private static final String CONTEXT_PATH = "/academicPortal";
    private static final String REST_API_BASE = "/ecplus/api/v1";
    private static final String WORDS_RESOURCE = "/words";
    private static final String SYNDROMES_RESOURCE = "/sindromes";
    public static final Comparator<PalabraRes> PALABRA_COMPARATOR = new Comparator<PalabraRes>() {
        @Override
        public int compare(PalabraRes lhs, PalabraRes rhs) {
            if (lhs.getId() < rhs.getId()) {
                return -1;
            } else if (lhs.getId() > rhs.getId()) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    public UpdateService() {
        super("UpdateService");
    }

    public static void startUpdateWords(Context context, String language, String resolution) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(UPDATE_WORDS);
        intent.putExtra(EXTRA_LANGUAGE, language);
        intent.putExtra(EXTRA_RESOLUTION, resolution);
        context.startService(intent);
    }


    public static void startUpdateSyndromes(Context context, String language) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(UPDATE_SYNDROMES);
        intent.putExtra(EXTRA_LANGUAGE, language);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (UPDATE_WORDS.equals(action)) {
                final String language = intent.getStringExtra(EXTRA_LANGUAGE);
                final String resolution = intent.getStringExtra(EXTRA_RESOLUTION);
                handleUpdateWords(language, resolution);
            } else if (UPDATE_SYNDROMES.equals(action)) {
                final String language = intent.getStringExtra(EXTRA_LANGUAGE);
                handleUpdateSyndromes(language);
            }
        }
    }

    private void handleUpdateWords(String language, String resolution) {
        // TODO
        String url = PROTOCOL + HOST + CONTEXT_PATH + REST_API_BASE + WORDS_RESOURCE + "/" + language;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        PalabraRes[] palabras = restTemplate.getForObject(url, PalabraRes [].class);
        Arrays.sort(palabras, PALABRA_COMPARATOR);


        for (PalabraRes palabra: palabras) {
            // TODO
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleUpdateSyndromes(String language) {
        String hash = getHashFromDBForListOfSyndromes(language);
        if (hash == null) {
            return;
        }





        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private String getHashFromDBForListOfSyndromes(String language) {
        SQLiteDatabase db = ECPlusDB.getDatabase();
        Cursor c = db.query(ECPlusDBContract.ListaSindromes.TABLE_NAME,
                new String[]{ECPlusDBContract.ListaSindromes.HASH},
                ECPlusDBContract.ListaSindromes.IDIOMA+"=?",new String[]{language},null,null,null);
        String hash = null;
        if (c.moveToFirst()) {
            hash = c.getString(c.getColumnIndex(ECPlusDBContract.ListaSindromes.HASH));
        }
        c.close();
        db.close();

        return hash;
    }
}
