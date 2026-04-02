#  ExpenseSync API (Mobile Backend)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Hibernate/JPA](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)

A specialized RESTful API designed to serve as the backend for an **Offline-First Mobile Application**. Built with Spring Boot and Clean Architecture principles.

 **Frontend Companion App:** [Expenses-tracker-app](https://github.com/DerekBurned/expenses-tracker-app/blob/master/README.md)

---

##  The Problem It Solves
Mobile apps frequently lose network connection. Standard CRUD APIs fail when a user tries to save data offline. 

This backend is designed to support an **Offline-First Sync Strategy**:
1. The mobile client (Android/Room) saves data locally using generated UUIDs.
2. When the network is restored, the client sends a **batch payload** of unsynced data.
3. This API processes the batch, checks for duplicate UUIDs to ensure idempotency (preventing double-charges if the sync drops and retries), and safely persists the new records to the database.



---

##  Tech Stack
* **Framework:** Spring Boot 3.x
* **Language:** Java 17+
* **Build Tool:** Maven
* **Database:** H2 (In-Memory for easy testing) / Ready for PostgreSQL
* **Data Access:** Spring Data JPA / Hibernate

---

##  API Endpoints

| Method | Endpoint                 | Description                                                                  | Payload |
| :--- |:-------------------------|:-----------------------------------------------------------------------------| :--- |
| `POST` | `/api/transactions/sync` | Syncs a batch of offline records from the mobile client. Ignores duplicates. | `List<ExpenseDTO>` |
| `GET` | `/api/transactions`      | Fetches all synced transactions from the server.                              | None |

### Example Payload (`POST /api/transaction/sync`)
```json
[
  {
    "localId": "550e8400-e29b-41d4-a716-446655440000",
    "amount": 42.50,
    "description": "Coffee and Lunch",
    "transactionDate": "2026-02-26T12:30:00"
  },
  {
    "localId": "b1928374-1234-5678-90ab-cdef12345678",
    "amount": 120.00,
    "description": "Groceries",
    "transactionDate": "2026-02-25T18:15:00"
  }
]
