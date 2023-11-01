package org.treinchauffeur.roosterbuilder.obj;

import java.util.Date;
import java.util.Objects;

public class Shift {

    private String dateString;
    private Pupil pupil;
    private String extraInfo = "";
    private String modifier = "";
    private Date date;
    private String shiftNumber = "";
    private int weekDay;

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

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
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

    public String getNeatShiftNumber() {
        if(shiftNumber.endsWith("H"))
            return "Hgl "+modifier+shiftNumber.split("H")[0];
        else if(!isNonRegularShiftNumber())
            return "Es "+modifier+shiftNumber;
        else
            return modifier+shiftNumber;
    }

    private boolean isNonRegularShiftNumber() {
        switch (shiftNumber.toLowerCase()) {
            case "r":
            case "streepjesdag":
            case "vl":
            case "gvl":
            case "wa":
            case "wr":
            case "wv":
            case "co":
            case "w":
            case "curs":
                return true;
            default:
                return false;
        }
    }
}
