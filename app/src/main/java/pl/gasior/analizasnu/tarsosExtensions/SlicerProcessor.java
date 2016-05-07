package pl.gasior.analizasnu.tarsosExtensions;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.writer.WriterProcessor;
import pl.gasior.analizasnu.EventBusPOJO.DreamSliceEvent;
import pl.gasior.analizasnu.RecordService;

/**
 * Created by Piotrek on 11.04.2016.
 */
public class SlicerProcessor implements AudioProcessor {

    private final String TAG = this.getClass().getName();
    double threshold;
    boolean silentLast;
    String filenameBase;
    AudioDispatcher audioDispatcher;
    PipeOutProcessor pipeOutProcessor;
    WriterProcessor writerProcessor;
    int sliceId;
    String path;
    double lastSound;
    double currentTime;
    long dreamId;
    double startTimestamp;
    Calendar startCalendar;
    String newFilename;
    private double gapSize = 2.0;

    public SlicerProcessor(double threshold,String path, String filenameBase, AudioDispatcher audioDispatcher, long dreamId) {
        this.threshold = threshold;
        this.filenameBase = filenameBase;
        this.path = path;
        this.audioDispatcher = audioDispatcher;
        this.sliceId = 0;
        this.silentLast = true;
        this.lastSound = 0.0;
        this.dreamId = dreamId;
        this.startCalendar = Calendar.getInstance();

    }

    public SlicerProcessor(double threshold,String path, String filenameBase, AudioDispatcher audioDispatcher, long dreamId, Date startDate) {
        this(threshold,path, filenameBase, audioDispatcher,  dreamId);
        this.startCalendar.setTime(startDate);
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        currentTime = audioEvent.getTimeStamp();
        boolean silentNow = audioEvent.getdBSPL()< threshold;

        if(!silentNow) {
            if(silentLast) {
                Log.i(TAG,"Wykryto dzwiek, zaczynam "+ sliceId +" w "+audioEvent.getTimeStamp());
                startTimestamp = audioEvent.getTimeStamp();
                silentLast = false;
                newFilename = filenameBase.replace(RecordService.EXTENSION,"."+sliceId+".wav");
                //pipeOutProcessor = new PipeOutProcessor(audioDispatcher.getFormat(), path + newFilename);
                File plik = new File( path + newFilename);
                try {
                    writerProcessor = new WriterProcessor(audioDispatcher.getFormat(),new RandomAccessFile( plik,"rw"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                audioDispatcher.addAudioProcessor(writerProcessor);
            }
            lastSound = audioEvent.getTimeStamp();
            return true;
        } else if(silentNow && !silentLast && audioEvent.getTimeStamp()<lastSound+gapSize) {
            return true;
        } else {
            if(!silentLast) {
                Log.i(TAG, "Wykryto cisze, ucinam " + sliceId + " w " + audioEvent.getTimeStamp());
                audioDispatcher.removeAudioProcessor(writerProcessor);
                sendSlice(currentTime);
                sliceId++;

            }
            silentLast = true;
            return false;
        }


    }
    private void sendSlice(double endTimestamp) {
        Calendar sliceStartCalendar = (Calendar)startCalendar.clone();
        sliceStartCalendar.add(Calendar.SECOND,(int)startTimestamp);
        int startMilis = (int)(1000*(startTimestamp-(int)startTimestamp));
        sliceStartCalendar.add(Calendar.MILLISECOND,startMilis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormat.setCalendar(sliceStartCalendar);
        String startDateString = dateFormat.format(sliceStartCalendar.getTime());

        Calendar sliceEndCalendar = (Calendar)startCalendar.clone();
        sliceEndCalendar.add(Calendar.SECOND,(int)endTimestamp);
        int endMilis = (int)(1000*(endTimestamp-(int)endTimestamp));
        sliceStartCalendar.add(Calendar.MILLISECOND,endMilis);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormat.setCalendar(sliceEndCalendar);
        String endDateString = dateFormat.format(sliceEndCalendar.getTime());

        EventBus.getDefault().post(new DreamSliceEvent(1,newFilename,startDateString,endDateString));
        Log.i(TAG, "Wyslalem event");
    }

    @Override
    public void processingFinished() {
        if(!silentLast) {
            Log.i(TAG, "Koniec nagrywania, zamykam ostatnia probke " + sliceId + " w " + currentTime);
            sendSlice(currentTime);
        }
        EventBus.getDefault().postSticky(new DreamSliceEvent(0,null,null,null));

    }
}
