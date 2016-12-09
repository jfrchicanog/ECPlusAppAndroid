package es.uma.ecplusproject.ecplusandroidapp.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;

import static es.uma.ecplusproject.ecplusandroidapp.views.ZoomCoordinator.Status.ZOOMED_IN;
import static es.uma.ecplusproject.ecplusandroidapp.views.ZoomCoordinator.Status.ZOOMED_OUT;
import static es.uma.ecplusproject.ecplusandroidapp.views.ZoomCoordinator.Status.ZOOMING_IN;
import static es.uma.ecplusproject.ecplusandroidapp.views.ZoomCoordinator.Status.ZOOMING_OUT;

/**
 * Created by francis on 8/12/16.
 */

public abstract class ZoomCoordinator {
    public enum Status {ZOOMED_OUT, ZOOMING_IN, ZOOMING_OUT, ZOOMED_IN}

    private Status status;
    private Animator animator;

    public ZoomCoordinator() {
        status = ZOOMED_OUT;
    }

    public Status getStatus() {
        return status;
    }

    public Animator getAnimator() {
        return animator;
    }

    public void zoomIn() {
        if (status == ZOOMED_OUT) {
            status = ZOOMING_IN;
            animator = prepareZoomIn();
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    status = ZOOMED_IN;
                    zoomedIn();
                }
            });
            animator.start();
        }
    }

    public void zoomOut() {
        if (status == ZOOMED_IN) {
            status = ZOOMING_OUT;
            animator = prepareZoomOut();
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    status = ZOOMED_OUT;
                    zoomedOut();
                }
            });
            animator.start();
        }
    }

    protected abstract Animator prepareZoomIn();
    protected abstract void zoomedIn();
    protected abstract Animator prepareZoomOut();
    protected abstract void zoomedOut();


}
