package pl.gasior.analizasnu.tarsosExtensions;

import org.greenrobot.eventbus.EventBus;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import pl.gasior.analizasnu.EventBusPOJO.EventTimeElapsed;

/**
 * Created by Piotrek on 03.04.2016.
 */
public class TimeReporter implements AudioProcessor {
    int lastTime;
    int offset;

    public TimeReporter() {
        lastTime= 0;
        offset = 0;
    }
    public TimeReporter(int offset) {
        lastTime = 0;
        this.offset = offset;
    }
    @Override
    public boolean process(AudioEvent audioEvent) {
        int currentTime = (int)audioEvent.getTimeStamp();
        if(currentTime>lastTime+offset) {
            lastTime=currentTime;
            EventBus.getDefault().post(new EventTimeElapsed(currentTime));
        }
        return true;
    }

    @Override
    public void processingFinished() {

    }
}
