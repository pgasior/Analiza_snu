package pl.gasior.analizasnu.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;

import pl.gasior.analizasnu.R;
import pl.gasior.analizasnu.db.DreamListContract;
import pl.gasior.analizasnu.db.DreamListContract.DreamEntry;
import pl.gasior.analizasnu.db.DreamListDbHelper;

public class DreamMetadataEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    EditText dreamName;
    EditText dreamDescription;
    RatingBar dreamRating;
    int dreamId;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_metadata_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.dream_metadata_toolbar);
        setSupportActionBar(toolbar);
        dreamName = (EditText)findViewById(R.id.dreamNameEditText);
        dreamDescription = (EditText)findViewById(R.id.dreamDescriptionEditText);
        dreamRating = (RatingBar)findViewById(R.id.dreamRatingBar);
        Intent intent = getIntent();
        filename = intent.getStringExtra("filename");
        getSupportLoaderManager().initLoader(0, null, this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.adction_cancel:
                finish();
                return true;
            case R.id.adction_accept:
                updateMetadata();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateMetadata() {
        DreamListDbHelper helper = new DreamListDbHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DreamEntry.COLUMN_NAME_METADATA_NAME,dreamName.getText().toString());
        cv.put(DreamEntry.COLUMN_NAME_METADATA_RATING,dreamRating.getRating());
        cv.put(DreamEntry.COLUMN_NAME_METADATA_DESCRIPTION,dreamDescription.getText().toString());
        String whereClause = DreamEntry.COLUMN_NAME_AUDIO_FILENAME+"=?";
        String[] whereArgs = new String[] {filename};
        db.update(DreamEntry.TABLE_NAME,cv,whereClause,whereArgs);
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.metadata_appbar_menu,menu);
        return true;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri uri = DreamListContract.BASE_CONTENT_URI.buildUpon().appendPath(DreamListContract.PATH_DREAMS).build();
        String projection[] = new String[] {
                DreamEntry._ID,
                DreamEntry.COLUMN_NAME_AUDIO_FILENAME,
                DreamEntry.COLUMN_NAME_METADATA_NAME,
                DreamEntry.COLUMN_NAME_METADATA_RATING,
                DreamEntry.COLUMN_NAME_METADATA_DESCRIPTION
        };
        String selection = DreamEntry.COLUMN_NAME_AUDIO_FILENAME+"=?";
        String selectionArgs[] = new String[] {filename};
        return new CursorLoader(this,
                uri,
                projection,
                selection,selectionArgs,null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        data.moveToFirst();
        dreamName.setText(data.getString(data.getColumnIndex(DreamEntry.COLUMN_NAME_METADATA_NAME)));
        dreamRating.setRating(data.getFloat(data.getColumnIndex(DreamEntry.COLUMN_NAME_METADATA_RATING)));
        dreamDescription.setText(data.getString(data.getColumnIndex(DreamEntry.COLUMN_NAME_METADATA_DESCRIPTION)));

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
