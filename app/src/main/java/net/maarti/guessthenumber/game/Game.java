package net.maarti.guessthenumber.game;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Instancie le jeu en cours.
 * Created by Bryan MARTINET on 02/01/2016.
 */
public class Game {

    private int numberToGuess, max, nbStep = 0, nearestMin = 0, nearestMax;
    private final int min, difficulty;
    private String totalTimeStr;
    private long totalTime;

    public Game(int difficulty) {
        this.difficulty = difficulty;
        this.min = Difficulty.getMin(difficulty);
        this.max = Difficulty.getMax(difficulty);
        this.nearestMin = this.min;
        this.nearestMax = this.max;

        // Génération du nombre aléatoire entre 1 et max (inclus)
        Random r = new Random();
        numberToGuess = r.nextInt(this.max) + 1;
    }

    /**
     * Soumet un nombre au jeu, renvoie vrai si c'est le nombre à deviner
     */
    public boolean submitNumber(int subNum){
        nbStep++;
        if (subNum<numberToGuess){
            nearestMin = (subNum > nearestMin) ? subNum : nearestMin;
        } else if (subNum>numberToGuess){
            nearestMax = (subNum < nearestMax) ? subNum : nearestMax;
        }else{
            return true;
        }
        return false;
    }

    // GETTERS
    public int getNumberToGuess() {
        return numberToGuess;
    }

    public int getMax() {
        return max;
    }

    public int getNbStep() {
        return nbStep;
    }

    public int getNearestMin() {
        return nearestMin;
    }

    public int getNearestMax() {
        return nearestMax;
    }

    /**
     * Pass the total time in milliseconds in paramter, it will format it to String, set the totalTime of the game and return the String formatted totalTime
     * @param millis    The total time of the game in milliseconds
     * @return          The total time of the game formatted as mm:ss.xxx
     */
    public String setTotalTime(long millis){
        totalTimeStr = String.format("%02d:%02d.%03d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                (millis - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis))) );

        totalTime = millis;
        return totalTimeStr;
    }

    public long getTotalTime(){
        return totalTime;
    }

    public int getMin() {
        return min;
    }
}
