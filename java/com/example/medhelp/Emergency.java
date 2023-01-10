package com.example.medhelp;

public class Emergency {
    private String latitude;
    private String longitude;
    private String address;
    private String url;
    private String description;
    private String username;

    public Emergency() {}

    public Emergency(String latitude, String longitude, String address, String url, String description, String username) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.url = url;
        this.description = description;
        this.username = username;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
