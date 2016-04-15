package pl.gasior.analizasnu.tarsosExtensions;

import org.greenrobot.eventbus.EventBus;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.beatroot.Event;
import pl.gasior.analizasnu.EventBusPOJO.TarsosPlayFinishedEvent;

/**
 * Created by Piotrek on 15.04.2016.
 */
public class PlayFinishedProcessor implements AudioProcessor {
    @Override
    public boolean process(AudioEvent audioEvent) {
        return true;
    }

    @Override
    public void processingFinished() {
        EventBus.getDefault().post(new TarsosPlayFinishedEvent());

    }
}
