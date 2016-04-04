package pl.gasior.analizasnu.db;

import android.provider.BaseColumns;

/**
 * Created by Piotrek on 04.04.2016.
 */
public class DreamListContract {
    public DreamListContract(){ }

    public static abstract class DreamEntry implements BaseColumns {
        public static final String TABLE_NAME = "dreams";
        public static final String COLUMN_NAME_AUDIO_FILENAME = "audiofilename";
        public static final String COLUMN_NAME_SAMPLES_DB_FILENAME = "samplesdbfilename";


    }

}
