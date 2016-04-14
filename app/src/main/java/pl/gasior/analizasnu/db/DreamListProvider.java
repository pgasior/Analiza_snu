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
    //public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME+"/dreams");

    static final int DREAMS = 1;
    static final int DREAMS_ID = 2;
    static final int SLICES = 3;

    private static final String sDreamIdSelection =
            DreamEntry.TABLE_NAME+"."+DreamEntry._ID+" = ?";


    static final UriMatcher uriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DreamListContract.CONTENT_AUTHORITY;
        matcher.addURI(PROVIDER_NAME, DreamListContract.PATH_DREAMS, DREAMS);
        matcher.addURI(PROVIDER_NAME, DreamListContract.PATH_DREAMS + "/#", DREAMS_ID);
        matcher.addURI(PROVIDER_NAME,DreamListContract.PATH_DREAM_SLICES,SLICES);

        return matcher;
    }
//    static{
//        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//        uriMatcher.addURI(PROVIDER_NAME, "dreams", DREAMS);
//
//
//    }
    @Override
    public boolean onCreate() {
        dreamListDbHelper = new DreamListDbHelper(getContext());
        return true;
    }

    private Cursor getDreamById(
            Uri uri, String[] projection, String sortOrder) {
        long id = DreamListContract.DreamEntry.getIdFromUri(uri);
        return dreamListDbHelper.getReadableDatabase().query(
                DreamEntry.TABLE_NAME,
                projection,
                sDreamIdSelection,
                new String[]{String.valueOf(id)},
                null,
                null,
                sortOrder
        );
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
            case DREAMS_ID:
                Log.i("CP","matched dreams_id");
                retCursor = getDreamById(uri,projection,sortOrder);
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
        switch(uriMatcher.match(uri)) {
            case DREAMS:
                return DreamListContract.DreamEntry.CONTENT_TYPE;
            case DREAMS_ID:
                return DreamListContract.DreamEntry.CONTENT_ITEM_TYPE;
            case SLICES:
                return DreamListContract.DreamSliceEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
//                type =  "android.cursor.dir/"+DreamListProvider.class.getName()+DreamEntry.TABLE_NAME;
//                break;
        }
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
