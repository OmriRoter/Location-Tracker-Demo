package com.omri.locationtrackerdemo.interfaces;

import com.omri.trackinglibrary.models.Location;

/**
 * Interface for handling remote user location updates and tracking events.
 * Provides callbacks for successful location updates, user inactivity,
 * and error scenarios during remote tracking.
 */
public interface RemoteUserLocationListener {

    /**
     * Called when a tracked user's location is updated
     * @param userId The ID of the tracked user
     * @param remoteLocation The user's new location
     * @param isFirstUpdate Whether this is the first location update for this user
     */
    void onRemoteLocationUpdated(String userId, Location remoteLocation, boolean isFirstUpdate);

    /**
     * Called when a tracked user becomes inactive or stops sharing location
     * @param userId The ID of the inactive user
     */
    void onUserInactive(String userId);

    /**
     * Called when an error occurs during remote user tracking
     * @param error Description of the tracking error
     */
    void onTrackingError(String error);
}