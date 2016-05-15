package pl.gasior.analizasnu.ui;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.EditText;
import android.widget.RatingBar;

import pl.gasior.analizasnu.R;

public class DreamMetadataEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks{

    EditText dreamName;
    EditText dreamDescription;
    RatingBar dreamRating;
    int dreamId;

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
        dreamId = intent.getExtras().getInt("dreamId");
        getSupportLoaderManager().initLoader(0, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.metadata_appbar_menu,menu);
        return true;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
