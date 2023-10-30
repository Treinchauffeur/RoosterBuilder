package org.treinchauffeur.roosterbuilder.obj;

public class Pupil {

    public String name;
    public String[] shiftLines;
    public Pupil(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
