package com.calmatrixoffline;

public class WeightEntry {
    private int id;
    private double weight;
    private String date;
    private String timestamp;

    public WeightEntry(int id, double weight, String date, String timestamp) {
        this.id = id;
        this.weight = weight;
        this.date = date;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public double getWeight() { return weight; }
    public String getDate() { return date; }
    public String getTimestamp() { return timestamp; }
}