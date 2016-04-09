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
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import pl.gasior.analizasnu.EventBusPOJO.SilenceRemovalFinishedEvent;
import pl.gasior.analizasnu.R;
import pl.gasior.analizasnu.SilenceRemovalService;

public class DreamDetailActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    private boolean playing;
    private boolean removingSilence;
    private PlayRetainingFragment playFragment;
    private Button startPlayButton;
    private Button stopPlayButton;
    private Button analysisButton;
    private ProgressBar progressBar;
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
        analysisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removingSilence=!removingSilence;
                Intent intent = new Intent(getApplicationContext(),SilenceRemovalService.class);
                intent.putExtra("filename",filename);
                startService(intent);
                updateUi();
            }
        });
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        if(savedInstanceState!=null) {
            removingSilence = savedInstanceState.getBoolean("removingSilence");
        } else {
            removingSilence = false;
        }
        //Log.i(TAG,"updateui w oncreate activity");
        updateUi();
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
    public void handleSilenceRemovalFinished(SilenceRemovalFinishedEvent ev) {
        removingSilence = false;
        Intent intent = new Intent(this,SilenceRemovalService.class);
        stopService(intent);
        updateUi();
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
                if (playFragment.isPlaying()) {
                    startPlayButton.setEnabled(false);
                    stopPlayButton.setEnabled(true);
                    analysisButton.setEnabled(false);
                } else {
                    startPlayButton.setEnabled(true);
                    stopPlayButton.setEnabled(false);
                    analysisButton.setEnabled(true);
                }

                if(removingSilence) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
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
