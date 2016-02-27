package net.maarti.guessthenumber.model;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Bryan MARTINET on 24/02/2016.
 */
public class ScoreProvider extends ContentProvider {
    private ScoreDbHelper mScoreDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int URI_ID_CREATE_SCORE = 100;                     //  content://net.maarti.simpleweather/score/
    static final int URI_ID_GET_ALL_SCORE_WITH_DIFFICULTY = 101;    //  content://net.maarti.simpleweather/score/[DIFFICULTY]

    //score.difficulty = ?
    private static final String sDifficultySelection = ScoreContract.ScoreEntry.TABLE_NAME+
            "." +ScoreContract.ScoreEntry.COLUMN_DIFFICULTY+" = ? ";



    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScoreContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ScoreContract.PATH_SCORE, URI_ID_CREATE_SCORE);
        matcher.addURI(authority, ScoreContract.PATH_SCORE + "/*", URI_ID_GET_ALL_SCORE_WITH_DIFFICULTY);

        return matcher;
    }

    private Cursor getScoreByDifficulty(Uri uri, String[] projection, String sortOrder) {
        String difficulty = ScoreContract.ScoreEntry.getDifficultyFromUri(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ScoreContract.ScoreEntry.TABLE_NAME);
        return queryBuilder.query(
                mScoreDbHelper.getReadableDatabase(),
                projection,
                //ScoreContract.ScoreEntry.TABLE_NAME+"."+ ScoreContract.ScoreEntry.COLUMN_DIFFICULTY+" = ? ",
                sDifficultySelection,
                new String[]{difficulty},
                null,
                null,
                sortOrder
                );
    }

    @Override
    public boolean onCreate() {
        mScoreDbHelper = new ScoreDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case URI_ID_GET_ALL_SCORE_WITH_DIFFICULTY:
            {
                sortOrder = ScoreContract.ScoreEntry.COLUMN_CHRONO + " ASC, "+ ScoreContract.ScoreEntry.COLUMN_TRIES+" ASC";
                retCursor =  getScoreByDifficulty(uri, projection, sortOrder);
                break;
            }
            case URI_ID_CREATE_SCORE: {
                retCursor = mScoreDbHelper.getReadableDatabase().query(
                        ScoreContract.ScoreEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int uriId = sUriMatcher.match(uri);

        switch (uriId){
            case URI_ID_CREATE_SCORE :
                return ScoreContract.ScoreEntry.CONTENT_TYPE;
            case URI_ID_GET_ALL_SCORE_WITH_DIFFICULTY :
                return ScoreContract.ScoreEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mScoreDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case URI_ID_CREATE_SCORE: {
                long _id = db.insert(ScoreContract.ScoreEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ScoreContract.ScoreEntry.buildScoreUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mScoreDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case URI_ID_CREATE_SCORE:
                rowsDeleted = db.delete(
                        ScoreContract.ScoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mScoreDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case URI_ID_CREATE_SCORE:
                rowsUpdated = db.update(ScoreContract.ScoreEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
