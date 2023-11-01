package org.treinchauffeur.roosterbuilder.obj;

import java.util.ArrayList;

public class Pupil {

    private String name;
    private String[] shiftLines;
    private final ArrayList<Shift> shifts = new ArrayList<>();
    public Pupil(String name) {
        this.name = name;
        for (int i = 0; i < 7; i++) {
            shifts.add(new Shift(i));
        }
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
}
