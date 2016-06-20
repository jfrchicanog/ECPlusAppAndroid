package es.uma.ecplusproject.ecplusandroidapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Locale;
import java.util.Set;

import es.uma.ecplusproject.ecplusandroidapp.MainActivity;
import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.Splash;

/**
 * Created by francis on 20/6/16.
 */
public class ChooseLanguageDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Set<String> idiomas = getActivity()
                .getSharedPreferences(Splash.ECPLUS_MAIN_PREFS, Context.MODE_PRIVATE)
                .getStringSet(Splash.LANGUAGES_KEY_PREFS, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.language_menu);

        if (idiomas != null) {
            final String [] idiomasArray = idiomas.toArray(new String[0]);
            final String [] displayName = new String [idiomasArray.length];
            for(int i=0; i < idiomasArray.length; i++) {
                displayName[i] = new Locale(idiomasArray[i]).getDisplayLanguage();
            }
            builder.setItems(displayName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainActivity)getActivity()).changeLanguage(idiomasArray[which]);
                }
            });
        } else {
            builder.setMessage(R.string.no_languages);
            builder.setPositiveButton(R.string.language_dialog_ok, null);
        }

        return builder.create();
    }
}
