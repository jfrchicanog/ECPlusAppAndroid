package es.uma.ecplusproject.ecplusandroidapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.modelo.PalabrasDAO;
import es.uma.ecplusproject.ecplusandroidapp.modelo.PalabrasDAOImpl;
import es.uma.ecplusproject.ecplusandroidapp.modelo.SindromesDAO;
import es.uma.ecplusproject.ecplusandroidapp.modelo.SindromesDAOImpl;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Sindrome;
import es.uma.ecplusproject.ecplusandroidapp.restws.PalabrasWS;
import es.uma.ecplusproject.ecplusandroidapp.restws.PalabrasWSImpl;
import es.uma.ecplusproject.ecplusandroidapp.restws.SindromesWS;
import es.uma.ecplusproject.ecplusandroidapp.restws.SindromesWSImpl;


public class UpdateService extends IntentService {
    private static final String UPDATE_WORDS = "es.uma.ecplusproject.ecplusandroidapp.services.action.update.words";
    private static final String UPDATE_SYNDROMES = "es.uma.ecplusproject.ecplusandroidapp.services.action.update.syndromes";

    private static final String EXTRA_LANGUAGE = "es.uma.ecplusproject.ecplusandroidapp.services.extra.language";
    private static final String EXTRA_RESOLUTION = "es.uma.ecplusproject.ecplusandroidapp.services.extra.resolution";

    private static final Comparator<Palabra> PALABRA_COMPARATOR = new Comparator<Palabra>() {
        @Override
        public int compare(Palabra lhs, Palabra rhs) {
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
    private static final String TAG = "UpdateService";

    public class UpdateServiceBinder extends Binder {
        public UpdateService getService() {
            return UpdateService.this;
        }
    }

    private SindromesDAO daoSindromes;
    private PalabrasDAO daoPalabras;

    private SindromesWS wsSindromes;
    private PalabrasWS wsPalabras;

    private ResourcesStore resourcesStore;

    private UpdateServiceBinder binder = new UpdateServiceBinder();
    private boolean updating;
    private List<UpdateListener> listeners;

    public UpdateService() {
        super("UpdateService");
        listeners = new ArrayList<>();
    }

    private ResourcesStore getResourcesStore() {
        if (resourcesStore ==null) {
            resourcesStore = new ResourcesStore(this);
        }
        return resourcesStore;
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
                handleUpdateWords(language, Resolucion.valueOf(resolution));
            } else if (UPDATE_SYNDROMES.equals(action)) {
                final String language = intent.getStringExtra(EXTRA_LANGUAGE);
                handleUpdateSyndromes(language);
            }

        }
    }

    private void handleUpdateWords(String language, Resolucion resolution) {
        try {
            updating = true;
            fireEvent(UpdateListenerEvent.startUpdateWordsEvent());

            String localHash = getDAOPalabras().getHashForListOfWords(language, resolution);
            String remoteHash = getWSPalabras().getHashForListOfWords(language, resolution);

            boolean databaseChanged = false;
            if (remoteHash == null) {
                getDAOPalabras().removeAllResourcesForWordsList(language, resolution);
                databaseChanged = true;
            } else if (localHash == null || !localHash.equals(remoteHash)) {
                if (localHash == null) {
                    getDAOPalabras().createListOfWords(language);
                }
                updateLocalWordList(language, resolution, remoteHash);
                databaseChanged = true;
            }

            fireEvent(UpdateListenerEvent.stopUpdateWordsDatabaseEvent(databaseChanged));

            updateFiles(language, resolution);
            updating = false;
            fireEvent(UpdateListenerEvent.stopUpdateWordsFilesEvent(false));
        } catch (RuntimeException e) {
            Log.d(TAG, e.getMessage());
            updating=false;
            fireEvent(UpdateListenerEvent.stopUpdateWordsError());
        }
    }

    private void updateFiles(String language, Resolucion resolution) {
        for (Palabra palabra: getDAOPalabras().getWords(language, resolution)) {
            for (RecursoAV recurso: palabra.getRecursos()) {
                String hash = recurso.getFicheros().get(resolution);
                if (hash != null && !existsResource(hash)) {
                    downloadResource(hash);
                }
            }
        }
    }

    private boolean existsResource(String file) {
        return getResourcesStore().getFileResource(file).exists();
    }

    private void downloadResource(String hash) {
        try {
            Log.d(TAG, "Downloading "+hash);
            InputStream is = getWSPalabras().getResource(hash);
            OutputStream os = new FileOutputStream(getResourcesStore().getFileResource(hash));

            copyStream(is, os);

            is.close();
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copyStream(InputStream is, OutputStream os) throws IOException {
        final int buffer_size = 1024;

        byte[] bytes = new byte[buffer_size];
        for (; ; ) {
            int count = is.read(bytes, 0, buffer_size);
            if (count == -1)
                break;
            os.write(bytes, 0, count);
        }
    }

    private void handleUpdateSyndromes(String language) {
        try {
            updating = true;
            fireEvent(UpdateListenerEvent.startUpdateSyndromesEvent());

            String localHash = getDAOSindromes().getHashForListOfSyndromes(language);
            String remoteHash = getWSSindromes().getHashForListOfSindromes(language);

            boolean databaseChanged = false;
            if (remoteHash == null) {
                getDAOSindromes().removeSyndromeList(language);
                databaseChanged = true;
            } else if (localHash == null || !localHash.equalsIgnoreCase(remoteHash)) {
                if (localHash == null) {
                    getDAOSindromes().createListOfSyndromes(language);
                }
                updateLocalSyndromeList(language, remoteHash);
                databaseChanged = true;
            }
            updating = false;
            fireEvent(UpdateListenerEvent.stopUpdateSyndromesEvent(databaseChanged));
        } catch (RuntimeException e) {
            Log.d(TAG, e.getMessage());
            fireEvent(UpdateListenerEvent.stopUpdateSyndromesError());
        }
    }

    private void updateLocalWordList(String language, Resolucion resolution, String remoteHash) {
        List<Palabra> remoteWords = getWSPalabras().getWords(language, resolution);
        List<Palabra> localWords = getDAOPalabras().getWords(language, resolution);
        Collections.sort(remoteWords, PALABRA_COMPARATOR);
        Collections.sort(localWords, PALABRA_COMPARATOR);

        Iterator<Palabra> localIterator = localWords.iterator();
        Iterator<Palabra> remoteIterator = remoteWords.iterator();

        Palabra local = getNextElementOfIterator(localIterator);
        Palabra remote = getNextElementOfIterator(remoteIterator);

        while (local!=null || remote != null) {
            if (local==null) {
                Log.d(TAG, "Adding "+remote.getId()+" "+remote.getNombre());
                getDAOPalabras().addWord(remote, language, resolution);
                remote = getNextElementOfIterator(remoteIterator);
            } else if (remote == null) {
                Log.d(TAG, "Removing "+local.getId()+" "+local.getNombre());
                getDAOPalabras().removeWord(local);
                local = getNextElementOfIterator(localIterator);
            } else if (local.getId() > remote.getId()) {
                Log.d(TAG, "Adding "+remote.getId()+" "+remote.getNombre());
                getDAOPalabras().addWord(remote, language, resolution);
                remote = getNextElementOfIterator(remoteIterator);
            } else if (local.getId() < remote.getId()) {
                Log.d(TAG, "Removing "+local.getId()+" "+local.getNombre());
                getDAOPalabras().removeWord(local);
                local = getNextElementOfIterator(localIterator);
            } else {
                //Log.d(TAG, "Should I update? "+local.getId()+" "+local.getNombre());
                if (!local.getHash(resolution).equalsIgnoreCase(remote.getHash(resolution))) {
                    Log.d(TAG, "Updating "+local.getId()+" "+local.getNombre());
                    getDAOPalabras().updateWord(remote, language);
                }
                local = getNextElementOfIterator(localIterator);
                remote = getNextElementOfIterator(remoteIterator);
            }
        }

        getDAOPalabras().setHashForListOfWords(language, resolution, remoteHash);
    }

    private static <E> E getNextElementOfIterator(Iterator<E> iterator) {
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
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
                if (!local.getHash().equalsIgnoreCase(remote.getHash())) {
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

    private PalabrasWS getWSPalabras() {
        if (wsPalabras == null) {
            wsPalabras = new PalabrasWSImpl();
        }
        return wsPalabras;
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
