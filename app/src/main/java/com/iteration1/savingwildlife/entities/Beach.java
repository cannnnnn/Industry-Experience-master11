package com.iteration1.savingwildlife.entities;

import java.io.Serializable;

public class Beach implements Serializable {
    private String name;
    private String description;
    private String area;
    private String location;
    private String feature1;
    private String feature2;
    private String banner;
    private Double latitude;

    public String getEmail() {
        return email;
    }

    public Beach(String name, String description, String area, String location, String feature1, String feature2, String banner, Double latitude, Double longitude, String email) {
        this.name = name;
        this.description = description;
        this.area = area;
        this.location = location;
        this.feature1 = feature1;
        this.feature2 = feature2;
        this.banner = banner;
        this.latitude = latitude;
        this.longitude = longitude;
        this.email = email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private Double longitude;
    private String email;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFeature1() {
        return feature1;
    }

    public void setFeature1(String feature1) {
        this.feature1 = feature1;
    }

    public String getFeature2() {
        return feature2;
    }

    public void setFeature2(String feature2) {
        this.feature2 = feature2;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Beach() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }
}
