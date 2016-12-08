package es.uma.ecplusproject.ecplusandroidapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.caverock.androidsvg.SVGImageView;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Fotografia;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Pictograma;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Video;
import es.uma.ecplusproject.ecplusandroidapp.services.ResourcesStore;

import static es.uma.ecplusproject.ecplusandroidapp.R.drawable.video;

/**
 * Created by francis on 13/5/16.
 */
public class AdaptadorRecursos extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TIPO_VIDEO = 0;
    public static final int TIPO_PICTOGRAMA = 1;
    public static final int TIPO_FOTO = 2;
    public static final int TIPO_AUDIO = 3;

    private class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoView video;
        public ImageView thumbnail;
        public TextView texto;
        public VideoViewHolder(View video) {
            super(video);
            this.video=(VideoView)video.findViewById(R.id.video);
            this.thumbnail=(ImageView)video.findViewById(R.id.thumbnail);
            this.texto=(TextView)video.findViewById(R.id.texto);
        }
    }

    private class PictogramaViewHolder extends RecyclerView.ViewHolder {
        public SVGImageView pictograma;
        public TextView texto;
        public PictogramaViewHolder(View pictograma) {
            super(pictograma);
            this.pictograma=(SVGImageView)pictograma.findViewById(R.id.pictograma);
            this.texto = (TextView)pictograma.findViewById(R.id.texto);
        }
    }

    private class FotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView foto;
        public TextView texto;
        public FotoViewHolder(View foto) {
            super(foto);
            this.foto = (ImageView)foto.findViewById(R.id.foto);
            this.texto = (TextView)foto.findViewById(R.id.texto);
        }
    }

    private ResourcesStore resourcesStore;
    private Resolucion resolucion = Resolucion.BAJA;
    private List<RecursoAV> recursos;

    public AdaptadorRecursos(Context ctx) {
        resourcesStore = new ResourcesStore(ctx);
        recursos = new ArrayList<>();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        //resourcesStore = new ResourcesStore(recyclerView.getContext());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TIPO_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detallevideo, parent, false);
                final VideoViewHolder videoVH = new VideoViewHolder(view);
                videoVH.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        MediaController mediaController = new MediaController(parent.getContext());
                        mediaController.setAnchorView(videoVH.video);
                        videoVH.video.setMediaController(mediaController);
                        videoVH.thumbnail.setVisibility(View.GONE);
                    }
                });
                return videoVH;
            case TIPO_PICTOGRAMA:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detallepictograma, parent, false);
                return new PictogramaViewHolder(view);
            case TIPO_FOTO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detallefoto, parent, false);
                return new FotoViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        RecursoAV recurso = recursos.get(position);
        if (holder instanceof PictogramaViewHolder) {
            Log.d("Adpater", "Mostrando "+recurso.getFicheros().get(resolucion));
            if (resourcesStore.tryToUseSVG(((PictogramaViewHolder) holder).pictograma, recurso.getFicheros().get(resolucion))){
                ((PictogramaViewHolder) holder).texto.setVisibility(View.GONE);
            } else {
                ((PictogramaViewHolder) holder).texto.setVisibility(View.VISIBLE);
            }
        } else if (holder instanceof FotoViewHolder) {
            resourcesStore.tryToUseBitmap(((FotoViewHolder) holder).foto, recurso.getFicheros().get(resolucion),
                    new ResourcesStore.BitmapLoadListener() {
                        @Override
                        public void finishedBitmapLoad(boolean success) {
                            if (success) {
                                ((FotoViewHolder) holder).texto.setVisibility(View.GONE);
                            } else {
                                ((FotoViewHolder) holder).texto.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else if (holder instanceof VideoViewHolder) {
            File file = resourcesStore.getFileResource(recurso.getFicheros().get(resolucion));
            if (file.exists()) {
                Bitmap bm = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                ((VideoViewHolder) holder).thumbnail.setImageBitmap(bm);
                ((VideoViewHolder) holder).thumbnail.setVisibility(View.VISIBLE);
                ((VideoViewHolder) holder).video.setVideoPath(file.getPath());
                ((VideoViewHolder) holder).texto.setVisibility(View.GONE);
            } else {
                ((VideoViewHolder) holder).texto.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        RecursoAV recurso = recursos.get(position);
        if (recurso instanceof Pictograma) {
            return TIPO_PICTOGRAMA;
        } else if (recurso instanceof Fotografia) {
            return TIPO_FOTO;
        } else if (recurso instanceof Video) {
            return TIPO_VIDEO;
        }

        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return recursos.size();
    }

    public void setRecursos(List<RecursoAV> recursos) {
        this.recursos = recursos;
        notifyDataSetChanged();
    }
}
