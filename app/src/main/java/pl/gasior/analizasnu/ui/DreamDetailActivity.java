package pl.gasior.analizasnu.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import pl.gasior.analizasnu.EventBusPOJO.EventTimeElapsed;
import pl.gasior.analizasnu.EventBusPOJO.SilenceRemovalFinishedEvent;
import pl.gasior.analizasnu.EventBusPOJO.SilenceRemovalProgessEvent;
import pl.gasior.analizasnu.EventBusPOJO.SlicesChangedEvent;
import pl.gasior.analizasnu.EventBusPOJO.TarsosPlayFinishedEvent;
import pl.gasior.analizasnu.R;
import pl.gasior.analizasnu.SilenceRemovalService;
import pl.gasior.analizasnu.db.DreamListContract;
import pl.gasior.analizasnu.db.DreamListContract.DreamSliceEntry;
import pl.gasior.analizasnu.db.DreamListDbHelper;

public class DreamDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, ConfirmAnalysisDialogFragment.ConfirmAnalysisDialogListener{

    private final String TAG = this.getClass().getName();
    public final static String COLUMN_SLICE_START_ALIAS = "COLUMN_SLICE_START_ALIAS";

    private boolean playing;
    private boolean removingSilence;
    private PlayRetainingFragment playFragment;
    private Button startPlayButton;
    private Button stopPlayButton;
    private Button analysisButton;
    private Button graphButton;
    private ProgressBar progressBar;
    private String filename;
    private TextView tvProcessed;
    private ListView slicesListView;
    private Button metadataButton;
    //SimpleCursorAdapter adapter;
    SlicesCursorAdapter slicesCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        filename = intent.getCharSequenceExtra("filename").toString();
        String dateStart = intent.getStringExtra("dateStart");
        setTitle(dateStart);
        Log.i(TAG, "Odczytalem: " + filename);
        FragmentManager fm = getSupportFragmentManager();
        playFragment = (PlayRetainingFragment) fm.findFragmentByTag("playFragment");
        if (playFragment == null) {
            // add the fragment
            playFragment = new PlayRetainingFragment();
            fm.beginTransaction().add(playFragment, "playFragment").commit();
            playing = false;
            playFragment.setPlaying(playing);
            // load the data from the web
        }

        tvProcessed = (TextView)findViewById(R.id.textView3);
        playing = playFragment.isPlaying();
        startPlayButton = (Button)findViewById(R.id.startPlayButton);
        startPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playFragment.play(filename);
                playFragment.playTarsos(filename);
                //updateUi();
            }
        });
        stopPlayButton = (Button)findViewById(R.id.stopPlayButton);
        stopPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playFragment.stopPlaying();
                playFragment.stopTarsos();
                updateUi();
            }
        });
        analysisButton = (Button)findViewById(R.id.analysisButton);
        analysisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmAnalysisDialogFragment dialog = new ConfirmAnalysisDialogFragment();
                dialog.show(getSupportFragmentManager(),"cnfirmDialog");
            }
        });
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        if(savedInstanceState!=null) {
            removingSilence = savedInstanceState.getBoolean("removingSilence");
        } else {
            removingSilence = false;
        }

        slicesListView = (ListView)findViewById(R.id.slicesListView);
        String[] fromColumns = {DreamSliceEntry.COLUMN_SLICE_FILENAME};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1
//        adapter = new SimpleCursorAdapter(this,
//                android.R.layout.simple_list_item_1,null,
//                fromColumns,toViews,0);
//        slicesListView.setAdapter(adapter);
        slicesCursorAdapter = new SlicesCursorAdapter(this,null,0);
        slicesListView.setAdapter(slicesCursorAdapter);

        slicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                String filename = c.getString(c.getColumnIndex(DreamSliceEntry.COLUMN_SLICE_FILENAME));
                if(playFragment.isPlaying()) {
                    playFragment.stopTarsos();
                }
                playFragment.playTarsos(filename);
                updateUi();
            }
        });

        graphButton = (Button)findViewById(R.id.graphButton);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(v.getContext(),GraphActivity.class);
                intent2.putExtra("filename", filename);
                startActivity(intent2);
                updateUi();
            }
        });

        metadataButton = (Button)findViewById(R.id.metadataButton);
        metadataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),DreamMetadataEditActivity.class);
                intent.putExtra("filename",filename);
                startActivity(intent);
            }
        });



        //Log.i(TAG,"updateui w oncreate activity");
        getSupportLoaderManager().initLoader(0, null, this);
        updateUi();

    }

    private void deleteOldSlices() {
        Log.i(TAG,"deleteOldSlices");
        Uri uri = DreamListContract.BASE_CONTENT_URI.buildUpon().appendPath(DreamListContract.PATH_DREAM_SLICES).build();
        String[] projection = new String[] {
                DreamSliceEntry.TABLE_NAME+"."+ DreamSliceEntry._ID,
                DreamSliceEntry.COLUMN_SLICE_FILENAME
        };
        String selection= DreamListContract.DreamEntry.COLUMN_NAME_AUDIO_FILENAME+" = ?";
        String[] selectionArgs= new String[] {filename};
        Cursor c  = getContentResolver().query(uri,projection,selection,selectionArgs,null);
        //c.moveToFirst();
        SQLiteDatabase db = new DreamListDbHelper(this).getWritableDatabase();
        while(c.moveToNext()) {
            long id = c.getLong(c.getColumnIndex(DreamSliceEntry._ID));
            String filename = c.getString(c.getColumnIndex(DreamSliceEntry.COLUMN_SLICE_FILENAME));
            Log.i(TAG,"Filename: "+filename+"  id: "+id);
            //Log.i(TAG,"Filename: "+filename);
            File f = new File(getExternalFilesDir(null).getAbsolutePath(),filename);
            if(f.exists()) {
                f.delete();
            }
            db.delete(DreamSliceEntry.TABLE_NAME, DreamSliceEntry._ID+"=?",new String[] {String.valueOf(id)});
        }
        c.close();
        db.close();
    }


    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void playFinished(TarsosPlayFinishedEvent ev) {
        playFragment.setPlaying(false);
        updateUi();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateTvProcessed(EventTimeElapsed ev) {
        int totalSecs = ev.getTime();
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        tvProcessed.setText(timeString);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleSilenceRemovalFinished(SilenceRemovalFinishedEvent ev) {
        removingSilence = false;
        Intent intent = new Intent(this,SilenceRemovalService.class);
        stopService(intent);
        updateUi();
        getSupportLoaderManager().restartLoader(0,null,this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleSliceChanged(SlicesChangedEvent ev) {
        getSupportLoaderManager().restartLoader(0,null,this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("removingSilence",removingSilence);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void updateUi() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"isplaying: "+playFragment.isPlaying());
                if (playFragment.isPlaying()) {
                    startPlayButton.setEnabled(false);
                    stopPlayButton.setEnabled(true);
                    analysisButton.setEnabled(false);
                } else {
                    startPlayButton.setEnabled(true);
                    stopPlayButton.setEnabled(false);
                    analysisButton.setEnabled(true);

                    if(removingSilence ) {
                        progressBar.setVisibility(View.VISIBLE);
                        startPlayButton.setEnabled(false);
                        stopPlayButton.setEnabled(false);
                        analysisButton.setEnabled(false);

                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        startPlayButton.setEnabled(true);
                        //stopPlayButton.setEnabled(true);
                        analysisButton.setEnabled(true);
                    }
                }



            }
        });

    }

    @Override
    public void onBackPressed() {
        if(playFragment.isPlaying()) {
            //playFragment.stopPlaying();
            playFragment.stopTarsos();
        }
        super.onBackPressed();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DreamListContract.BASE_CONTENT_URI.buildUpon().appendPath(DreamListContract.PATH_DREAM_SLICES).build();
        String[] projection = new String[] {
                DreamSliceEntry.TABLE_NAME+"."+ DreamSliceEntry._ID,
                DreamSliceEntry.COLUMN_SLICE_FILENAME,
                DreamSliceEntry.COLUMN_USER_VERDICT,
                "time("+DreamSliceEntry.COLUMN_SLICE_START+") as " + COLUMN_SLICE_START_ALIAS
        };
        String selection= DreamListContract.DreamEntry.COLUMN_NAME_AUDIO_FILENAME+" = ?";
        String[] selectionArgs= new String[] {filename};
        return new CursorLoader(this,uri,projection,selection,selectionArgs,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG,"Load finished");
        //adapter.swapCursor(data);
        slicesCursorAdapter.swapCursor(data);
        if(data.getCount()>0){
            graphButton.setEnabled(true);
            tvProcessed.setVisibility(View.INVISIBLE);
        } else {

            tvProcessed.setVisibility(View.VISIBLE);
            tvProcessed.setText(R.string.press_button_to_analyze);
            graphButton.setEnabled(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //adapter.swapCursor(null);
        slicesCursorAdapter.swapCursor(null);
    }

    @Override
    public void onConfirmAnalysisDialogPositiveClick() {
        removingSilence=!removingSilence;
        Intent intent = new Intent(getApplicationContext(),SilenceRemovalService.class);
        intent.putExtra("filename",filename);
        deleteOldSlices();
        startService(intent);
        updateUi();
    }

    @Override
    public void onConfirmAnalysisDialogNegativeClick() {

    }
}
