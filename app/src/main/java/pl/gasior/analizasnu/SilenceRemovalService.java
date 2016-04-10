package pl.gasior.analizasnu;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.SilenceDetector;

import pl.gasior.analizasnu.tarsosExtensions.AudioDispatcherFactory;
import pl.gasior.analizasnu.tarsosExtensions.CustomFFMPEGLocator;
import pl.gasior.analizasnu.tarsosExtensions.PipeOutProcessor;
import pl.gasior.analizasnu.tarsosExtensions.SilenceRemovalFinishedReporter;
import pl.gasior.analizasnu.tarsosExtensions.TimeReporter;
import pl.gasior.analizasnu.ui.DreamDetailActivity;
import pl.gasior.analizasnu.ui.MainActivity;

public class SilenceRemovalService extends Service {

    private static final int NOTIFICATION_ID = 51;
    AudioDispatcher audioDispatcher = null;
    Thread dispatcherThread = null;
    PipeOutProcessor outProcessor;
    
    public SilenceRemovalService() {
    }
    
    private void makeForeground(String filename) {
        Intent intent  = new Intent(getApplicationContext(), DreamDetailActivity.class);
        intent.putExtra("filename", filename);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setOngoing(true)
                        .setContentTitle("Usuwanie ciszy")
                        .setSmallIcon(R.drawable.ic_hearing)
                        .setContentIntent(pi);
        startForeground(NOTIFICATION_ID,mBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String filename = intent.getStringExtra("filename");

        makeForeground(filename);
        //startTARSOSDispatcher();
        startSileneceRemoval(filename);
        return START_STICKY;
    }

    private void startSileneceRemoval(String filename) {
        new CustomFFMPEGLocator(getApplicationContext());
        audioDispatcher = AudioDispatcherFactory.fromPipe(
                getExternalFilesDir(null).getAbsolutePath() + "/" + filename,
                22050,1024,0);
        audioDispatcher.addAudioProcessor(new TimeReporter(10));
        audioDispatcher.addAudioProcessor(new SilenceDetector(-92.0,true));
        String newfn = filename.replace(".mp4",".sielnce_removed.aac");
        outProcessor = new PipeOutProcessor(audioDispatcher.getFormat(),getExternalFilesDir(null).getAbsolutePath() + "/" +newfn);
        audioDispatcher.addAudioProcessor(outProcessor);

        audioDispatcher.addAudioProcessor(new SilenceRemovalFinishedReporter());
        dispatcherThread = new Thread(audioDispatcher,"Audio Dispatcher");
        dispatcherThread.start();


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
