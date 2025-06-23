# Mobile Application
Random User App - Android Project

Overview
A comprehensive Android application showcasing modern Android development practices. The app retrieves random user data from an external API, stores it locally in a SQLite database, and provides features like QR code scanning, camera integration, and offline functionality.

***************Features***************
Core Functionality
•	User Management: Display, search, and sort users fetched from the API.
•	Database Integration: Persistent storage with Room on a SQLite database.
•	QR Code System: Generate and scan QR codes linked to individual users.
•	Camera Integration: Real-time QR code scanning via ML Kit.
•	Multi-Screen Navigation: Seamless navigation across four main screens.

User Interface
1.	User Overview Screen: List view with search and sort options.
2.	User Detail Screen: Detailed user profiles with QR code generation.
3.	AR/Camera Screen: Live camera preview and QR code scanning.
4.	Settings Screen: Tools for configuration and database management.
   
Advanced Features
•	Offline Support: Complete functionality without internet access.
•	Real-Time Search: Filter users by name, email, or location.
•	Multiple Sorting Options: Sort users by name, location, or creation date.
•	Database Management: Tools to inspect, populate, or clear the database.
•	Error Handling: Robust error recovery and user feedback mechanisms.

***************Technical Requirements***************
Build Environment
•	Android Studio: Hedgehog (2023.1.1) or newer.
•	JDK: Version 11 or higher.
•	Gradle: Version 8.2+.

Target Platform
•	Minimum SDK: API 24 (Android 7.0).
•	Target SDK: API 35 (Android 15).
•	Compile SDK: API 35.

Required Permissions
•	CAMERA - For QR code scanning.
•	INTERNET - For fetching data from the API.

***************Build Instructions***************
1. Project Setup
•	Extract project files to the desired location.
•	Open Android Studio and select "Open an existing project".
•	Navigate to the project folder and load it.

3. Dependency Installation
•	All dependencies are managed via Gradle and will be automatically downloaded during the build process.

4. Build Process
./gradlew clean build           # Clean and build the project
./gradlew assembleDebug         # Generate a debug APK
./gradlew installDebug          # Install APK on connected device

4. Running the Application
•	Connect an Android device with USB debugging enabled or use an emulator.
•	Click the Run button in Android Studio.

***************Dependencies***************
Core Framework
•	Jetpack Compose BOM: 2023.10.01
•	Activity Compose: 1.8.1
•	Navigation Compose: 2.7.6
•	Lifecycle ViewModel Compose: 2.7.0

Database
•	Room Runtime: 2.6.1
•	Room KTX: 2.6.1
•	Room Compiler: 2.6.1 (annotation processor)

Networking
•	Retrofit: 2.9.0
•	Gson Converter: 2.9.0
•	OkHttp Logging Interceptor: 4.12.0

Camera and QR Code Processing
•	CameraX Bundle: 1.3.1
•	ML Kit Barcode Scanning: 17.2.0
•	ZXing Core: 3.5.2
•	ZXing Android Embedded: 4.3.0

Additional Libraries
•	Coil Compose: 2.5.0
•	Accompanist Permissions: 0.32.0
•	Coroutines: 1.7.3

***************API Integration***************
Base API Details
•	Base URL: https://randomuser.me/
•	Format: JSON
•	Authentication: None required.
•	Rate Limiting: Fair use policy.

Endpoints Used
•	GET /api/ - Fetch a single random user.
•	GET /api/?results={count} - Fetch multiple random users.

***************Database Schema***************
UserEntity Table
•	Primary Key: id (Auto-generated).
•	Unique Identifier: uniqueId (String).
•	User Information: Name, contact details, and location data.
•	Metadata: Timestamp and source indicator.
•	Media: URLs for profile pictures.

Operations
•	Create: Add new users.
•	Read: Query users with filters and sorting.
•	Update: Edit user information.
•	Delete: Remove users or clear the database.

***************Troubleshooting***************
Common Issues
•	Build Failures:
o	Verify JDK 11+ and up-to-date Android SDK.
o	Try File > Invalidate Caches and Restart.
•	Camera Issues:
o	Ensure permissions are granted.
o	Verify camera functionality.
•	QR Code Scanning:
o	Maintain adequate lighting.
o	Reset database for valid QR codes.
•	Database Errors:
o	Inspect logs using Logcat.
o	Clear app data and rebuild the database.

Debug Features
•	Tools to reset and inspect the database.
•	User statistics and cache management options.

Additional Notes
•	Offline support ensures usability after initial data load.
•	QR codes contain user identification data and are generated with ZXing.
•	The UI follows Material Design 3 with a modern custom theme.
•	All asynchronous tasks are handled via Kotlin Coroutines for optimal performance.

