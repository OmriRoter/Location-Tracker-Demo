package com.omri.locationtrackerdemo.managers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.omri.locationtrackerdemo.interfaces.LocationUpdateListener;

/**
 * Manages device location updates using FusedLocationProviderClient.
 * Handles location permissions, update intervals, and location change notifications.
 */
public class LocationManager {
    private static final long UPDATE_INTERVAL = 3000;  // 3 seconds
    private static final long FASTEST_INTERVAL = 1500; // 1.5 seconds

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationRequest locationRequest;
    private final LocationCallback locationCallback;
    private LocationUpdateListener locationUpdateListener;
    private Location lastLocation;

    /**
     * Creates a new LocationManager instance configured for high-accuracy updates
     * @param context Application context for location services
     */
    public LocationManager(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        locationRequest = LocationRequest.create()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    lastLocation = location;
                    if (locationUpdateListener != null) {
                        locationUpdateListener.onLocationUpdated(location);
                    }
                }
            }
        };
    }

    /**
     * Sets the listener for location updates
     * @param listener Callback interface for location changes
     */
    public void setLocationUpdateListener(LocationUpdateListener listener) {
        this.locationUpdateListener = listener;
    }

    /**
     * Checks if the app has location permissions
     * @return true if location permission is granted
     */
    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Starts location updates if permissions are granted
     */
    public void startLocationUpdates() {
        if (hasLocationPermission()) {
            try {
                fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                );
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stops location updates
     */
    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    /**
     * Returns the most recent location update
     * @return The last known location, or null if no location available
     */
    public Location getLastLocation() {
        return lastLocation;
    }
}