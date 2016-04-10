package pl.gasior.analizasnu;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import pl.gasior.analizasnu.EventBusPOJO.EventTimeElapsed;
import pl.gasior.analizasnu.db.DreamListContract.DreamEntry;
import pl.gasior.analizasnu.db.DreamListDbHelper;
import pl.gasior.analizasnu.tarsosExtensions.CustomFFMPEGLocator;
import pl.gasior.analizasnu.tarsosExtensions.PipeOutProcessor;
import pl.gasior.analizasnu.tarsosExtensions.TimeReporter;
import pl.gasior.analizasnu.ui.MainActivity;

public class RecordService extends Service {

    private static final String TAG = RecordService.class.getName();

    private static final String ACTION_START_RECORDING = "pl.gasior.analizasnu.action.START_RECORDING";
    private static final String ACTION_STOP_RECORDING = "pl.gasior.analizasnu.action.STOP_RECORDING";

    private String recordingDate;

    private static final int NOTIFICATION_ID = 50;
    AudioDispatcher dispatcher;
    PipeOutProcessor outProcessor;
    Thread dispatcherThread = null;

    private MediaRecorder recorder;
    private TimeReportThread timeReportThread;

    public RecordService() {
    }

    private void makeForeground() {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setOngoing(true)
                        .setContentTitle("Nagrywanie")
                        .setSmallIcon(R.drawable.ic_hearing)
                        .setContentIntent(pi);
        startForeground(NOTIFICATION_ID,mBuilder.build());
    }


    private void startTARSOSDispatcher() {
        new CustomFFMPEGLocator(getApplicationContext());
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        recordingDate = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
        String currFilename = recordingDate+".mp4";
        String filename = getFilesDir().getAbsolutePath() + "/" + currFilename;
        outProcessor = new PipeOutProcessor(dispatcher.getFormat(),filename);
        dispatcher.addAudioProcessor(outProcessor);
        dispatcher.addAudioProcessor(new TimeReporter());
        dispatcherThread = new Thread(dispatcher,"Audio Dispatcher");
        dispatcherThread.start();
    }

    private void startMediaRecorder() {
        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recordingDate = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
        String currFilename = recordingDate+".mp4";
        String filename = getExternalFilesDir(null).getAbsolutePath() + "/" + currFilename;
        Log.i(TAG,filename);
        recorder.setOutputFile(filename);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        makeForeground();
        //startTARSOSDispatcher();
        startMediaRecorder();
        timeReportThread = new TimeReportThread();
        timeReportThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        recorder.stop();
        recorder.reset();
        recorder.release();
        timeReportThread.setShouldRun(false);
        timeReportThread = null;
        DreamListDbHelper dreamListDbHelper= new DreamListDbHelper(this);
        SQLiteDatabase db = dreamListDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DreamEntry.COLUMN_NAME_AUDIO_FILENAME,recordingDate+".mp4");
        db.insert(DreamEntry.TABLE_NAME, null, values);
        db.close();
        stopForeground(true);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    private class TimeReportThread extends Thread {
        int i=0;
        private boolean shouldRun = true;

        public void run() {
            i=0;
            while(shouldRun) {
                EventBus.getDefault().post(new EventTimeElapsed(i));
                i++;
                SystemClock.sleep(1000);
            }
        }

        public void setShouldRun(boolean shouldRun) {
            this.shouldRun = shouldRun;
        }
    }
}
