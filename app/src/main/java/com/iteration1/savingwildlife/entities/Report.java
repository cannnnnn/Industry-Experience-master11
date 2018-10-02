package com.iteration1.savingwildlife.entities;

public class Report {
    private String event_type;
    private String event_date;
    private String beach_name;
    private String reference;

    public String getBeach_name() {
        return beach_name;
    }

    public void setBeach_name(String beach_name) {
        this.beach_name = beach_name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Report() {
    }

    public Report(String event_type, String event_date) {
        this.event_type = event_type;
        this.event_date = event_date;
        this.beach_name = beach_name;
        this.reference = reference;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }


}
