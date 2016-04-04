package pl.gasior.analizasnu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import pl.gasior.analizasnu.R;

public class DreamDetailActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    private boolean playing;
    private PlayRetainingFragment playFragment;
    private Button startPlayButton;
    private Button stopPlayButton;
    private Button analysisButton;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        filename = intent.getCharSequenceExtra("filename").toString();
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
        playing = playFragment.isPlaying();
        startPlayButton = (Button)findViewById(R.id.startPlayButton);
        startPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playFragment.play(filename);
                updateUi();
            }
        });
        stopPlayButton = (Button)findViewById(R.id.stopPlayButton);
        stopPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playFragment.stopPlaying();
                updateUi();
            }
        });
        analysisButton = (Button)findViewById(R.id.analysisButton);
        //Log.i(TAG,"updateui w oncreate activity");
        updateUi();

    }

    public void updateUi() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (playFragment.isPlaying()) {
                    startPlayButton.setEnabled(false);
                    stopPlayButton.setEnabled(true);
                    analysisButton.setEnabled(false);
                } else {
                    startPlayButton.setEnabled(true);
                    stopPlayButton.setEnabled(false);
                    analysisButton.setEnabled(true);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(playFragment.isPlaying()) {
            playFragment.stopPlaying();
        }
        super.onBackPressed();
    }
}
