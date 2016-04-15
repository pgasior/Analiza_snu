package pl.gasior.analizasnu.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import be.tarsos.dsp.AudioDispatcher;
import pl.gasior.analizasnu.tarsosExtensions.AudioDispatcherFactory;
import pl.gasior.analizasnu.tarsosExtensions.CalibrationProcessor;

/**
 * Created by Piotrek on 15.04.2016.
 */
public class CalibrationRetainingFragment extends Fragment {

    private AudioDispatcher audioDispatcher;
    private Thread dispatcherThread;
    private boolean calibrating;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void startCalibration() {
        audioDispatcher = AudioDispatcherFactory.alternativeFromDefaultMicrophone(22050, 1024, 0);
        audioDispatcher.addAudioProcessor(new CalibrationProcessor());
        dispatcherThread = new Thread(audioDispatcher,"Calibration");
        dispatcherThread.start();
        calibrating = true;
    }

    public void stopCalibration() {
        audioDispatcher.stop();
        calibrating = false;
    }

    public boolean isCalibrating() {
        return calibrating;
    }

    public void setCalibrating(boolean calibrating) {
        this.calibrating = calibrating;
    }
}
