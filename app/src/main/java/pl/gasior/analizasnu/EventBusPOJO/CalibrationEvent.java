package pl.gasior.analizasnu.EventBusPOJO;

/**
 * Created by Piotrek on 15.04.2016.
 */
public class CalibrationEvent {
    private double calibrationValue;
    public CalibrationEvent(double calibrationValue) {
        this.setCalibrationValue(calibrationValue);
    }

    public double getCalibrationValue() {
        return calibrationValue;
    }

    public void setCalibrationValue(double calibrationValue) {
        this.calibrationValue = calibrationValue;
    }
}
