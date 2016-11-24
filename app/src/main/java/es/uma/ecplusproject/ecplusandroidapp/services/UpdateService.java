package es.uma.ecplusproject.ecplusandroidapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.modelo.PalabrasDAO;
import es.uma.ecplusproject.ecplusandroidapp.modelo.PalabrasDAOImpl;
import es.uma.ecplusproject.ecplusandroidapp.modelo.SindromesDAO;
import es.uma.ecplusproject.ecplusandroidapp.modelo.SindromesDAOImpl;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Sindrome;
import es.uma.ecplusproject.ecplusandroidapp.restws.PalabrasWS;
import es.uma.ecplusproject.ecplusandroidapp.restws.SindromesWS;
import es.uma.ecplusproject.ecplusandroidapp.restws.SindromesWSImpl;
import es.uma.ecplusproject.ecplusandroidapp.restws.domain.PalabraRes;


public class UpdateService extends IntentService {
    private static final String UPDATE_WORDS = "es.uma.ecplusproject.ecplusandroidapp.services.action.update.words";
    private static final String UPDATE_SYNDROMES = "es.uma.ecplusproject.ecplusandroidapp.services.action.update.syndromes";

    private static final String EXTRA_LANGUAGE = "es.uma.ecplusproject.ecplusandroidapp.services.extra.language";
    private static final String EXTRA_RESOLUTION = "es.uma.ecplusproject.ecplusandroidapp.services.extra.resolution";

    //private static final String HOST = "192.168.57.1:8080";
    public static final String HOST = "ecplusproject.uma.es";
    private static final String PROTOCOL = "https://";
    private static final String CONTEXT_PATH = "/academicPortal";
    private static final String REST_API_BASE = "/ecplus/api/v1";
    private static final String WORDS_RESOURCE = "/words";
    private static final Comparator<PalabraRes> PALABRA_COMPARATOR = new Comparator<PalabraRes>() {
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

    private static final Comparator<Sindrome> SYNDROME_COMPARATOR = new Comparator<Sindrome>() {
        @Override
        public int compare(Sindrome lhs, Sindrome rhs) {
            if (lhs.getId() < rhs.getId()) {
                return -1;
            } else if (lhs.getId() > rhs.getId()) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    public class UpdateServiceBinder extends Binder {
        public UpdateService getService() {
            return UpdateService.this;
        }
    }

    private SindromesDAO daoSindromes;
    private PalabrasDAO daoPalabras;

    private SindromesWS wsSindromes;
    private PalabrasWS wsPalabras;

    private UpdateServiceBinder binder = new UpdateServiceBinder();
    private boolean updating;
    private List<UpdateListener> listeners;

    public UpdateService() {
        super("UpdateService");
        listeners = new ArrayList<>();
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
            updating=true;
            final String action = intent.getAction();
            if (UPDATE_WORDS.equals(action)) {
                final String language = intent.getStringExtra(EXTRA_LANGUAGE);
                final String resolution = intent.getStringExtra(EXTRA_RESOLUTION);
                handleUpdateWords(language, resolution);
            } else if (UPDATE_SYNDROMES.equals(action)) {
                final String language = intent.getStringExtra(EXTRA_LANGUAGE);
                handleUpdateSyndromes(language);
            }
            updating=false;
        }
    }

    private void handleUpdateWords(String language, String resolution) {
        fireEvent(UpdateListenerEvent.startUpdateWordsEvent());
        String url = PROTOCOL + HOST + CONTEXT_PATH + REST_API_BASE + WORDS_RESOURCE + "/" + language;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        PalabraRes[] palabras = restTemplate.getForObject(url, PalabraRes [].class);
        Arrays.sort(palabras, PALABRA_COMPARATOR);


        for (PalabraRes palabra: palabras) {
            // TODO
        }

        fireEvent(UpdateListenerEvent.stopUpdateWordsDatabaseEvent(false));
        fireEvent(UpdateListenerEvent.stopUpdateWordsFilesEvent(false));
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleUpdateSyndromes(String language) {
        fireEvent(UpdateListenerEvent.startUpdateSyndromesEvent());

        String localHash = getDAOSindromes().getHashForListOfSyndromes(language);
        String remoteHash = getWSSindromes().getHashForListOfSindromes(language);

        boolean databaseChanged=false;
        if (remoteHash==null) {
            getDAOSindromes().removeSyndromeList(language);
            databaseChanged=true;
        } else if (localHash == null || !localHash.equals(remoteHash)) {
            if (localHash == null) {
                getDAOSindromes().createListOfSyndromes(language);
            }
            updateLocalSyndromeList(language, remoteHash);
            databaseChanged=true;
        }
        fireEvent(UpdateListenerEvent.stopUpdateSyndromesEvent(databaseChanged));
    }

    private void updateLocalSyndromeList(String language, String remoteHash) {
        List<Sindrome> remoteSindromes = getWSSindromes().getSindromes(language);
        List<Sindrome> localSindromes = getDAOSindromes().getSindromes(language);
        Collections.sort(remoteSindromes, SYNDROME_COMPARATOR);
        Collections.sort(localSindromes, SYNDROME_COMPARATOR);

        Iterator<Sindrome> iterator = localSindromes.iterator();
        for (Sindrome remote: remoteSindromes) {

            Sindrome local = removeLocalSyndromesUpToNextRemoteSyndrome(iterator, remote);

            if (local == null || local.getId() > remote.getId()) {
                getDAOSindromes().addSyndrome(remote, language);
            } else if (local.getId() == remote.getId()) {
                if (local.getHash() != remote.getHash()) {
                    getDAOSindromes().updateSyndrome(remote);
                }
            }
        }

        getDAOSindromes().setHashForListOfSyndromes(language, remoteHash);

    }

    @Nullable
    private Sindrome removeLocalSyndromesUpToNextRemoteSyndrome(Iterator<Sindrome> iterator, Sindrome remote) {
        Sindrome local;
        do {
            if (iterator.hasNext()) {
                local = iterator.next();
                if (local.getId() < remote.getId()) {
                    getDAOSindromes().removeSyndrome(local);
                }
            } else {
                local=null;
            }
        } while ((local != null) && (local.getId() < remote.getId()));
        return local;
    }

    private SindromesDAO getDAOSindromes() {
        if (daoSindromes==null) {
            daoSindromes = new SindromesDAOImpl(this);
        }
        return daoSindromes;
    }

    private PalabrasDAO getDAOPalabras() {
        if (daoPalabras==null) {
            daoPalabras = new PalabrasDAOImpl(this);
        }
        return daoPalabras;
    }

    private SindromesWS getWSSindromes() {
        if (wsSindromes == null) {
            wsSindromes = new SindromesWSImpl();
        }
        return wsSindromes;
    }

    public boolean isUpdating() {
        return updating;
    }

    public void addUpdateListener(UpdateListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeUpdateListener(UpdateListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private void fireEvent(UpdateListenerEvent event) {
        for (UpdateListener listener: listeners) {
            listener.onUpdateEvent(event);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
