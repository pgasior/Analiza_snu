package pl.gasior.analizasnu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import pl.gasior.analizasnu.db.DreamListContract.DreamEntry;
import pl.gasior.analizasnu.db.DreamListContract.DreamSliceEntry;

/**
 * Created by Piotrek on 04.04.2016.
 */
public class DreamListDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "DreamsList.db";

    public DreamListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String TEXT_TYPE = " TEXT";
        final String COMMA_SEP = ",";
        final String SQL_CREATE_DREAMS_TABLE =
                "CREATE TABLE " + DreamEntry.TABLE_NAME + " (" +
                        DreamEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DreamEntry.COLUMN_NAME_AUDIO_FILENAME + TEXT_TYPE + COMMA_SEP +
                        DreamEntry.COLUMN_NAME_DATE_START + TEXT_TYPE + COMMA_SEP +
                        DreamEntry.COLUMN_NAME_DATE_END + TEXT_TYPE + COMMA_SEP +
                        DreamEntry.COLUMN_NAME_CALIBRATION_LEVEL + TEXT_TYPE + COMMA_SEP +
                        DreamEntry.COLUMN_NAME_METADATA_NAME + TEXT_TYPE + COMMA_SEP +
                        DreamEntry.COLUMN_NAME_METADATA_RATING + " REAL" + COMMA_SEP +
                        DreamEntry.COLUMN_NAME_METADATA_DESCRIPTION +
                        " )";
        db.execSQL(SQL_CREATE_DREAMS_TABLE);

        final String SQL_CREATE_SLICES_TABLE =
                "CREATE TABLE " + DreamSliceEntry.TABLE_NAME + " (" +
                        DreamSliceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DreamSliceEntry.COLUMN_DREAM_KEY + " INTEGER" + COMMA_SEP +
                        DreamSliceEntry.COLUMN_SLICE_FILENAME + TEXT_TYPE + COMMA_SEP +
                        DreamSliceEntry.COLUMN_SLICE_START + TEXT_TYPE + COMMA_SEP +
                        DreamSliceEntry.COLUMN_SLICE_END + TEXT_TYPE + COMMA_SEP +
                        DreamSliceEntry.COLUMN_USER_VERDICT + " INTEGER DEFAULT 0" + COMMA_SEP +
                        " FOREIGN KEY (" + DreamSliceEntry.COLUMN_DREAM_KEY + ") REFERENCES " +
                        DreamEntry.TABLE_NAME + " (" + DreamEntry._ID + ") ON DELETE CASCADE " +
                ")";
        db.execSQL(SQL_CREATE_SLICES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + DreamEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        final String SQL_DELETE_SLICES =
                "DROP TABLE IF EXISTS " + DreamSliceEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_SLICES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
