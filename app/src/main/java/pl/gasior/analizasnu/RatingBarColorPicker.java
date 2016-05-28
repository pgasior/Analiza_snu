package pl.gasior.analizasnu;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;

/**
 * Created by Piotrek on 28.05.2016.
 */
public class RatingBarColorPicker {
    public static int getColorForProgress(float progress, Context context) {
        int color;
        if(progress >=9) {
            color = ContextCompat.getColor(context,R.color.five_stars);
        } else if (progress >=7) {
            color = ContextCompat.getColor(context,R.color.four_stars);
        } else if (progress >=5) {
            color = ContextCompat.getColor(context,R.color.three_stars);
        } else if (progress >=3) {
            color = ContextCompat.getColor(context,R.color.two_stars);
        } else if (progress >=1) {
            color = ContextCompat.getColor(context,R.color.one_star);
        } else {
            TypedArray array = context.getTheme().obtainStyledAttributes(new int[] {
                    android.R.attr.colorBackground,
            });
            color = array.getColor(0, 0xFF00FF);
        }
        return color;
    }
}
