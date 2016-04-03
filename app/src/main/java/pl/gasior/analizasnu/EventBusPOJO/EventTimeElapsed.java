package pl.gasior.analizasnu.EventBusPOJO;

/**
 * Created by Piotrek on 03.04.2016.
 */
public class EventTimeElapsed {
    private int time;

    public EventTimeElapsed(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
