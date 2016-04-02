package pl.gasior.analizasnu;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import pl.gasior.analizasnu.tarsosExtensions.PipeOutProcessor;

public class RecordService extends Service {

    private static final String TAG = RecordService.class.getName();

    private static final String ACTION_START_RECORDING = "pl.gasior.analizasnu.action.START_RECORDING";
    private static final String ACTION_STOP_RECORDING = "pl.gasior.analizasnu.action.STOP_RECORDING";

    private static final int NOTIFICATION_ID = 50;
    AudioDispatcher dispatcher;
    PipeOutProcessor outProcessor;
    Thread dispatcherThread = null;

    public RecordService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setOngoing(true)
                        .setContentTitle("Nagrywanie")
                        .setSmallIcon(R.drawable.ic_hearing);
        startForeground(NOTIFICATION_ID,mBuilder.build());
                new AndroidFFMPEGLocator(getApplicationContext());
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        String date = new SimpleDateFormat("dd-mm-yyyy_HH-mm-ss").format(new Date());
        String currFilename = date+".aac";
        String filename = getExternalFilesDir(null).getAbsolutePath() + "/" + currFilename;
        outProcessor = new PipeOutProcessor(dispatcher.getFormat(),filename);
        dispatcher.addAudioProcessor(outProcessor);
        dispatcherThread = new Thread(dispatcher,"Audio Dispatcher");
        dispatcherThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        dispatcher.stop();
        stopForeground(true);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
