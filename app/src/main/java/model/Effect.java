package model;

import android.graphics.Bitmap;
import android.graphics.ColorMatrix;

/**
 * Created by ricogao on 27/05/2016.
 */
public class Effect {
    private float[] matrix;
    private String name;
    private Bitmap thumbNail;

    public Effect(float[] matrix, String name) {
        this.matrix = matrix;
        this.name = name;
    }

    public float[] getMatrix() {
        return matrix;
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getThumbNail() {
        return thumbNail;
    }

    public void setThumbNail(Bitmap thumbNail) {
        this.thumbNail = thumbNail;
    }

    public ColorMatrix getColorMatrix(){
        ColorMatrix colorMatrix=new ColorMatrix(matrix);
        return colorMatrix;
    }
}
