package com.navigation;

import java.util.ArrayList;
import java.util.List;

public class NavigationService {
    private Location destination;
    private Location currentLocation;
    private List<String> route;
    private boolean routeActive;

    public NavigationService() {
        this.route = new ArrayList<>();
        this.routeActive = false;
    }

    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
    }

    /** US-2: Accept a destination and compute a route. */
    public boolean setDestination(Location destination) {
        if (destination == null) return false;
        this.destination = destination;
        this.route = calculateRoute(currentLocation, destination);
        this.routeActive = !route.isEmpty();
        return routeActive;
    }

    /** US-2: Accept a destination by address string. */
    public boolean setDestinationByAddress(String address) {
        if (address == null || address.trim().isEmpty()) return false;
        // In production this would geocode the address; here we simulate it.
        Location geocoded = geocode(address);
        if (geocoded == null) return false;
        return setDestination(geocoded);
    }

    private Location geocode(String address) {
        // Simulated geocoder — returns null for blank/unknown addresses
        if (address.equalsIgnoreCase("unknown address")) return null;
        return new Location(32.7157, -117.1611, address);   // default → San Diego downtown
    }

    private List<String> calculateRoute(Location from, Location to) {
        List<String> steps = new ArrayList<>();
        if (from == null || to == null) return steps;
        steps.add("Head north on Main St");
        steps.add("Turn right onto Highway 101");
        steps.add(String.format("Arrive at destination: %s",
                to.getAddress() != null ? to.getAddress() : to.toString()));
        return steps;
    }

    public Location getDestination()   { return destination; }
    public List<String> getRoute()     { return route; }
    public boolean isRouteActive()     { return routeActive; }
    public int getRouteStepCount()     { return route.size(); }

    public void clearRoute() {
        this.destination = null;
        this.route.clear();
        this.routeActive = false;
    }
}
