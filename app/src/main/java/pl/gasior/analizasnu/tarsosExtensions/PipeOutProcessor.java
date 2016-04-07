/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gasior.analizasnu.tarsosExtensions;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Piotrek
 */
public class PipeOutProcessor implements AudioProcessor {

    private final TarsosDSPAudioFormat audioFormat;
    private final String fileName;
    private final PipeEncoder pipeEncoder;
    private final OutputStream outputStream;
    private boolean writePaused;

    public PipeOutProcessor(TarsosDSPAudioFormat audioFormat, String fileName) {
        this.audioFormat = audioFormat;
        this.fileName = fileName;
        this.pipeEncoder = new PipeEncoder(this.audioFormat);
        this.outputStream = pipeEncoder.getEncodingStream(fileName, "mp4", 22050);
        this.writePaused = false;
    }
    

    @Override
    public boolean process(AudioEvent audioEvent) {
        try {
            if (!isWritePaused()) {
                outputStream.write(audioEvent.getByteBuffer());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void processingFinished() {
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(PipeOutProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public boolean isWritePaused() {
        return writePaused;
    }


    public void setWritePaused(boolean writePaused) {
        this.writePaused = writePaused;
    }

}
