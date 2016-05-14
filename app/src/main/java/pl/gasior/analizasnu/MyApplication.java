package pl.gasior.analizasnu;

import android.app.Application;
import android.content.Context;


import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Piotrek on 04.04.2016.
 */
@ReportsCrashes(mailTo = "analizasnu.bledy@gmail.com")
public class MyApplication extends Application {



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

    @Override public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
