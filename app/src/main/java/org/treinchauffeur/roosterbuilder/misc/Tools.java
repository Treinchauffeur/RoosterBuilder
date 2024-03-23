package org.treinchauffeur.roosterbuilder.misc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.treinchauffeur.roosterbuilder.obj.Mentor;
import org.treinchauffeur.roosterbuilder.obj.Pupil;

import java.util.Map;

public class Tools {

    public static final boolean EASYDEBUG = false;
    public static final String dudText = "ZZ!!DUD!!ZZ";
    public static final String TAG = "Tools";

    /**
     * Match a number with optional '-' and decimal.
     *
     * @param str input String
     * @return is number or not
     * @noinspection unused
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
    public static boolean containsNumeric(String str) { return str.matches(".*\\d.*");}

    /**
     * We need to be able to deal with P-shifts. In order to do this, we need to check whether the next
     * characters in the string array is a shift number. If yes
     * @param input the string to check.
     * @return whether this is a shift or not.
     */
    public static boolean isShiftNumber(String input) {
        if (containsNumeric(input)) return true;
        if (isRestingDay(input)) return true;
        if (isNonRegularShiftNumber(input)) return true;
        return false;
    }

    /**
     * Determines whether a symbol or multiple symbols are shiftmodifiers.
     * This is necessary in order to properly read each day of the week correctly.
     *
     * @param modifier the given symbol(s).
     * @return whether it is a shift modifier or not.
     * <p>
     * Known modifiers for Regio Twente:
     * # Guaranteed
     * > Pupil
     * E Extra
     * *
     * !
     * @
     */
    public static boolean isShiftModifier(String modifier) {
        switch (modifier) {
            case "!":
            case "@":
            case ">":
            case "<":
            case "*":
            case "?":
            case "E":
            case "#":
            case "$":
            case "%":
            case "=":
            case "P":
            case "P!":
            case "P@":
            case "P>":
            case "P<":
            case "P*":
            case "P?":
            case "PE":
            case "P#":
            case "P$":
            case "P%":
            case "P=":
            case "E!":
            case "E@":
            case "E>":
            case "E<":
            case "E*":
            case "E?":
            case "E#":
            case "E$":
            case "E%":
            case "E=":
                return true;
            default:
                return false;

        }
    }

    /**
     * Determines whether a shift number (or rather a title?) is a day off, and should be listed as a shift or not.
     *
     * @param shiftNumber the given shift number to check.
     * @return whether it should be listed or not.
     */
    public static boolean isRestingDay(String shiftNumber) {
        switch (shiftNumber.toLowerCase()) {
            case "z":
            case "ba":
            case "cv":
            case "eg":
            case "kzv":
            case "lzv":
            case "r":
            case "==":
            case "vl":
            case "gvl":
            case "ot":
            case "wa":
            case "wr":
            case "wv":
            case "co":
            case "ro":
                return true;
            default:
                return false;
        }
    }

    public static boolean isNonRegularShiftNumber(String shiftNumber) {
        switch (shiftNumber.toLowerCase()) {
            case "ot":
            case "z":
            case "r":
            case "==":
            case "vl":
            case "gvl":
            case "wa":
            case "ba":
            case "eg":
            case "wr":
            case "wv":
            case "co":
            case "ro":
            case "w":
            case "curs":
            case "p":
                return true;
            default:
                return false;
        }
    }

    /**
     * Some shift information strings will contain the first shift task as the first symbol.
     * This is problematic because this way, we can't properly filter out the mentor id & name.
     * @param s the string to check. Input will only be the first word of the entire string.
     * @return whether we should filter this bit out of the shift information string.
     */
    public static boolean isShiftTask(String s) {
        switch (s.toLowerCase()) {
            case "d":
            case "p":
            case "rgvt":
            case "rgnt":
                return true;
            default:
                return false;
        }
    }

    public static String camelCase(String input) {
        String output;
        output = input.toLowerCase();
        output = output.substring(0, 1).toUpperCase() + output.substring(1);
        return output;
    }

    public static float getPageHeight(Map<String, Pupil> pupils, Map<String, Mentor> mentors) {
        float height = 250;
        int biggestSize = Math.max(pupils.size(), mentors.size());
        float rowHeight = 10;

        Logger.debug(TAG, pupils.size() + ", " + mentors.size());

        height += pupils.size() * 2 * rowHeight;
        height += biggestSize * rowHeight;
        return height;
    }

    public static boolean isJson(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            try {
                new JSONArray(json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
