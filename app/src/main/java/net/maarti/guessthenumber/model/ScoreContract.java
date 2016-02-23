package net.maarti.guessthenumber.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Bryan MARTINET on 23/02/2016.
 */
public class ScoreContract {
    public static final String CONTENT_AUTHORITY = "net.maarti.guessthenumber";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SCORE = "score";


    public static final class ScoreEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCORE).build();

        public static final String CONTENT_TYPE =       ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCORE;
        public static final String CONTENT_ITEM_TYPE =  ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCORE;

        public static final String TABLE_NAME = "score";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_CHRONO = "chrono";
        public static final String COLUMN_TRIES = "tries";
        public static final String COLUMN_DIFFICULTY = "difficulty";
    }
}
