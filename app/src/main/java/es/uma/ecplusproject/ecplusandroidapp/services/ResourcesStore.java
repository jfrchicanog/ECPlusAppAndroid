package es.uma.ecplusproject.ecplusandroidapp.services;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
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

    public void tryToUseSVG(SVGImageView icono, String hash) {
        SVG svg = getSVGFromFile(hash);
        if (svg != null) {
            icono.setSVG(svg);
        } else {
            icono.setImageDrawable(contexto.getResources().getDrawable(R.drawable.logo));
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

    public void tryToUseBitmap(ImageView imagen, String hash) {
        Bitmap bm = getBitmapFromFile(hash);
        if (bm != null) {
            imagen.setImageBitmap(bm);
        } else {
            imagen.setImageDrawable(contexto.getResources().getDrawable(R.drawable.logo));
        }
    }

}