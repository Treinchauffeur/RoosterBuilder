package org.treinchauffeur.roosterbuilder.io;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.treinchauffeur.roosterbuilder.MainActivity;
import org.treinchauffeur.roosterbuilder.misc.Logger;
import org.treinchauffeur.roosterbuilder.obj.Pupil;
import org.treinchauffeur.roosterbuilder.obj.Shift;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileReader {

    private static ArrayList<String> fileContents = new ArrayList<>();
    private static final String TAG = "FileReader";
    public static final int REASON_FAILED_READ = 1, REASON_FAILED_PROCESS = 2;
    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    public Context context;
    public MainActivity activity;

    private String mondayDateString, tuesdayDateString, wednesdayDateString, thursdayDateString,
            fridayDateString, saturdayDateString, sundayDateString;
    private int weekNumber = -1, yearNumber = -1;

    public FileReader(MainActivity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public boolean startReading(Uri uri) {
        boolean success = false;
        int lines = 0;
        fileContents.clear();
        Logger.debug(TAG, "Started reading: "+uri);

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


            while (reader.readLine() != null) lines++;
            reader.close();
            assert inputStream != null;
            inputStream.close();
            Logger.debug(TAG, "File has amount of lines: " + lines);

            //Reopen those streams to actually assign the filecontent lines.
            inputStream = context.getContentResolver().openInputStream(uri);

            reader = new BufferedReader(new InputStreamReader(inputStream));

            for (int i = 0; i < lines; i++) {
                fileContents.add(reader.readLine());
            }

            if(fileContents.size() > 9) { //9 is hypothetically the  minimal amount of lines
                processData();
            }
        } catch (Exception e) {
            Log.e(TAG, "startReading: ", e);
            return false;
        }
        return success;
    }

    public void processData() throws ParseException {
        ArrayList<String> filteredContents = new ArrayList<>();
        for(String line : fileContents) {
            String formattedLine = line.replaceAll("\\s+", " ");

            //Defining week and year numbers.
            if(formattedLine.contains("Selectiecriteria") && weekNumber == -1) {
                weekNumber = Integer.parseInt(formattedLine.split(" ")[3].replace(":", "").substring(0, 2));
                yearNumber = Integer.parseInt(formattedLine.split(" ")[3].replace(":", "").substring(2, 6));
                Logger.debug(TAG, "week: "+weekNumber + ", year: " + yearNumber);
            }

            //Let's check out what the current dates are. We're saving these strings to be able to convert them later on.
            if(formattedLine.startsWith(" Personeelslid") && formattedLine.contains("Opmerking")) {
                mondayDateString = formattedLine.split(" ")[3];
                tuesdayDateString = formattedLine.split(" ")[5];
                wednesdayDateString = formattedLine.split(" ")[7];
                thursdayDateString = formattedLine.split(" ")[9];
                fridayDateString = formattedLine.split(" ")[11];
                saturdayDateString = formattedLine.split(" ")[13];
                sundayDateString = formattedLine.split(" ")[15];
            }

            //Let's start by filtering some of the formatting data
            if(!formattedLine.startsWith("-") && !formattedLine.startsWith("|")
                    && !formattedLine.startsWith(" Personeelslid") && !formattedLine.startsWith(" ---")
                    && !formattedLine.startsWith(" Toelichting:") && !(formattedLine.split(" ").length < 3)) {
                if(formattedLine.split(" ")[1].contains("dag") || formattedLine.split(" ")[2].contains("dag")
                        || formattedLine.split(" ")[3].contains("dag") || formattedLine.split(" ")[4].contains("dag")) {
                    filteredContents.add(formattedLine);
                }
            }
        }


        //Second, detailed portion of the file.
        Pupil pupil = null;
        String pupilName = "";
        activity.dataTextView.setText("");
        for(String rawLine : filteredContents) {
            String formattedLine = rawLine.replaceAll("\\s+", " ");

            //Check whether this line defines a new person
            if(!formattedLine.split(" ")[1].contains("dag") && formattedLine.split(" ")[1].equals(formattedLine.split(" ")[1].toUpperCase())) {
                pupilName = formattedLine.split(" ")[1];

                //We're checking for infixes and/or multiple initials. If they're there; remove them
                // and reformat! They'll mess things up later if not.
                if(!formattedLine.split(" ")[2].contains("dag")) {
                    pupilName += " " + formattedLine.split(" ")[2];
                    formattedLine = formattedLine.replace(formattedLine.split(" ")[2], "");
                    if(!formattedLine.split(" ")[3].contains("dag")) {
                        pupilName += " " + formattedLine.split(" ")[3];
                        formattedLine = formattedLine.replace(formattedLine.split(" ")[3], "");
                        if(!formattedLine.split(" ")[4].contains("dag")) {
                            pupilName += " " + formattedLine.split(" ")[4];
                            formattedLine = formattedLine.replace(formattedLine.split(" ")[4], "");
                        }
                    }
                }
                pupil = new Pupil(pupilName);

                //Now that we have the name, let's get rid of it for now. Get rid of extra white spaces as well.
                formattedLine = formattedLine.replace(formattedLine.split(" ")[1], "");
                formattedLine = formattedLine.replaceAll("\\s+", " ");
            }

            Shift shift = new Shift();
            assert pupil != null;
            shift.setPupil(pupil);

            String shiftDateString = "";

            if(formattedLine.contains("aandag")) {
                shiftDateString = mondayDateString;
            } else if(formattedLine.contains("Dinsdag")) {
                shiftDateString = tuesdayDateString;
            } else if(formattedLine.contains("Woensdag")) {
                shiftDateString = wednesdayDateString;
            } else if(formattedLine.contains("Donderdag")) {
                shiftDateString = thursdayDateString;
            } else if(formattedLine.contains("Vrijdag")) {
                shiftDateString = fridayDateString;
            } else if(formattedLine.contains("Zaterdag")) {
                shiftDateString = saturdayDateString;
            } else if(formattedLine.contains("Zondag")) {
                shiftDateString = sundayDateString;
            }

            formattedLine = formattedLine.split("dag ")[1];

            //This might get screwed up during newyear's. We're not gonna worry about that
            // right now since this won't be used in a calendar-like application.
            Date shiftDate = sdf.parse(shiftDateString + "-" + yearNumber);

            shift.setDateString(shiftDateString);
            shift.setDate(shiftDate);

            //Line layout is currently as follows:
            //(MODIFIER) SHIFTNUMBER (START:TIME) (END:TIME) (MENTOR/MISC-INFO)

            String shiftModifier = "";
            if(isShiftModifier(formattedLine.split(" ")[0])) {
                shiftModifier = formattedLine.split(" ")[0];
                formattedLine = formattedLine.split(shiftModifier + " ")[1];
            }
            shift.setModifier(shiftModifier);

            String shiftNumber = formattedLine.split(" ")[0];
            shift.setShiftNumber(shiftNumber);

            //This person has a day off; we're done here.
            if(isRestingDay(shiftNumber)) continue;

            //We don't care about the start & end times.
            if(formattedLine.split(" ")[1].contains(":")) {
                shift.setExtraInfo(formattedLine.split(formattedLine.split(" ")[2])[1]);
            } else {
                shift.setExtraInfo(formattedLine.split(formattedLine.split(" ")[0])[1]);
            }

            activity.dataTextView.append(shift.getPupil().getName() + " on : " + shift.getDateString() +
                    " has shift nr. " + shift.getModifier() + shift.getShiftNumber()  + " with extra info: " + shift.getExtraInfo() + "\n");
        }
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
    private static boolean isRestingDay(String shiftNumber) {
        switch (shiftNumber.toLowerCase()) {
            case "r":
            case "streepjesdag":
            case "vl":
            case "gvl":
            case "wa":
            case "wr":
            case "wv":
            case "co":
                return true;
            default:
                return false;
        }
    }

}
