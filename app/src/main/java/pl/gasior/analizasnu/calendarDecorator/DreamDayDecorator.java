package pl.gasior.analizasnu.calendarDecorator;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Piotrek on 14.05.2016.
 */
public class DreamDayDecorator implements DayViewDecorator {
    private HashSet<CalendarDay> dates;

    public DreamDayDecorator(Collection<CalendarDay> dates) {
        this.dates=new HashSet<>(dates);
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan((float) 10.0));
    }
}
