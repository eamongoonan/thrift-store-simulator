# 🛍️ Thrift Store Simulator

A command-line simulation of a thrift store's daily operations, written in Java. This project models customer behavior, pricing logic, and inventory turnover to demonstrate basic object-oriented design, randomness, and business process modeling.

Originally created as part of a university assignment, the simulator is structured using **Java classes and interfaces** to represent different types of customers and thrift store items.

---

## 🧠 Key Concepts

- **Simulation modeling** of a retail business
- **Customer types** with varying behavior:
  - Standard shoppers
  - Budget-conscious buyers
  - Impulse buyers
- **Randomization** of item stock and purchasing behavior
- **Polymorphism** and **inheritance** in customer/item classes
- Command-line interaction and console-based output

---

## 🗂️ Project Structure

- `Main.java` – Runs the simulation and outputs the result
- `Item.java` – Represents a thrift store item (e.g., price, category)
- `Customer.java` – Abstract customer class
- `RegularCustomer.java`, `BargainHunter.java`, etc. – Concrete subclasses with specific buying behaviors
- `Inventory.java` – Holds and manages all available items
- `Simulation.java` – Orchestrates the day-to-day activity
