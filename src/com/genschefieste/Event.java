package com.genschefieste;

public class Event {

    // Private variables.
    int id;
    int external_id;
    String title;
    String description;
    String price;
    String location;
    int date;
    String start_hour;
    int favorite;

    // Empty constructor.
    public Event() {

    }

    // Constructor
    public Event(int id, String title, String description, String price, int date, String location, String start_hour, int favorite) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.date = date;
        this.location = location;
        this.start_hour = start_hour;
        this.favorite = favorite;
    }

    // Setting ID
    public int getID() {
        return this.id;
    }

    // Setting ID
    public void setID(int id) {
        this.id = id;
    }

    // Setting External ID
    public int getExternalID() {
        return this.external_id;
    }

    // Setting ID
    public void setExternalID(int external_id) {
        this.external_id = external_id;
    }

    // Getting title.
    public String getTitle() {
        return this.title;
    }

    // Setting title.
    public void setTitle(String title) {
        this.title = title;
    }

    // Getting description.
    public String getDescription() {
        return this.description;
    }

    // Setting description.
    public void setDescription(String description) {
        this.description = description;
    }

    // Getting location name.
    public String getLocation() {
        return this.location;
    }

    // Setting location name.
    public void setLocation(String location) {
        this.location = location;
    }

    // Getting price.
    public String getPrice() {
        return this.price;
    }

    // Setting price
    public void setPrice(String price) {
        this.price = price;
    }

    // Getting date.
    public int getDate() {
        return this.date;
    }

    // Setting date
    public void setDate(int date) {
        this.date = date;
    }

    // Getting start hour.
    public String getStartHour() {
        return this.start_hour;
    }

    // Setting start hour
    public void setStartHour(String start_hour) {
        this.start_hour = start_hour;
    }

    // Getting favorite.
    public Integer getFavorite() {
        return this.favorite;
    }

    // Setting favorite
    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
}
