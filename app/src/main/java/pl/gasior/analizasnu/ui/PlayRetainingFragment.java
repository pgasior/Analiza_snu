package pl.gasior.analizasnu.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.rtp.AudioStream;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.IOException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;

/**
 * Created by Piotrek on 04.04.2016.
 */
public class PlayRetainingFragment extends Fragment {
    MediaPlayer mPlayer;
    private final String LOG_TAG = this.getClass().getName();
    private boolean playing;
    private AudioDispatcher audioDispatcher;
    private Thread dispatcherThread;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "Oncreate fragmentu");
        //setPlaying(false);
        // retain this fragment
        setRetainInstance(true);

    }

    public void playTarsos(String plik) {
        audioDispatcher = AudioDispatcherFactory.fromPipe(
                getActivity().getExternalFilesDir(null).getAbsolutePath() + "/" + plik,
                22050,1024,0);
        audioDispatcher.addAudioProcessor(new AndroidAudioPlayer(audioDispatcher.getFormat()));
        dispatcherThread = new Thread(audioDispatcher,"Audio Dispatcher");
        dispatcherThread.start();

    }

    public void stopTarsos() {
        audioDispatcher.stop();
    }


    public void play(String plik) {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playing = false;
                mPlayer.release();
                ((DreamDetailActivity) getActivity()).updateUi();

            }
        });
        try {
            Log.i(LOG_TAG,getActivity().getExternalFilesDir(null).getAbsolutePath() + "/" +plik);
            mPlayer.setDataSource(getActivity().getExternalFilesDir(null).getAbsolutePath() + "/" +plik);

            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        setPlaying(true);

    }

    public void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        setPlaying(false);
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}
