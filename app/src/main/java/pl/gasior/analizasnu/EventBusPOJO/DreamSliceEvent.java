package pl.gasior.analizasnu.EventBusPOJO;

/**
 * Created by Piotrek on 15.04.2016.
 */
public class DreamSliceEvent {
    private String startDate;
    private String endDate;
    private String filename;
    private int type;

    public DreamSliceEvent(int type, String filename, String startDate, String endDate) {
        this.type=type;
        this.startDate=startDate;
        this.endDate=endDate;
        this.filename = filename;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
