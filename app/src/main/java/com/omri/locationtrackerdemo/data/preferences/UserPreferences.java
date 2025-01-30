package com.omri.locationtrackerdemo.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * UserPreferences manages persistent storage of user-related data using Android's SharedPreferences.

 * This class provides a clean interface for storing and retrieving user identification data
 * locally on the device. It ensures consistent access to SharedPreferences throughout the application
 * and encapsulates the implementation details of data persistence.

 * Key Features:
 * - Secure storage using MODE_PRIVATE
 * - Null-safe user ID retrieval
 * - Automatic asynchronous writes
 * - Single responsibility for user preferences management

 * Usage Example:
 * ```
 * UserPreferences prefs = new UserPreferences(context);

 * // Store user ID
 * prefs.setUserId("user123");

 * // Retrieve user ID
 * String userId = prefs.getUserId(); // Returns "user123" or null if not set
 * ```

 * Note: This class currently handles only user ID storage, but can be extended
 * to manage other user-related preferences such as:
 * - User settings
 * - App configuration
 * - Session information
 * - Feature flags
 */
public class UserPreferences {

    /**
     * Name of the SharedPreferences file
     */
    private static final String PREFS_NAME = "prefs";

    /**
     * Key for storing the user ID in SharedPreferences
     */
    private static final String KEY_USER_ID = "user_id";

    /**
     * SharedPreferences instance for persistent storage
     */
    private final SharedPreferences sharedPreferences;

    /**
     * Constructs a new UserPreferences instance.
     *
     * @param context The application context used to access SharedPreferences.
     *                Should be Application context to prevent memory leaks.
     */
    public UserPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Retrieves the stored user ID.
     *
     * @return The stored user ID, or null if no user ID has been stored.
     */
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    /**
     * Stores a new user ID.
     * The storage operation is performed asynchronously using apply() rather than commit()
     * to prevent blocking the main thread.
     *
     * @param userId The user ID to store. Can be null to clear the stored user ID.
     */
    public void setUserId(String userId) {
        sharedPreferences.edit()
                .putString(KEY_USER_ID, userId)
                .apply();
    }
}