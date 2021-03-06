package pl.gasior.analizasnu.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.utils.ViewPortHandler;


//import java.sql.Timestamp;

import java.util.Arrays;
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
    //private List<String> x = new ArrayList<String>();

    private int xIndex;
    private DateTimeFormatter f;
    private float[] y;
    private int[] x;

    private String label;

    private String filename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);



        Intent intent = getIntent();
        filename = intent.getCharSequenceExtra("filename").toString();
        String title = getString(R.string.graph_activity_title);
        //Log.i(TAG,"Odebralem "+filename);
        setTitle(title);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
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
                DreamListContract.DreamSliceEntry.COLUMN_USER_VERDICT,
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
        //Log.i("CP",selection);
        //Log.i("CP","Selection args:");
        for(String s : selectionArgs) {
            Log.i("CP",s);
        }
        return new CursorLoader(this,uri,projection,selection,selectionArgs,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Log.i(TAG,"Load finished");
        processData(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // adapter.swapCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void processData(Cursor c) {


        mChart = (BarChart) findViewById(R.id.chart1);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinValue(0f);

        xIndex = 0;

        label = "";

        f = new DateTimeFormatterBuilder()
                .appendYear(4, 4)
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

        DateTime pocz_s = DateTime.parse(c.getString(c.getColumnIndex(DreamListContract.DreamEntry.COLUMN_NAME_DATE_START)), f);
        DateTime kon_s = DateTime.parse(c.getString(c.getColumnIndex(DreamListContract.DreamEntry.COLUMN_NAME_DATE_END)), f);
       //Log.i(TAG, "przed while, poczatek snu: " + c.getString(c.getColumnIndex(DreamListContract.DreamEntry.COLUMN_NAME_DATE_START)) + " koniec: " + c.getString(c.getColumnIndex(DreamListContract.DreamEntry.COLUMN_NAME_DATE_END)));
        Seconds seconds = Seconds.secondsBetween(pocz_s, kon_s);
        int diff_s = seconds.getSeconds();
        //Log.i(TAG, "w while diff_s = " + diff_s);
        y = new float[diff_s + 1];
        Arrays.fill(y, -1.0f);
        x = new int[diff_s + 1];
        int lfrag = 0;
        DateTime[][] fragmenty = new DateTime[diff_s + 1][2];
        int[] verdict = new int[diff_s + 1];
        int[] datasetnr = new int[diff_s + 1];
        Arrays.fill(datasetnr, 2);
        while (!c.isAfterLast()) {
            fragmenty[c.getPosition()][0] = DateTime.parse(c.getString(c.getColumnIndex(DreamListContract.DreamSliceEntry.COLUMN_SLICE_START)).substring(0, 19), f);
            fragmenty[c.getPosition()][1] = DateTime.parse(c.getString(c.getColumnIndex(DreamListContract.DreamSliceEntry.COLUMN_SLICE_END)).substring(0, 19), f);
            verdict[c.getPosition()] = c.getInt(c.getColumnIndex(DreamListContract.DreamSliceEntry.COLUMN_USER_VERDICT));
            lfrag++;
            //Log.i(TAG, "w while, poczatek fragmentu: " + c.getString(c.getColumnIndex(DreamListContract.DreamSliceEntry.COLUMN_SLICE_START)).substring(0, 19) + " koniec: " + c.getString(c.getColumnIndex(DreamListContract.DreamSliceEntry.COLUMN_SLICE_END)).substring(0, 19));
            c.moveToNext();
        }
        int i;
        for(i = 0; i<diff_s;i++)
        {
            x[i] = i;
        }
        for(int j = 0; j < lfrag; j++)
        {
            //Log.i(TAG,"j = "+j +", verdict[j] =  "+verdict[j]);
            if(verdict[j] == 0 || verdict[j] == 1)
            {
                Seconds r1 = Seconds.secondsBetween(pocz_s, fragmenty[j][0]);
                Seconds r2 = Seconds.secondsBetween(fragmenty[j][0], fragmenty[j][1]);
                int roznica = r1.getSeconds();
                int rozmiar = r2.getSeconds();
                //
                for (i = roznica; i < roznica + rozmiar; i++) {
                    //                    Log.i(TAG,"we fragm, i = "+i);
                    if (verdict[j] == 1)
                    {
                        datasetnr[i] = 1;
                    }
                    else if(verdict[j] == 0)
                    {
                        datasetnr[i] = 0;
                    }
                    y[i] = 1;
                }
            }
        }
//        int l;
//        for(l = 0;l<diff_s;l++)
//        {
//            Log.i(TAG,l+", x =  "+x[l]+"y = "+y[l]);
//        }
        List<BarEntry> vals = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();
        for(int k=0;k<diff_s;k++)
        {
            //Log.i(TAG,"y["+k+"] = "+y[k]);
            //Log.i(TAG,"dataset = "+datasetnr[k]);
            BarEntry e = new BarEntry(y[k],x[k]);
            vals.add(e);
            xVals.add(" "+k);
        }
        BarDataSet set1 = new BarDataSet(vals, " ");
        int[] colors = new int[diff_s+1];
        //Arrays.fill(colors, Color.WHITE);
        for(int k = 0;k<diff_s;k++)
        {
            if(datasetnr[k] == 0){
                colors[k] = Color.rgb(30,144,255);
            }
            else if(datasetnr[k] == 1){
                colors[k] = Color.rgb(50,205,50);
            }
            else
            {
                colors[k] = Color.WHITE;
            }
            //Log.i(TAG,"color["+k+"]= "+datasetnr[k]);
        }
        set1.setAxisDependency(AxisDependency.LEFT);
        set1.setBarSpacePercent(0);
        set1.setColors(colors);
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        YAxis lyax = mChart.getAxisLeft();
        YAxis ryax = mChart.getAxisRight();
        XAxis xax = mChart.getXAxis();
        lyax.setAxisMaxValue(1.0f); //2.0f
        lyax.setDrawLabels(false);
        ryax.setDrawLabels(false);
        set1.setDrawValues(false);
        xax.setDrawLabels(true);
        xax.setValueFormatter(new XAxisValueFormatter() {
            @Override
            public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
                String x_final;
                int hours = Integer.parseInt(original.trim()) / 3600;
                int minutes = (Integer.parseInt(original.trim()) % 3600) / 60;
                int seconds = Integer.parseInt(original.trim()) % 60;
                x_final = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                return x_final;
            }
        });
        mChart.getLegend().setEnabled(false);
        mChart.setDescription(getString(R.string.graph_description));
        BarData data = new BarData(xVals, dataSets);
        mChart.setData(data);
        mChart.invalidate();
    }

}
