package org.treinchauffeur.roosterbuilder.obj;

public class Mentor {

    private String name = "";
    private String id = ""; //We know this is an Integer, but the number could be 012345 for instance. The '0' being problematic.
    private String NeatName = "";
    private String phoneNumber = "";
    private String email = "";

    public Mentor(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNeatName() {
        return NeatName;
    }

    public void setNeatName(String neatName) {
        NeatName = neatName;
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
}
