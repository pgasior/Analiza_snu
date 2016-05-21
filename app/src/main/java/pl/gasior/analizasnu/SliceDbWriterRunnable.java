package pl.gasior.analizasnu;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.LinkedBlockingQueue;

import pl.gasior.analizasnu.EventBusPOJO.DreamSliceEvent;
import pl.gasior.analizasnu.db.DreamListContract.DreamSliceEntry;
import pl.gasior.analizasnu.db.DreamListDbHelper;

/**
 * Created by Piotrek on 14.04.2016.
 */
public class SliceDbWriterRunnable implements Runnable{

    private final String TAG = this.getClass().getName();

    SQLiteDatabase db;
    long dreamId;
    LinkedBlockingQueue<DreamSliceEvent> queue;
    private boolean shouldRun;

    public SliceDbWriterRunnable(Context context, long id) {
        DreamListDbHelper helper = new DreamListDbHelper(context);
        db = helper.getWritableDatabase();
        queue = new LinkedBlockingQueue<>();
        this.dreamId = id;
        EventBus.getDefault().register(this);
        Log.i(TAG,"SliceDbWriterRunnable start");
        shouldRun = true;
    }

    @Subscribe
    public void addToQueue(DreamSliceEvent ev) {
        Log.i(TAG,"Otrzymalem event");
        queue.offer(ev);
    }

    @Override
    public void run() {
        DreamSliceEvent ev = new DreamSliceEvent(0,null,null,null);
        Log.i(TAG,"Otrzymalem event");
        while(shouldRun) {
            try {
                ev = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            switch(ev.getType()) {
                case 0:
                    shouldRun = false;
                    Log.i(TAG,"Otrzymalem event koniec");
                    break;
                case 1:
                    ContentValues cv = new ContentValues();
                    cv.put(DreamSliceEntry.COLUMN_DREAM_KEY, dreamId);
                    cv.put(DreamSliceEntry.COLUMN_SLICE_FILENAME, ev.getFilename());
                    cv.put(DreamSliceEntry.COLUMN_SLICE_START,ev.getStartDate());
                    cv.put(DreamSliceEntry.COLUMN_SLICE_END,ev.getEndDate());
                    db.insert(DreamSliceEntry.TABLE_NAME,null,cv);
                    Log.i(TAG,"Wpisalem event do bazy");
            }
        }
        EventBus.getDefault().unregister(this);
        db.close();

    }
}
