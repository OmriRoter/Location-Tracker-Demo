package com.omri.locationtrackerdemo.activities;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.omri.locationtrackerdemo.R;

/**
 * MainActivity serves as the launcher activity for the Location Tracker application.

 * This activity is responsible for:
 * - Initializing the application's main UI
 * - Setting up edge-to-edge display support for modern Android devices
 * - Handling system window insets for proper UI layout

 * The activity implements edge-to-edge display functionality, which:
 * - Extends the app's content behind the system bars (status bar and navigation bar)
 * - Properly handles window insets to ensure content remains visible
 * - Provides a more immersive user experience

 * Technical Details:
 * - Uses AndroidX EdgeToEdge API for modern window inset handling
 * - Applies appropriate padding based on system bar dimensions
 * - Maintains compatibility across different Android versions

 * Note: This activity currently serves as a basic entry point and could be extended
 * in the future to include:
 * - Splash screen functionality
 * - Initial app setup and configuration
 * - User onboarding flows
 * - Version checking and updates
 * - Navigation to other main features
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display support
        EdgeToEdge.enable(this);

        // Set the activity's layout
        setContentView(R.layout.activity_main);

        // Configure window insets handling for proper edge-to-edge display
        setupWindowInsets();
    }

    /**
     * Sets up the window insets listener to handle edge-to-edge display properly.
     * This ensures content is properly padded around system bars while maintaining
     * full-screen immersive experience.
     */
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Extract the system bars insets (status bar, navigation bar)
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Apply padding to the main container to prevent content from being hidden
            // behind system bars while maintaining edge-to-edge display
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            // Return the original insets to allow proper propagation
            return insets;
        });
    }
}