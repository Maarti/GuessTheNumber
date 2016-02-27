package net.maarti.guessthenumber.utility;

import com.google.android.gms.ads.AdRequest;

/**
 * Class with utilities function for the app
 * Created by Bryan MARTINET on 27/01/2016.
 */
public class Utility {

    public static final String DEVICE_ID_LGG3 = "81C55CEACBF852C2B5D230A251B889FA";
    public static final String DEVICE_ID_GALAXYS = "2611C41137470FF51DA956478F70B0B8";

    //public static final String DEVICE_ID_TABLET = "";

    /**
     * Generate the right AdRequest whether it's in debug or release
     */
    public static AdRequest buildAdRequest() {
       /* if (BuildConfig.DEBUG)
        */    return new AdRequest.Builder()
                  //  .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice(DEVICE_ID_LGG3)
                    .addTestDevice(DEVICE_ID_GALAXYS)
                    .build();
        //else
         //   return new AdRequest.Builder().build();
    }

    /**
     * Format the time string to user-friendly format if it's less than a minute
     * @param timeStr   Input time string formatted as "00:15.750"
     * @return          Output formatted time string as "15.750s"
     */
    public static String formatTime(String timeStr){
        String[] parts = timeStr.split(":|\\.");
        if (parts.length>2 && parts[0].equals("00")) {
            return String.valueOf(Integer.parseInt(parts[1])) +"."+ parts[2] + "s";
        }
        else
            return timeStr;
    }

}
