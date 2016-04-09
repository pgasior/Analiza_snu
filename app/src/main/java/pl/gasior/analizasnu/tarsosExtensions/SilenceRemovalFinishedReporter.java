package pl.gasior.analizasnu.tarsosExtensions;

import org.greenrobot.eventbus.EventBus;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import pl.gasior.analizasnu.EventBusPOJO.SilenceRemovalFinishedEvent;

/**
 * Created by Piotrek on 09.04.2016.
 */
public class SilenceRemovalFinishedReporter implements AudioProcessor {


    @Override
    public boolean process(AudioEvent audioEvent) {
        return true;
    }

    @Override
    public void processingFinished() {
        EventBus.getDefault().post(new SilenceRemovalFinishedEvent());

    }
}
