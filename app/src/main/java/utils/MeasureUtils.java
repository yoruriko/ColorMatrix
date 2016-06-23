package utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by ricogao on 27/05/2016.
 */
public class MeasureUtils {

    public static int dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }
}
