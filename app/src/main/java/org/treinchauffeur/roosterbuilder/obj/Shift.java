package org.treinchauffeur.roosterbuilder.obj;

import androidx.annotation.NonNull;

import org.treinchauffeur.roosterbuilder.MainActivity;
import org.treinchauffeur.roosterbuilder.misc.Tools;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shift {
    private String dateString = "";
    private Pupil pupil;
    private String extraInfo = "";
    private String modifier = "";
    protected Date date;
    private String shiftNumber = "";
    private int weekDay = -1;
    private Mentor mentor = null;

    public static final int MAANDAG = 0, DINSDAG = 1, WOENSDAG = 2, DONDERDAG = 3, VRIJDAG = 4, ZATERDAG = 5, ZONDAG = 6;

    public Shift() {}

    public Shift(int weekDay) {
        this.weekDay = weekDay;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public Pupil getPupil() {
        return pupil;
    }

    public void setPupil(Pupil pupil) {
        this.pupil = pupil;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getShiftNumber() {
        return shiftNumber;
    }

    public void setShiftNumber(String shiftNumber) {
        this.shiftNumber = shiftNumber;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public boolean hasExtra() {
        return !Objects.equals(extraInfo, "");
    }

    public boolean withMentor() {
        return !Objects.equals(mentor, null);
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    /**
     * Adds the extra information to a shift. Also checks whether the supplied information mentions a mentor.
     * If yes, we try to combine this information & couple a mentor to this shift.
     * @param extraInfo The raw information.
     */
    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
        if(extraInfo.split(" ").length > 1) {
            if(Tools.isShiftTask(extraInfo.split(" ")[0]))
                this.extraInfo = extraInfo.substring(this.extraInfo.split(" ")[0].length() + 1);
            try {
                int checkId = Integer.parseInt(this.extraInfo.split(" ")[0]);
                if(checkId > 10000 && checkId < 1000000) {
                    String mentorId = this.extraInfo.split(" ")[0];
                    String mentorName = this.extraInfo.replace(mentorId + " ", "");

                    Mentor mentor;
                    if(MainActivity.mentorsMap.containsKey(mentorId))
                        mentor = MainActivity.mentorsMap.get(mentorId);
                    else {
                        mentor = new Mentor(mentorId, mentorName);
                        MainActivity.mentorsMap.put(mentorId, mentor);
                    }
                    setMentor(mentor);
                }
            } catch (NumberFormatException e) {
                //The given string is not an integer, therefor this information needn't be changed.
            }
        }
    }

    /**
     * @return the shift number formatted in a way that takes in account the location, additional
     * modifiers and special shift types that can be displayed a little neater than just one or a couple of characters.
     */
    public String getNeatShiftNumber() {
        if(shiftNumber.endsWith("H"))
            return "Hgl "+modifier+shiftNumber.split("H")[0];
        else if(shiftNumber.endsWith("E"))
            return "Es "+modifier+shiftNumber.substring(0, shiftNumber.length()-1);
        if(shiftNumber.toUpperCase().contains("TWO")) return shiftNumber;
        if(shiftNumber.equalsIgnoreCase("Z")) return "-"; //Hide these
        if(shiftNumber.equalsIgnoreCase("BA")) return "-"; //Hide these
        if(shiftNumber.equalsIgnoreCase("P")) return "Praktijk";
        if(shiftNumber.equalsIgnoreCase("W")) return "Wegleren";
        if(shiftNumber.equalsIgnoreCase("VL")) return "Verlof";
        if(shiftNumber.equalsIgnoreCase("GVL")) return "Verlof";
        if(shiftNumber.equalsIgnoreCase("R")) return "Rustdag";
        if(shiftNumber.equalsIgnoreCase("==")) return "Streepjesdag";
        if(shiftNumber.equalsIgnoreCase("MONS")) return "MO";
        if(shiftNumber.equalsIgnoreCase("PONS")) return "PO";
        if(shiftNumber.equalsIgnoreCase("CURS")) return "Cursus";
        if(shiftNumber.equalsIgnoreCase("WV") || shiftNumber.equals("WA") || shiftNumber.equals("WR")) return "WTV-dag";
        if(shiftNumber.toUpperCase().contains("MAT")) return "Materieel";
        if(shiftNumber.toUpperCase().contains("CURS") && extraInfo.toLowerCase().contains("zelfstudie")) return "Zelfstudiedag";
        else if(!Tools.isNonRegularShiftNumber(shiftNumber))
            return "Es "+modifier+shiftNumber;
        else {
            return modifier + shiftNumber;
        }
    }

    /**
     * Some information could be considered confidential or just unnecessary to show to everybody.
     * @return whether to display the shift's extra information or not.
     */
    public boolean shouldNotDisplayExtraInfo() {
        if (shiftNumber.contains("50")) return true;
        if (shiftNumber.toUpperCase().contains("MONS")) return true;
        if (shiftNumber.toUpperCase().contains("PONS")) return true;

        //If extra information contains specific times.
        Pattern p = Pattern.compile(".*([01]?[0-9]|2[0-3]):[0-5][0-9].*");
        Matcher m = p.matcher(extraInfo);
        if(m.matches()) {
            return true;
        }

        //Too convoluted
        if(extraInfo.length() > 25 && !withMentor()) return true;

        return false;
    }

    public boolean isRestingDay() {
        return Tools.isRestingDay(shiftNumber);
    }

    @NonNull
    @Override
    public String toString() {
        if(isRestingDay())
            return dateString + ": geen dienst (" + shiftNumber + ")";
        if(withMentor())
            return dateString + ": dienst " + getNeatShiftNumber() + " met mentor: '" + (getMentor().getNeatName().equals("") ? getMentor().getName() : getMentor().getNeatName()) + "'";
        else if(hasExtra())
            return dateString + ": dienst " + getNeatShiftNumber() + " met extra info: '"+extraInfo + "'";
        else
            return dateString + ": dienst " + getNeatShiftNumber();
    }
}
