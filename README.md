# Location Tracker Demo

An Android application for real-time location tracking and sharing between users.

## Features

- **User Authentication**
  - User registration with username
  - Login with user ID
  - Persistent session management

- **Real-time Location Tracking**
  - High-accuracy device location updates
  - Location sharing toggle
  - Background location updates
  - Configurable update intervals

- **Remote User Tracking**
  - Search users by ID
  - Real-time location updates of tracked users
  - User status monitoring (active/inactive)
  - Error handling for offline users

- **Map Visualization**
  - Google Maps integration
  - Current location display
  - Remote user markers
  - Automatic camera focus
  - Edge-to-edge display support

## Project Structure

### Activities
- `LoginActivity`: Handles user authentication and registration
- `MainActivity`: Application entry point with edge-to-edge display setup
- `MapActivity`: Main tracking interface with map display and controls

### Data Management
- `LocationRepository`: Manages communication with location tracking API
- `UserPreferences`: Handles local data persistence using SharedPreferences

### Location Services
- `LocationManager`: Manages device location updates using FusedLocationProvider
- `RemoteTrackingManager`: Handles remote user tracking with periodic polling

### UI Components
- `MapViewController`: Controls map visualization and camera movements
- `MarkerManager`: Manages map markers for remote users

### Interfaces
- `LocationUpdateListener`: Callback for device location updates
- `RemoteUserLocationListener`: Callback for remote user tracking events

### Utilities
- `TimeFormatter`: Formats timestamps for location updates

## Technical Details

### Location Updates
- Local updates: 3 seconds interval (fastest: 1.5 seconds)
- Remote polling: 3 seconds interval
- High accuracy mode using GPS and network providers

### Permissions
Required permissions:
- `ACCESS_FINE_LOCATION`: For precise location tracking
- Internet access: For remote user tracking

### Dependencies
- Google Maps Android SDK
- Google Play Services Location
- Custom Location Tracking Library

## Setup

1. Clone the repository
2. Add your Google Maps API key to `AndroidManifest.xml`
3. Configure the location tracking library endpoint
4. Build and run the application

## Usage

1. **Registration/Login**
   - Register with a username to get a user ID
   - Login using the provided user ID
   - User ID is saved for future sessions

2. **Location Sharing**
   - Toggle location sharing using the switch
   - View your current coordinates
   - See last update time

3. **Track Other Users**
   - Enter a user ID to track
   - View their location on the map
   - Automatic updates when they move
   - Clear tracking when finished

## Error Handling

The application handles various error scenarios:
- Lost network connectivity
- Location permission denials
- Inactive or offline users
- Invalid user IDs
- Location service failures

## Future Improvements

Potential enhancements:
- Multiple user tracking
- Custom marker icons
- Location history
- User search by username
- Group tracking features
- Enhanced error recovery
