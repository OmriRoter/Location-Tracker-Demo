package com.omri.locationtrackerdemo.ui.map;

import android.location.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Controls the Google Maps visualization including camera movements,
 * marker management, and location display settings.
 */
public class MapViewController {
    private static final float DEFAULT_ZOOM = 15f;
    private final GoogleMap googleMap;
    private final MarkerManager markerManager;

    /**
     * Creates a new MapViewController instance
     * @param googleMap The GoogleMap instance to control
     */
    public MapViewController(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.markerManager = new MarkerManager(googleMap);
    }

    /**
     * Enables/disables the "My Location" blue dot on the map
     * @param hasPermission Whether location permission is granted
     */
    public void enableMyLocation(boolean hasPermission) {
        if (hasPermission) {
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the local user's location and centers the camera
     * @param location The new location to display
     */
    public void updateLocalLocation(Location location) {
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }
    }

    /**
     * Updates remote user's location with camera focus
     * @param userId ID of the remote user
     * @param remoteLocation The user's new location
     */
    public void updateRemoteUserLocation(String userId, com.omri.trackinglibrary.models.Location remoteLocation) {
        LatLng remoteLatLng = new LatLng(remoteLocation.getLatitude(), remoteLocation.getLongitude());
        markerManager.updateRemoteUserMarker(userId, remoteLatLng);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(remoteLatLng, DEFAULT_ZOOM));
    }

    /**
     * Updates remote user's marker without moving the camera
     * @param userId ID of the remote user
     * @param remoteLocation The user's new location
     */
    public void updateRemoteUserMarker(String userId, com.omri.trackinglibrary.models.Location remoteLocation) {
        LatLng remoteLatLng = new LatLng(remoteLocation.getLatitude(), remoteLocation.getLongitude());
        markerManager.updateRemoteUserMarker(userId, remoteLatLng);
    }

    /**
     * Removes the remote user's marker from the map
     */
    public void clearRemoteUserMarker() {
        markerManager.clearRemoteUserMarker();
    }

    /**
     * Centers the camera on a specific location
     * @param location The location to focus on
     */
    public void focusOnLocation(Location location) {
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }
    }
}