package com.example.medhelp;

public class MedicalServices {
    private String medicalService;
    private String price;

    public MedicalServices() { }

    public MedicalServices(String medicalService, String price) {
        this.medicalService = medicalService;
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public String getMedicalService() {
        return medicalService;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setMedicalService(String medicalService) {
        this.medicalService = medicalService;
    }
}
