package pl.gasior.analizasnu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import pl.gasior.analizasnu.db.DreamListContract.DreamEntry;

/**
 * Created by Piotrek on 04.04.2016.
 */
public class DreamListDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DreamsList.db";

    public DreamListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String TEXT_TYPE = " TEXT";
        final String COMMA_SEP = ",";
        final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DreamEntry.TABLE_NAME + " (" +
                        DreamEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DreamEntry.COLUMN_NAME_AUDIO_FILENAME + TEXT_TYPE + COMMA_SEP +
                        DreamEntry.COLUMN_NAME_SAMPLES_DB_FILENAME + TEXT_TYPE +
                        " )";
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + DreamEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
