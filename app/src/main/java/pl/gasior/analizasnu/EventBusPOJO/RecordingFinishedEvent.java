package pl.gasior.analizasnu.EventBusPOJO;

/**
 * Created by Piotrek on 15.05.2016.
 */
public class RecordingFinishedEvent {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public RecordingFinishedEvent(String filename) {
        this.filename=filename;
    }
}
