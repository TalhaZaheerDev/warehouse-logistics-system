# 📦 Smart Logistics & Warehouse Management System

A Core Java backend simulation of a logistics company — customers create shipments, warehouses receive and store them, a fleet of vehicles delivers them, and a reporting/analytics engine summarizes the whole operation.

Built as a deep-dive project to demonstrate **layered architecture**, **OOP fundamentals**, **concurrency**, **file persistence**, and **classic design patterns** — all in pure Core Java, no frameworks.

---

## ✨ Highlights

- Clean **layered architecture** (`model → repository → service → report/simulation → app`)
- Full **OOP toolkit**: encapsulation, inheritance, polymorphism, abstraction, interfaces
- **Custom exception hierarchy** for domain-specific error handling
- **Concurrent simulation** (500 customers, 2000 shipments, thread-safe warehouse writes)
- **Stream API / Optional / Collectors** based reporting and analytics
- **File persistence** via NIO, BufferedReader/Writer, and Java Serialization
- **Design patterns**: Factory, Builder, Strategy, Singleton, Repository
- No external dependencies — 100% Core Java

---

## 🏗️ Architecture

```
src
│
├── app          → entry point (Main), wires everything together
├── model        → domain objects: Customer, Shipment, Warehouse, Vehicle hierarchy
├── enums        → ShipmentStatus, ShipmentPriority, VehicleType
├── service      → business logic: DeliveryEngine, TrackingService, interfaces
├── repository   → generic data storage (Repository<T>) + file persistence
├── exception    → custom domain exceptions
├── util         → BillingUtil, VehicleFactory (stateless helpers)
├── report       → Stream/Collectors-based reporting engine
├── simulation   → multithreaded load simulation (ExecutorService)
└── data         → generated CSV / serialized snapshots (runtime output)
```

**Dependency rule:** dependencies only ever point inward —
`app → simulation/report → service → repository → model`
Domain models never depend on business logic; business logic never depends on the entry point. This keeps the codebase testable and easy to extend (e.g. swapping file storage for a database later).

---

## 🔄 Core Flow

```
Customer ──creates──▶ Shipment (status: CREATED)
                          │
                          ▼
                Warehouse.receiveShipment()      [synchronized, thread-safe]
                          │  status → IN_WAREHOUSE
                          ▼
        DeliveryEngine.assignAndDispatch(shipment, vehicle, distanceKm)
                          │  status → DISPATCHED, vehicle marked unavailable
                          ▼
        DeliveryEngine.markDelivered(shipment, vehicle)
                          │  status → DELIVERED, vehicle freed up
                          ▼
        TrackingService logs each location change
        BillingUtil calculates the final invoice (BigDecimal, tax included)
                          │
                          ▼
        ReportService / Analytics summarize the entire operation
```

---

## 🧩 Design Patterns Used

| Pattern     | Where                                   | Why                                                                |
|-------------|------------------------------------------|---------------------------------------------------------------------|
| **Factory**    | `VehicleFactory`                        | Centralizes vehicle creation logic in one place                    |
| **Builder**    | `ShipmentBuilder`                       | Readable, self-documenting object construction with optional fields |
| **Strategy**   | `Vehicle.costPerKm()` / `calculateDeliveryCapacity()` | Interchangeable per-vehicle-type behavior via polymorphism |
| **Singleton**  | `TrackingService`                        | One shared tracking log across the entire application               |
| **Repository** | `Repository<T>`                         | Generic, reusable data-access layer decoupled from storage details  |

---

## 🧠 Core Java Concepts Covered

OOP (encapsulation, inheritance, polymorphism, abstraction) • Interfaces • Enums with fields/methods • Custom exceptions (unchecked hierarchy) • Collections (`List`, `Map`, `HashMap`) • Generics (`Repository<T>`) • Comparator & sorting • Stream API • Lambda expressions • Method references • `Optional` / `OptionalDouble` • `BigDecimal` for precise currency math • File I/O (NIO, BufferedReader/Writer) • Java Serialization • Multithreading (`ExecutorService`) • Synchronization (`synchronized`) • Design patterns (Factory, Builder, Strategy, Singleton, Repository)

---

## 🚀 Getting Started

### Prerequisites
- JDK 17+
- Any IDE (IntelliJ IDEA / Eclipse / VS Code)

### Run
```bash
git clone https://github.com/TalhaZaheerDev/warehouse-logistics-system.git
cd warehouse-logistics-system
# compile and run via your IDE, or:
javac -d out $(find src -name "*.java")
java -cp out app.Main
```

Running `Main` executes an end-to-end demonstration: customer/shipment creation, warehouse capacity limits, vehicle dispatch, tracking, billing, file persistence, a 2000-shipment concurrent simulation, and final analytics — all printed to console with labeled sections.

---

## 📊 Sample Output

```
========== MODULE 11: SIMULATION (THREADS + CONCURRENCY) ==========
Total received: 2000 / capacity: 2500
Warehouse-0 | load=383/500
Warehouse-1 | load=401/500
Warehouse-2 | load=425/500
Warehouse-3 | load=388/500
Warehouse-4 | load=403/500
Shipments with wrong status (should be 0): 0
```

---

## 🗺️ Roadmap

- [ ] Spring Boot REST API layer over the same business logic
- [ ] Replace file persistence with PostgreSQL
- [ ] JUnit test suite
- [ ] Docker containerization

---

## 👤 Author

**Talha Zaheer Malik**
- GitHub: [@TalhaZaheerDev](https://github.com/TalhaZaheerDev/warehouse-logistics-system)
- LinkedIn: [talha-zaheer-malik](https://www.linkedin.com/in/talha-zaheer-malik-a2619837b)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).