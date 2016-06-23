package com.gospelware.testcolormatrix;

import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import adapter.EffectAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.Effect;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import utils.MeasureUtils;

/**
 * Created by ricogao on 26/05/2016.
 */
public class EffectFragment extends CanvasBaseFragment {


    private Subscription subscription;

    private float[] effect1 = {0.393f, 0.739f, 0.189f, 0f, 0f, 0.349f, 0.686f, 0.168f, 0f, 0f, 0.278f, 0.537f, -0.131f, 0f, 0f, 0f, 0f, 0f, 1f, 0f};
    private float[] effect2 = {-0.36f, 1.691f, -0.32f, 0f, 0f, 0.325f, 0.398f, 0.275f, 0f, 0f, 0.79f, 0.796f, -0.76f, 0f, 0f, 0f, 0f, 0f, 1f, 0f};
    private float[] effect3 = {0.5f, 0.5f, 0.5f, 0f, 0f, 0.5f, 0.5f, 0.5f, 0f, 0f, 0.5f, 0.5f, 0.5f, 0f, 0f, 0f, 0f, 0f, 1f, 0f};
    private float[] effect4 = {1.438f, -0.062f, -0.062f, 0f, 0f, -0.122f, 1.378f, -0.122f, 0f, 0f, -0.016f, -0.016f, 1.483f, 0f, 0f, -0.03f, 0.05f, -0.02f, 1f, 0f};

    private List<Bitmap> thumbNails = new ArrayList<Bitmap>();

    private List<Effect> effects;


    public final static String TAG = EffectFragment.class.getSimpleName();

    private EffectAdapter adapter;
    private int imageHeight;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.effect_fragment_layout, container, false);
        ButterKnife.bind(this, view);

        imageHeight = MeasureUtils.dp2px(getContext(), 80);

        effects = new ArrayList<>();
        effects.add(new Effect(effect1, "old photo"));
        effects.add(new Effect(effect2, "light inverse"));
        effects.add(new Effect(effect3, "grey scale"));
        effects.add(new Effect(effect4, "polaris"));

        initAdapter();


        return view;
    }


    @Override
    protected void onSourceSet() {
        createEffectThumbNails();
    }

    private void createEffectThumbNails() {

        subscription = Observable.from(effects)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        thumbNails.clear();
                    }
                })
                .observeOn(Schedulers.newThread())
                .map(new Func1<Effect, ColorMatrix>() {
                    @Override
                    public ColorMatrix call(Effect effect) {
                        return effect.getColorMatrix();
                    }
                })
                .map(new Func1<ColorMatrix, Bitmap>() {
                    @Override
                    public Bitmap call(ColorMatrix matrix) {
                        return ImageHelper.getThumbNailWithEffect(getThumbNail(), imageHeight, matrix);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {
                        setThumbNails(thumbNails);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        thumbNails.add(bitmap);
                    }
                });

    }

    private void initAdapter() {

        adapter = new EffectAdapter(effects, new EffectAdapter.EffectItemClickListener() {
            @Override
            public void onEffectClick(int position) {
                applyEffectOnSource(effects.get(position).getMatrix());
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

    }

    private void setThumbNails(List<Bitmap> bitmaps) {
        for (int i = 0; i < bitmaps.size(); i++) {
            effects.get(i).setThumbNail(bitmaps.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    private void applyEffectOnSource(float[] matrix) {
        if (getListener() != null) {
            subscription = Observable.just(matrix)
                    .observeOn(Schedulers.newThread())
                    .map(new Func1<float[], ColorMatrix>() {
                        @Override
                        public ColorMatrix call(float[] floats) {
                            return new ColorMatrix(floats);
                        }
                    })
                    .map(new Func1<ColorMatrix, Bitmap>() {
                        @Override
                        public Bitmap call(ColorMatrix matrix) {
                            return ImageHelper.applyMatrix(getThumbNail(), matrix);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Bitmap>() {
                        @Override
                        public void call(Bitmap bitmap) {
                            getListener().onChange(bitmap);
                        }
                    });
        }
    }

    @Override
    public void onDestroy() {
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }
}
