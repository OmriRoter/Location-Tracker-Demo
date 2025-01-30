package com.omri.locationtrackerdemo.data.repository;

import com.omri.trackinglibrary.LocationTrackerImpl;
import com.omri.trackinglibrary.interfaces.LocationCallback;
import com.omri.trackinglibrary.interfaces.UserCallback;

/**
 * Repository that handles all location tracking and user management operations.
 * Provides a clean interface to the LocationTracker library and centralizes
 * all remote data operations.
 */
public class LocationRepository {
    private final LocationTrackerImpl locationTracker;

    public LocationRepository() {
        this.locationTracker = new LocationTrackerImpl();
    }

    /**
     * Updates user's location on the remote server
     * @param userId User identifier
     * @param latitude Location latitude
     * @param longitude Location longitude
     * @param callback Callback for the operation result
     */
    public void updateLocation(String userId, double latitude, double longitude, LocationCallback callback) {
        locationTracker.updateLocation(userId, latitude, longitude, callback);
    }

    /**
     * Updates user's active status
     * @param userId User identifier
     * @param isActive Whether the user is actively sharing location
     * @param callback Callback for the operation result
     */
    public void updateUserStatus(String userId, boolean isActive, UserCallback callback) {
        locationTracker.updateUserStatus(userId, isActive, callback);
    }

    /**
     * Retrieves user's current status from the server
     */
    public void getUserStatus(String userId, UserCallback callback) {
        locationTracker.getUserStatus(userId, callback);
    }

    /**
     * Verifies user existence and authentication
     */
    public void verifyUser(String userId, UserCallback callback) {
        locationTracker.verifyUser(userId, callback);
    }

    /**
     * Creates new user with the given username
     */
    public void createUser(String username, UserCallback callback) {
        locationTracker.createUser(username, callback);
    }

    /**
     * Provides direct access to the underlying LocationTracker implementation
     * for advanced use cases
     */
    public LocationTrackerImpl getLocationTracker() {
        return locationTracker;
    }
}