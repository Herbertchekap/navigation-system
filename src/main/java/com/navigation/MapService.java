package com.navigation;

public class MapService {
    private Location currentLocation;
    private boolean mapLoaded;

    public MapService() {
        this.mapLoaded = false;
    }

    /** US-1: Load the map centred on the driver's current location. */
    public boolean loadMapAtCurrentLocation(Location location) {
        if (location == null) return false;
        this.currentLocation = location;
        this.mapLoaded = true;
        return true;
    }

    public Location getCurrentLocation() { return currentLocation; }
    public boolean isMapLoaded()         { return mapLoaded; }

    /** Returns true when the supplied location is within the visible map bounds. */
    public boolean isLocationVisible(Location location) {
        if (!mapLoaded || location == null) return false;
        double latDiff = Math.abs(location.getLatitude()  - currentLocation.getLatitude());
        double lonDiff = Math.abs(location.getLongitude() - currentLocation.getLongitude());
        return latDiff <= 0.1 && lonDiff <= 0.1;   // ≈ 11 km radius
    }
}

