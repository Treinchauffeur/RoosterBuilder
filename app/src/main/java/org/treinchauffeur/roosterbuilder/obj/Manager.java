package org.treinchauffeur.roosterbuilder.obj;

import android.util.Log;

import org.treinchauffeur.roosterbuilder.misc.Logger;

import java.util.Objects;

public class Manager {

    private String name;
    private String[] availability = new String[7];
    public static final String TAG = "Manager";

    /**
     * A manager can also declare their availability for the current week so that pupils & mentors
     * know when they can reach them.
     * @param name Their name.
     */
    public Manager(String name) {
        this.name = name;
    }

    /**
     * Sets the availability for a certain day.
     * @param day The day of the week (0 being monday, 6 being sunday).
     * @param availability Their availability in String format, this string is printed to the final file.
     */
    public void setAvailability(int day, String availability) {
        this.availability[day] = availability;
    }

    /**
     * Gets their availability in String format, this string is printed to the final file.
     * @param day The day of the week (0 being monday, 6 being sunday)
     * @return Their availability in String format, this string is printed to the final file.
     */
    public String getAvailability(int day) {
        if(Objects.equals(availability[day], "")) return "Onbekend";
        else return availability[day];
    }

    /**
     * Does what it says on the tin; sets the manager's name.
     * @param name Their name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Does what it says on the tin; gets the manager's name.
     * @return The manager's name.
     */
    public String getName() {
        return name;
    }
}
