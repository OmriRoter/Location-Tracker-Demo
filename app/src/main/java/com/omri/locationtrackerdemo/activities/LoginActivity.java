package com.omri.locationtrackerdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.omri.locationtrackerdemo.R;
import com.omri.locationtrackerdemo.data.preferences.UserPreferences;
import com.omri.locationtrackerdemo.data.repository.LocationRepository;
import com.omri.trackinglibrary.interfaces.UserCallback;
import com.omri.trackinglibrary.models.User;
import java.util.Objects;

/**
 * LoginActivity manages user authentication and registration flows for the location tracking application.

 * This activity provides two main functionalities:
 * 1. User Login: Allows existing users to verify their identity using their user ID
 * 2. User Registration: Enables new users to create an account with a username

 * The activity follows MVVM architecture patterns by:
 * - Using UserPreferences for data persistence
 * - Delegating location tracking operations to LocationRepository
 * - Implementing clean separation of concerns between UI and business logic

 * Key Features:
 * - Input validation for both login and registration
 * - Persistent session management using SharedPreferences
 * - Error handling and user feedback
 * - Loading state management
 * - Automatic navigation to MapActivity upon successful authentication
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    // UI Components
    private TextInputLayout loginInputLayout, registerInputLayout;
    private TextInputEditText loginUserIdInput, registerUsernameInput;
    private Button loginButton, registerButton;
    private ProgressBar progressBar;

    // Dependencies
    private LocationRepository locationRepository;
    private UserPreferences userPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeDependencies();
        initializeViews();
        setupClickListeners();
        checkSavedUserId();
    }

    /**
     * Initializes all dependencies required by the activity.
     * This includes the location repository for API calls,
     * user preferences for local storage, and Gson for JSON parsing.
     */
    private void initializeDependencies() {
        locationRepository = new LocationRepository();
        userPreferences = new UserPreferences(this);
        gson = new Gson();
    }

    /**
     * Binds all UI elements from the layout to their respective fields.
     * This includes text input layouts, edit texts, buttons, and progress indicators.
     */
    private void initializeViews() {
        loginInputLayout = findViewById(R.id.loginInputLayout);
        registerInputLayout = findViewById(R.id.registerInputLayout);
        loginUserIdInput = findViewById(R.id.loginUserIdInput);
        registerUsernameInput = findViewById(R.id.registerUsernameInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * Sets up click listeners for the login and register buttons.
     * Each button triggers its respective authentication flow.
     */
    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> handleLogin());
        registerButton.setOnClickListener(v -> handleRegister());
    }

    /**
     * Checks for a previously saved user ID in SharedPreferences
     * and populates the login input field if found.
     */
    private void checkSavedUserId() {
        String savedUserId = userPreferences.getUserId();
        if (savedUserId != null) {
            loginUserIdInput.setText(savedUserId);
        }
    }

    /**
     * Handles the login (verification) process when the user clicks the login button.
     * 1. Validates the input user ID
     * 2. Shows loading state
     * 3. Attempts to verify the user through LocationRepository
     * 4. Handles success/failure cases appropriately
     */
    private void handleLogin() {
        String userId = Objects.requireNonNull(loginUserIdInput.getText()).toString().trim();
        loginInputLayout.setError(null);

        if (userId.isEmpty()) {
            loginInputLayout.setError("Please enter User ID");
            return;
        }

        setLoading(true);
        Log.d(TAG, "Attempting to verify user with ID: " + userId);

        locationRepository.verifyUser(userId, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "Verify user response: " + gson.toJson(user));
                runOnUiThread(() -> {
                    setLoading(false);
                    if (user != null && user.getId() != null) {
                        saveUserAndStartMap(user);
                    } else {
                        Log.e(TAG, "User object is null or has null ID");
                        Toast.makeText(LoginActivity.this,
                                "Invalid user data received", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Verify user error: " + error);
                runOnUiThread(() -> {
                    setLoading(false);
                    loginInputLayout.setError("Invalid User ID");
                    Toast.makeText(LoginActivity.this,
                            "Login failed: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Handles the registration process when the user clicks the register button.
     * 1. Validates the input username
     * 2. Shows loading state
     * 3. Attempts to create a new user through LocationRepository
     * 4. Handles success/failure cases appropriately
     */
    private void handleRegister() {
        String username = Objects.requireNonNull(registerUsernameInput.getText()).toString().trim();
        registerInputLayout.setError(null);

        if (!validateUsername(username)) {
            return;
        }

        setLoading(true);
        Log.d(TAG, "Attempting to create user with username: " + username);

        locationRepository.createUser(username, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "Create user response: " + gson.toJson(user));
                runOnUiThread(() -> {
                    setLoading(false);
                    if (user != null && user.getId() != null) {
                        handleSuccessfulRegistration(user);
                    } else {
                        handleFailedRegistration();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Create user error: " + error);
                runOnUiThread(() -> {
                    setLoading(false);
                    registerInputLayout.setError("Registration failed");
                    Toast.makeText(LoginActivity.this,
                            "Registration failed: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Validates the username according to the application's requirements:
     * - Must not be empty
     * - Must be between 3 and 50 characters long
     *
     * @param username The username to validate
     * @return true if the username is valid, false otherwise
     */
    private boolean validateUsername(String username) {
        if (username.isEmpty()) {
            registerInputLayout.setError("Please enter username");
            return false;
        }
        if (username.length() < 3) {
            registerInputLayout.setError("Username must be at least 3 characters");
            return false;
        }
        if (username.length() > 50) {
            registerInputLayout.setError("Username must be less than 50 characters");
            return false;
        }
        return true;
    }

    /**
     * Handles a successful user registration by:
     * 1. Auto-filling the login input with the new user ID
     * 2. Showing a success message with the new user ID
     *
     * @param user The newly created User object
     */
    private void handleSuccessfulRegistration(User user) {
        loginUserIdInput.setText(user.getId());
        String message = "Registration successful! Your User ID is: " + user.getId();
        Log.d(TAG, message);
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Handles a failed registration attempt by showing an appropriate error message
     */
    private void handleFailedRegistration() {
        Log.e(TAG, "Created user object is null or has null ID");
        Toast.makeText(LoginActivity.this,
                "Registration succeeded but received invalid user data",
                Toast.LENGTH_LONG).show();
    }

    /**
     * Saves the user ID to persistent storage and starts the MapActivity
     *
     * @param user The verified User object
     */
    private void saveUserAndStartMap(User user) {
        userPreferences.setUserId(user.getId());
        startMapActivity(user.getId());
    }

    /**
     * Starts the MapActivity with the verified user ID
     * and finishes this activity to prevent back navigation
     *
     * @param userId The verified user ID to pass to MapActivity
     */
    private void startMapActivity(String userId) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }

    /**
     * Controls the loading state of the UI by showing/hiding the progress bar
     * and enabling/disabling input fields and buttons
     *
     * @param isLoading true to show loading state, false to hide it
     */
    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!isLoading);
        registerButton.setEnabled(!isLoading);
        loginUserIdInput.setEnabled(!isLoading);
        registerUsernameInput.setEnabled(!isLoading);
    }
}