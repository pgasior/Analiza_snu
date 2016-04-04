package pl.gasior.analizasnu;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import pl.gasior.analizasnu.db.DreamListContract.DreamEntry;
import pl.gasior.analizasnu.db.DreamListDbHelper;
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
        new AndroidFFMPEGLocator(getApplicationContext());
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        recordingDate = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
        String currFilename = recordingDate+".aac";
        String filename = getExternalFilesDir(null).getAbsolutePath() + "/" + currFilename;
        outProcessor = new PipeOutProcessor(dispatcher.getFormat(),filename);
        dispatcher.addAudioProcessor(outProcessor);
        dispatcher.addAudioProcessor(new TimeReporter());
        dispatcherThread = new Thread(dispatcher,"Audio Dispatcher");
        dispatcherThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        makeForeground();
        startTARSOSDispatcher();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        dispatcher.stop();
        DreamListDbHelper dreamListDbHelper= new DreamListDbHelper(this);
        SQLiteDatabase db = dreamListDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DreamEntry.COLUMN_NAME_AUDIO_FILENAME,recordingDate+".aac");
        db.insert(DreamEntry.TABLE_NAME, null, values);
        db.close();
        stopForeground(true);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
