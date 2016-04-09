package pl.gasior.analizasnu.EventBusPOJO;

/**
 * Created by Piotrek on 09.04.2016.
 */
public class SilenceRemovalFinishedEvent {
    private boolean finished;

    public SilenceRemovalFinishedEvent() {
        finished = true;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
