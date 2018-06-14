package es.uma.ecplusproject.ecplusandroidapp;

import android.app.Application;
import android.content.Context;

public class ECPlusApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
    }

    public static Context getContext() {
        return context;
    }
}
