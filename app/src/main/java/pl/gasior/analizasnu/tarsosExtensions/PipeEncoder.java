/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gasior.analizasnu.tarsosExtensions;

import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioFormat.Encoding;
import be.tarsos.dsp.util.FFMPEGDownloader;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 *
 * @author Piotrek
 */
public class PipeEncoder {

    private final static Logger LOG = Logger.getLogger(PipeEncoder.class.getName());
    private final String pipeEnvironment;
    private final String pipeArgument;
    private final String pipeCommand;
    private final int pipeBuffer;
    private final TarsosDSPAudioFormat audioFormat;
    
    private String decoderBinaryAbsolutePath;

    public PipeEncoder(TarsosDSPAudioFormat audioFormat) {
        pipeBuffer = 10000;
        this.audioFormat = audioFormat;
        //Use sensible defaults depending on the platform
        if (System.getProperty("os.name").indexOf("indows") > 0) {
            pipeEnvironment = "cmd.exe";
            pipeArgument = "/C";
        } else if (new File("/bin/bash").exists()) {
            pipeEnvironment = "/bin/bash";
            pipeArgument = "-c";
        } else if (new File("/system/bin/sh").exists()) {
            //probably we are on android here
            pipeEnvironment = "/system/bin/sh";
            pipeArgument = "-c";
        } else {
            LOG.severe("Coud not find a command line environment (cmd.exe or /bin/bash)");
            throw new Error("Decoding via a pipe will not work: Coud not find a command line environment (cmd.exe or /bin/bash)");
        }

        String path = System.getenv("PATH");
        String arguments = this.buildArguments(this.audioFormat);
        //String arguments = " -f s16le -ar 22050 -ac 1 -i pipe:0 -vn -ar %sample_rate% -ac %channels% -c:a aac \"%resource%\"";
        if (isAvailable("ffmpeg")) {
            LOG.info("found ffmpeg on the path (" + path + "). Will use ffmpeg for decoding media files.");
            pipeCommand = "ffmpeg" + arguments;
        } else if (isAvailable("avconv")) {
            LOG.info("found avconv on your path(" + path + "). Will use avconv for decoding media files.");
            pipeCommand = "avconv" + arguments;
        } else {
            if (isAndroid()) {
                String tempDirectory = System.getProperty("java.io.tmpdir");
                //printErrorstream = true;
                File f = new File(tempDirectory, "ffmpeg");
                if (f.exists() && f.length() > 1000000 && f.canExecute()) {
                    decoderBinaryAbsolutePath = f.getAbsolutePath();
                } else {
                    LOG.severe("Could not find an ffmpeg binary for your Android system. Did you forget calling: 'new AndroidFFMPEGLocator(this);' ?");
                    LOG.severe("Tried to unpack a statically compiled ffmpeg binary for your architecture to: " + f.getAbsolutePath());
                }
            } else {
                LOG.warning("Dit not find ffmpeg or avconv on your path(" + path + "), will try to download it automatically.");
                FFMPEGDownloader downloader = new FFMPEGDownloader();
                decoderBinaryAbsolutePath = downloader.ffmpegBinary();
                if (decoderBinaryAbsolutePath == null) {
                    LOG.severe("Could not download an ffmpeg binary automatically for your system.");
                }
            }
            if (decoderBinaryAbsolutePath == null) {
                pipeCommand = "false";
                throw new Error("Decoding via a pipe will not work: Could not find an ffmpeg binary for your system");
            } else {
                pipeCommand = '"' + decoderBinaryAbsolutePath + '"' + arguments;
            }
        }
    }

    private boolean isAvailable(String command) {
        try {
            Runtime.getRuntime().exec(command + " -version");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isAndroid() {
        try {
            // This class is only available on android
            Class.forName("android.app.Activity");
            System.out.println("Running on Android!");
            return true;
        } catch (ClassNotFoundException e) {
            //the class is not found when running JVM
            return false;
        }
    }
    
    public OutputStream getEncodingStream(final String resource, final String targetCodec, final int targetSampleRate) {
		
		try {
			String command = pipeCommand;
			command = command.replace("%resource%", resource);
			command = command.replace("%sample_rate%", String.valueOf(targetSampleRate));
			command = command.replace("%channels%","1");
                        command = command.replace("%output_codec%", targetCodec);
                        LOG.info("Command: " + command);
			
			ProcessBuilder pb;
			pb= new ProcessBuilder(pipeEnvironment, pipeArgument , command);

			LOG.info("Starting piped encoding process for " + resource);
			final Process process = pb.start();
			
			final OutputStream stdIn = new BufferedOutputStream(process.getOutputStream(), pipeBuffer){
				@Override
				public void close() throws IOException{
					super.close();
					// try to destroy the ffmpeg command after close
					process.destroy();
				}
			};

            new ErrorStreamGobbler(process.getErrorStream(),LOG, false).start();
			
			//print std error if requested

			
			new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						process.waitFor();
						LOG.info("Finished piped encoding process");
					} catch (InterruptedException e) {
						LOG.severe("Interrupted while waiting for encoding sub process exit.");
						e.printStackTrace();
					}
				}},"Encoding Pipe").start();
			return stdIn;
		} catch (IOException e) {
			LOG.warning("IO exception while encoding audio via sub process." + e.getMessage() );
			e.printStackTrace();
		}
		return null;
	}

    private String buildArguments(TarsosDSPAudioFormat audioFormat) {
        StringBuilder sb = new StringBuilder();
        //" -f s16le -ar 22050 -ac 1 -i pipe:0 -vn -ar %sample_rate% -ac %channels% -c:a aac \"%resource%\""
        sb.append(" -f ");
        if(audioFormat.getEncoding().equals(Encoding.PCM_SIGNED)) {
            sb.append("s");
        } else if(audioFormat.getEncoding().equals(Encoding.PCM_UNSIGNED)) {
            sb.append("u");
        }
        sb.append(audioFormat.getSampleSizeInBits());
        if(audioFormat.isBigEndian()) {
            sb.append("be");
        } else {
            sb.append("le");
        }
        sb.append(" -ar ");
        sb.append(Math.floor(audioFormat.getSampleRate()));
        sb.append(" -ac ");
        sb.append(audioFormat.getChannels());
        sb.append(" -i pipe:0 -vn -ar %sample_rate% -ac %channels% -c:a %output_codec% -strict -2 \"%resource%\"");
        return sb.toString();    
    }

    private class ErrorStreamGobbler extends Thread {
        private final InputStream is;
        private final Logger logger;
        private boolean outputToLogcat;

        private ErrorStreamGobbler(InputStream is, Logger logger, boolean outputToLogcat) {
            this.is = is;
            this.logger = logger;
            this.outputToLogcat = outputToLogcat;
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                //isr.
                while ((line = br.readLine()) != null) {
                    if(outputToLogcat) {
                        logger.info(line);
                    }
                }
            }
            catch (IOException ioe) {
                //ioe.printStackTrace();
                LOG.info("Stream closed");
            }
        }
    }

}
