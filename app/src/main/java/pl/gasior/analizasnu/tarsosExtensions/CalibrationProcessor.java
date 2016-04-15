package pl.gasior.analizasnu.tarsosExtensions;

import org.greenrobot.eventbus.EventBus;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import pl.gasior.analizasnu.EventBusPOJO.CalibrationEvent;

/**
 * Created by Piotrek on 15.04.2016.
 */
public class CalibrationProcessor implements AudioProcessor {
    double lastTime = 0;
    @Override
    public boolean process(AudioEvent audioEvent) {

        if(audioEvent.getTimeStamp()>lastTime+0.2) {
            lastTime = audioEvent.getTimeStamp();
            EventBus.getDefault().post(new CalibrationEvent(audioEvent.getdBSPL()));
        }


        return true;
    }

    @Override
    public void processingFinished() {

    }
}
