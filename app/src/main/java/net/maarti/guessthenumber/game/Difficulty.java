package net.maarti.guessthenumber.game;

import android.view.View;

import net.maarti.guessthenumber.R;

/**
 * Données relaives aux difficultés du jeu.
 * Created by Bryan MARTINET on 05/01/2016.
 */
public class Difficulty {

    // Id des difficultés
    public static final int EASY = 10;
    public static final int MEDIUM = 20;
    public static final int HARD = 30;

    // Seuils min et max des difficultés
    public static final int EASY_MIN = 0;
    public static final int EASY_MAX = 100;
    public static final int MEDIUM_MIN = 0;
    public static final int MEDIUM_MAX = 1000;
    public static final int HARD_MIN = 0;
    public static final int HARD_MAX = 10000;

    // Label de la difficulté sauvegardé dans les préférences
    public static final String DIFFICULTY_LABEL = "game_difficulty";

    // Difficulté par défault (si aucune dans les préférences)
    public static final int DIFFICULTY_DEFAULT = EASY;

    /**
     * Renvoie le seuil mini d'une difficulté
     */
    public static int getMin(int difficulty){
        switch (difficulty){
            case EASY :
                return EASY_MIN;
            case MEDIUM :
                return MEDIUM_MIN;
            case HARD :
                return HARD_MIN;
            default:
                return EASY_MIN;
        }
    }

    /**
     * Renvoie le seuil max d'une difficulté
     */
    public static int getMax(int difficulty){
        switch (difficulty){
            case EASY :
                return EASY_MAX;
            case MEDIUM :
                return MEDIUM_MAX;
            case HARD :
                return HARD_MAX;
            default:
                return EASY_MAX;
        }
    }

    /**
     * Renvoie la taille max des nombres à saisir en fonction de la difficulté
     */
    public static int getNumberLength(int difficulty){
        switch (difficulty){
            case EASY :
                return 3;
            case MEDIUM :
                return 4;
            case HARD :
                return 5;
            default :
                return 5;
        }
    }

    /**
     * Retourne la difficulté adéquate en fonction de la vue passée en paramètre
     * (view est le widget sur lequel on a cliqué dans le menu principal)
     */
    public static int getIdFromView(View view){
        switch (view.getId()){
            case R.id.buttonEasy :
                return EASY;
            case R.id.buttonMedium :
                return MEDIUM;
            case R.id.buttonHard :
                return HARD;
            default :
                return EASY;
        }
    }


}
