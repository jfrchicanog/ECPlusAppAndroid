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
import es.uma.ecplusproject.ecplusandroidapp.fragments.ChangePictureListener;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;

/**
 * Created by francis on 20/6/16.
 */
public class ChangePictureDialog extends DialogFragment {

    private String nombrePalabra;
    private boolean customizedIcon;

    @Override
    public void setArguments(Bundle args) {
        nombrePalabra = args.getString(MainActivity.NOMBRE_PALABRA);
        customizedIcon = args.getBoolean(MainActivity.CUSTOMIZED_ICON);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.changePictureTitle);
        //builder.setMessage(String.format(getString(R.string.changePictureMessage),nombrePalabra));

        String [] options;
        if (customizedIcon) {
            options = new String[]{getString(R.string.takeFromCamera), getString(R.string.restoreOriginalIcon)};
        } else {
            options = new String[]{getString(R.string.takeFromCamera)};
        }

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO
            }
        });


        builder.setNegativeButton(R.string.cancel, null);

        return builder.create();
    }
}
