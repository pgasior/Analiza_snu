package pl.gasior.analizasnu.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Piotrek on 04.04.2016.
 */
public class DreamListContract {

    public static final String CONTENT_AUTHORITY = "pl.gasior.analizasnu";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_DREAMS = "dreams";
    public static final String PATH_DREAM_SLICES = "dream_slices";

    public DreamListContract(){ }

    public static final class DreamEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DREAMS).build();
        public static final String TABLE_NAME = "dreams";
        public static final String COLUMN_NAME_AUDIO_FILENAME = "audiofilename";
        public static final String COLUMN_NAME_SAMPLES_DB_FILENAME = "samplesdbfilename";
        public static final String COLUMN_NAME_DATE_START = "date_start";
        public static final String COLUMN_NAME_LENGTH = "length";
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DREAMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DREAMS;

        public static Uri buildDreamUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static final class DreamSliceEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DREAM_SLICES).build();
        public static final String TABLE_NAME = "dream_slices";
        public static final String COLUMN_DREAM_KEY = "dream_id";
        public static final String COLUMN_SLICE_FILENAME = "slice_filename";
        public static final String COLUMN_SLICE_START = "slice_start";
        public static final String COLUMN_SLICE_END = "slice_end";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DREAM_SLICES;

        public static Uri buildDreamSliceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }

}
