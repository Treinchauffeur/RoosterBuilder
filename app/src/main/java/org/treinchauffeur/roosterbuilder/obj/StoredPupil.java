package org.treinchauffeur.roosterbuilder.obj;

public class StoredPupil {
    private String savedName = "";
    private String neatName = "";
    private String email = "";
    private String phone = "";

    public StoredPupil(String savedName, String email, String phone) {
        this.savedName = savedName;
        this.email = email;
        this.phone = phone;
    }

    public String getSavedName() {
        return savedName;
    }

    public void setSavedName(String savedName) {
        this.savedName = savedName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNeatName() {
        return neatName;
    }

    public void setNeatName(String neatName) {
        this.neatName = neatName;
    }
}
