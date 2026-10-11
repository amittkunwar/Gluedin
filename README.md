# Gluedin OTT Android Application

A clean, modern OTT-style Android application engineered to demonstrate Clean Architecture, MVVM design patterns, Unidirectional Data Flow, Android Jetpack components, Media3/ExoPlayer video playback, and a decoupled player analytics event pipeline.

---

## 📱 Features & Highlights

- **OTT Home Screen**:
  - Prominent **Hero Banner** featuring headline content with backdrop poster, category/rating pills, synopsis, and dual CTAs (*"▶ Play"* and *"ℹ More Info"*).
  - Multiple horizontally scrollable **Content Rails** (*"Trending Now"*, *"Recommended for You"*, *"Action & Blockbusters"*).
  - High performance: Nested horizontal RecyclerViews share a single `RecyclerView.RecycledViewPool` to eliminate view allocation overhead during scrolling.
- **Content Detail Screen**:
  - Detailed metadata view: High-res poster backdrop, duration, release year, age ratings, overview synopsis.
  - Primary *"▶ Play Now"* CTA button navigating directly into full-screen video playback.
- **Media3 / ExoPlayer Video Playback**:
  - Fullscreen playback with immersive system bars handling (`WindowInsetsControllerCompat`).
  - Supports both MP4 and HLS (`.m3u8`) streaming.
  - Android lifecycle-aware resource management (`onStart`, `onResume`, `onPause`, `onStop`).
- **Decoupled Player Event Pipeline**:
  - Player events are completely decoupled from UI controllers:
    $$\text{ExoPlayer} \longrightarrow \text{PlayerEventHandler} \longrightarrow \text{PlayerViewModel} \longrightarrow \text{AnalyticsRepository}$$
  - Captures and logs:
    1. `PLAYER_READY`
    2. `PLAYER_STARTED`
    3. `PLAYER_PAUSED`
    4. `PLAYER_SEEKED`
    5. `PLAYER_BUFFERING` (Started & Ended)
    6. `PLAYER_COMPLETED`
    7. `PLAYER_ERROR`
  - Real-time event badge overlay in player UI for live demonstration.
- **Robust State Management**:
  - Single Source of Truth via `UiState<T>` (`Loading`, `Success`, `Empty`, `Error`).
  - UI retry mechanisms on error.

---

## 🏗 Architecture Overview

The application strictly adheres to **Clean Architecture** and **MVVM** principles with clear layer separation:

```
┌────────────────────────────────────────────────────────┐
│                   Presentation Layer                   │
│   • Activities: MainActivity, DetailActivity,          │
│                 PlayerActivity                         │
│   • ViewModels: HomeViewModel, DetailViewModel,        │
│                 PlayerViewModel                        │
│   • Adapters: HomeFeedAdapter, ContentItemAdapter      │
│   • UI State: UiState<T>, PlayerUiState                │
└───────────────────────────┬────────────────────────────┘
                            │ depends on
                            ▼
┌────────────────────────────────────────────────────────┐
│                      Domain Layer                      │
│   • Models: HomeFeed, HeroBanner, ContentRail,         │
│             ContentItem                                │
│   • Use Cases: GetHomeFeedUseCase,                     │
│                GetContentDetailUseCase                 │
│   • Contracts: ContentRepository, AnalyticsRepository  │
│   • Events: PlayerEvent (Sealed Interface)             │
└───────────────────────────▲────────────────────────────┘
                            │ implemented by
                            │ (Dependency Inversion)
┌────────────────────────────────────────────────────────┐
│                       Data Layer                       │
│   • Repository Impls: ContentRepositoryImpl,           │
│                       AnalyticsRepositoryImpl          │
│   • Data Sources: ContentRemoteDataSource,             │
│                   MockContentRemoteDataSourceImpl      │
│   • DTOs: HomeFeedResponseDto, HeroBannerDto, etc.     │
│   • Mappers: ContentMapper                             │
└────────────────────────────────────────────────────────┘
```

---

## 📁 Package & Module Structure

```
com.example.gluedin/
│
├── data/
│   ├── analytics/
│   │   └── AnalyticsRepositoryImpl.kt      // Logs PLAYER_* events with TAG
│   ├── datasource/
│   │   ├── ContentRemoteDataSource.kt      // Remote API abstraction
│   │   └── MockContentRemoteDataSourceImpl.kt // Reads JSON from assets with simulated delay
│   ├── mapper/
│   │   └── ContentMapper.kt                // Sanitizes & transforms DTOs -> Domain models
│   ├── model/dto/
│   │   └── HomeFeedDto.kt                  // Network DTOs with @SerializedName
│   └── repository/
│       └── ContentRepositoryImpl.kt        // Implements Domain ContentRepository
│
├── domain/
│   ├── analytics/
│   │   ├── AnalyticsRepository.kt          // Domain analytics contract
│   │   └── PlayerEvent.kt                  // Domain media events (Ready, Started, Paused, etc.)
│   ├── model/
│   │   └── ContentModels.kt                // Pure Kotlin domain models
│   ├── repository/
│   │   └── ContentRepository.kt            // Domain content contract
│   └── usecase/
│       ├── GetContentDetailUseCase.kt      // Single-responsibility use case
│       └── GetHomeFeedUseCase.kt           // Single-responsibility use case
│
└── presentation/
    ├── common/
    │   ├── ServiceLocator.kt               // Dependency provider & ViewModel factories
    │   └── UiState.kt                      // Sealed interface (Loading, Success, Empty, Error)
    ├── detail/
    │   ├── DetailActivity.kt               // Content Detail Screen
    │   └── DetailViewModel.kt              // Detail state management
    ├── home/
    │   ├── HomeViewModel.kt                // Home state management
    │   └── adapter/
    │       ├── ContentItemAdapter.kt       // Horizontal rail cards adapter (DiffUtil)
    │       └── HomeFeedAdapter.kt          // Heterogeneous vertical adapter (SharedViewPool)
    └── player/
        ├── PlayerActivity.kt               // Media3 player screen with lifecycle handling
        ├── PlayerEventHandler.kt           // Bridges Media3 Player.Listener to Domain PlayerEvent
        └── PlayerViewModel.kt              // Coordinates playback state & analytics dispatch
```

---

## 🔌 API-Ready Design (Mock JSON to Real API)

The app is built to connect to a live REST/GraphQL backend with **zero changes to the UI or Domain layer**:

### Current Setup:
$$\text{ContentRepositoryImpl} \longrightarrow \text{ContentRemoteDataSource} \longrightarrow \text{MockContentRemoteDataSourceImpl (Assets JSON)}$$

### Transitioning to Retrofit:
1. Define the Retrofit interface:
   ```kotlin
   interface ContentApiService {
       @GET("api/v1/home/feed")
       suspend fun getHomeFeed(): HomeFeedResponseDto
   }
   ```
2. Implement `ContentRemoteDataSource`:
   ```kotlin
   class RetrofitContentRemoteDataSourceImpl(
       private val apiService: ContentApiService
   ) : ContentRemoteDataSource {
       override suspend fun getHomeFeed(): HomeFeedResponseDto {
           return apiService.getHomeFeed()
       }
   }
   ```
3. Update `ServiceLocator.kt` to inject `RetrofitContentRemoteDataSourceImpl` instead of `MockContentRemoteDataSourceImpl`.

**Zero modifications are needed in `HomeFeedAdapter`, `MainActivity`, `HomeViewModel`, `GetHomeFeedUseCase`, or `ContentRepository`.**

---

## 🎬 Player Event Handling Approach

Section 5 of the technical requirements specifies:
> *"Player events should not simply be handled and logged directly inside the Activity or Fragment. The implementation should demonstrate an appropriate separation of responsibilities."*

### Implementation Flow:
1. **`Player.Listener`**: `PlayerEventHandler` registers with `ExoPlayer` and listens to low-level engine callbacks (`onPlaybackStateChanged`, `onIsPlayingChanged`, `onPositionDiscontinuity`, `onPlayerError`).
2. **Translation**: `PlayerEventHandler` translates low-level events into domain-level `PlayerEvent` instances (`PlayerEvent.Started`, `PlayerEvent.Seeked(positionMs)`, etc.).
3. **ViewModel Dispatch**: `PlayerViewModel.onPlayerEvent(event)` updates the UI state and dispatches the event to `AnalyticsRepository`.
4. **Analytics Logging**: `AnalyticsRepositoryImpl` records the event with the required identifiers:
   - `PLAYER_READY`
   - `PLAYER_STARTED`
   - `PLAYER_PAUSED`
   - `PLAYER_SEEKED (position: Xms)`
   - `PLAYER_BUFFERING (buffering: true/false)`
   - `PLAYER_COMPLETED`
   - `PLAYER_ERROR (error: ...)`

---

## 🛠 Tech Stack & Libraries Used

| Library / Tool | Purpose |
| :--- | :--- |
| **Kotlin** | Language |
| **XML + ViewBinding** | Compile-time safe UI layouts (No Jetpack Compose as requested) |
| **Android Jetpack** | ViewModel, Lifecycle Runtime, Navigation |
| **Kotlin Coroutines & Flow** | Asynchronous operations and reactive UI streams (`StateFlow`, `SharedFlow`) |
| **Media3 ExoPlayer (`1.5.1`)** | Modern Google video playback engine & HLS streaming |
| **Coil (`2.7.0`)** | Kotlin-first image loading & caching |
| **Gson (`2.11.0`)** | JSON deserialization for network DTOs |
| **Material Components 3** | Buttons, Cards, Theming |

---

## 🚀 How to Build and Run

### Prerequisites
- Android Studio Ladybug / Meerkat or command-line SDK
- JDK 21
- Android SDK 35/37

### Build via Terminal
```bash
# Clone the repository
git clone https://github.com/amittkunwar/Gluedin.git
cd Gluedin

# Build Debug APK
./gradlew assembleDebug

# Install on connected device or emulator
./gradlew installDebug
```

The compiled APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```
