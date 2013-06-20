package com.genschefieste;

public class Event {

    // Private variables.
    int id;
    String title = "";
    int externalId = 0;
    int free = 0;
    String price = "";
    String pricePresale = "";
    String description = "";
    int date = 0;
    String datePeriod = "";
    String startHour = "";
    int dateSort = 0;
    String category = "";
    int categoryId = 0;
    String url = "";
    int locationId = 0;
    String location = "";
    String latitude = "";
    String longitude = "";
    String discount = "";
    int festival = 0;
    int favorite = 0;

    // Empty constructor.
    public Event() {

    }

    // Full constructor.
    public Event(int id, String title, int externalId, int free, String price, String pricePresale, String description, int date, String datePeriod, String startHour, int dateSort, String category, int categoryId, String url, int locationId, String location, String latitude, String longitude, String discount, int festival, int favorite) {
        this.id = id;
        this.title = title;
        this.externalId = externalId;
        this.free = free;
        this.price = price;
        this.pricePresale = pricePresale;
        this.description = description;
        this.date = date;
        this.datePeriod = datePeriod;
        this.startHour = startHour;
        this.dateSort = dateSort;
        this.category = category;
        this.categoryId = categoryId;
        this.url = url;
        this.locationId = locationId;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.discount = discount;
        this.festival = festival;
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getExternalId() {
        return externalId;
    }

    public void setExternalId(int externalId) {
        this.externalId = externalId;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPricePresale() {
        return pricePresale;
    }

    public void setPricePresale(String pricePresale) {
        this.pricePresale = pricePresale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getDatePeriod() {
        return datePeriod;
    }

    public void setDatePeriod(String datePeriod) {
        this.datePeriod = datePeriod;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public int getDateSort() {
        return dateSort;
    }

    public void setDateSort(int dateSort) {
        this.dateSort = dateSort;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getFestival() {
        return festival;
    }

    public void setFestival(int festival) {
        this.festival = festival;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

}
