package es.uma.ecplusproject.ecplusandroidapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Fotografia;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Pictograma;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Video;
import es.uma.ecplusproject.ecplusandroidapp.services.ResourcesStore;

/**
 * Created by francis on 13/5/16.
 */
public class AdaptadorRecursos extends RecyclerView.Adapter<AdaptadorRecursos.RecursosAVViewHolder> {

    private static final String TAG = "AdaptadorRecursos";
    public static final int TIPO_VIDEO = 0;
    public static final int TIPO_PICTOGRAMA = 1;
    public static final int TIPO_FOTO = 2;
    public static final int TIPO_AUDIO = 3;

    public interface ItemClickListener {
        void onItemClick(RecursosAVViewHolder viewHolder);
    }

    public abstract class RecursosAVViewHolder extends RecyclerView.ViewHolder {
        private RecursoAV recurso;
        public RecursosAVViewHolder(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("click");
                    if (listener != null) {
                        listener.onItemClick(RecursosAVViewHolder.this);
                    }
                }
            });
        }

        public RecursoAV getRecurso() {
            return this.recurso;
        }

        public void bindRecursoAV(RecursoAV recursoAV) {
            this.recurso = recursoAV;
        }
    }

    public class VideoViewHolder extends RecursosAVViewHolder {
        public VideoView video;
        public ImageView thumbnail;
        public TextView texto;
        public VideoViewHolder(View video) {
            super(video);
            this.video=(VideoView)video.findViewById(R.id.video);
            this.thumbnail=(ImageView)video.findViewById(R.id.thumbnail);
            this.texto=(TextView)video.findViewById(R.id.texto);
            prepareMediaController();
        }

        private void prepareMediaController() {
            /*
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    MediaController mediaController = new MediaController(itemView.getContext());
                    mediaController.setAnchorView(video);
                    video.setMediaController(mediaController);
                    thumbnail.setVisibility(View.GONE);
                }
            });*/
        }

        @Override
        public void bindRecursoAV(RecursoAV recurso) {
            super.bindRecursoAV(recurso);
            File file = resourcesStore.getFileResource(recurso.getFicheros().get(resolucion));
            if (file.exists()) {
                Bitmap bm = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                thumbnail.setImageBitmap(bm);
                thumbnail.setVisibility(View.VISIBLE);
                video.setVideoPath(file.getPath());
                texto.setVisibility(View.GONE);
            } else {
                texto.setVisibility(View.VISIBLE);
            }
        }
    }

    public class PictogramaViewHolder extends RecursosAVViewHolder {
        public SVGImageView pictograma;
        public TextView texto;
        public SVG imagenSVG;
        public PictogramaViewHolder(View pictograma) {
            super(pictograma);
            this.pictograma=(SVGImageView)pictograma.findViewById(R.id.pictograma);
            this.texto = (TextView)pictograma.findViewById(R.id.texto);
        }

        @Override
        public void bindRecursoAV(RecursoAV recurso) {
            super.bindRecursoAV(recurso);
            Log.d("Adpater", "Mostrando "+recurso.getFicheros().get(resolucion));
            imagenSVG = resourcesStore.tryToUseSVG(pictograma, recurso.getFicheros().get(resolucion));
            if (imagenSVG != null){
                texto.setVisibility(View.GONE);
            } else {
                texto.setVisibility(View.VISIBLE);
            }
        }
    }

    public class FotoViewHolder extends RecursosAVViewHolder {
        public ImageView foto;
        public TextView texto;
        public Bitmap bitmap;
        public FotoViewHolder(View foto) {
            super(foto);
            this.foto = (ImageView)foto.findViewById(R.id.foto);
            this.texto = (TextView)foto.findViewById(R.id.texto);
        }

        @Override
        public void bindRecursoAV(RecursoAV recurso) {
            super.bindRecursoAV(recurso);
            resourcesStore.tryToUseBitmap(foto, recurso.getFicheros().get(resolucion),
                    new ResourcesStore.BitmapLoadListener() {
                        @Override
                        public void finishedBitmapLoad(Bitmap bitmap) {
                            FotoViewHolder.this.bitmap = bitmap;
                            if (bitmap!=null) {
                                texto.setVisibility(View.GONE);
                            } else {
                                texto.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
    }

    private ResourcesStore resourcesStore;
    private Resolucion resolucion = Resolucion.BAJA;
    private List<RecursoAV> recursos;
    private ItemClickListener listener;

    public AdaptadorRecursos(Context ctx) {
        resourcesStore = new ResourcesStore(ctx);
        recursos = new ArrayList<>();
        listener = null;
    }

    @Override
    public RecursosAVViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TIPO_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detallevideo, parent, false);
                return new VideoViewHolder(view);
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
    public void onBindViewHolder(final RecursosAVViewHolder holder, int position) {
        RecursoAV recurso = recursos.get(position);
        holder.bindRecursoAV(recurso);
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

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

}
