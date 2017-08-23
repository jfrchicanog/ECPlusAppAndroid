package es.uma.ecplusproject.ecplusandroidapp.services;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.R;
import es.uma.ecplusproject.ecplusandroidapp.fragments.AdaptadorPalabras;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;

public class ResourcesStore {
    private static final String FILES_PATH="resources";
    private File filesDirectory;
    private Context contexto;

    private SVG applicationLog;
    private Drawable drawable;

    public interface BitmapLoadListener {
        void finishedBitmapLoad(Bitmap bitmap);
    }

    public interface ImageViewContainer {
        void setImageBitmap(CargaBitmapEscalado loader, Bitmap bm);
        void setImageDrawable(CargaBitmapEscalado loader, Drawable dr);
    }

    public static class ImageViewContainerAdapter  implements ImageViewContainer{
        private ImageView imageView;

        public ImageViewContainerAdapter(ImageView im) {
            imageView=im;
        }

        @Override
        public void setImageBitmap(CargaBitmapEscalado loader, Bitmap bm) {
            imageView.setImageBitmap(bm);
        }

        @Override
        public void setImageDrawable(CargaBitmapEscalado loader, Drawable dr) {
            imageView.setImageDrawable(dr);
        }


    }

    public class CargaBitmapEscalado extends AsyncTask<Integer, Void, Bitmap> {
        private ImageViewContainer imagen;
        private String hash;
        private BitmapLoadListener listener;

        public CargaBitmapEscalado(ImageViewContainer imagen, String hash, BitmapLoadListener listener) {
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
                imagen.setImageBitmap(this, bm);
                fireBitmapLoadFinished(bm);
            } else {
                imagen.setImageDrawable(this, getDefaultDrawable());
                fireBitmapLoadFinished(null);
            }
        }

        private void fireBitmapLoadFinished(Bitmap success) {
            if (listener != null) {
                listener.finishedBitmapLoad(success);
            }
        }
    }

    public Drawable getDefaultDrawable() {
        if (drawable == null) {
            drawable = contexto.getResources().getDrawable(R.drawable.logo);
        }
        return drawable;
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

    public File [] getAllFileResourcesInStore() {
        return filesDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory() && pathname.canRead() && pathname.exists() && pathname.isFile();
            }
        });
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
        SVG svg = hash!=null?getSVGFromFile(hash):null;
        if (svg != null) {
            icono.setSVG(svg);
            return svg;
        } else {
            icono.setSVG(getApplicationLogoSVG());
            return getApplicationLogoSVG();
        }
    }

    public SVG getApplicationLogoSVG() {
        if (applicationLog == null) {
            try {
                InputStream is = contexto.getResources().openRawResource(R.raw.logo_proyecto);
                SVG svg = SVG.getFromInputStream(is);
                svg.renderToPicture();
                is.close();
                SVG.Box box = svg.getDocumentBoundingBox();
                svg.setDocumentViewBox(box.minX, box.minY, box.width, box.height);
                applicationLog = svg;

            } catch (IOException | SVGParseException e) {
                e.printStackTrace();
            }
        }
        return applicationLog;
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
        if (hash != null) {
            final CargaBitmapEscalado cargador = new CargaBitmapEscalado(new ImageViewContainerAdapter(imagen), hash, listener);
            tryToUseBitmap(imagen, cargador);
        } else {
            // TODO
        }
    }

    public void tryToUseBitmap(final ImageView imagen, final CargaBitmapEscalado cargador) {

        Log.d("RS", "width: " + imagen.getWidth() + ", height: " + imagen.getHeight());
        if (imagen.getWidth() == 0 || imagen.getHeight() == 0) {
            imagen.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    cargador.execute(v.getWidth());
                    v.removeOnLayoutChangeListener(this);
                }
            });
        } else {
            cargador.execute(imagen.getWidth());
        }

    }


    private Bitmap getBitmapFromFile(String hash, int width) {
        Bitmap bitmap=null;
        try {
        File fichero = getFileResource(hash);
            ExifInterface exif = new ExifInterface(fichero.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

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
                Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, width, (int) height, false);
                if (newBitmap!=bitmap) {
                    bitmap.recycle();
                }
                bitmap=newBitmap;

                newBitmap = rotateBitmap(bitmap, orientation);
                if (newBitmap != bitmap) {
                    bitmap.recycle();
                }
                bitmap=newBitmap;
            }

            return bitmap;

        } catch (IOException e) {
            Log.d("ResourceStore", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

}