# ExpenseSync API (Mobile Backend)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Hibernate/JPA](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)

A specialized RESTful API designed to serve as the backend for an **Offline-First Mobile Application**. Built with Spring Boot and Clean Architecture principles.

**Frontend Companion App:** [Expenses-tracker-app](https://github.com/DerekBurned/expenses-tracker-app/blob/master/README.md)

> **Note:** The Android companion app is currently being optimised. Performance improvements, UI polish, and additional features are in progress.

---

## The Problem It Solves

Mobile apps frequently lose network connection. Standard CRUD APIs fail when a user tries to save data offline.

This backend is designed to support an **Offline-First Sync Strategy**:
1. The mobile client (Android/Room) saves data locally using generated UUIDs.
2. When the network is restored, the client sends a **batch payload** of unsynced data.
3. This API processes the batch, checks for duplicate UUIDs to ensure idempotency (preventing double-charges if the sync drops and retries), and safely persists the new records to the database.

---

## Tech Stack

- **Framework:** Spring Boot 3.x
- **Language:** Java 17+
- **Build Tool:** Maven
- **Database:** H2 (file-based persistence) / Ready for PostgreSQL
- **Data Access:** Spring Data JPA / Hibernate

---

## Running the API

### Prerequisites

- Java 17 or higher
- Maven

### Start the server

```bash
mvn spring-boot:run
```

The server starts on `http://localhost:8080`. The H2 console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/expensedb`, username: `sa`, password: `password`).

---

## Connecting the Android App to the API

The Android app communicates with this API over HTTP. There are two ways to connect depending on your setup.

### Option A — Same Wi-Fi network (direct IP)

1. Find your PC's local IP address. On Windows, open Command Prompt and run:
   ```
   ipconfig
   ```
   Look for **Wireless LAN adapter Wi-Fi** and note the **IPv4 Address** (e.g. `192.168.1.42`).

2. Make sure port 8080 is open in Windows Firewall. Run Command Prompt as Administrator:
   ```
   netsh advfirewall firewall add rule name="Spring Boot 8080" dir=in action=allow protocol=TCP localport=8080 profile=any
   ```

3. In the Android app's `NetworkModule.kt`, set:
   ```kotlin
   .baseUrl("http://192.168.1.42:8080/")
   ```

4. Test the connection from your phone's browser: `http://192.168.1.42:8080/actuator/health`
   If it returns `{"status":"UP"}` the connection works.

> Note: this IP is assigned by DHCP and may change when your PC reconnects to Wi-Fi. If the app stops connecting, re-run `ipconfig` and update the base URL.

---

### Option B — ngrok tunnel (recommended for development)

Use this if your router blocks device-to-device traffic, you are on a corporate or university network, or the direct IP approach does not work.

ngrok creates a secure public tunnel to your local server so any device can reach it over the internet.

#### Step 1 — Install ngrok

Download from [https://ngrok.com/download](https://ngrok.com/download) and follow the installation instructions for your OS. Create a free account if you do not have one.

#### Step 2 — Authenticate ngrok

```bash
ngrok config add-authtoken YOUR_AUTH_TOKEN
```

Your auth token is available at [https://dashboard.ngrok.com/get-started/your-authtoken](https://dashboard.ngrok.com/get-started/your-authtoken).

#### Step 3 — Start the tunnel

With Spring Boot already running on port 8080, open a separate terminal and run:

```bash
ngrok http 8080
```

You will see output similar to this:

```
Forwarding    https://abc123.ngrok-free.app -> http://localhost:8080
```

#### Step 4 — Update the Android app

In `NetworkModule.kt`, replace the base URL with your ngrok forwarding URL:

```kotlin
.baseUrl("https://abc123.ngrok-free.app/")
```

Rebuild and run the app.

> Note: on the free plan, the ngrok URL changes every time you restart ngrok. When that happens, update `baseUrl` and rebuild. You can reserve a static domain at [https://dashboard.ngrok.com](https://dashboard.ngrok.com) to avoid this.

#### Why the `ngrok-skip-browser-warning` header is required

By default, ngrok intercepts the first request to a free tunnel and returns an HTML browser warning page instead of forwarding it to your server. Gson (the Android JSON parser) cannot parse HTML and throws a `JsonSyntaxException`. The app's `NetworkModule` already adds the following header to every request to bypass this:

```kotlin
.addHeader("ngrok-skip-browser-warning", "true")
```

No action is needed on your part — this is already handled in the app.

---

## API Endpoints

| Method | Endpoint | Description | Payload |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/expenses/sync` | Syncs a batch of offline records from the mobile client. Ignores duplicates. | `List<ExpenseDTO>` |
| `GET` | `/api/expenses` | Fetches all synced expenses from the server. | None |

### Example Payload (`POST /api/expenses/sync`)

```json
[
  {
    "localId": "550e8400-e29b-41d4-a716-446655440000",
    "amount": 42.50,
    "description": "Coffee and Lunch",
    "expenseDate": "2026-02-26T12:30:00"
  },
  {
    "localId": "b1928374-1234-5678-90ab-cdef12345678",
    "amount": 120.00,
    "description": "Groceries",
    "expenseDate": "2026-02-25T18:15:00"
  }
]
```

---

## Full API Reference

### Expenses — `/api/expenses`

| Method | Endpoint | Description | Request | Response |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/expenses/sync` | Batch sync of offline expenses. Duplicate `localId` values are ignored. | `List<ExpenseDTO>` | `"Synced N new expenses."` |
| `GET` | `/api/expenses` | Fetch all expenses. Optionally filter by user and sort order. | Query params | `List<ExpenseResponseDTO>` |
| `DELETE` | `/api/expenses/{id}` | Delete an expense by its `localId`. | Path variable | `"Deleted"` or 404 |

#### GET `/api/expenses` — query parameters

| Parameter | Required | Default | Description |
| :--- | :--- | :--- | :--- |
| `userId` | No | `default-user` | Filter expenses by user |
| `sortBy` | No | `date` | Sort order — `date` or `amount` |

#### ExpenseDTO (request body for sync)

```json
{
  "localId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 42.50,
  "description": "Coffee and Lunch",
  "transactionType": "EXPENSE",
  "categoryLocalId": "FOOD",
  "userId": "default-user",
  "expenseDate": "2026-02-26T12:30:00"
}
```

| Field | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `localId` | `String` (UUID) | Yes | Client-generated UUID — used for deduplication |
| `amount` | `Number` | Yes | Transaction amount |
| `description` | `String` | Yes | Human-readable description |
| `transactionType` | `String` | Yes | `EXPENSE` or `INCOME` |
| `categoryLocalId` | `String` | No | Category identifier (e.g. `FOOD`, `SALARY`) |
| `userId` | `String` | No | Defaults to `default-user` if omitted |
| `expenseDate` | `ISO DateTime` | No | Defaults to server time if omitted |

#### ExpenseResponseDTO (response body for GET)

```json
{
  "localId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 42.50,
  "description": "Coffee and Lunch",
  "transactionType": "EXPENSE",
  "categoryLocalId": "FOOD",
  "date": "2026-02-26"
}
```

---

### Categories — `/api/categories`

Categories map to the `Settings` entity and represent expense/income types associated with a user.

| Method | Endpoint | Description | Request | Response |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/categories` | Fetch all categories for a user. Optionally filter by transaction type. | Query params | `List<Settings>` |
| `POST` | `/api/categories/sync` | Batch sync of offline-created categories. Duplicates ignored. | `List<SettingsDTO>` | `"Synced N new categories."` |
| `DELETE` | `/api/categories/{localId}` | Delete a category by its `localId`. | Path variable | `"Deleted"` or 404 |

#### GET `/api/categories` — query parameters

| Parameter | Required | Default | Description |
| :--- | :--- | :--- | :--- |
| `userId` | Yes | — | Filter categories by user |
| `type` | No | All | Filter by type — `EXPENSE` or `INCOME` |

#### SettingsDTO (request body for sync)

```json
{
  "localId": "b1928374-1234-5678-90ab-cdef12345678",
  "name": "FOOD",
  "transactionType": "EXPENSE",
  "userId": "default-user"
}
```

| Field | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `localId` | `String` (UUID) | Yes | Client-generated UUID — used for deduplication |
| `name` | `String` | Yes | Display name of the category |
| `transactionType` | `String` | Yes | `EXPENSE` or `INCOME` |
| `userId` | `String` | Yes | Owner of the category |

---

## Transaction Types

| Value | Description |
| :--- | :--- |
| `EXPENSE` | Money spent |
| `INCOME` | Money received |

## Built-in Category Values

These are the default category identifiers recognised by the Android client.

**Expense categories:** `FOOD`, `TRANSPORTATION`, `ENTERTAINMENT`, `HEALTH`, `CLOTHING`, `EDUCATION`, `SALARY`, `DEFAULT`

**Income categories:** `SALARY`, `BUSINESS`, `GIFT`, `OTHER`