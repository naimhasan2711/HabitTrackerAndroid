# 📱 Habit Tracker

A privacy-focused, fully offline habit tracking Android application built with modern Android development practices. Track your daily habits, monitor streaks, view analytics, and build positive routines — all without requiring an internet connection or account creation.

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

---

## 📖 Table of Contents

- [Features](#-features)
- [Screenshots](#-screenshots)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [User Guide](#-user-guide)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

### Core Features

- **📝 Habit Management** - Create, edit, archive, and delete habits with customizable icons and colors
- **✅ Daily Tracking** - Mark habits as complete with satisfying animations
- **🔥 Streak System** - Track current and longest streaks to stay motivated
- **📅 Calendar View** - View habit completion history on a monthly calendar
- **📊 Analytics Dashboard** - Visualize progress with charts and completion rates
- **⏰ Reminders** - Set daily notifications per habit at your preferred time
- **🌙 Dark Mode** - Full dark theme support with Material You dynamic colors

### Privacy & Data

- **🔒 Fully Offline** - All data stored locally on device, no internet required
- **📤 Export/Import** - Backup and restore your data as JSON files
- **🗑️ Archive System** - Archive old habits without losing history

### User Experience

- **🎨 Material 3 Design** - Modern UI following Material Design 3 guidelines
- **💫 Smooth Animations** - Delightful micro-interactions throughout the app
- **📱 Edge-to-Edge** - Immersive full-screen experience
- **🔄 Pull to Refresh** - Intuitive content refresh mechanism
- **⬇️ Bottom Navigation** - Easy access to all main sections

---

## 📸 Screenshots

|              Home               |         Habit Detail          |        Calendar         |      Analytics      |
| :-----------------------------: | :---------------------------: | :---------------------: | :-----------------: |
| Daily habits list with progress | Streak tracking & 30-day grid | Monthly completion view | Charts & statistics |

---

## 🛠 Technology Stack

### Language & UI

| Technology          | Purpose                            |
| ------------------- | ---------------------------------- |
| **Kotlin**          | Primary programming language       |
| **Jetpack Compose** | Modern declarative UI toolkit      |
| **Material 3**      | Design system with dynamic theming |

### Architecture & DI

| Technology             | Purpose                                                     |
| ---------------------- | ----------------------------------------------------------- |
| **MVVM**               | Presentation layer architecture pattern                     |
| **Clean Architecture** | Separation of concerns with data/domain/presentation layers |
| **Hilt**               | Dependency injection framework                              |
| **Navigation Compose** | Type-safe in-app navigation                                 |

### Data & Storage

| Technology            | Purpose                                 |
| --------------------- | --------------------------------------- |
| **Room Database**     | Local SQLite database with Flow support |
| **DataStore**         | User preferences storage                |
| **Kotlin Coroutines** | Asynchronous programming                |
| **Kotlin Flow**       | Reactive data streams                   |

### Additional Libraries

| Library          | Purpose                            |
| ---------------- | ---------------------------------- |
| **Vico Charts**  | Beautiful bar charts for analytics |
| **AlarmManager** | Scheduling local notifications     |

---

## 🏗 Architecture

The app follows **Clean Architecture** principles with clear separation between layers:

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Screens   │  │  ViewModels │  │     Components      │  │
│  │  (Compose)  │  │  (StateFlow)│  │   (Reusable UI)     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Models    │  │  Use Cases  │  │ Repository Interfaces│  │
│  │   (DTOs)    │  │  (Business) │  │    (Contracts)      │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       DATA LAYER                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  Room DAOs  │  │ Repositories│  │      Entities       │  │
│  │  (Database) │  │   (Impl)    │  │   (DB Tables)       │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

#### Presentation Layer

- **Screens**: Jetpack Compose UI screens (Home, Calendar, Analytics, Settings, etc.)
- **ViewModels**: Hold UI state using `StateFlow`, handle user interactions
- **Components**: Reusable UI components (HabitCard, BottomNavigation, etc.)

#### Domain Layer

- **Models**: Business data classes (Habit, HabitLog, Category, etc.)
- **Use Cases**: Single-responsibility business logic operations
- **Repository Interfaces**: Contracts for data operations

#### Data Layer

- **Entities**: Room database table definitions
- **DAOs**: Data Access Objects for database queries
- **Repositories**: Implementation of domain repository interfaces

### Data Flow

```
User Action → ViewModel → Use Case → Repository → DAO → Room Database
                ↑                                              │
                └──────────── Flow<Data> ─────────────────────┘
```

---

## 📁 Project Structure

```
app/src/main/java/com/abdur/rahman/habittracker/
│
├── 📂 data/                          # Data Layer
│   ├── 📂 local/
│   │   ├── 📂 dao/                   # Room DAOs
│   │   │   ├── CategoryDao.kt
│   │   │   ├── HabitDao.kt
│   │   │   ├── HabitLogDao.kt
│   │   │   ├── SettingsDao.kt
│   │   │   └── StreakDao.kt
│   │   ├── 📂 entity/                # Room Entities
│   │   │   ├── CategoryEntity.kt
│   │   │   ├── HabitEntity.kt
│   │   │   ├── HabitLogEntity.kt
│   │   │   ├── SettingsEntity.kt
│   │   │   └── StreakEntity.kt
│   │   └── HabitDatabase.kt          # Room Database
│   │
│   └── 📂 repository/                # Repository Implementations
│       ├── CategoryRepositoryImpl.kt
│       ├── HabitRepositoryImpl.kt
│       └── SettingsRepositoryImpl.kt
│
├── 📂 di/                            # Dependency Injection
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
│
├── 📂 domain/                        # Domain Layer
│   ├── 📂 model/                     # Domain Models
│   │   ├── Category.kt
│   │   ├── Habit.kt
│   │   ├── HabitLog.kt
│   │   └── ...
│   ├── 📂 repository/                # Repository Interfaces
│   │   ├── CategoryRepository.kt
│   │   ├── HabitRepository.kt
│   │   └── SettingsRepository.kt
│   └── 📂 usecase/                   # Use Cases
│       ├── CreateHabitUseCase.kt
│       ├── GetHabitsUseCase.kt
│       ├── ToggleHabitCompletionUseCase.kt
│       └── ...
│
├── 📂 presentation/                  # Presentation Layer
│   ├── 📂 components/                # Reusable UI Components
│   │   ├── BottomNavigation.kt
│   │   ├── FormComponents.kt
│   │   ├── HabitComponents.kt
│   │   └── ...
│   ├── 📂 navigation/                # Navigation Setup
│   │   ├── AppNavHost.kt
│   │   └── NavRoutes.kt
│   └── 📂 ui/                        # Screens
│       ├── 📂 home/
│       ├── 📂 calendar/
│       ├── 📂 analytics/
│       ├── 📂 settings/
│       ├── 📂 detail/
│       ├── 📂 addhabit/
│       ├── 📂 edithabit/
│       └── 📂 archived/
│
├── 📂 shared/                        # Shared Utilities
│   ├── 📂 constant/                  # Constants
│   │   ├── HabitColors.kt
│   │   ├── HabitIcons.kt
│   │   └── SettingsKeys.kt
│   └── 📂 utils/                     # Utility Functions
│       └── DateUtils.kt
│
├── 📂 notification/                  # Notifications
│   ├── NotificationHelper.kt
│   ├── ReminderReceiver.kt
│   └── ReminderScheduler.kt
│
└── HabitTrackerApp.kt               # Application Class
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or higher
- Android SDK 34 (Android 14)
- Minimum SDK: 26 (Android 8.0)

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/yourusername/habit-tracker.git
   cd habit-tracker
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build the project**

   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on device/emulator**
   - Connect an Android device or start an emulator
   - Click "Run" or use `Shift + F10`

### Build Variants

| Variant   | Description                                 |
| --------- | ------------------------------------------- |
| `debug`   | Development build with debugging enabled    |
| `release` | Production build with ProGuard minification |

---

## 📚 User Guide

### Creating a Habit

1. Tap the **+** floating action button on the Home screen
2. Enter the habit name (required)
3. Optionally add a description
4. Select an icon from the icon picker
5. Choose a color for the habit
6. Set the frequency (Daily, Weekly, or Custom days)
7. Optionally set a daily reminder time
8. Tap **Save**

### Tracking Habits

- On the Home screen, tap the **"Do it"** button to mark a habit as complete
- Completed habits show **"Done"** with a checkmark
- Your progress percentage updates in real-time
- Streaks automatically track consecutive completion days

### Viewing Progress

#### Calendar View

- Navigate to the Calendar tab via bottom navigation
- Swipe left/right to change months
- Tap on any date to see habits for that day
- Completion indicators show daily progress

#### Analytics View

- Navigate to the Analytics tab
- View overall completion rate
- See individual habit performance bars
- Track your best streaks

### Managing Habits

#### Edit a Habit

1. Tap on a habit card to open details
2. Tap the edit icon (pencil) in the top bar
3. Make your changes
4. Tap **Save**

#### Archive a Habit

1. Open the habit detail screen
2. Tap the three-dot menu
3. Select **Archive**
4. Archived habits are accessible from Settings → Archived Habits

#### Delete a Habit

1. Open the habit detail screen
2. Tap the three-dot menu
3. Select **Delete**
4. Confirm deletion (this action is permanent)

### Settings & Data

#### Dark Mode

- Go to Settings → Appearance → Dark Mode toggle

#### Export Data

1. Go to Settings → Data → Export
2. Choose a save location
3. Data is saved as a JSON file

#### Import Data

1. Go to Settings → Data → Import
2. Select a previously exported JSON file
3. Your habits and history will be restored

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for public functions
- Write unit tests for new features

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- [Material Design 3](https://m3.material.io/) - Design system
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - UI toolkit
- [Vico](https://github.com/patrykandpatrick/vico) - Chart library
- [Hilt](https://dagger.dev/hilt/) - Dependency injection

---

<p align="center">
  Made with ❤️ using Kotlin & Jetpack Compose
</p>
