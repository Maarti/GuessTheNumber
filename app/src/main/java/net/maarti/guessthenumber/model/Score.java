package net.maarti.guessthenumber.model;


import java.util.concurrent.TimeUnit;

public class Score {
    private long _id;
    private String username;
    private String chrono;          // temps total au format d'affichage : 00:00.000
    private int tries;
    private String difficulty;
    private long totalTime;         // temps total en millisecondes

    public Score(long _id, String username, String chrono, int tries, String difficulty) {
        this._id = _id;
        this.username = username;
        this.chrono = chrono;
        this.tries = tries;
        this.difficulty = difficulty;
    }

    public Score() {}

    public Score(String username, String chrono, int tries,String difficulty) {
        this.difficulty = difficulty;
        this.username = username;
        this.chrono = chrono;
        this.tries = tries;
    }

    public static String millisToString (long millis){
        return String.format("%02d:%02d.%03d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                (millis - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis))) );
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChrono() {
        return chrono;
    }

    public void setChrono(long millis) {
        this.totalTime = millis;
        this.chrono = Score.millisToString(millis);
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

    @Override
    public String toString() {
        return  chrono + "  (" + String.format("%02d", tries) +")  :   " + username;
    }
}
