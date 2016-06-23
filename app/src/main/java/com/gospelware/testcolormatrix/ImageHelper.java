package com.gospelware.testcolormatrix;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by ricogao on 24/05/2016.
 */
public class ImageHelper {

    public static Bitmap applyMatrix(Bitmap bitmap, ColorMatrix matrix) {
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bmp;
    }

    public static Bitmap generateBitmap(Bitmap bitmap, float hue, float saturation, float luminance) {
        ColorMatrix hueMatrix = new ColorMatrix();
        hueMatrix.setRotate(0, hue);
        hueMatrix.setRotate(1, hue);
        hueMatrix.setRotate(2, hue);

        ColorMatrix lumMatrix = new ColorMatrix();
        lumMatrix.setScale(luminance, luminance, luminance, 1);

        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(saturation);

        ColorMatrix imageMatrix = new ColorMatrix();

        imageMatrix.postConcat(hueMatrix);
        imageMatrix.postConcat(saturationMatrix);
        imageMatrix.postConcat(lumMatrix);

        return applyMatrix(bitmap, imageMatrix);
    }

    public static Bitmap getThumbNailWithEffect(Bitmap bitmap, int size, ColorMatrix matrix) {
        float scale = size * 1f / bitmap.getHeight();
        int targetWidth = Math.round(scale * bitmap.getWidth());
        int targetHeight = Math.round(scale * bitmap.getHeight());
        Bitmap bmp = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
        return applyMatrix(bmp, matrix);
    }

    public static Bitmap getThumbNailFromUri(Uri uri, ContentResolver resolver, int maxWidth, int maxHeight) {
        File imageFile = new File(uri.getPath());
        Bitmap bmp = null;

        try {
            Bitmap raw = MediaStore.Images.Media.getBitmap(resolver, uri);

            float scale = getScale(raw.getWidth(), raw.getHeight(), maxWidth, maxHeight);
            int targetWidth = Math.round(scale * raw.getWidth());
            int targetHeight = Math.round(scale * raw.getHeight());

            bmp = ThumbnailUtils.extractThumbnail(raw, targetWidth, targetHeight);
        } catch (IOException e) {
            Log.e(ImageHelper.class.getSimpleName(), "Error:" + e.getMessage());
        }

        return bmp;
    }

    public static float getScale(int rawWidth, int rawHeight, int maxWidth, int maxHeight) {
        float scale = (maxWidth > maxHeight) ? maxWidth * 1f / rawWidth : maxHeight * 1f / rawHeight;
        return scale;

    }

}
