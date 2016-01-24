package net.maarti.guessthenumber.game;

import java.util.Random;

/**
 * Instancie le jeu en cours.
 * Created by Bryan MARTINET on 02/01/2016.
 */
public class Game {

    private int numberToGuess, max, nbStep = 0, nearestMin = 0, nearestMax;
    private final int min, difficulty;

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


    public int getMin() {
        return min;
    }
}
