package org.treinchauffeur.roosterbuilder.obj;

import androidx.annotation.NonNull;

import org.treinchauffeur.roosterbuilder.MainActivity;
import org.treinchauffeur.roosterbuilder.misc.Logger;
import org.treinchauffeur.roosterbuilder.misc.Tools;

import java.util.Date;
import java.util.Objects;

public class Shift {

    public static final String TAG = "Shift";
    private String dateString = "";
    private Pupil pupil;
    private String extraInfo = "";
    private String modifier = "";
    private Date date;
    private String shiftNumber = "";
    private int weekDay = -1;
    private Mentor mentor = null;

    public static final int MAANDAG = 0, DINSDAG = 1, WOENSDAG = 2, DONDERDAG = 3, VRIJDAG = 4, ZATERDAG = 5, ZONDAG = 6;

    public Shift() {}

    public Shift(int weekDay) {
        this.weekDay = weekDay;
    }

    public String getDateString() {
        return dateString;
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

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getDate() {
        return date;
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

    public String getNeatShiftNumber() {
        if(shiftNumber.endsWith("H"))
            return "Hgl "+modifier+shiftNumber.split("H")[0];
        else if(shiftNumber.endsWith("E"))
            return "Es "+modifier+shiftNumber.substring(0, shiftNumber.length()-1);
        if(shiftNumber.toUpperCase().contains("TWO")) return shiftNumber;
        if(shiftNumber.equals("W")) return "wegleren";
        if(shiftNumber.equals("VL")) return "verlof";
        if(shiftNumber.equals("GVL")) return "verlof";
        if(shiftNumber.equals("R")) return "rustdag";
        if(shiftNumber.equals("==")) return "streepjesdag";
        if(shiftNumber.equals("CURS")) return "cursus";
        if(shiftNumber.equals("WV") || shiftNumber.equals("WA") || shiftNumber.equals("WR")) return "WTV dag";
        if(shiftNumber.toUpperCase().contains("MAT")) return "materieel";
        if(shiftNumber.toUpperCase().contains("CURS") && extraInfo.toLowerCase().contains("zelfstudie")) return "Zelfstudiedag";
        else if(!Tools.isNonRegularShiftNumber(shiftNumber))
            return "Es "+modifier+shiftNumber;
        else {
            return modifier + shiftNumber;
        }
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
            return dateString + ": dienst " + getNeatShiftNumber() + " met mentor: '" + getMentor().getName() + "'";
        else if(hasExtra())
            return dateString + ": dienst " + getNeatShiftNumber() + " met extra info: '"+extraInfo + "'";
        else
            return dateString + ": dienst " + getNeatShiftNumber();
    }
}
