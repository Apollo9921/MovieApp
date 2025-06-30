# Ecommerce App

## Overview

This application displays a list of e-commerce products fetched from an API. Users can scroll through the products and view detailed information by selecting a specific product. The app implements pagination for efficient loading and local caching for an optimized offline/repeat-use experience.

## How the App Works

1.  **Initial Load:** Upon launching, the app checks for locally stored product data.
    *   If cached data exists, it's displayed immediately.
    *   Simultaneously, or if no cache exists, the app fetches the first set of 10 products from the API.
2.  **Data Caching:** Each batch of 10 products fetched from the API is saved to the local Room database.
3.  **Pagination:** As the user scrolls to the end of the currently displayed product list, the app automatically requests the next set of 10 products from the API. This new data is then displayed and also saved to the local database.
4.  **Product Details:** Tapping on any product in the list navigates the user to a dedicated details screen, displaying more information about that specific product.
5.  **Offline Access:** When the app is opened again, it will first attempt to load products from the local database, providing a fast startup and offline access to previously loaded items.
6.  **Network Monitoring:** The app includes a network observer to monitor internet connectivity in real-time, potentially influencing data fetching strategies.

## Package Structure

This app follows a **Clean Architecture** approach with **MVVM (Model-View-ViewModel)** as the presentation layer pattern. The codebase is organized into the following main packages:

*   **`com.example.ecommerce.components`**: Contains reusable Jetpack Compose functions that are used across different screens of the application.
*   **`com.example.ecommerce.core`**: Houses core application elements, primarily focusing on styling and theming (e.g., `Color.kt`, `Theme.kt`, `Type.kt`).
*   **`com.example.ecommerce.koin`**: Contains Koin modules and definitions for dependency injection, providing necessary dependencies throughout the application (e.g., ViewModels, Repositories, Database instances, Network services).
*   **`com.example.ecommerce.navigation`**: Manages navigation between different screens/composables within the app, likely using Jetpack Navigation Compose.
*   **`com.example.ecommerce.networking`**: Handles API requests using Retrofit. This is also where ViewModel classes are currently located.
*   **`com.example.ecommerce.room`**: Contains all classes related to the Room Database implementation, including entities, DAOs, and the database class itself.
*   **`com.example.ecommerce.screens`**: Contains the UI-related logic using Composable screens.
*   **`com.example.ecommerce.utils`**: Contains utility classes and helper functions.
    *   `NetworkObserver.kt`: Observes network connectivity in real-time.
    *   `ScreenSizeUtils.kt`: Provides utilities for responsive UI sizing based on screen dimensions.

## Technologies Used

*   **Programming Language:** Kotlin
*   **Architecture:** Clean Architecture, MVVM (Model-View-ViewModel)
*   **UI Toolkit:** Jetpack Compose for building modern, declarative UIs.
*   **Asynchronous Programming:** Kotlin Coroutines for managing background tasks and asynchronous operations.
*   **Dependency Injection:** Koin for managing dependencies.
*   **Networking:** Retrofit for making HTTP requests to the API.
*   **Database:** Room Persistence Library for local data storage and caching.
*   **Image Loading:** Coil for efficient image loading and caching in Jetpack Compose.
*   **Navigation:** Jetpack Navigation Compose.
*   **Build System:** Gradle
*   **Version Control:** Git
