package pl.gasior.analizasnu.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import pl.gasior.analizasnu.db.DreamListContract.DreamEntry;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Piotrek on 04.04.2016.
 */
public class DreamListProvider extends ContentProvider {

    private static final String PROVIDER_NAME =  DreamListProvider.class.getName();
    private DreamListDbHelper dreamListDbHelper;
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME+"/dreams");

    static final int DREAMS = 1;
    static final int DREAMS_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "dreams", DREAMS);

    }
    @Override
    public boolean onCreate() {
        dreamListDbHelper = new DreamListDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.i("CP","query: "+uri.toString());
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        Cursor retCursor;
        qb.setTables(DreamEntry.TABLE_NAME);
        switch(uriMatcher.match(uri)) {
            case DREAMS:
                Log.i("CP","matched dreams");
                retCursor = dreamListDbHelper.getReadableDatabase().query(
                        DreamEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);

        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.i("CP","GetType: "+uri.toString());
        String type = "";
        switch(uriMatcher.match(uri)) {
            case DREAMS:
                type =  "android.cursor.dir/"+DreamListProvider.class.getName()+DreamEntry.TABLE_NAME;
                break;
        }
        return type;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
