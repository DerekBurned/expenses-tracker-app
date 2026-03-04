# 📱 ExpenseSync — Android App

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Hilt](https://img.shields.io/badge/Hilt-DI-orange?style=for-the-badge)

The Android companion app for the **ExpenseSync API** — an offline-first expense tracker that saves your data locally and syncs to the cloud when you're back online.

🔗 **Backend Companion API:** [ExpenseSync API](../README.md)

---

## 📱 What It Does

Lost your connection mid-transaction? No problem. ExpenseSync saves every expense to your device instantly using **Room (SQLite)**, then automatically batches and syncs unsynced records to the Spring Boot backend whenever the network is restored — without duplicates, without data loss.

---

## 🛠 Tech Stack

| Layer | Technology |
|:---|:---|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt (via KSP) |
| Local DB | Room |
| Networking | Retrofit + OkHttp |
| Async | Kotlin Coroutines + Flow |
| Background Sync | WorkManager |
| Image Loading | Coil |
| Preferences | DataStore |
| Navigation | Jetpack Navigation Compose |

---

## 🏗 Project Structure
```
app/src/main/java/com/example/expenses_tracker_app/
├── data/
│   ├── local/
│   │   ├── dao/          # Room DAOs (ExpenseDao)
│   │   ├── database/     # RDatabase setup
│   │   └── entity/       # Room entities (ExpenseEntity)
│   ├── remote/           # Retrofit API interface + DTOs
│   └── repository/       # Repository implementations
├── di/                   # Hilt DI modules
│   ├── AppModule
│   ├── DatabaseModule
│   ├── NetworkModule
│   ├── RepositoryModule
│   └── UseCaseModule
├── domain/
│   ├── model/            # Domain models (Expense)
│   ├── repository/       # Repository interfaces
│   └── usecase/          # Use cases (GetAll, Sync)
└── presentation/
    ├── MainActivity.kt
    └── theme/            # Material 3 theming + ViewModel
```

---

## ⚙️ Setup & Configuration

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 11+
- An emulator or physical device running **Android 9 (API 28)+**
- The [ExpenseSync Spring Boot backend](../README.md) running locally

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd expenses-tracker-app
```

### 2. Configure the backend URL

Open `app/src/main/java/com/example/expenses_tracker_app/di/NetworkModule.kt` and set your machine's IP:
```kotlin
.baseUrl("http://<YOUR_LOCAL_IP>:8080/")
```

> **Emulator tip:** Use `http://10.0.2.2:8080/` to reach your host machine's localhost from an Android emulator.

### 3. Build and run

Open the project in Android Studio and click **Run ▶**, or use:
```bash
./gradlew assembleDebug
```

---

## 🔄 Offline-First Sync Flow
```
User adds expense
       ↓
 Saved to Room DB
 (isSynced = false)
       ↓
 Network available?
    ↙        ↘
  YES          NO
   ↓            ↓
 Batch POST   Stays local
 to /api/      until online
 expenses/sync
       ↓
 Server confirms
       ↓
 Room: isSynced = true
```

The `ExpenseRepositoryImpl` handles this flow — querying only unsynced rows, posting them, and marking them synced on success.

---

## 📡 API Integration

The app communicates with one endpoint:

| Method | Endpoint | Description |
|:---|:---|:---|
| `POST` | `/api/expenses/sync` | Batch sync unsynced local expenses |

**DTO sent to server:**
```json
[
  {
    "localId": "uuid-generated-on-device",
    "amount": 42.50,
    "description": "Coffee",
    "expenseDate": "2026-02-26T12:30:00"
  }
]
```

---

## 🧪 Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest
```

---

## 📋 Requirements

- **Min SDK:** API 28 (Android 9.0 Pie)
- **Target SDK:** API 36
- **Compile SDK:** API 36
- **Kotlin:** 2.x with KSP
- **Gradle:** Kotlin DSL (`build.gradle.kts`)

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
