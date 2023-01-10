package com.example.medhelp;

public class Appointments {

    private String date;
    private String medicalService;
    private String username;

    public Appointments(String date, String medicalService, String username) {
        this.date = date;
        this.medicalService = medicalService;
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMedicalService() {
        return medicalService;
    }

    public void setMedicalService(String medicalService) {
        this.medicalService = medicalService;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
