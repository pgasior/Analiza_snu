package pl.gasior.analizasnu.EventBusPOJO;

/**
 * Created by Piotrek on 15.04.2016.
 */
public class TarsosPlayFinishedEvent {
    private boolean finished;
    public TarsosPlayFinishedEvent() {
        setFinished(true);
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
