package com.gospelware.testcolormatrix;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ImageView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ricogao on 25/05/2016.
 */
public class CanvasActivity extends FragmentActivity implements CanvasBaseFragment.CanvasChangeListener {

    @BindColor(R.color.red)
    int red;
    @BindColor(R.color.blue)
    int blue;
    @BindColor(R.color.yellow)
    int yellow;
    @BindColor(R.color.pink)
    int pink;

    @BindView(R.id.imageView)
    ImageView imageView;

    @OnClick(R.id.btnClear)
    void onClear() {
        if (thumbNail != null) {
            imageView.setImageBitmap(thumbNail);
            canvasImage = thumbNail;
        }
    }


    private final static String TAG = CanvasActivity.class.getSimpleName();
    private BottomBar bottomBar;

    private Uri targetUri;
    public Bitmap thumbNail, canvasImage;
    private Subscription subscription;

    private CanvasBaseFragment effectFragment, tuneFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canvas_activity_layout);
        ButterKnife.bind(this);
        init(savedInstanceState);
        loadImage();
    }

    @Override
    protected void onDestroy() {
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

    private void changeFragment(CanvasBaseFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void init(Bundle saveInstanceState) {
        effectFragment = new EffectFragment();
        effectFragment.setListener(this);
        initBottomBar(saveInstanceState);
    }

    private void initBottomBar(Bundle savedInstanceState) {
        bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.bottom_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                Log.i(TAG, "Tab Selected: " + menuItemId);
                changeFragmentWithMenuId(menuItemId);
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                Log.i(TAG, "Tab ReSelected:" + menuItemId);
                changeFragmentWithMenuId(menuItemId);
            }
        });

        bottomBar.mapColorForTab(0, red);
        bottomBar.mapColorForTab(1, blue);
        bottomBar.mapColorForTab(2, yellow);
        bottomBar.mapColorForTab(3, pink);
    }

    private void changeFragmentWithMenuId(int menuId) {
        switch (menuId) {
            case R.id.action_effects:
                changeFragment(effectFragment);
                if (thumbNail != null) {
                    effectFragment.setThumbNail(thumbNail);
                }
                break;
            case R.id.action_tune:
                if (tuneFragment == null) {
                    tuneFragment = new TuneFragment();
                    tuneFragment.setListener(this);
                }
                changeFragment(tuneFragment);
                if (thumbNail != null) {
                    tuneFragment.setThumbNail(canvasImage);
                }
                break;
        }
    }


    private void loadImage() {
        Intent it = getIntent();
        if (it != null && it.getData() != null) {
            targetUri = it.getData();
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    getThumbNail();
                }
            });

        }
    }

    private void getThumbNail() {
        subscription = Observable.just(targetUri)
                .observeOn(Schedulers.newThread())
                .map(new Func1<Uri, Bitmap>() {
                    @Override
                    public Bitmap call(Uri uri) {
                        return ImageHelper.getThumbNailFromUri(uri, getContentResolver(), imageView.getWidth(), imageView.getHeight());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        setImage(bitmap);
                    }
                });

    }

    private void setImage(Bitmap bitmap) {
        if (bitmap != null) {
            thumbNail = bitmap;
            canvasImage=thumbNail;
            imageView.setImageBitmap(thumbNail);
            effectFragment.setThumbNail(thumbNail);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (bottomBar != null) {
            bottomBar.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onChange(Bitmap bitmap) {
        canvasImage = bitmap;
        imageView.setImageBitmap(canvasImage);
    }
}
