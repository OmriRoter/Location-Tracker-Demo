package com.omri.locationtrackerdemo.activities;

import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.omri.locationtrackerdemo.R;
import com.omri.locationtrackerdemo.data.preferences.UserPreferences;
import com.omri.locationtrackerdemo.data.repository.LocationRepository;
import com.omri.locationtrackerdemo.interfaces.LocationUpdateListener;
import com.omri.locationtrackerdemo.interfaces.RemoteUserLocationListener;
import com.omri.locationtrackerdemo.managers.LocationManager;
import com.omri.locationtrackerdemo.managers.RemoteTrackingManager;
import com.omri.locationtrackerdemo.ui.map.MapViewController;
import com.omri.locationtrackerdemo.utils.TimeFormatter;
import com.omri.trackinglibrary.interfaces.LocationCallback;
import com.omri.trackinglibrary.interfaces.UserCallback;
import com.omri.trackinglibrary.models.User;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * MapActivity is responsible for displaying a map view and handling
 * location tracking for both the local user and remote users.
 * It integrates with location managers and repositories to update
 * and retrieve location data, and manages UI elements for user interaction.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationUpdateListener, RemoteUserLocationListener {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Switch for enabling or disabling location sharing.
     */
    private SwitchMaterial locationSharingSwitch;

    /**
     * TextView that shows the current tracking status ("Sharing Location" or "Not Sharing Location").
     */
    private TextView trackingStatus;

    /**
     * TextView that displays the user's current latitude.
     */
    private TextView latitudeText;

    /**
     * TextView that displays the user's current longitude.
     */
    private TextView longitudeText;

    /**
     * Chip that displays the last update time of the location.
     */
    private Chip updateTimeChip;

    /**
     * Floating Action Button to refocus the map on the user's current location.
     */
    private ExtendedFloatingActionButton myLocationFab;

    /**
     * TextInputLayout for user ID search input.
     */
    private TextInputLayout searchUserIdInputLayout;

    /**
     * TextInputEditText where the user can type the user ID to be tracked.
     */
    private TextInputEditText searchUserIdEditText;

    /**
     * Button that triggers the search and remote tracking of another user.
     */
    private Button searchUserButton;

    /**
     * Manages the local user's location updates.
     */
    private LocationManager locationManager;

    /**
     * Manages remote tracking operations for other users.
     */
    private RemoteTrackingManager remoteTrackingManager;

    /**
     * Repository for handling location data (e.g., database or network).
     */
    private LocationRepository locationRepository;

    /**
     * Handles user preferences, including storing and retrieving the local user's ID.
     */
    private UserPreferences userPreferences;

    /**
     * Controls the Google Map, including markers and camera positioning.
     */
    private MapViewController mapViewController;

    /**
     * Called when the activity is created. Responsible for setting
     * the content view and initializing important components.
     *
     * @param savedInstanceState A Bundle object containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initializeComponents();
        validateUserLogin();
        initializeViews();
        setupMap();
        checkAndUpdateSharingStatus();
    }

    /**
     * Initializes core components such as the LocationRepository,
     * UserPreferences, LocationManager, and RemoteTrackingManager.
     */
    private void initializeComponents() {
        locationRepository = new LocationRepository();
        userPreferences = new UserPreferences(this);
        locationManager = new LocationManager(this);
        remoteTrackingManager = new RemoteTrackingManager(locationRepository.getLocationTracker());

        locationManager.setLocationUpdateListener(this);
        remoteTrackingManager.setLocationListener(this);
    }

    /**
     * Validates whether the user is logged in by checking the stored user ID.
     * If not found, it prompts the user to login and closes the activity.
     */
    private void validateUserLogin() {
        if (userPreferences.getUserId() == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Initializes all the UI components from the layout and sets up
     * relevant click or change listeners.
     */
    private void initializeViews() {
        locationSharingSwitch = findViewById(R.id.locationSharingSwitch);
        trackingStatus = findViewById(R.id.trackingStatus);
        latitudeText = findViewById(R.id.latitudeText);
        longitudeText = findViewById(R.id.longitudeText);
        updateTimeChip = findViewById(R.id.updateTimeChip);
        myLocationFab = findViewById(R.id.myLocationFab);
        searchUserIdInputLayout = findViewById(R.id.searchUserIdInputLayout);
        searchUserIdEditText = findViewById(R.id.searchUserIdEditText);
        searchUserButton = findViewById(R.id.searchUserButton);

        setupViewListeners();
    }

    /**
     * Sets up listeners for user interaction with UI components,
     * such as toggling location sharing or searching for a user ID.
     */
    private void setupViewListeners() {
        locationSharingSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                updateLocationSharing(isChecked));

        myLocationFab.setOnClickListener(v -> focusOnMyLocation());

        searchUserButton.setOnClickListener(v -> {
            String userIdToSearch = Objects.requireNonNull(searchUserIdEditText.getText()).toString().trim();
            searchUserIdInputLayout.setError(null);

            if (userIdToSearch.isEmpty()) {
                searchUserIdInputLayout.setError("Please enter user ID");
                return;
            }
            remoteTrackingManager.startTrackingUser(userIdToSearch);
        });
    }

    /**
     * Initializes the map fragment and sets up the async callback
     * to be notified when the map is ready.
     */
    private void setupMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Callback invoked when the map is ready to be used.
     *
     * @param googleMap Instance of the GoogleMap that is now ready for manipulation.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapViewController = new MapViewController(googleMap);
        if (locationManager.hasLocationPermission()) {
            mapViewController.enableMyLocation(true);
        }
    }

    /**
     * Updates the user's location sharing status in the repository
     * and starts/stops location updates accordingly.
     *
     * @param isEnabled True if location sharing should be enabled, false otherwise.
     */
    private void updateLocationSharing(boolean isEnabled) {
        String userId = userPreferences.getUserId();
        if (userId == null) {
            Toast.makeText(this, "Error: No local user ID, please login", Toast.LENGTH_LONG).show();
            locationSharingSwitch.setChecked(false);
            finish();
            return;
        }

        locationRepository.updateUserStatus(userId, isEnabled, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    trackingStatus.setText(isEnabled ? "Sharing Location" : "Not Sharing Location");
                    if (isEnabled) {
                        locationManager.startLocationUpdates();
                    } else {
                        locationManager.stopLocationUpdates();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MapActivity.this,
                            "Failed to update status: " + error,
                            Toast.LENGTH_SHORT).show();
                    locationSharingSwitch.setChecked(!isEnabled);
                });
            }
        });
    }

    /**
     * Checks and updates the initial location sharing status of the user
     * by fetching the user's status from the repository.
     */
    private void checkAndUpdateSharingStatus() {
        String userId = userPreferences.getUserId();
        if (userId == null) return;

        locationRepository.getUserStatus(userId, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    locationSharingSwitch.setChecked(user.isActive());
                    if (user.isActive()) {
                        locationManager.startLocationUpdates();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(MapActivity.this,
                                "Failed to get user status: " + error,
                                Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    /**
     * Callback from the LocationManager when a new location is available.
     * Updates the UI and optionally updates the remote location if sharing is enabled.
     *
     * @param location The updated Location object.
     */
    @Override
    public void onLocationUpdated(Location location) {
        updateLocationDisplay(location);
        if (locationSharingSwitch.isChecked()) {
            updateRemoteLocation(location);
        }
    }

    /**
     * Updates the UI elements with the newly received location and
     * moves the local map marker to the new position.
     *
     * @param location The user's current location.
     */
    private void updateLocationDisplay(Location location) {
        latitudeText.setText(String.format(Locale.getDefault(), "%.6f", location.getLatitude()));
        longitudeText.setText(String.format(Locale.getDefault(), "%.6f", location.getLongitude()));
        mapViewController.updateLocalLocation(location);
    }

    /**
     * Updates the remote database or service with the new location
     * and sets the time chip to show the last update time.
     *
     * @param location The user's current location to send to the remote repository.
     */
    private void updateRemoteLocation(Location location) {
        locationRepository.updateLocation(
                userPreferences.getUserId(),
                location.getLatitude(),
                location.getLongitude(),
                new LocationCallback() {
                    @Override
                    public void onSuccess(com.omri.trackinglibrary.models.Location loc) {
                        runOnUiThread(() ->
                                updateTimeChip.setText(TimeFormatter.formatTime(new Date())));
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() ->
                                Toast.makeText(MapActivity.this,
                                        "Failed to update location: " + error,
                                        Toast.LENGTH_SHORT).show());
                    }
                }
        );
    }

    /**
     * Callback from the RemoteTrackingManager when a tracked user's location changes.
     *
     * @param userId         The user ID being tracked.
     * @param remoteLocation The latest remote location data.
     * @param isFirstUpdate  True if this is the first time we get a location for the user.
     */
    @Override
    public void onRemoteLocationUpdated(String userId,
                                        com.omri.trackinglibrary.models.Location remoteLocation,
                                        boolean isFirstUpdate) {
        runOnUiThread(() -> {
            if (isFirstUpdate) {
                // Only focus on remote user's location on first update
                mapViewController.updateRemoteUserLocation(userId, remoteLocation);
                Toast.makeText(this, "Found and tracking user: " + userId, Toast.LENGTH_SHORT).show();
            } else {
                // Just update marker without camera movement
                mapViewController.updateRemoteUserMarker(userId, remoteLocation);
            }
        });
    }

    /**
     * Callback indicating that the remote user is inactive.
     *
     * @param userId The user ID that is inactive.
     */
    @Override
    public void onUserInactive(String userId) {
        runOnUiThread(() ->
                Toast.makeText(this,
                        "User " + userId + " is not active!",
                        Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Callback for handling errors encountered during remote tracking.
     *
     * @param error A string describing the error that occurred.
     */
    @Override
    public void onTrackingError(String error) {
        runOnUiThread(() ->
                Toast.makeText(this,
                        "Tracking error: " + error,
                        Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Repositions the map camera to the user's current location
     * if it is available.
     */
    private void focusOnMyLocation() {
        mapViewController.focusOnLocation(locationManager.getLastLocation());
    }

    /**
     * Handles the result of permission requests. Specifically checks
     * if location permission has been granted, and starts location updates if needed.
     *
     * @param requestCode  The permission request code.
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && mapViewController != null) {
            mapViewController.enableMyLocation(locationManager.hasLocationPermission());
            if (locationManager.hasLocationPermission() && locationSharingSwitch.isChecked()) {
                locationManager.startLocationUpdates();
            }
        }
    }

    /**
     * Lifecycle callback invoked when the activity is resumed.
     * Restarts location updates if sharing is enabled.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (locationSharingSwitch.isChecked()) {
            locationManager.startLocationUpdates();
        }
    }

    /**
     * Lifecycle callback invoked when the activity is paused.
     * Stops location updates and any remote tracking.
     */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.stopLocationUpdates();
        remoteTrackingManager.stopTracking();
    }
}
