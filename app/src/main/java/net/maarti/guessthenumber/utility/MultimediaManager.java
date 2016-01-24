package net.maarti.guessthenumber.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

/**
 * Utility class that check preferences before play sound, vibrate, ...
 * Created by Bryan MARTINET on 24/01/2016.
 */

public class MultimediaManager {

    public static final String PREF_SOUND = "pref_sound";
    public static final String PREF_VIBRATION = "pref_vibration";

    public static void play(Context Ctx, MediaPlayer mp){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Ctx);
        if (preferences.getBoolean(PREF_SOUND,true)){
            mp.start();
        }
    }
}
