package com.navigation;

public class Location {
    private final double latitude;
    private final double longitude;
    private String address;

    public Location(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90)
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        if (longitude < -180 || longitude > 180)
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(double latitude, double longitude, String address) {
        this(latitude, longitude);
        this.address = address;
    }

    public double getLatitude()  { return latitude; }
    public double getLongitude() { return longitude; }
    public String getAddress()   { return address; }

    @Override
    public String toString() {
        return String.format("Location{lat=%.6f, lon=%.6f, address='%s'}", latitude, longitude, address);
    }
}
