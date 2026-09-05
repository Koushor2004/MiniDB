# 🗄️ MiniDB

> A lightweight, zero-dependency, in-memory relational database engine built in Java , featuring automatic disk persistence, rich data types, and an interactive SQL REPL interface.

---

## 📖 About The Project

**MiniDB** is a lightweight Relational Database Management System (RDBMS) designed and implemented from scratch in pure Java 17 without relying on external libraries.

It serves as a clean, educational, and system-level demonstration of how relational database engines operate under the hood:
- **SQL Query Parsing**: Matching and evaluating SQL statements using Regular Expressions.
- **In-Memory Storage Engine**: Managing schemas, column types, and records using `LinkedHashMap` and type-safe `Row` structures.
- **Type Validation & Comparison**: Parsing data types (`INT`, `LONG`, `DOUBLE`, `BOOLEAN`, `STRING`) with flexible comparison operators (`=`, `!=`, `<`, `>`, `<=`, `>=`).
- **Disk Persistence**: Automatically serializing table schemas and records into `data/*.db` files so state persists across application restarts.

---

## ✨ Features

- 🧠 SQL-like query parsing (Regex parser)
- 🏗️ `CREATE TABLE` with typed columns (`INT`, `LONG`, `DOUBLE`, `BOOLEAN`, `STRING`)
- ➕ `INSERT INTO` with strict type validation
- 🔍 `SELECT` all or with `WHERE` clause (`=`, `!=`, `<>`, `>`, `<`, `>=`, `<=`)
- ✏️ `UPDATE` with conditional logic
- ❌ `DELETE` rows by condition
- 💾 Automatic disk persistence (`data/*.db`) & startup reload
- 📦 Modular code design using Object-Oriented Programming (OOP) principles
- 🧪 Robust CLI interface with informative error feedback

---

## 🚀 Build & Run

### 🪟 Windows (PowerShell)

```powershell
# 1. Compile all Java source files
javac -d out (Get-ChildItem -Recurse -Filter *.java).FullName

# 2. Package into an executable JAR binary
jar cfe minidb.jar minidb.Main -C out .

# 3. Run MiniDB executable binary
java -jar minidb.jar
```

---

### 🐧 Linux / macOS (Bash)

```bash
# 1. Compile all Java source files
javac -d out $(find src -name "*.java")

# 2. Package into an executable JAR binary
jar cfe minidb.jar minidb.Main -C out .

# 3. Run MiniDB executable binary
java -jar minidb.jar
```

---

## 🧪 Example Queries

Once MiniDB launches, you can test a full database workflow:

```sql
minidb> CREATE TABLE students (id INT, name STRING, cgpa DOUBLE, active BOOLEAN)

minidb> INSERT INTO students VALUES (1, 'Koushor', 9.0, true)

minidb> INSERT INTO students VALUES (2, 'Sayan', 8.8, false)

minidb> SELECT * FROM students WHERE cgpa >= 9.0

minidb> UPDATE students SET cgpa = 10.0 WHERE id = 1

minidb> DELETE FROM students WHERE active = false

minidb> SHOW TABLES

minidb> EXIT
```

---

## 💾 Storage & Persistence

All database operations (`CREATE`, `INSERT`, `UPDATE`, `DELETE`, `DROP`) automatically synchronize with the `data/` folder:
- **Storage Format:** Each table is saved as `data/<tablename>.db` using a schema header and CSV row format.
- **Startup Recovery:** When MiniDB starts, it scans `data/` and reloads all previously saved tables and rows into memory.

---

