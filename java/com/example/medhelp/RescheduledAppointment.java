package com.example.medhelp;

public class RescheduledAppointment {
    private String oldDate;
    private String newDate;
    private String from;
    private String to;

    public RescheduledAppointment(String oldDate, String newDate, String from, String to) {
        this.oldDate = oldDate;
        this.newDate = newDate;
        this.from = from;
        this.to = to;
    }

    public void setNewDate(String newDate) {
        this.newDate = newDate;
    }

    public void setOldDate(String oldDate) {
        this.oldDate = oldDate;
    }

    public String getNewDate() {
        return newDate;
    }

    public String getOldDate() {
        return oldDate;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
