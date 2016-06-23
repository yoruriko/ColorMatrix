package com.gospelware.testcolormatrix;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.jakewharton.rxbinding.widget.RxSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ricogao on 27/05/2016.
 */
public class TuneFragment extends CanvasBaseFragment {

    @BindView(R.id.seekBar)
    SeekBar seekBar;

    @BindView(R.id.btnHue)
    Button btnHue;
    @BindView(R.id.btnLuminance)
    Button btnLum;
    @BindView(R.id.btnSaturation)
    Button btnSat;

    @OnClick(R.id.btnHue)
    void onHueClick() {
        changeButton(btnHue);
        seekBar.setProgress(hueProgress);
    }

    @OnClick(R.id.btnSaturation)
    void onSatClick() {
        changeButton(btnSat);
        seekBar.setProgress(satProgress);
    }

    @OnClick(R.id.btnLuminance)
    void onLumClick() {
        changeButton(btnLum);
        seekBar.setProgress(lumProgress);
    }

    private float mHue, mSat, mLum;
    private int hueProgress, satProgress, lumProgress;
    private Button currentBtn;

    private final static int MAX_VALUE = 255;
    private final static int MID_VALUE = 127;

    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(container.getContext()).inflate(R.layout.tune_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onDestroy() {
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        initSubscription();
        super.onResume();
    }

    @Override
    public void onPause() {
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onPause();
    }

    private void init() {
        seekBar.setMax(MAX_VALUE);
        seekBar.setProgress(MID_VALUE);
        mHue = 0f;
        hueProgress = MID_VALUE;
        mSat = 1f;
        satProgress = MID_VALUE;
        mLum = 1f;
        lumProgress = MID_VALUE;

        changeButton(btnHue);
    }

    private void changeButton(Button button) {
        btnLum.setSelected(false);
        btnSat.setSelected(false);
        btnHue.setSelected(false);

        currentBtn = button;
        currentBtn.setSelected(true);
    }

    private void calculateProgress(int progress) {
        switch (currentBtn.getId()) {
            case R.id.btnHue:
                mHue = calculateHue(progress);
                hueProgress = progress;
                break;
            case R.id.btnLuminance:
                mLum = calculateLuminance(progress);
                lumProgress = progress;
                break;
            case R.id.btnSaturation:
                mSat = calculateSaturation(progress);
                satProgress = progress;
                break;
        }
    }

    private float calculateHue(int progress) {
        float hue = (progress - MID_VALUE) * 1f / MID_VALUE * 180;
        return hue;
    }

    private float calculateSaturation(int progress) {
        float sat = (progress * 1f) / MID_VALUE;
        return sat;
    }

    private float calculateLuminance(int progress) {
        float lum = (progress * 1f) / MID_VALUE;
        return lum;
    }

    @Override
    protected void onSourceSet() {

    }

    private void initSubscription() {
        subscription = RxSeekBar.userChanges(seekBar)
                .onBackpressureDrop()
                .observeOn(Schedulers.newThread())
                .map(new Func1<Integer, Bitmap>() {
                    @Override
                    public Bitmap call(Integer progress) {
                        calculateProgress(progress);
                        return ImageHelper.generateBitmap(getThumbNail(), mHue, mSat, mLum);
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
