package pl.gasior.analizasnu.ui;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import pl.gasior.analizasnu.R;
import pl.gasior.analizasnu.RatingBarColorPicker;
import pl.gasior.analizasnu.calendarDecorator.DreamDayDecorator;
import pl.gasior.analizasnu.calendarDecorator.RatingDecorator;
import pl.gasior.analizasnu.db.DreamListContract;
import pl.gasior.analizasnu.db.DreamListContract.DreamEntry;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DreamCalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DreamCalendarFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String TAG = this.getClass().getName().toString();
    MaterialCalendarView calendarView;
    public DreamCalendarFragment() {
        // Required empty public constructor
    }


    public static DreamCalendarFragment newInstance() {
        DreamCalendarFragment fragment = new DreamCalendarFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_dream_calendar, container, false);
        calendarView = (MaterialCalendarView)view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String stringDateStart = simpleSqliteDate(date.getDate());
                Calendar endCalendar = date.getCalendar();
                endCalendar.add(Calendar.DAY_OF_MONTH,1);
                String stringDateEnd = simpleSqliteDate(endCalendar.getTime());
                ((MainActivity)getActivity()).showDreamsForDateRange(stringDateStart,stringDateEnd);
            }
        });
        getLoaderManager().initLoader(0, null, this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("RF", "oncreateloader");
        Uri uri = DreamListContract.BASE_CONTENT_URI.buildUpon().appendPath(DreamListContract.PATH_DREAMS).build();
        Log.i("RF",uri.toString());
        String[] projection = new String[] {
                DreamEntry._ID,
                DreamEntry.COLUMN_NAME_AUDIO_FILENAME,
                DreamEntry.COLUMN_NAME_DATE_START,
                DreamEntry.COLUMN_NAME_METADATA_RATING
        };
        return new CursorLoader(getActivity().getApplicationContext(),
                uri,
                projection,
                null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        DreamDayDecorator decorator = createDreamDayDecorator(data);
//        calendarView.addDecorator(decorator);
        addDecorators(data,calendarView);
        getLoaderManager().destroyLoader(loader.getId());
    }

    Date sqliteStringToDate(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    String simpleSqliteDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private DreamDayDecorator createDreamDayDecorator(Cursor data) {
        HashSet<CalendarDay> dates = new HashSet<>();
        while(data.moveToNext()) {
            Date date = sqliteStringToDate(data.getString(data.getColumnIndex(DreamEntry.COLUMN_NAME_DATE_START)));
            dates.add(CalendarDay.from(date));
        }
        DreamDayDecorator decorator = new DreamDayDecorator(dates);
        return decorator;
    }

    private void addDecorators(Cursor data, MaterialCalendarView calendarView) {
        Log.i(TAG,"addDecorators");
        List<HashSet<CalendarDay>> ratingDates = new ArrayList<HashSet<CalendarDay>>();
        HashSet<CalendarDay> dates = new HashSet<>();
        for(int i=0;i<5;i++) {
            ratingDates.add(new HashSet<CalendarDay>());
        }
//        data.moveToFirst();
        float rating;
        while(data.moveToNext()) {
            rating = data.getFloat(data.getColumnIndex(DreamEntry.COLUMN_NAME_METADATA_RATING));
            Date date = sqliteStringToDate(data.getString(data.getColumnIndex(DreamEntry.COLUMN_NAME_DATE_START)));
            int int_rating  = (int)rating;
            if(rating >0) {
                int tmp_rating = (int)Math.ceil(rating);

                Log.i(TAG,String.valueOf(tmp_rating));
                ratingDates.get(tmp_rating-1).add(CalendarDay.from(date));
            }
            dates.add(CalendarDay.from(date));
            Log.i(TAG,"Dodalem " + date+" z ratingiem "+rating);
        }
        for(int i=0;i<5;i++) {
            if(!ratingDates.get(i).isEmpty()) {
                RatingDecorator decorator = new RatingDecorator(ratingDates.get(i), RatingBarColorPicker.getColorForProgress((i+1)*2,getActivity()));
                calendarView.addDecorator(decorator);
            }
        }
        calendarView.addDecorator(new DreamDayDecorator(dates));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
