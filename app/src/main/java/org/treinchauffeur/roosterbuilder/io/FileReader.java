package org.treinchauffeur.roosterbuilder.io;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.treinchauffeur.roosterbuilder.MainActivity;
import org.treinchauffeur.roosterbuilder.misc.Logger;
import org.treinchauffeur.roosterbuilder.misc.Tools;
import org.treinchauffeur.roosterbuilder.obj.Mentor;
import org.treinchauffeur.roosterbuilder.obj.Pupil;
import org.treinchauffeur.roosterbuilder.obj.Shift;
import org.treinchauffeur.roosterbuilder.obj.StoredPupil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    public int weekNumber = -1, yearNumber = -1;

    public ArrayList<Uri> filesUsed = new ArrayList<>();

    private final ArrayList<String> pupilNames = new ArrayList<>();



    public FileReader(MainActivity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    public boolean startReading(Uri uri) {
        boolean success = false;
        int lines = 0;
        fileContents.clear();
        Logger.debug(TAG, "Started reading: "+uri);

        if(filesUsed.contains(uri)) {
            Toast.makeText(context, "Dit bestand is reeds ingelezen!", Toast.LENGTH_SHORT).show();
            return false;
        }

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

            filesUsed.add(uri);
            activity.selectButton.setVisibility(View.GONE);

            if(fileContents.size() > 9) { //9 is hypothetically the  minimal amount of lines
                processData();
                success = activity.pupilsMap.size() > 0;
            }
            success = true;
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
                        || formattedLine.split(" ")[3].contains("dag") || formattedLine.split(" ")[4].contains("dag")
                        || formattedLine.split(" ")[5].contains("dag")) {
                    filteredContents.add(formattedLine);
                }
            }
        }

        Pupil tempPupil = null;
        String pupilName = "";
        for(String rawLine : filteredContents) {
            String formattedLine = rawLine.replaceAll("\\s+", " ");
            //Secondary portion of the file; these shifts are special or contain extra information like a mentor etc.
            //Check whether this line defines a new person
            if(!formattedLine.split(" ")[1].contains("dag") && formattedLine.split(" ")[1].equals(formattedLine.split(" ")[1].toUpperCase())) {
                pupilName = formattedLine.split(" ")[1];

                //We're checking for infixes and/or multiple initials. If they're there; remove them
                // and reformat! They'll mess things up later if this isn't done.
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

                //This is stupid, but DiSys apparently outputs some names with commas instead of spaces..
                if(pupilName.contains(",")) {
                    pupilName = pupilName.replace(",", " ");
                    if(pupilName.endsWith(" ")) pupilName = pupilName.substring(0, pupilName.length() - 1);
                }

                tempPupil = new Pupil(pupilName);
                pupilNames.add(pupilName);
                activity.pupilsMap.put(pupilName, tempPupil);

                //Now that we have the name, let's get rid of it for now. Get rid of extra white spaces as well.
                formattedLine = formattedLine.replace(formattedLine.split(" ")[1], "");
                formattedLine = formattedLine.replaceAll("\\s+", " ");
            }

            Shift shift = new Shift();
            assert tempPupil != null;
            shift.setPupil(tempPupil);

            String shiftDateString = "";

            if(formattedLine.contains("aandag")) {
                shiftDateString = mondayDateString;
                shift.setWeekDay(Shift.MAANDAG);
            } else if(formattedLine.contains("Dinsdag")) {
                shiftDateString = tuesdayDateString;
                shift.setWeekDay(Shift.DINSDAG);
            } else if(formattedLine.contains("Woensdag")) {
                shiftDateString = wednesdayDateString;
                shift.setWeekDay(Shift.WOENSDAG);
            } else if(formattedLine.contains("Donderdag")) {
                shiftDateString = thursdayDateString;
                shift.setWeekDay(Shift.DONDERDAG);
            } else if(formattedLine.contains("Vrijdag")) {
                shiftDateString = fridayDateString;
                shift.setWeekDay(Shift.VRIJDAG);
            } else if(formattedLine.contains("Zaterdag")) {
                shiftDateString = saturdayDateString;
                shift.setWeekDay(Shift.ZATERDAG);
            } else if(formattedLine.contains("Zondag")) {
                shiftDateString = sundayDateString;
                shift.setWeekDay(Shift.ZONDAG);
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
            if(Tools.isShiftModifier(formattedLine.split(" ")[0]) && !formattedLine.split(" ")[1].contains(":")) {
                shiftModifier = formattedLine.split(" ")[0];
                formattedLine = formattedLine.substring(shiftModifier.length() + 1);
            }
            shift.setModifier(shiftModifier);

            String shiftNumber = formattedLine.split(" ")[0];
            shift.setShiftNumber(shiftNumber);

            //This person has a day off; we're done here.
            if(Tools.isRestingDay(shiftNumber)) continue;

            //We don't care about the start & end times, but we check whether they're there for proper identification.
            if(formattedLine.split(" ").length > 1) {
                if (formattedLine.split(" ")[1].contains(":") && formattedLine.split(" ").length > 3) {
                    shift.setExtraInfo(formattedLine.substring(shift.getShiftNumber().length() + 13));
                } else {
                    shift.setExtraInfo(formattedLine.substring(shift.getShiftNumber().length() + 1));
                }
            }


            for(Map.Entry<String, Pupil> set : activity.pupilsMap.entrySet()) {
                Pupil pupil = set.getValue();
                if(shift.getPupil().getName().equals(pupil.getName())) {
                    pupil.setShift(shift.getWeekDay(), shift);
                }
            }
        }

        for(String rawLine : fileContents) {
            String formattedLine = rawLine.replaceAll("\\s+", " ");

            //Primary portion of the file; every person defined has a full week of shifts data (like a shift nr, R or WV)
            Shift mondayShift, tuesdayShift, wednesdayShift, thursdayShift, fridayShift, saturdayShift, sundayShift;
            mondayShift = new Shift();
            tuesdayShift = new Shift();
            wednesdayShift = new Shift();
            thursdayShift = new Shift();
            fridayShift = new Shift();
            saturdayShift = new Shift();
            sundayShift = new Shift();

            mondayShift.setDateString(mondayDateString);
            tuesdayShift.setDateString(tuesdayDateString);
            wednesdayShift.setDateString(wednesdayDateString);
            thursdayShift.setDateString(thursdayDateString);
            fridayShift.setDateString(fridayDateString);
            saturdayShift.setDateString(saturdayDateString);
            sundayShift.setDateString(sundayDateString);

            //Check if these are the lines we need; they're all uppercase.
            //Maybe we need to deploy more checks to make sure.
            if(formattedLine.toUpperCase().equals(formattedLine)) {
                formattedLine = formattedLine.replaceAll(",", " ");
                formattedLine = formattedLine.replaceAll("\\s+", " "); //This is stupid.
                if(formattedLine.length() > 0) formattedLine = formattedLine.substring(1);

                //Add the pupils that we know from previous sessions, but don't have any shifts with special information this week
                //This is a flaw in my system, however otherwise there's no way of confidently reading those names.
                //It doesn't really matter that much anyways since all regular shifts will have a mentor specified in the 'special information' bits.
                for (Map.Entry<String, StoredPupil> set : activity.savedPupils.entrySet()) {
                    StoredPupil pupil = set.getValue();
                    if(formattedLine.startsWith(pupil.getSavedName().toUpperCase())) {
                        pupilNames.add(pupil.getSavedName());
                    }
                }

                for(String name : pupilNames) {
                    if(formattedLine.startsWith(name.toUpperCase())) {
                        Pupil pupil = new Pupil(name);
                        formattedLine = formattedLine.split(name + " ")[1];

                        if(Tools.isShiftModifier(formattedLine.split(" ")[0]) && formattedLine.split(" ").length > 6) {
                            mondayShift.setModifier(formattedLine.split(" ")[0]);
                            formattedLine = formattedLine.substring(formattedLine.split(" ")[0].length() + 1);
                        }
                        mondayShift.setWeekDay(Shift.MAANDAG);
                        mondayShift.setShiftNumber(formattedLine.split(" ")[0]);
                        formattedLine = formattedLine.substring(formattedLine.split(" ")[0].length() + 1);

                        if(Tools.isShiftModifier(formattedLine.split(" ")[0]) && formattedLine.split(" ").length > 6) {
                            tuesdayShift.setModifier(formattedLine.split(" ")[0]);
                            formattedLine = formattedLine.substring(formattedLine.split(" ")[0].length() + 1);
                        }
                        tuesdayShift.setWeekDay(Shift.DINSDAG);
                        tuesdayShift.setShiftNumber(formattedLine.split(" ")[0]);
                        formattedLine = formattedLine.substring(formattedLine.split(" ")[0].length() + 1);

                        if(Tools.isShiftModifier(formattedLine.split(" ")[0]) && formattedLine.split(" ").length > 5) {
                            wednesdayShift.setModifier(formattedLine.split(" ")[0]);
                            formattedLine = formattedLine.substring(formattedLine.split(" ")[0].length() + 1);
                        }
                        wednesdayShift.setWeekDay(Shift.WOENSDAG);
                        wednesdayShift.setShiftNumber(formattedLine.split(" ")[0]);
                        formattedLine = formattedLine.substring(formattedLine.split(" ")[0].length() + 1);

                        if(Tools.isShiftModifier(formattedLine.split(" ")[0]) && formattedLine.split(" ").length > 4) {
                            thursdayShift.setModifier(formattedLine.split(" ")[0]);
                            formattedLine = formattedLine.substring(formattedLine.split(" ")[0].length() + 1);
                        }
                        thursdayShift.setWeekDay(Shift.DONDERDAG);
                        thursdayShift.setShiftNumber(formattedLine.split(" ")[0]);
                        formattedLine = formattedLine.substring(formattedLine.split(" ")[0].length() + 1);

                        if(Tools.isShiftModifier(formattedLine.split(" ")[0]) && formattedLine.split(" ").length > 3) {
                            fridayShift.setModifier(formattedLine.split(" ")[0]);
                            formattedLine = formattedLine.substring(formattedLine.split(" ")[0].length() + 1);
                        }
                        fridayShift.setWeekDay(Shift.VRIJDAG);
                        fridayShift.setShiftNumber(formattedLine.split(" ")[0]);
                        formattedLine = formattedLine.substring(formattedLine.split(" ")[0].length() + 1);

                        if(Tools.isShiftModifier(formattedLine.split(" ")[0]) && formattedLine.split(" ").length > 2) {
                            saturdayShift.setModifier(formattedLine.split(" ")[0]);
                            formattedLine = formattedLine.substring(formattedLine.split(" ")[0].length() + 1);
                        }
                        saturdayShift.setWeekDay(Shift.ZATERDAG);
                        saturdayShift.setShiftNumber(formattedLine.split(" ")[0]);
                        formattedLine = formattedLine.substring(formattedLine.split(" ")[0].length() + 1);

                        if(Tools.isShiftModifier(formattedLine.split(" ")[0]) && formattedLine.split(" ").length > 1) {
                            sundayShift.setModifier(formattedLine.split(" ")[0]);
                            formattedLine = formattedLine.substring(formattedLine.split(" ")[0].length() + 1);
                        }
                        sundayShift.setWeekDay(Shift.ZONDAG);
                        sundayShift.setShiftNumber(formattedLine.split(" ")[0]);

                        pupil.setShift(Shift.MAANDAG, mondayShift);
                        pupil.setShift(Shift.DINSDAG, tuesdayShift);
                        pupil.setShift(Shift.WOENSDAG, wednesdayShift);
                        pupil.setShift(Shift.DONDERDAG, thursdayShift);
                        pupil.setShift(Shift.VRIJDAG, fridayShift);
                        pupil.setShift(Shift.ZATERDAG, saturdayShift);
                        pupil.setShift(Shift.ZONDAG, sundayShift);

                        //Now let's merge both parts. If we have a shift from the more detailed part,
                        // we're not gonna input these shifts if the pupil already has one on that certain day
                        if(activity.pupilsMap.containsKey(pupil.getName())) {
                            for (Map.Entry<String, Pupil> set : activity.pupilsMap.entrySet()) {
                                Pupil p = set.getValue();
                                if (pupil.getName().equals(p.getName())) {
                                    for (Shift toCompare : p.getShifts()) {
                                        if (toCompare.getShiftNumber().equals("")) {
                                            switch (toCompare.getWeekDay()) {
                                                case Shift.MAANDAG:
                                                    p.setShift(Shift.MAANDAG, mondayShift);
                                                    break;
                                                case Shift.DINSDAG:
                                                    p.setShift(Shift.DINSDAG, tuesdayShift);
                                                    break;
                                                case Shift.WOENSDAG:
                                                    p.setShift(Shift.WOENSDAG, wednesdayShift);
                                                    break;
                                                case Shift.DONDERDAG:
                                                    p.setShift(Shift.DONDERDAG, thursdayShift);
                                                    break;
                                                case Shift.VRIJDAG:
                                                    p.setShift(Shift.VRIJDAG, fridayShift);
                                                    break;
                                                case Shift.ZATERDAG:
                                                    p.setShift(Shift.ZATERDAG, saturdayShift);
                                                    break;
                                                case Shift.ZONDAG:
                                                    p.setShift(Shift.ZONDAG, sundayShift);
                                                    break;
                                            }
                                        } else {
                                            if(toCompare.getWeekDay() == Shift.MAANDAG)
                                                mondayShift.setExtraInfo(toCompare.getExtraInfo());
                                            if(toCompare.getWeekDay() == Shift.DINSDAG)
                                                tuesdayShift.setExtraInfo(toCompare.getExtraInfo());
                                            if(toCompare.getWeekDay() == Shift.WOENSDAG)
                                             wednesdayShift.setExtraInfo(toCompare.getExtraInfo());
                                            if(toCompare.getWeekDay() == Shift.DONDERDAG)
                                                thursdayShift.setExtraInfo(toCompare.getExtraInfo());
                                            if(toCompare.getWeekDay() == Shift.VRIJDAG)
                                                fridayShift.setExtraInfo(toCompare.getExtraInfo());
                                            if(toCompare.getWeekDay() == Shift.ZATERDAG)
                                                saturdayShift.setExtraInfo(toCompare.getExtraInfo());
                                            if(toCompare.getWeekDay() == Shift.ZONDAG)
                                                sundayShift.setExtraInfo(toCompare.getExtraInfo());

                                            //Not really sure what we're doing here..
                                            if(Tools.isNonRegularShiftNumber(toCompare.getShiftNumber())) continue;
                                            switch (toCompare.getWeekDay()) {
                                                case Shift.MAANDAG:
                                                    if(!toCompare.getShiftNumber().equals(mondayShift.getShiftNumber()))
                                                        p.getShift(Shift.MAANDAG).setShiftNumber(mondayShift.getShiftNumber());
                                                    break;
                                                case Shift.DINSDAG:
                                                    if(!toCompare.getShiftNumber().equals(tuesdayShift.getShiftNumber()))
                                                        p.getShift(Shift.DINSDAG).setShiftNumber(tuesdayShift.getShiftNumber());
                                                    break;
                                                case Shift.WOENSDAG:
                                                    if(!toCompare.getShiftNumber().equals(wednesdayShift.getShiftNumber()))
                                                        p.getShift(Shift.WOENSDAG).setShiftNumber(wednesdayShift.getShiftNumber());
                                                    break;
                                                case Shift.DONDERDAG:
                                                    if(!toCompare.getShiftNumber().equals(thursdayShift.getShiftNumber()))
                                                        p.getShift(Shift.DONDERDAG).setShiftNumber(thursdayShift.getShiftNumber());
                                                    break;
                                                case Shift.VRIJDAG:
                                                    if(!toCompare.getShiftNumber().equals(fridayShift.getShiftNumber()))
                                                        p.getShift(Shift.VRIJDAG).setShiftNumber(fridayShift.getShiftNumber());
                                                    break;
                                                case Shift.ZATERDAG:
                                                    if(!toCompare.getShiftNumber().equals(saturdayShift.getShiftNumber()))
                                                        p.getShift(Shift.ZATERDAG).setShiftNumber(saturdayShift.getShiftNumber());
                                                    break;
                                                case Shift.ZONDAG:
                                                    if(!toCompare.getShiftNumber().equals(sundayShift.getShiftNumber()))
                                                        p.getShift(Shift.ZONDAG).setShiftNumber(sundayShift.getShiftNumber());
                                                    break;
                                            }
                                        }
                                    }
                                }
                            }
                            activity.pupilsMap.put(pupil.getName(), pupil);
                        } else {
                            activity.pupilsMap.put(pupil.getName(), pupil);
                        }
                    }
                }
            }
        }
    }

}
