package pl.gasior.analizasnu.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

//import java.sql.Timestamp;

import java.util.List;
import java.util.ArrayList;

import pl.gasior.analizasnu.R;
import pl.gasior.analizasnu.db.DreamListContract;

/**
 * Created by serq9_000 on 2016-04-16.
 */
public class GraphActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private final String TAG = this.getClass().getName();
    SimpleCursorAdapter adapter;
    private BarChart mChart;
    private List<String> x = new ArrayList<String>();

    private int xIndex;
    private DateTimeFormatter f;
    private float[] y;

    private String label;

    private String filename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
 //       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
 //       setSupportActionBar(toolbar);



        Intent intent = getIntent();
        filename = intent.getCharSequenceExtra("filename").toString();
        Log.i(TAG,"Odebralem "+filename);

        getSupportLoaderManager().initLoader(0, null, this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DreamListContract.BASE_CONTENT_URI.buildUpon().appendPath(DreamListContract.PATH_DREAM_SLICES).build();
        String[] projection = new String[] {
                DreamListContract.DreamSliceEntry.TABLE_NAME+"."+DreamListContract.DreamSliceEntry._ID,
                DreamListContract.DreamSliceEntry.COLUMN_SLICE_FILENAME,
                DreamListContract.DreamSliceEntry.COLUMN_SLICE_START,
                DreamListContract.DreamSliceEntry.COLUMN_SLICE_END,
                DreamListContract.DreamEntry.TABLE_NAME+"."+DreamListContract.DreamEntry._ID,
                DreamListContract.DreamEntry.COLUMN_NAME_AUDIO_FILENAME,
                DreamListContract.DreamEntry.COLUMN_NAME_DATE_START,
                DreamListContract.DreamEntry.COLUMN_NAME_DATE_END
        };
        String selection= DreamListContract.DreamEntry.COLUMN_NAME_AUDIO_FILENAME+" = ?";
        //String filename = "";
        String[] selectionArgs= new String[] {filename};

        String zero = DreamListContract.DreamEntry.COLUMN_NAME_DATE_START;
        String[] sounds = new String[] {
                DreamListContract.DreamEntry.COLUMN_NAME_DATE_START
        };
        Log.i("CP",selection);
        Log.i("CP","Selection args:");
        for(String s : selectionArgs) {
            Log.i("CP",s);
        }
        return new CursorLoader(this,uri,projection,selection,selectionArgs,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG,"Load finished");
        processData(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // adapter.swapCursor(null);
    }

    public void processData(Cursor c){

        mChart = new BarChart(this);

        xIndex = 0;

        label = "";

        f = new DateTimeFormatterBuilder()
                .appendYear(4,4)
                .appendLiteral("-")
                .appendMonthOfYear(2)
                .appendLiteral("-")
                .appendDayOfMonth(2)
                .appendLiteral(" ")
                .appendHourOfDay(2)
                .appendLiteral(":")
                .appendMinuteOfHour(2)
                .appendLiteral(":")
                .appendSecondOfMinute(2)
                .toFormatter();


        c.moveToFirst();
//        int l = c.getColumnCount();
//        for(int i=0;i<l;i++)
//        {
//            Log.i(TAG,i+" "+c.getString(i));
//        }
//        Log.i(TAG,c.getString(c.getColumnIndex(DreamListContract.DreamEntry.COLUMN_NAME_DATE_START)));
        //c.get
//        while(c.moveToNext()) {
//            Log.i(TAG,"wpis");
//        }

        DateTime pocz_s = DateTime.parse(c.getString(c.getColumnIndex(DreamListContract.DreamEntry.COLUMN_NAME_DATE_START)),f);
        DateTime kon_s = DateTime.parse(c.getString(c.getColumnIndex(DreamListContract.DreamEntry.COLUMN_NAME_DATE_END)),f);
        int diff_s = kon_s.compareTo(pocz_s);

        y = new float[diff_s/1000];

        while(c.moveToNext()) {
            DateTime pocz_f = DateTime.parse(c.getString(c.getColumnIndex(DreamListContract.DreamEntry.COLUMN_NAME_DATE_START)).substring(0,19),f);
            DateTime kon_f =  DateTime.parse(c.getString(c.getColumnIndex(DreamListContract.DreamEntry.COLUMN_NAME_DATE_END)).substring(0,19),f);

            int diff_f = kon_f.compareTo(pocz_f);

            for(int i=0; i<diff_s/1000;i++)
            {
                x.add(String.valueOf(i));
                if(pocz_s.plusSeconds(i) == pocz_f)
                {
                    y[i] = 1.0f;
                }
                else
                {
                    y[i] = 0.0f;
                }
            }
        }
        BarEntry be = new BarEntry(y,xIndex);

        List<BarEntry> list_be = new ArrayList<BarEntry>();
        list_be.add(be);
        BarDataSet ds = new BarDataSet(list_be,label);

        BarData d = new BarData(x,ds);
        mChart.setData(d);

    }

}
