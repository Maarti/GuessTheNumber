package net.maarti.guessthenumber.model;


public class Score {
    private long _id;
    private String username;
    private String chrono;
    private int tries;
    private String difficulty;

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

    public void setChrono(String chrono) {
        this.chrono = chrono;
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
        return  chrono + "  (" + String.valueOf(tries) +")  :   " + username;
    }
}
