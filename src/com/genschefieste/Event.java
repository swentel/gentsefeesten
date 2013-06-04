package com.genschefieste;

public class Event {

    public static String eventUrl = "";
    public static String locationsUrl = "";
    public static String categoryUrl = "";

    // Private variables.
    int id;
    int external_id;
    String title;
    String price;
    String date;
    String start_hour;
    int favorite;

    // Empty constructor.
    public Event() {

    }

    // Constructor
    public Event(int id, String title, String price, String date, String start_hour, int favorite) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.date = date;
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

    // Setting title
    public void setTitle(String title) {
        this.title = title;
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
    public String getDate() {
        return this.date;
    }

    // Setting date
    public void setDate(String date) {
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
