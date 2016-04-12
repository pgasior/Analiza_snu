package pl.gasior.analizasnu.tarsosExtensions;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.writer.WriterProcessor;

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

    public SlicerProcessor(double threshold,String path, String filenameBase, AudioDispatcher audioDispatcher) {
        this.threshold = threshold;
        this.filenameBase = filenameBase;
        this.path = path;
        this.audioDispatcher = audioDispatcher;
        this.sliceId = 0;
        this.silentLast = true;
        this.lastSound = 0.0;

    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        boolean silentNow = audioEvent.getdBSPL()< threshold;

        if(!silentNow) {
            if(silentLast) {
                Log.i(TAG,"Wykryto dzwiek, zaczynam "+ sliceId +" w "+audioEvent.getTimeStamp());
                silentLast = false;
                String newFilename = filenameBase.replace(".mp4","."+sliceId+".wav");
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
        } else if(silentNow && !silentLast && audioEvent.getTimeStamp()<lastSound+0.5) {
            return true;
        } else {
            if(!silentLast) {
                Log.i(TAG, "Wykryto cisze, ucinam " + sliceId + " w " + audioEvent.getTimeStamp());
                audioDispatcher.removeAudioProcessor(writerProcessor);
                sliceId++;

            }
            silentLast = true;
            return false;
        }

//        if(silentNow && audioEvent.getTimeStamp()>lastSound+0.5 && !silentLast) { //cisza
////            if(!silentLast/*!silent &&*/ /*audioEvent.getTimeStamp()>lastSound+0.5*/) { //&& pipeOutProcessor!=null) { //wczesniej byl dzwiek
//                //pipeOutProcessor.setWritePaused(true);
//
//                //audioDispatcher.removeAudioProcessor(writerProcessor);
//                //audioDispatcher.removeAudioProcessor(pipeOutProcessor);
//                Log.i(TAG,"Wykryto cisze, ucinam " + sliceId +" w "+audioEvent.getTimeStamp());
//                //silent = true;
//                sliceId++;
//                //lastSound = audioEvent.getTimeStamp();
//            silentLast = true;
//
////            } //else zignoruj
//            return false;
//        } else { //dzwiek
//            if(silentLast) { //wczesniej byla cisza
//                String newFilename = filenameBase.replace(".mp4","."+sliceId+".wav");
//                //pipeOutProcessor = new PipeOutProcessor(audioDispatcher.getFormat(), path + newFilename);
//                //File plik = new File( path + newFilename);
////                try {
////                    writerProcessor = new WriterProcessor(audioDispatcher.getFormat(),new RandomAccessFile( plik,"rw"));
////                } catch (FileNotFoundException e) {
////                    e.printStackTrace();
////                }
////                audioDispatcher.addAudioProcessor(writerProcessor);
//                //audioDispatcher.addAudioProcessor(pipeOutProcessor);
//
//                Log.i(TAG,"Wykryto dzwiek, zaczynam "+ sliceId +" w "+audioEvent.getTimeStamp());
////                silent = false;
//
//                silentLast = false;
//
//            } //else zapisz do pliku
//            if(!silentNow) {
//                lastSound = audioEvent.getTimeStamp();
//            }
//            //Log.i(TAG,"Zapis ");
//        }
//        return true;
    }

    @Override
    public void processingFinished() {

    }
}
