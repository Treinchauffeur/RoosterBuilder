package org.treinchauffeur.roosterbuilder.misc;

public class Tools {

    /**
     * Match a number with optional '-' and decimal.
     *
     * @param str input String
     * @return is number or not
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
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
            case "r":
            case "streepjesdag":
            case "vl":
            case "gvl":
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
            case "r":
            case "streepjesdag":
            case "vl":
            case "gvl":
            case "wa":
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

    public static String camelCase(String input) {
        String output = "";
        output = input.toLowerCase();
        output = output.substring(0, 1).toUpperCase() + output.substring(1);
        return output;
    }
}
