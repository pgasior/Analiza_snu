package pl.gasior.analizasnu.calendarDecorator;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Piotrek on 28.05.2016.
 */
public class RatingDecorator implements DayViewDecorator {
    int color;
    private HashSet<CalendarDay> dates;
    private final Drawable highlightDrawable;

    public RatingDecorator(Collection<CalendarDay> dates, int color) {
        this.dates=new HashSet<>(dates);
        this.color = color;
        highlightDrawable = new ColorDrawable(color);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(highlightDrawable);
    }
}
