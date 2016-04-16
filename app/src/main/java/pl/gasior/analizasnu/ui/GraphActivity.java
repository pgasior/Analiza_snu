package pl.gasior.analizasnu.ui;

import android.content.Intent;
import android.database.Cursor;
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

import pl.gasior.analizasnu.R;
import pl.gasior.analizasnu.db.DreamListContract;

/**
 * Created by serq9_000 on 2016-04-16.
 */
public class GraphActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DreamListContract.BASE_CONTENT_URI.buildUpon().appendPath(DreamListContract.PATH_DREAM_SLICES).build();
        String[] projection = new String[] {
                DreamListContract.DreamSliceEntry.TABLE_NAME+"."+DreamListContract.DreamSliceEntry._ID,
                DreamListContract.DreamSliceEntry.COLUMN_SLICE_FILENAME
        };
        String selection= DreamListContract.DreamEntry.COLUMN_NAME_AUDIO_FILENAME+" = ?";
        String filename = "";
        String[] selectionArgs= new String[] {filename};
        return new CursorLoader(this,uri,projection,selection,selectionArgs,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG,"Load finished");
        //adapter.swapCursor(data);
    }

   public void onLoaderReset(Loader<Cursor> loader) {
       // adapter.swapCursor(null);
    }

}
