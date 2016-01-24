package net.maarti.guessthenumber.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.preference.PreferenceManager;

/**
 * Utility class that check preferences before play sound, vibrate, ...
 * Created by Bryan MARTINET on 24/01/2016.
 */

public class MultimediaManager {

    public static final String PREF_SOUND = "pref_sound";
    public static final String PREF_VIBRATION = "pref_vibration";

    /**
     * Play a sound sounds are allowed in the preferences
     * @param Ctx   Application context
     * @param mp    MediaPlayer to play
     */
    public static void play(Context Ctx, MediaPlayer mp){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Ctx);
        if (preferences.getBoolean(PREF_SOUND,true)){
            mp.start();
        }
    }

    /**
     * Vibrate if vibrations are allowed in the preferences
     * @param Ctx   Application context
     * @param duration  Duration of the vibration in ms
     */
    public static void vibrate(Context Ctx, int duration){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Ctx);
        if (preferences.getBoolean(PREF_VIBRATION,true)) {
            Vibrator vibrator = (Vibrator) Ctx.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(duration);
        }
    }
}
