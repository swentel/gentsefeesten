package com.genschefieste;

/**
 * Representation of an ATM object
 *
 * @author Jeppe Knockaert, Leen De Baets, Nicolas Dierck, Benjamin Mestdagh
 * (c) 2013, OKFN. All rights reserved.
 */

public class Atm {
    private String name;
    private String address;
    private double longitude;
    private double latitude;

    /**
     * Gets the name of the ATM.
     *
     * @return
     *  name of the ATM.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the ATM.
     *
     * @param name
     *  name of the ATM.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the address of the ATM (format: [Street] [Number], [Postal code] [City]).
     *
     * @return
     *  address of the ATM.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the ATM (format: [Street] [Number], [Postal code] [City]).
     *
     * @param address
     *  address of the ATM.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the longitude of the ATM.
     *
     * @return
     *  longitude of the ATM.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the ATM.
     *
     * @param longitude
     *  longitude of the ATM.
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the latitude of the ATM.
     *
     * @return
     *  latitude of the ATM.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the ATM.
     *
     * @param latitude
     *  latitude of the ATM.
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

}