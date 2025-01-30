package com.omri.locationtrackerdemo.managers;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.omri.trackinglibrary.LocationTrackerImpl;
import com.omri.trackinglibrary.interfaces.LocationCallback;
import com.omri.trackinglibrary.interfaces.UserCallback;
import com.omri.trackinglibrary.models.Location;
import com.omri.trackinglibrary.models.User;
import com.omri.locationtrackerdemo.interfaces.RemoteUserLocationListener;

/**
 * Manages tracking of remote users' locations through periodic polling.
 * Handles user status verification, location updates, and error scenarios.
 */
public class RemoteTrackingManager {
    private static final String TAG = "RemoteTrackingManager";
    private static final long POLL_INTERVAL_MS = 3000; // 3 seconds

    private final LocationTrackerImpl locationTracker;
    private final Handler handler;
    private String currentlyTrackedUserId;
    private RemoteUserLocationListener locationListener;
    private final Runnable pollRunnable;
    private boolean isFirstUpdate = true;

    /**
     * Creates a new RemoteTrackingManager instance
     * @param locationTracker The location tracking implementation to use
     */
    public RemoteTrackingManager(LocationTrackerImpl locationTracker) {
        this.locationTracker = locationTracker;
        this.handler = new Handler(Looper.getMainLooper());

        pollRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentlyTrackedUserId != null) {
                    pollRemoteUserLocation();
                }
                handler.postDelayed(this, POLL_INTERVAL_MS);
            }
        };
    }

    /**
     * Sets the listener for remote location updates
     * @param listener Callback interface for tracking events
     */
    public void setLocationListener(RemoteUserLocationListener listener) {
        this.locationListener = listener;
    }

    /**
     * Starts tracking a specific user's location
     * Verifies user status before beginning periodic location updates
     * @param userId ID of the user to track
     */
    public void startTrackingUser(String userId) {
        isFirstUpdate = true;
        locationTracker.getUserStatus(userId, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user.isActive()) {
                    currentlyTrackedUserId = userId;
                    handler.removeCallbacks(pollRunnable);
                    handler.post(pollRunnable);
                } else if (locationListener != null) {
                    locationListener.onUserInactive(userId);
                }
            }

            @Override
            public void onError(String error) {
                if (locationListener != null) {
                    locationListener.onTrackingError(error);
                }
            }
        });
    }

    /**
     * Polls for the tracked user's current location
     */
    private void pollRemoteUserLocation() {
        locationTracker.getUserLocation(currentlyTrackedUserId, new LocationCallback() {
            @Override
            public void onSuccess(Location location) {
                if (locationListener != null) {
                    locationListener.onRemoteLocationUpdated(currentlyTrackedUserId, location, isFirstUpdate);
                    isFirstUpdate = false;
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to get remote user location: " + error);
                if (locationListener != null) {
                    locationListener.onTrackingError(error);
                }
            }
        });
    }

    /**
     * Stops tracking the current user and cleans up resources
     */
    public void stopTracking() {
        currentlyTrackedUserId = null;
        handler.removeCallbacks(pollRunnable);
        isFirstUpdate = true;
    }

    /**
     * Returns the ID of the currently tracked user
     * @return User ID or null if not tracking anyone
     */
    public String getCurrentlyTrackedUserId() {
        return currentlyTrackedUserId;
    }
}