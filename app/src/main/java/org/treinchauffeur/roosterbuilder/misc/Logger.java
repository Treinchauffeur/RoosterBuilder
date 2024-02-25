package org.treinchauffeur.roosterbuilder.misc;

import android.util.Log;

public class Logger {

    /**
     * We use our own Logger for code-efficiency. It logs when either app is set to debug or
     * when a dev is using the app.
     *
     * @param TAG   TAG to pass to the main logger.
     * @param toLog message to log.
     */
    public static void debug(String TAG, String toLog) {
        if(Tools.DEBUG) Log.d(TAG, toLog);
    }
}