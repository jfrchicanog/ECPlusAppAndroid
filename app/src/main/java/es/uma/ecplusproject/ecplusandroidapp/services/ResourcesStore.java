package es.uma.ecplusproject.ecplusandroidapp.services;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;

import java.io.File;

public class ResourcesStore {
    private static final String FILES_PATH="resources";
    private File filesDirectory;

    public ResourcesStore(Context context) {
        filesDirectory = new File(context.getFilesDir(), FILES_PATH);
        if (!filesDirectory.exists()) {
            filesDirectory.mkdir();
        }
    }

    @NonNull
    public File getFileResource(String hash) {
        return new File(filesDirectory,hash.toLowerCase());
    }
}