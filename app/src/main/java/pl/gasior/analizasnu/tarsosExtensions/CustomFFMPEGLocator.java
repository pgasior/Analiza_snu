package pl.gasior.analizasnu.tarsosExtensions;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by Piotrek on 04.04.2016.
 */
public class CustomFFMPEGLocator {
    private static final String TAG = "AndroidFFMPEGLocator";

    public CustomFFMPEGLocator(Context context){
        CPUArchitecture architecture = getCPUArchitecture();

        Log.i(TAG, "Detected Native CPU Architecture: " + architecture.name());

        if(!ffmpegIsCorrectlyInstalled()){
            String ffmpegFileName = getFFMPEGFileName(architecture);
            AssetManager assetManager = context.getAssets();
            unpackFFmpeg(assetManager,ffmpegFileName);
        }
        File ffmpegTargetLocation = CustomFFMPEGLocator.ffmpegTargetLocation();
        Log.i(TAG, "Ffmpeg binary location: " + ffmpegTargetLocation.getAbsolutePath() + " is executable? " + ffmpegTargetLocation.canExecute() + " size: " + ffmpegTargetLocation.length() + " bytes");
    }

    private String getFFMPEGFileName(CPUArchitecture architecture){
        final String ffmpegFileName;
        switch (architecture){
            case X86:
                ffmpegFileName = "x86_ffmpeg";
                break;
            case ARMEABI_V7A:
                ffmpegFileName = "armeabi-v7a_ffmpeg";
                break;
            case ARMEABI_V7A_NEON:
                ffmpegFileName = "armeabi-v7a-neon_ffmpeg";
                break;
            default:
                ffmpegFileName = null;
                String message= "Could not determine your processor architecture correctly, no ffmpeg binary available.";
                Log.e(TAG,message);
                throw new Error(message);
        }
        return ffmpegFileName;
    }

    private boolean ffmpegIsCorrectlyInstalled(){
        File ffmpegTargetLocation = CustomFFMPEGLocator.ffmpegTargetLocation();
        //assumed to be correct if existing and executable and larger than 1MB:
        return ffmpegTargetLocation.exists() && ffmpegTargetLocation.canExecute() && ffmpegTargetLocation.length() > 1000000;
    }

    private void unpackFFmpeg(AssetManager assetManager,String ffmpegAssetFileName) {
        InputStream inputStream=null;
        OutputStream outputStream=null;
        try{
            File ffmpegTargetLocation = CustomFFMPEGLocator.ffmpegTargetLocation();
            inputStream = assetManager.open(ffmpegAssetFileName);
            outputStream = new FileOutputStream(ffmpegTargetLocation);
            byte buffer[] = new byte[1024];
            int length = 0;
            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }
            //makes ffmpeg executable
            ffmpegTargetLocation.setExecutable(true);
            Log.i(TAG,"Unpacked ffmpeg binary " + ffmpegAssetFileName + " , extracted  " + ffmpegTargetLocation.length() + " bytes. Extracted to: " + ffmpegTargetLocation.getAbsolutePath());
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            //cleanup
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final File ffmpegTargetLocation(){
        String tempDirectory = System.getProperty("java.io.tmpdir");
        File ffmpegTargetLocation = new File(tempDirectory,"ffmpeg");
        return ffmpegTargetLocation;
    }

    private enum CPUArchitecture{
        X86,ARMEABI_V7A,ARMEABI_V7A_NEON
    }

    private CPUArchitecture getCPUArchitecture() {
        // check if device is x86

        String abi ="";
        if(Build.VERSION.SDK_INT>21) {
            abi = Build.SUPPORTED_ABIS[0];
        } else {
            abi = Build.CPU_ABI;
        }
        Log.i(TAG,"ABI: " + abi);
        if (abi.equals("x86")){
            return CPUArchitecture.X86;
        } else if (abi.equals("armeabi-v7a")) {
            // check if NEON is supported:
            if(isNeonSupported()){
                return CPUArchitecture.ARMEABI_V7A_NEON;
            }else{
                return CPUArchitecture.ARMEABI_V7A;
            }
        }
        //return null;
        return CPUArchitecture.ARMEABI_V7A;
    }

    private boolean isNeonSupported() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/proc/cpuinfo"))));
            String line = null;
            while ((line = input.readLine()) != null) {
                Log.d(TAG, "CPUINFO line: " + line);
                if(line.toLowerCase().contains("neon")) {
                    return true;
                }
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
