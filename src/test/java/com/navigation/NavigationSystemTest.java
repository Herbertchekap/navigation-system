package com.navigation;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for Navigation System
 *
 * User Story 1 – Map Display:
 *   "As a driver, I need to see a basic map displaying my current location
 *    so that I know where I am at all times."
 *
 * User Story 2 – Destination Entry:
 *   "As a driver, I need to enter a destination into the navigation system
 *    so that I can receive a route to follow."
 */
@DisplayName("Navigation System – Unit Tests")
class NavigationSystemTest {

    // ─────────────────────────────────────────────────────────────────────────
    // USER STORY 1 — Map Display & Current Location
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("US-1 | Map displays driver's current location")
    class MapDisplayTests {

        private MapService mapService;

        @BeforeEach
        void setUp() {
            mapService = new MapService();
        }

        @Test
        @DisplayName("Map loads successfully with a valid GPS location")
        void testMapLoadsWithValidLocation() {
            // Arrange — driver's current GPS coordinates (San Diego, CA)
            Location driverLocation = new Location(32.7157, -117.1611);

            // Act
            boolean loaded = mapService.loadMapAtCurrentLocation(driverLocation);

            // Assert
            assertTrue(loaded,
                    "Map should load successfully when given a valid location");
            assertTrue(mapService.isMapLoaded(),
                    "MapService should report that the map is now loaded");
            assertNotNull(mapService.getCurrentLocation(),
                    "getCurrentLocation() must not return null after loading");
            assertEquals(32.7157, mapService.getCurrentLocation().getLatitude(),  1e-6,
                    "Latitude must match the driver's reported position");
            assertEquals(-117.1611, mapService.getCurrentLocation().getLongitude(), 1e-6,
                    "Longitude must match the driver's reported position");
        }

        @Test
        @DisplayName("Map is not loaded before a location is provided")
        void testMapNotLoadedInitially() {
            // Assert — map must be in an unloaded state before any location is set
            assertFalse(mapService.isMapLoaded(),
                    "Map must NOT be marked as loaded before loadMapAtCurrentLocation() is called");
            assertNull(mapService.getCurrentLocation(),
                    "getCurrentLocation() must return null when no location has been set");
        }

        @Test
        @DisplayName("Current location is visible on the loaded map")
        void testCurrentLocationIsVisibleOnMap() {
            // Arrange
            Location driverLocation = new Location(32.7157, -117.1611);
            mapService.loadMapAtCurrentLocation(driverLocation);

            // Act & Assert — the driver's own position must always appear on the map
            assertTrue(mapService.isLocationVisible(driverLocation),
                    "The driver's current location must be visible on the loaded map");
        }

        @Test
        @DisplayName("Map does not load when location is null")
        void testMapDoesNotLoadWithNullLocation() {
            // Act
            boolean loaded = mapService.loadMapAtCurrentLocation(null);

            // Assert
            assertFalse(loaded,          "loadMapAtCurrentLocation(null) must return false");
            assertFalse(mapService.isMapLoaded(), "Map must remain unloaded after a null location");
        }

        @Test
        @DisplayName("Invalid coordinates throw IllegalArgumentException")
        void testInvalidCoordinatesThrowException() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Location(200.0, -117.1611),
                    "Latitude > 90 must raise IllegalArgumentException");
            assertThrows(IllegalArgumentException.class,
                    () -> new Location(32.7157, 500.0),
                    "Longitude > 180 must raise IllegalArgumentException");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // USER STORY 2 — Destination Entry & Route Calculation
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("US-2 | Driver enters a destination and receives a route")
    class DestinationEntryTests {

        private NavigationService navService;
        private Location          startLocation;

        @BeforeEach
        void setUp() {
            navService    = new NavigationService();
            startLocation = new Location(32.7157, -117.1611, "San Diego, CA");
            navService.setCurrentLocation(startLocation);
        }

        @Test
        @DisplayName("Setting a valid destination activates navigation and produces a route")
        void testSetValidDestinationActivatesRoute() {
            // Arrange
            Location destination = new Location(34.0522, -118.2437, "Los Angeles, CA");

            // Act
            boolean routeStarted = navService.setDestination(destination);

            // Assert
            assertTrue(routeStarted,
                    "setDestination() must return true for a valid destination");
            assertTrue(navService.isRouteActive(),
                    "A route must be active after a valid destination is set");
            assertNotNull(navService.getDestination(),
                    "getDestination() must not return null after setting a destination");
            assertEquals("Los Angeles, CA", navService.getDestination().getAddress(),
                    "Destination address must match what the driver entered");
            assertFalse(navService.getRoute().isEmpty(),
                    "A non-empty list of route steps must be generated");
            assertTrue(navService.getRouteStepCount() >= 1,
                    "Route must contain at least one navigation step");
        }

        @Test
        @DisplayName("Setting a destination by address string generates a valid route")
        void testSetDestinationByAddressGeneratesRoute() {
            // Act — driver types an address into the input field
            boolean result = navService.setDestinationByAddress("Los Angeles, CA");

            // Assert
            assertTrue(result,
                    "setDestinationByAddress() must return true for a recognised address");
            assertTrue(navService.isRouteActive(),
                    "Route must become active after address-based destination entry");
            assertNotNull(navService.getRoute(),
                    "Route list must not be null");
            assertFalse(navService.getRoute().isEmpty(),
                    "Route must contain at least one step");
        }

        @Test
        @DisplayName("Setting a null destination does not activate a route")
        void testNullDestinationDoesNotActivateRoute() {
            // Act
            boolean result = navService.setDestination(null);

            // Assert
            assertFalse(result,              "setDestination(null) must return false");
            assertFalse(navService.isRouteActive(), "Route must NOT be active for a null destination");
        }

        @Test
        @DisplayName("Blank or empty address is rejected")
        void testEmptyAddressIsRejected() {
            assertFalse(navService.setDestinationByAddress(""),
                    "Empty string must be rejected");
            assertFalse(navService.setDestinationByAddress("   "),
                    "Whitespace-only string must be rejected");
            assertFalse(navService.setDestinationByAddress(null),
                    "Null address must be rejected");
        }

        @Test
        @DisplayName("Unknown address fails gracefully without activating a route")
        void testUnknownAddressFailsGracefully() {
            // Act
            boolean result = navService.setDestinationByAddress("unknown address");

            // Assert
            assertFalse(result, "Unresolvable address must return false");
            assertFalse(navService.isRouteActive(),
                    "Route must NOT be active when geocoding fails");
        }

        @Test
        @DisplayName("Clearing the route deactivates navigation")
        void testClearRouteDeactivatesNavigation() {
            // Arrange — set up an active route first
            navService.setDestination(new Location(34.0522, -118.2437, "Los Angeles, CA"));
            assertTrue(navService.isRouteActive(), "Pre-condition: route should be active");

            // Act
            navService.clearRoute();

            // Assert
            assertFalse(navService.isRouteActive(),
                    "Route must be inactive after clearRoute()");
            assertNull(navService.getDestination(),
                    "Destination must be null after clearRoute()");
            assertTrue(navService.getRoute().isEmpty(),
                    "Route step list must be empty after clearRoute()");
        }
    }
}

