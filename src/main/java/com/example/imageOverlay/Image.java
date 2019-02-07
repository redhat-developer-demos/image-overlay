package com.example.imageOverlay;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Image {
    private String imageData;
    private String imageType = "";
    private String greeting = "Having a wonderful time at";
    private String language = "en";
    private String location = "US";
    private String dateFormatString = "MMMM d, yyyy"; 

    public Image(String imageData, String imageType,
                 String greeting, String language,
                 String location, String dateFormatString) {
        this.imageData = imageData;
        this.imageType = imageType;
        this.greeting = greeting;
        this.language = language;
        this.location = location;
        this.dateFormatString = dateFormatString;
    }
    
    @JsonProperty("imageData")
    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
    
    @JsonProperty("imageType")
    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    @JsonProperty("greeting")
    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    
    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    @JsonProperty("dateFormatString")
    public String getDateFormatString() {
        return dateFormatString;
    }

    public void setDateFormatString(String dateFormatString) {
        this.dateFormatString = dateFormatString;
    }
}
