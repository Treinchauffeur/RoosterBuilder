package org.treinchauffeur.roosterbuilder;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class ScheduleApplication extends Application {

    /**
     * We've enabled dynamic colouring used by Material You :D
     */
    @Override
    public void onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this);
        super.onCreate();
    }
}
