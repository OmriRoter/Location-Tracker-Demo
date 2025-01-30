package com.omri.locationtrackerdemo.interfaces;

import android.location.Location;

/**
 * Interface for receiving updates when device location changes.
 * Implement this interface to be notified of local device location updates
 * from the LocationManager.
 */
public interface LocationUpdateListener {

    /**
     * Called when a new location update is available
     * @param location The new location data from device GPS/network providers
     */
    void onLocationUpdated(Location location);
}