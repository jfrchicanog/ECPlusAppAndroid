package es.uma.ecplusproject.ecplusandroidapp.services;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.fragments.AdaptadorPalabras;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;

public class ResourcesStore {
    private static final String FILES_PATH="resources";
    private File filesDirectory;
    private Context contexto;

    public interface BitmapLoadListener {
        void finishedBitmapLoad(Bitmap bitmap);
    }

    public class CargaBitmapEscalado extends AsyncTask<Integer, Void, Bitmap> {
        private ImageView imagen;
        private String hash;
        private BitmapLoadListener listener;

        public CargaBitmapEscalado(ImageView imagen, String hash, BitmapLoadListener listener) {
            this.imagen = imagen;
            this.hash=hash;
            this.listener=listener;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            return getBitmapFromFile(hash, params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            if (bm != null) {
                imagen.setImageBitmap(bm);
                fireBitmapLoadFinished(bm);
            } else {
                imagen.setImageDrawable(contexto.getResources().getDrawable(R.drawable.logo));
                fireBitmapLoadFinished(null);
            }
        }

        private void fireBitmapLoadFinished(Bitmap success) {
            if (listener != null) {
                listener.finishedBitmapLoad(success);
            }
        }
    }

    public ResourcesStore(Context context) {
        this.contexto = context;
        filesDirectory = new File(context.getFilesDir(), FILES_PATH);
        if (!filesDirectory.exists()) {
            filesDirectory.mkdir();
        }
    }

    @NonNull
    public File getFileResource(String hash) {
        return new File(filesDirectory,hash.toLowerCase());
    }

    @NonNull
    public SVG getSVGFromFile(String hash) {
        SVG svg=null;
        try {
            InputStream is = new FileInputStream(getFileResource(hash));
            svg = SVG.getFromInputStream(is);
            svg.renderToPicture();
            is.close();
            SVG.Box box = svg.getDocumentBoundingBox();
            svg.setDocumentViewBox(box.minX, box.minY, box.width, box.height);

        } catch (IOException |SVGParseException e) {
        }
        return svg;
    }

    public SVG tryToUseSVG(SVGImageView icono, String hash) {
        SVG svg = getSVGFromFile(hash);
        if (svg != null) {
            icono.setSVG(svg);
            return svg;
        } else {
            icono.setImageDrawable(contexto.getResources().getDrawable(R.drawable.logo));
            return null;
        }
    }

    public Bitmap getBitmapFromFile(String hash) {
        Bitmap bm=null;
        try {
            InputStream is = new FileInputStream(getFileResource(hash));
            bm = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bm;
    }

    public void tryToUseBitmap(final ImageView imagen, final String hash, final BitmapLoadListener listener) {
        Log.d("RS", "width: "+imagen.getWidth()+", height: "+imagen.getHeight());
        if (imagen.getWidth()==0 || imagen.getHeight()==0) {
            imagen.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    new CargaBitmapEscalado(imagen, hash, listener).execute(v.getWidth());
                    v.removeOnLayoutChangeListener(this);
                }
            });
        } else {
            new CargaBitmapEscalado(imagen, hash, listener).execute(imagen.getWidth());
        }
    }

    private Bitmap getBitmapFromFile(String hash, int width) {
        Bitmap bitmap=null;

        File fichero = getFileResource(hash);
        // Averiguamos las dimensiones de la imagen sin cargarla
        BitmapFactory.Options opciones = new BitmapFactory.Options();
        opciones.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(fichero.getAbsolutePath(), opciones);

        int originalWidth= opciones.outWidth;
        int originalHeight= opciones.outHeight;

        // Calculamos por cuanto tenemos que dividir las dimensiones de la imagen

        if (originalWidth < width) {
            return BitmapFactory.decodeFile(fichero.getAbsolutePath());
        }

        double ratio = (double)originalWidth / width;
        double height = originalHeight/ratio;

        // Ajustamos el tamño de la muestra para cargar una versión reducida
        opciones.inSampleSize = 1 << ((int)Math.floor(Math.log(ratio)/Math.log(2.0)));
        opciones.inJustDecodeBounds = false;

        // Cargamos la imagen (si es capaz de decodificarla)
        bitmap = BitmapFactory.decodeFile(fichero.getAbsolutePath(), opciones);
        if (bitmap != null) {
            bitmap = Bitmap.createScaledBitmap(bitmap, width, (int) height, false);
        }

        return bitmap;
    }

}