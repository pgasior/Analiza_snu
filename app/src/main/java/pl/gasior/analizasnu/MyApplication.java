package pl.gasior.analizasnu;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by Piotrek on 04.04.2016.
 */
@ReportsCrashes(mailTo = "analizasnu.bledy@gmail.com")
public class MyApplication extends Application {
//    public static RefWatcher getRefWatcher(Context context) {
//        MyApplication application = (MyApplication) context.getApplicationContext();
//        return application.refWatcher;
//    }

    private RefWatcher refWatcher;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

    @Override public void onCreate() {
        super.onCreate();
        //refWatcher = LeakCanary.install(this);
    }
}
