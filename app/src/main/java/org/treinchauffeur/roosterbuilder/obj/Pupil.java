package org.treinchauffeur.roosterbuilder.obj;
import org.treinchauffeur.roosterbuilder.misc.Tools;

import java.util.ArrayList;

public class Pupil {

    public static final String TAG = "Pupil";
    private String name;
    private String neatName = "";
    private String phoneNumber = "";
    private String email = "";
    private boolean toDisplay;
    private final ArrayList<Shift> shifts = new ArrayList<>();
    public Pupil(String name) {
        this.name = name;
        if(!name.equals(Tools.dudText))
            toDisplay = true;
        for (int i = 0; i < 7; i++) {
            shifts.add(new Shift(i));
        }
    }

    public void shouldDisplay(boolean toDisplay) {
        this.toDisplay = toDisplay;
    }

    public boolean getShouldDisplay() {
        return toDisplay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Shift> getShifts() {
        return shifts;
    }

    public Shift getShift(int index) {
        return shifts.get(index);
    }

    public void setShift(int index, Shift toSet) {
        shifts.set(index, toSet);
    }

    public String getNeatName() {
        if(!neatName.equals("")) return neatName;
        String pupilName = name;
        //I hate this as well, but let's make their names a lot neater (camelcase)
        if(pupilName.split(" ").length == 5)
            pupilName = Tools.camelCase(pupilName.split(" ")[0]) + " " + pupilName.split(" ")[1].toLowerCase() + " " + pupilName.split(" ")[2].toLowerCase() + " " +
                    pupilName.split(" ")[3].toLowerCase() + " " + pupilName.split(" ")[4];
        else if(pupilName.split(" ").length == 4)
            pupilName = Tools.camelCase(pupilName.split(" ")[0]) + " " + pupilName.split(" ")[1].toLowerCase() + " " +
                    pupilName.split(" ")[2].toLowerCase() + " " + pupilName.split(" ")[3];
        else if(pupilName.split(" ").length == 3)
            pupilName = Tools.camelCase(pupilName.split(" ")[0]) + " " + pupilName.split(" ")[1].toLowerCase() + " " + pupilName.split(" ")[2];
        else
            pupilName = Tools.camelCase(pupilName.split(" ")[0]) + " " + pupilName.split(" ")[1];

        return pupilName;
    }

    public void setNeatName(String neatName) {
        this.neatName = neatName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String generateApproxEmailAddress() {
        if(name.split(" ").length == 5)
            return "." + name.split(" ")[1].toLowerCase() +
                    name.split(" ")[2].toLowerCase() + name.split(" ")[3].toLowerCase() +
                    name.split(" ")[0].toLowerCase() + "@ns.nl";
        else if(name.split(" ").length == 4)
            return "." + name.split(" ")[1].toLowerCase() +
                    name.split(" ")[2].toLowerCase() +
                    name.split(" ")[0].toLowerCase() + "@ns.nl";
        else if(name.split(" ").length == 3)
            return "." + name.split(" ")[1].toLowerCase() +
                    name.split(" ")[0].toLowerCase() + "@ns.nl";
        else return "." + name.split(" ")[0].toLowerCase() + "@ns.nl";
    }

    public boolean hasWeekOff() {
        int i = 0;
        for(Shift s : shifts) {
            if(s.isRestingDay())
                i++;
        }
        return i == 7;
    }
}
