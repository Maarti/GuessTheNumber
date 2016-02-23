package net.maarti.guessthenumber.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "guessthenumber.db";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCORE_TABLE = "CREATE TABLE " + ScoreContract.ScoreEntry.TABLE_NAME + "("
                + ScoreContract.ScoreEntry._ID + " INTEGER PRIMARY KEY,"
                + ScoreContract.ScoreEntry.COLUMN_USERNAME + " TEXT,"
                + ScoreContract.ScoreEntry.COLUMN_CHRONO + " TEXT,"
                + ScoreContract.ScoreEntry.COLUMN_TRIES + " INTEGER,"
                + ScoreContract.ScoreEntry.COLUMN_DIFFICULTY + " TEXT" + ")";
        db.execSQL(CREATE_SCORE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ScoreContract.ScoreEntry.TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ScoreContract.ScoreEntry.TABLE_NAME);
        onCreate(db);
    }

    public void addScore(Score score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScoreContract.ScoreEntry.COLUMN_USERNAME, score.getUsername());
        values.put(ScoreContract.ScoreEntry.COLUMN_CHRONO, score.getChrono());
        values.put(ScoreContract.ScoreEntry.COLUMN_TRIES, score.getTries());
        values.put(ScoreContract.ScoreEntry.COLUMN_DIFFICULTY, score.getDifficulty());

        // Inserting Row
        db.insert(ScoreContract.ScoreEntry.TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

 /*   public List<Score> getAllScores() {
        List<Score> scoreList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SCORE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                long _id = Long.parseLong(cursor.getString(0));
                String username = cursor.getString(1);
                String chrono = cursor.getString(2);
                int tries = Integer.parseInt(cursor.getString(3));
                String difficulty = cursor.getString(4);

                Score score = new Score(_id,username,chrono,tries,difficulty);
                scoreList.add(score);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return scoreList;
    }*/

    public List<Score> getTopScores(int difficulty, int limit) {
        List<Score> scoreList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + ScoreContract.ScoreEntry.TABLE_NAME + " WHERE " + ScoreContract.ScoreEntry.COLUMN_DIFFICULTY+" = "+String.valueOf(difficulty) + " ORDER BY "+ ScoreContract.ScoreEntry.COLUMN_CHRONO+" ASC LIMIT "+ String.valueOf(limit);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                long _id = Long.parseLong(cursor.getString(0));
                String username = cursor.getString(1);
                String chrono = cursor.getString(2);
                int tries = Integer.parseInt(cursor.getString(3));
                String diff = cursor.getString(4);

                Score score = new Score(_id,username,chrono,tries,diff);
                scoreList.add(score);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return scoreList;
    }
}
