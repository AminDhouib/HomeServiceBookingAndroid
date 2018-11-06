package com.handy.agile.agile_app;

public class Service {

    private String type;
    private float hourlyRate;

    public Service() {

    }

    public Service(String type, float hourlyRate) {
        this.type = type;
        this.hourlyRate = hourlyRate;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public float getHourlyRate() { return hourlyRate; }

    public void setHourlyRate(float hourlyRate) { this.hourlyRate = hourlyRate; }


}
