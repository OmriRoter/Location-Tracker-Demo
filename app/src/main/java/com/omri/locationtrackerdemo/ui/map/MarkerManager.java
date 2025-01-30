package com.omri.locationtrackerdemo.ui.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Manages markers on the Google Map for displaying remote user locations.
 * Handles marker creation, updates, and removal.
 */
public class MarkerManager {
    private final GoogleMap googleMap;
    private Marker remoteUserMarker;

    /**
     * Creates a new MarkerManager instance
     * @param googleMap The GoogleMap instance to manage markers for
     */
    public MarkerManager(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    /**
     * Updates or creates a marker for the remote user
     * @param userId ID of the remote user
     * @param position The user's current position
     */
    public void updateRemoteUserMarker(String userId, LatLng position) {
        if (remoteUserMarker == null) {
            remoteUserMarker = googleMap.addMarker(
                    new MarkerOptions()
                            .position(position)
                            .title("User " + userId)
            );
        } else {
            remoteUserMarker.setPosition(position);
        }
    }

    /**
     * Removes the remote user's marker from the map
     */
    public void clearRemoteUserMarker() {
        if (remoteUserMarker != null) {
            remoteUserMarker.remove();
            remoteUserMarker = null;
        }
    }
}