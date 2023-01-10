package com.example.medhelp;

public class DPersonalData {
    private String firstName;
    private String secondName;
    private String phone;
    private String specialization;
    private String url;

    public DPersonalData() { }

    public DPersonalData(String firstName, String secondName, String phone, String specialization, String url) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.phone = phone;
        this.specialization = specialization;
        this.url = url;
    }

    public String getFirstName() { return this.firstName; }

    public String getSecondName() { return  this.secondName; }

    public String getPhone() { return this.phone; }

    public String getSpecialization() { return this.specialization; }

    public String getUrl() { return this.url; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setSecondName(String secondName) { this.secondName = secondName; }

    public void setPhone(String phone) { this.phone = phone; }

    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public void setUrl(String url) { this.url = url; }

}
