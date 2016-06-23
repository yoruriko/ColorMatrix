package com.gospelware.testcolormatrix;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

/**
 * Created by ricogao on 26/05/2016.
 */
public abstract class CanvasBaseFragment extends Fragment {


    public interface CanvasChangeListener {
        void onChange(Bitmap bitmap);
    }

    private Bitmap thumbNail;
    private WeakReference<CanvasChangeListener> listener;

    public void setListener(CanvasChangeListener listener) {
        this.listener = new WeakReference<CanvasChangeListener>(listener);
    }

    public void setThumbNail(Bitmap bitmap) {
        this.thumbNail = bitmap;
        onSourceSet();
    }


    protected CanvasChangeListener getListener() {
        return listener.get();
    }


    public Bitmap getThumbNail() {
        return thumbNail;
    }

    protected abstract void onSourceSet();


}
