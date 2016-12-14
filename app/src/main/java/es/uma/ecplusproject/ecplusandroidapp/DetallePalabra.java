package es.uma.ecplusproject.ecplusandroidapp;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.caverock.androidsvg.SVGImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Palabra;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.RecursoAV;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Resolucion;
import es.uma.ecplusproject.ecplusandroidapp.modelo.dominio.Video;
import es.uma.ecplusproject.ecplusandroidapp.services.ResourcesStore;
import es.uma.ecplusproject.ecplusandroidapp.views.SquaredCardView;
import es.uma.ecplusproject.ecplusandroidapp.views.ZoomCoordinator;

import static android.view.View.GONE;

/**
 * Created by francis on 20/4/16.
 */
public class DetallePalabra extends AppCompatActivity {

    public static final String PALABRA = "palabra";
    private static final String TAG ="DetallePalabra";

    private class ImageZoomCoordinator extends ZoomCoordinator {
        private static final int DURATION = 300;
        private String hash;
        private AdaptadorRecursos.RecursosAVViewHolder viewHolder;

        public ImageZoomCoordinator(AdaptadorRecursos.RecursosAVViewHolder vh, String hash) {
            this.viewHolder = vh;
            this.hash = hash;
        }

        @Override
        protected Animator prepareZoomIn() {
            Log.d(TAG, "Preparing zoom in ");
            if (viewHolder instanceof AdaptadorRecursos.PictogramaViewHolder) {
                imagen.setSVG(((AdaptadorRecursos.PictogramaViewHolder) viewHolder).imagenSVG);
            } else if (viewHolder instanceof AdaptadorRecursos.FotoViewHolder) {

                Bitmap bitmap = ((AdaptadorRecursos.FotoViewHolder) viewHolder).bitmap;
                if (bitmap!=null) {
                    imagen.setImageBitmap(bitmap);
                } else {
                    imagen.setImageDrawable(resourcesStore.getDefaultDrawable());
                }
            }
            touchableArea.setVisibility(View.VISIBLE);

            return prepareZoomInAnimation();

        }

        @NonNull
        private AnimatorSet prepareZoomInAnimation() {

            cardView.setTranslationX(0);
            cardView.setTranslationY(0);
            cardView.setScaleX(1f);
            cardView.setScaleY(1f);

            final Rect startBounds = new Rect();
            final Rect finalBounds = new Rect();
            final Point globalOffset = new Point();

            // The start bounds are the global visible rectangle of the thumbnail,
            // and the final bounds are the global visible rectangle of the container
            // view. Also set the container view's offset as the origin for the
            // bounds, since that's the origin for the positioning animation
            // properties (X, Y).
            viewHolder.itemView.getGlobalVisibleRect(startBounds);
            Log.d(TAG, "Start bounds:"+startBounds);
            cardView.getGlobalVisibleRect(finalBounds, globalOffset);
            Log.d(TAG, "Final bounds: "+finalBounds);
            Log.d(TAG, "Global offset: "+globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);


            float startScale = (float) startBounds.width() / finalBounds.width();
            cardView.setPivotX(0f);
            cardView.setPivotY(0f);

            ValueAnimator alphaIncrease = ValueAnimator.ofInt(0, 100);
            alphaIncrease.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    touchableArea.setBackgroundColor(Color.argb((Integer)animation.getAnimatedValue(), 0, 0, 0));
                }
            });
            AnimatorSet set = new AnimatorSet();
            set
                    .play(ObjectAnimator.ofFloat(cardView, View.TRANSLATION_X,
                            startBounds.left, finalBounds.left))
                    .with(ObjectAnimator.ofFloat(cardView, View.TRANSLATION_Y,
                            startBounds.top, finalBounds.top))
                    .with(ObjectAnimator.ofFloat(cardView, View.SCALE_X, startScale, 1f))
                    .with(ObjectAnimator.ofFloat(cardView, View.SCALE_Y, startScale, 1f))
                    .with(alphaIncrease);

            set.setDuration(DURATION).setInterpolator(new DecelerateInterpolator());
            return set;
        }

        @NonNull
        private AnimatorSet prepareZoomOutAnimation() {

            cardView.setTranslationX(0);
            cardView.setTranslationY(0);
            cardView.setScaleX(1f);
            cardView.setScaleY(1f);

            final Rect startBounds = new Rect();
            final Rect finalBounds = new Rect();
            final Point globalOffset = new Point();

            // The start bounds are the global visible rectangle of the thumbnail,
            // and the final bounds are the global visible rectangle of the container
            // view. Also set the container view's offset as the origin for the
            // bounds, since that's the origin for the positioning animation
            // properties (X, Y).
            viewHolder.itemView.getGlobalVisibleRect(startBounds);
            Log.d(TAG, "Start bounds:"+startBounds);
            cardView.getGlobalVisibleRect(finalBounds, globalOffset);
            Log.d(TAG, "Final bounds: "+finalBounds);
            Log.d(TAG, "Global offset: "+globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);


            float startScale = (float) startBounds.width() / finalBounds.width();
            cardView.setPivotX(0f);
            cardView.setPivotY(0f);

            ValueAnimator alphaDecrease = ValueAnimator.ofInt(100, 0);
            alphaDecrease.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    touchableArea.setBackgroundColor(Color.argb((Integer)animation.getAnimatedValue(), 0, 0, 0));
                }
            });
            AnimatorSet set = new AnimatorSet();
            set
                    .play(ObjectAnimator.ofFloat(cardView, View.TRANSLATION_X,
                            finalBounds.left, startBounds.left))
                    .with(ObjectAnimator.ofFloat(cardView, View.TRANSLATION_Y,
                            finalBounds.top, startBounds.top))
                    .with(ObjectAnimator.ofFloat(cardView, View.SCALE_X, 1f, startScale))
                    .with(ObjectAnimator.ofFloat(cardView, View.SCALE_Y, 1f, startScale))
                    .with(alphaDecrease);
            set.setDuration(DURATION).setInterpolator(new DecelerateInterpolator());
            return set;
        }

        @Override
        protected void zoomedIn() {
            Log.d(TAG, "Zoomed in");
            if (viewHolder instanceof AdaptadorRecursos.FotoViewHolder) {
                resourcesStore.tryToUseBitmap(imagen, hash, null);
            }
        }

        @Override
        protected Animator prepareZoomOut() {
            Log.d(TAG, "Preparing zoom out ");
            return prepareZoomOutAnimation();
        }

        @Override
        protected void zoomedOut() {
            Log.d(TAG, "Zoomed out");
            touchableArea.setVisibility(View.INVISIBLE);
        }
    }
    private AdaptadorRecursos adaptador;
    private TextView nombre;
    private SquaredCardView cardView;
    private SVGImageView imagen;
    private View touchableArea;
    private ResourcesStore resourcesStore;
    private ImageZoomCoordinator izc;

    private RecyclerView recursos;
    private Comparator<RecursoAV> comparator = new Comparator<RecursoAV>() {
        @Override
        public int compare(RecursoAV lhs, RecursoAV rhs) {
            boolean lhsVideo = lhs instanceof Video;
            boolean rhsVideo = rhs instanceof Video;
            if (lhsVideo && !rhsVideo) {
                return -1;
            } else if (rhsVideo && !lhsVideo) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detallepalabra);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Palabra palabra = (Palabra) getIntent().getSerializableExtra(PALABRA);
        nombre = (TextView) findViewById(R.id.nombre);
        nombre.setText(palabra.getNombre());

        recursos = (RecyclerView) findViewById(R.id.recursosAV);
        recursos.setHasFixedSize(true);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recursos.setLayoutManager(mLayoutManager);

        adaptador = new AdaptadorRecursos(this);
        List<RecursoAV> listaRecursos = palabra.getRecursos();

        Collections.sort(listaRecursos, comparator);
        adaptador.setRecursos(listaRecursos);

        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adaptador.getItemViewType(position)==AdaptadorRecursos.TIPO_VIDEO?2:1;
            }
        });

        recursos.setAdapter(adaptador);

        adaptador.setItemClickListener(new AdaptadorRecursos.ItemClickListener() {
            @Override
            public void onItemClick(AdaptadorRecursos.RecursosAVViewHolder viewHolder) {
                if (!(viewHolder instanceof AdaptadorRecursos.VideoViewHolder)) {
                    Log.d(TAG, "Click en elemento");
                    izc = new ImageZoomCoordinator(viewHolder, viewHolder.getRecurso().getFicheros().get(Resolucion.BAJA));
                    izc.zoomIn();
                } else {
                    ((AdaptadorRecursos.VideoViewHolder) viewHolder).video.start();
                    ((AdaptadorRecursos.VideoViewHolder) viewHolder).thumbnail.setVisibility(GONE);
                }
            }
        });

        resourcesStore = new ResourcesStore(this);

        touchableArea = findViewById(R.id.touchableArea);
        cardView = (SquaredCardView) findViewById(R.id.zoomedCardView);
        imagen = (SVGImageView) findViewById(R.id.zoomedImg);
        touchableArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                izc.zoomOut();
            }
        });
        touchableArea.setVisibility(View.INVISIBLE);




    }

}
