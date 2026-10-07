package org.example.model;

public class LanguageReport {
    private String language;
    private long speakers;
    private double percentage;

    public LanguageReport(String language, long speakers, double percentage) {
        this.language = language;
        this.speakers = speakers;
        this.percentage = percentage;
    }
    public String getLanguage() {
        return language;
    }
    public long getSpeakers() {
        return speakers;
    }
    public double getPercentage() {
        return percentage;
    }
}