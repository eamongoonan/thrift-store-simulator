# 🛍️ Thrift Store Simulator

A multithreaded command-line **thrift store simulation** written in Java. Models customer arrivals, inventory processing, and checkout in parallel using threads with proper synchronization.

---

## 🧠 Key Concepts

- **Multithreading**: Simulates independent customers arriving and shopping concurrently.
- **Thread safety**: Uses Java synchronization (`synchronized` methods/blocks and locks) to safely manage shared resources like inventory and sales counters.
- **Customer types**: Diverse shopper behaviors are implemented via polymorphism (RegularCustomer, BargainHunter, etc.).
- **Randomized behavior**: Customers interact with inventory randomly to simulate real-life unpredictability.
- **Business process modeling**: Tracks pricing, purchases, and inventory turnover over a simulated day.

---

## 🔧 Threading & Synchronization

- Each `Customer` runs in its **own thread**, calling methods like `Inventory.browse(...)` and `Inventory.purchase(...)`.
- `Inventory` is designed to be thread-safe:
  - Critical sections are protected via `synchronized` blocks.
  - Shared data structures (e.g., item lists, stock counts) are accessed in lock-protected methods to avoid race conditions.
- A top-level `Simulation` class waits for all customer threads to complete before compiling sales results.

This design allows realistic interleaving of customer actions with safe, synchronized updates to shared store resources.

---

## 🗂️ Project Structure

- `Main.java` – Initializes and starts the simulation with multiple customer threads.
- `Inventory.java` – Shared synchronized inventory and helper methods.
- `Customer.java` (abstract) – Defines customer behavior templates.
- `RegularCustomer.java`, `BargainHunter.java`, etc. – Concrete customer types with specific shopping logic.
- `Simulation.java` – Coordinates thread creation, start-up, and result aggregation.
- `Item.java` – Represents thrift store goods.
