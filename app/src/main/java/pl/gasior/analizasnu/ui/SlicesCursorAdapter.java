package pl.gasior.analizasnu.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.ParallelExecutorCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import pl.gasior.analizasnu.EventBusPOJO.SlicesChangedEvent;
import pl.gasior.analizasnu.R;
import pl.gasior.analizasnu.db.DreamListContract;
import pl.gasior.analizasnu.db.DreamListContract.DreamSliceEntry;
import pl.gasior.analizasnu.db.DreamListDbHelper;

/**
 * Created by Piotrek on 15.05.2016.
 */
public class SlicesCursorAdapter extends CursorAdapter {
    private final String TAG = this.getClass().getName().toString();
    private static class ViewHolder {
        TextView name;
        TextView button;

        public ViewHolder(View view) {
            name = (TextView)view.findViewById(R.id.slice_filename);
            button = (Button)view.findViewById(R.id.slice_toggle_button);
        }
    }

    public SlicesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dream_slice,parent,false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        //final int color = ((ColorDrawable)view.getBackground()).getColor();
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        final int id = cursor.getInt(cursor.getColumnIndex(DreamSliceEntry._ID));
        final int verdict = cursor.getInt(cursor.getColumnIndex(DreamSliceEntry.COLUMN_USER_VERDICT));
        updateViewColor(context, view,verdict);
        String filename = cursor.getString(cursor.getColumnIndex(DreamSliceEntry.COLUMN_SLICE_FILENAME));
        viewHolder.name.setText(filename);
        //viewHolder.button.setTag(1,verdict);
        //viewHolder.button.setTag(2,cursor.getInt(cursor.getColumnIndex(DreamSliceEntry._ID)));
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"ID: "+id+" verdict: "+verdict);
                //Log.i(TAG,"Nacisnieto: "+((Button)v).getTag(2));
                int updatedVerdict = DreamSliceEntry.VERDICT_UTOUCHED;
                if(verdict==DreamSliceEntry.VERDICT_UTOUCHED || verdict==DreamSliceEntry.VERDICT_BAD) {
                    setVerdict(context,DreamSliceEntry.VERDICT_GOOD,
                            id);
                    updatedVerdict=DreamSliceEntry.VERDICT_GOOD;
                } else {
                    setVerdict(context,DreamSliceEntry.VERDICT_BAD,
                            id);
                    updatedVerdict=DreamSliceEntry.VERDICT_BAD;
                }
                //notifyDataSetChanged();
                EventBus.getDefault().post(new SlicesChangedEvent());
                //updateViewColor(view,updatedVerdict);
            }
        });
    }

    private void updateViewColor(Context context, View view, int verdict) {
        Log.i(TAG,"verdict:"+verdict);
        if(verdict==DreamSliceEntry.VERDICT_GOOD) {
            view.setBackgroundColor(Color.GREEN);
        } else if(verdict==DreamSliceEntry.VERDICT_BAD) {
            view.setBackgroundColor(Color.RED);
        } else {
            TypedArray array = context.getTheme().obtainStyledAttributes(new int[] {
                    android.R.attr.colorBackground,
            });
            int backgroundColor = array.getColor(0, 0xFF00FF);
            view.setBackgroundColor(backgroundColor);
            array.recycle();
        }
    }

    private void setVerdict(Context context, int verdict, int id) {
        Log.i(TAG,"Zapisuje id:"+id+" verdict:"+verdict);
        DreamListDbHelper helper = new DreamListDbHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DreamSliceEntry.COLUMN_USER_VERDICT,verdict);
        db.update(DreamSliceEntry.TABLE_NAME,cv,
                DreamSliceEntry._ID+"=?",new String[]{Integer.toString(id)});
        db.close();
    }
}
