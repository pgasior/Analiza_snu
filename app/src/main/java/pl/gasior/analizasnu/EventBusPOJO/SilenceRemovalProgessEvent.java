package pl.gasior.analizasnu.EventBusPOJO;

/**
 * Created by Piotrek on 09.04.2016.
 */
public class SilenceRemovalProgessEvent {

    private int progress;

    public SilenceRemovalProgessEvent(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
