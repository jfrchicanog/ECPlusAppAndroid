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

    public enum PictureSource {CAMERA, GALLERY, RESTORE};

    public interface OnSourceSelectionListener {
        void onSourceSelection(Palabra palabra, PictureSource source);
    }

    private Palabra palabra;
    private OnSourceSelectionListener listener;

    public void setPalabra(Palabra palabra) {
        this.palabra=palabra;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.changePictureTitle);
        //builder.setMessage(String.format(getString(R.string.changePictureMessage),nombrePalabra));

        String [] options;
        if (palabra.getIconoPersonalizado()!=null) {
            options = new String[]{getString(R.string.takeFromCamera), getString(R.string.restoreOriginalIcon)};
        } else {
            options = new String[]{getString(R.string.takeFromCamera)};
        }

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PictureSource source = null;
                switch (which){
                    case 0: source=PictureSource.CAMERA;
                        break;
                    case 1: source=PictureSource.RESTORE;
                        break;
                }
                if (listener!=null) {
                    listener.onSourceSelection(palabra,source);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    public void setOnSourceSelectionListener(OnSourceSelectionListener listener) {
        this.listener=listener;
    }
}
