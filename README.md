# MiniDB

A minimal in-memory database engine written in Java, with a simple SQL-like command interface.

## 🚀 Build & Run

### 🪟 Windows (PowerShell)

```powershell
# 1. Compile all Java source files
javac -d out (Get-ChildItem -Recurse -Filter *.java).FullName

# 2. Package into an executable JAR binary
jar cfe minidb.jar minidb.Main -C out .

# 3. Run MiniDB executable binary
java -jar minidb.jar


### 🐧 Linux / macOS (Bash)
# 1. Compile all Java source files
javac -d out $(find src -name "*.java")

# 2. Package into an executable JAR binary
jar cfe minidb.jar minidb.Main -C out .

# 3. Run MiniDB executable binary
java -jar minidb.jar



```
CREATE TABLE students (id INT, name STRING)
INSERT INTO students VALUES (1, 'Koushor')
SELECT * FROM students
SELECT * FROM students WHERE id >= 1
UPDATE students SET name = 'Bob' WHERE id != 2
DELETE FROM students WHERE id <= 0
DROP TABLE students
SHOW TABLES
EXIT
```


